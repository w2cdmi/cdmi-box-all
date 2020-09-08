package pw.cdmi.file.engine.filesystem.uds.loadbalance;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LoadBalanceUDSArithmetic
{
  private static final Logger LOGGER = LoggerFactory.getLogger(LoadBalanceUDSArithmetic.class);

  private static final Map<String, Queue<UDSNode>> ALL_ACTIVE_NODES = new ConcurrentHashMap();

  public static UDSNode getServiceNode(String host)
    throws UnknownHostException
  {
    UDSNode activeNode = null;
    synchronized (ALL_ACTIVE_NODES)
    {
      Queue queue = getAllActiveNodes(host);
      activeNode = (UDSNode)queue.poll();
      queue.add(activeNode);
    }
    if (activeNode != null)
    {
      LOGGER.debug(">>>>>> Get service node, Host: " + host + ", ip : " + activeNode.getIP());
    }
    return activeNode;
  }

  public static void refreshAll()
    throws UnknownHostException
  {
    LOGGER.info(">>> Refresh all the UDS node list ...");

    Queue queue = null;
    for (Map.Entry entry : ALL_ACTIVE_NODES.entrySet())
    {
      queue = tryFindByHost((String)entry.getKey());
      if (queue != null)
      {
        synchronized (ALL_ACTIVE_NODES)
        {
          ALL_ACTIVE_NODES.put((String)entry.getKey(), queue);
        }
      }
    }
  }

  private static Queue<UDSNode> tryFindByHost(String domain)
  {
    try {
      return findByHost(domain);
    }
    catch (Exception e)
    {
      LOGGER.error("Refresh node failed! Host: " + domain, e);
    }return null;
  }

  private static Queue<UDSNode> getAllActiveNodes(String host)
    throws UnknownHostException
  {
    if (ALL_ACTIVE_NODES.containsKey(host))
    {
      return (Queue)ALL_ACTIVE_NODES.get(host);
    }

    Queue nodes = findByHost(host);
    ALL_ACTIVE_NODES.put(host, nodes);
    return nodes;
  }

  private static Queue<UDSNode> findByHost(String host)
    throws UnknownHostException
  {
    InetAddress[] addressList = InetAddress.getAllByName(host);
    Queue nodes = new LinkedBlockingQueue();
    UDSNode udsNode = null;
    for (InetAddress address : addressList)
    {
      udsNode = new UDSNode(address.getHostAddress());
      nodes.add(udsNode);
    }
    return nodes;
  }
}