<template>
  <div class="navbar">
    <hamburger :is-active="sidebar.opened" class="hamburger-container" @toggleClick="toggleSideBar" />
    <breadcrumb class="breadcrumb-container" />
    <div class="right-menu">
      <el-dropdown class="avatar-container" trigger="click">
        <div class="avatar-wrapper">
          <img src="@/assets/logo.png" class="user-avatar">
          <i class="el-icon-caret-bottom" />
        </div>
        <el-dropdown-menu slot="dropdown" class="user-dropdown">
          <router-link to="/">
            <el-dropdown-item>首页</el-dropdown-item>
          </router-link>
          <el-dropdown-item divided @click.native="logout">
            <span style="display:block;">退出登录</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Breadcrumb from '@/components/Breadcrumb'
import Hamburger from '@/components/Hamburger'

export default {
  components: { Breadcrumb, Hamburger },
  computed: { ...mapGetters(['sidebar', 'avatar']) },
  methods: {
    toggleSideBar() { this.$store.dispatch('app/toggleSideBar') },
    async logout() {
      await this.$store.dispatch('user/logout')
      this.$router.push(`/login?redirect=${this.$route.fullPath}`)
    }
  }
}
</script>

<style lang="scss" scoped>
.navbar {
  height: 60px;
  overflow: hidden;
  position: relative;
  margin-bottom: 20px;

  /* 悬浮毛玻璃导航条 */
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.8);

  .hamburger-container {
    line-height: 56px;
    height: 100%;
    float: left;
    cursor: pointer;
    transition: background .3s;
    -webkit-tap-highlight-color:transparent;
    padding: 0 20px;
    border-radius: 20px 0 0 20px;
    &:hover { background: rgba(0, 0, 0, .025) }
  }

  .breadcrumb-container { float: left; }

  .right-menu {
    float: right;
    height: 100%;
    line-height: 60px;
    padding-right: 20px;
    &:focus { outline: none; }

    .avatar-container {
      margin-right: 10px;
      .avatar-wrapper {
        margin-top: 10px;
        position: relative;
        cursor: pointer;
        display: flex;
        align-items: center;

        .user-avatar {
          cursor: pointer;
          width: 40px;
          height: 40px;
          border-radius: 50%;
          border: 2px solid #fff;
          box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        .el-icon-caret-bottom {
          cursor: pointer;
          font-size: 12px;
          margin-left: 8px;
          color: #666;
        }
      }
    }
  }
}
</style>
