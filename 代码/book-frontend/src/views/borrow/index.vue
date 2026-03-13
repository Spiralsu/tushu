<template>
  <div class="app-container">
    <div v-if="checkPermission(['admin'])" class="admin-view">
      <el-card class="filter-container" shadow="never">
        <div class="filter-header">
          <span class="filter-title">📚 全局借阅与交接管理</span>
          <div class="filter-actions">
            <el-input v-model="listQuery.username" placeholder="搜索借阅人..." style="width: 150px;" class="filter-item round-input" clearable />
            <el-input v-model="listQuery.bookname" placeholder="搜索书名..." style="width: 150px;" class="filter-item round-input" clearable />
            <el-button class="filter-item round-btn" type="primary" icon="el-icon-search" @click="handleFilter">查找记录</el-button>
          </div>
        </div>
      </el-card>

      <el-card class="table-card" shadow="hover">
        <el-table :data="list" v-loading="listLoading" style="width: 100%" :header-cell-style="{background:'#f5f7fa', color:'#606266'}">
          <el-table-column label="ID" prop="borrowid" width="60" align="center" />
          <el-table-column label="借阅书籍" min-width="160">
            <template slot-scope="{row}"><div class="book-info-cell"><i class="el-icon-notebook-2" style="color: #409eff; margin-right: 8px;"></i><span class="book-name-text">{{ row.bookname }}</span></div></template>
          </el-table-column>
          <el-table-column label="接收人" width="120" align="center"><template slot-scope="{row}"><el-tag size="small" effect="light" style="font-weight: bold;">{{ row.username }}</el-tag></template></el-table-column>
          <el-table-column label="信用评级" width="110" align="center">
            <template slot-scope="{row}"><el-tag size="small" effect="dark" :type="getCreditType(row.creditScore)" style="border-radius: 10px;"><i class="el-icon-s-data"></i> {{ row.creditScore != null ? row.creditScore : 100 }} 分</el-tag></template>
          </el-table-column>
          <el-table-column label="学号" width="140" align="center"><template slot-scope="{row}"><span style="color: #606266; font-family: monospace; font-size: 14px;">{{ row.studentid || row.studentId || '暂无数据' }}</span></template></el-table-column>
          <el-table-column label="申请理由" prop="borrowreason" min-width="150" show-overflow-tooltip />
          <el-table-column label="申请时间" prop="applytime" width="160" align="center" sortable />
          <el-table-column label="流转状态" width="100" align="center">
            <template slot-scope="{row}">
              <el-tag v-if="row.state === 0" type="info" size="small">待审核</el-tag>
              <el-tag v-else-if="row.state === 1" type="primary" size="small">漂流中</el-tag>
              <el-tag v-else-if="row.state === 2" type="success" size="small">已归还</el-tag>
              <el-tag v-else-if="row.state === 3" type="danger" size="small">已驳回</el-tag>
              <el-tag v-else-if="row.state === 4" type="warning" size="small">⏳ 待交接</el-tag>
              <el-tag v-else-if="row.state === 5" type="info" effect="plain" size="small" style="color:#909399;">已撤销</el-tag>
              <el-tag v-else-if="row.state === 6" type="info" effect="dark" size="small" style="background-color: #909399; border-color: #909399;">已报损</el-tag>
              <el-tag v-else-if="row.state === 7" type="info" effect="plain" size="small" style="color:#C0C4CC;">已失效</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="160" align="center" fixed="right">
            <template slot-scope="{row}">
              <template v-if="row.state === 0">
                <el-button size="mini" type="success" round @click="handleAudit(row, 1)">同意</el-button>
                <el-button size="mini" type="danger" plain round @click="handleAudit(row, 3)">驳回</el-button>
              </template>
              <el-button v-else-if="row.state === 4" size="mini" type="danger" plain round @click="handleCancel(row)">强制撤销</el-button>
              <el-button v-else-if="row.state === 1" size="mini" type="danger" round plain @click="handleReturn(row)">强制归还</el-button>
              <el-button v-else size="mini" type="text" disabled>{{ row.state === 5 ? '已撤销' : (row.state === 6 ? '已报损' : (row.state === 7 ? '已失效' : '已结束')) }}</el-button>
            </template>
          </el-table-column>
        </el-table>
        <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />
      </el-card>
    </div>

    <div v-else class="user-view">
      <div class="timeline-header"><h2>我的漂流档案</h2><p>让知识传递，让信任流转</p></div>
      <el-tabs v-model="activeTab" @tab-click="handleTabClick" class="custom-tabs">
        <el-tab-pane label="我借入的 (漂流足迹)" name="borrow">
          <div class="timeline-container" v-loading="listLoading">
            <el-timeline v-if="list.length > 0">
              <el-timeline-item v-for="(item, index) in list" :key="item.borrowid" :timestamp="item.applytime" placement="top" :color="item.returntime ? '#e4e7ed' : (item.state === 4 ? '#E6A23C' : (item.state === 6 ? '#909399' : '#409eff'))">
                <el-card class="footprint-card" :class="{'is-active': item.state === 1, 'is-pending': item.state === 4}" shadow="hover">
                  <div class="card-content">
                    <div class="book-details">
                      <h3 class="book-title">📖 {{ item.bookname }}</h3>
                      <div class="status-line">
                        <span v-if="item.state === 0" class="status-tag">审核中... <span style="color:#F56C6C; margin-left:5px;">(超3天未处理将自动取消)</span></span>
                        <span v-else-if="item.state === 4" class="status-tag pending" style="color: #E6A23C;">审核已通过，请联系对方拿书 <span style="color:#F56C6C; margin-left:5px;">(超3天不交接将自动释放)</span></span>

                        <span v-else-if="item.state === 1" class="status-tag active">正在阅读 {{ getRemainingTimeText(item) }}</span>
                        <span v-else-if="item.state === 3" class="status-tag" style="color: #F56C6C;">被拒原因：{{ item.returnmsg }}</span>
                        <span v-else-if="item.state === 5" class="status-tag" style="color: #C0C4CC;">已撤销：{{ item.returnmsg }}</span>
                        <span v-else-if="item.state === 6" class="status-tag" style="color: #909399; text-decoration: line-through;">书籍已报损，终止漂流</span>
                        <span v-else-if="item.state === 7" class="status-tag" style="color: #C0C4CC;">长时间未处理，申请已自动失效</span>
                      </div>
                      <div v-if="item.state === 4" class="secret-code-box"><span>向发布者出示此暗号提书:</span><strong class="code-text">{{ item.secretCode }}</strong></div>
                      <div v-if="item.state === 4" style="margin-top: 10px;"><el-button size="mini" type="primary" plain icon="el-icon-chat-line-square" @click="handleNudge(item)">无法前往？协商新地点</el-button></div>
                    </div>

                    <div class="book-action">
                      <template v-if="item.state === 1 && !item.returntime">
                        <el-button type="primary" round size="medium" @click="handleReturn(item)" style="margin-bottom: 8px; width: 100%;">归还 / 传递</el-button>
                        <el-button type="info" plain round size="mini" @click="handleReportLoss(item)" style="width: 100%; margin:0;">登记报损 / 遗失</el-button>
                      </template>
                      <el-button v-if="item.state === 0" size="small" type="info" plain round @click="handleCancel(item)">取消申请</el-button>
                      <el-button v-if="item.state === 4" size="small" type="danger" plain round @click="handleCancel(item)">联系不上？放弃交接</el-button>
                    </div>
                  </div>
                </el-card>
              </el-timeline-item>
            </el-timeline>
            <el-empty v-else description="暂无借入记录"></el-empty>
          </div>
        </el-tab-pane>

        <el-tab-pane label="我借出的 (我的发布)" name="lend">
          <div class="timeline-container" v-loading="listLoading">
            <el-timeline v-if="list.length > 0">
              <el-timeline-item v-for="(item, index) in list" :key="item.borrowid" :timestamp="item.applytime" placement="top" :color="item.state === 0 ? '#909399' : (item.state === 4 ? '#F56C6C' : '#67C23A')">
                <el-card class="footprint-card" :class="{'is-pending': item.state === 4 || item.state === 0}" shadow="hover" style="border-left-color: #F56C6C">
                  <div class="card-content">
                    <div class="book-details">
                      <h3 class="book-title">📘 《{{ item.bookname }}》 被 <span style="color:#409EFF">{{ item.username }}</span> 申请了
                        <el-tooltip :content="item.creditScore < 60 ? '信用极低，建议拒绝' : '信用良好'" placement="top">
                          <el-tag size="mini" effect="dark" :type="getCreditType(item.creditScore)" style="margin-left: 8px; border-radius: 10px; vertical-align: middle;">信用评级: {{ item.creditScore != null ? item.creditScore : 100 }}</el-tag>
                        </el-tooltip>
                      </h3>
                      <p class="borrow-meta">TA 的留言：{{ item.borrowreason }}</p>
                      <div class="status-line" style="margin-top: 8px;">
                        <span v-if="item.state === 0" class="status-tag" style="color: #F56C6C; font-weight: bold;"><i class="el-icon-bell"></i> 等待您审批 <span style="font-weight:normal; margin-left:5px;">(超3天未处理将自动取消)</span></span>
                        <span v-else-if="item.state === 4" class="status-tag pending" style="color: #E6A23C;"><i class="el-icon-time"></i> 已同意，等待出示暗号 <span style="color:#F56C6C; margin-left:5px;">(超3天不交接将自动释放)</span></span>
                        <span v-else-if="item.state === 1" class="status-tag" style="color: #67C23A;"><i class="el-icon-reading"></i> 对方正在阅读中 {{ getRemainingTimeText(item) }}</span>
                        <span v-else-if="item.state === 3" class="status-tag" style="color: #909399;">已被您驳回</span>
                        <span v-else-if="item.state === 5" class="status-tag" style="color: #F56C6C;">对方已撤销：{{ item.returnmsg }}</span>
                        <span v-else-if="item.state === 7" class="status-tag" style="color: #C0C4CC;">长时间未交接，已自动释放并退回库存</span>
                      </div>
                    </div>
                    <div class="book-action">
                      <template v-if="item.state === 0">
                        <el-button size="small" type="success" round @click="handleAudit(item, 1)" style="box-shadow: 0 4px 10px rgba(103, 194, 58, 0.3);">同意借出</el-button>
                        <el-button size="small" type="info" plain round @click="handleAudit(item, 3)" style="margin-top: 5px;">委婉拒绝</el-button>
                      </template>
                      <el-button v-else-if="item.state === 4" size="medium" type="danger" round @click="openHandoverDialog(item)" style="box-shadow: 0 4px 10px rgba(245, 108, 108, 0.3);"><i class="el-icon-scan"></i> 核销提书暗号</el-button>
                      <el-button v-else-if="item.state === 1" size="small" type="warning" plain round @click="handleUrge(item)" icon="el-icon-message-solid">一键催还</el-button>
                      <el-button v-else type="text" disabled>已结束 / 已失效</el-button>
                    </div>
                  </div>
                </el-card>
              </el-timeline-item>
            </el-timeline>
            <el-empty v-else description="你发布的书籍暂无流转记录"></el-empty>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <el-dialog :visible.sync="dialogVisible" width="420px" custom-class="glass-dialog" :show-close="false">
      <div class="dialog-header-custom"><div class="icon-ring"><i class="el-icon-lock"></i></div><h3 style="margin: 15px 0 5px 0; color: #303133;">面对面交接验证</h3><p style="color: #909399; font-size: 13px; margin: 0;">请输入借阅者出示的 6 位数字暗号，确认交接</p></div>
      <div style="padding: 20px 30px;"><el-input v-model="inputSecretCode" placeholder="··· ···" maxlength="6" class="secret-input" /></div>
      <div slot="footer" style="text-align: center; padding-bottom: 10px;"><el-button round @click="dialogVisible = false" style="margin-right: 15px;">取消</el-button><el-button type="primary" round @click="submitHandover" :loading="submitLoading">核 销 暗 号</el-button></div>
    </el-dialog>
    <el-dialog title="📚 读完并传递 (成为新一任持书人)" :visible.sync="returnDialogVisible" width="550px" custom-class="glass-dialog">
      <el-form :model="returnForm" label-position="top">
        <el-form-item label="您的漂流感悟 (所有人可见的漂流足迹)"><el-input type="textarea" :rows="3" v-model="returnForm.returnMsg" placeholder="这本书给你带来了什么启发？留下你的足迹吧..." /></el-form-item>
        <el-form-item label="您的交接说明 (非常重要！绝对保密)"><el-input type="textarea" :rows="2" v-model="returnForm.contactInfo" placeholder="例如：我是男生，住在5栋302，或者加我微信 xxxx" /><div style="font-size: 12px; color: #909399; margin-top: 5px;"><i class="el-icon-info"></i> 这本书现在由您保管！请留下您的联系方式，等待下一位有缘人申请。</div></el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer"><el-button @click="returnDialogVisible = false" round>暂不传递</el-button><el-button type="primary" @click="submitReturn" round>确认传递，等待下一位</el-button></div>
    </el-dialog>
  </div>
