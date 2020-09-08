package pw.cdmi.file.engine.filesystem.uds.loadbalance;

import java.io.Serializable;

public class UDSNode
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  private String ip;
  private int weight;
  private int accessTime;

  public UDSNode()
  {
  }

  public UDSNode(String ip)
  {
    this.ip = ip;
  }

  public String getIP()
  {
    return this.ip;
  }

  public void setIP(String ip)
  {
    this.ip = ip;
  }

  public int getWeight()
  {
    return this.weight;
  }

  public void setWeight(int weight)
  {
    this.weight = weight;
  }

  public int getAccessTime()
  {
    return this.accessTime;
  }

  public void setAccessTime(int accessTime)
  {
    this.accessTime = accessTime;
  }
}