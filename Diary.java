import java.util.*;
import java.io.*;
import java.text.*;

import static java.lang.Integer.parseInt;

public class Diary extends AbstractPagable<Comment>
{
  protected FetchSNS  sns;

  protected int         diary_id;
  protected Member      member      = null;
  protected String      title       = null;
  protected String      body        = null;
  protected PublicEnum  public_enum = null;
  protected String[]    image_url   = new String[3];

  protected Comment[] comments;
  protected int comment_pos;

  private int page = 1;

  private static ScrappingScript scrapDiary = null;

  static
  {
    try
    {
      scrapDiary = new ScrappingScript("diary.txt");
    }
    catch (IOException e)
    {
      e.printStackTrace();
      System.exit(1);
    }
  }

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
    return comments;
  }

  protected int getLastFetched()
  {
    return comments.length - comment_pos;
  }

  protected int getTotal()
  {
    return comments.length;
  }

  protected boolean fetchNextPage()
  {
    try
    {
      Http http = sns.getHttp();

      HashMap<String, String> get = new HashMap<String, String>();
      get.put("m", "pc");
      get.put("a", "page_fh_diary");
      get.put("target_c_diary_id", Integer.toString(getDiaryId()));
      get.put("page", Integer.toString(page));
      get.put("page_size", "100");

      String html = http.get(sns.getUrl(), get);
      ScrappingResult scrap = scrapDiary.scrappingAll(html);

      List<String>
        c_id = scrap.getMulti("comment_id"),
        c_date = scrap.getMulti("comment_date"),
        c_member_id = scrap.getMulti("comment_member_id"),
        c_member_name = scrap.getMulti("comment_member_name"),
        c_body = scrap.getMulti("comment_body");

      comments = new Comment[parseInt(c_id.get(0)) + c_id.size() - 1];

      for (int i = 0; i < c_id.size(); i++)
      {
        int id = parseInt(c_id.get(i));

        Member m = sns.getMember(parseInt(c_member_id.get(i)));
        m.name = c_member_name.get(i);

        Comment c = new Comment(id);
        c.setMember(m);
        c.setCreatedAt(c_date.get(i));
        c.setBody(c_body.get(i));

        comments[id] = c;
      }

      page++;
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }

    return getTotal() == getLastFetched();
  }

  protected Comment doGetEntry(int count)
  {
    return comments[count];
  }

  enum PublicEnum
  {
    Public,
    Friend,
    Private
  }
}
