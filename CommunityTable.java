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
    ArrayList<Community> result = new ArrayList<Community>();
    for (int i = 0; i < id.size(); i++)
    {
      Community c = getCommunity(parseInt(id.get(i)));
      c.name = name.get(i);
      result.add(c);
    }
    return result;
  }
}
