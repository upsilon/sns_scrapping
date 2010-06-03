import java.util.*;
import java.io.*;

import static java.lang.Integer.parseInt;

public class EventTable extends AbstractPagable<Event>
{
  private FetchSNS sns;
  private Community community;

  private int page = 1;
  private int total = -1;

  private static ScrappingScript scrapEventList = null;

  static
  {
    try
    {
      scrapEventList = new ScrappingScript("topiclist.txt");
    }
    catch (IOException e)
    {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public EventTable(FetchSNS sns, Community community)
  {
    this.sns = sns;
    this.community = community;
  }

  protected int getTotal()
  {
    return total;
  }

  protected void fetchNextPage(List<Event> list)
  {
    try
    {
      Http http = sns.getHttp();

      HashMap<String, String> get = new HashMap<String, String>();
      get.put("m", "pc");
      get.put("a", "page_c_event_list");
      get.put("target_c_commu_id", Integer.toString(community.getCommunityId()));
      get.put("page", Integer.toString(page));

      String html = http.get(sns.getUrl(), get);
      ScrappingResult scrap = scrapEventList.scrappingAll(html);

      Iterable<Map<String, String>> e_itr =
        scrap.getMapIterable("topic_id", "topic_title");

      for (Map<String, String> e_map : e_itr)
      {
        Event event = new Event(sns, parseInt(e_map.get("topic_id")));
        event.title = e_map.get("topic_title");

        list.add(event);
      }

      page++;
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}
