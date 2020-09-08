import * as types from "./mutations_types";
import utils from "@/common/js/util.js";
import vue from "vue";
export default {
  [types.DEPARTMENTINFO] (state, data) {
    state.departmentInfo = data;
    state.departmentInfoFromat = utils.parseTree(
      JSON.parse(JSON.stringify(data))
    );
  },
  [types.SAVEBYPATH] (state, data) {
    state.departmentInfoBypath[data.path].info = data.data;
    state.departmentInfoBypath[data.path].fromatInfo = utils.parseTree(
      JSON.parse(JSON.stringify(data.data))
    );
  },
  [types.DEPARTEMPLOYEES] (state, data) {
    // state.departmentInfo = data;
    // state
  },
  [types.CHECKEDTREEITEM] (state, { self, id }) {
    let list = utils.checkedTreeitem.call(self, state.departmentInfoFromat, id);
    let lists = [];
    lists = state.departmentInfoFromat;
    lists.forEach((item, index) => {
      lists.splice(index, 1, list.ary[index]);
      vue.set(self.treelist, index, list.ary[index]);
    });
    console.log(state.departmentInfoFromat);
    state.departmentInfoFromat = lists;
  }
};
