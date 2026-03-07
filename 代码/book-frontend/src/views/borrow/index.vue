<template>
  <div class="app-container">

    <div v-if="checkPermission(['admin'])" class="admin-view">
      <el-card class="filter-container" shadow="never">
        <div class="filter-header">
          <span class="filter-title">📚 全局借阅管理</span>
          <div class="filter-actions">
            <el-input v-model="listQuery.username" placeholder="搜索借阅人..." style="width: 200px;" class="filter-item round-input" @keyup.enter.native="handleFilter" prefix-icon="el-icon-user" clearable />
            <el-input v-model="listQuery.bookname" placeholder="搜索书名..." style="width: 200px;" class="filter-item round-input" @keyup.enter.native="handleFilter" prefix-icon="el-icon-reading" clearable />
            <el-button class="filter-item round-btn" type="primary" icon="el-icon-search" @click="handleFilter">查找记录</el-button>
          </div>
        </div>
      </el-card>

      <el-card class="table-card" shadow="hover">
        <el-table :data="list" v-loading="listLoading" style="width: 100%" :header-cell-style="{background:'#f5f7fa', color:'#606266'}">
          <el-table-column label="ID" prop="borrowid" width="80" align="center" />
          <el-table-column label="借阅书籍" min-width="180">
            <template slot-scope="{row}">
              <div class="book-info-cell">
                <i class="el-icon-notebook-2" style="color: #409eff; margin-right: 8px;"></i>
                <span class="book-name-text">{{ row.bookname }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="借阅人" width="150" align="center">
            <template slot-scope="{row}">
              <el-tag size="small" type="info" effect="plain">{{ row.username }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="借阅时间" prop="borrowtime" width="180" align="center" sortable />
          <el-table-column label="归还状态" width="120" align="center">
            <template slot-scope="{row}">
              <el-tag :type="row.returntime ? 'success' : 'warning'" effect="dark" size="small" style="border-radius: 12px;">
                {{ row.returntime ? '已归还' : '漂流中' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="归还时间" width="180" align="center">
            <template slot-scope="{row}">
              <span v-if="row.returntime">{{ row.returntime }}</span>
              <span v-else class="text-muted">--</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" align="center" fixed="right">
            <template slot-scope="{row}">
              <el-button v-if="!row.returntime" size="mini" type="danger" round plain @click="handleReturn(row)">强制归还</el-button>
              <el-button v-else size="mini" type="text" disabled>已结束</el-button>
            </template>
          </el-table-column>
        </el-table>

        <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />
      </el-card>
    </div>


    <div v-else class="user-view">
      <div class="timeline-header">
        <h2>我的漂流足迹</h2>
        <p>每一本书的相遇，都是一次奇妙的旅程</p>
      </div>

      <div class="timeline-container" v-loading="listLoading">
        <el-timeline v-if="list.length > 0">
          <el-timeline-item v-for="(item, index) in list" :key="item.borrowid" :timestamp="item.borrowtime" placement="top" :color="item.returntime ? '#e4e7ed' : '#409eff'" :icon="item.returntime ? 'el-icon-check' : 'el-icon-time'" size="large">

            <el-card class="footprint-card" :class="{'is-active': !item.returntime}" shadow="hover">
              <div class="card-content">
                <div class="book-cover-placeholder">
                  <i class="el-icon-reading"></i>
                </div>

                <div class="book-details">
                  <h3 class="book-title">{{ item.bookname }}</h3>
                  <div class="status-line">
                    <span v-if="!item.returntime" class="status-tag active">
                      <i class="el-icon-loading"></i> 正在漂流中
                    </span>
                    <span v-else class="status-tag returned">
                      <i class="el-icon-circle-check"></i> 旅程已结束
                    </span>
                  </div>
                  <p class="borrow-meta" v-if="item.returntime">归还于: {{ item.returntime }}</p>
                  <p class="borrow-meta" v-else>已陪伴你 {{ calculateDays(item.borrowtime) }} 天</p>
                </div>

                <div class="book-action">
                  <el-button v-if="!item.returntime" type="primary" round size="medium" @click="handleReturn(item)" class="return-btn">
                    归还 / 传递
                  </el-button>
                  <el-button v-else type="text" disabled class="archived-btn">已归档</el-button>
                </div>
              </div>
            </el-card>

          </el-timeline-item>
        </el-timeline>

        <el-empty v-else description="你还没有开启漂流之旅，去书库看看吧！">
          <el-button type="primary" round @click="$router.push('/bookmanage/bookinfo')">去探索好书</el-button>
        </el-empty>
      </div>
    </div>

  </div>
</template>

<script>
import { queryBorrowsByPage, returnBook } from '@/api/borrow'
import Pagination from '@/components/Pagination'
import checkPermission from '@/utils/permission' // 权限判断工具
import { mapGetters } from 'vuex'

export default {
  name: 'Borrow',
  components: { Pagination },
  data() {
    return {
      list: [],
      total: 0,
      listLoading: true,
      // 查询参数
      listQuery: {
        page: 1,
        limit: 10,
        username: undefined, // 管理员搜索用
        bookname: undefined
      }
    }
  },
  computed: {
    ...mapGetters(['roles', 'id'])
  },
  created() {
    this.getList()
  },
  methods: {
    checkPermission, // 暴露给模板使用

    getList() {
      this.listLoading = true

      // 构造请求参数
      const params = { ...this.listQuery }

      // 【关键逻辑】如果是普通读者，强制加上 userid 过滤，只看自己的
      if (!this.checkPermission(['admin'])) {
        params.userid = this.id // 从 Vuex 获取当前用户 ID
      }

      queryBorrowsByPage(params).then(response => {
        // 兼容不同的后端返回结构
        if (response.data && Array.isArray(response.data)) {
          this.list = response.data
          this.total = response.total || response.data.length
        } else if (response.rows) {
          this.list = response.rows
          this.total = response.total
        } else {
          // 假如后端直接返回 List
          this.list = response.data || []
          this.total = response.count || 0
        }
        this.listLoading = false
      }).catch(() => {
        this.listLoading = false
      })
    },

    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },

    handleReturn(row) {
      const isMyBook = !this.checkPermission(['admin']);
      const confirmText = isMyBook
        ? `读完了吗？确定要归还《${row.bookname}》让它继续漂流吗？`
        : `确定要强制归还用户 ${row.username} 的《${row.bookname}》吗？`;

      this.$confirm(confirmText, '归还确认', {
        confirmButtonText: '确认归还',
        cancelButtonText: '取消',
        type: 'warning',
        customClass: 'glass-message-box'
      }).then(() => {
        returnBook({ borrowid: row.borrowid }).then(res => {
          // 兼容后端返回：可能直接返回 int 1，或者 {code: 0}
          if (res === 1 || res.code === 0 || res.code === 200) {
            this.$message({
              message: isMyBook ? '归还成功！期待你的下一次漂流。' : '强制归还成功',
              type: 'success'
            })
            this.getList()
          } else {
            this.$message.error(res.msg || '操作失败')
          }
        })
      }).catch(() => {})
    },

    // 计算借阅天数
    calculateDays(borrowTime) {
      if (!borrowTime) return 0;
      const start = new Date(borrowTime).getTime();
      const now = new Date().getTime();
      const diff = now - start;
      return Math.floor(diff / (24 * 3600 * 1000));
    }
  }
}
</script>

<style lang="scss" scoped>
.app-container {
  padding: 24px;
  background-color: #f7f9fc;
  min-height: calc(100vh - 50px);
}

/* ================= 管理员视图样式 ================= */
.admin-view {
  .filter-container {
    border-radius: 16px;
    border: none;
    margin-bottom: 20px;
    .filter-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      .filter-title { font-size: 18px; font-weight: bold; color: #303133; }
      .filter-actions { display: flex; gap: 10px; }
    }
  }
  .table-card {
    border-radius: 16px;
    border: none;
    .book-name-text { font-weight: 500; color: #303133; }
    .text-muted { color: #c0c4cc; }
  }
  .round-input ::v-deep .el-input__inner { border-radius: 20px; }
  .round-btn { border-radius: 20px; }
}

/* ================= 用户视图样式 (时间轴) ================= */
.user-view {
  max-width: 900px;
  margin: 0 auto;

  .timeline-header {
    text-align: center;
    margin-bottom: 40px;
    h2 { font-size: 28px; color: #303133; margin-bottom: 10px; }
    p { color: #909399; font-size: 16px; letter-spacing: 1px; }
  }

  .footprint-card {
    border-radius: 16px;
    border: none;
    transition: transform 0.3s, box-shadow 0.3s;
    background: #fff;

    // 漂流中的卡片高亮一点
    &.is-active {
      border-left: 5px solid #409eff;
      background: #fdfdff;
    }

    &:hover {
      transform: translateY(-3px);
      box-shadow: 0 8px 20px rgba(0,0,0,0.08);
    }

    .card-content {
      display: flex;
      align-items: center;
      padding: 10px;
    }

    .book-cover-placeholder {
      width: 60px;
      height: 80px;
      background: linear-gradient(135deg, #a1c4fd 0%, #c2e9fb 100%);
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 20px;
      color: #fff;
      font-size: 24px;
      box-shadow: 0 4px 10px rgba(161, 196, 253, 0.4);
    }

    .book-details {
      flex: 1;
      .book-title { margin: 0 0 8px 0; font-size: 18px; color: #303133; }
      .status-line { margin-bottom: 8px; }
      .status-tag {
        font-size: 13px;
        display: inline-flex;
        align-items: center;
        gap: 4px;
        &.active { color: #409eff; font-weight: 600; }
        &.returned { color: #909399; }
      }
      .borrow-meta { margin: 0; font-size: 12px; color: #c0c4cc; }
    }

    .book-action {
      margin-left: 20px;
      .return-btn {
        box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
        transition: all 0.3s;
        &:hover { transform: scale(1.05); }
      }
      .archived-btn { color: #dcdfe6; }
    }
  }
}
</style>
