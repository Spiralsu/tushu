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
          <div class="action-btn" @click="openProfileDialog">
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

    <el-dialog title="个人中心" :visible.sync="profileDialogVisible" width="400px" center custom-class="glass-dialog">
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
            <span class="label" style="padding-top:2px;">学号 / 账号：</span>
            <span class="value" style="color: #409eff; font-size: 16px;">{{ username }}</span>
          </div>

          <div class="detail-item" style="margin-top: 15px; border-top: 1px dashed #dcdfe6; padding-top: 15px;">
            <span class="label" style="padding-top:4px;">当前信用分：</span>
            <span class="value">
              <el-tag :type="myCreditScore >= 90 ? 'success' : (myCreditScore >= 60 ? 'warning' : 'danger')" effect="dark" style="font-size: 16px; border-radius: 10px;">
                <i class="el-icon-s-data"></i> {{ myCreditScore }} 分
              </el-tag>
            </span>
          </div>
          <div style="font-size: 12px; color: #909399; margin-top: 8px;" v-if="myCreditScore < 100">
            <i class="el-icon-warning-outline"></i> 信用低于 60 分将被限制借阅书籍
          </div>
        </div>

        <div class="profile-actions">
          <el-button type="primary" round style="width: 100%;" @click="goToPassword">修改安全密码</el-button>
          <el-button type="success" round style="width: 100%; margin-top: 15px; margin-left: 0; box-shadow: 0 4px 10px rgba(103,194,58,0.3);" icon="el-icon-chat-dot-round" @click="openWxBind">管理微信推送</el-button>
        </div>
      </div>
    </el-dialog>

    <el-dialog title="📲 微信实时推送管理" :visible.sync="wxDialogVisible" width="550px" custom-class="glass-dialog" append-to-body>
      <div style="text-align: center; padding: 10px;">
        <i class="el-icon-chat-dot-round" style="font-size: 45px; color: #07c160; margin-bottom: 10px;"></i>

        <div style="margin: 20px 0; padding: 20px; background: #f0f7ff; border-radius: 12px; border: 1px dashed #a1c4fd;">
          <h4 style="margin-top: 0; color: #409eff; font-size: 16px;"><i class="el-icon-link"></i> 第一步：获取专属 UID</h4>
          <p style="font-size: 13px; color: #606266; line-height: 1.6; margin-bottom: 15px;">
            请点击下方按钮，在打开的网页中<strong>使用微信扫码关注</strong>。<br>
            关注后，微信公众号将自动给您发送一串您的专属 UID 字符。
          </p>
          <a href="https://wxpusher.zjiecode.com/wxuser/?type=1&id=120258#/follow" target="_blank" style="text-decoration: none;">
            <el-button type="primary" round icon="el-icon-position" style="box-shadow: 0 4px 10px rgba(64,158,255,0.3);">
              点此处获取微信 UID
            </el-button>
          </a>
        </div>

        <div style="text-align: left; background: #f0f9eb; padding: 15px; border-radius: 12px; border: 1px solid #e1f3d8;">
          <h4 style="margin-top: 0; color: #67C23A; font-size: 15px;"><i class="el-icon-edit-outline"></i> 第二步：绑定账号</h4>
          <p style="color: #606266; font-size: 13px; margin: 0; line-height: 1.8;">
            将您在微信里收到的 <strong>UID_</strong> 开头的字符完整复制，填入下方输入框进行绑定或换绑。
          </p>
        </div>

        <el-input v-model="wxOpenId" placeholder="请粘贴您的专属 UID (格式如: UID_xXyY...)" style="margin-top: 20px;" clearable>
          <template slot="prepend"><i class="el-icon-key"></i></template>
        </el-input>
      </div>

      <div slot="footer" style="display: flex; justify-content: space-between; align-items: center; padding: 0 10px;">
        <el-button type="text" style="color: #F56C6C;" @click="submitUnbindWx">
          <i class="el-icon-delete"></i> 解除当前绑定
        </el-button>
        <div>
          <el-button round @click="wxDialogVisible = false">取消</el-button>
          <el-button type="success" round @click="submitBindWx" style="background-color: #07c160; border-color: #07c160; box-shadow: 0 4px 10px rgba(7,193,96,0.3);">确 认 绑 定</el-button>
        </div>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import { mapGetters } from 'vuex'
