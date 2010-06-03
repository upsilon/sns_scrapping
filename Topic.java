import java.util.*;

public class Topic extends AbstractPagable<Comment>
{
  protected FetchSNS  sns;

  protected int       topic_id;
  protected Community community = null;
  protected Member    member    = null;
  protected String    title     = null;
  protected String    body      = null;

  private int total = -1;

  public Topic(FetchSNS sns, int topic_id)
  {
    this.sns = sns;
    this.topic_id = topic_id;
  }

  public int getTopicId()
  {
    return topic_id;
  }

  public Community getCommunity()
  {
    fetchFirstPageIf(null == community);
    return community;
  }

  public Member getMember()
  {
    fetchFirstPageIf(null == member);
    return member;
  }

  public String getTitle()
  {
    fetchFirstPageIf(null == title);
    return title;
  }

  public String getBody()
  {
    fetchFirstPageIf(null == body);
    return body;
  }

  public Comment[] getComments()
  {
    fetchRemainPages();
    return list.toArray(new Comment[0]);
  }

  protected int getTotal()
  {
    return total;
  }

  protected void fetchNextPage(List<Comment> list)
  {
  }
}
