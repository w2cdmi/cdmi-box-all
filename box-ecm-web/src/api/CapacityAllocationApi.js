/**
 * 导出所有模块需要用到接口
 * 一级属性：模块名
 * 一级属性中的方法：当前模块需要用的接口
 * @type {Object}
 */
import ajax from "./ajax";
let api = {
  capacity: {
    /**
     *
       获得公司基本信息
     * @param {object} params 参数
     * @param {string} data.XXX XXX
     * @param {function} fn 成功回调
     */

    getenterprise (params = {}) {
      ajax.call(
        this,
        "get",
        "/ecm/api/v2/enterprise/spaceQuota",
        "",
        params.query.data,
        params.success,
        params.error
      );
    },
    /**
     *
       设置个人上限
     * @param {object} params 参数
     * @param {string} data.XXX XXX
     * @param {function} fn 成功回调
     */

    setSpaceQuota (params = {}) {
      ajax.call(
        this,
        "put",
        "/ecm/api/v2/enterprise/employees/spaceQuota",
        "",
        params.query.data,
        params.success,
        params.error
      );
    },
    /**
     *
       设置员工默认上限
     * @param {object} params 参数
     * @param {string} data.XXX XXX
     * @param {function} fn 成功回调
     */

    setEmployeesSpaceQuota (params = {}) {
      console.log(params);
      ajax.call(
        this,
        "put",
        "/ecm/api/v2/enterprise/spaceQuota",
        "",
        params.query.data,
        params.success,
        params.error
      );
    }
  }
};

export default api;
