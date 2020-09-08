package pw.cdmi.file.engine.filesystem.model;

public enum FSEndpointStatus
{
  NOT_ENABLED(
    0), 
  ENABLE(
    1), 
  DISABLED(
    2);

  private int code;

  private FSEndpointStatus(int code)
  {
    this.code = code;
  }

  public int getCode()
  {
    return this.code;
  }

  public static FSEndpointStatus parseState(int code)
  {
    for (FSEndpointStatus s : values())
    {
      if (s.getCode() == code)
      {
        return s;
      }
    }
    return null;
  }
}