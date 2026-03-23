<template>
  <div class="custom-sidebar" :class="{ 'is-collapsed': collapsed }">
    <!-- 首页菜单项 -->
    <el-tooltip :disabled="!collapsed" content="首页" placement="right" :open-delay="200">
      <div class="menu-item" :class="{ active: $route.path === '/dashboard' }" @click="navigate('/dashboard')">
        <div class="menu-icon-wrapper">
          <i class="el-icon-s-grid menu-icon"></i>
        </div>
        <span v-if="!collapsed" class="menu-title">首页</span>
      </div>
    </el-tooltip>

    <!-- 图书管理菜单组 -->
    <div class="menu-group">
      <el-popover :disabled="!collapsed" placement="right-start" trigger="hover" popper-class="sidebar-submenu-popover" :visible-arrow="false" :open-delay="200">
        <div class="popover-menu-list">
          <div class="popover-menu-title">图书管理</div>
          <div class="popover-menu-item" :class="{ active: $route.path.includes('/bookmanage/bookinfo') }" @click="navigate('/bookmanage/bookinfo')">
            <i class="el-icon-notebook-1"></i>
            <span>图书信息管理</span>
          </div>
          <div v-if="isAdmin" class="popover-menu-item" :class="{ active: $route.path.includes('/bookmanage/booktype') }" @click="navigate('/bookmanage/booktype')">
            <i class="el-icon-collection-tag"></i>
            <span>图书类型管理</span>
          </div>
          <div class="popover-menu-item" :class="{ active: $route.path.includes('/bookmanage/borrow') }" @click="navigate('/bookmanage/borrow')">
            <i class="el-icon-document"></i>
            <span>借阅信息管理</span>
          </div>
        </div>
        <div slot="reference" class="menu-item parent-menu"
             :class="{ active: $route.path.includes('/bookmanage'), expanded: bookManageExpanded && !collapsed }"
             @click="handleParentClick('bookManage', '/bookmanage/bookinfo')">
          <div class="menu-icon-wrapper">
            <i class="el-icon-reading menu-icon"></i>
          </div>
          <span v-if="!collapsed" class="menu-title">图书管理</span>
          <i v-if="!collapsed" class="el-icon-arrow-down menu-arrow"></i>
        </div>
      </el-popover>

      <div v-if="!collapsed" class="submenu-container" :class="{ expanded: bookManageExpanded }">
        <div class="menu-item sub-menu"
             :class="{ active: $route.path.includes('/bookmanage/bookinfo') }"
             @click.stop="navigate('/bookmanage/bookinfo')">
          <div class="menu-icon-wrapper">
            <i class="el-icon-notebook-1 menu-icon"></i>
          </div>
          <span class="menu-title">图书信息管理</span>
        </div>

        <div v-if="isAdmin" class="menu-item sub-menu"
             :class="{ active: $route.path.includes('/bookmanage/booktype') }"
             @click.stop="navigate('/bookmanage/booktype')">
          <div class="menu-icon-wrapper">
            <i class="el-icon-collection-tag menu-icon"></i>
          </div>
          <span class="menu-title">图书类型管理</span>
        </div>

        <div class="menu-item sub-menu"
             :class="{ active: $route.path.includes('/bookmanage/borrow') }"
             @click.stop="navigate('/bookmanage/borrow')">
          <div class="menu-icon-wrapper">
            <i class="el-icon-document menu-icon"></i>
          </div>
          <span class="menu-title">借阅信息管理</span>
        </div>
      </div>
    </div>

    <!-- 求书心愿广场 -->
    <el-tooltip :disabled="!collapsed" content="求书心愿广场" placement="right" :open-delay="200">
      <div class="menu-item"
           :class="{ active: $route.path.startsWith('/wish') }"
           @click="navigate('/wish/index')">
        <div class="menu-icon-wrapper">
          <i class="el-icon-star-off menu-icon"></i>
        </div>
        <span v-if="!collapsed" class="menu-title">求书心愿广场</span>
      </div>
    </el-tooltip>

    <!-- 其他管理菜单组（管理员） -->
    <div v-if="isAdmin" class="menu-group">
      <el-popover :disabled="!collapsed" placement="right-start" trigger="hover" popper-class="sidebar-submenu-popover" :visible-arrow="false" :open-delay="200">
        <div class="popover-menu-list">
          <div class="popover-menu-title">其他管理</div>
          <div class="popover-menu-item" :class="{ active: $route.path.includes('/other/user') }" @click="navigate('/other/user')">
            <i class="el-icon-user-solid"></i>
            <span>用户管理</span>
          </div>
          <div class="popover-menu-item" :class="{ active: $route.path.includes('/other/password') }" @click="navigate('/other/password')">
            <i class="el-icon-key"></i>
            <span>密码更改</span>
          </div>
        </div>
        <div slot="reference" class="menu-item parent-menu"
             :class="{ active: $route.path.includes('/other'), expanded: otherManageExpanded && !collapsed }"
             @click="handleParentClick('otherManage', '/other/user')">
          <div class="menu-icon-wrapper">
            <i class="el-icon-setting menu-icon"></i>
          </div>
          <span v-if="!collapsed" class="menu-title">其他管理</span>
          <i v-if="!collapsed" class="el-icon-arrow-down menu-arrow"></i>
        </div>
      </el-popover>

      <div v-if="!collapsed" class="submenu-container" :class="{ expanded: otherManageExpanded }">
        <div class="menu-item sub-menu"
             :class="{ active: $route.path.includes('/other/user') }"
             @click.stop="navigate('/other/user')">
          <div class="menu-icon-wrapper">
            <i class="el-icon-user-solid menu-icon"></i>
          </div>
          <span class="menu-title">用户管理</span>
        </div>

        <div class="menu-item sub-menu"
             :class="{ active: $route.path.includes('/other/password') }"
             @click.stop="navigate('/other/password')">
          <div class="menu-icon-wrapper">
            <i class="el-icon-key menu-icon"></i>
          </div>
          <span class="menu-title">密码更改</span>
        </div>
      </div>
    </div>

    <!-- 普通用户的密码更改菜单项 -->
    <el-tooltip :disabled="!collapsed" content="密码更改" placement="right" :open-delay="200">
      <div v-if="!isAdmin" class="menu-item"
           :class="{ active: $route.path.includes('/other/password') }"
           @click="navigate('/other/password')">
        <div class="menu-icon-wrapper">
          <i class="el-icon-key menu-icon"></i>
        </div>
        <span v-if="!collapsed" class="menu-title">密码更改</span>
      </div>
    </el-tooltip>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'CustomSidebar',
  props: {
    collapsed: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      bookManageExpanded: false,
      otherManageExpanded: false
    }
  },
  computed: {
    ...mapGetters([
      'roles'
    ]),
    isAdmin() {
      return this.roles.includes('admin')
    }
  },
  methods: {
    navigate(path) {
      this.$router.push(path);
    },
    isSubActive(subs) {
      return subs.some(sub => this.$route.path.includes(sub));
    },
    toggleExpand(menu) {
      if (menu === 'bookManage') {
        this.bookManageExpanded = !this.bookManageExpanded;
      } else if (menu === 'otherManage') {
        this.otherManageExpanded = !this.otherManageExpanded;
      }
    },
    // 收缩状态下点击父菜单直接导航到第一个子菜单，展开状态下切换展开/关闭
    handleParentClick(menu, defaultPath) {
      if (this.collapsed) {
        this.navigate(defaultPath);
      } else {
        this.toggleExpand(menu);
      }
    }
  },
  created() {
    // 自动展开当前路径所在的菜单
    if (this.$route.path.includes('/bookmanage')) {
      this.bookManageExpanded = true;
    }
    if (this.$route.path.includes('/other') && this.isAdmin) {
      this.otherManageExpanded = true;
    }
  }
}
</script>

