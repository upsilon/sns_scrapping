import java.util.*;

public abstract class AbstractPagable<T> implements Iterable<T>
{
  protected List<T> list = new ArrayList<T>();

  protected void fetchFirstPage()
  {
    if (1 <= getLastFetched())
    {
      return;
    }

    fetchNextPage(list);
  }

  protected void fetchFirstPageIf(boolean bool)
  {
    if (bool)
    {
      fetchFirstPage();
    }
  }

  protected void fetchRemainPages()
  {
    while (getLastFetched() != getTotal())
    {
      fetchNextPage(list);
    }
  }

  protected void fetchRemainPagesIf(boolean bool)
  {
    if (bool)
    {
      fetchRemainPages();
    }
  }

  public Iterator<T> iterator()
  {
    return new Itr<T>(this);
  }

  protected int getLastFetched()
  {
    return list.size();
  }

  protected abstract int getTotal();
  protected abstract void fetchNextPage(List<T> list);

  class Itr<T> implements Iterator<T>
  {
    private AbstractPagable<T> pager;
    private int count;

    public Itr(AbstractPagable<T> pager)
    {
      this.pager = pager;
      this.count = 0;
    }

    public boolean hasNext()
    {
      return pager.getTotal() > count;
    }

    public T next()
    {
      if (!hasNext())
      {
        return null;
      }

      if (pager.getLastFetched() < count)
      {
        pager.fetchNextPage(pager.list);
      }

      return pager.list.get(count++);
    }

    public void remove()
    {
    }
  }
}
