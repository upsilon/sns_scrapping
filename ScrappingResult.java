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

  public ArrayList<String> getMulti(String key)
  {
    ArrayList<String> list = result.get(key);
    return list.isEmpty() ? null : list;
  }

  public Iterator<Map.Entry<String, ArrayList<String>>> iterator()
  {
    return result.entrySet().iterator();
  }
}
