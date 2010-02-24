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
    return result.get(key).get(0);
  }

  public ArrayList<String> getMulti(String key)
  {
    return result.get(key);
  }

  public Iterator<Map.Entry<String, ArrayList<String>>> iterator()
  {
    return result.entrySet().iterator();
  }
}
