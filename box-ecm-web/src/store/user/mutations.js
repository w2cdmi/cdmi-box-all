import * as types from "./mutations_types";

export default {
  [types.SET_USER_INFO] (state, data) {
    state.userInfo = data;
    state.userInfo.edit = true;
  },
  [types.REMOVE_USER_INFO] (state) {
    state.userInfo = {};
  }
};
