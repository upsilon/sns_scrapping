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
    ListIterator<String> id_itr = id.listIterator();
    ListIterator<String> name_itr = name.listIterator();
    LinkedList<Member> result = new LinkedList<Member>();

    while (id_itr.hasNext())
    {
      Member m = getMember(parseInt(id_itr.next()));
      m.name = name_itr.next();
      result.add(m);
    }

    return result;
  }
}
