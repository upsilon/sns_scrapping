import java.io.*;
import java.util.*;

import static java.lang.Integer.parseInt;

public class Member
{
  protected FetchSNS  sns;

  protected int       member_id;
  protected String    name      = null;
  protected String    top_image_url = null;
  protected String[]  image_url = new String[3];
  protected int       point     = -1;
  protected String    rank      = null;

  protected int friend_num = -1;
  protected ArrayList<Member> friends = new ArrayList<Member>();

  protected int favorite_num = -1;
  protected ArrayList<Member> favorites = new ArrayList<Member>();

  protected int community_num = -1;
  protected ArrayList<Community> communities = new ArrayList<Community>();

  protected DiaryTable diaryTable;

  private static ScrappingScript scrapMypage = null;
  private static ScrappingScript scrapProfile = null;
  private static ScrappingScript scrapProfileImages = null;

  static
  {
    try
    {
      scrapMypage = new ScrappingScript("mypage.txt");
      scrapProfile = new ScrappingScript("profile.txt");
      scrapProfileImages = new ScrappingScript("profile_images.txt");
    }
    catch (IOException e)
    {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public Member(FetchSNS sns, int member_id)
  {
    this.sns = sns;
    this.member_id = member_id;
    this.diaryTable = new DiaryTable(sns, this);
  }

  public int getMemberId()
  {
    return member_id;
  }

  public String getName()
  {
    fetchProfileIf(null == name);
    return name;
  }

  public String getTopImageURL()
  {
    fetchProfileIf(null == top_image_url);
    return top_image_url;
  }

  public String[] getImageURLArray()
  {
    fetchProfileImagesIf(null == image_url[0]);
    return image_url;
  }

  public int getPoint()
  {
    fetchProfileIf(-1 == point);
    return point;
  }

  public String getRank()
  {
    fetchProfileIf(null == rank);
    return rank;
  }

  public Member[] getFriends()
  {
    fetchFriendsIf(friend_num != friends.size());
    return friends.toArray(new Member[0]);
  }

  public Member[] getFavorites()
  {
    fetchFavoritesIf(favorite_num != favorites.size());
    return favorites.toArray(new Member[0]);
  }

  public DiaryTable getDiaryTable()
  {
    return diaryTable;
  }

  /*
   * マイページから取得できる情報
   * ・メンバーID, 名前
   * ・プロフィール画像
   * ・ポイント, ランク
   * ・フレンド, お気に入り
   */
  public static Member scrappingMypage(FetchSNS sns, String html)
  {
    ScrappingResult scrap = scrapMypage.scrappingAll(html);

    Member m = sns.getMember(parseInt(scrap.getOne("member_id")));

    m.name = scrap.getOne("name");
    m.top_image_url = scrap.getOne("image_url");
    m.point = parseInt(scrap.getOne("point"));
    m.rank = scrap.getOne("rank");

    m.friend_num = parseInt(scrap.getOne("friend_num"));
    List<String> friend_id = scrap.getMulti("friend_id");
    if (m.friend_num == friend_id.size())
    {
      List<String> friend_name = scrap.getMulti("friend_name");
      m.friends.addAll(sns.getMemberTable().toMemberList(friend_id, friend_name));
    }

    m.favorite_num = parseInt(scrap.getOne("favorite_num"));
    List<String> favorite_id = scrap.getMulti("favorite_id");
    if (m.favorite_num == favorite_id.size())
    {
      List<String> favorite_name = scrap.getMulti("favorite_name");
      m.favorites.addAll(sns.getMemberTable().toMemberList(favorite_id, favorite_name));
    }

    m.community_num = parseInt(scrap.getOne("community_num"));
    List<String> community_id = scrap.getMulti("community_id");
    if (m.community_num == community_id.size())
    {
      List<String> community_name = scrap.getMulti("community_name");
      m.communities.addAll(sns.getCommunityTable().toCommunityList(community_id, community_name));
    }

    return m;
  }

  protected void fetchProfileIf(boolean bool)
  {
    if (bool)
    {
      fetchProfile();
    }
  }

  protected void fetchProfile()
  {
    try
    {
      Http http = sns.getHttp();

      HashMap<String, String> get = new HashMap<String, String>();
      get.put("m", "pc");
      get.put("a", "page_f_home");
      get.put("target_c_member_id", Integer.toString(getMemberId()));

      String html = http.get(sns.getUrl(), get);
      ScrappingResult scrap = scrapProfile.scrappingAll(html);

      name = scrap.getOne("name");
      top_image_url = scrap.getOne("image_url");

      Map<String, String> profile = scrap.getMap("profile_name", "profile_value");
      point = parseInt(profile.get("ポイント"));

      friend_num = parseInt(scrap.getOne("friend_num"));
      List<String> friend_id = scrap.getMulti("friend_id");
      if (friend_num == friend_id.size())
      {
        List<String> friend_name = scrap.getMulti("friend_name");
        friends.addAll(sns.getMemberTable().toMemberList(friend_id, friend_name));
      }

      community_num = parseInt(scrap.getOne("community_num"));
      List<String> community_id = scrap.getMulti("community_id");
      if (community_num == community_id.size())
      {
        List<String> community_name = scrap.getMulti("community_name");
        communities.addAll(sns.getCommunityTable().toCommunityList(community_id, community_name));
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  protected void fetchProfileImagesIf(boolean bool)
  {
    if (bool)
    {
      fetchProfileImages();
    }
  }

  protected void fetchProfileImages()
  {
    try
    {
      Http http = sns.getHttp();

      HashMap<String, String> get = new HashMap<String, String>();
      get.put("m", "pc");
      get.put("a", "page_f_show_image");
      get.put("target_c_member_id", Integer.toString(getMemberId()));

      String html = http.get(sns.getUrl(), get);
      ScrappingResult scrap = scrapProfileImages.scrappingAll(html);

      List<String> images = scrap.getMulti("images");
      for (int i = 0; i < images.size(); i++)
      {
        String image = images.get(i);
        image_url[i] = image.contains("no_image") ? null : image;
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  protected void fetchFriendsIf(boolean bool)
  {
    if (bool)
    {
      fetchFriends();
    }
  }

  protected void fetchFriends()
  {
  }

  protected void fetchFavoritesIf(boolean bool)
  {
    if (bool)
    {
      fetchFavorites();
    }
  }

  protected void fetchFavorites()
  {
  }
}
