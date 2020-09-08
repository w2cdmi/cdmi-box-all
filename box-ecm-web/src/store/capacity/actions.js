import * as types from "./mutations_types";
import Api from "../../api/CapacityAllocationApi.js";
export default {
  /**
   * 获得公司基本信息
   */
  enterprise: ({
    commit
  }, {
    self,
    query
  }) => {
    return new Promise((resolve, reject) => {
      Api.capacity.getenterprise.call(self, {
        query: query,
        success (data) {
          resolve(data)
        },
        error (err) {
          reject(err);
        }
      })
    });
  },
  spacequota: ({
    commit
  }, {
    self,
    query
  }) => {
    return new Promise((resolve, reject) => {
      Api.capacity.setSpaceQuota.call(self, {
        query: query,
        success (data) {
          resolve(data)
        },
        error (err) {
          console.log(err);
          reject(err);
        }
      })
    })
  },
  setEmployeesSpaceQuota: ({
    commit
  }, {
    self,
    query
  }) => {
    return new Promise((resolve, reject) => {
      Api.capacity.setEmployeesSpaceQuota.call(self, {
        query: query,
        success (data) {
          resolve(data)
        },
        error (err) {
          reject(err);
        }
      })
    })
  }
}
