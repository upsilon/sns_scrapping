import java.util.*;
import java.io.*;

import static java.lang.Integer.parseInt;

public class SnsScrapping
{
  protected HashMap<String, ScrappingScript> scrapping;

  public static void main(String[] args)
  {
    Console console;
    console = System.console();

    String url, mail, pass;
    url = console.readLine("SNSのURLを入力してください: ");
    mail = console.readLine("メールアドレス: ");
    pass = new String(console.readPassword("パスワード: "));

    FetchSNS sns = new FetchSNS(url);
    sns.login(mail, pass);

    for (;;)
    {
      int select;

      System.out.println("1 - メンバー");
      System.out.println("2 - コミュニティ");
      System.out.println("0 - 終了");
      select = parseInt(console.readLine("番号を入力してください: "));

      if (select == 0)
      {
        System.exit(0);
      }
      else if (select == 1)
      {
        Integer memberId;
        memberId = Integer.parseInt(console.readLine("メンバーID: "));

        Member member = sns.getMember(memberId);
        System.out.println("メンバー名: " + member.getName());

        System.out.println("1 - メンバー情報");
        System.out.println("2 - 日記");
        System.out.println("3 - メッセージ");
        System.out.println("4 - レビュー");
        select = parseInt(console.readLine("番号を入力してください: "));

        switch (select)
        {
          case 1:
            break;
          case 2:
            break;
          case 3:
            break;
          case 4:
            break;
          case 0:
          default:
            break;
        }
      }
      else if (select == 2)
      {
        Integer comId;
        comId = Integer.parseInt(console.readLine("コミュニティID: "));

        Community com = sns.getCommunity(comId);

        System.out.println("コミュニティ名: " + com.getName());

        System.out.println("1 - コミュニティ情報");
        System.out.println("2 - トピック");
        System.out.println("3 - イベント");
        System.out.println("0 - 戻る");
        select = parseInt(console.readLine("番号を入力してください: "));

        switch (select)
        {
          case 1:
            break;
          case 2:
            break;
          case 3:
            break;
          case 0:
          default:
            break;
        }
      }
    }
  }
}
