<template>
  <div class="app-container">
    <el-card class="filter-wrapper" shadow="never">
      <div class="filter-left">
        <span class="page-title">👥 书友档案库</span>
      </div>
      <div class="filter-right">
        <el-select v-model="listQuery.department" placeholder="按系部筛选" clearable class="round-input" style="width: 180px;" @change="handleFilter">
          <el-option v-for="dept in departments" :key="dept" :label="dept" :value="dept" />
        </el-select>

        <el-input v-model="listQuery.username" placeholder="搜索姓名或学号..." style="width: 200px;" class="round-input" @keyup.enter.native="handleFilter">
          <i slot="prefix" class="el-input__icon el-icon-search"></i>
        </el-input>
        <el-button type="primary" class="round-btn" icon="el-icon-search" @click="handleFilter">搜索</el-button>
        <el-button type="success" class="round-btn" icon="el-icon-plus" @click="handleCreate">录入新书友</el-button>
      </div>
    </el-card>

    <el-row :gutter="24" v-loading="listLoading">
      <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="user in list" :key="user.userid" style="margin-bottom: 24px;">
        <el-card class="user-card" shadow="hover" :class="{'is-admin': user.isadmin === 1}">

          <div class="card-header-bg">
            <div class="role-badge">
              <i :class="user.isadmin === 1 ? 'el-icon-s-custom' : 'el-icon-user'"></i>
              {{ user.isadmin === 1 ? '管理员' : '漂流书友' }}
            </div>
          </div>

          <div class="card-avatar">
            <el-avatar :size="70" :src="user.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'"></el-avatar>
          </div>

          <div class="card-body">
            <h3 class="user-name">{{ user.username }}</h3>
            <p class="user-id">学号: {{ user.studentid || user.username }}</p>

            <div class="user-status">
              <div style="margin-bottom: 8px;">
                <el-tooltip content="低于 60 分将被禁止借书" placement="top">
                  <el-tag size="small" effect="dark" :type="getCreditType(user.creditScore)" style="border-radius: 10px;">
                    <i class="el-icon-s-data"></i> 信用分: {{ user.creditScore != null ? user.creditScore : 100 }}
                  </el-tag>
                </el-tooltip>
              </div>

              <div style="margin-bottom: 8px;">
                <el-tag v-if="user.department" size="small" effect="light" :type="getDeptType(user.department)" style="border-radius: 10px;">
                  <i class="el-icon-office-building"></i> {{ user.department }}
                </el-tag>
                <el-tag v-else size="small" effect="plain" type="info" style="border-radius: 10px;">
                  <i class="el-icon-collection"></i> 暂无系部
                </el-tag>
              </div>

              <el-tag v-if="user.status === 1" type="success" size="small" effect="dark" class="status-tag" style="margin-right: 5px;">状态正常</el-tag>
              <el-tag v-else type="danger" size="small" effect="dark" class="status-tag" style="margin-right: 5px;">已禁用</el-tag>

              <el-tag size="small" effect="plain" :type="user.gender === 1 ? '' : (user.gender === 0 ? 'danger' : 'info')" class="status-tag">
                <i :class="user.gender === 1 ? 'el-icon-male' : (user.gender === 0 ? 'el-icon-female' : 'el-icon-lock')"></i>
                {{ user.gender === 1 ? '男生' : (user.gender === 0 ? '女生' : '保密') }}
              </el-tag>
            </div>

            <div class="card-actions">
              <el-button type="text" icon="el-icon-edit" @click="handleUpdate(user)">编辑</el-button>
              <el-button type="text" icon="el-icon-key" @click="handleResetPwd(user)">重置</el-button>
              <el-button type="text" icon="el-icon-delete" class="delete-btn" @click="handleDelete(user)">删除</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <div class="pagination-container">
      <el-pagination background @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="listQuery.page" :page-sizes="[8, 16, 32]" :page-size="listQuery.limit" layout="total, sizes, prev, pager, next, jumper" :total="total"></el-pagination>
    </div>

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" width="480px" custom-class="glass-dialog" center>
      <el-form ref="dataForm" :rules="rules" :model="temp" label-position="left" label-width="80px" style="padding: 0 20px;">
        <el-form-item label="姓名" prop="username">
          <el-input v-model="temp.username" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="学号" prop="studentid">
          <el-input v-model="temp.studentid" placeholder="作为登录账号" :disabled="dialogStatus==='update'" />
        </el-form-item>

        <el-form-item label="系部" prop="department">
          <el-select v-model="temp.department" placeholder="请选择所属系部" style="width: 100%;" clearable>
            <el-option v-for="dept in departments" :key="dept" :label="dept" :value="dept" />
          </el-select>
        </el-form-item>

        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="temp.gender">
            <el-radio :label="1">男生</el-radio>
            <el-radio :label="0">女生</el-radio>
            <el-radio :label="2">保密</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="信用分" v-if="dialogStatus==='update'">
          <el-slider v-model="temp.creditScore" :min="0" :max="100" show-input></el-slider>
          <div style="font-size: 12px; color: #E6A23C; margin-top: -10px; line-height: 1.2;">* 逾期被扣除信用的用户，若线下说明情况，管理员可在此为其恢复分数 (默认 100，低于 60 封禁借阅)。</div>
        </el-form-item>

        <el-form-item label="身份" prop="isadmin">
          <el-radio-group v-model="temp.isadmin">
            <el-radio :label="0">普通书友</el-radio>
            <el-radio :label="1">管理员</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="temp.status" :active-value="1" :inactive-value="2" active-text="正常" inactive-text="禁用" active-color="#13ce66" inactive-color="#ff4949"></el-switch>
        </el-form-item>

        <div v-if="dialogStatus==='create'" class="tips">
          <i class="el-icon-info"></i> 默认初始密码为: 123456
        </div>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false" round>取消</el-button>
        <el-button type="primary" @click="dialogStatus==='create'?createData():updateData()" round>确认提交</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import { queryUsersByPage, addUser, updateUser, deleteUser } from '@/api/user'

