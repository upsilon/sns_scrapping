import java.util.*;
import java.net.*;
import java.io.*;

public class FetchSNS
{
  private String url;
  private Http http = new Http("UTF-8");

  private MemberTable member_table;
  private CommunityTable community_table;

  public FetchSNS(String url)
  {
    this.url = url;

    member_table = new MemberTable(this);
    community_table = new CommunityTable(this);
  }

  public boolean login(String mail, String pass)
  {
    try
    {
      HashMap<String, String> post = new HashMap<String, String>();

      post.put("m", "pc");
      post.put("a", "do_o_login");
      post.put("username", mail);
      post.put("password", pass);

      String html = http.post(url, post);
      if (http.cookieLength(new URL(url)) >= 2)
      {
        Member.scrappingMypage(this, html);
        return true;
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }

    return false;
  }

  public String getUrl()
  {
    return url;
  }

  public Http getHttp()
  {
    return http;
  }

  public MemberTable getMemberTable()
  {
    return member_table;
  }

  public Member getMember(int member_id)
  {
    return member_table.getMember(member_id);
  }

  public CommunityTable getCommunityTable()
  {
    return community_table;
  }

  public Community getCommunity(int community_id)
  {
    return community_table.getCommunity(community_id);
  }
}
