<template>
  <div class="app-container">
    <div class="filter-container" style="margin-bottom: 15px">
      <el-input v-permission="['admin']" v-model="queryParam.username" placeholder="用户名" style="width: 200px" class="filter-item" @keyup.enter.native="handleFilter"/>
      <el-input v-model="queryParam.bookname" placeholder="图书名" style="width: 200px" class="filter-item" @keyup.enter.native="handleFilter"/>
      <br /><br />
      <el-button v-waves class="filter-item" type="primary" size="small" style="font-size: 18px" @click="handleFilter">
        <i class="iconfont icon-r-find" style="font-size: 22px"></i> 搜索
      </el-button>
      <el-button v-permission="['admin']" class="filter-item" style="margin-left: 10px; font-size: 18px" size="small" type="danger" @click="handleDeleteSome">
        <i class="iconfont icon-r-delete" style="font-size: 22px"></i> 批量删除
      </el-button>
    </div>

    <el-table ref="multipleTable" :data="tableData" border size="small" style="width: 100%">
      <el-table-column fixed type="selection" width="55"></el-table-column>
      <el-table-column fixed prop="borrowid" label="序号" width="80"></el-table-column>
      <el-table-column prop="username" label="漂流者" show-overflow-tooltip></el-table-column>
      <el-table-column prop="bookname" label="图书名" show-overflow-tooltip></el-table-column>
      <el-table-column prop="borrowreason" label="申请理由" show-overflow-tooltip></el-table-column>
      <el-table-column prop="applytime" label="申请时间" width="140">
        <template slot-scope="scope">{{ formatDate(scope.row.applytime) }}</template>
      </el-table-column>

      <el-table-column label="当前状态" width="100" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.state === 0" type="warning">待审核</el-tag>
          <el-tag v-else-if="scope.row.state === 1" type="success">漂流中</el-tag>
          <el-tag v-else-if="scope.row.state === 2" type="info">已归还</el-tag>
          <el-tag v-else type="danger">已驳回</el-tag>
        </template>
      </el-table-column>

      <el-table-column fixed="right" label="操作" width="280">
        <template slot-scope="scope">
          <div v-if="roleIsAdmin && scope.row.state === 0" style="display:inline-block; margin-right:5px">
            <el-button @click="handleAudit(scope.row, 1)" type="success" size="small">通过</el-button>
            <el-button @click="handleAudit(scope.row, 3)" type="warning" size="small">驳回</el-button>
          </div>

          <el-button v-if="scope.row.state === 1" @click="handleReturn(scope.row)" type="primary" size="small">归还</el-button>

          <el-button v-permission="['admin']" @click="handleDelete(scope.row, scope.$index)" type="danger" size="small">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination background @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page.sync="queryParam.page" :page-sizes="[5, 10, 20, 50]" :page-size="queryParam.limit" layout="total, sizes, prev, pager, next, jumper" :total="recordTotal" style="margin-top: 15px"></el-pagination>
  </div>
</template>

<script>
import { mapGetters } from "vuex";
import permission from "@/directive/permission/index.js";
import waves from "@/directive/waves";
import { queryBorrowsByPage, deleteBorrow, deleteBorrows, returnBook } from "@/api/borrow";
// 引入 request 调用审核接口
import request from "@/utils/request";

export default {
  name: "BorrowIndex",
  directives: { waves, permission },
  created() {
    this.fetchData();
  },
  methods: {
    fetchData() {
      queryBorrowsByPage(this.queryParam).then((res) => {
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
    // 删除
    handleDelete(row, index) {
      this.$confirm("确定要删除该条记录吗?", "提示", { confirmButtonText: "确定", cancelButtonText: "取消", type: "warning" }).then(() => {
        deleteBorrow(row).then((res) => {
          if (res === 1) {
            this.$message.success("删除成功");
            this.fetchData();
          } else {
            this.$message.error("删除失败");
          }
        });
      });
    },
    handleDeleteSome() {
      this.$confirm("确定要删除这些记录吗?", "提示", { confirmButtonText: "确定", cancelButtonText: "取消", type: "warning" }).then(() => {
        const items = this.$refs.multipleTable.selection;
        deleteBorrows(items).then((res) => {
          if (res > 0) {
            this.$message.success("删除" + res + "条记录成功");
            this.fetchData();
          } else {
            this.$message.error("删除失败");
          }
        });
      });
    },
    // 归还
    handleReturn(row) {
      this.$confirm("确定要归还图书吗?", "提示", { confirmButtonText: "确定", cancelButtonText: "取消", type: "warning" }).then(() => {
        returnBook(row.borrowid, row.bookid).then((res) => {
          if (res === 1) {
            this.$message.success("归还成功");
            this.fetchData();
          } else {
            this.$message.error("归还失败");
          }
        });
      });
    },
    // 【核心修改】审核逻辑
    handleAudit(row, status) {
      let actionText = status === 1 ? "通过" : "驳回";
      this.$prompt(`确定要${actionText}该申请吗？如有需要请填写备注：`, '审核确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
      }).then(({ value }) => {
        request({
          url: '/borrow/audit',
          method: 'post',
          params: {
            borrowId: row.borrowid,
            state: status,
            feedback: value
          }
        }).then(res => {
          if(res.code === 0 || res === 1) {
            this.$message.success("操作成功");
            this.fetchData();
          } else {
            this.$message.error(res.msg || "操作失败");
          }
        });
      }).catch(() => {});
    },
    formatDate(time) {
      if(!time) return '';
      return time.replace("T", " ");
    }
  },
  data() {
    return {
      tableData: [],
      recordTotal: 0,
      queryParam: { page: 1, limit: 10, userid: null, username: null, bookname: null },
    };
  },
  computed: {
    ...mapGetters(["id", "name", "roles"]),
    roleIsAdmin() { return this.roles[0] === "admin"; },
  },
  watch: {
    "queryParam.userid": {
      immediate: true,
      handler() {
        if (!this.roleIsAdmin) {
          this.queryParam.userid = this.id;
        }
      },
    },
  },
};
</script>
