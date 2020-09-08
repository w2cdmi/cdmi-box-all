package pw.cdmi.file.engine.filesystem.cache;

public class FileCacheClientProperties
{
  private boolean cacheSupported;
  private int asyncLoadTaskQueueSize;
  private int monitorAsyncLoadTaskQueueThreadNumber;
  private int bufferGate;

  public boolean isCacheSupported()
  {
    return this.cacheSupported;
  }

  public void setCacheSupported(boolean cacheSupported)
  {
    this.cacheSupported = cacheSupported;
  }

  public int getAsyncLoadTaskQueueSize()
  {
    return this.asyncLoadTaskQueueSize;
  }

  public void setAsyncLoadTaskQueueSize(int asyncLoadTaskQueueSize)
  {
    this.asyncLoadTaskQueueSize = asyncLoadTaskQueueSize;
  }

  public int getMonitorAsyncLoadTaskQueueThreadNumber()
  {
    return this.monitorAsyncLoadTaskQueueThreadNumber;
  }

  public void setMonitorAsyncLoadTaskQueueThreadNumber(int monitorAsyncLoadTaskQueueThreadNumber)
  {
    this.monitorAsyncLoadTaskQueueThreadNumber = monitorAsyncLoadTaskQueueThreadNumber;
  }

  public int getBufferGate()
  {
    return this.bufferGate;
  }

  public void setBufferGate(int bufferGate)
  {
    this.bufferGate = bufferGate;
  }
}