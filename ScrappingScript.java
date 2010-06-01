import java.io.*;
import java.util.*;
import java.util.regex.*;

public class ScrappingScript
{
  private HashMap<String, ScrappingPattern> pattern;

  public ScrappingScript(String filename)
    throws FileNotFoundException, IOException
  {
    pattern = new HashMap<String, ScrappingPattern>();

    readScript(filename);
  }

  private void readScript(String filename)
    throws FileNotFoundException, IOException
  {
    BufferedReader reader = new BufferedReader(new FileReader(filename));
    String line;

    final Pattern startPattern = Pattern.compile("\\[(.+)\\]");

    if ((line = reader.readLine()) == null)
    {
      reader.close();
      return;
    }

    readLoop:
    for (;;)
    {
      Matcher startMatcher = startPattern.matcher(line);
      if (startMatcher.matches())
      {
        ScrappingPattern s = new ScrappingPattern();
        pattern.put(startMatcher.group(1), s);

        for (;;)
        {
          if ((line = reader.readLine()) == null)
          {
            break readLoop;
          }

          if (line.startsWith("multi"))
          {
            if (getValue(line).equalsIgnoreCase("true"))
            {
              s.multi = true;
            }
          }
          else if (line.startsWith("ign_blankline"))
          {
            if (getValue(line).equalsIgnoreCase("false"))
            {
              s.ign_blankline = false;
            }
          }
          else if (line.endsWith("{"))
          {
            String blockName = line.substring(0, line.length() - 1).trim();
            StringBuilder block = new StringBuilder();

            for (;;)
            {
              if ((line = reader.readLine()) == null)
              {
                break readLoop;
              }
              if (line.equals("}"))
              {
                break;
              }

              block.append(line).append('\n');
            }

            // 最後の行の改行文字を取り除く
            block.deleteCharAt(block.length() - 1);

            if (blockName.equals("match"))
            {
              String match;

              if (s.ign_blankline)
              {
                match = block.toString().replace("\n", "\\n\\s*");
              }
              else
              {
                match = block.toString().replace("\n", "\\n");
              }

              s.pattern = Pattern.compile(match);
            }
            else if (blockName.equals("output"))
            {
              s.output = block.toString();
            }
          }
          else if (line.startsWith("["))
          {
            continue readLoop;
          }
        }
      }

      if ((line = reader.readLine()) == null)
      {
        break readLoop;
      }
    }

    reader.close();
  }

  private String getValue(String line)
  {
    int start = line.indexOf("=");

    if (-1 != start)
    {
      return line.substring(start + 1).trim();
    }
    else
    {
      return null;
    }
  }

  public ScrappingResult scrappingAll(String str)
  {
    HashMap<String, List<String>> result = new HashMap<String, List<String>>();

    for (Map.Entry<String, ScrappingPattern> entry : pattern.entrySet())
    {
      ScrappingPattern pattern = entry.getValue();
      Matcher matcher = pattern.pattern.matcher(str);

      LinkedList<String> list = new LinkedList<String>();
      while (matcher.find())
      {
        Matcher replaceMatch = pattern.pattern.matcher(str);
        replaceMatch.region(matcher.start(), matcher.end());
        replaceMatch.useTransparentBounds(true);

        String output = new String(pattern.output);
        replaceMatch.matches();
        for (int i = 0; i <= replaceMatch.groupCount(); i++)
        {
          output = output.replaceAll("\\$" + i + "(?!\\d)", Matcher.quoteReplacement(replaceMatch.group(i)));
        }

        list.add(output);

        if (! pattern.multi)
        {
          break;
        }
      }
      result.put(entry.getKey(), list);
    }

    return new ScrappingResult(result);
  }

  // このクラスのmainメソッドは実験用なので、かなり雑です。
  // ここ以外も十分雑ですが(^^;
  public static void main(String[] args)
    throws Exception // 例外処理丸投げ
  {
    System.err.println(" * ファイルの読み込み");
    String text = readAll(args[1]);

    System.err.println(" * 正規表現の初期化");
    ScrappingScript script = new ScrappingScript(args[0]);

    System.err.println(" * スクレイピング開始");
    ScrappingResult result = script.scrappingAll(text.toString());

    System.err.println(" * 結果出力");

    for (Map.Entry<String, List<String>> entry : result)
    {
      System.out.print(entry.getKey() + " (" + entry.getValue().size() + "): ");
      System.out.println(entry.getValue());
    }
  }

  private static String readAll(String file)
    throws FileNotFoundException, IOException
  {
    BufferedReader reader = new BufferedReader(new FileReader(file));
    StringBuilder text = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null)
    {
      text.append(line).append('\n');
    }
    reader.close();

    return text.toString();
  }

  class ScrappingPattern
  {
    public Pattern pattern;
    public String output = "$1";
    public boolean multi = false;
    public boolean ign_blankline = true;
  }
}
