import java.util.*;

public class ScrappingResult
  implements Iterable<Map.Entry<String, ArrayList<String>>>
{
  private HashMap<String, ArrayList<String>> result;

  public ScrappingResult(HashMap<String, ArrayList<String>> result)
  {
    this.result = result;
  }

  public String getOne(String key)
  {
    ArrayList<String> list = result.get(key);
    return list.isEmpty() ? null : list.get(0);
  }

  public List<String> getMulti(String key)
  {
    ArrayList<String> list = result.get(key);
    return list.isEmpty() ? null : list;
  }

  public Map<String, String> getMap(String key1, String key2)
  {
    HashMap<String, String> result = new HashMap<String, String>();

    List<String> list1 = getMulti(key1);
    List<String> list2 = getMulti(key2);
    for (int i = 0; i < list1.size(); i++)
    {
      result.put(list1.get(i), list2.get(i));
    }

    return result;
  }

  public Iterator<Map.Entry<String, ArrayList<String>>> iterator()
  {
    return result.entrySet().iterator();
  }
}
