import java.util.*;

public class Event extends AbstractPagable<Comment>
{
  protected FetchSNS  sns;

  protected int       event_id;
  protected Community community = null;
  protected Member    member    = null;
  protected String    title     = null;
  protected String    body      = null;

  private int total = -1;

  public Event(FetchSNS sns, int event_id)
  {
    this.sns = sns;
    this.event_id = event_id;
  }

  public int getEventId()
  {
    return event_id;
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
