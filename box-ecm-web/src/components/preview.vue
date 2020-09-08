<template>
  <div>
    <div class="preview-reader">
      <iframe class="preview-reader-ifeame" id="reader"></iframe>
    </div>
  </div>
</template>
<script>
export default {
  data() {
    return {
      showfile: "",
      preview: {
        ownerId: this.$route.query.ownerId,
        nodeId: this.$route.query.nodeId
      }
    };
  },
  created() {
    this.$store.dispatch("localToken");
    console.log(this.$route.query);
    this.init();
  },
  methods: {
    init() {
      this.$store
        .dispatch("previewfiles", {
          self: this,
          query: this.preview
        })
        .then(data => {
          console.log(data);
        })
        .catch(err => {});
    }
  }
};
</script>

<style lang="less">
.preview-reader {
  width: 100%;
  height: 100%;
  position: fixed;
  top: 0;
  bottom: 0;
  background: #fff;
  z-index: 99999;
}

.preview-reader-ifeame {
  width: 100%;
  height: 100%;
  border: none;
}
</style>

