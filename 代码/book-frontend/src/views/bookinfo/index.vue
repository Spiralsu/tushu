<template>
  <div class="app-container">
    <el-card class="filter-wrapper" shadow="never">
      <el-form :inline="true" :model="queryParam" class="search-form" size="small">
        <el-form-item>
          <el-input v-model="queryParam.bookname" placeholder="输入书名寻找知识..." prefix-icon="el-icon-search" clearable @keyup.enter.native="handleFilter" class="round-input" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParam.bookauthor" placeholder="作者名" prefix-icon="el-icon-user" clearable @keyup.enter.native="handleFilter" class="round-input" />
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParam.booktypeid" filterable placeholder="选择漂流分类" clearable class="round-input">
            <el-option v-for="item in typeData" :key="item.booktypeid" :label="item.booktypename" :value="item.booktypeid"/>
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" class="round-btn" icon="el-icon-search" @click="handleFilter">探索好书</el-button>
          <el-button type="success" class="round-btn" icon="el-icon-upload2" @click="handleCreate">
            {{ roleIsAdmin ? '添加图书' : '发布旧书' }}
          </el-button>
          <el-button size="small" class="round-btn" icon="el-icon-refresh" @click="handleShowAll">显示全部</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-row :gutter="24" class="book-grid" v-loading="tableLoading">
      <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="item in tableData" :key="item.bookid" style="margin-bottom: 24px;">
        <el-card shadow="hover" class="book-card">
          <div class="card-cover">
            <el-image
              :src="$store.state.settings.baseApi + item.bookimg"
              class="book-image"
              fit="cover">
              <div slot="error" class="image-error"><i class="el-icon-reading"></i></div>
            </el-image>
            <div class="status-badge" :class="{'is-drifting': item.inventory <= 0}">
              {{ item.inventory > 0 ? '等待漂流' : '他人漂流中' }}
            </div>
          </div>

          <div class="card-body">
            <h3 class="book-title" :title="item.bookname">{{ item.bookname }}</h3>
            <p class="book-author">作者：{{ item.bookauthor }}</p>
            <div class="book-tags">
              <el-tag size="mini" effect="plain" type="info">{{ item.booktypename }}</el-tag>
              <span class="book-inventory">余 {{ item.inventory || 0 }} 本</span>
            </div>

            <div class="action-row">
              <el-button type="primary" size="small" round @click="handleBorrow(item)" :disabled="item.inventory <= 0" class="get-btn">
                {{ item.inventory > 0 ? '申请漂流' : '已借出' }}
              </el-button>
              <div class="admin-actions" v-if="roleIsAdmin">
                <el-button type="text" size="small" @click="handleUpdate(item)">维护信息</el-button>
                <el-button type="text" size="small" style="color:#f56c6c;" @click="handleDelete(item)">下架</el-button>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-empty v-if="tableData.length === 0" description="暂无漂流书籍数据"></el-empty>

    <div class="pagination-container">
      <el-pagination background @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page.sync="queryParam.page" :page-sizes="[12, 24, 36, 48]" :page-size="queryParam.limit" layout="total, sizes, prev, pager, next, jumper" :total="recordTotal"></el-pagination>
    </div>

    <el-dialog :title="formTitle" :visible.sync="dialogFormVisible" width="60%" top="5vh" :close-on-click-modal="false" custom-class="glass-dialog">
      <el-row :gutter="20">
        <el-col :span="16">
          <el-form :model="form" :rules="rules" ref="ruleForm" label-width="100px">
            <el-form-item label="图书名称" prop="bookname"><el-input v-model="form.bookname" placeholder="请输入想要共享的书名"></el-input></el-form-item>
            <el-form-item label="作者" prop="bookauthor"><el-input v-model="form.bookauthor" placeholder="作者名"></el-input></el-form-item>
            <el-form-item label="原价评估" prop="bookprice"><el-input-number v-model="form.bookprice" :min="0" :precision="2" style="width: 100%"></el-input-number></el-form-item>
            <el-form-item label="图书分类" prop="booktypeid">
              <el-select v-model="form.booktypeid" placeholder="请选择" style="width: 100%"><el-option v-for="item in typeData" :key="item.booktypeid" :label="item.booktypename" :value="item.booktypeid"></el-option></el-select>
            </el-form-item>
            <el-form-item label="漂流数量" prop="bookcount">
              <el-input-number v-model="form.bookcount" :min="1" style="width: 100%"></el-input-number>
            </el-form-item>
            <el-form-item label="寄语/简介" prop="bookdesc"><el-input type="textarea" v-model="form.bookdesc" :rows="4" placeholder="写几句话推荐这本书吧..."></el-input></el-form-item>
          </el-form>
        </el-col>
        <el-col :span="8">
          <div class="upload-container">
            <h3>图书封面</h3>
            <el-upload class="avatar-uploader" action="http://localhost:9111/BookManager/upload/uploadImg" :show-file-list="false" :on-success="handleAvatarSuccess" :before-upload="beforeAvatarUpload">
              <img v-if="form.bookimg" :src="$store.state.settings.baseApi + form.bookimg" class="avatar"/>
              <div v-else class="upload-placeholder"><i class="el-icon-plus"></i><div class="upload-text">点击上传图片</div></div>
            </el-upload>
          </div>
        </el-col>
      </el-row>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false" round>再想想</el-button>
        <el-button type="primary" @click="submitForm" round>确定发布</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
