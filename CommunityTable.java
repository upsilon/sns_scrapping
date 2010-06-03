import java.util.*;

import static java.lang.Integer.parseInt;

public class CommunityTable
{
  protected FetchSNS sns;
  private HashMap<Integer, Community> communities;

  public CommunityTable(FetchSNS sns)
  {
    this.sns = sns;
    this.communities = new HashMap<Integer, Community>();
  }

  public Community getCommunity(int community_id)
  {
    if (communities.containsKey(community_id))
    {
      return communities.get(community_id);
    }
    else
    {
      Community c = new Community(sns, community_id);
      communities.put(community_id, c);

      return c;
    }
  }

  protected List<Community> toCommunityList(List<String> id, List<String> name)
  {
    ListIterator<String> id_itr = id.listIterator();
    ListIterator<String> name_itr = name.listIterator();
    LinkedList<Community> result = new LinkedList<Community>();

    while (id_itr.hasNext())
    {
      Community c = getCommunity(parseInt(id_itr.next()));
      c.name = name_itr.next();
      result.add(c);
    }

    return result;
  }
}
