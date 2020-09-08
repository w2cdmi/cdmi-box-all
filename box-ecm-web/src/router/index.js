import Vue from "vue";
import Router from "vue-router";
Vue.use(Router);

const Login = resolve => require(["@/pages/Login.vue"], resolve);
const NotFound = resolve => require(["@/pages/404.vue"], resolve);
const Home = resolve => require(["@/pages/Home.vue"], resolve);
const Main = resolve => require(["@/pages/Main.vue"], resolve);
const status = resolve => require(["@/components/status.vue"], resolve);
const preview = resolve => require(["@/components/preview.vue"], resolve);
const Management = resolve =>
  require(["@/pages/accountManagement/Management.vue"], resolve);
const AuthorityManagement = resolve =>
  require(["@/pages/accountManagement/AuthorityManagement.vue"], resolve);
const DepartmentAndStaffManagement = resolve =>
  require([
    "@/pages/accountManagement/DepartmentAndStaffManagement.vue"
  ], resolve);
const DepartureManagement = resolve =>
  require(["@/pages/accountManagement/DepartureManagement.vue"], resolve);
const CapacityAllocation = resolve =>
  require(["@/pages/accountManagement/CapacityAllocation.vue"], resolve);
// innerPages
const addMember = resolve =>
  require(["@/pages/accountManagement/innerPage/showMember.vue"], resolve);
const Table = resolve => require(["@/components/Table.vue"], resolve);
const handOver = resolve =>
  require(["@/pages/accountManagement/innerPage/handOver.vue"], resolve);
const Registered = resolve => require(["@/pages/registered.vue"], resolve);
const routers = new Router({
  mode: "hash",
  routes: [
    {
      path: "/",
      redirect: "/login",
      hidden: true,
      meta: {
        title: "后台管理"
      }
    },
    {
      path: "/login",
      component: Login,
      hidden: true,
      name: "login",
      meta: {
        title: "登录"
      }
    },
    {
      path: "/404",
      component: NotFound,
      name: "404",
      hidden: true,
      meta: {
        title: "404"
      }
    },
    {
      path: "/preview",
      component: preview,
      name: "预览",
      hidden: true,
      meta: {
        title: "预览"
      }
    },
    // {   path: '/admin',   component: Home,   name: '首页',   meta: {     title:
    // 'home'   } },
    {
      path: "/",
      component: Home,
      name: "账号管理",
      meta: {
        title: "账号管理"
      },
      children: [
        {
          path: "/DepartmentAndStaffManagement",
          component: DepartmentAndStaffManagement,
          name: "部门与员工管理",
          meta: {
            title: "部门与员工管理"
          },
          // redirect: '/DepartmentAndStaffManagement/employeelist',
          children: [
            // {
            //   path: '/DepartmentAndStaffManagement/employeelist',
            //   component: Table
            //   // hidden: true
            // },
            {
              path: "/DepartmentAndStaffManagement/:id/Member",
              component: addMember,
              meta: {
                title: "部门与员工管理"
              },
              // hidden: true
            }
          ]
        },
        {
          path: "/DepartureManagement",
          component: DepartureManagement,
          name: "离职管理",
          meta: {
            title: "离职管理"
          },
        },
        {
          path: "/AuthorityManagement",
          component: AuthorityManagement,
          name: "应用角色管理",
          meta: {
            title: "应用角色管理"
          },
        },
        {
          path: "/CapacityAllocation",
          component: CapacityAllocation,
          name: "容量配置",
          meta: {
            title: "容量配置"
          },
        },
        {
          path: "/handOver",
          component: handOver,
          name: "权限管理",
          hidden: true,
          meta: {
            title: "权限管理"
          },
        }
      ]
    },
    {
      path: "/registered/:id",
      component: Registered
    },
    // {   path: '/',   component: Home,   name: '任务奖励',   power: '12005', iconCls:
    // 'iconfont icon-fuli',   children: [{       path: '/AuthorityManagement',
    //  component: AuthorityManagement,       power: '12006',       name: '抽奖记录'
    // },     {       path: '/DepartureManagement',      component:
    // DepartureManagement,       power: '12007',       name: '我的奖励'     }   ]  },

    {
      path: "*",
      hidden: true,
      redirect: {
        path: "/404"
      }
    }
  ]
});

export default routers;
