<template>
  <div class="app-container">
    <el-card class="filter-wrapper" shadow="never">
      <div class="filter-left">
        <span class="page-title">🏷️ 漂流分类索引</span>
        <span class="page-desc">为每一本书找到它的归属</span>
      </div>
      <div class="filter-right">
        <el-input v-model="listQuery.booktypename" placeholder="搜索分类..." class="round-input" style="width: 200px;" @keyup.enter.native="handleFilter">
          <i slot="prefix" class="el-input__icon el-icon-search"></i>
        </el-input>
        <el-button type="primary" class="round-btn" icon="el-icon-search" @click="handleFilter">搜索</el-button>
        <el-button type="success" class="round-btn" icon="el-icon-plus" @click="handleCreate">新建分类</el-button>
      </div>
    </el-card>

    <div class="category-grid" v-loading="listLoading">
      <div v-for="(item, index) in list" :key="item.booktypeid" class="category-card" :style="{ '--delay': index * 0.05 + 's' }">
        <div class="card-inner">
          <div class="icon-wrapper" :class="'gradient-' + (index % 6)">
            <i class="el-icon-collection-tag"></i>
          </div>
          <div class="info-wrapper">
            <h3 class="category-name">{{ item.booktypename }}</h3>
            <p class="category-desc">分类 ID: {{ item.booktypeid }}</p>
          </div>
          <div class="action-wrapper">
            <el-button type="primary" circle icon="el-icon-edit" size="small" @click="handleUpdate(item)"></el-button>
            <el-button type="danger" circle icon="el-icon-delete" size="small" @click="handleDelete(item)"></el-button>
          </div>
        </div>
      </div>
    </div>

    <div class="pagination-container" v-if="total > 0">
      <el-pagination background @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="listQuery.page" :page-sizes="[12, 24, 36]" :page-size="listQuery.limit" layout="total, prev, pager, next" :total="total"></el-pagination>
    </div>

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" width="380px" custom-class="glass-dialog" center>
      <el-form ref="dataForm" :rules="rules" :model="temp" label-position="top">
        <el-form-item label="分类名称" prop="booktypename">
          <el-input v-model="temp.booktypename" placeholder="例如：科幻小说、考研资料..." />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false" round>取消</el-button>
        <el-button type="primary" @click="dialogStatus==='create'?createData():updateData()" round>保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { queryBookTypesByPage, addBookType, updateBookType, deleteBookType } from '@/api/booktype'

export default {
  name: 'BookType',
  data() {
    return {
      list: [],
      total: 0,
      listLoading: true,
      listQuery: { page: 1, limit: 12, booktypename: undefined },
      temp: { booktypeid: undefined, booktypename: '' },
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: { update: '编辑分类', create: '新建分类' },
      rules: { booktypename: [{ required: true, message: '分类名不能为空', trigger: 'blur' }] }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.listLoading = true
      // 注意：如果你的后端只有 queryBookTypes (不带分页)，请改用 queryBookTypes()
      // 这里假设你按照规范有分页接口，如果没有，请自行替换为 queryBookTypes().then(res => this.list = res)
      queryBookTypesByPage(this.listQuery).then(res => {
        if(res.data) { this.list = res.data; this.total = res.count || res.total || 0; }
        else if(res.rows) { this.list = res.rows; this.total = res.total; }
        else { this.list = res; this.total = res.length; } // 兼容不分页
        this.listLoading = false
      }).catch(() => { this.listLoading = false })
    },
    handleFilter() { this.listQuery.page = 1; this.getList() },
    handleSizeChange(val) { this.listQuery.limit = val; this.getList() },
    handleCurrentChange(val) { this.listQuery.page = val; this.getList() },
    resetTemp() { this.temp = { booktypeid: undefined, booktypename: '' } },
    handleCreate() {
      this.resetTemp(); this.dialogStatus = 'create'; this.dialogFormVisible = true;
      this.$nextTick(() => { this.$refs['dataForm'].clearValidate() })
    },
    createData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          addBookType(this.temp).then(() => {
            this.list.unshift(this.temp); this.dialogFormVisible = false;
            this.$message.success('创建成功'); this.getList()
          })
        }
      })
    },
    handleUpdate(row) {
      this.temp = Object.assign({}, row); this.dialogStatus = 'update'; this.dialogFormVisible = true;
      this.$nextTick(() => { this.$refs['dataForm'].clearValidate() })
    },
    updateData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          updateBookType(this.temp).then(() => {
            this.dialogFormVisible = false; this.$message.success('更新成功'); this.getList()
          })
        }
      })
    },
    handleDelete(row) {
      this.$confirm('删除分类可能导致该分类下的书籍数据异常，确定删除吗？', '慎重操作', {
        type: 'warning', customClass: 'glass-message-box'
      }).then(() => {
        deleteBookType({ booktypeid: row.booktypeid }).then(() => {
          this.$message.success('删除成功'); this.getList()
        })
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.app-container { padding: 24px; background-color: #f7f9fc; min-height: calc(100vh - 50px); }
.filter-wrapper {
  margin-bottom: 24px; border-radius: 16px; border: none;
  ::v-deep .el-card__body { display: flex; justify-content: space-between; align-items: center; padding: 18px 24px; }
  .filter-left { display: flex; flex-direction: column; }
  .page-title { font-size: 20px; font-weight: bold; color: #303133; }
  .page-desc { font-size: 13px; color: #909399; margin-top: 4px; }
  .filter-right { display: flex; gap: 10px; }
  .round-input ::v-deep .el-input__inner { border-radius: 20px; }
  .round-btn { border-radius: 20px; }
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 20px;
}

.category-card {
  background: #fff; border-radius: 16px; position: relative; overflow: hidden;
  box-shadow: 0 4px 12px rgba(0,0,0,0.03); transition: all 0.3s;
  animation: fadeUp 0.5s ease forwards; animation-delay: var(--delay); opacity: 0;

  &:hover {
    transform: translateY(-5px); box-shadow: 0 8px 24px rgba(0,0,0,0.08);
    .action-wrapper { opacity: 1; transform: translateY(0); }
  }

  .card-inner {
    padding: 24px; display: flex; align-items: center; gap: 20px;
  }

  .icon-wrapper {
    width: 56px; height: 56px; border-radius: 14px; display: flex; align-items: center; justify-content: center; font-size: 24px; color: #fff; flex-shrink: 0;
    &.gradient-0 { background: linear-gradient(135deg, #FF9A9E 0%, #FECFEF 100%); }
    &.gradient-1 { background: linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%); }
    &.gradient-2 { background: linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%); }
    &.gradient-3 { background: linear-gradient(135deg, #cfd9df 0%, #e2ebf0 100%); color: #666; }
    &.gradient-4 { background: linear-gradient(135deg, #e0c3fc 0%, #8ec5fc 100%); }
    &.gradient-5 { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
  }

  .info-wrapper {
    .category-name { margin: 0 0 6px 0; font-size: 18px; color: #303133; }
    .category-desc { margin: 0; font-size: 12px; color: #909399; }
  }

  .action-wrapper {
    position: absolute; right: 15px; top: 0; bottom: 0; display: flex; flex-direction: column; justify-content: center; gap: 8px;
    opacity: 0; transform: translateX(10px); transition: all 0.3s;
    background: linear-gradient(90deg, transparent, #fff 20%); padding-left: 10px;
  }
}

.pagination-container { text-align: center; margin-top: 30px; }
@keyframes fadeUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
</style>
