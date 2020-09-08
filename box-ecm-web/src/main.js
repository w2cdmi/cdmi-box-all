import "babel-polyfill";
import Vue from "vue";
import VueVideoPlayer from "vue-video-player";
import router from "./router";
import axios from "axios";
import ElementUI from "element-ui";
// import "element-ui/lib/theme-chalk/index.css"; // 默认主题
import "video.js/dist/video-js.css";
// import '../static/css/theme-green/index.css';       // 浅绿色主题
import "../static/css/index.css";
import store from "./store/index.js";
import VueClipboard from "vue-clipboard2";
import preview from "./vue-photo/src/lib/index";
import "vue-photo-preview/dist/skin.css";
import App from "./App";
Vue.use(VueClipboard);
Vue.use(ElementUI, { size: "small" });
Vue.use(
  VueVideoPlayer /* {
  options: global default options,
  events: global videojs events
} */
);
// Vue.directive("hello", {
//   bind: el => {
//     console.log("hello");
//     console.log("haha el", el);
//   },
//   componentUpdated: el => {}
// });
var options = {
  fullscreenEl: false // 关闭全屏按钮
};
Vue.use(preview, options);

Vue.prototype.$axios = axios;
// 使用钩子函数对路由进行权限跳转;
router.beforeEach((to, from, next) => {
  if (to.meta.title) {
    document.title = to.meta.title;
  } else {
    document.title = "admin";
  }
  next();
});

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount("#app");
