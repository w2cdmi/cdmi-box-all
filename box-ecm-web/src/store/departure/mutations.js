import * as types from "./mutations_types";

export default {
  // 文档移交
  [types.DOCUMENT_HAND_OVER_LIST] (state, data) {
    state.documentHandOverList = data
  },
  [types.AUTHORITY_HAND_OVER] (state, data) {
    state.authorityHandOver = data
  },
  // 文档移交列表
  [types.DOCUMENT_HAND_OVER_INNER_LIST] (state, data) {
    state.documentHandOverInnerList = data
    state.documentHandOverInnerListTotalCount = data.data.totalCount
  },
  // 离职管理
  [types.DEPARTURE_MANAGEMENT] (state, data) {
    state.departuremanagementlist = data.data.content
  },
  // 权限移交列表
  [types.TEAMSPACES_SPACES] (state, data) {
    state.teamspacesSpacesList = data.data.memberships
    state.teamspacesSpacesListTotalCount = data.data.totalCount
  },
  // 预览
  [types.FILE_PREVIEW] (state, data) {
    state.filePreview = data.data.url
  }
};
