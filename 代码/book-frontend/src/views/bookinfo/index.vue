<template>
  <div class="app-container">
    <el-card class="filter-container" shadow="never" style="margin-bottom: 35px; border: none;">
      <el-form :inline="true" :model="queryParam" size="medium" class="header-form-flex">
        <el-form-item>
          <el-input v-model="queryParam.bookname" placeholder="书名检索..." prefix-icon="el-icon-search" clearable />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParam.bookauthor" placeholder="作者..." prefix-icon="el-icon-user" clearable />
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParam.booktypeid" placeholder="图书分类" clearable>
            <el-option v-for="item in typeData" :key="item.booktypeid" :label="item.booktypename" :value="item.booktypeid"/>
          </el-select>
        </el-form-item>

        <el-form-item class="search-btn-group">
          <el-button type="primary" icon="el-icon-search" @click="handleFilter" class="modern-btn">探索好书</el-button>
          <el-button type="success" icon="el-icon-plus" @click="handleCreate" class="modern-btn">发布旧书</el-button>
          <el-button icon="el-icon-refresh" @click="handleShowAll" class="modern-btn reset-btn">显示全部</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-row :gutter="24" v-loading="tableLoading">
      <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="item in tableData" :key="item.bookid" style="margin-bottom: 24px;">
        <el-card class="book-card-item" shadow="hover">
          <div class="book-image-box">
            <el-image
              :src="item.bookimg ? 'http://localhost:9111/BookManager' + item.bookimg : ''"
              fit="cover"
              class="book-main-img">
              <div slot="error" class="error-img-slot"><i class="el-icon-collection"></i></div>
            </el-image>
            <div class="status-tag" :class="{'out': item.inventory <= 0}">
              {{ item.inventory > 0 ? '剩余 ' + item.inventory : '漂流中' }}
            </div>
          </div>
          <div class="book-content-box">
            <h3 class="b-title" :title="item.bookname">{{ item.bookname }}</h3>
            <p class="b-author">{{ item.bookauthor }}</p>
            <el-button type="primary" size="small" round @click="handleBorrow(item)" :disabled="item.inventory <= 0" style="width: 100%;">
              {{ item.inventory > 0 ? '申请漂流' : '已借完' }}
            </el-button>
            <div class="b-ops">
              <el-button type="text" size="small" @click="openFootprint(item)" icon="el-icon-guide">漂流足迹</el-button>
              <div v-if="roleIsAdmin">
                <el-button type="text" size="small" @click="handleUpdate(item)">维护</el-button>
                <el-button type="text" size="small" style="color:#f56c6c;" @click="handleDelete(item)">下架</el-button>
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
            <el-form-item label="寄语简介"><el-input type="textarea" v-model="form.bookdesc" :rows="4" placeholder="说点什么吧..." /></el-form-item>
          </el-col>

          <el-col :span="10">
            <div class="side-upload-panel">
              <span class="upload-title">图书封面</span>
              <el-upload
                class="book-uploader"
                action="http://localhost:9111/BookManager/upload/uploadImg"
                :show-file-list="false"
                :on-success="handleAvatarSuccess">
                <img v-if="form.bookimg" :src="'http://localhost:9111/BookManager' + form.bookimg" class="cover-image" />
                <div v-else class="upload-placeholder">
                  <i class="el-icon-picture-outline"></i>
                  <p>添加封面图</p>
                </div>
              </el-upload>
            </div>
          </el-col>
        </el-row>
      </el-form>

      <div slot="footer" class="standard-footer">
        <el-button @click="dialogFormVisible = false" round class="dialog-action-btn cancel-style">再 想 想</el-button>
        <el-button type="primary" @click="submitForm" round class="dialog-action-btn">确 认 发 布</el-button>
      </div>
    </el-dialog>

    <el-dialog title="📚 书籍漂流足迹" :visible.sync="footprintDialogVisible" width="500px" custom-class="glass-dialog">
      <div v-loading="footprintLoading" style="min-height: 200px; padding: 10px;">
        <el-timeline v-if="footprintList.length > 0">
          <el-timeline-item v-for="(record, index) in footprintList" :key="index" :timestamp="record.borrowtime" placement="top" color="#6366f1">
            <el-card shadow="never" style="border:none; background:rgba(255,255,255,0.4);">
              <h4 style="margin:0;">读者：{{ record.username }}</h4>
              <p v-if="record.returntime" style="color: #10b981; margin-top: 8px;">
                <i class="el-icon-chat-line-square"></i> "{{ record.returnmsg || '未留寄语' }}"
              </p>
              <p v-else style="color: #6366f1; font-weight: bold; margin-top: 8px;">正在研读中...</p>
            </el-card>
          </el-timeline-item>
        </el-timeline>
        <el-empty v-else description="暂无足迹"></el-empty>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from "vuex";