// 完整沿用你原有的业务逻辑！
import { mapGetters } from "vuex";
import permission from "@/directive/permission/index.js";
import waves from "@/directive/waves";
import { getCount, queryBookInfosByPage, addBookInfo, deleteBookInfo, deleteBookInfos, updateBookInfo } from "@/api/bookinfo";
import { queryBookTypes } from "@/api/booktype";
import request from "@/utils/request";

export default {
  name: "Bookinfo",
  directives: { waves, permission },
  created() {
    this.fetchData();
    queryBookTypes().then((res) => { this.typeData = res; });
  },
  mounted() {
    if (this.$route.query.openDonate === 'true') {
      this.handleCreate();
    }
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
    handleSizeChange(curSize) {
      this.queryParam.limit = curSize;
      this.fetchData();
    },
    handleCurrentChange(curPage) {
      this.queryParam.page = curPage;
      this.fetchData();
    },
    handleFilter() {
      this.queryParam.page = 1;
      this.fetchData();
    },
    handleShowAll() {
      this.queryParam.page = 1;
      this.queryParam.bookname = null;
      this.queryParam.bookauthor = null;
      this.queryParam.booktypeid = null;
      this.fetchData();
    },
    handleAvatarSuccess(res, file) {
      if (res.code === 0) {
        this.$message.success("上传成功");
        this.form.bookimg = res.data;
      } else {
        this.$message.error("上传失败");
      }
    },
    beforeAvatarUpload(file) {
      const isJPG = file.type === "image/jpeg";
      const isLt2M = file.size / 1024 / 1024 < 2;
      if (!isJPG) this.$message.error("只能是 JPG 格式!");
      if (!isLt2M) this.$message.error("大小不能超过 2MB!");
      return isJPG && isLt2M;
    },
    handleCreate() {
      queryBookTypes().then((res) => { this.typeData = res; });
      this.formType = 0;
      this.form = { bookid: null, bookname: "", bookauthor: "", bookprice: 0, booktypeid: 1, bookdesc: "", bookcount: 1, inventory: 1, bookimg: "" };
      this.dialogFormVisible = true;
    },
    handleUpdate(row) {
      queryBookTypes().then((res) => { this.typeData = res; });
      this.formType = 1;
      this.form = { ...row };
      this.dialogFormVisible = true;
    },
    handleBorrow(row) {
      this.$prompt('每一段漂流都需要理由，请输入您想获取它的理由/计划：', '获取漂流申请', {
        confirmButtonText: '提交申请',
        cancelButtonText: '取消',
        inputPattern: /\S/,
        inputErrorMessage: '理由不能为空',
        customClass: 'glass-message-box'
      }).then(({ value }) => {
        request({
          url: '/borrow/borrowBook',
          method: 'post',
          data: {
            userid: this.id,
            bookid: row.bookid,
            borrowreason: value
          }
        }).then(res => {
          if (res === 1) {
            this.$message.success("申请已提交，系统即将为您安排接头漂流！");
            this.fetchData();
          } else {
            this.$message.error("申请失败，可能书已被抢先一步");
          }
        });
      }).catch(() => {});
    },
    submitForm() {
      if (this.formType === 0) {
        this.form.inventory = this.form.bookcount;
        addBookInfo(this.form).then((res) => {
          if (res === 1) {
            this.$message.success(this.roleIsAdmin ? "上架成功" : "感谢您的捐赠发布！");
            this.handleShowAll();
          } else {
            this.$message.error("操作失败");
          }
          this.dialogFormVisible = false;
        });
      } else if (this.formType === 1) {
        updateBookInfo(this.form).then((res) => {
          if (res === 1 || res === 0) {
            this.$message.success("维护成功");
            this.fetchData();
          } else {
            this.$message.error("维护失败");
          }
          this.dialogFormVisible = false;
        });
      }
    },
    handleDelete(row) {
      this.$confirm("确定要下架这本漂流书吗?", "提示", { type: "warning" }).then(() => {
        deleteBookInfo(row).then((res) => {
          if (res === 1) {
            this.$message.success("已成功下架");
            this.fetchData();
          } else if (res === -1) {
            this.$message.error("这本书正在他人的旅途中，无法下架");
          } else {
            this.$message.error("下架失败");
          }
        });
      });
    }
  },
  data() {
    return {
      tableData: [],
      recordTotal: 0,
      typeData: [],
      // 默认卡片布局推荐一排 4 个，故 limit 设为 12
      queryParam: { page: 1, limit: 12, bookname: null, bookauthor: null, booktypeid: null },
      dialogFormVisible: false,
      formType: 0,
      form: { bookid: null, bookname: "", bookauthor: "", bookprice: 0, booktypeid: 1, bookdesc: "", bookcount: 1, inventory: 1, bookimg: "" },
      rules: {
        bookname: [{ required: true, message: "请输入你想共享的书名", trigger: "blur" }],
        bookauthor: [{ required: true, message: "是谁写出了这部大作？", trigger: "blur" }],
        bookcount: [{ required: true, message: "要放入几本漂流？", trigger: "blur" }],
      },
      tableLoading: false
    };
  },
  computed: {
    ...mapGetters(["id", "name", "roles"]),
    formTitle() { return this.formType === 0 ? "发布旧书 / 开启漂流" : "维护书籍信息"; },
    roleIsAdmin() { return this.roles[0] === "admin"; },
  },
};
</script>

