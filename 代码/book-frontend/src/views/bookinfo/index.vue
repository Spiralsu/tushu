<template>
  <div class="app-container">
    <el-card class="filter-container" shadow="never" style="margin-bottom: 35px; border: none;">
      <el-form :inline="true" :model="queryParam" size="medium" class="header-form-flex">
        <el-form-item><el-input v-model="queryParam.bookname" placeholder="书名检索..." prefix-icon="el-icon-search" clearable /></el-form-item>
        <el-form-item><el-input v-model="queryParam.bookauthor" placeholder="作者..." prefix-icon="el-icon-user" clearable /></el-form-item>
        <el-form-item>
          <el-select v-model="queryParam.booktypeid" placeholder="图书分类" clearable>
            <el-option v-for="item in typeData" :key="item.booktypeid" :label="item.booktypename" :value="item.booktypeid"/>
          </el-select>
        </el-form-item>
        <el-form-item class="search-btn-group">
          <el-button type="primary" icon="el-icon-search" @click="handleFilter" class="modern-btn">探索好书</el-button>
          <el-button type="info" plain icon="el-icon-refresh-left" @click="resetQuery" class="modern-btn">显示全部</el-button>
          <el-button type="warning" plain icon="el-icon-user" @click="handleMyPublished" class="modern-btn">我发布的</el-button>
          <el-button type="success" icon="el-icon-plus" @click="handleCreate" class="modern-btn">发布旧书</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-row :gutter="24" v-loading="tableLoading" class="book-list-container">
      <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="item in tableData" :key="item.bookid" style="margin-bottom: 24px;">
        <el-card class="book-card-item" shadow="hover">
          <div class="book-image-box">
            <el-image :src="item.bookimg ? 'http://localhost:9111/BookManager' + item.bookimg : ''" fit="cover" class="book-main-img">
              <div slot="error" class="error-img-slot"><i class="el-icon-collection"></i></div>
            </el-image>
            <div class="status-tag" :class="{'out': item.inventory <= 0}">
              {{ item.inventory > 0 ? '剩余 ' + item.inventory : '漂流中' }}
            </div>
          </div>
          <div class="book-content-box">
            <h3 class="b-title" :title="item.bookname">{{ item.bookname }}</h3>
            <div style="margin: 5px 0;" v-if="item.isSameSex === 1">
              <el-tag type="success" size="mini" effect="dark" style="border-radius: 10px;"><i class="el-icon-location-information"></i> 同性宿区 极速交接</el-tag>
            </div>
            <p class="b-author">{{ item.bookauthor }}</p>
            <el-tooltip :content="item.bookdesc || '这位书友很神秘，没有留下寄语...'" placement="top" effect="light">
              <p class="b-desc"><i class="el-icon-chat-line-round"></i> {{ item.bookdesc || '暂无寄语...' }}</p>
            </el-tooltip>
            <el-button type="primary" round class="action-btn-custom" @click="openBorrowDialog(item)" :disabled="item.inventory <= 0" style="width: 100%; margin-top: 5px;">
              <i class="el-icon-circle-plus-outline" v-if="item.inventory > 0" style="margin-right: 4px;"></i>
              {{ item.inventory > 0 ? '申请漂流' : '已借完' }}
            </el-button>
            <div class="b-ops">
              <el-button type="text" size="small" @click="openFootprint(item)" icon="el-icon-guide">漂流足迹</el-button>

              <div v-if="roleIsAdmin || item.uploaderid === id" style="display:inline-flex; align-items:center; gap: 8px;">
                <el-button type="text" size="small" icon="el-icon-edit" @click="handleUpdate(item)">维护</el-button>
                <el-button type="text" size="small" icon="el-icon-delete" style="color:#f56c6c; margin-left: 0;" @click="handleDelete(item)">下架</el-button>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <div class="page-box">
      <pagination v-show="recordTotal>0" :total="recordTotal" :page.sync="queryParam.page" :limit.sync="queryParam.limit" @pagination="fetchData" />
    </div>

    <el-dialog :title="formTitle" :visible.sync="dialogFormVisible" width="800px" custom-class="glass-dialog" center>
      <el-form :model="form" ref="ruleForm" label-width="90px">
        <el-row :gutter="40">
          <el-col :span="14">
            <el-form-item label="图书名称"><el-input v-model="form.bookname" placeholder="书名" /></el-form-item>
            <el-form-item label="图书作者"><el-input v-model="form.bookauthor" placeholder="作者" /></el-form-item>
            <el-form-item label="图书分类">
              <el-select v-model="form.booktypeid" style="width: 100%">
                <el-option v-for="item in typeData" :key="item.booktypeid" :label="item.booktypename" :value="item.booktypeid"/>
              </el-select>
            </el-form-item>
            <el-form-item label="书籍总数"><el-input-number v-model="form.bookcount" :min="1" style="width: 100%" /></el-form-item>
            <el-form-item label="寄语简介">
              <el-input type="textarea" v-model="form.bookdesc" :rows="2" placeholder="谈谈读书感悟（所有人可见）" />
            </el-form-item>
            <el-form-item label="交接说明">
              <el-input type="textarea" v-model="form.contactinfo" :rows="2" placeholder="微信号/宿舍号（仅审核通过后对借阅者可见，绝对保密）" />
            </el-form-item>
          </el-col>

          <el-col :span="10">
            <div class="side-upload-panel">
              <span class="upload-title">图书封面</span>
              <el-upload class="book-uploader" action="http://localhost:9111/BookManager/upload/uploadImg" :show-file-list="false" :on-success="handleAvatarSuccess">
                <img v-if="form.bookimg" :src="'http://localhost:9111/BookManager' + form.bookimg" class="cover-image" />
                <div v-else class="upload-placeholder"><i class="el-icon-picture-outline"></i><p>添加封面图</p></div>
              </el-upload>
            </div>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="standard-footer">
        <el-button @click="dialogFormVisible = false" round class="dialog-action-btn cancel-style">取 消</el-button>
        <el-button type="primary" @click="submitForm" round class="dialog-action-btn">发 布</el-button>
      </div>
    </el-dialog>

    <el-dialog title="📚 提交漂流申请" :visible.sync="borrowDialogVisible" width="500px" custom-class="glass-dialog" center>
      <el-form :model="borrowForm" label-position="top">
        <el-form-item label="阅读计划 / 申请理由">
          <el-input type="textarea" v-model="borrowForm.borrowreason" :rows="3" placeholder="请真诚地填写您的申请理由，这会大大提高通过率哦..." />
        </el-form-item>

        <el-form-item>
          <template slot="label">
            预计借阅天数 <span style="color: #909399; font-size: 12px; font-weight: normal; margin-left: 10px;">(可自由填入，最高 365 天)</span>
          </template>
          <el-input-number v-model="borrowForm.borrowDays" :min="0" :max="365" style="width: 100%;"></el-input-number>
          <div style="font-size: 12px; color: #F56C6C; margin-top: 5px;" v-if="borrowForm.borrowDays === 0">
            <i class="el-icon-warning"></i> 提示：设为 0 天为测试模式，书籍交付后将立刻触发逾期。
          </div>
        </el-form-item>
      </el-form>
      <div slot="footer" style="text-align: center;">
        <el-button @click="borrowDialogVisible = false" round>暂不申请</el-button>
        <el-button type="primary" @click="submitBorrow" round :loading="borrowLoading">确 认 申 请</el-button>
      </div>
    </el-dialog>

    <el-dialog title="🗺️ 书籍漂流足迹" :visible.sync="footprintDialogVisible" width="500px" custom-class="glass-dialog">
      <div class="footprint-scroll-box">
        <el-timeline v-if="footprintList.length > 0" style="padding-top: 10px;">
          <el-timeline-item v-for="(activity, index) in footprintList" :key="index" :icon="activity.state === 2 ? 'el-icon-check' : (activity.state === 1 ? 'el-icon-reading' : 'el-icon-info')" :type="activity.state === 2 ? 'success' : (activity.state === 1 ? 'primary' : 'info')" :timestamp="activity.applytime">
            <span style="font-weight: bold; color: #409EFF;">{{ activity.username }}</span>
            <span v-if="activity.state === 0" style="color: #909399;"> 提交了漂流申请，等待审核...</span>
            <span v-else-if="activity.state === 3" style="color: #F56C6C;"> 的申请被驳回了</span>
            <span v-else-if="activity.state === 4" style="color: #E6A23C;"> 审核通过，正在等待线下交接</span>
            <span v-else-if="activity.state === 5" style="color: #C0C4CC; text-decoration: line-through;"> 撤销了本次借阅</span>
            <span v-else-if="activity.state === 1"> 正在阅读中...</span>
            <span v-else-if="activity.state === 2"> 读完了这本书</span>
            <p v-if="activity.state === 2 && activity.returnmsg" style="color: #67C23A; font-style: italic; font-size: 13px; margin-top: 8px; background: #f0f9eb; padding: 5px 10px; border-radius: 4px;">"{{ activity.returnmsg }}"</p>
          </el-timeline-item>
        </el-timeline>
        <el-empty v-else description="这本书还没有开始它的漂流之旅哦"></el-empty>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from "vuex";
