<template>
  <div class="app-container">
    <div class="filter-container" style="margin-bottom: 15px">
      <el-input
        v-model="queryParam.username"
        placeholder="请输入用户名或学号"
        style="width: 200px"
        class="filter-item"
        @keyup.enter.native="handleFilter"
      />
      <br /><br />
      <el-button
        v-waves
        class="filter-item"
        type="primary"
        style="font-size: 18px"
        size="small"
        @click="handleFilter"
      >
        <i class="iconfont icon-r-find" style="font-size: 22px"></i>
        搜索
      </el-button>

      <el-button
        class="filter-item"
        style="margin-left: 10px; font-size: 18px"
        type="primary"
        size="small"
        @click="handleCreate"
      >
        <i class="iconfont icon-r-add" style="font-size: 22px"></i>
        添加用户
      </el-button>
      <el-button
        class="filter-item"
        style="margin-left: 10px; font-size: 18px"
        size="small"
        type="danger"
        @click="handleDeleteSome"
      >
        <i class="iconfont icon-r-delete" style="font-size: 22px"></i>
        批量删除
      </el-button>
    </div>

    <el-dialog
      :title="formTitle"
      :visible.sync="dialogFormVisible"
      width="30%"
      :close-on-click-modal="false"
    >
      <el-form
        :model="form"
        :rules="rules"
        ref="ruleForm"
        label-width="100px"
      >
        <el-form-item label="学号" prop="studentid">
          <el-input
            v-model="form.studentid"
            placeholder="请输入学号（登录账号）"
            :disabled="formType === 1"
          ></el-input>
        </el-form-item>

        <el-form-item label="姓名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户姓名"></el-input>
        </el-form-item>

        <el-form-item label="密码" prop="userpassword">
          <el-input v-model="form.userpassword" placeholder="请输入登录密码"></el-input>
        </el-form-item>

        <el-form-item label="身份" prop="isadmin">
          <el-radio-group v-model="form.isadmin">
            <el-radio :label="1">管理员</el-radio>
            <el-radio :label="0">读者</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button
          @click="dialogFormVisible = false"
          style="font-size: 18px"
        >
          <i class="iconfont icon-r-left" style="font-size: 22px"></i>
          取 消</el-button
        >
        <el-button
          type="primary"
          @click="submitForm"
          style="font-size: 18px"
        >
          <i class="iconfont icon-r-yes" style="font-size: 22px"></i>
          确 定</el-button
        >
      </div>
    </el-dialog>

    <el-table
      ref="multipleTable"
      :data="tableData"
      size="small"
      border
      stripe
      style="width: 100%"
    >
      <el-table-column fixed type="selection" width="55" align="center">
      </el-table-column>
      <el-table-column fixed prop="userid" label="序号" width="80" align="center">
      </el-table-column>
      <el-table-column
        prop="studentid"
        label="学号"
        width="150"
        align="center"
        show-overflow-tooltip
      >
      </el-table-column>
      <el-table-column
        prop="username"
        label="姓名"
        min-width="120"
        align="center"
        show-overflow-tooltip
      >
      </el-table-column>
      <el-table-column
        prop="userpassword"
        label="密码"
        min-width="120"
        align="center"
        show-overflow-tooltip
      >
      </el-table-column>
      <el-table-column label="身份" width="100" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.isadmin === 1" type="warning" size="small">管理员</el-tag>
          <el-tag v-else type="success" size="small">读者</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === 0" type="info" size="small">待审核</el-tag>
          <el-tag v-else-if="scope.row.status === 1" type="success" size="small">正常</el-tag>
          <el-tag v-else type="danger" size="small">禁用</el-tag>
        </template>
      </el-table-column>
      <el-table-column fixed="right" label="操作" width="200" align="center">
        <template slot-scope="scope">
          <el-button
            @click="handleUpdate(scope.row)"
            type="primary"
            size="mini"
            icon="el-icon-edit"
          >
            编辑</el-button
          >
          <el-button
            @click="handleDelete(scope.row, scope.$index)"
            type="danger"
            size="mini"
            icon="el-icon-delete"
          >
            删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      background
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page.sync="queryParam.page"
      :page-sizes="[5, 10, 20, 50]"
      :page-size="queryParam.limit"
      layout="total, sizes, prev, pager, next, jumper"
      :total="recordTotal"
      style="margin-top: 15px; text-align: right;"
    >
    </el-pagination>
  </div>
