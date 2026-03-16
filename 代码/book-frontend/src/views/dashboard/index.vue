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
        <div class="banner-illustration"><i class="el-icon-collection" style="font-size: 100px; opacity: 0.2; color: #409eff;"></i></div>
      </div>
      <div class="bento-item stat-card"><div class="stat-icon bg-blue"><i class="el-icon-reading"></i></div><div class="stat-info"><div class="stat-value">{{ bookCount }}</div><div class="stat-label">全校在漂书籍</div></div></div>
      <div class="bento-item stat-card"><div class="stat-icon bg-green"><i class="el-icon-refresh"></i></div><div class="stat-info"><div class="stat-value">{{ borrowCount }}</div><div class="stat-label">成功传递次数</div></div></div>
      <div class="bento-item stat-card"><div class="stat-icon bg-orange"><i class="el-icon-user"></i></div><div class="stat-info"><div class="stat-value">{{ userCount }}</div><div class="stat-label">参与漂流师生</div></div></div>
      <div class="bento-item activity-feed">
        <div class="item-header"><h3>最新上架漂流书籍</h3><el-button type="text" @click="$router.push('/bookmanage/bookinfo')">查看书库</el-button></div>
        <div class="recent-books">
          <div v-for="book in recentBooks" :key="book.bookid" class="recent-book-item">
            <el-image :src="$store.state.settings.baseApi + book.bookimg" class="book-mini-cover" fit="cover"><div slot="error" class="image-error"><i class="el-icon-picture-outline"></i></div></el-image>
            <div class="book-meta"><span class="book-name">{{ book.bookname }}</span><span class="book-time">发布者共享</span></div>
            <el-button size="mini" round class="go-look-btn" @click="$router.push('/bookmanage/bookinfo')">去看看</el-button>
          </div>
          <div v-if="recentBooks.length === 0" class="empty-tips">暂无新书上架</div>
        </div>
      </div>
      <div class="bento-item quick-actions">
        <div class="item-header"><h3>常用功能</h3></div>
        <div class="action-grid">
          <div class="action-btn" @click="$router.push('/bookmanage/bookinfo?openDonate=true')"><i class="el-icon-upload el-icon--upload"></i><span>发布旧书</span></div>
          <div class="action-btn" @click="$router.push('/bookmanage/borrow')"><i class="el-icon-document-checked"></i><span>我的借阅</span></div>
          <div class="action-btn" @click="handleOpenMessage"><i class="el-icon-bell"></i><span>站内消息通知</span></div>
          <div class="action-btn" @click="openProfileDialog"><i class="el-icon-user"></i><span>个人中心</span></div>
        </div>
      </div>
    </div>

    <el-dialog title="站内消息通知" :visible.sync="msgDialogVisible" width="450px" style="border-radius: 16px;">
      <div v-loading="msgLoading" style="min-height: 120px;">
        <el-timeline v-if="messages.length > 0"><el-timeline-item v-for="(msg, index) in messages" :key="index" :type="msg.type" :timestamp="msg.time">{{ msg.content }}</el-timeline-item></el-timeline>
        <el-empty v-if="!msgLoading && messages.length === 0" description="暂无新的通知消息"></el-empty>
      </div>
    </el-dialog>

    <el-dialog title="个人中心" :visible.sync="profileDialogVisible" width="400px" center custom-class="glass-dialog">
      <div class="profile-card">
        <div class="avatar-wrapper"><i class="el-icon-user-solid"></i></div>
        <h3 class="username">书友 {{ name || '未知用户' }}</h3>
        <p class="role-tag"><el-tag size="small" :type="roles[0] === 'admin' ? 'danger' : 'success'">{{ roles[0] === 'admin' ? '系统管理员' : '漂流书友' }}</el-tag></p>
        <div class="profile-details">
          <div class="detail-item"><span class="label" style="padding-top:2px;">学号 / 账号：</span><span class="value" style="color: #409eff; font-size: 16px;">{{ username }}</span></div>
          <div class="detail-item" style="margin-top: 15px; border-top: 1px dashed #dcdfe6; padding-top: 15px; align-items: center;">
            <span class="label">当前信用分：</span>
            <span class="value">
              <el-tag :type="myCreditScore >= 90 ? 'success' : (myCreditScore >= 60 ? 'warning' : 'danger')" effect="dark" style="font-size: 16px; border-radius: 10px; cursor: pointer; display: flex; align-items: center; gap: 5px;" @click="ruleVisible = true">
                <i class="el-icon-s-data"></i> {{ myCreditScore }} 分 <i class="el-icon-question" style="font-size:14px;"></i>
              </el-tag>
            </span>
          </div>
          <div style="font-size: 12px; color: #909399; margin-top: 8px;" v-if="myCreditScore < 100"><i class="el-icon-warning-outline"></i> 信用低于 60 分将被限制借阅书籍</div>
        </div>
        <div class="profile-actions">
          <el-button
            type="primary"
            class="unified-btn primary-shadow"
            icon="el-icon-lock"
            @click="goToPassword">
            修改安全密码
          </el-button>

          <el-button
            type="success"
            class="unified-btn success-shadow"
            icon="el-icon-chat-dot-round"
            @click="openWxBind">
            管理微信推送
          </el-button>
        </div>
      </div>
    </el-dialog>

    <el-dialog title="📋 信用分流转与惩罚规则" :visible.sync="ruleVisible" width="450px" custom-class="glass-dialog">
      <div style="line-height: 1.8; color: #606266; font-size: 14px;">
        <p><strong>🌟 信用分回血（每日最高可+5分）：</strong><br/>1. 发布一本闲置旧书：<span style="color:#67C23A; font-weight:bold;">+2 分</span><br/>2. 成功将书交接给下一位书友：<span style="color:#67C23A; font-weight:bold;">+3 分</span></p>
        <p><strong>⚠️ 违规扣分记录：</strong><br/>1. 逾期未还：系统每日凌晨巡查，<span style="color:#F56C6C; font-weight:bold;">每日扣除 2 分</span><br/>2. 无视催还被管理员强裁：<span style="color:#F56C6C; font-weight:bold;">一次性扣 10 分</span></p>
        <div style="margin-top: 20px; padding: 12px; background: #fef0f0; border-radius: 8px; color: #F56C6C; font-size: 13px;">
          <i class="el-icon-warning"></i> <strong>极度警告：</strong><br/>当信用分低于 60 分时，系统将彻底锁死您的所有借阅权限！<br/>若需解封，请您携带相关证明线下联系辅导员或管理员进行处理！
        </div>
      </div>
      <div slot="footer" style="text-align:center;"><el-button round @click="ruleVisible = false" type="primary">我已了解规则</el-button></div>
    </el-dialog>

    <el-dialog title="📲 微信实时推送管理" :visible.sync="wxDialogVisible" width="500px" custom-class="glass-dialog" append-to-body>
      <div class="wx-dialog-body">
        <div class="status-header">
          <i class="el-icon-chat-dot-round wx-icon" :class="{'is-bound': isWxBound}"></i>
          <div class="status-text">
            当前状态：
            <el-tag :type="isWxBound ? 'success' : 'info'" size="small" effect="dark" class="status-badge">
              <i :class="isWxBound ? 'el-icon-success' : 'el-icon-warning'"></i>
              {{ isWxBound ? '已绑定' : '未绑定' }}
            </el-tag>
          </div>
        </div>

        <div class="step-card primary-step">
          <h4 class="step-title"><i class="el-icon-link"></i> 第一步：获取专属 UID</h4>
          <p class="step-desc">请点击下方按钮，在打开的网页中使用微信扫码关注。<br>关注后，公众号将自动给您发送一串您的专属 UID。</p>
          <div style="margin-top: 10px;">
            <el-button type="primary" class="unified-btn primary-shadow step-btn" icon="el-icon-position" @click="openWxPusherWindow">点此处打开扫码窗口</el-button>
          </div>
        </div>

        <div class="step-card success-step">
          <h4 class="step-title"><i class="el-icon-edit-outline"></i> 第二步：绑定账号</h4>
          <p class="step-desc">将您在微信里收到的 <strong>UID_</strong> 开头的字符完整复制并填入下方。</p>
        </div>

        <el-input v-model="wxOpenId" placeholder="请粘贴您的专属 UID (格式如: UID_xXyY...)" class="uid-input" clearable>
          <template slot="prepend"><i class="el-icon-key"></i></template>
        </el-input>
      </div>

      <div slot="footer" class="wx-dialog-footer">
        <el-button type="text" class="unbind-btn" @click="submitUnbindWx" :disabled="!isWxBound">
          <i class="el-icon-delete"></i> 解除当前绑定
        </el-button>
        <div class="right-actions">
          <el-button class="unified-btn default-shadow" @click="wxDialogVisible = false">取 消</el-button>
          <el-button type="success" class="unified-btn success-shadow" @click="submitBindWx">确认绑定</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { getCount as getBookCount, queryBookInfosByPage } from "@/api/bookinfo";
