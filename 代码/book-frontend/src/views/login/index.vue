<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h2 class="title">校园旧书漂流共享系统</h2>
        <p class="subtitle">传递书香，共享知识</p>
      </div>

      <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
        <el-form-item prop="username">
          <el-input
            ref="username"
            v-model="loginForm.username"
            placeholder="请输入学号/工号"
            name="username"
            type="text"
            tabindex="1"
            prefix-icon="el-icon-user"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            :key="passwordType"
            ref="password"
            v-model="loginForm.password"
            :type="passwordType"
            placeholder="请输入密码"
            name="password"
            tabindex="2"
            prefix-icon="el-icon-lock"
            @keyup.enter.native="handleLogin"
          />
          <span class="show-pwd" @click="showPwd">
            <svg-icon :icon-class="passwordType === 'password' ? 'eye' : 'eye-open'" />
          </span>
        </el-form-item>

        <div class="form-actions">
          <el-checkbox v-model="rememberMe">记住我</el-checkbox>
          <el-button type="text" class="forgot-pwd" @click="handleForgetPwd">忘记密码？</el-button>
        </div>

        <el-button :loading="loading" type="primary" class="login-btn" @click.native.prevent="handleLogin">
          安全登录
        </el-button>
      </el-form>

      <div class="footer-tips">
        <p>本系统仅供校内师生内部使用</p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Login',
  data() {
    return {
      loginForm: { username: '', password: '' },
      loginRules: {
        username: [{ required: true, trigger: 'blur', message: '学号/工号不能为空' }],
        password: [{ required: true, trigger: 'blur', message: '密码不能为空' }]
      },
      loading: false,
      passwordType: 'password',
      redirect: undefined,
      rememberMe: false
    }
  },
  watch: {
    $route: {
      handler: function(route) { this.redirect = route.query && route.query.redirect },
      immediate: true
    }
  },
  methods: {
    showPwd() {
      this.passwordType = this.passwordType === 'password' ? '' : 'password'
      this.$nextTick(() => { this.$refs.password.focus() })
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          this.$store.dispatch('user/login', this.loginForm).then(() => {
            this.$router.push({ path: this.redirect || '/' })
            this.loading = false
          }).catch(() => {
            this.loading = false
          })
        } else {
          return false
        }
      })
    },
    handleForgetPwd() {
      this.$alert('为保障校内信息安全，请携带本人学生证/教工卡前往图书馆1楼服务台或网络信息中心进行密码重置。', '密码重置指引', {
        confirmButtonText: '我知道了',
        type: 'info'
      });
    }
  }
}
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f0f2f5;
  background-image: url('data:image/svg+xml,%3Csvg width="60" height="60" viewBox="0 0 60 60" xmlns="http://www.w3.org/2000/svg"%3E%3Cg fill="none" fill-rule="evenodd"%3E%3Cg fill="%23d6d9e0" fill-opacity="0.4"%3E%3Cpath d="M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z"/%3E%3C/g%3E%3C/g%3E%3C/svg%3E');

  .login-box {
    width: 420px;
    background: #ffffff;
    border-radius: 16px;
    box-shadow: 0 10px 40px -10px rgba(0, 0, 0, 0.08);
    padding: 50px 40px;
    border: 1px solid #ebeef5;
  }

  .login-header {
    text-align: center;
    margin-bottom: 40px;

    .title {
      font-size: 24px;
      color: #1f2d3d;
      font-weight: 600;
      margin: 0 0 8px 0;
    }
    .subtitle {
      font-size: 14px;
      color: #909399;
      margin: 0;
    }
  }

  .form-actions {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;

    .forgot-pwd {
      color: #909399;
      font-size: 14px;
      &:hover { color: #409eff; }
    }
  }

  .login-btn {
    width: 100%;
    height: 44px;
    font-size: 16px;
    border-radius: 8px;
    font-weight: 500;
    letter-spacing: 1px;
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
  }

  .show-pwd {
    position: absolute;
    right: 15px;
    top: 0px;
    font-size: 16px;
    color: #c0c4cc;
    cursor: pointer;
  }

  ::v-deep .el-input__inner {
    height: 46px;
    background: #f7f9fc !important;
    border: 1px solid transparent !important;
    border-radius: 8px !important;
    padding-left: 35px;
    transition: all 0.3s;
    &:focus {
      background: #ffffff !important;
      border-color: #409eff !important;
      box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
    }
  }

  .footer-tips {
    margin-top: 30px;
    text-align: center;
    font-size: 12px;
    color: #c0c4cc;
  }
}
</style>
