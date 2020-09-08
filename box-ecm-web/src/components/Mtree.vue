<template>
  <ul class="tree">
    <li class="tree-item">
      <p class="tree-item-title">
        <span  @click="nodeClick(data.title)">{{data.title}}</span>
        <i v-if="data.children" class="el-icon-caret-bottom"></i>
      </p> 
      <transition>
        <template>
          <item v-for="(item , idx) in data.children" :data="item" :key="idx"></item>
        </template>
      </transition>
    </li>
  </ul>
</template>
<script>
export default {
  name: "item",
  data() {
    return {};
  },
  props: {
    data: {
      type: [Array, Object],
      validator: function() {
        return [];
      }
    }
  },
  methods: {
    nodeClick(e) {
      console.log("e", e);
    }
  }
};
</script>
<style lang="less" scoped>
ul {
  list-style: none;
}
.tree {
  width: 100%;
  float: left;
  .tree-item {
    width: 100%;
    padding: 5px 0;
    // margin-left: 10px;
    .tree-item-title {
      cursor: pointer;
      width: 100%;
      float: left;
      &:hover {
        background-color: #eeeeee;
      }
    }
  }
}
</style>
