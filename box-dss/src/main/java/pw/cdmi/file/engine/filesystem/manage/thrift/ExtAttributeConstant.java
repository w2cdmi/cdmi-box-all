package pw.cdmi.file.engine.filesystem.manage.thrift;

public abstract interface ExtAttributeConstant
{
  public static final String BEST_START_RANGE = "bestStartRange";
  public static final String BEST_END_RANGE = "bestEndRange";
  public static final String MULTI_PART_FIRST = "multipartFirst";
  public static final Long BEST_START_RANGE_DEFAULT = Long.valueOf(0L);

  public static final Long BEST_END_RANGE_DEFAULT = Long.valueOf(-1L);

  public static final Boolean MULTI_PART_FIRST_DEFAULT = Boolean.valueOf(false);
  public static final String JUDGE_TRUE = "true";
  public static final String JUDGE_FALSE = "false";
}