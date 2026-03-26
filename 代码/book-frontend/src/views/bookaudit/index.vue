<template>
  <div class="app-container">
    <div class="admin-view">
      <el-card class="filter-container" shadow="never">
        <div class="filter-header">
          <span class="filter-title">🛡️ 图书发布审核管理</span>
          <div class="filter-actions">
            <el-input v-model="listQuery.bookname" placeholder="搜索书名..." style="width: 200px;" class="filter-item round-input" clearable @keyup.enter.native="handleFilter" />
            <el-button class="filter-item round-btn" type="primary" icon="el-icon-search" @click="handleFilter">精确检索</el-button>
          </div>
        </div>
      </el-card>

      <el-tabs v-model="activeTab" @tab-click="handleTabClick" class="custom-tabs" style="margin-top: 20px;">
        <el-tab-pane label="⏳ 待审核" name="pending">
          <!-- 待审核列表 -->
        </el-tab-pane>
        <el-tab-pane label="✅ 已通过 (上架)" name="approved">
          <!-- 已通过列表 -->
        </el-tab-pane>
        <el-tab-pane label="❌ 已驳回" name="rejected">
          <!-- 已驳回列表 -->
        </el-tab-pane>
      </el-tabs>

      <el-card class="table-card" shadow="hover" style="margin-top: 10px;">
        <el-table :data="list" v-loading="listLoading" style="width: 100%" :header-cell-style="{background:'#f5f7fa', color:'#606266'}">
          <el-table-column label="ID" prop="bookid" width="70" align="center" />
          <el-table-column label="封面" width="100" align="center">
            <template slot-scope="{row}">
              <el-image style="width: 50px; height: 70px; border-radius: 4px;" :src="row.bookimg ? 'http://localhost:9111/BookManager' + row.bookimg : ''" fit="cover">
                <div slot="error" class="image-slot"><i class="el-icon-picture-outline"></i></div>
              </el-image>
            </template>
          </el-table-column>
          <el-table-column label="图书名称" min-width="150" prop="bookname">
            <template slot-scope="{row}"><span style="font-weight: bold; color: #303133;">{{ row.bookname }}</span></template>
          </el-table-column>
          <el-table-column label="作者" prop="bookauthor" width="120" />
          <el-table-column label="分类" prop="booktypename" width="120" align="center">
            <template slot-scope="{row}"><el-tag size="small" type="info">{{ row.booktypename || '默认' }}</el-tag></template>
          </el-table-column>
          <el-table-column label="剩余/总数" width="100" align="center">
            <template slot-scope="{row}">{{ row.inventory }} / {{ row.bookcount }}</template>
          </el-table-column>
          <el-table-column label="发布简介与交接说明" min-width="200" show-overflow-tooltip>
            <template slot-scope="{row}">
              <div><strong style="color: #909399;">简介: </strong>{{ row.bookdesc || '无' }}</div>
              <div style="margin-top: 4px;"><strong style="color: #E6A23C;">交接: </strong>{{ row.contactinfo || '无' }}</div>
            </template>
          </el-table-column>
          
          <el-table-column label="当前状态" width="100" align="center">
            <template slot-scope="{row}">
              <el-tag v-if="row.auditstatus === 0" type="warning" size="small">待审核</el-tag>
              <el-tag v-else-if="row.auditstatus === 1" type="success" size="small">已上架</el-tag>
              <el-tag v-else-if="row.auditstatus === 2" type="danger" size="small">已驳回</el-tag>
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="180" align="center" fixed="right">
            <template slot-scope="{row}">
              <template v-if="row.auditstatus === 0">
                <el-button size="mini" type="success" round @click="handleAudit(row, 1)">予以通过</el-button>
                <el-button size="mini" type="danger" plain round @click="handleAudit(row, 2)">驳回</el-button>
              </template>
              <span v-else style="color: #C0C4CC; font-size: 13px;">已处理完毕</span>
            </template>
          </el-table-column>
        </el-table>
        <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />
      </el-card>
    </div>
  </div>
</template>

<script>
import { queryBookInfosByPage, auditBook } from '@/api/bookinfo'
import Pagination from '@/components/Pagination'

export default {
  name: 'Bookaudit',
  components: { Pagination },
  data() {
    return {
      list: [],
      total: 0,
      listLoading: true,
      activeTab: 'pending',
      listQuery: {
        page: 1,
        limit: 10,
        bookname: undefined,
        auditstatus: 0 // 默认为待审核 0
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    handleTabClick(tab) {
      if (tab.name === 'pending') {
        this.listQuery.auditstatus = 0;
      } else if (tab.name === 'approved') {
        this.listQuery.auditstatus = 1;
      } else if (tab.name === 'rejected') {
        this.listQuery.auditstatus = 2;
      }
      this.listQuery.page = 1;
      this.getList();
    },
    getList() {
      this.listLoading = true;
      queryBookInfosByPage(this.listQuery).then(response => {
        let dataList = [];
        if (response.data && Array.isArray(response.data)) {
          dataList = response.data;
          this.total = response.count || response.total || response.data.length;
        } else if (response.rows) {
          dataList = response.rows;
          this.total = response.total;
        }
        this.list = dataList;
        this.listLoading = false;
      }).catch(() => {
        this.listLoading = false;
      })
    },
    handleFilter() {
      this.listQuery.page = 1;
      this.getList();
    },
    handleAudit(row, status) {
      if (status === 2) {
        this.$prompt('请输入驳回该图书发布的理由：', '驳回确认', {
          confirmButtonText: '确定驳回',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(({ value }) => {
          auditBook(row.bookid, status, value || '包含违规或不适宜内容').then(res => {
            if (res === 1) {
              this.$message.success('已驳回该图书发布。');
              this.getList();
            }
          })
        }).catch(() => {});
      } else {
        this.$confirm('确定审核通过并允许该图书上架吗？', '提示', {
          confirmButtonText: '确定通过',
          cancelButtonText: '取消',
          type: 'success'
        }).then(() => {
          auditBook(row.bookid, status, '').then(res => {
            if (res === 1) {
              this.$message.success('审核通过！系统已通知发布者。');
              this.getList();
            }
          })
        }).catch(() => {});
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.app-container { padding: 24px; background-color: #f7f9fc; min-height: calc(100vh - 50px); }
.admin-view {
  .filter-container {
    border-radius: 16px; border: none;
    .filter-header { display: flex; justify-content: space-between; align-items: center; .filter-title { font-size: 18px; font-weight: bold; color: #303133; } .filter-actions { display: flex; gap: 10px; } }
  }
  .table-card { border-radius: 16px; border: none; }
  .round-input ::v-deep .el-input__inner { border-radius: 20px; }
  .round-btn { border-radius: 20px; }
}

::v-deep .el-button {
  height: 34px !important;
  border-radius: 17px !important;
  padding: 0 15px !important;
  display: inline-flex !important;
  justify-content: center !important;
  align-items: center !important;
  font-size: 13px !important;
  font-weight: 500 !important;
}

.custom-tabs ::v-deep .el-tabs__item { font-size: 16px; height: 50px; line-height: 50px; }
.image-slot { display: flex; justify-content: center; align-items: center; width: 100%; height: 100%; background: #f5f7fa; color: #909399; font-size: 20px; }
</style>
