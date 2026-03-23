<template>
  <div class="sidebar-logo-container" :class="{'collapse':collapse}">
    <div class="sidebar-logo-content">
      <!-- 图标按钮：点击切换侧边栏展开/收缩 -->
      <div class="logo-icon" :class="{'collapsed': collapse}" :title="collapse ? '展开菜单' : '收起菜单'" @click.prevent="toggleSidebar">
        <i class="el-icon-reading"></i>
      </div>
      <!-- 展开时显示标题，点击跳转首页 -->
      <transition name="titleFade">
        <h1 v-if="!collapse" class="sidebar-title" @click="goHome">校园旧书漂流共享系统</h1>
      </transition>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SidebarLogo',
  props: {
    collapse: {
      type: Boolean,
      required: true
    }
  },
  methods: {
    toggleSidebar() {
      this.$store.dispatch('app/toggleSideBar')
    },
    goHome() {
      if (this.$route.path !== '/') {
        this.$router.push('/')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.titleFade-enter-active,
.titleFade-leave-active {
  transition: opacity 0.3s, transform 0.3s;
}

.titleFade-enter,
.titleFade-leave-to {
  opacity: 0;
  transform: translateX(-10px);
}

.sidebar-logo-container {
  position: relative;
  width: 100%;
  height: 70px;
  background: linear-gradient(135deg, #1e3a8a, #1e40af);
  text-align: center;
  overflow: hidden;
  box-shadow: 0 1px 0 0 rgba(0, 0, 0, 0.05);

  .sidebar-logo-content {
    height: 100%;
    width: 100%;
    display: flex;
    align-items: center;
    padding-left: 20px;
  }

  &.collapse .sidebar-logo-content {
    justify-content: center;
    padding-left: 0;
  }

  .logo-icon {
    width: 40px;
    height: 40px;
    min-width: 40px;
    background: rgba(255, 255, 255, 0.15);
    border-radius: 8px;
    margin-right: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1), 0 0 15px rgba(255, 255, 255, 0.1) inset;
    backdrop-filter: blur(5px);
    cursor: pointer;
    transition: all 0.3s ease;

    &:hover {
      background: rgba(255, 255, 255, 0.3);
      transform: scale(1.08);
      box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15), 0 0 20px rgba(255, 255, 255, 0.2) inset;
    }

    &:active {
      transform: scale(0.95);
    }

    &.collapsed {
      margin: 0;
    }

    i {
      font-size: 24px;
      color: #fff;
      transition: transform 0.3s ease;
    }

    &:hover i {
      transform: rotate(15deg);
    }
  }

  .sidebar-title {
    margin: 0;
    color: #fff;
    font-weight: 600;
    font-size: 17px;
    font-family: "Microsoft YaHei", Arial, sans-serif;
    letter-spacing: 1px;
    white-space: nowrap;
    line-height: 40px;
    display: flex;
    align-items: center;
    height: 40px;
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
    cursor: pointer;
    transition: opacity 0.2s;

    &:hover {
      opacity: 0.85;
    }
  }
}
</style>
