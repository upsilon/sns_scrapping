import java.util.*;

public class Topic extends AbstractTopic
{
  protected FetchSNS  sns;

  protected int       topic_id;
  protected Community community = null;
  protected Member    member    = null;
  protected String    title     = null;
  protected String    body      = null;
  protected ArrayList<Comment> comments = new ArrayList<Comment>();

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
    fetchRemainPagesIf(hasNoNext);
    return comments.toArray(new Comment[0]);
  }

  protected boolean fetchPage(int page)
  {
    return true;
  }
}
