<template>
  <div class="app-container">
    <el-card class="box-card" shadow="hover" style="max-width: 500px; margin: 50px auto; border-radius: 16px;">
      <div slot="header" class="clearfix" style="text-align: center;">
        <span style="font-size: 18px; font-weight: bold; color: #303133;">🔒 修改登录密码</span>
      </div>

      <el-form :model="ruleForm" status-icon :rules="rules" ref="ruleForm" label-width="100px" size="medium">
        <el-form-item label="当前密码" prop="oldPassword">
          <el-input type="password" v-model="ruleForm.oldPassword" autocomplete="off" placeholder="请输入原密码" show-password></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input type="password" v-model="ruleForm.newPassword" autocomplete="off" placeholder="请输入新密码" show-password></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input type="password" v-model="ruleForm.confirmPassword" autocomplete="off" placeholder="请再次输入新密码" show-password></el-input>
        </el-form-item>

        <div class="action-buttons">
          <el-button type="primary" @click="submitForm('ruleForm')" class="unified-btn primary-shadow">确认修改</el-button>
          <el-button @click="resetForm('ruleForm')" class="unified-btn default-shadow">重置输入</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { updatePassword } from '@/api/user'
import { mapGetters } from 'vuex'

export default {
  name: 'Password',
  data() {
    var validatePass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入新密码'));
      } else {
        if (this.ruleForm.confirmPassword !== '') {
          this.$refs.ruleForm.validateField('confirmPassword');
        }
        callback();
      }
    };
    var validatePass2 = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'));
      } else if (value !== this.ruleForm.newPassword) {
        callback(new Error('两次输入密码不一致!'));
      } else {
        callback();
      }
    };
    return {
      ruleForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      rules: {
        oldPassword: [
          { required: true, message: '请输入当前密码', trigger: 'blur' }
        ],
        newPassword: [
          { validator: validatePass, trigger: 'blur' }
        ],
        confirmPassword: [
          { validator: validatePass2, trigger: 'blur' }
        ]
      }
    };
  },
  computed: {
    ...mapGetters(['id'])
  },
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          const data = {
            userid: this.id,
            oldPassword: this.ruleForm.oldPassword,
            newPassword: this.ruleForm.newPassword
          }
          updatePassword(data).then(res => {
            if (res === 1) {
              this.$message.success('密码修改成功，请牢记新密码！');
              this.resetForm(formName);
            } else {
              this.$message.error('原密码错误或修改失败');
            }
          })
        } else {
          return false;
        }
      });
    },
    resetForm(formName) {
      this.$refs[formName].resetFields();
    }
  }
}
</script>

<style lang="scss" scoped>
.app-container {
  padding: 40px;
  background-color: #f7f9fc;
  min-height: calc(100vh - 50px);
}

::v-deep .el-input__inner {
  border-radius: 20px;
}

/* ======== 新增按钮统一样式 ======== */
.action-buttons {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 24px; /* 控制两个按钮之间的间距 */
  margin-top: 35px;
  padding-bottom: 10px;

  /* 强制统一基础按钮样式 */
  ::v-deep .unified-btn {
    width: 130px !important;
    height: 40px !important;
    border-radius: 20px !important;
    margin: 0 !important; /* 清除 Element UI 的默认外边距 */
    padding: 0 !important; /* 清除默认内边距 */
    display: flex !important;
    justify-content: center !important;
    align-items: center !important;
    font-size: 15px !important;
    font-weight: 500 !important;
    letter-spacing: 2px !important; /* 稍微拉开字间距更好看 */

    span {
      margin-left: 0 !important;
    }
  }

  /* 蓝色主按钮 */
  ::v-deep .primary-shadow {
    background-color: #409eff !important;
    border-color: #409eff !important;
    box-shadow: 0 4px 10px rgba(64, 158, 255, 0.3) !important;
    &:hover {
      background-color: #66b1ff !important;
      border-color: #66b1ff !important;
    }
  }

  /* 白色重置按钮 */
  ::v-deep .default-shadow {
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.04) !important;
    &:hover {
      color: #409eff !important;
      border-color: #c6e2ff !important;
      background-color: #ecf5ff !important;
    }
  }
}
</style>
