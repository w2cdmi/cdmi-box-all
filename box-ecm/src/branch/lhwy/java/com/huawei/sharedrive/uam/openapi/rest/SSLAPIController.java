package com.huawei.sharedrive.uam.openapi.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/api/v2/ssl")
public class SSLAPIController
{
    // private static Logger logger = LoggerFactory.getLogger(SSLAPIController.class);
    //
    // public static final String SSL_SUCCESS = "SUCCESS";
    //
    // @Autowired
    // private UserService userService;
    //
    // @Autowired
    // private UserTokenHelper userTokenHelper;
    //
    // @Autowired
    // private UserLockService userLockService;
    //
    // @Autowired
    // private UserLogService userLogService;
    //
    // @Autowired
    // private AuthAppService authAppService;
    //
    // @Autowired
    // private SSLAuthService sslAuthService;
    //
    // @Autowired
    // private SecurityMatrixControl securityMatrixControl;
    //
    // @Autowired
    // private TerminalService terminalService;
    //
    // @Autowired
    // private EnterpriseAuthControlManager enterpriseAuthControlManager;
    //
    // /**
    // *
    // * @param requestIp
    // * @param restUserLoginCreateRequest
    // * @return
    // * @throws BaseRunException
    // */
    // @RequestMapping(value = "", method = RequestMethod.POST)
    // @ResponseBody
    // public ResponseEntity<RestLoginResponse> getToken(
    // @RequestBody RestUserSSLAuthRequest restUserSSLAuthRequest, HttpServletRequest
    // request)
    // throws BaseRunException
    // {
    // String appId = restUserSSLAuthRequest.getAppId();
    // String data = restUserSSLAuthRequest.getData();
    // String sign = restUserSSLAuthRequest.getSign();
    // String sslCert = restUserSSLAuthRequest.getSslCert();
    // String verified = request.getHeader("VERIFIED");
    // String proxySSLCert = request.getHeader("SSL_CERT");
    // String loginName = restUserSSLAuthRequest.getLoginName();
    // String deviceTypeStr = request.getHeader("x-device-type");
    // String deviceSN = request.getHeader("x-device-sn");
    // String deviceOS = request.getHeader("x-device-os");
    // String deviceName = request.getHeader("x-device-name");
    // String deviceAgent = request.getHeader("x-client-version");
    // String requestIp = RequestUtils.getRealIP(request);
    // String proxyIp = RequestUtils.getProxyIP(request);
    // // proxy VERIFIED
    // // proxy SSL_CERT
    // if (!StringUtils.isBlank(proxySSLCert) && SSL_SUCCESS.equals(verified))
    // {
    // sslCert = proxySSLCert;
    // }
    //
    // if (StringUtils.isBlank(appId) || StringUtils.isBlank(loginName) ||
    // StringUtils.isBlank(requestIp))
    // {
    // logger.error("invalidate parameter appId:" + appId + " requestIp:" + requestIp +
    // " loginName:"
    // + loginName);
    // throw new InvalidParamterException();
    // }
    //
    // if (StringUtils.isBlank(data) || StringUtils.isBlank(sign) ||
    // StringUtils.isBlank(sslCert))
    // {
    // logger.error("invalidate parameter data:" + data + " sign:" + sign + " sslcert:" +
    // sslCert);
    // throw new InvalidParamterException();
    // }
    //
    // if (StringUtils.isBlank(deviceOS) || StringUtils.isBlank(deviceAgent)
    // || StringUtils.isBlank(deviceTypeStr))
    // {
    // logger.error("invalidate parameter deviceOS:" + deviceOS + " deviceAgent:" +
    // deviceAgent
    // + " deviceTypeStr" + deviceTypeStr);
    // throw new InvalidParamterException();
    // }
    // if (!deviceTypeStr.equals(Terminal.CLIENT_TYPE_WEB_STR)
    // && (StringUtils.isBlank(deviceName) || StringUtils.isBlank(deviceSN)))
    // {
    // logger.error("invalidate parameter deviceSN:" + deviceSN + " deviceName:" +
    // deviceName);
    // throw new InvalidParamterException();
    // }
    // AuthApp authApp = authAppService.getByAuthAppID(appId);
    // if (null == authApp)
    // {
    // logger.error("no such appId:" + appId);
    // throw new InvalidParamterException();
    // }
    // UserLog userLog = UserLogType.getUserLog(request, appId, loginName, false);
    // userLog.setClientAddress(requestIp);
    // LoginInfo loginInfo = enterpriseAuthControlManager.getLoginInfo(loginName,
    // domainName, appId);
    // boolean unlocked = userLockService.checkUserLocked(loginName, appId);
    // if (unlocked)
    // {
    // userLogService.saveUserLog(userLog, UserLogType.KEY_UNLOCK_USER, null);
    // }
    // UserToken user = sslAuthService.authenticate(loginName, data, sign, sslCert,
    // appId);
    // if (null == user)
    // {
    // boolean locked = userLockService.addUserLocked(loginName, appId);
    // if (locked)
    // {
    // userLogService.saveUserLog(userLog, UserLogType.KEY_LOCK_USER, null);
    // }
    // throw new LoginAuthFailedException();
    // }
    // user.setAppId(appId);
    // user.setDeviceSn(deviceSN);
    // user.setDeviceName(deviceName);
    // user.setDeviceOS(deviceOS);
    // user.setDeviceAgent(deviceAgent);
    // user.setDeviceAddress(requestIp);
    // user.setProxyAddress(proxyIp);
    // user.setMaxVersions(-1);
    //
    // user.setAppId(appId);
    // user.setMaxVersions(-1);
    //
    // // userService.saveOrUpdateUser(user, authApp, requestIp);
    //
    // // Terminal terminal = terminalService.fillTerminal(loginName, request,false);
    // // terminal.setUserId(user.getId());
    // // terminal.setAppId(appId);
    // // terminalService.saveOrUpdateTerminal(terminal);
    // // userTokenHelper.createToken(user, terminal);
    // // userService.saveOrUpdateUser(user, authApp, requestIp);
    // RestLoginResponse restLoginResponse = new RestLoginResponse();
    // userService.setAreaBandWidth(restLoginResponse, user.getId(), requestIp);
    // long timeout = (user.getExpiredAt().getTime() - new Date().getTime()) / 1000;
    // restLoginResponse.setTimeout((int) timeout);
    // restLoginResponse.setToken(user.getToken());
    // restLoginResponse.setRefreshToken(user.getRefreshToken());
    // restLoginResponse.setLoginName(user.getLoginName());
    // restLoginResponse.setUserId(user.getId());
    // restLoginResponse.setCloudUserId(user.getCloudUserId());
    // userService.createEvent(user, EventType.USER_LOGIN, user.getId());
    // unlocked = userLockService.deleteUserLocked(loginName, appId);
    // if (unlocked)
    // {
    // userLog = UserLogType.getUserLog(user);
    // userLogService.saveUserLog(userLog, UserLogType.KEY_UNLOCK_USER, null);
    // }
    // return new ResponseEntity<RestLoginResponse>(restLoginResponse, HttpStatus.OK);
    // }
}