import { queryBookInfosByPage, addBookInfo, updateBookInfo, deleteBookInfo } from "@/api/bookinfo";
import { queryBookTypes } from "@/api/booktype";
import Pagination from '@/components/Pagination';
import request from "@/utils/request";

export default {
  name: "Bookinfo",
  components: { Pagination },
  data() {
    return {
      tableData: [], recordTotal: 0, typeData: [],
      queryParam: { page: 1, limit: 12, bookname: null, bookauthor: null, booktypeid: null },
      dialogFormVisible: false, formType: 0,
      form: { bookid: null, bookname: "", bookauthor: "", bookprice: 0, booktypeid: 1, bookdesc: "", contactinfo: "", bookcount: 1, inventory: 1, bookimg: "", uploaderid: null },
      tableLoading: false,
      borrowDialogVisible: false, borrowLoading: false,
      borrowForm: { bookid: null, borrowreason: '', borrowDays: 30 },
      footprintDialogVisible: false, footprintList: [],
    };
  },
  computed: {
    ...mapGetters(["id", "roles"]),
    formTitle() { return this.formType === 0 ? "发布新的漂流书" : "维护图书档案"; },
    roleIsAdmin() { return this.roles && this.roles[0] === "admin"; },
  },
  created() {
    this.fetchData();
    queryBookTypes().then((res) => { this.typeData = res; });
    if (this.$route.query.openDonate === 'true') {
      setTimeout(() => {
        this.handleCreate();
        if (this.$route.query.wishBookName) this.form.bookname = this.$route.query.wishBookName;
      }, 300);
    }
  },
  methods: {
    fetchData() {
      this.tableLoading = true;
      const params = { ...this.queryParam, userid: this.id };
      queryBookInfosByPage(params).then((res) => {
        this.tableData = res.data || [];
        this.recordTotal = res.count || 0;
        this.tableLoading = false;
      }).catch(() => { this.tableLoading = false; });
    },
    handleFilter() { this.queryParam.page = 1; this.fetchData(); },
    handleCreate() {
      this.formType = 0;
      this.form = { bookid: null, bookname: "", bookauthor: "", bookprice: 0, booktypeid: 1, bookdesc: "", contactinfo: "", bookcount: 1, inventory: 1, bookimg: "", uploaderid: null };
      this.dialogFormVisible = true;
    },
    handleUpdate(row) { 
      if (row.inventory < row.bookcount) {
        this.$message.warning("该书籍正在漂流交接中或已被借阅，暂时无法修改信息！");
        return;
      }
      this.formType = 1; 
      this.form = { ...row }; 
      this.dialogFormVisible = true; 
    },
    handleAvatarSuccess(res) { if (res.code === 0) { this.form.bookimg = res.data; this.$message.success("封面上传成功"); } },

    openBorrowDialog(row) {
      this.borrowForm = { bookid: row.bookid, borrowreason: '', borrowDays: 30 };
      this.borrowDialogVisible = true;
    },

    submitBorrow() {
      if (!this.borrowForm.borrowreason || this.borrowForm.borrowreason.trim() === '') { return this.$message.warning("申请理由不能为空哦！"); }
      this.borrowLoading = true;
      request({
        url: '/borrow/borrowBook', method: 'post',
        data: { userid: this.id, bookid: this.borrowForm.bookid, borrowreason: this.borrowForm.borrowreason, borrowDays: this.borrowForm.borrowDays }
      }).then(res => {
        this.borrowLoading = false;
        if (res.code === 0 || res === 1 || res.code === 200) {
          this.$message.success("🎉 申请成功！系统已通知发布者。");
          this.borrowDialogVisible = false;
          this.fetchData();
        } else {
          this.$message.error(res.msg || res.message || "操作失败，请检查或重试！");
        }
      }).catch(() => { this.borrowLoading = false; });
    },

    submitForm() {
      const action = this.formType === 0 ? addBookInfo : updateBookInfo;
      if (this.formType === 0) { this.form.inventory = this.form.bookcount; this.form.uploaderid = this.id; }
      action(this.form).then((res) => { if (res === 1) { this.$message.success("操作成功"); this.fetchData(); this.dialogFormVisible = false; } });
    },
    handleDelete(row) {
      if (row.inventory < row.bookcount) {
        this.$message.warning("该书籍正在漂流交接中或已被借阅，无法下架！");
        return;
      }
      this.$confirm("确定要下架这本图书吗?", "提示", { type: 'warning' }).then(() => {
        deleteBookInfo(row).then((res) => { if (res === 1) { this.$message.success("下架成功"); this.fetchData(); } });
      });
    },
    resetQuery() {
      // 清空所有条件，包括 uploaderId
      this.queryParam = { page: 1, limit: 12, bookname: null, bookauthor: null, booktypeid: null, uploaderId: null };
      this.fetchData();
    },
    // 【新增】：仅查询自己的发布
    handleMyPublished() {
      this.queryParam = { page: 1, limit: 12, bookname: null, bookauthor: null, booktypeid: null, uploaderId: this.id };
      this.fetchData();
    },
    openFootprint(row) {
      this.footprintDialogVisible = true; this.footprintList = [];
      import('@/utils/request').then(({ default: request }) => {
        request({ url: '/borrow/queryBorrowsByPage', method: 'get', params: { page: 1, limit: 100, bookId: row.bookid } }).then(res => {
          if (res.data) { this.footprintList = res.data; } else if (res.rows) { this.footprintList = res.rows; }
        });
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.header-form-flex { display: flex; align-items: center; flex-wrap: wrap; gap: 10px; .el-form-item { margin-bottom: 0 !important; } }
.modern-btn { min-width: 110px !important; height: 40px !important; display: inline-flex !important; align-items: center; justify-content: center; padding: 0 15px !important; }

/* 【关键修复 2】：把粗暴的全局 .el-row 改成了限定于 .book-list-container，这样就绝对不会影响弹窗了！ */
.book-list-container { display: flex; flex-wrap: wrap; }
.book-list-container > .el-col { display: flex; margin-bottom: 24px; }

.book-card-item {
  width: 100%; border-radius: 18px; transition: transform 0.3s ease; display: flex; flex-direction: column;
  .book-image-box { height: 180px; position: relative; background: rgba(0,0,0,0.03); display: flex; justify-content: center; align-items: center; flex-shrink: 0; .book-main-img { width: 100%; height: 100%; } .status-tag { position: absolute; top: 12px; right: 12px; padding: 2px 8px; border-radius: 10px; font-size: 11px; background: #6366f1; color: #fff; &.out { background: #94a3b8; } } }
  .book-content-box {
    padding: 15px; flex: 1; display: flex; flex-direction: column;
    .b-title { font-size: 16px; font-weight: 600; margin: 0; color: #1e293b; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
    .b-author { font-size: 13px; color: #64748b; margin: 5px 0 8px 0; }
    .b-desc { font-size: 12px; color: #94a3b8; margin: 0 0 10px 0; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; font-style: italic; flex: 1; }
    .b-ops { display: flex; justify-content: space-between; align-items: center; margin-top: 10px; border-top: 1px solid #f1f5f9; padding-top: 5px; }
  }
  &:hover { transform: translateY(-5px); }
}

.side-upload-panel { display: flex; flex-direction: column; align-items: center; .upload-title { font-size: 14px; font-weight: bold; margin-bottom: 15px; color: #475569; } .book-uploader ::v-deep .el-upload { border: 2px dashed #cbd5e1; border-radius: 12px; cursor: pointer; width: 170px; height: 230px; display: flex; justify-content: center; align-items: center; background: rgba(255,255,255,0.4); &:hover { border-color: #6366f1; } } .cover-image { width: 170px; height: 230px; border-radius: 12px; object-fit: cover; } .upload-placeholder { color: #94a3b8; text-align: center; i { font-size: 30px; margin-bottom: 8px; } } }
.standard-footer { display: flex; justify-content: center; gap: 20px; .dialog-action-btn { width: 140px !important; height: 45px !important; font-size: 15px !important; display: inline-flex !important; align-items: center; justify-content: center; } .cancel-style { background: #f1f5f9 !important; color: #475569 !important; border: none !important; } }
.page-box { text-align: center; margin-top: 25px; }
.footprint-scroll-box { max-height: 400px; overflow-y: auto; padding-right: 15px; }
.footprint-scroll-box::-webkit-scrollbar { width: 6px; }
.footprint-scroll-box::-webkit-scrollbar-thumb { border-radius: 6px; background: rgba(144, 147, 153, 0.3); }
.footprint-scroll-box::-webkit-scrollbar-thumb:hover { background: rgba(144, 147, 153, 0.5); }
.footprint-scroll-box::-webkit-scrollbar-track { border-radius: 6px; background: rgba(144, 147, 153, 0.05); }


/* ======== 修复顶部操作按钮文字与容器大小不统一 ======== */
/* 注意：这里的外层类名可能是 filter-wrapper 或 filter-container，我把常见的都加上了以防万一 */
::v-deep .filter-wrapper .el-button,
::v-deep .filter-container .el-button,
::v-deep .filter-right .el-button {
  height: 38px !important; /* 强制统一高度 */
  border-radius: 19px !important; /* 完美胶囊圆角 */
  padding: 0 18px !important; /* 重置内边距 */
  display: inline-flex !important; /* 核心：开启 Flex 布局 */
  justify-content: center !important; /* 水平居中 */
  align-items: center !important; /* 垂直绝对居中 */
  font-size: 14px !important;
  font-weight: 500 !important;
  letter-spacing: 1px !important;

  /* 强制统一图标的尺寸与间距 */
  i {
    font-size: 16px !important;
    margin-right: 5px !important;
  }

  /* 杀掉自带的 span 偏移 */
  span {
    margin-left: 0 !important;
  }
}

/* 强制给相邻的按钮加上合理的间距，防止挤在一起 */
::v-deep .filter-wrapper .el-button + .el-button,
::v-deep .filter-container .el-button + .el-button {
  margin-left: 12px !important;
}

::v-deep .action-btn-custom {
  height: 38px !important;
  border-radius: 19px !important;
  padding: 0 !important;
  display: flex !important;
  justify-content: center !important;
  align-items: center !important;
  font-size: 14px !important;
  font-weight: bold !important;
  letter-spacing: 1px !important;
}
</style>
