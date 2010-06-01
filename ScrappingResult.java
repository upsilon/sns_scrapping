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

  public Iterable<Map<String, String>> getMapIterable(String... keys)
  {
    return new MapIterable(this, keys);
  }

  public Iterator<Map.Entry<String, ArrayList<String>>> iterator()
  {
    return result.entrySet().iterator();
  }
}

class MapIterable implements Iterable<Map<String, String>>
{
  private String[] keys;
  private List<String>[] lists;

  @SuppressWarnings({"unchecked"})
  public MapIterable(ScrappingResult result, String... keys)
  {
    this.keys = keys;

    lists = (List<String>[]) new List[keys.length];
    for (int i = 0; i < keys.length; i++)
    {
      lists[i] = result.getMulti(keys[i]);
    }
  }

  public Iterator<Map<String, String>> iterator()
  {
    return new Iter();
  }

  class Iter implements Iterator<Map<String, String>>
  {
    public Iterator<String>[] iters;

    @SuppressWarnings({"unchecked"})
    public Iter()
    {
      iters = (Iterator<String>[]) new Iterator[lists.length];

      for (int i = 0; i < lists.length; i++)
      {
        iters[i] = lists[i].iterator();
      }
    }

    public boolean hasNext()
    {
      for (Iterator<String> iter : iters)
      {
        if (iter.hasNext())
        {
          return true;
        }
      }

      return false;
    }

    public Map<String, String> next()
    {
      HashMap<String, String> result = new HashMap<String, String>();

      for (int i = 0; i < iters.length; i++)
      {
        Iterator<String> iter = iters[i];
        if (iter.hasNext())
        {
          result.put(keys[i], iter.next());
        }
      }

      return result;
    }

    public void remove()
    {
    }
  }
}
