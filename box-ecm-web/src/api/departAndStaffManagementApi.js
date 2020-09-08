/**
 * 导出所有模块需要用到接口
 * 一级属性：模块名
 * 一级属性中的方法：当前模块需要用的接口
 * @type {Object}
 */
import ajax from "./ajax";
let api = {
  /**
   * 添加部门
   * @param {object} params 参数
   * @param {object} data
   * @param {function} fn 成功回调
   * @parentId {string} 父级id
   * @name {sting} 添加节点名称
   */
  addDepart (params = {}) {
    ajax.call(
      this,
      "post",
      `/ecm/api/v2/enterprise/depts?parentId=${params.parentId}&name=${
        params.name
      }`,
      "",
      "",
      params.success,
      params.error
    );
  },
  deleteDepartment (params = {}) {
    /**
     * 删除部门
     * @param {object} params 参数
     * @param {object} data
     * @param {function} fn 成功回调
     * @parentId {string} 节点id
     */
    ajax.call(
      this,
      "delete",
      `/ecm/api/v2/enterprise/depts/${params.deptId}`,
      "",
      "",
      params.success,
      params.error
    );
  },
  modDepartmentName (params = {}) {
    /**
     * 修改部门名称
     * @param {object} params 参数
     * @param {object} data
     * @param {function} fn 成功回调
     * @parentId {string} 修改节点id
     * @name {sting} 修改节点的名称
     */
    ajax.call(
      this,
      "put",
      `/ecm/api/v2/enterprise/depts/${params.deptId}`,
      params.token,
      params.data,
      params.success,
      params.error
    );
  },
  getDepart (params = {}) {
    /**
     * 获取公司是部门结构
     * @param {object} params 参数
     * @param {object} data
     * @param {function} fn 成功回调
     */

    ajax.call(
      this,
      "get",
      "/ecm/api/v2/enterprise/depts/all",
      params.token,
      params.data,
      params.success,
      params.error
    );
  },
  /**
   * 快速搜索部门
   * @param.search {string} 关键字
   */
  searchDepart (params = {}) {
    ajax.call(
      this,
      "get",
      `/ecm/api/v2/enterprise/depts/search?search=${params.search}`,
      "",
      "",
      params.success,
      params.error
    );
  },
  /**
   * 获取部门下的成员
   * @param {object} params 参数
   * @param {object} params.data
   * @param {function} fn 成功回调
   */
  getDepartEmployees (params = {}) {
    if (params.page === undefined) {
      params.page = 1;
      params.pagesize = 1000;
    }
    ajax.call(
      this,
      "get",
      `/ecm/api/v2/enterprise/employees?deptId=${params.deptId}&page=${
        params.page
      }&pageSize=${params.pagesize}&search=${params.search}`,
      params.token,
      "",
      params.success,
      params.error
    );
  },
  /**
   * 获取知识管理员，系统管理员接口
   * @param {object} params 参数
   * @param {object} params.data
   * @param {function} fn 成功回调
   */
  getSpeciaList (params = {}) {
    ajax.call(
      this,
      "get",
      `/ecm/api/v2/enterprise/privileges?role=${params.role}&limit=${
        params.limit
      }&offset=${params.offset}`,
      params.token,
      "",
      params.success,
      params.error
    );
  },
  /**
   * 查询公司所有成员
   * @param {object} params 参数
   * @param {object} params.data
   * page 1开始
   * @param {function} fn 成功回调
   */
  getAllPeople (params = {}) {
    ajax.call(
      this,
      "get",
      `/ecm/api/v2/enterprise/employees?deptId=-1&page=${
        params.page
      }&pageSize=${params.pagesize}`,
      params.token,
      "",
      params.success,
      params.error
    );
  },
  /**
   * 新增知识管理员，系统管理员借口
   * @param {object} params 参数
   * @param {object} params.data
   * @param {function} fn 成功回调
   */
  addSpeciaLists (params = {}) {
    // params.data
    // enterpriseUserId 用户id
    // enterpriseId
    // type 1-> targetId表示部门id 2->targetId企业文库
    // targetId
    // role 2
    ajax.call(
      this,
      "post",
      "/ecm/api/v2/enterprise/privileges",
      params.token,
      params.data,
      params.success,
      params.error
    );
  },
  /**
   * 删除知识管理员，系统管理员借口
   * @param {object} params 参数
   * @param {object} params.data
   * @param {function} fn 成功回调
   */
  deleteManagement (params = {}) {
    ajax.call(
      this,
      "delete",
      `/ecm/api/v2/enterprise/privileges/${params.id}`,
      // /api/v2/enterprise/privileges/{id} 方法一
      // /api/v2/enterprise/privileges 方法二
      params.token,
      params.data,
      params.success,
      params.error
    );
  },
  /**
   * 获取个人信息
   * @param {object} params 参数
   * @param {object} params.data
   * @param {function} fn 成功回调
   */
  getPersonalInfo (params = {}) {
    ajax.call(
      this,
      "get",
      `/ecm/api/v2/enterprise/employees/${params.employeeid}`,
      "",
      "",
      params.success,
      params.error
    );
  },
  /**
   * 设置离职人员
   * @param {object} params 参数
   * @param {object} params.data
   * @param {function} fn 成功回调
   */
  setDeparture (params) {
    ajax.call(
      this,
      "put",
      `/ecm/api/v2/enterprise/employees/${params.employeeid}/dismiss`,
      "",
      "",
      params.success,
      params.error
    );
  },
  /**
   *获取邀请注册链接
   */
  getLink (params) {
    ajax.call(
      this,
      "get",
      "/ecm/api/v2/enterprise/invitToken",
      "",
      "",
      params.success,
      params.error
    );
  },
  /**
   *增加所在的部门
   *
   */
  increaseDepartments (params) {
    ajax.call(
      this,
      "post",
      "/ecm/api/v2/enterprise/user_dept",
      "",
      params.data,
      params.success,
      params.error
    );
  },
  /**
   *修改所在的部门
   *
   */
  modifyDepartments (params) {
    ajax.call(
      this,
      "put",
      "/ecm/api/v2/enterprise/user_dept",
      "",
      params.data,
      params.success,
      params.error
    );
  },
  /**
   *删除所在的部门
   *
   */
  deleteDepartments (params) {
    ajax.call(
      this,
      "delete",
      "/ecm/api/v2/enterprise/user_dept",
      "",
      params.data,
      params.success,
      params.error
    );
  },
  /**
   *获取ECM系统管理员
   * @params {enterpriseId}企业id
   */
  getAdminList (params) {
    ajax.call(
      this,
      "get",
      `/ecm/api/v2/account/${params.enterpriseId}/admin`,
      "",
      params.data,
      params.success,
      params.error
    );
  },
  /**
   * 增加ECM系统管理员
   */
  addAdmin (params) {
    ajax.call(
      this,
      "post",
      `/ecm/api/v2/account/${params.enterpriseId}/admin`,
      "",
      params.data,
      params.success,
      params.error
    );
  },
  /** 删除管理员
   * /api/v2/account/{enterpriseId}/admin/{id}
   */
  delAdmin (params) {
    ajax.call(
      this,
      "delete",
      `/ecm/api/v2/account/${params.enterpriseId}/admin/${params.id}`,
      "",
      "",
      params.success,
      params.error
    );
  },
  /**
   * 获取团队空间....
   */
  getTeamspaces (params) {
    ajax.call(
      this,
      "post",
      "/ufm/api/v2/teamspaces/all",
      "",
      { type: 4 },
      params.success,
      params.error
    );
  }
};
export default api;
