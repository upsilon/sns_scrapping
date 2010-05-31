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
      System.out.println();
      System.out.println("1 - メンバー");
      System.out.println("2 - コミュニティ");
      System.out.println("0 - 終了");
      int select = parseInt(console.readLine("番号を入力してください: "));

      switch (select)
      {
        case 0:
          System.exit(0);
        case 1:
          menuMember(console, sns);
          break;
        case 2:
          menuCommunity(console, sns);
          break;
        default:
          break;
      }
    }
  }

  private static void menuMember(Console console, FetchSNS sns)
  {
    int memberId = Integer.parseInt(console.readLine("メンバーID: "));

    Member member = sns.getMember(memberId);

    System.out.println();
    System.out.println("メンバー名: " + member.getName());
    System.out.println("1 - メンバー情報");
    System.out.println("2 - 日記");
    System.out.println("3 - メッセージ");
    System.out.println("4 - レビュー");
    int select = parseInt(console.readLine("番号を入力してください: "));

    switch (select)
    {
      case 1:
        break;
      case 2:
        for (Diary diary : member.getDiaryTable())
        {
          System.out.println(diary.getDiaryId());
        }
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

  private static void menuCommunity(Console console, FetchSNS sns)
  {
    int comId = Integer.parseInt(console.readLine("コミュニティID: "));
    Community com = sns.getCommunity(comId);

    System.out.println();
    System.out.println("コミュニティ名: " + com.getName());
    System.out.println("1 - コミュニティ情報");
    System.out.println("2 - トピック");
    System.out.println("3 - イベント");
    System.out.println("0 - 戻る");
    int select = parseInt(console.readLine("番号を入力してください: "));
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