import { getCount as getBorrowCount } from "@/api/borrow";
import { getCount as getUserCount, queryUsersByPage } from "@/api/user";
import request from '@/utils/request';

export default {
  name: 'Dashboard',
  computed: { ...mapGetters(['id', 'name', 'roles', 'username']) },
  data() {
    return {
      bookCount: 0, borrowCount: 0, userCount: 0, recentBooks: [],
      msgDialogVisible: false, profileDialogVisible: false, wxDialogVisible: false, ruleVisible: false,
      msgLoading: false, messages: [], wxOpenId: '', myCreditScore: 100,
      isWxBound: false // <-- 新增这行
    }
  },
  created() { this.fetchRealData() },
  methods: {
    async fetchRealData() {
      try { this.bookCount = await getBookCount() || 0; } catch (e) {}
      try { this.borrowCount = await getBorrowCount() || 0; } catch (e) {}
      try { this.userCount = await getUserCount() || 0; } catch (e) {}
      try { const resRecent = await queryBookInfosByPage({ page: 1, limit: 4 }); this.recentBooks = resRecent.data || []; } catch (e) {}
    },
    openProfileDialog() {
      this.profileDialogVisible = true;
      queryUsersByPage({ username: this.username, page: 1, limit: 1 }).then(res => {
        const list = res.data || res.rows || res;
        if (list && list.length > 0) {
          this.myCreditScore = list[0].creditScore != null ? list[0].creditScore : 100;
          // 判定是否有绑定的微信 openid (兼容不同命名)
          this.isWxBound = !!(list[0].openid || list[0].openId);
        }
      }).catch(() => {});
    },
    handleOpenMessage() {
      this.msgDialogVisible = true; this.msgLoading = true;
      request({ url: '/message/getByUserId', method: 'get', params: { userid: this.id } }).then(res => {
        const msgList = res.data || res || [];
        this.messages = msgList.map(msg => { return { content: msg.content, time: new Date(msg.createtime).toLocaleString(), type: msg.isread === 0 ? 'primary' : 'info' } });
        this.msgLoading = false;
      }).catch(err => { this.msgLoading = false; });
    },
    goToPassword() { this.profileDialogVisible = false; this.$router.push('/other/password'); },
    openWxBind() { this.profileDialogVisible = false; setTimeout(() => { this.wxDialogVisible = true; }, 300); },
    submitBindWx() {
      if(!this.wxOpenId || !this.wxOpenId.startsWith('UID_')) return this.$message.warning("格式不正确！请填入以 UID_ 开头的完整字符。");
      request({ url: '/borrow/bindWx', method: 'post', params: { userId: this.id, openId: this.wxOpenId } }).then(res => {
        if(res.code === 0 || res === 1) {
          this.$message.success("微信推送绑定成功！");
          this.isWxBound = true; // 绑定成功，状态变为 true
          this.wxDialogVisible = false;
        } else {
          this.$message.error(res.msg || "绑定失败");
        }
      });
    },
// 打开限制大小的微信扫码窗口
    openWxPusherWindow() {
      const url = 'https://wxpusher.zjiecode.com/wxuser/?type=1&id=120258#/follow';
      const width = 400;  // 限制窗口宽度（手机屏幕大小）
      const height = 650; // 限制窗口高度
      // 计算让窗口在屏幕居中的位置
      const left = (window.screen.width - width) / 2;
      const top = (window.screen.height - height) / 2;

      // 打开独立小窗口
      window.open(url, 'WxPusher', `width=${width},height=${height},top=${top},left=${left},toolbar=no,menubar=no,scrollbars=auto,resizable=no,location=no,status=no`);
    }
  }
}
</script>

