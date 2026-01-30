<template>
  <div class="app-container">
    <el-card class="filter-container" shadow="hover">
      <el-form :inline="true" :model="queryParam" class="search-form" size="small">
        <div class="form-row">
          <div class="form-item-container">
            <label class="form-label">书名</label>
            <el-input v-model="queryParam.bookname" placeholder="输入书名关键字" prefix-icon="el-icon-search" clearable @keyup.enter.native="handleFilter" class="form-input"/>
          </div>
          <div class="form-item-container">
            <label class="form-label">作者</label>
            <el-input v-model="queryParam.bookauthor" placeholder="输入作者名" prefix-icon="el-icon-edit" clearable @keyup.enter.native="handleFilter" class="form-input"/>
          </div>
          <div class="form-item-container">
            <label class="form-label">图书类型</label>
            <el-select v-model="queryParam.booktypeid" filterable placeholder="选择类型" clearable class="form-input">
              <el-option v-for="item in typeData" :key="item.booktypeid" :label="item.booktypename" :value="item.booktypeid"/>
            </el-select>
          </div>
        </div>

        <el-row :gutter="20" type="flex" align="middle" justify="start" class="action-row">
          <el-col>
            <el-form-item class="action-buttons">
              <el-button v-waves type="primary" size="small" icon="el-icon-search" @click="handleFilter">搜索</el-button>

              <el-button type="success" size="small" icon="el-icon-upload2" @click="handleCreate">
                {{ roleIsAdmin ? '添加图书' : '捐赠图书' }}
              </el-button>

              <el-button v-permission="['admin']" type="danger" size="small" icon="el-icon-delete" @click="handleDeleteSome" :disabled="selectedRows.length === 0">批量删除</el-button>
              <el-button size="small" icon="el-icon-refresh" @click="handleShowAll">显示全部</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <div class="control-panel">
      <div class="view-switch">
        <el-radio-group v-model="viewMode" size="small">
          <el-radio-button label="table">表格视图</el-radio-button>
          <el-radio-button label="card">卡片视图</el-radio-button>
        </el-radio-group>
      </div>
    </div>

    <el-card shadow="hover" v-show="viewMode === 'table'" class="table-container">
      <el-table ref="multipleTable" :data="tableData" border stripe highlight-current-row @selection-change="handleSelectionChange" style="width: 100%" v-loading="tableLoading">
        <el-table-column fixed type="selection" width="50" align="center"></el-table-column>
        <el-table-column fixed prop="bookid" label="序号" width="80" align="center"></el-table-column>
        <el-table-column label="图书封面" width="100" align="center">
          <template slot-scope="scope">
            <el-image :src="$store.state.settings.baseApi + scope.row.bookimg" style="width: 60px; height: 80px; border-radius: 4px;" :preview-src-list="[$store.state.settings.baseApi + scope.row.bookimg]" fit="cover">
              <div slot="error" class="image-error"><i class="el-icon-picture-outline"></i></div>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column prop="bookname" label="图书名称" min-width="150" show-overflow-tooltip></el-table-column>
        <el-table-column prop="bookauthor" label="作者" min-width="120" show-overflow-tooltip></el-table-column>
        <el-table-column prop="bookprice" label="价格" min-width="80" align="center">
          <template slot-scope="scope"><span class="book-price">{{ scope.row.bookprice }} 元</span></template>
        </el-table-column>
        <el-table-column prop="booktypename" label="图书类型" min-width="100" align="center">
          <template slot-scope="scope"><el-tag size="small">{{ scope.row.booktypename }}</el-tag></template>
        </el-table-column>

        <el-table-column label="库存数量" min-width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.inventory > 0 ? 'success' : 'danger'" size="small" effect="plain">
              {{ scope.row.inventory || 0 }} 本
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column fixed="right" label="操作" min-width="200" align="center">
          <template slot-scope="scope">
            <el-button v-permission="['admin']" type="primary" size="mini" icon="el-icon-edit" @click="handleUpdate(scope.row)" circle></el-button>
            <el-button v-permission="['admin']" type="danger" size="mini" icon="el-icon-delete" @click="handleDelete(scope.row)" circle></el-button>
            <el-tooltip content="申请漂流" placement="top">
              <el-button :disabled="scope.row.inventory <= 0" type="success" size="mini" icon="el-icon-position" @click="handleBorrow(scope.row)">
                {{ scope.row.inventory > 0 ? '申请' : '无货' }}
              </el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination background @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page.sync="queryParam.page" :page-sizes="[10, 20, 50, 100]" :page-size="queryParam.limit" layout="total, sizes, prev, pager, next, jumper" :total="recordTotal"></el-pagination>
      </div>
    </el-card>

    <div v-show="viewMode === 'card'" class="card-view-container">
      <el-row :gutter="24">
        <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="item in tableData" :key="item.bookid">
          <el-card shadow="hover" class="book-card">
            <div class="book-cover">
              <el-image :src="$store.state.settings.baseApi + item.bookimg" fit="contain"><div slot="error" class="image-error"><i class="el-icon-picture-outline"></i></div></el-image>
              <div class="book-status">
                <el-tag :type="item.inventory > 0 ? 'success' : 'danger'" size="mini">
                  {{ item.inventory > 0 ? `余${item.inventory}本` : "无货" }}
                </el-tag>
              </div>
            </div>
            <div class="book-info">
              <h3 class="book-title" :title="item.bookname">{{ item.bookname }}</h3>
              <div class="book-meta">
                <span class="book-author" :title="item.bookauthor">作者: {{ item.bookauthor }}</span>
                <span class="book-price">¥ {{ item.bookprice }}</span>
              </div>
              <div class="book-category"><el-tag size="mini" effect="plain">{{ item.booktypename }}</el-tag></div>
              <p class="book-desc" :title="item.bookdesc">{{ item.bookdesc }}</p>
              <div class="book-actions">
                <el-button v-permission="['admin']" type="primary" size="mini" icon="el-icon-edit" @click="handleUpdate(item)" circle></el-button>
                <el-button v-permission="['admin']" type="danger" size="mini" icon="el-icon-delete" @click="handleDelete(item)" circle></el-button>
                <el-button :disabled="item.inventory <= 0" type="success" size="mini" icon="el-icon-position" @click="handleBorrow(item)">申请</el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <div class="pagination-container">
        <el-pagination background @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page.sync="queryParam.page" :page-sizes="[12, 24, 36, 48]" :page-size="queryParam.limit" layout="total, sizes, prev, pager, next, jumper" :total="recordTotal"></el-pagination>
      </div>
    </div>

    <el-dialog :title="formTitle" :visible.sync="dialogFormVisible" width="60%" top="5vh" :close-on-click-modal="false">
      <el-row :gutter="20">
        <el-col :span="16">
          <el-form :model="form" :rules="rules" ref="ruleForm" label-width="100px" class="book-form">
            <el-form-item label="图书名称" prop="bookname"><el-input v-model="form.bookname"></el-input></el-form-item>
            <el-form-item label="作者" prop="bookauthor"><el-input v-model="form.bookauthor"></el-input></el-form-item>
            <el-form-item label="价格" prop="bookprice"><el-input-number v-model="form.bookprice" :min="0" :precision="2" style="width: 100%"></el-input-number></el-form-item>
            <el-form-item label="类型" prop="booktypeid">
              <el-select v-model="form.booktypeid" placeholder="请选择" style="width: 100%"><el-option v-for="item in typeData" :key="item.booktypeid" :label="item.booktypename" :value="item.booktypeid"></el-option></el-select>
            </el-form-item>
            <el-form-item label="数量" prop="bookcount">
              <el-input-number v-model="form.bookcount" :min="1" style="width: 100%"></el-input-number>
            </el-form-item>
            <el-form-item label="简介" prop="bookdesc"><el-input type="textarea" v-model="form.bookdesc" :rows="4"></el-input></el-form-item>
          </el-form>
        </el-col>
        <el-col :span="8">
          <div class="upload-container">
            <h3>图书封面</h3>
            <el-upload class="avatar-uploader" action="http://localhost:9111/BookManager/upload/uploadImg" :show-file-list="false" :on-success="handleAvatarSuccess" :before-upload="beforeAvatarUpload">
              <img v-if="form.bookimg" :src="$store.state.settings.baseApi + form.bookimg" class="avatar"/>
              <div v-else class="upload-placeholder"><i class="el-icon-plus"></i><div class="upload-text">点击上传</div></div>
            </el-upload>
          </div>
        </el-col>
      </el-row>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
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
  // 【问题3修复】监听路由参数，自动打开捐赠弹窗
  mounted() {
    if (this.$route.query.openDonate === 'true') {
      this.handleCreate();
    }
  },
  methods: {
    fetchData() {
      queryBookInfosByPage(this.queryParam).then((res) => {
        this.tableData = res.data;
        this.recordTotal = res.count;
      });
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
    // 点击添加/捐赠
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
    // 申请漂流
    handleBorrow(row) {
      this.$prompt('请输入申请漂流的理由/计划', '漂流申请', {
        confirmButtonText: '提交申请',
        cancelButtonText: '取消',
        inputPattern: /\S/,
        inputErrorMessage: '理由不能为空'
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
            this.$message.success("申请已提交，请等待管理员审核");
            this.fetchData();
          } else {
            this.$message.error("申请失败");
          }
        });
      }).catch(() => {});
    },
    submitForm() {
      if (this.formType === 0) {
        // 默认设置库存等于总数
        this.form.inventory = this.form.bookcount;
        addBookInfo(this.form).then((res) => {
          if (res === 1) {
            this.$message.success(this.roleIsAdmin ? "添加成功" : "捐赠成功！");
            this.handleShowAll();
          } else {
            this.$message.error("操作失败");
          }
          this.dialogFormVisible = false;
        });
      } else if (this.formType === 1) {
        updateBookInfo(this.form).then((res) => {
          if (res === 1 || res === 0) {
            this.$message.success("更新成功");
            this.fetchData();
          } else {
            this.$message.error("更新失败");
          }
          this.dialogFormVisible = false;
        });
      }
    },
    handleDelete(row) {
      this.$confirm("确定要删除该条记录吗?", "提示", { type: "warning" }).then(() => {
        deleteBookInfo(row).then((res) => {
          if (res === 1) {
            this.$message.success("删除成功");
            this.fetchData();
          } else if (res === -1) {
            this.$message.error("存在未归还记录，无法删除");
          } else {
            this.$message.error("删除失败");
          }
        });
      });
    },
    handleDeleteSome() {
      this.$confirm("确定要删除这些记录吗?", "提示", { type: "warning" }).then(() => {
        const items = this.$refs.multipleTable.selection;
        deleteBookInfos(items).then((res) => {
          if (res > 0) {
            this.$message.success("删除成功");
            this.fetchData();
          } else {
            this.$message.error("删除失败");
          }
        });
      });
    },
    handleSelectionChange(selection) {
      this.selectedRows = selection;
    },
  },
  data() {
    return {
      tableData: [],
      recordTotal: 0,
      typeData: [],
      queryParam: { page: 1, limit: 10, bookname: null, bookauthor: null, booktypeid: null },
      dialogFormVisible: false,
      formType: 0,
      form: { bookid: null, bookname: "", bookauthor: "", bookprice: 0, booktypeid: 1, bookdesc: "", bookcount: 1, inventory: 1, bookimg: "" },
      rules: {
        bookname: [{ required: true, message: "请输入图书名称", trigger: "blur" }],
        bookauthor: [{ required: true, message: "请输入作者", trigger: "blur" }],
        bookcount: [{ required: true, message: "请输入数量", trigger: "blur" }],
      },
      viewMode: "table",
      tableLoading: false,
      selectedRows: [],
    };
  },
  computed: {
    ...mapGetters(["id", "name", "roles"]),
    formTitle() { return this.formType === 0 ? "添加/捐赠图书" : "编辑图书"; },
    roleIsAdmin() { return this.roles[0] === "admin"; },
  },
};
</script>

