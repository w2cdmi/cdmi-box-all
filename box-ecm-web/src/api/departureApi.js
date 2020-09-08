/**
 * 导出所有模块需要用到接口
 * 一级属性：模块名
 * 一级属性中的方法：当前模块需要用的接口
 * @type {Object}
 */
import ajax from "./ajax";
let api = {
  departure: {
    /**
     * 文档移交列表
     * @param {object} params 参数
     * @param {string} data.XXX XXX
     * @param {function} fn 成功回调
     */

    documentHandOverInnerList (params = {}) {
      ajax.call(
        this,
        "post",
        "/ufm/api/v2/migration/folders/" +
        params.query.ownerId +
        "/" +
        params.query.folderId +
        "/items",
        "",
        params.query.data,
        params.success,
        params.error
      );
    },
    /**
     * 文档移交
     * @param {object} params 参数
     * @param {string} data.XXX XXX
     * @param {function} fn 成功回调
     */
    documentHandOverList (params = {}) {
      ajax.call(
        this,
        "post",
        "/ufm/api/v2/migration/nodes/" + params.ownerId,
        "",
        params.data,
        params.success,
        params.error
      );
    },
    /**
     * 离职管理
     * @param {object} params 参数
     * @param {string} data.XXX XXX
     * @param {function} fn 成功回调
     */

    departureManagement (params = {}) {
      ajax.call(
        this,
        "get",
        "/ecm/api/v2/enterprise/employees?status=-2&deptId=-1",
        "",
        params.data,
        params.success,
        params.error
      );
    },
    /**
     * 权限移交列表
     * @param {object} params 参数
     * @param {string} data.XXX XXX
     * @param {function} fn 成功回调
     */

    teamspacesSpaces (params = {}) {
      ajax.call(
        this,
        "post",
        "/ufm/api/v2/migration/teamspaces/" + params.query.userId + "/spaces",
        "",
        params.query.data,
        params.success,
        params.error
      );
    },
    /**
     * 权限移交
     * @param {object} params 参数
     * @param {string} data.XXX XXX
     * @param {function} fn 成功回调
     */
    teamspacesPut (params = {}) {
      ajax.call(
        this,
        "put",
        "/ufm/api/v2/migration/teamspaces/" + params.query.teamId,
        "",
        params.query.data,
        params.success,
        params.error
      );
    },
    /**
     * 文档移交
     * @param {object} params 参数
     * @param {string} data.XXX XXX
     * @param {function} fn 成功回调
     */
    nodesPut (params = {}) {
      ajax.call(
        this,
        "post",
        "/ufm/api/v2/migration/nodes/" + params.query.ownerId,
        "",
        params.query.data,
        params.success,
        params.error
      );
    },
    /**
     * 文档删除
     * @param {object} params 参数
     * @param {string} data.XXX XXX
     * @param {function} fn 成功回调
     */
    nodesDelete (params = {}) {
      ajax.call(
        this,
        "delete",
        "/ufm/api/v2/migration/folders/" + params.query.ownerId,
        "",
        params.query.data,
        params.success,
        params.error
      );
    },
    /**
     * 空间删除
     * @param {object} params 参数
     * @param {string} data.XXX XXX
     * @param {function} fn 成功回调
     */
    teamspacesDelete (params = {}) {
      ajax.call(
        this,
        "delete",
        "/ufm/api/v2/migration/teamspaces/" + params.query.ownerId,
        "",
        params.query.data,
        params.success,
        params.error
      );
    },
    /**
     * 预览
     * @param {object} params 参数
     * @param {string} data.XXX XXX
     * @param {function} fn 成功回调
     */

    preview (params = {}) {
      console.log(params);
      ajax.call(
        this,
        "get",
        "/ufm/api/v2/files/" + params.query.ownerId + "/" + params.query.fileId + "/preview", "",
        params.query.data,
        params.success,
        params.error
      );
    },
    /**
     * 检测离职用户是否还有文件
     * @param {object} params 参数
     * @param {string} data.XXX XXX
     * @param {function} fn 成功回调
     */

    fileCheck (params = {}) {
      console.log(params);
      ajax.call(
        this,
        "get",
        "/ufm/api/v2/migration/"+ params.query.cloudUserId +"/fileCheck", "",
        params.query.data,
        params.success,
        params.error
      );
    },
    /**
     * 检测离职用户是否还有文件如果有就调用此接口
     * @param {object} params 参数
     * @param {string} data.XXX XXX
     * @param {function} fn 成功回调
     */

    deleteHandovEremployees (params = {}) {
      console.log(params);
      ajax.call(
        this,
        "put",
        "/ecm/api/v2/enterprise/employees/"+ params.query.id +"/handover", "",
        params.query.data,
        params.success,
        params.error
      );
    },
    /**
     * 预览接口
     * @param {object} params 参数
     * @param {string} data.XXX XXX
     * @param {function} fn 成功回调
     */

    previewfiles (params = {}) {
      ajax.call(
        this,
        "get",
        "/ufm/api/v2/files/" + params.query.ownerId + "/" + params.query.nodeId + "/preview", "",
        params.query.data,
        params.success,
        params.error
      );
    }
  }
};

export default api;
