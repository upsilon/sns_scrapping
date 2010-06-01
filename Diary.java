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

  protected LinkedList<Comment> comments;
  protected int total = -1;

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

    comments = new LinkedList<Comment>();
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

  protected int getLastFetched()
  {
    return comments.size();
  }

  protected int getTotal()
  {
    return total;
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

      ListIterator<Comment> comments_iter = comments.listIterator(); // commentsの先頭を指す

      Iterable<Map<String, String>> c_iterable =
        scrap.getMapIterable("comment_id", "comment_date",
          "comment_member_id", "comment_member_name", "comment_body");

      for (Map<String, String> c_map : c_iterable)
      {
        int id = parseInt(c_map.get("comment_id"));

        Member m = sns.getMember(parseInt(c_map.get("comment_member_id")));
        m.name = c_map.get("comment_member_name");

        Comment c = new Comment(id);
        c.setMember(m);
        c.setCreatedAt(c_map.get("comment_date"));
        c.setBody(c_map.get("comment_body"));

        comments_iter.add(c);
      }

      if (1 == page)
      {
        Comment c = comments.getLast();
        total = c.getNumber();
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
    return comments.get(count);
  }

  enum PublicEnum
  {
    Public,
    Friend,
    Private
  }
}
