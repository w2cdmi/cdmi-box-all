/**
 * 
 */
package pw.cdmi.box.uam.user.service.impl;

import pw.cdmi.box.uam.user.service.UserLockService;

//@Service
public abstract class MemcachedUserLockServiceImpl implements UserLockService
{
    // private static final class UserLock implements Serializable
    // {
    // private static final long serialVersionUID = -8057001699552124344L;
    //
    // /**
    // * 当前登录失败计数
    // */
    // private int loginFailedCount;
    //
    // /**
    // * 超时时间，当失败计数小于阈值时，表示计数超时时间；当失败计数大于等于阈值时，表示锁超时时间
    // */
    // private long expireTime;
    //
    // public int getLoginFailedCount()
    // {
    // return loginFailedCount;
    // }
    //
    // public void setLoginFailedCount(int loginFailedCount)
    // {
    // this.loginFailedCount = loginFailedCount;
    // }
    //
    // public long getExpireTime()
    // {
    // return expireTime;
    // }
    //
    // public void setExpireTime(long expireTime)
    // {
    // this.expireTime = expireTime;
    // }
    //
    // }
    //
    // private static Logger logger =
    // LoggerFactory.getLogger(MemcachedUserLockServiceImpl.class);
    //
    // private static final String LOCK_KEY_PREFIX = "login_lock_";
    //
    // @Resource(name = "cacheClient")
    // private CacheClient cacheClient;
    //
    // @Value("${lock.date.limit}")
    // private int lockTimeout;
    //
    // @Value("${lock.time.limit}")
    // private int loginFailedLimit;
    //
    // @Value("${lock.time.period}")
    // private int loginFailedPeriod;
    //
    // @Autowired
    // private UserLogService userLogService;
    //
    // /**
    // * 检查用户是否已锁定
    // *
    // * @param loginName
    // * @param appId
    // * @return 是否在本次检查时解锁
    // * @throws UserLockedException 用户已锁定
    // */
    // @Override
    // public boolean checkUserLocked(String loginName, String appId) throws
    // UserLockedException
    // {
    // String cacheKey = buildLockKey(loginName, appId);
    // UserLock userLock = (UserLock) cacheClient.getCache(cacheKey);
    // if (userLock == null)
    // {
    // return false;
    // }
    // if (userLock.getLoginFailedCount() < loginFailedLimit)
    // {
    // return false;
    // }
    // if (userLock.getExpireTime() < System.currentTimeMillis())
    // {
    // cacheClient.deleteCache(cacheKey);
    // logger.info("user unlocked for " + loginName + '-' + appId +
    // ", lock expire time is "
    // + new Date(userLock.getExpireTime()));
    // return true;
    // }
    // throw new UserLockedException("user " + loginName + '-' + appId + " locked until "
    // + new Date(userLock.getExpireTime()));
    // }
    //
    // /**
    // * 增加一次登录失败
    // *
    // * @param loginName
    // * @param appId
    // * @return 是否在本次增加后锁定
    // */
    // @Override
    // public boolean addUserLocked(String loginName, String appId)
    // {
    // String cacheKey = buildLockKey(loginName, appId);
    // UserLock userLock = (UserLock) cacheClient.getCache(cacheKey);
    // if (userLock == null)
    // {
    // return newLock(cacheKey, loginName, appId);
    // }
    // return incrementLock(cacheKey, loginName, appId);
    // }
    //
    // /**
    // * 清除用户锁定信息
    // *
    // * @param loginName
    // * @param appId
    // * @return 本次清除前，用户是否已锁定
    // */
    // @Override
    // public boolean deleteUserLocked(String loginName, String appId)
    // {
    // String cacheKey = buildLockKey(loginName, appId);
    // UserLock userLock = (UserLock) cacheClient.getCache(cacheKey);
    // cacheClient.deleteCache(cacheKey);
    // logger.info("user lock was clean for " + loginName + '-' + appId);
    // if (userLock != null && userLock.getLoginFailedCount() >= loginFailedLimit)
    // {
    // return true;
    // }
    // return false;
    // }
    //
    // /**
    // * 创建用户第一次失败计数
    // *
    // * @param cacheKey
    // * @param loginName
    // * @param appId
    // * @return
    // */
    // private boolean newLock(String cacheKey, String loginName, String appId)
    // {
    // UserLock userLock = new UserLock();
    // userLock.setLoginFailedCount(1);
    // userLock.setExpireTime(System.currentTimeMillis() + loginFailedPeriod);
    // boolean succ = cacheClient.addCacheNoExpire(cacheKey, userLock);
    // if (succ)
    // {
    // return false;
    // }
    // // add操作失败，说明有其他请求已设置，进入增加失败记录逻辑
    // return incrementLock(cacheKey, loginName, appId);
    // }
    //
    // /**
    // * 增加一次失败计数
    // *
    // * @param cacheKey
    // * @param userLog
    // * @param loginName
    // * @param appId
    // * @return 是否在本次增加后达到锁定阈值
    // */
    // private boolean incrementLock(String cacheKey, String loginName, String appId)
    // {
    // GetsResponse<UserLock> response = cacheClient.getsCache(cacheKey);
    // if (response == null)
    // {
    // return newLock(cacheKey, loginName, appId);
    // }
    // UserLock userLock = response.getValue();
    // if (userLock == null)
    // {
    // return newLock(cacheKey, loginName, appId);
    // }
    // int count = userLock.getLoginFailedCount();
    // if (count >= loginFailedLimit)
    // {
    // // 用户已锁定
    // return false;
    // }
    // long lockExpireTime = 0;
    // if (userLock.getExpireTime() < System.currentTimeMillis())
    // {
    // // 超过计数周期，重新设置计数和计数超时
    // count = 1;
    // lockExpireTime = System.currentTimeMillis() + loginFailedPeriod;
    // userLock.setLoginFailedCount(count);
    // userLock.setExpireTime(lockExpireTime);
    // }
    // else
    // {
    // count++;
    // lockExpireTime = System.currentTimeMillis() + lockTimeout;
    // userLock.setLoginFailedCount(count);
    // if (count >= loginFailedLimit)
    // {
    // // 达到锁定阈值，设置锁超时
    // userLock.setExpireTime(lockExpireTime);
    // }
    // }
    // boolean succ = cacheClient.casCacheNoExpire(cacheKey, userLock, response.getCas());
    // if (succ)
    // {
    // if (count >= loginFailedLimit)
    // {
    // logger.info("user locked for " + loginName + '-' + appId + ", until "
    // + new Date(lockExpireTime));
    // return true;
    // }
    // if (count == 1)
    // {
    // logger.info("user lock for " + loginName + '-' + appId + " was recount, until "
    // + new Date(lockExpireTime));
    // }
    // return false;
    // }
    // // cas操作失败，说明有其他请求已设置，重新进入增加失败记录逻辑
    // return incrementLock(cacheKey, loginName, appId);
    // }
    //
    // /**
    // * 使用md5方式生成key，防止key长度超过memcached限制
    // *
    // * @param loginName
    // * @param appId
    // * @return
    // */
    // private String buildLockKey(String loginName, String appId)
    // {
    // if (StringUtils.isBlank(loginName))
    // {
    // throw new InvalidParamterException();
    // }
    // if (StringUtils.isBlank(appId))
    // {
    // return LOCK_KEY_PREFIX + DigestUtils.md5Hex(loginName.toLowerCase());
    // }
    // return LOCK_KEY_PREFIX + DigestUtils.md5Hex(loginName.toLowerCase() + '-' + appId);
    // }
    
}
