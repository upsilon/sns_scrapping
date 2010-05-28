import java.util.*;

import static java.lang.Integer.parseInt;

public class MemberTable
{
  private FetchSNS sns;
  private HashMap<Integer, Member> members;

  public MemberTable(FetchSNS sns)
  {
    this.sns = sns;
    this.members = new HashMap<Integer, Member>();
  }

  public Member getMember(int member_id)
  {
    if (members.containsKey(member_id))
    {
      return members.get(member_id);
    }
    else
    {
      Member m = new Member(sns, member_id);
      members.put(member_id, m);

      return m;
    }
  }

  protected List<Member> toMemberList(List<String> id, List<String> name)
  {
    ArrayList<Member> result = new ArrayList<Member>();
    for (int i = 0; i < id.size(); i++)
    {
      Member m = getMember(parseInt(id.get(i)));
      m.name = name.get(i);
      result.add(m);
    }
    return result;
  }
}