<style lang="scss" scoped>
.app-container { padding: 20px; background-color: #f5f7fa; min-height: calc(100vh - 84px); }
.filter-container { margin-bottom: 20px; .search-form { .form-row { display: flex; flex-wrap: wrap; margin-bottom: 15px; } .form-item-container { display: flex; align-items: center; margin-right: 20px; margin-bottom: 10px; .form-label { white-space: nowrap; margin-right: 10px; color: #606266; font-size: 14px; } .form-input { width: 180px; } } } .action-buttons { margin-bottom: 0; .el-button { margin-right: 10px; margin-bottom: 5px; } } }
.control-panel { display: flex; justify-content: flex-end; margin-bottom: 20px; }
.book-card { margin-bottom: 24px; height: 520px; overflow: hidden; transition: all 0.3s; border-radius: 8px; display: flex; flex-direction: column; &:hover { transform: translateY(-5px); box-shadow: 0 5px 15px rgba(0,0,0,0.1); } .book-cover { position: relative; height: 300px; background-color: #f9f9f9; display: flex; align-items: center; justify-content: center; overflow: hidden; padding: 15px; .el-image { max-width: 100%; max-height: 270px; } .book-status { position: absolute; top: 10px; right: 10px; z-index: 1; } } .book-info { padding: 15px; flex: 1; display: flex; flex-direction: column; .book-title { font-size: 16px; font-weight: bold; margin: 0 0 10px 0; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; } .book-meta { display: flex; justify-content: space-between; font-size: 14px; color: #606266; margin-bottom: 10px; } .book-desc { font-size: 13px; color: #909399; margin: 0 0 15px 0; height: 60px; overflow: hidden; display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical; flex: 1; } .book-actions { display: flex; justify-content: flex-end; margin-top: auto; .el-button { margin-left: 8px; } } } }
.upload-container { text-align: center; .avatar-uploader { border: 1px dashed #d9d9d9; border-radius: 8px; cursor: pointer; position: relative; overflow: hidden; width: 180px; height: 240px; margin: 0 auto 10px; &:hover { border-color: #409EFF; } .avatar { width: 100%; height: 100%; display: block; object-fit: cover; } .upload-placeholder { width: 100%; height: 100%; display: flex; flex-direction: column; justify-content: center; align-items: center; i { font-size: 28px; color: #8c939d; margin-bottom: 10px; } } } }
.book-price { color: #F56C6C; font-weight: bold; }
</style>