import { queryBookInfosByPage, addBookInfo, deleteBookInfo, updateBookInfo } from "@/api/bookinfo";
import { queryBookTypes } from "@/api/booktype";
import { queryBorrowsByPage } from "@/api/borrow";
import Pagination from '@/components/Pagination';
import request from "@/utils/request";

export default {
  name: "Bookinfo",
  components: { Pagination },
  data() {
    return {
      tableData: [],
      recordTotal: 0,
      typeData: [],
      queryParam: { page: 1, limit: 12, bookname: null, bookauthor: null, booktypeid: null },
      dialogFormVisible: false,
      formType: 0,
      form: { bookid: null, bookname: "", bookauthor: "", bookprice: 0, booktypeid: 1, bookdesc: "", bookcount: 1, inventory: 1, bookimg: "" },
      tableLoading: false,
      footprintDialogVisible: false,
      footprintLoading: false,
      footprintList: []
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
  },
  methods: {
    fetchData() {
      this.tableLoading = true;
      queryBookInfosByPage(this.queryParam).then((res) => {
        this.tableData = res.data || [];
        this.recordTotal = res.count || 0;
        this.tableLoading = false;
      }).catch(() => { this.tableLoading = false; });
    },
    handleFilter() { this.queryParam.page = 1; this.fetchData(); },
    handleShowAll() {
      this.queryParam = { page: 1, limit: 12, bookname: null, bookauthor: null, booktypeid: null };
      this.fetchData();
    },
    handleCreate() {
      this.formType = 0;
      this.form = { bookid: null, bookname: "", bookauthor: "", bookprice: 0, booktypeid: 1, bookdesc: "", bookcount: 1, inventory: 1, bookimg: "" };
      this.dialogFormVisible = true;
    },
    handleUpdate(row) {
      this.formType = 1;
      this.form = { ...row };
      this.dialogFormVisible = true;
    },
    handleAvatarSuccess(res) {
      if (res.code === 0) {
        this.$message.success("封面上传成功");
        this.form.bookimg = res.data;
      }
    },
    handleBorrow(row) {
      this.$prompt('请输入您的阅读计划/理由：', '漂流申请', {
        confirmButtonText: '提交申请',
        cancelButtonText: '取消',
        customClass: 'glass-message-box'
      }).then(({ value }) => {
        request({
          url: '/borrow/borrowBook',
          method: 'post',
          data: { userid: this.id, bookid: row.bookid, borrowreason: value }
        }).then(res => {
          if (res === 1) { this.$message.success("申请成功！"); this.fetchData(); }
        });
      });
    },
    submitForm() {
      const action = this.formType === 0 ? addBookInfo : updateBookInfo;
      if (this.formType === 0) this.form.inventory = this.form.bookcount;
      action(this.form).then((res) => {
        if (res === 1) {
          this.$message.success("操作成功");
          this.fetchData();
          this.dialogFormVisible = false;
        }
      });
    },
    handleDelete(row) {
      this.$confirm("确定要下架这本图书吗?", "提示", { type: 'warning' }).then(() => {
        deleteBookInfo(row).then((res) => {
          if (res === 1) { this.$message.success("下架成功"); this.fetchData(); }
        });
      });
    },
    openFootprint(item) {
      this.footprintDialogVisible = true;
      this.footprintLoading = true;
      queryBorrowsByPage({ bookId: item.bookid, page: 1, limit: 100 }).then(res => {
        let list = res.data || res.rows || [];
        this.footprintList = list.filter(r => r.state >= 1);
        this.footprintLoading = false;
      }).catch(() => { this.footprintLoading = false; });
    }
  }
};
</script>

<style lang="scss" scoped>
/* 1. 顶部表单 Flex 对齐 */
.header-form-flex {
  display: flex; align-items: center; flex-wrap: wrap; gap: 10px;
  .el-form-item { margin-bottom: 0 !important; }
}
/* 直接隐藏确认发布按钮内的所有图标，最彻底 */
::v-deep .standard-footer .el-button--primary i {
  display: none !important;
}

/* 统一搜索栏按钮：解决文字偏移与对齐问题 */
.modern-btn {
  min-width: 110px !important;
  height: 40px !important;
  /* 强制图标与文字垂直居中 */
  display: inline-flex !important;
  align-items: center;
  justify-content: center;
  padding: 0 15px !important;

  ::v-deep i {
    font-size: 14px !important;
    margin-right: 6px;
    margin-top: 0 !important; /* 防止图标导致的文字下移 */
  }
}

/* 修复“显示全部”按钮：强制取消边框差异，保持高度与其他两个带背景的按钮一致 */
.reset-btn {
  background: rgba(192, 192, 192, 0.4) !important;
  border: none !important;
  color: #606266 !important;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05) !important;
  &:hover { background: rgba(255, 255, 255, 0.8) !important; }
}

/* 2. 卡片美化 */
.book-card-item {
  border-radius: 18px; transition: transform 0.3s ease;
  .book-image-box {
    height: 180px; position: relative; background: rgba(0,0,0,0.03);
    display: flex; justify-content: center; align-items: center;
    .book-main-img { width: 100%; height: 100%; }
    .error-img-slot { font-size: 40px; color: #cbd5e1; }
    .status-tag {
      position: absolute; top: 12px; right: 12px; padding: 2px 8px; border-radius: 10px;
      font-size: 11px; background: #6366f1; color: #fff;
      &.out { background: #94a3b8; }
    }
  }
  .book-content-box {
    padding: 15px;
    .b-title { font-size: 16px; font-weight: 600; margin: 0; color: #1e293b; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
    .b-author { font-size: 13px; color: #64748b; margin: 5px 0 12px 0; }
    .b-ops { display: flex; justify-content: space-between; align-items: center; margin-top: 10px; border-top: 1px solid #f1f5f9; padding-top: 5px; }
  }
  &:hover { transform: translateY(-5px); }
}

/* 3. 弹窗左文右图布局 */
.side-upload-panel {
  display: flex; flex-direction: column; align-items: center;
  .upload-title { font-size: 14px; font-weight: bold; margin-bottom: 15px; color: #475569; }
  .book-uploader ::v-deep .el-upload {
    border: 2px dashed #cbd5e1; border-radius: 12px; cursor: pointer; width: 170px; height: 230px;
    display: flex; justify-content: center; align-items: center; background: rgba(255,255,255,0.4);
    &:hover { border-color: #6366f1; }
  }
  .cover-image { width: 170px; height: 230px; border-radius: 12px; object-fit: cover; }
  .upload-placeholder { color: #94a3b8; text-align: center; i { font-size: 30px; margin-bottom: 8px; } }
}

/* 弹窗底部按钮对齐与美化 */
.standard-footer {
  display: flex; justify-content: center; gap: 20px;
  .dialog-action-btn {
    width: 140px !important;
    height: 45px !important;
    font-size: 15px !important;
    display: inline-flex !important;
    align-items: center;
    justify-content: center;
  }
  .cancel-style { background: #f1f5f9 !important; color: #475569 !important; border: none !important; }
}

.page-box { text-align: center; margin-top: 25px; }
</style>
