package pw.cdmi.file.engine.filesystem.manage;

import java.util.List;
import java.util.Random;

import pw.cdmi.file.engine.filesystem.model.FSEndpoint;

public class RandomSelectAlgorithm extends SelectAlgorithm
{
  private static final Random RANDOM_SELECTOR = new Random(System.currentTimeMillis());

  public FSEndpoint select(List<FSEndpoint> endpoints, Object condition)
  {
    return (FSEndpoint)endpoints.get(RANDOM_SELECTOR.nextInt(endpoints.size()));
  }
}