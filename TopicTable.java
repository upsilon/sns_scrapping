import java.util.*;
import java.io.*;

import static java.lang.Integer.parseInt;

public class TopicTable extends AbstractPagable<Topic>
{
  private FetchSNS sns;
  private Community community;
  private LinkedList<Topic> topics;

  private int page = 1;
  private int total = -1;

  private static ScrappingScript scrapTopicList = null;

  static
  {
    try
    {
      scrapTopicList = new ScrappingScript("topiclist.txt");
    }
    catch (IOException e)
    {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public TopicTable(FetchSNS sns, Community community)
  {
    this.sns = sns;
    this.community = community;
    this.topics = new LinkedList<Topic>();
  }

  protected int getLastFetched()
  {
    return topics.size();
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
      get.put("a", "page_c_topic_list");
      get.put("target_c_commu_id", Integer.toString(community.getCommunityId()));
      get.put("page", Integer.toString(page));

      String html = http.get(sns.getUrl(), get);
      ScrappingResult scrap = scrapTopicList.scrappingAll(html);

      Iterable<Map<String, String>> t_itr =
        scrap.getMapIterable("topic_id", "topic_title");

      for (Map<String, String> t_map : t_itr)
      {
        Topic topic = new Topic(sns, parseInt(t_map.get("topic_id")));
        topic.title = t_map.get("topic_title");

        topics.add(topic);
      }

      page++;
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }

    return getTotal() == getLastFetched();
  }

  protected Topic doGetEntry(int count)
  {
    return topics.get(count);
  }
}
