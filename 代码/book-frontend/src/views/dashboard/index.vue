<template>
  <div class="dashboard-container">
    <div class="bento-grid">

      <div class="bento-item welcome-banner">
        <div class="banner-content">
          <h2>你好，{{ name }}！欢迎回到校园旧书漂流站</h2>
          <p>每一本书都有一段旅程，今天你想让哪本书开启新的漂流？</p>
          <div class="banner-actions">
            <el-button type="primary" round icon="el-icon-position" @click="$router.push('/bookmanage/bookinfo')">我要漂流</el-button>
          </div>
        </div>
        <div class="banner-illustration">
          <i class="el-icon-collection" style="font-size: 100px; opacity: 0.2; color: #409eff;"></i>
        </div>
      </div>

      <div class="bento-item stat-card">
        <div class="stat-icon bg-blue"><i class="el-icon-reading"></i></div>
        <div class="stat-info">
          <div class="stat-value">{{ bookCount }}</div>
          <div class="stat-label">全校在漂书籍</div>
        </div>
      </div>

      <div class="bento-item stat-card">
        <div class="stat-icon bg-green"><i class="el-icon-refresh"></i></div>
        <div class="stat-info">
          <div class="stat-value">{{ borrowCount }}</div>
          <div class="stat-label">成功传递次数</div>
        </div>
      </div>

      <div class="bento-item stat-card">
        <div class="stat-icon bg-orange"><i class="el-icon-user"></i></div>
        <div class="stat-info">
          <div class="stat-value">{{ userCount }}</div>
          <div class="stat-label">参与漂流师生</div>
        </div>
      </div>

      <div class="bento-item activity-feed">
        <div class="item-header">
          <h3>最新上架漂流书籍</h3>
          <el-button type="text" @click="$router.push('/bookmanage/bookinfo')">查看书库</el-button>
        </div>
        <div class="recent-books">
          <div v-for="book in recentBooks" :key="book.bookid" class="recent-book-item">
            <el-image
              :src="$store.state.settings.baseApi + book.bookimg"
              class="book-mini-cover"
              fit="cover">
              <div slot="error" class="image-error"><i class="el-icon-picture-outline"></i></div>
            </el-image>
            <div class="book-meta">
              <span class="book-name">{{ book.bookname }}</span>
              <span class="book-time">发布者共享</span>
            </div>
            <el-button size="mini" round class="go-look-btn" @click="$router.push('/bookmanage/bookinfo')">去看看</el-button>
          </div>
          <div v-if="recentBooks.length === 0" class="empty-tips">暂无新书上架</div>
        </div>
      </div>

      <div class="bento-item quick-actions">
        <div class="item-header">
          <h3>常用功能</h3>
        </div>
        <div class="action-grid">
          <div class="action-btn" @click="$router.push('/bookmanage/bookinfo?openDonate=true')">
            <i class="el-icon-upload el-icon--upload"></i>
            <span>发布旧书</span>
          </div>
          <div class="action-btn" @click="$router.push('/bookmanage/borrow')">
            <i class="el-icon-document-checked"></i>
            <span>我的借阅</span>
          </div>
          <div class="action-btn" @click="handleOpenMessage">
            <i class="el-icon-bell"></i>
            <span>站内消息通知</span>
          </div>
          <div class="action-btn" @click="profileDialogVisible = true">
            <i class="el-icon-user"></i>
            <span>个人中心</span>
          </div>
        </div>
      </div>

    </div>

    <el-dialog title="站内消息通知" :visible.sync="msgDialogVisible" width="450px" style="border-radius: 16px;">
      <div v-loading="msgLoading" style="min-height: 120px;">
        <el-timeline v-if="messages.length > 0">
          <el-timeline-item v-for="(msg, index) in messages" :key="index" :type="msg.type" :timestamp="msg.time">
            {{ msg.content }}
          </el-timeline-item>
        </el-timeline>
        <el-empty v-if="!msgLoading && messages.length === 0" description="暂无新的通知消息"></el-empty>
      </div>
    </el-dialog>

    <el-dialog title="个人中心" :visible.sync="profileDialogVisible" width="400px" center>
      <div class="profile-card">
        <div class="avatar-wrapper"><i class="el-icon-user-solid"></i></div>
        <h3 class="username">书友 {{ name || '未知用户' }}</h3>
        <p class="role-tag">
          <el-tag size="small" :type="roles[0] === 'admin' ? 'danger' : 'success'">
            {{ roles[0] === 'admin' ? '系统管理员' : '漂流书友' }}
          </el-tag>
        </p>

        <div class="profile-details">
          <div class="detail-item">
            <span class="label">学号 / 账号：</span>
            <span class="value" style="color: #409eff; font-size: 16px;">{{ username }}</span>
          </div>
        </div>

        <div class="profile-actions">
          <el-button type="primary" round style="width: 100%; margin-top: 15px;" @click="goToPassword">修改安全密码</el-button>
        </div>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { getCount as getBookCount, queryBookInfosByPage } from "@/api/bookinfo";
import { getCount as getBorrowCount } from "@/api/borrow";
import { getCount as getUserCount } from "@/api/user";
import request from '@/utils/request';

