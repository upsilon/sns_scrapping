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
            int start = line.indexOf("=");
            String bool = line.substring(start + 1).trim();
            if (bool.equalsIgnoreCase("true"))
            {
              s.multi = true;
            }
          }
          else if (line.startsWith("ign_blankline"))
          {
            int start = line.indexOf("=");
            String bool = line.substring(start + 1).trim();
            if (bool.equalsIgnoreCase("false"))
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

  public ScrappingResult scrappingAll(String str)
  {
    HashMap<String, ArrayList<String>> result =
      new HashMap<String, ArrayList<String>>();

    for (Map.Entry<String, ScrappingPattern> entry : pattern.entrySet())
    {
      ScrappingPattern pattern = entry.getValue();
      Matcher matcher = pattern.pattern.matcher(str);

      ArrayList<String> list = new ArrayList<String>();
      while (matcher.find())
      {
        Matcher replaceMatch = pattern.pattern.matcher(matcher.group());
        String output = replaceMatch.replaceAll(pattern.output);

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
    BufferedReader reader = new  BufferedReader(new FileReader(args[1]));
    StringBuilder text = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null)
    {
      text.append(line).append('\n');
    }
    reader.close();

    ScrappingResult result =
      new ScrappingScript(args[0]).scrappingAll(text.toString());

    for (Map.Entry<String, ArrayList<String>> entry : result)
    {
      System.out.print(entry.getKey() + " (" + entry.getValue().size() + "): ");
      System.out.println(entry.getValue());
    }
  }

  class ScrappingPattern
  {
    public Pattern pattern;
    public String output = "$1";
    public boolean multi = false;
    public boolean ign_blankline = true;
  }
}
