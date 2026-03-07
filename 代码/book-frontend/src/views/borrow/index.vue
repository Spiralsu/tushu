<template>
  <div class="app-container">

    <div v-if="checkPermission(['admin'])" class="admin-view">
      <el-card class="filter-container" shadow="never">
        <div class="filter-header">
          <span class="filter-title">📚 全局借阅与交接管理</span>
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
          <el-table-column label="接收人" width="150" align="center">
            <template slot-scope="{row}">
              <el-tag size="small" type="info" effect="plain">{{ row.username }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="申请时间" prop="borrowtime" width="180" align="center" sortable />

          <el-table-column label="流转状态" width="120" align="center">
            <template slot-scope="{row}">
              <el-tag v-if="row.status === 1" type="warning" effect="dark" size="small" style="border-radius: 12px; background-color: #E6A23C; border-color: #E6A23C;">
                ⏳ 待交接
              </el-tag>
              <el-tag v-else :type="row.returntime ? 'success' : 'primary'" effect="dark" size="small" style="border-radius: 12px;">
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

          <el-table-column label="操作" width="180" align="center" fixed="right">
            <template slot-scope="{row}">
              <el-button v-if="row.status === 1" size="mini" type="warning" round @click="openHandoverDialog(row)" style="box-shadow: 0 4px 10px rgba(230, 162, 60, 0.3);">
                <i class="el-icon-key"></i> 验证暗号
              </el-button>

              <el-button v-else-if="!row.returntime" size="mini" type="danger" round plain @click="handleReturn(row)">强制归还</el-button>
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
          <el-timeline-item v-for="(item, index) in list" :key="item.borrowid" :timestamp="item.borrowtime" placement="top" :color="item.returntime ? '#e4e7ed' : (item.status === 1 ? '#E6A23C' : '#409eff')" :icon="item.returntime ? 'el-icon-check' : (item.status === 1 ? 'el-icon-key' : 'el-icon-time')" size="large">

            <el-card class="footprint-card" :class="{'is-active': !item.returntime, 'is-pending': item.status === 1}" shadow="hover">
              <div class="card-content">
                <div class="book-cover-placeholder" :style="item.status === 1 ? 'background: linear-gradient(135deg, #fdfbfb 0%, #ebedee 100%); color: #E6A23C;' : ''">
                  <i class="el-icon-reading"></i>
                </div>

                <div class="book-details">
                  <h3 class="book-title">{{ item.bookname }}</h3>
                  <div class="status-line">

                    <span v-if="item.status === 1" class="status-tag pending" style="color: #E6A23C;">
                      <i class="el-icon-mobile"></i> 审核已通过，待线下交接
                    </span>

                    <span v-else-if="!item.returntime" class="status-tag active">
                      <i class="el-icon-loading"></i> 正在漂流中
                    </span>
                    <span v-else class="status-tag returned">
                      <i class="el-icon-circle-check"></i> 旅程已结束
                    </span>
                  </div>

                  <div v-if="item.status === 1" class="secret-code-box">
                    <span>向交出人出示暗号:</span>
                    <strong class="code-text">{{ item.secretCode || '正在生成...' }}</strong>
                  </div>

                  <p class="borrow-meta" v-if="item.returntime">归还于: {{ item.returntime }}</p>
                  <p class="borrow-meta" v-else-if="item.status !== 1">已陪伴你 {{ calculateDays(item.borrowtime) }} 天</p>

                  <p class="borrow-meta" style="color:#67c23a; margin-top:5px; font-style:italic;" v-if="item.returnmsg">
                    <i class="el-icon-chat-dot-round"></i> "{{ item.returnmsg }}"
                  </p>
                </div>

                <div class="book-action">
                  <el-button v-if="!item.returntime && item.status !== 1" type="primary" round size="medium" @click="handleReturn(item)" class="return-btn">
                    归还 / 传递
                  </el-button>
                  <el-button v-else-if="item.status === 1" type="warning" plain round disabled class="archived-btn">等待交接中</el-button>
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

    <el-dialog :visible.sync="dialogVisible" width="420px" custom-class="glass-dialog" :show-close="false">
      <div class="dialog-header-custom">
        <div class="icon-ring">
          <i class="el-icon-lock"></i>
        </div>
        <h3 style="margin: 15px 0 5px 0; color: #303133;">面对面交接验证</h3>
        <p style="color: #909399; font-size: 13px; margin: 0;">请输入对方出示的 6 位数字暗号</p>
      </div>

      <div style="padding: 20px 30px;">
        <el-input
          v-model="inputSecretCode"
          placeholder="··· ···"
          maxlength="6"
          class="secret-input"
        ></el-input>
      </div>

      <div slot="footer" style="text-align: center; padding-bottom: 10px;">
        <el-button round @click="dialogVisible = false" style="margin-right: 15px;">稍后再试</el-button>
        <el-button type="primary" round @click="submitHandover" :loading="submitLoading" style="box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);">
          确 认 提 交
        </el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import { queryBorrowsByPage, returnBook } from '@/api/borrow'
import Pagination from '@/components/Pagination'
import checkPermission from '@/utils/permission'
import { mapGetters } from 'vuex'

export default {
  name: 'Borrow',
  components: { Pagination },
  data() {
    return {
      list: [],
      total: 0,
      listLoading: true,
      listQuery: { page: 1, limit: 10, username: undefined, bookname: undefined },

      // 暗号交接功能所需数据
      dialogVisible: false,
      inputSecretCode: '',
      currentRowId: null,
      submitLoading: false
    }
  },
  computed: { ...mapGetters(['roles', 'id']) },
  created() { this.getList() },
  methods: {
    checkPermission,

    getList() {
      this.listLoading = true
      const params = { ...this.listQuery }
      if (!this.checkPermission(['admin'])) { params.userid = this.id }

      queryBorrowsByPage(params).then(response => {
        let dataList = [];
        if (response.data && Array.isArray(response.data)) { dataList = response.data; this.total = response.total || response.data.length; }
        else if (response.rows) { dataList = response.rows; this.total = response.total; }
        else { dataList = response.data || []; this.total = response.count || 0; }

        // 【本地前台假数据植入测试】：为了让你看到效果，强制把第一条未归还的数据变为“待交接”状态
        if (dataList.length > 0) {
          const firstActive = dataList.find(item => !item.returntime);
          if (firstActive) {
            firstActive.status = 1; // 1代表待交接
            firstActive.secretCode = '884821'; // 模拟后台生成的暗号
          }
        }

        this.list = dataList;
        this.listLoading = false;
      }).catch(() => { this.listLoading = false })
    },

    handleFilter() { this.listQuery.page = 1; this.getList() },

    // 【新增】打开交接弹窗
    openHandoverDialog(row) {
      this.inputSecretCode = '';
      this.currentRowId = row.borrowid;
      this.dialogVisible = true;
    },

    // 【新增】提交暗号交接
    submitHandover() {
      if (this.inputSecretCode.length !== 6) {
        this.$message.warning("请输入完整的 6 位交接暗号！");
        return;
      }
      this.submitLoading = true;

      // 模拟后端请求：前端查找当前行数据进行模拟验证
      setTimeout(() => {
        const row = this.list.find(item => item.borrowid === this.currentRowId);
        if (row && row.secretCode === this.inputSecretCode) {
          this.$message.success("🎉 暗号正确！交接成功，图书正式进入漂流中！");
          row.status = 2; // 变成正式漂流中
          this.dialogVisible = false;
        } else {
          this.$message.error("❌ 暗号错误！请向接收人确认最新暗号。");
        }
        this.submitLoading = false;
      }, 800);
    },

    handleReturn(row) {
      const isMyBook = !this.checkPermission(['admin']);
      if (isMyBook) {
        this.$prompt(`读完了《${row.bookname}》吗？留下你对下一位读者的寄语吧：`, '归还与传递', {
          confirmButtonText: '留下寄语并归还',
          cancelButtonText: '默默归还',
          inputPlaceholder: '例如：这本书让我受益匪浅，希望你也能喜欢...',
          customClass: 'glass-message-box'
        }).then(({ value }) => {
          this.submitReturn(row, value || '前一位读者默默地归还了这本书，未留只言片语。');
        }).catch(() => {
          this.submitReturn(row, '前一位读者默默地归还了这本书，未留只言片语。');
        });
      } else {
        this.$confirm(`确定要强制归还用户 ${row.username} 的《${row.bookname}》吗？`, '强制归还', {
          confirmButtonText: '确认归还',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.submitReturn(row, '管理员强制结束了本次漂流。');
        }).catch(() => {});
      }
    },

    submitReturn(row, msg) {
      returnBook(row.borrowid, row.bookid, msg).then(res => {
        if (res === 1 || res.code === 0 || res.code === 200) {
          this.$message.success('归还成功！期待你的下一次漂流。');
          this.getList();
        } else {
          this.$message.error('操作失败');
        }
      });
    },

    calculateDays(borrowTime) {
      if (!borrowTime) return 0;
      const start = new Date(borrowTime).getTime();
      const now = new Date().getTime();
      return Math.floor((now - start) / (24 * 3600 * 1000));
    }
  }
}
</script>

<style lang="scss" scoped>
/* =========== 完全保留你原有的精美 CSS =========== */
.app-container { padding: 24px; background-color: #f7f9fc; min-height: calc(100vh - 50px); }
.admin-view {
  .filter-container {
    border-radius: 16px; border: none; margin-bottom: 20px;
    .filter-header { display: flex; justify-content: space-between; align-items: center;
      .filter-title { font-size: 18px; font-weight: bold; color: #303133; }
      .filter-actions { display: flex; gap: 10px; }
    }
  }
  .table-card { border-radius: 16px; border: none; .book-name-text { font-weight: 500; color: #303133; } .text-muted { color: #c0c4cc; } }
  .round-input ::v-deep .el-input__inner { border-radius: 20px; }
  .round-btn { border-radius: 20px; }
}

.user-view {
  max-width: 900px; margin: 0 auto;
  .timeline-header { text-align: center; margin-bottom: 40px; h2 { font-size: 28px; color: #303133; margin-bottom: 10px; } p { color: #909399; font-size: 16px; letter-spacing: 1px; } }
  .footprint-card {
    border-radius: 16px; border: none; transition: transform 0.3s, box-shadow 0.3s; background: #fff;
    &.is-active { border-left: 5px solid #409eff; background: #fdfdff; }
    &.is-pending { border-left: 5px solid #E6A23C; background: #fff8eb; } /* 为待交接单独设计的卡片侧边栏颜色 */
    &:hover { transform: translateY(-3px); box-shadow: 0 8px 20px rgba(0,0,0,0.08); }
    .card-content { display: flex; align-items: center; padding: 10px; }
    .book-cover-placeholder {
      width: 60px; height: 80px; background: linear-gradient(135deg, #a1c4fd 0%, #c2e9fb 100%); border-radius: 8px; display: flex; align-items: center; justify-content: center; margin-right: 20px; color: #fff; font-size: 24px; box-shadow: 0 4px 10px rgba(161, 196, 253, 0.4);
    }
    .book-details {
      flex: 1; .book-title { margin: 0 0 8px 0; font-size: 18px; color: #303133; }
      .status-line { margin-bottom: 8px; }
      .status-tag { font-size: 13px; display: inline-flex; align-items: center; gap: 4px; &.active { color: #409eff; font-weight: 600; } &.returned { color: #909399; } }
      .borrow-meta { margin: 0; font-size: 12px; color: #c0c4cc; }
    }
    .book-action { margin-left: 20px; .return-btn { box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3); transition: all 0.3s; &:hover { transform: scale(1.05); } } .archived-btn { color: #dcdfe6; } }
  }
}

/* =========== 新增的暗号专属美化样式 =========== */
.secret-code-box {
  display: inline-block;
  background-color: #fdf6ec;
  border: 1px dashed #f5dac0;
  padding: 5px 12px;
  border-radius: 8px;
  margin-top: 5px;
  margin-bottom: 5px;
  font-size: 13px;
  color: #E6A23C;
  .code-text {
    font-size: 18px;
    font-family: 'Courier New', Courier, monospace;
    color: #F56C6C;
    margin-left: 8px;
    letter-spacing: 2px;
  }
}

/* 拟物化验证弹窗的 CSS 魔法 */
::v-deep .glass-dialog {
  border-radius: 20px !important;
  overflow: hidden;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.1) !important;
}
.dialog-header-custom {
  text-align: center;
  padding-top: 10px;
  .icon-ring {
    width: 60px; height: 60px;
    margin: 0 auto;
    background: #ecf5ff;
    border-radius: 50%;
    display: flex; align-items: center; justify-content: center;
    color: #409EFF; font-size: 28px;
    box-shadow: 0 0 0 8px rgba(64,158,255, 0.05);
  }
}
.secret-input ::v-deep .el-input__inner {
  text-align: center;
  font-size: 32px;
  letter-spacing: 12px;
  font-family: monospace;
  font-weight: bold;
  height: 60px;
  border-radius: 12px;
  border: 2px solid #e4e7ed;
  background-color: #fafbfc;
  transition: all 0.3s;
}
.secret-input ::v-deep .el-input__inner:focus {
  border-color: #409EFF;
  box-shadow: 0 0 0 4px rgba(64, 158, 255, 0.1);
  background-color: #fff;
}
</style>
