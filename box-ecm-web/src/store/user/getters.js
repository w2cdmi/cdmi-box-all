export default {
  getUserinfo (state) {
    return state.userInfo;
  },
  getToken (state) {
    return state.userInfo.token;
  },
  getEnterpriseId (state) {
    return state.userInfo.enterpriseId || "";
  },
  getEdit (state) {
    // state.userInfo.isWxEnterprise!==1
    return state.userInfo.isAdmin === 1;
  }
};