<style lang="scss">
.sidebar-submenu-popover {
  padding: 8px 0;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border: 1px solid #ebeef5;
  background: #ffffff;
  
  .popover-menu-title {
    padding: 8px 16px;
    font-size: 13px;
    color: #909399;
    font-weight: 500;
    margin-bottom: 4px;
  }
  
  .popover-menu-item {
    padding: 10px 20px;
    cursor: pointer;
    display: flex;
    align-items: center;
    color: #414750;
    font-size: 14px;
    transition: all 0.3s;
    
    i {
      margin-right: 10px;
      font-size: 16px;
    }
    
    &:hover {
      background-color: #f0f7ff;
      color: #1890FF;
    }
    
    &.active {
      color: #1890FF;
      font-weight: 600;
      background-color: #e6f2ff;
    }
  }
}
</style>

<style lang="scss" scoped>
.custom-sidebar {
  height: 100%;
  padding-bottom: 20px;
  box-sizing: border-box;

  .menu-group {
    margin-bottom: 4px;
    position: relative;
  }

  .menu-item {
    display: flex;
    align-items: center;
    height: 56px;
    padding: 0 20px;
    cursor: pointer;
    transition: all 0.3s;
    position: relative;
    border-left: 4px solid transparent;
    margin: 4px 0;
    border-radius: 0 6px 6px 0;
    margin-right: 12px;

    &:hover {
      background-color: #e6f2ff;

      .menu-icon-wrapper {
        transform: scale(1.05);
      }
    }

    &.active {
      background-color: #e6f2ff;
      color: #1890FF;
      font-weight: 600;
      border-left: 4px solid #1890FF;

      .menu-icon-wrapper {
        background: rgba(24, 144, 255, 0.15);
      }

      .menu-icon {
        color: #1890FF;
      }
    }

    &.parent-menu {
      &.expanded {
        background-color: #f0f7ff;

        .menu-arrow {
          transform: rotate(180deg);
          color: #1890FF;
        }
      }
    }

    &.sub-menu {
      height: 46px;
      padding-left: 48px;
      margin: 2px 12px 2px 0;
      border-radius: 0 6px 6px 0;

      &:hover {
        background-color: #e6f2ff;
      }

      .menu-icon-wrapper {
        width: 28px;
        height: 28px;
      }

      .menu-icon {
        font-size: 16px;
      }
    }
  }

  .submenu-container {
    max-height: 0;
    overflow: hidden;
    transition: max-height 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    background-color: rgba(240, 245, 255, 0.5);
    border-radius: 0 0 8px 0;
    margin-top: -4px;

    &.expanded {
      max-height: 200px; // 足够容纳子菜单
      padding-top: 4px;
      padding-bottom: 4px;
    }
  }

  .menu-icon-wrapper {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 32px;
    height: 32px;
    background: rgba(24, 144, 255, 0.1);
    border-radius: 8px;
    transition: all 0.3s;
    margin-right: 12px;
  }

  .menu-icon {
    font-size: 18px;
    color: #414750;
  }

  .menu-title {
    font-size: 14px;
    line-height: 1.5;
    flex: 1;
  }

  .menu-arrow {
    font-size: 12px;
    transition: transform 0.3s;
    margin-left: 8px;
    color: #9da5b1;
  }

  // ===== 收缩状态的样式 =====
  &.is-collapsed {
    .menu-item {
      justify-content: center;
      padding: 0;
      margin: 4px 8px;
      border-left: none;
      border-radius: 8px;
      height: 48px;

      &.active {
        border-left: none;
        background-color: #e6f2ff;
        border-radius: 8px;

        // 使用底部小圆点代替左边框作为激活指示器
        &::after {
          content: '';
          position: absolute;
          bottom: 4px;
          left: 50%;
          transform: translateX(-50%);
          width: 16px;
          height: 3px;
          border-radius: 2px;
          background-color: #1890FF;
        }
      }
    }

    .menu-icon-wrapper {
      margin-right: 0;
    }
  }
}
</style>
