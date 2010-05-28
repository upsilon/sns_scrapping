import java.util.*;

public class Comment
{
  private int       number    = -1;
  private int       member_id = -1;
  private String    body      = null;
  private String[]  image_url = new String[3];
  private GregorianCalendar created_at = null;

  public Comment(int number)
  {
    this.number = number;
  }

  public void setMemberId(int member_id)
  {
    this.member_id = member_id;
  }

  public void setBody(String body)
  {
    this.body = body;
  }

  public void setImageURLArray(String[] image_url)
  {
    this.image_url = image_url;
  }

  public void setCreatedAt(GregorianCalendar created_at)
  {
    this.created_at = created_at;
  }

  public int getNumber()
  {
    return number;
  }

  public int getMemberId()
  {
    return member_id;
  }

  public String getBody()
  {
    return body;
  }

  public String[] getImageURLArray()
  {
    return image_url;
  }

  public GregorianCalendar getCreatedAt()
  {
    return created_at;
  }
}