</template>

<script>
import { queryBorrowsByPage, returnBook } from '@/api/borrow'
import request from '@/utils/request'
import Pagination from '@/components/Pagination'
import checkPermission from '@/utils/permission'
import { mapGetters } from 'vuex'

export default {
  name: 'Borrow',
  components: { Pagination },
  data() {
    return {
      list: [], total: 0, listLoading: true, activeTab: 'borrow',
      listQuery: { page: 1, limit: 10, username: undefined, bookname: undefined },
      dialogVisible: false, inputSecretCode: '', currentRowId: null, submitLoading: false,
      returnDialogVisible: false, returnForm: { borrowId: null, bookId: null, returnMsg: '', contactInfo: '' }
    }
  },
  computed: { ...mapGetters(['roles', 'id']) },
  created() { this.getList() },
  methods: {
    checkPermission,
    getCreditType(score) { if (score == null || score >= 90) return 'success'; if (score >= 60) return 'warning'; return 'danger'; },

    // 【修复】：计算剩余天数及逾期提醒
    getRemainingTimeText(item) {
      if(!item.borrowtime) return '';
      // 致命Bug修复：必须用 != null 过滤掉数据库里的 null 值
      const days = (item.borrowDays != null) ? item.borrowDays : 30;

      if (days === 0) return `(⚠️ 测试0天模式，已触发逾期！)`;

      const dueTime = new Date(item.borrowtime).getTime() + days * 24 * 3600 * 1000;
      const now = new Date().getTime();
      const diff = dueTime - now;

      if (diff > 0) {
        return `(⏳ 剩 ${Math.ceil(diff / (1000*3600*24))} 天)`;
      } else {
        return `(⚠️ 已逾期 ${Math.ceil(-diff / (1000*3600*24))} 天！系统每日扣分中)`;
      }
    },

    handleTabClick() { this.listQuery.page = 1; this.getList(); },
    getList() {
      this.listLoading = true; const params = { ...this.listQuery };
      if (!this.checkPermission(['admin'])) { if(this.activeTab === 'borrow') params.userId = this.id; else params.uploaderId = this.id; }
      queryBorrowsByPage(params).then(response => {
        let dataList = [];
        if (response.data && Array.isArray(response.data)) { dataList = response.data; this.total = response.total || response.data.length; }
        else if (response.rows) { dataList = response.rows; this.total = response.total; }
        else { dataList = response.data || []; this.total = response.count || 0; }
        this.list = dataList; this.listLoading = false;
      }).catch(() => { this.listLoading = false })
    },
    handleFilter() { this.listQuery.page = 1; this.getList() },
    handleAudit(row, targetState) {
      if (targetState === 3) {
        this.$prompt('请输入委婉拒绝的理由：', '驳回申请', { confirmButtonText: '驳回', cancelButtonText: '取消', type: 'warning' }).then(({ value }) => {
          request({ url: '/borrow/audit', method: 'post', params: { borrowId: row.borrowid, state: targetState, feedback: value || '发布者暂时无法出借' } }).then(res => {
            if (res.code === 0 || res === 1) { this.$message.success('已驳回。'); this.getList(); } else { this.$message.error(res.msg); }
          });
        });
      } else {
        this.$confirm(`确认同意借出该请求吗？`, '提示', { type: 'success' }).then(() => {
          request({ url: '/borrow/audit', method: 'post', params: { borrowId: row.borrowid, state: targetState } }).then(res => {
            if (res.code === 0 || res === 1) { this.$message.success('审批通过！已成功通知对方。'); this.getList(); } else { this.$message.error(res.msg); }
          });
        });
      }
    },
    openHandoverDialog(row) { this.inputSecretCode = ''; this.currentRowId = row.borrowid; this.dialogVisible = true; },
    submitHandover() {
      if (this.inputSecretCode.length !== 6) return this.$message.warning("请输入完整的 6 位交接暗号！");
      this.submitLoading = true;
      request({ url: '/borrow/verifyCode', method: 'post', params: { borrowId: this.currentRowId, secretCode: this.inputSecretCode } }).then(res => {
        this.submitLoading = false;
        if (res.code === 0 || res === 1) { this.$message.success("🎉 暗号核销正确！交接成功！您的信用分已获奖励！"); this.dialogVisible = false; this.getList(); }
        else { this.$message.error(res.msg || "❌ 拦截失败！" + res.msg); } // 把后端的风控阻断提示直接弹出来
      }).catch(() => { this.submitLoading = false; });
    },
    handleReturn(row) { this.returnForm = { borrowId: row.borrowid, bookId: row.bookid, returnMsg: '', contactInfo: '' }; this.returnDialogVisible = true; },
    submitReturn() {
      if(!this.returnForm.returnMsg) return this.$message.warning("写点感悟吧！");
      if(!this.returnForm.contactInfo) return this.$message.warning("请留下交接说明！");
      request({ url: '/borrow/returnBook', method: 'post', params: { borrowId: this.returnForm.borrowId, bookId: this.returnForm.bookId, returnMsg: this.returnForm.returnMsg, contactInfo: this.returnForm.contactInfo } }).then(res => {
        if(res.code === 0 || res === 1) { this.$message.success("传递成功！"); this.returnDialogVisible = false; this.getList(); } else { this.$message.error(res.msg); }
      });
    },
    handleCancel(row) {
      this.$prompt('请输入撤销/取消的原因：', '撤销申请', { confirmButtonText: '确认', cancelButtonText: '暂不撤销', type: 'warning' }).then(({ value }) => {
        request({ url: '/borrow/cancel', method: 'post', params: { borrowId: row.borrowid, reason: value || '个人原因取消' } }).then(res => {
          if (res.code === 0 || res === 1) { this.$message.success('已成功撤销！'); this.getList(); }
        });
      });
    },
    handleNudge(row) {
      this.$prompt('留下建议碰面地点：', '请求协商交接', { confirmButtonText: '发送', cancelButtonText: '取消' }).then(({ value }) => {
        if (!value) return this.$message.warning("留言不能为空");
        request({ url: '/borrow/nudge', method: 'post', params: { borrowId: row.borrowid, message: value } }).then(res => {
          if (res.code === 0 || res === 1) { this.$message.success('发送成功！'); }
        });
      });
    },
    handleReportLoss(row) {
      this.$prompt('填写报损原因：', '登记报损', { confirmButtonText: '确认报损', cancelButtonText: '取消', type: 'error' }).then(({ value }) => {
        if (!value) return this.$message.warning("请填写报损原因！");
        request({ url: '/borrow/reportLoss', method: 'post', params: { borrowId: row.borrowid, reason: value } }).then(res => {
          if(res.code === 0 || res === 1) { this.$message.success("报损登记成功。"); this.getList(); }
        });
      });
    },
    handleUrge(row) {
      this.$confirm('确定要催促该读者尽快阅读并传递吗？', '一键催还', { type: 'warning' }).then(() => {
        request({ url: '/borrow/urge', method: 'post', params: { borrowId: row.borrowid } }).then(res => {
          if(res.code === 0 || res === 1) {
            this.$message.success("催促通知已成功发送！");
          } else {
            // 【修复】：把后端“还没到期，请耐心等待”的提示弹出来
            this.$message.error(res.msg || "操作失败");
          }
        });
      });
    }
  }
}
</script>

