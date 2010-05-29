import java.util.*;

public abstract class AbstractPagable<T> implements Iterable<T>
{
  protected boolean hasNoNext = false;
  protected Itr<T> itr;

  public AbstractPagable()
  {
    itr = new Itr<T>(this);
  }

  protected void fetchFirstPage()
  {
    if (1 > getLastFetched())
    {
      return;
    }

    hasNoNext = fetchNextPage();
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
    if (getTotal() == getLastFetched())
    {
      return;
    }

    while (!hasNoNext)
    {
      hasNoNext = fetchNextPage();
    }
  }

  protected void fetchRemainPagesIf(boolean bool)
  {
    if (bool)
    {
      fetchRemainPages();
    }
  }

  public T get(int count)
  {
    if (getTotal() < count)
    {
      return null;
    }

    while (getLastFetched() < count)
    {
      fetchNextPage();
    }

    return doGetEntry(count - 1);
  }

  public Iterator<T> iterator()
  {
    return itr;
  }

  protected abstract int getLastFetched();
  protected abstract int getTotal();

  protected abstract boolean fetchNextPage();
  protected abstract T doGetEntry(int count);

  class Itr<T> implements Iterator<T>
  {
    private AbstractPagable<T> pager;
    private int count;

    public Itr(AbstractPagable<T> pager)
    {
      this.pager = pager;
      this.count = 1;
    }

    public boolean hasNext()
    {
      return pager.getTotal() >= count;
    }

    public T next()
    {
      return pager.get(count++);
    }

    public void remove()
    {
    }
  }
}
