<template>
  <div class="security-container">
    <el-card class="security-card">
      <div class="card-content">
        <div class="visual-side">
          <div class="icon-box">
            <i class="el-icon-lock"></i>
          </div>
          <h2>安全中心</h2>
          <p>定期修改密码，保护您的漂流账户安全</p>
          <ul class="security-tips">
            <li><i class="el-icon-check"></i> 建议使用 6 位以上密码</li>
            <li><i class="el-icon-check"></i> 不要使用纯数字</li>
            <li><i class="el-icon-check"></i> 妥善保管您的账号</li>
          </ul>
        </div>

        <div class="form-side">
          <h3>修改登录密码</h3>
          <el-form ref="ruleForm" :model="ruleForm" :rules="rules" label-position="top" class="pwd-form">
            <el-form-item label="原密码" prop="oldPassword">
              <el-input v-model="ruleForm.oldPassword" type="password" placeholder="请输入当前使用的密码" show-password prefix-icon="el-icon-key"></el-input>
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="ruleForm.newPassword" type="password" placeholder="请输入想要设置的新密码" show-password prefix-icon="el-icon-lock"></el-input>
            </el-form-item>
            <el-form-item label="确认新密码" prop="checkPassword">
              <el-input v-model="ruleForm.checkPassword" type="password" placeholder="请再次输入新密码" show-password prefix-icon="el-icon-circle-check"></el-input>
            </el-form-item>

            <el-form-item style="margin-top: 30px;">
              <el-button type="primary" class="submit-btn" @click="submitForm('ruleForm')" :loading="loading">确认修改</el-button>
              <el-button class="reset-btn" @click="resetForm('ruleForm')">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { alterPassword } from '@/api/user'
import { mapGetters } from 'vuex'

export default {
  name: 'Password',
  data() {
    var validatePass = (rule, value, callback) => {
      if (value === '') callback(new Error('请输入密码'));
      else {
        if (this.ruleForm.checkPassword !== '') this.$refs.ruleForm.validateField('checkPassword');
        callback();
      }
    };
    var validatePass2 = (rule, value, callback) => {
      if (value === '') callback(new Error('请再次输入密码'));
      else if (value !== this.ruleForm.newPassword) callback(new Error('两次输入密码不一致!'));
      else callback();
    };
    return {
      loading: false,
      ruleForm: { oldPassword: '', newPassword: '', checkPassword: '' },
      rules: {
        oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
        newPassword: [{ validator: validatePass, trigger: 'blur' }],
        checkPassword: [{ validator: validatePass2, trigger: 'blur' }]
      }
    };
  },
  computed: { ...mapGetters(['id', 'username']) },
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.loading = true
          // 调用后端接口
          alterPassword({
            username: this.username, // 你的后端可能需要 username
            userid: this.id,         // 或者 userid
            oldPassword: this.ruleForm.oldPassword,
            newPassword: this.ruleForm.newPassword
          }).then(res => {
            this.loading = false
            // 兼容后端返回
            if(res.code === 0 || res === 1 || res.code === 200) {
              this.$message.success('密码修改成功，请重新登录')
              this.$store.dispatch('user/logout')
              this.$router.push(`/login?redirect=${this.$route.fullPath}`)
            } else {
              this.$message.error(res.msg || '修改失败，原密码可能错误')
            }
          }).catch(() => { this.loading = false })
        }
      });
    },
    resetForm(formName) { this.$refs[formName].resetFields(); }
  }
}
</script>

<style lang="scss" scoped>
.security-container {
  min-height: calc(100vh - 50px);
  background-color: #f7f9fc;
  display: flex; justify-content: center; align-items: center; padding: 20px;
}

.security-card {
  width: 900px; height: 500px; border-radius: 24px; border: none; overflow: hidden;
  box-shadow: 0 20px 40px rgba(0,0,0,0.08);
  ::v-deep .el-card__body { padding: 0; height: 100%; }
}

.card-content { display: flex; height: 100%; }

.visual-side {
  width: 40%; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff; padding: 40px; display: flex; flex-direction: column; justify-content: center; align-items: center; text-align: center;

  .icon-box {
    width: 80px; height: 80px; background: rgba(255,255,255,0.2); border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 36px; margin-bottom: 20px;
  }
  h2 { margin: 0 0 10px 0; }
  p { opacity: 0.9; font-size: 14px; margin-bottom: 40px; }
  .security-tips {
    text-align: left; list-style: none; padding: 0; margin: 0;
    li { margin-bottom: 12px; font-size: 13px; opacity: 0.8; i { margin-right: 8px; } }
  }
}

.form-side {
  width: 60%; padding: 40px 60px; display: flex; flex-direction: column; justify-content: center;
  h3 { color: #303133; font-size: 24px; margin-bottom: 30px; }

  .submit-btn { width: 100%; height: 44px; border-radius: 22px; font-size: 16px; margin-bottom: 15px; background: linear-gradient(90deg, #667eea 0%, #764ba2 100%); border: none; }
  .reset-btn { width: 100%; border-radius: 22px; border: none; background: #f4f4f5; &:hover { background: #e9e9eb; } }

  ::v-deep .el-input__inner { border-radius: 20px; background: #f8f9fa; border: 1px solid #e4e7ed; &:focus { background: #fff; border-color: #764ba2; } }
}
</style>
