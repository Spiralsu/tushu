<template>
  <div class="wish-wall-container">
    <div class="header-section">
      <h1 class="wall-title">✨ 星空心愿墙 ✨</h1>
      <p class="wall-subtitle">在这里写下你苦苦寻找的书籍，也许会有缘人为你的星空点亮光芒</p>
      <el-button type="primary" round icon="el-icon-magic-stick" size="medium" @click="openWishDialog" class="make-wish-btn">
        许下我的求书心愿
      </el-button>
    </div>

    <div class="cards-container" v-loading="loading">
      <div v-for="item in wishList" :key="item.wishId" class="wish-card" :class="{'is-fulfilled': item.state === 1}">
        <div class="card-pin"></div>
        <div class="card-content">
          <h3 class="book-name">《{{ item.bookName }}》</h3>
          <p class="wish-desc">"{{ item.wishDesc }}"</p>
          <div class="user-info">
            <span class="user-name"><i class="el-icon-user"></i> {{ item.userName }}</span>
            <span class="time">{{ formatDate(item.createTime) }}</span>
          </div>
        </div>
        <div class="card-footer">
          <div v-if="item.state === 1" class="fulfilled-tag">
            <i class="el-icon-success"></i> 愿望已达成
          </div>
          <el-button v-else-if="item.userId !== id" type="success" round size="mini" @click="handleFulfill(item)">我来满足TA</el-button>
          <el-button v-else type="info" round size="mini" disabled>我的心愿</el-button>

          <el-button v-if="roleIsAdmin || item.userId === id" type="text" style="color:#ff4d4d; margin-left: 10px;" @click="handleDelete(item)">删除</el-button>
        </div>
      </div>
    </div>

    <el-empty v-if="wishList.length === 0" description="星空暂时很安静，来许下第一个心愿吧"></el-empty>

    <el-dialog title="🌟 许下求书心愿" :visible.sync="dialogVisible" width="500px" custom-class="glass-dialog" center>
      <el-form :model="form" label-width="80px">
        <el-form-item label="想看的书"><el-input v-model="form.bookName" placeholder="例如：百年孤独" /></el-form-item>
        <el-form-item label="心愿寄语"><el-input type="textarea" v-model="form.wishDesc" :rows="4" placeholder="说说为什么想看这本书，或者有什么特别的版本要求..." /></el-form-item>
      </el-form>
      <div slot="footer" style="display:flex; justify-content:center; gap:20px;">
        <el-button @click="dialogVisible = false" round style="width:120px;">取 消</el-button>
        <el-button type="primary" @click="submitWish" round style="width:140px;">将心愿抛向星空</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getWishList, addWish, fulfillWish, deleteWish } from '@/api/wish'
import { mapGetters } from "vuex"

export default {
  name: 'WishWall',
  data() {
    return {
      wishList: [],
      loading: false,
      dialogVisible: false,
      form: { bookName: '', wishDesc: '' }
    }
  },
  computed: {
    ...mapGetters(['id', 'name', 'roles']),
    roleIsAdmin() { return this.roles && this.roles[0] === 'admin'; }
  },
  created() {
    this.fetchData()
  },
  methods: {
    fetchData() {
      this.loading = true
      getWishList().then(res => {
        this.wishList = res || []
        this.loading = false
      }).catch(() => { this.loading = false })
    },
    openWishDialog() {
      this.form = { bookName: '', wishDesc: '' }
      this.dialogVisible = true
    },
    submitWish() {
      if (!this.form.bookName) {
        this.$message.warning("书名不能为空哦！")
        return
      }
      const data = {
        userId: this.id,
        userName: this.name,
        bookName: this.form.bookName,
        wishDesc: this.form.wishDesc || '希望能借到这本书！'
      }
      addWish(data).then(res => {
        if(res > 0) {
          this.$message.success("心愿发射成功！")
          this.dialogVisible = false
          this.fetchData()
        }
      })
    },


    handleFulfill(item) {
      this.$confirm(`如果你手头刚好有《${item.bookName}》，请去【发布旧书】并把它漂流起来吧！系统会在你发布成功后自动点亮TA的心愿！`, '我来满足TA', {
        confirmButtonText: '去发布书籍',
        cancelButtonText: '再想想',
        type: 'success'
      }).then(() => {
        this.$message.info("请填写书籍信息进行发布，发布成功后心愿将自动达成并通知对方！");

        // 【核心修改】：不仅传书名，还要把心愿ID和许愿人的ID传过去，为了给TA发微信！
        this.$router.push({
          path: '/bookmanage/bookinfo',
          query: {
            openDonate: 'true',
            wishBookName: item.bookName,
            wishId: item.wishId,      // 追踪这是哪个心愿
            wisherId: item.userId     // 追踪该给谁发微信
          }
        });
      }).catch(() => {});
    },




    handleDelete(item) {
      this.$confirm("确定要摘下这颗心愿星吗？", "提示").then(() => {
        deleteWish(item.wishId).then(res => {
          if(res > 0) {
            this.$message.success("已摘下星光")
            this.fetchData()
          }
        })
      })
    },
    formatDate(dateStr) {
      if (!dateStr) return '';
      const date = new Date(dateStr);
      return `${date.getMonth() + 1}月${date.getDate()}日`;
    }
  }
}
</script>