export default {
  name: 'UserManage',
  data() {
    return {
      departments: ['机械工程系', '电气与控制工程系', '大数据与智能工程系', '土木工程系', '地球科学与工程系', '矿业工程系', '管理科学与工程系', '经济与管理系', '艺术与设计科学系', '材料科学与工程系'],
      list: [], total: 0, listLoading: true,
      listQuery: { page: 1, limit: 8, username: undefined, department: undefined },
      temp: { userid: undefined, username: '', studentid: '', isadmin: 0, status: 1, userpassword: '', gender: 2, department: '', creditScore: 100 },
      dialogFormVisible: false, dialogStatus: '',
      textMap: { update: '编辑书友信息', create: '录入新书友' },
      rules: { username: [{ required: true, message: '姓名不能为空', trigger: 'blur' }], studentid: [{ required: true, message: '学号不能为空', trigger: 'blur' }] }
    }
  },
  created() { this.getList() },
  methods: {
    // 动态渲染信用分标签颜色
    getCreditType(score) {
      if (score == null || score >= 90) return 'success'; // 优秀
      if (score >= 60) return 'warning'; // 及格警告
      return 'danger'; // 差评拉黑
    },
    getDeptType(deptName) {
      if (!deptName) return 'info';
      if (deptName.includes('大数据') || deptName.includes('电气')) return '';
      if (deptName.includes('机械') || deptName.includes('土木') || deptName.includes('矿业') || deptName.includes('材料')) return 'warning';
      if (deptName.includes('管理') || deptName.includes('经济') || deptName.includes('地球')) return 'success';
      if (deptName.includes('艺术')) return 'danger';
      return 'info';
    },

    getList() {
      this.listLoading = true
      queryUsersByPage(this.listQuery).then(res => {
        if(res.data) { this.list = res.data; this.total = res.count || res.total || 0 }
        else if(res.rows) { this.list = res.rows; this.total = res.total }
        else { this.list = res; this.total = res.length }
        this.listLoading = false
      })
    },
    handleFilter() { this.listQuery.page = 1; this.getList() },
    handleSizeChange(val) { this.listQuery.limit = val; this.getList() },
    handleCurrentChange(val) { this.listQuery.page = val; this.getList() },
    resetTemp() {
      this.temp = { userid: undefined, username: '', studentid: '', isadmin: 0, status: 1, userpassword: '', gender: 2, department: '', creditScore: 100 }
    },
    handleCreate() {
      this.resetTemp()
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.$nextTick(() => { this.$refs['dataForm'].clearValidate() })
    },
    createData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          this.temp.userpassword = '123456'
          addUser(this.temp).then(() => {
            this.list.unshift(this.temp)
            this.dialogFormVisible = false
            this.$message.success('添加成功，初始密码 123456')
            this.getList()
          })
        }
      })
    },
    handleUpdate(row) {
      this.temp = Object.assign({}, row)
      // 处理数据库没有记录时默认显示100分
      if(this.temp.creditScore == null) this.temp.creditScore = 100;
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.$nextTick(() => { this.$refs['dataForm'].clearValidate() })
    },
    updateData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          const tempData = Object.assign({}, this.temp)
          updateUser(tempData).then(() => {
            this.dialogFormVisible = false
            this.$message.success('更新成功，信用分及信息已同步修改')
            this.getList()
          })
        }
      })
    },
    handleDelete(row) {
      this.$confirm('确定要删除该用户吗？此操作无法撤销。', '警告', { confirmButtonText: '确定删除', cancelButtonText: '取消', type: 'warning', customClass: 'glass-message-box' }).then(() => {
        deleteUser({ userid: row.userid }).then(() => { this.$message.success('删除成功'); this.getList() })
      })
    },
    handleResetPwd(row) {
      this.$confirm(`确定要重置 ${row.username} 的密码为 123456 吗？`, '重置密码', { type: 'warning' }).then(() => {
        updateUser({ userid: row.userid, userpassword: '123456' }).then(() => { this.$message.success('密码已重置') })
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.app-container { padding: 24px; background-color: #f7f9fc; min-height: calc(100vh - 50px); }
.filter-wrapper { margin-bottom: 24px; border-radius: 16px; border: none; display: flex; align-items: center; ::v-deep .el-card__body { width: 100%; display: flex; justify-content: space-between; align-items: center; padding: 15px 24px; } .page-title { font-size: 20px; font-weight: bold; color: #303133; } .filter-right { display: flex; gap: 12px; } .round-input ::v-deep .el-input__inner { border-radius: 20px; } .round-btn { border-radius: 20px; } }
.user-card { border-radius: 16px; border: none; overflow: visible; position: relative; margin-top: 30px; transition: all 0.3s; &:hover { transform: translateY(-5px); box-shadow: 0 12px 24px rgba(0,0,0,0.08); } &.is-admin { .card-header-bg { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); } } .card-header-bg { height: 80px; background: linear-gradient(135deg, #a1c4fd 0%, #c2e9fb 100%); border-radius: 16px 16px 0 0; position: relative; .role-badge { position: absolute; top: 10px; right: 10px; background: rgba(255,255,255,0.2); backdrop-filter: blur(5px); color: #fff; padding: 4px 10px; border-radius: 12px; font-size: 12px; font-weight: bold; } } .card-avatar { position: absolute; top: 45px; left: 50%; transform: translateX(-50%); border: 4px solid #fff; border-radius: 50%; box-shadow: 0 4px 12px rgba(0,0,0,0.1); } .card-body { padding: 50px 20px 20px 20px; text-align: center; .user-name { font-size: 18px; color: #303133; margin: 0 0 5px 0; } .user-id { font-size: 13px; color: #909399; margin: 0 0 10px 0; } .status-tag { margin-bottom: 20px; } .card-actions { display: flex; justify-content: space-around; align-items: center; border-top: 1px dashed #ebeef5; padding-top: 15px; .el-button { font-size: 14px !important; font-weight: 500; i { font-size: 15px !important; margin-right: 2px; } } .el-button:nth-child(2) { margin: 0 4px; } .delete-btn { color: #f56c6c; &:hover { color: #f78989; } } } } }
.tips { font-size: 12px; color: #909399; margin-top: -10px; margin-bottom: 20px; margin-left: 80px; }
.pagination-container { text-align: center; margin-top: 30px; }
</style>
