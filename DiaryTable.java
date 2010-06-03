import java.util.*;
import java.io.*;

import static java.lang.Integer.parseInt;

public class DiaryTable extends AbstractPagable<Diary>
{
  private FetchSNS sns;
  private Member member;

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
  }

  protected int getTotal()
  {
    return total;
  }

  protected void fetchNextPage(List<Diary> list)
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

      Iterable<Map<String, String>> d_itr =
        scrap.getMapIterable("diary_id", "diary_title");

      for (Map<String, String> d_map : d_itr)
      {
        Diary diary = new Diary(sns, parseInt(d_map.get("diary_id")));
        diary.title = d_map.get("diary_title");

        list.add(diary);
      }

      page++;
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}