export default {
  name: 'Dashboard',
  computed: {
    // 引入 username
    ...mapGetters(['id', 'name', 'roles', 'username'])
  },
  data() {
    return {
      bookCount: 0,
      borrowCount: 0,
      userCount: 0,
      recentBooks: [],
      msgDialogVisible: false,
      profileDialogVisible: false,
      msgLoading: false,
      messages: []
    }
  },
  created() {
    this.fetchRealData()
  },
  methods: {
    async fetchRealData() {
      try { this.bookCount = await getBookCount() || 0; } catch (e) {}
      try { this.borrowCount = await getBorrowCount() || 0; } catch (e) {}
      try { this.userCount = await getUserCount() || 0; } catch (e) {}
      try {
        const resRecent = await queryBookInfosByPage({ page: 1, limit: 4 });
        this.recentBooks = resRecent.data || [];
      } catch (e) {}
    },

    handleOpenMessage() {
      this.msgDialogVisible = true;
      this.msgLoading = true;
      request({
        url: '/message/getByUserId',
        method: 'get',
        params: { userid: this.id }
      }).then(res => {
        const msgList = res.data || res || [];
        this.messages = msgList.map(msg => {
          return {
            content: msg.content,
            time: new Date(msg.createtime).toLocaleString(),
            type: msg.isread === 0 ? 'primary' : 'info'
          }
        });
        this.msgLoading = false;
      }).catch(err => {
        console.error("获取消息失败", err);
        this.msgLoading = false;
      });
    },

    goToPassword() {
      this.profileDialogVisible = false;
      this.$router.push('/other/password');
    }
  }
}
</script>

<style lang="scss" scoped>
/* 样式保持不变，直接复制之前的即可，这里简略以节省篇幅，确保覆盖时样式完整 */
.dashboard-container { padding: 10px; background-color: #f7f9fc; min-height: calc(100vh - 50px); }
.bento-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 24px; }
.bento-item { background: #ffffff; border-radius: 20px; padding: 24px; box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03); border: 1px solid #f0f2f5; transition: transform 0.2s, box-shadow 0.2s; &:hover { transform: translateY(-2px); box-shadow: 0 8px 30px rgba(0, 0, 0, 0.06); } }
.welcome-banner { grid-column: span 3; display: flex; justify-content: space-between; align-items: center; background: linear-gradient(135deg, #f0f7ff 0%, #e6f0ff 100%); border: none; .banner-content { flex: 1; h2 { margin: 0 0 12px 0; color: #1f2d3d; font-size: 24px; } p { color: #5e6d82; margin-bottom: 24px; font-size: 15px; } } }
.stat-card { display: flex; align-items: center; gap: 20px; .stat-icon { width: 60px; height: 60px; border-radius: 16px; display: flex; align-items: center; justify-content: center; font-size: 28px; color: #fff; &.bg-blue { background: #409eff; box-shadow: 0 4px 12px rgba(64,158,255,0.3); } &.bg-green { background: #67c23a; box-shadow: 0 4px 12px rgba(103,194,58,0.3); } &.bg-orange { background: #e6a23c; box-shadow: 0 4px 12px rgba(230,162,60,0.3); } } .stat-info { .stat-value { font-size: 28px; font-weight: 700; color: #303133; margin-bottom: 4px; } .stat-label { font-size: 14px; color: #909399; } } }
.activity-feed { grid-column: span 2; }
.quick-actions { grid-column: span 1; }
.item-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; h3 { margin: 0; font-size: 18px; color: #303133; font-weight: 600; } }
.recent-book-item { display: flex; align-items: center; padding: 12px 0; border-bottom: 1px dashed #ebeef5; .book-mini-cover { width: 40px; height: 55px; border-radius: 4px; margin-right: 15px; background: #f0f2f5; } .book-meta { flex: 1; display: flex; flex-direction: column; .book-name { font-size: 15px; font-weight: 500; color: #333; } .book-time { font-size: 12px; color: #999; margin-top: 4px; } } &:last-child { border-bottom: none; } }
.go-look-btn { background-color: #ecf5ff; color: #409eff; border: none; font-weight: 600; padding: 8px 16px; transition: all 0.3s; &:hover { background-color: #409eff; color: #ffffff; } }
.empty-tips { text-align: center; color: #999; font-size: 13px; margin-top: 20px; }
.action-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 16px; .action-btn { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 20px; background: #f7f9fc; border-radius: 12px; cursor: pointer; transition: background 0.2s; i { font-size: 28px; color: #409eff; margin-bottom: 12px; } span { font-size: 14px; color: #606266; font-weight: 500; } &:hover { background: #ecf5ff; } } }
.profile-card { text-align: center; padding: 10px; .avatar-wrapper { width: 80px; height: 80px; margin: 0 auto 15px; border-radius: 50%; background: #ecf5ff; display: flex; align-items: center; justify-content: center; font-size: 40px; color: #409eff; } .username { font-size: 20px; margin: 0 0 10px 0; color: #303133; } .role-tag { margin-bottom: 25px; } .profile-details { background: #f7f9fc; border-radius: 8px; padding: 15px; margin-bottom: 25px; .detail-item { font-size: 14px; display: flex; justify-content: center; gap: 10px; .label { color: #909399; } .value { font-weight: bold; color: #303133; } } } }
::v-deep .el-dialog { border-radius: 16px; overflow: hidden; }
</style>