<style lang="scss" scoped>
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

.profile-card {
  text-align: center;
  padding: 0 10px 5px; /* 减小了顶部内边距 */

  .avatar-wrapper {
    width: 70px; /* 头像微调变小一点 */
    height: 70px;
    margin: 0 auto 10px; /* 减小了底部间距 */
    border-radius: 50%;
    background: #ecf5ff;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 36px;
    color: #409eff;
  }

  .username {
    font-size: 19px;
    margin: 0 0 8px 0;
    color: #303133;
  }

  .role-tag {
    margin-bottom: 15px; /* 减小了标签的下边距 (原为25px) */
  }

  .profile-details {
    background: #f7f9fc;
    border-radius: 8px;
    padding: 12px; /* 稍微减小内边距 */
    margin-bottom: 15px; /* 减小与下方按钮的距离 (原为25px) */

    .detail-item {
      font-size: 14px;
      display: flex;
      justify-content: center;
      gap: 10px;

      .label {
        color: #909399;
      }

      .value {
        font-weight: bold;
        color: #303133;
      }
    }
  }

  /* ======== 统一的按钮样式区 ======== */
  .profile-actions {
    margin-top: 10px;
    padding: 0 10px;

    /* 强制统一的基础按钮样式 */
    ::v-deep .unified-btn {
      width: 100% !important;
      height: 40px !important; /* 高度从 42px 微调到 40px */
      border-radius: 20px !important;
      margin-left: 0 !important;
      padding: 0 !important;
      display: flex !important;
      justify-content: center !important;
      align-items: center !important;
      font-size: 15px !important;
      font-weight: 500 !important;
      letter-spacing: 1px !important;

      i {
        font-size: 17px !important;
        margin-right: 6px !important;
      }

      span {
        margin-left: 0 !important;
      }
    }

    /* 强制控制两个按钮之间的垂直间距 */
    ::v-deep .unified-btn + .unified-btn {
      margin-top: 12px !important; /* 间距从 16px 减小到 12px */
      margin-left: 0 !important;
    }

    ::v-deep .primary-shadow {
      background-color: #409eff !important;
      border-color: #409eff !important;
      box-shadow: 0 4px 10px rgba(64, 158, 255, 0.3) !important;
      &:hover {
        background-color: #66b1ff !important;
        border-color: #66b1ff !important;
      }
    }

    ::v-deep .success-shadow {
      background-color: #07c160 !important;
      border-color: #07c160 !important;
      box-shadow: 0 4px 10px rgba(7, 193, 96, 0.3) !important;
      &:hover {
        background-color: #06ad56 !important;
        border-color: #06ad56 !important;
      }
    }
  }
}
::v-deep .el-dialog { border-radius: 16px; overflow: hidden; }
::v-deep .glass-dialog { border-radius: 20px !important; box-shadow: 0 20px 50px rgba(0, 0, 0, 0.1) !important; }
::v-deep .el-dialog__body { max-height: 450px; overflow-y: auto; }

