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

        <el-form-item style="margin-top: 30px; text-align: center; margin-left: -100px;">
          <el-button type="primary" @click="submitForm('ruleForm')" style="width: 120px;" round>确认修改</el-button>
          <el-button @click="resetForm('ruleForm')" style="width: 120px;" round>重置输入</el-button>
        </el-form-item>
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

<style scoped>
.app-container {
  padding: 40px;
  background-color: #f7f9fc;
  min-height: calc(100vh - 50px);
}
::v-deep .el-input__inner {
  border-radius: 20px;
}
</style>
