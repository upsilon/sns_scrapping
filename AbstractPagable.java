public abstract class AbstractPagable
{
  protected boolean hasNoNext = false;

  protected void fetchFirstPage()
  {
    hasNoNext = fetchPage(1);
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
    for (int page = 2; ; page++)
    {
      hasNoNext = fetchPage(page);
      if (!hasNoNext)
      {
        break;
      }
    }
  }

  protected void fetchRemainPagesIf(boolean bool)
  {
    if (bool)
    {
      fetchRemainPages();
    }
  }

  protected abstract boolean fetchPage(int page);
}
