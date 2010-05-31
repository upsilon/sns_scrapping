import java.util.*;
import java.text.*;

public class Comment
{
  private int       number    = -1;
  private Member    member    = null;
  private String    body      = null;
  private String[]  image_url = new String[3];
  private GregorianCalendar created_at = null;

  public Comment(int number)
  {
    this.number = number;
  }

  public void setMember(Member member)
  {
    this.member = member;
  }

  public void setBody(String body)
  {
    this.body = body;
  }

  public void setImageURLArray(String[] image_url)
  {
    this.image_url = image_url;
  }

  public void setCreatedAt(String created_at)
  {
    try
    {
      GregorianCalendar gc = new GregorianCalendar();
      DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm");
      gc.setTime(df.parse(created_at));

      setCreatedAt(gc);
    }
    catch (ParseException e)
    {
      e.printStackTrace();
    }
  }

  public void setCreatedAt(GregorianCalendar created_at)
  {
    this.created_at = created_at;
  }

  public int getNumber()
  {
    return number;
  }

  public Member getMember()
  {
    return member;
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
