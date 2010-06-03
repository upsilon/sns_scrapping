import java.util.*;

public class Community
{
  protected FetchSNS sns;

  protected int    community_id;
  protected String name       = null;
  protected String body       = null;
  protected Member owner      = null;
  protected Member sub_owner  = null;
  protected String image_url  = null;
  protected String category   = null;
  protected ArrayList<Member> member;
  protected PublicEnum public_enum = null;

  protected TopicTable topicTable;

  public Community(FetchSNS sns, int community_id)
  {
    this.sns = sns;
    this.community_id = community_id;

    topicTable = new TopicTable(sns, this);
  }

  public int getCommunityId()
  {
    return community_id;
  }

  public String getName()
  {
    fetchCommunityIf(null == name);
    return name;
  }

  public Member getOwner()
  {
    fetchCommunityIf(null == owner);
    return owner;
  }

  public Member getSubOwner()
  {
    fetchCommunityIf(null == sub_owner && null == owner);
    return sub_owner;
  }

  public PublicEnum getPublicEnum()
  {
    fetchCommunityIf(null == public_enum);
    return public_enum;
  }

  public TopicTable getTopicTable()
  {
    return topicTable;
  }

  protected void fetchCommunityIf(boolean bool)
  {
    if (bool)
    {
      fetchCommunity();
    }
  }

  protected void fetchCommunity()
  {
  }

  enum PublicEnum
  {
    Public,
    AuthPublic,
    AuthCommuMember,
  }
}
