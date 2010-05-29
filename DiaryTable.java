import java.util.*;
import java.io.*;

import static java.lang.Integer.parseInt;

public class DiaryTable extends AbstractPagable<Diary>
{
  private FetchSNS sns;
  private Member member;
  private ArrayList<Diary> diaries;

  private int page = 1;
  private int total = -1;

  private static ScrappingScript scrapDiaryList = null;

  static
  {
    try
    {
      scrapDiaryList = new ScrappingScript("diarylist.txt");
    }
    catch (IOException e)
    {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public DiaryTable(FetchSNS sns, Member member)
  {
    this.sns = sns;
    this.member = member;
    this.diaries = new ArrayList<Diary>();
  }

  protected int getLastFetched()
  {
    return diaries.size();
  }

  protected int getTotal()
  {
    if (-1 == total)
    {
      fetchNextPage();
    }

    return total;
  }

  protected boolean fetchNextPage()
  {
    try
    {
      Http http = sns.getHttp();

      HashMap<String, String> get = new HashMap<String, String>();
      get.put("m", "pc");
      get.put("a", "page_fh_diary_list");
      get.put("target_c_member_id", Integer.toString(member.getMemberId()));
      get.put("page", Integer.toString(page));

      String html = http.get(sns.getUrl(), get);
      ScrappingResult scrap = scrapDiaryList.scrappingAll(html);

      total = parseInt(scrap.getOne("total"));

      for (String diary_id : scrap.getMulti("diary_id"))
      {
        diaries.add(new Diary(sns, parseInt(diary_id)));
      }

      page++;
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }

    return getTotal() == getLastFetched();
  }

  protected Diary doGetEntry(int count)
  {
    return diaries.get(count);
  }
}