<style lang="scss" scoped>
.app-container {
  padding: 24px; background-color: #f7f9fc; min-height: calc(100vh - 50px);
}

.filter-wrapper {
  margin-bottom: 24px; border-radius: 16px; border: none; box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.02);
  .search-form { display: flex; flex-wrap: wrap; align-items: center; margin-bottom: -18px; }
  .round-input ::v-deep .el-input__inner { border-radius: 20px; background-color: #f5f7fa; border: transparent; }
  .round-btn { border-radius: 20px; padding: 10px 20px; }
}

.book-card {
  border-radius: 16px; border: none; overflow: hidden; transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  ::v-deep .el-card__body { padding: 0; display: flex; flex-direction: column; height: 100%; }
  &:hover { transform: translateY(-5px); box-shadow: 0 12px 24px rgba(0,0,0,0.08); }

  .card-cover {
    height: 220px; background: #eef1f6; position: relative; display: flex; align-items: center; justify-content: center; overflow: hidden;
    .book-image { width: 100%; height: 100%; transition: transform 0.3s; }
    &:hover .book-image { transform: scale(1.05); }
    .image-error { font-size: 50px; color: #dcdfe6; }

    .status-badge {
      position: absolute; top: 12px; right: 12px; padding: 4px 12px; border-radius: 20px; font-size: 12px; font-weight: bold; background: #67c23a; color: #fff; box-shadow: 0 2px 8px rgba(0,0,0,0.1);
      &.is-drifting { background: #e6a23c; }
    }
  }

  .card-body {
    padding: 20px; flex: 1; display: flex; flex-direction: column;
    .book-title { font-size: 18px; margin: 0 0 8px 0; color: #303133; font-weight: 600; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
    .book-author { font-size: 13px; color: #909399; margin: 0 0 12px 0; }

    .book-tags {
      display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;
      .book-inventory { font-size: 12px; color: #409eff; font-weight: 500; background: #ecf5ff; padding: 2px 8px; border-radius: 10px; }
    }

    .action-row {
      margin-top: auto; display: flex; justify-content: space-between; align-items: center; border-top: 1px solid #f0f2f5; padding-top: 16px;
      .get-btn { width: 100%; font-weight: 600; letter-spacing: 1px; }
      .admin-actions { display: flex; gap: 10px; width: 100%; margin-left: 10px; justify-content: flex-end; }
    }
  }
}

.upload-container {
  text-align: center;
  .avatar-uploader {
    border: 2px dashed #d9d9d9; border-radius: 12px; cursor: pointer; position: relative; overflow: hidden; width: 160px; height: 220px; margin: 0 auto; background: #fafafa;
    &:hover { border-color: #409EFF; }
    .avatar { width: 100%; height: 100%; display: block; object-fit: cover; }
    .upload-placeholder { width: 100%; height: 100%; display: flex; flex-direction: column; justify-content: center; align-items: center; color: #8c939d; i { font-size: 28px; margin-bottom: 10px; } }
  }
}
.pagination-container { text-align: center; margin-top: 30px; }
</style>