<style lang="scss" scoped>
.wish-wall-container {
  padding: 40px;
  background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
  min-height: calc(100vh - 50px);
  border-radius: 16px;
}
.header-section {
  text-align: center;
  margin-bottom: 40px;
  .wall-title { font-size: 32px; font-weight: 700; color: #f8fafc; text-shadow: 0 0 15px rgba(255,255,255,0.4); margin-bottom: 10px; }
  .wall-subtitle { font-size: 15px; color: #94a3b8; margin-bottom: 25px; }
  .make-wish-btn {
    background: linear-gradient(135deg, #8b5cf6 0%, #d946ef 100%); border: none; color: white; padding: 12px 30px; font-size: 16px; box-shadow: 0 4px 20px rgba(217, 70, 239, 0.5); transition: transform 0.3s;
    &:hover { transform: scale(1.05); }
  }
}

.cards-container { display: flex; flex-wrap: wrap; gap: 30px; justify-content: center; }

/* 玻璃质感便利贴 */
.wish-card {
  width: 320px; background: rgba(255, 255, 255, 0.08); backdrop-filter: blur(12px); border: 1px solid rgba(255,255,255,0.15); border-radius: 20px; padding: 25px 20px; position: relative; transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1); box-shadow: 0 10px 30px rgba(0,0,0,0.2); display: flex; flex-direction: column;
  &:hover { transform: translateY(-10px); border-color: rgba(255,255,255,0.4); box-shadow: 0 15px 35px rgba(139, 92, 246, 0.2); }
  &.is-fulfilled { background: rgba(16, 185, 129, 0.05); border-color: rgba(16, 185, 129, 0.2); .card-pin { background: #10b981; } }

  .card-pin { position: absolute; top: -12px; left: 50%; transform: translateX(-50%); width: 24px; height: 24px; background: radial-gradient(circle at 30% 30%, #fcd34d, #f59e0b); border-radius: 50%; box-shadow: inset -2px -2px 4px rgba(0,0,0,0.3), 0 4px 8px rgba(0,0,0,0.5); }
  .card-content {
    flex: 1;
    .book-name { font-size: 20px; margin: 5px 0 15px 0; color: #f8fafc; font-weight: bold; }
    .wish-desc { font-size: 14px; color: #cbd5e1; line-height: 1.7; font-style: italic; margin-bottom: 25px; }
    .user-info { display: flex; justify-content: space-between; font-size: 13px; color: #94a3b8; border-bottom: 1px solid rgba(255,255,255,0.1); padding-bottom: 15px; margin-bottom: 15px; }
  }
  .card-footer { display: flex; justify-content: space-between; align-items: center; .fulfilled-tag { color: #34d399; font-weight: bold; font-size: 14px; } }
}
</style>