</template>

<script>
import { mapGetters } from "vuex";
import waves from "@/directive/waves";

import {
  getCount,
  queryUsersByPage,
  addUser,
  deleteUser,
  deleteUsers,
  updateUser,
} from "@/api/user";

export default {
  name: "Bookinfo",
  directives: { waves },
  created() {
    this.fetchData();
  },
  methods: {
    fetchData() {
      queryUsersByPage(this.queryParam).then((res) => {
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
    handleCreate() {
      this.formType = 0;
      this.form = {
        userid: null,
        username: "",
        studentid: "", // 初始化学号
        userpassword: "",
        isadmin: 0, // 默认添加读者
        status: 1
      };
      this.dialogFormVisible = true;
      this.$nextTick(() => {
        this.$refs.ruleForm.clearValidate();
      });
    },
    handleUpdate(row) {
      this.formType = 1;
      this.form = {
        userid: row.userid,
        username: row.username,
        studentid: row.studentid, // 读取学号
        userpassword: row.userpassword,
        isadmin: row.isadmin,
        status: row.status
      };
      this.dialogFormVisible = true;
      this.$nextTick(() => {
        this.$refs.ruleForm.clearValidate();
      });
    },
    submitForm() {
      this.$refs.ruleForm.validate((valid) => {
        if (valid) {
          if (this.formType === 0) {
            addUser(this.form).then((res) => {
              if (res === 1) {
                this.$message.success("添加成功");
                this.fetchData();
              } else {
                this.$message.error("添加失败，可能学号已存在");
              }
              this.dialogFormVisible = false;
            });
          } else if (this.formType === 1) {
            updateUser(this.form).then((res) => {
              if (res === 1) {
                this.$message.success("更新成功");
                this.fetchData();
              } else {
                this.$message.error("更新失败");
              }
              this.dialogFormVisible = false;
            });
          }
        }
      });
    },
    handleDelete(row, index) {
      this.$confirm("确定要删除该用户吗? 删除后无法恢复。", "警示", {
        confirmButtonText: "确定删除",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        deleteUser(row).then((res) => {
          if (res === 1) {
            this.$message.success("删除成功");
            this.fetchData();
          } else if (res === -1) {
            this.$message.error("该用户有未归还图书，无法删除");
          } else if (res === -2) {
            this.$message.error("无法删除管理员账户");
          } else {
            this.$message.error("删除失败");
          }
        });
      });
    },
    handleDeleteSome() {
      if (this.$refs.multipleTable.selection.length === 0) {
        this.$message.warning("请先选择要删除的用户");
        return;
      }
      this.$confirm("确定要批量删除选中用户吗?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        const items = this.$refs.multipleTable.selection;
        deleteUsers(items).then((res) => {
          if (res > 0) {
            this.$message.success("成功删除 " + res + " 条记录");
            this.fetchData();
          } else {
            this.$message.error("批量删除失败");
          }
        });
      });
    },
  },
  data() {
    return {
      tableData: [],
      recordTotal: 0,
      queryParam: {
        page: 1,
        limit: 10,
        username: null,
      },
      dialogFormVisible: false,
      formType: 0,
      form: {
        userid: null,
        username: "",
        studentid: "",
        userpassword: "",
        isadmin: 0,
      },
      rules: {
        studentid: [
          { required: true, message: "请输入学号", trigger: "blur" }
        ],
        username: [
          { required: true, message: "请输入姓名", trigger: "blur" }
        ],
        userpassword: [
          { required: true, message: "请输入密码", trigger: "blur" }
        ],
      },
    };
  },
  computed: {
    ...mapGetters(["id", "name", "roles"]),
    formTitle() {
      return this.formType === 0 ? "添加用户" : "编辑用户";
    },
  },
};
</script>

<style scoped>
/* 保持原有样式 */
</style>