<style lang="scss" scoped>
/* 保持之前的所有漂亮样式完全一致，不用改动 */
.app-container { padding: 24px; background-color: #f7f9fc; min-height: calc(100vh - 50px); }
.admin-view { .filter-container { border-radius: 16px; border: none; margin-bottom: 20px; .filter-header { display: flex; justify-content: space-between; align-items: center; .filter-title { font-size: 18px; font-weight: bold; color: #303133; } .filter-actions { display: flex; gap: 10px; } } } .table-card { border-radius: 16px; border: none; .book-name-text { font-weight: 500; color: #303133; } .text-muted { color: #c0c4cc; } } .round-input ::v-deep .el-input__inner { border-radius: 20px; } .round-btn { border-radius: 20px; } }
.user-view { max-width: 900px; margin: 0 auto; .timeline-header { text-align: center; margin-bottom: 40px; h2 { font-size: 28px; color: #303133; margin-bottom: 10px; } p { color: #909399; font-size: 16px; letter-spacing: 1px; } } .footprint-card { border-radius: 16px; border: none; transition: transform 0.3s, box-shadow 0.3s; background: #fff; display: flex; flex-direction: column; &.is-active { border-left: 5px solid #409eff; background: #fdfdff; } &.is-pending { border-left: 5px solid #E6A23C; background: #fff8eb; } &:hover { transform: translateY(-3px); box-shadow: 0 8px 20px rgba(0,0,0,0.08); } .card-content { display: flex; align-items: center; padding: 10px; } .book-details { flex: 1; .book-title { margin: 0 0 8px 0; font-size: 18px; color: #303133; } .status-line { margin-bottom: 8px; } .status-tag { font-size: 13px; display: inline-flex; align-items: center; gap: 4px; &.active { color: #409eff; font-weight: 600; } &.returned { color: #909399; } } .borrow-meta { margin: 0; font-size: 12px; color: #c0c4cc; } } .book-action { margin-left: 20px; display: flex; flex-direction: column; justify-content: center; gap: 6px; } } }
.secret-code-box { display: inline-block; background-color: #fdf6ec; border: 1px dashed #f5dac0; padding: 5px 12px; border-radius: 8px; margin-top: 5px; margin-bottom: 5px; font-size: 13px; color: #E6A23C; .code-text { font-size: 18px; font-family: 'Courier New', Courier, monospace; color: #F56C6C; margin-left: 8px; letter-spacing: 2px; } }
::v-deep .glass-dialog { border-radius: 20px !important; overflow: hidden; box-shadow: 0 20px 50px rgba(0, 0, 0, 0.1) !important; }
.dialog-header-custom { text-align: center; padding-top: 10px; .icon-ring { width: 60px; height: 60px; margin: 0 auto; background: #ecf5ff; border-radius: 50%; display: flex; align-items: center; justify-content: center; color: #409EFF; font-size: 28px; box-shadow: 0 0 0 8px rgba(64,158,255, 0.05); } }
.secret-input ::v-deep .el-input__inner { text-align: center; font-size: 32px; letter-spacing: 12px; font-family: monospace; font-weight: bold; height: 60px; border-radius: 12px; border: 2px solid #e4e7ed; background-color: #fafbfc; transition: all 0.3s; }
.secret-input ::v-deep .el-input__inner:focus { border-color: #409EFF; box-shadow: 0 0 0 4px rgba(64, 158, 255, 0.1); background-color: #fff; }
.custom-tabs ::v-deep .el-tabs__item { font-size: 16px; height: 50px; line-height: 50px; }
</style>
