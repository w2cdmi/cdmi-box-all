import * as types from "./mutations_types";
import Api from "../../api/departureApi.js";

export default {
  // 文档移交列表
  documentHandOverInnerList: ({
    commit
  }, {
    self,
    query
  }) => {
    return new Promise((resolve, reject) => {
      Api
        .departure
        .documentHandOverInnerList
        .call(self, {
          query: query,
          success (data) {
            commit(types.DOCUMENT_HAND_OVER_INNER_LIST, {
              self: this,
              data
            });
            resolve(data);
          },
          error (err) {
            reject(err);
          }
        })
    });
  },
  // 文档移交
  documentHandOverList: ({
    commit
  }, {
    self,
    query
  }) => {
    return new Promise((resolve, reject) => {
      Api
        .departure
        .documentHandOverList
        .call(self, query, data => {
          commit(types.AUTHORITY_HAND_OVER, {
            self: this,
            data
          });
          resolve();
        }, () => {
          reject(err);
        });
    });
  },

  /**
   * 离职管理
   */
  departureManagement: ({
    commit
  }, {
    self,
    query
  }) => {
    return new Promise((resolve, reject) => {
      Api
        .departure
        .departureManagement
        .call(self, {
          success (data) {
            commit(types.DEPARTURE_MANAGEMENT, {
              self: this,
              data
            });
            resolve(data)
          },
          error (err) {
            reject(err);
          }
        })
    });
  },
  /**
   * 权限移交列表
   */
  teamspacesSpaces: ({
    commit
  }, {
    self,
    query
  }) => {
    return new Promise((resolve, reject) => {
      Api
        .departure
        .teamspacesSpaces
        .call(self, {
          query: query,
          success (data) {
            commit(types.TEAMSPACES_SPACES, {
              self: this,
              data
            });
            resolve(data);
          },
          error (err) {
            reject(err);
          }
        })
    });
  },
  /**
   * 权限移交
   */
  teamspacesPut: ({
    commit
  }, {
    self,
    query
  }) => {
    return new Promise((resolve, reject) => {
      Api
        .departure
        .teamspacesPut
        .call(self, {
          query: query,
          success (data) {
            self = this,
            data;
            resolve();
          },
          error (err) {
            reject(err);
          }
        })
    });
  },
  /**
   * 文档移交
   */
  nodesPut: ({
    commit
  }, {
    self,
    query
  }) => {
    return new Promise((resolve, reject) => {
      Api
        .departure
        .nodesPut
        .call(self, {
          query: query,
          success (data) {
            self = this,
            data;
            resolve();
          },
          error (err) {
            reject(err);
          }
        })
    });
  },
  /**
   * 文档删除
   */
  nodesDelete: ({
    commit
  }, {
    self,
    query
  }) => {
    return new Promise((resolve, reject) => {
      Api
        .departure
        .nodesDelete
        .call(self, {
          query: query,
          success (data) {
            self = this,
            data;
            resolve();
          },
          error (err) {
            reject(err);
          }
        })
    });
  },
  /**
   * 空间删除
   */
  teamspacesDelete: ({
    commit
  }, {
    self,
    query
  }) => {
    return new Promise((resolve, reject) => {
      Api
        .departure
        .nodesDelete
        .call(self, {
          query: query,
          success (data) {
            self = this,
            data;
            resolve();
          },
          error (err) {
            reject(err);
          }
        })
    });
  },
  /**
   * 预览
   */
  filePreview: ({
    commit
  }, {
    self,
    query
  }) => {
    return new Promise((resolve, reject) => {
      Api
        .departure
        .preview
        .call(self, {
          query: query,
          success (data) {
            commit(types.FILE_PREVIEW, {
              self: this,
              data
            });
            resolve(data)
          },
          error (err) {
            reject(err);
          }
        })
    });
  },
  /**
   * 检测离职用户是否还有文件
   */
  fileCheck: ({
    commit
  }, {
    self,
    query
  }) => {
    return new Promise((resolve, reject) => {
      Api
        .departure
        .fileCheck
        .call(self, {
          query: query,
          success (data) {
            self = this,
            data
            resolve(data)
          },
          error (err) {
            reject(err);
          }
        })
    });
  },
  /**
   * 检测离职用户是否还有文件如果有就调用此接口
   */
  deleteHandovEremployees: ({
    commit
  }, {
    self,
    query
  }) => {
    return new Promise((resolve, reject) => {
      Api
        .departure
        .deleteHandovEremployees
        .call(self, {
          query: query,
          success (data) {
            self = this,
            data
            resolve(data)
          },
          error (err) {
            reject(err);
          }
        })
    });
  },
  /**
   * 预览文件
   */
  previewfiles: ({
    commit
  }, {
    self,
    query
  }) => {
    return new Promise((resolve, reject) => {
      Api
        .departure
        .previewfiles
        .call(self, {
          query: query,
          success (data) {
            self = this,
            data
            resolve(data)
          },
          error (err) {
            reject(err);
          }
        })
    });
  }
};
