<template>
  <div id="app">
    <router-view></router-view>
  </div>
</template>

<script>
export default {
  name: "app",
  data() {
    return {};
  },
  components: {}
};
</script>

<style lang="less">
body {
  margin: 0px;
  padding: 0px;
  font-family: "Microsoft YaHei", "serif", "sans-serif", "cursive", "fantasy",
    "monospace";
  font-size: 14px;
  -webkit-font-smoothing: antialiased;
  background: #f5f5f5;
}
#app {
  position: absolute;
  top: 0px;
  bottom: 0px;
  width: 100%;
  min-width: 1400px;
  background-size: cover;
  overflow: hidden;
}
* {
  margin: 0;
  padding: 0;
}
a:link,
a:visited {
  text-decoration: none;
  /*超链接无下划线*/
}
::-webkit-scrollbar {
  width: 10px;
  height: 10px;
  background-color: #eeeeee;
}

/*定义滚动条轨道 内阴影+圆角*/
::-webkit-scrollbar-track {
  -webkit-box-shadow: inset 0 0 3px rgba(0, 0, 0, 0.1);
  border-radius: 10px;
  background-color: #eeeeee;
}

/*定义滑块 内阴影+圆角*/
::-webkit-scrollbar-thumb {
  border-radius: 10px;
  -webkit-box-shadow: inset 0 0 3px rgba(0, 0, 0, 0.1);
  background-color: rgba(0, 0, 0, 0.1);
}
.el-button {
  background: #fafafa;
  border: 1px solid #eaeaea;
  color: #333333;
  &:hover {
    background: #f5f5f5;
  }
}
.el-table {
  margin-top: 20px;
  border: 0;
  .el-table__row {
    cursor: pointer;
  }
}
.el-table--border td,
.el-table--border th {
  border-right: 0;
}
.el-table__body tr.hover-row > td {
  background: #eeeeee !important;
}
.el-table th.is-leaf {
  background: #fafafa;
}
.el-table td,
.el-table th.is-leaf {
  border: 0;
}
.el-table--border::after,
.el-table--group::after,
.el-table::before {
  background: transparent;
}
</style>