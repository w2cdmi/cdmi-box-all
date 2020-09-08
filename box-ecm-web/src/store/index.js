import Vue from "vue";
import Vuex from "vuex";
import user from "./user";
import departure from "./departure";
import department from "./department";
import capacity from "./capacity"

Vue.use(Vuex);
export default new Vuex.Store({
  modules: {
    user,
    departure,
    department,
    capacity
  }
});
