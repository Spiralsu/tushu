<template>
  <div style="padding: 0 15px;" @click="toggleClick">
    <i :class="isActive ? 'el-icon-s-fold' : 'el-icon-s-unfold'" class="hamburger-icon"></i>
  </div>
</template>

<script>
export default {
  name: 'Hamburger',
  props: {
    isActive: {
      type: Boolean,
      default: false
    }
  },
  methods: {
    toggleClick() {
      this.$emit('toggleClick')
    }
  }
}
</script>

<style scoped>
.hamburger-icon {
  font-size: 22px;
  color: #5a5e66;
  cursor: pointer;
  transition: color 0.3s;
}
.hamburger-icon:hover {
  color: #409eff;
}
</style>
