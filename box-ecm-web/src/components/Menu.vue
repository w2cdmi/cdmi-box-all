<template>
  <ul class="MENU">
    <li class="">
    <div v-html="menuData.content"></div>
      <p>
        <slot :message="menuData"></slot>
        <span @click="nodeClick(menuData.title)">{{menuData.title}}</span>
      </p>
      <template>
        <div v-for="(item , idx) in menuData.children" :key="idx">
          <transition>
            <Menu :menuData="item">
            </Menu>
          </transition>
        </div>
      </template>
    </li>
  </ul>
</template>
<script>
export default {
  name: "Menu",
  data() {
    return {
      show: []
    };
  },
  props: {
    menuData: {
      type: [Array, Object],
      validator: function() {
        return {};
      }
    }
  },
  watch: {
    menuData: (n, o) => {
      console.log(n);
    }
  },
  computed: {},
  created() {},
  methods: {
    nodeClick() {}
  }
};
</script>
<style lang='less'>
.MENU {
  width: 200px;
  // min-height: 300px;
}
</style>