/* ======== 微信推送弹窗样式优化 ======== */
.wx-dialog-body {
  padding: 0 5px;

  .status-header {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-bottom: 12px;

    .wx-icon {
      font-size: 42px;
      color: #dcdfe6; /* 未绑定时显示灰色 */
      transition: color 0.3s;
      &.is-bound {
        color: #07c160; /* 绑定后变为微信绿 */
      }
    }

    .status-text {
      margin-top: 8px;
      font-size: 13px;
      color: #606266;
      display: flex;
      align-items: center;
      gap: 6px;

      .status-badge {
        border-radius: 12px;
        padding: 0 10px;
      }
    }
  }

  /* 步骤卡片优化：缩小内边距消除滚动条 */
  .step-card {
    padding: 12px 15px;
    border-radius: 12px;
    margin-bottom: 12px;

    .step-title {
      margin: 0 0 6px 0;
      font-size: 15px;
    }

    .step-desc {
      font-size: 13px;
      color: #606266;
      line-height: 1.5;
      margin: 0;
    }

    &.primary-step {
      background: #f0f7ff;
      border: 1px dashed #a1c4fd;
      .step-title { color: #409eff; }
      .step-link { text-decoration: none; display: block; margin-top: 10px; }
      /* 获取UID按钮，单独应用居中样式 */
      ::v-deep .step-btn {
        width: 100% !important;
        height: 38px !important;
        border-radius: 19px !important;
        margin: 0 !important;
      }
    }

    &.success-step {
      background: #f0f9eb;
      border: 1px solid #e1f3d8;
      .step-title { color: #67C23A; }
    }
  }

  /* 输入框圆角处理 */
  ::v-deep .uid-input .el-input__inner {
    border-radius: 0 20px 20px 0;
    height: 40px;
  }
  ::v-deep .uid-input .el-input-group__prepend {
    border-radius: 20px 0 0 20px;
    background-color: #f5f7fa;
  }
}

/* 底部操作区优化 */
.wx-dialog-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 5px;
  margin-top: -10px;

  .unbind-btn {
    color: #F56C6C;
    font-size: 14px;
    padding: 0;
    &:hover { color: #f78989; }
    &.is-disabled { color: #c0c4cc; cursor: not-allowed; }
  }

  .right-actions {
    display: flex;
    gap: 12px;

    /* 强制统一确认与取消按钮 */
    ::v-deep .unified-btn {
      width: 100px !important;
      height: 38px !important;
      border-radius: 19px !important;
      margin: 0 !important;
      padding: 0 !important;
      display: flex !important;
      justify-content: center !important;
      align-items: center !important;
      font-size: 14px !important;
      font-weight: 500 !important;
      letter-spacing: 1px !important;
    }

    ::v-deep .default-shadow {
      box-shadow: 0 4px 10px rgba(0, 0, 0, 0.04) !important;
      border: 1px solid #DCDFE6 !important;
      color: #606266 !important;
      background: #fff !important;
      &:hover {
        color: #409eff !important;
        border-color: #c6e2ff !important;
        background-color: #ecf5ff !important;
      }
    }

    ::v-deep .success-shadow {
      background-color: #07c160 !important;
      border-color: #07c160 !important;
      box-shadow: 0 4px 10px rgba(7, 193, 96, 0.3) !important;
      &:hover {
        background-color: #06ad56 !important;
        border-color: #06ad56 !important;
      }
    }
  }
}

</style>