// 【新增】：引入 queryUsersByPage 用于抓取最新的信用分
import { getCount as getBookCount, queryBookInfosByPage } from "@/api/bookinfo";
import { getCount as getBorrowCount } from "@/api/borrow";
import { getCount as getUserCount, queryUsersByPage } from "@/api/user";
import request from '@/utils/request';

export default {
  name: 'Dashboard',
  computed: {
    ...mapGetters(['id', 'name', 'roles', 'username'])
  },
  data() {
    return {
      bookCount: 0, borrowCount: 0, userCount: 0,
      recentBooks: [],
      msgDialogVisible: false, profileDialogVisible: false, wxDialogVisible: false,
      msgLoading: false, messages: [], wxOpenId: '',

      // 【新增】：存储当前用户的信用分
      myCreditScore: 100
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

// 【核心新增】：打开个人中心时，去查一下真实信用分
    openProfileDialog() {
      this.profileDialogVisible = true;
      // 【修复报错】：必须加上 page: 1，否则后端的分页工具会报空指针异常
      queryUsersByPage({ username: this.username, page: 1, limit: 1 }).then(res => {
        const list = res.data || res.rows || res;
        if (list && list.length > 0) {
          this.myCreditScore = list[0].creditScore != null ? list[0].creditScore : 100;
        }
      }).catch(() => {});
    },

    handleOpenMessage() {
      this.msgDialogVisible = true;
      this.msgLoading = true;
      request({ url: '/message/getByUserId', method: 'get', params: { userid: this.id } }).then(res => {
        const msgList = res.data || res || [];
        this.messages = msgList.map(msg => {
          return { content: msg.content, time: new Date(msg.createtime).toLocaleString(), type: msg.isread === 0 ? 'primary' : 'info' }
        });
        this.msgLoading = false;
      }).catch(err => { this.msgLoading = false; });
    },

    goToPassword() {
      this.profileDialogVisible = false;
      this.$router.push('/other/password');
    },

    openWxBind() {
      this.profileDialogVisible = false;
      setTimeout(() => { this.wxDialogVisible = true; }, 300);
    },

    submitBindWx() {
      if(!this.wxOpenId || !this.wxOpenId.startsWith('UID_')) return this.$message.warning("格式不正确！请填入以 UID_ 开头的完整字符。");
      request({ url: '/borrow/bindWx', method: 'post', params: { userId: this.id, openId: this.wxOpenId } }).then(res => {
        if(res.code === 0 || res === 1) {
          this.$message.success("🎉 微信推送绑定成功！快去试试收发消息吧！");
          this.wxDialogVisible = false;
        } else { this.$message.error(res.msg || "绑定失败，请重试"); }
      });
    },

    submitUnbindWx() {
      this.$confirm('确定要解除微信绑定吗？解除后，当有人申请您的书时，您将无法收到微信提醒！', '解绑确认', { confirmButtonText: '确定解绑', cancelButtonText: '暂不解绑', type: 'warning' }).then(() => {
        request({ url: '/borrow/unbindWx', method: 'post', params: { userId: this.id } }).then(res => {
          if(res.code === 0 || res === 1) {
            this.$message.success("已成功解除微信绑定！");
            this.wxOpenId = ''; this.wxDialogVisible = false;
          } else { this.$message.error(res.msg || "解绑失败"); }
        });
      });
    }
  }
}
</script>

<style lang="scss" scoped>
/* 样式与之前保持完全一致 */
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
.profile-card { text-align: center; padding: 10px; .avatar-wrapper { width: 80px; height: 80px; margin: 0 auto 15px; border-radius: 50%; background: #ecf5ff; display: flex; align-items: center; justify-content: center; font-size: 40px; color: #409eff; } .username { font-size: 20px; margin: 0 0 10px 0; color: #303133; } .role-tag { margin-bottom: 25px; } .profile-details { background: #f7f9fc; border-radius: 8px; padding: 15px; margin-bottom: 25px; .detail-item { font-size: 14px; display: flex; justify-content: center; gap: 10px; align-items: center; .label { color: #909399; } .value { font-weight: bold; color: #303133; } } } }

::v-deep .el-dialog { border-radius: 16px; overflow: hidden; }
::v-deep .glass-dialog { border-radius: 20px !important; box-shadow: 0 20px 50px rgba(0, 0, 0, 0.1) !important; }
::v-deep .el-dialog__body { max-height: 450px; overflow-y: auto; }
::v-deep .el-dialog__body::-webkit-scrollbar { width: 6px; }
::v-deep .el-dialog__body::-webkit-scrollbar-thumb { background-color: #dcdfe6; border-radius: 3px; }
</style>
