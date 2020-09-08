import * as types from "./mutations_types";
import Api from "../../api";

const Depart = Api.departAndStaffManagementApi.default;

export default {
  getDepart: ({ dispatch, commit }, { self, obj = {} }) => {
    return new Promise((resolve, reject) => {
      Depart.getDepart.call(self, {
        data: {},
        success (data) {
          dispatch("saveDepartInfo", data);
          resolve(data);
        },
        error (err) {
          reject(err);
        }
      });
    });
  },
  saveDepartInfo: ({ dispatch, commit }, data) => {
    commit("DEPARTMENTINFO", JSON.parse(JSON.stringify(data)));
  },
  saveBypathDepartInfo: ({ dispatch, commit }, { path, data }) => {
    let info = JSON.parse(JSON.stringify(data));
    commit("SAVEBYPATH", { path, info });
  },
  addDepart: ({ dispatch, commit }, { self, obj = {} }) => {
    return new Promise((resolve, reject) => {
      Depart.addDepart.call(self, {
        parentId: obj.parentId,
        name: obj.name,
        success (data) {
          resolve(data);
        },
        error (err) {
          reject(err);
        }
      });
    });
  },
  modDepartmentName: ({ dispatch, commit }, { self, obj = {} }) => {
    return new Promise((resolve, reject) => {
      Depart.modDepartmentName.call(self, {
        deptId: obj.deptId,
        data: { name: obj.name },
        success (data) {
          resolve(data);
        },
        error (err) {
          reject(err);
        }
      });
    });
  },
  deleteDepartment: ({ dispatch, commit }, { self, obj = {} }) => {
    return new Promise((resolve, reject) => {
      Depart.deleteDepartment.call(self, {
        deptId: obj.deptId,
        success (data) {
          resolve(data);
        },
        error (err) {
          reject(err);
        }
      });
    });
  },
  searchDepart: ({ dispatch, commit }, { self, obj = {} }) => {
    return new Promise((resolve, reject) => {
      Depart.searchDepart.call(self, {
        search: obj.search,
        data: {},
        success (data) {
          // commit("DEPARTMENTINFO", JSON.parse(JSON.stringify(data)));
          resolve(data);
        },
        error (err) {
          reject(err);
        }
      });
    });
  },
  getDepartEmployees: ({ dispatch, commit }, { self, obj = {} }) => {
    return new Promise((resolve, reject) => {
      Depart.getDepartEmployees.call(self, {
        deptId: obj.deptId,
        page: obj.page,
        pagesize: obj.pagesize,
        search: obj.search || "",
        data: "",
        success (res) {
          commit(types.DEPARTMENTINFO, res);
          commit(types.DEPARTEMPLOYEES, { id: obj.deptId, data: res });
          resolve(res);
        },
        error (err) {
          reject(err);
        }
      });
    });
  },
  getSpeciaList: ({ dispatch, commit }, { self, obj = {} }) => {
    return new Promise((resolve, reject) => {
      Depart.getSpeciaList.call(self, {
        role: obj.role,
        limit: obj.limit,
        offset: obj.offset,
        data: "",
        success (res) {
          // commit(types.DEPARTMENTINFO, res);
          // commit(types.DEPARTEMPLOYEES, { id: obj.deptId, data: res });
          resolve(res);
        },
        error (err) {
          reject(err);
        }
      });
    });
  },
  getAllpeople: ({ dispatch, commit }, { self, obj = {} }) => {
    return new Promise((resolve, reject) => {
      Depart.getAllPeople.call(self, {
        page: obj.page,
        deptId: obj.deptId,
        pagesize: obj.pagesize,
        data: "",
        success (res) {
          // commit(types.DEPARTMENTINFO, res);
          // commit(types.DEPARTEMPLOYEES, { id: obj.deptId, data: res });
          resolve(res);
        },
        error (err) {
          reject(err);
        }
      });
    });
  },
  addSpeciaLists: ({ dispatch, commit }, { self, obj = {} }) => {
    return new Promise((resolve, reject) => {
      Depart.addSpeciaLists.call(self, {
        data: obj,
        success (res) {
          resolve(res);
        },
        error (err) {
          reject(err);
        }
      });
    });
  },
  deleteManagement: ({ dispatch, commit }, { self, id }) => {
    return new Promise((resolve, reject) => {
      Depart.deleteManagement.call(self, {
        id: id,
        success (res) {
          resolve(res);
        },
        error (err) {
          reject(err);
        }
      });
    });
  },
  getPersonalInfo: ({ dispatch, commit }, { self, obj = {} }) => {
    return new Promise((resolve, reject) => {
      Depart.getPersonalInfo.call(self, {
        employeeid: obj.employeeid,
        data: "",
        success (res) {
          resolve(res);
        },
        error (err) {
          reject(err);
        }
      });
    });
  },
  setDeparture: ({ dispatch, commit }, { self, obj = {} }) => {
    return new Promise((resolve, reject) => {
      Depart.setDeparture.call(self, {
        employeeid: obj.employeeid,
        success (res) {
          resolve(res);
        },
        error (err) {
          reject(err);
        }
      });
    });
  },
  getLink: ({ dispatch, commit }, { self }) => {
    return new Promise((resolve, reject) => {
      Depart.getLink.call(self, {
        success (res) {
          resolve(res);
        },
        error (err) {
          reject(err);
        }
      });
    });
  },
  deleteDepartments: ({ dispatch, commit }, { self, obj }) => {
    return new Promise((resolve, reject) => {
      Depart.deleteDepartments.call(self, {
        data: obj,
        success (res) {
          resolve(res);
        },
        error (err) {
          reject(err);
        }
      });
    });
  },
  modifyDepartments: ({ dispatch, commit }, { self, obj }) => {
    return new Promise((resolve, reject) => {
      Depart.modifyDepartments.call(self, {
        data: obj,
        success (res) {
          resolve(res);
        },
        error (err) {
          reject(err);
        }
      });
    });
  },
  increaseDepartments: ({ dispatch, commit }, { self, obj }) => {
    return new Promise((resolve, reject) => {
      Depart.increaseDepartments.call(self, {
        data: obj,
        success (res) {
          resolve(res);
        },
        error (err) {
          reject(err);
        }
      });
    });
  },
  getAdminList: ({ dispatch, commit }, { self, enterpriseId }) => {
    return new Promise((resolve, reject) => {
      Depart.getAdminList.call(self, {
        enterpriseId: enterpriseId,
        success (res) {
          resolve(res);
        },
        error (err) {
          reject(err);
        }
      });
    });
  },
  delAdmin: ({ dispatch, commit }, { self, enterpriseId, id }) => {
    return new Promise((resolve, reject) => {
      Depart.delAdmin.call(self, {
        enterpriseId: enterpriseId,
        id: id,
        success (res) {
          resolve(res);
        },
        error (err) {
          reject(err);
        }
      });
    });
  },
  addAdmin: ({ dispatch, commit }, { self, enterpriseId, loginName }) => {
    return new Promise((resolve, reject) => {
      Depart.addAdmin.call(self, {
        enterpriseId: enterpriseId,
        data: { loginName, enterpriseId },
        success (res) {
          resolve(res);
        },
        error (err) {
          reject(err);
        }
      });
    });
  },
  getTeamspaces ({ dispatch, commit }, { self }) {
    return new Promise((resolve, reject) => {
      Depart.getTeamspaces.call(self, {
        success (res) {
          resolve(res);
        },
        error (err) {
          reject(err);
        }
      });
    });
  }
};
