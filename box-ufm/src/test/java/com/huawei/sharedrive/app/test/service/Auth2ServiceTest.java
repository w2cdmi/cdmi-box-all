package com.huawei.sharedrive.app.test.service;

import com.huawei.sharedrive.app.test.other.AbstractSpringTest;

public class Auth2ServiceTest extends AbstractSpringTest
{
//    
//    @Autowired
//    private OAuth2Service auth2Service;
//    
//    @Autowired
//    private OAuth2ServiceHelper auth2ServiceHelper;
//    
//    @Autowired
//    private AuthClientDAO authClientDAO;
//    
//    @Autowired
//    private AuthCodeDAO authCodeDAO;
//    
//    @Autowired
//    private AuthTokenDAO authTokenDAO;
//    
//    @Autowired
//    private UserDAO userDAO;
//    
//    @Autowired
//    private UserService userService;
//    
//    private Map<String, Object> localMap = new HashMap<String, Object>();
//    
//    @Test
//    public void testCreateClient()
//    {
//        String clientId = auth2Service.generateClientId();
//        String clientPwd = "123456";
//        String redirectUrl = "https://test";
//        AuthClient client1 = auth2Service.createAuthClient(clientId, clientPwd, "https://test");
//        Assert.assertNotNull(client1);
//        Assert.assertEquals(clientId, client1.getId());
//        Assert.assertEquals(clientPwd, client1.getPassword());
//        Assert.assertEquals(redirectUrl, client1.getRedirectUrl());
//        
//        AuthClient client2 = auth2Service.getAuthClient(clientId, clientPwd);
//        Assert.assertNotNull(client2);
//        Assert.assertEquals(clientId, client2.getId());
//        Assert.assertEquals(clientPwd, client2.getPassword());
//        Assert.assertEquals(redirectUrl, client2.getRedirectUrl());
//        
//        localMap.put("AuthClient", client2);
//    }
//    
//    @Test
//    public void testCreateCode()
//    {
//        AuthClient client = (AuthClient) localMap.get("AuthClient");
//        if (client == null)
//        {
//            testCreateClient();
//            client = (AuthClient) localMap.get("AuthClient");
//        }
//        User user = userService.getUserByLoginName("caiming@huawei.com");
//        if (user == null)
//        {
//            user = new User();
//            user.setLoginName("caiming@huawei.com");
//            user.setName("caiming");
//            user.setPassword("123456");
//            userService.create(user);
//            user = userService.getUserByLoginName("caiming@huawei.com");
//        }
//        AuthCode code = auth2Service.createAuthCode(new Authorize(), user.getId(), client);
//        
//        code = auth2Service.getAuthCode(code.getCode(), client.getId());
//        
//        Assert.assertNotNull(code);
//        Assert.assertEquals(code.getUserId().longValue(), user.getId());
//        
//        localMap.put("AuthCode", code);
//        localMap.put("AuthUser", user);
//    }
//    
//    @Test
//    public void testCreateWebLoginToken() throws Exception
//    {
//        User user = userService.getUserByLoginName("caiming@huawei.com");
//        if (user == null)
//        {
//            user = new User();
//            user.setLoginName("caiming@huawei.com");
//            user.setName("caiming");
//            user.setPassword("123456");
//            userService.create(user);
//            user = userService.getUserByLoginName("caiming@huawei.com");
//        }
//        AuthToken token = auth2ServiceHelper.createTokenForWebLogin(user, "");
//        
//        Assert.assertNotNull(token);
//        User user2 = auth2ServiceHelper.checkTokenAndGetUser(token.getToken(), null, null);
//        Assert.assertEquals(user.getLoginName(), user2.getLoginName());
//        
//        localMap.put("AuthToken", token);
//    }
//    
//    @Test
//    public void testCreateDataServerToken() throws Exception
//    {
//        User user = userService.getUserByLoginName("caiming@huawei.com");
//        if (user == null)
//        {
//            user = new User();
//            user.setLoginName("caiming@huawei.com");
//            user.setName("caiming");
//            user.setPassword("123456");
//            userService.create(user);
//            user = userService.getUserByLoginName("caiming@huawei.com");
//        }
//        AuthToken token = auth2ServiceHelper.createTokenDataServer(user.getId(),
//            "89765413132154651",
//            AuthorityMethod.GET_OBJECT,
//            user.getId());
//        Assert.assertNotNull(token);
//        Assert.assertEquals(TokenType.CreatedForDataServer.name(), token.getType());
//        User user2 = auth2ServiceHelper.checkTokenAndGetUser(token.getToken(),
//            "89765413132154651",
//            AuthorityMethod.GET_OBJECT);
//        Assert.assertEquals(user.getLoginName(), user2.getLoginName());
//        
//        Assert.assertNull(auth2Service.getAuthToken(token.getToken()));
//    }
//    
//    @Test(expected = IllegalArgumentException.class)
//    public void testCreateDataServerToken2() throws Exception
//    {
//        User user = userService.getUserByLoginName("caiming@huawei.com");
//        if (user == null)
//        {
//            user = new User();
//            user.setLoginName("caiming@huawei.com");
//            user.setName("caiming");
//            user.setPassword("123456");
//            userService.create(user);
//            user = userService.getUserByLoginName("caiming@huawei.com");
//        }
//        AuthToken token = auth2ServiceHelper.createTokenDataServer(user.getId(),
//            "89765413132154651",
//            AuthorityMethod.GET_OBJECT,
//            user.getId());
//        Assert.assertNotNull(token);
//        Assert.assertEquals(TokenType.CreatedForDataServer.name(), token.getType());
//        auth2ServiceHelper.checkTokenAndGetUser(token.getToken(),
//            "89765413132154651",
//            AuthorityMethod.PUT_OBJECT);
//    }
//    
//    @Test
//    public void testCreateToken()
//    {
//        AuthCode code = (AuthCode) localMap.get("AuthCode");
//        if (code == null)
//        {
//            testCreateCode();
//            code = (AuthCode) localMap.get("AuthCode");
//        }
//        AuthClient client = (AuthClient) localMap.get("AuthClient");
//        Authorize auth = new Authorize(AuthorityMethod.UPLOAD_OBJECT, "89765413132154651");
//        AuthToken token = auth2Service.createAuthToken(code.getCode(),
//            TokenType.RefreshAble,
//            auth,
//            client,
//            "");
//        token = auth2Service.getAuthToken(token.getToken());
//        Assert.assertNotNull(token);
//        Assert.assertEquals(token.getCode(), code.getCode());
//        
//        AuthToken newToken = auth2Service.refreshToken(client, token.getRefreshToken());
//        
//        Assert.assertNotSame(newToken.getToken(), token.getToken());
//        Assert.assertEquals(newToken.getCode(), token.getCode());
//        
//        localMap.put("AuthToken", newToken);
//    }
//    
//    @Test
//    public void testIsAuthorizedRequest()
//    {
//        AuthToken token = (AuthToken) localMap.get("AuthToken");
//        if (token == null)
//        {
//            testCreateToken();
//            token = (AuthToken) localMap.get("AuthToken");
//        }
//        Authorize auth = new Authorize(AuthorityMethod.UPLOAD_OBJECT, "89765413132154651");
//        Assert.assertTrue(auth2Service.isAuthorizedRequest(auth, token.getToken()));
//        
//        auth = new Authorize(AuthorityMethod.GET_OBJECT, "89765413132154651");
//        Assert.assertFalse(auth2Service.isAuthorizedRequest(auth, token.getToken()));
//        
//        auth = new Authorize(AuthorityMethod.PUT_PART, "89765413132154651");
//        Assert.assertTrue(auth2Service.isAuthorizedRequest(auth, token.getToken()));
//        auth = new Authorize(AuthorityMethod.POST_PART, "89765413132154651");
//        Assert.assertTrue(auth2Service.isAuthorizedRequest(auth, token.getToken()));
//        
//        auth = new Authorize(AuthorityMethod.GET_PARTS, "89765413132154651");
//        Assert.assertTrue(auth2Service.isAuthorizedRequest(auth, token.getToken()));
//        
//        auth = new Authorize(AuthorityMethod.GET_PREVIEW, "89765413132154651");
//        Assert.assertFalse(auth2Service.isAuthorizedRequest(auth, token.getToken()));
//        auth = new Authorize(AuthorityMethod.GET_THUMBNAIL, "89765413132154651");
//        Assert.assertFalse(auth2Service.isAuthorizedRequest(auth, token.getToken()));
//        
//        auth = new Authorize(AuthorityMethod.PUT_OBJECT, "89765413132154651");
//        Assert.assertTrue(auth2Service.isAuthorizedRequest(auth, token.getToken()));
//        auth = new Authorize(AuthorityMethod.POST_OBJECT, "89765413132154651");
//        Assert.assertTrue(auth2Service.isAuthorizedRequest(auth, token.getToken()));
//        
//        auth = new Authorize(AuthorityMethod.PUT_COMMIT, "89765413132154651");
//        Assert.assertTrue(auth2Service.isAuthorizedRequest(auth, token.getToken()));
//    }
//    
//    @Test(expected = java.lang.IllegalArgumentException.class)
//    public void testCreateOnetimeOnlyToken()
//    {
//        AuthCode code = (AuthCode) localMap.get("AuthCode");
//        if (code == null)
//        {
//            testCreateCode();
//            code = (AuthCode) localMap.get("AuthCode");
//        }
//        AuthClient client = (AuthClient) localMap.get("AuthClient");
//        
//        Authorize auth = new Authorize(AuthorityMethod.UPLOAD_OBJECT, "89765413132154651");
//        AuthToken token = auth2Service.createAuthToken(code.getCode(),
//            TokenType.CreatedForDataServer,
//            auth,
//            client,
//            "");
//        token = auth2Service.getAuthToken(token.getToken());
//        Assert.assertNotNull(token);
//        Assert.assertEquals(token.getCode(), code.getCode());
//        
//        localMap.put("AuthToken", token);
//        
//        auth2Service.refreshToken(client, token.getRefreshToken());
//    }
//    
//    @Test
//    public void testGetTokenUser()
//    {
//        AuthToken token = (AuthToken) localMap.get("AuthToken");
//        if (token == null)
//        {
//            testCreateToken();
//            token = (AuthToken) localMap.get("AuthToken");
//        }
//        User user = auth2Service.getTokenUser(token.getToken());
//        
//        Assert.assertEquals("caiming@huawei.com", user.getLoginName());
//        
//    }
//    
//    @After
//    public void clearTestData() throws Exception
//    {
//        AuthClient client = (AuthClient) localMap.get("AuthClient");
//        if (client != null)
//        {
//            authClientDAO.delete(client.getId());
//            Assert.assertNull(authClientDAO.get(client.getId()));
//        }
//        
//        AuthCode code = (AuthCode) localMap.get("AuthCode");
//        if (code != null)
//        {
//            authCodeDAO.delete(code.getCode());
//            Assert.assertNull(authCodeDAO.get(code.getCode()));
//        }
//        
//        AuthToken token = (AuthToken) localMap.get("AuthToken");
//        if (token != null)
//        {
//            authTokenDAO.delete(token.getToken());
//            Assert.assertNull(authTokenDAO.get(token.getToken()));
//        }
//        
//    }
}
