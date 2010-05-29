import java.util.*;

public class Diary extends AbstractPagable
{
  protected FetchSNS  sns;

  protected int         diary_id;
  protected Member      member      = null;
  protected String      title       = null;
  protected String      body        = null;
  protected PublicEnum  public_enum = null;
  protected String[]    image_url   = new String[3];

  protected ArrayList<Comment> comments = new ArrayList<Comment>();

  public Diary(FetchSNS sns, int diary_id)
  {
    this.sns = sns;
    this.diary_id = diary_id;
  }

  public int getDiaryId()
  {
    return diary_id;
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

  public PublicEnum getPublicEnum()
  {
    fetchFirstPageIf(null == public_enum);
    return public_enum;
  }

  public String[] getImageURLArray()
  {
    fetchFirstPageIf(null == image_url[0]);
    return image_url;
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

  enum PublicEnum
  {
    Public,
    Friend,
    Private
  }
}
