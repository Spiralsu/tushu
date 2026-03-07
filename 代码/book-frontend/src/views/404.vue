<template>
  <div class="wscn-http404-container">
    <div class="content-wrapper">
      <div class="pic-404">
        <i class="el-icon-ship icon-boat"></i>
        <div class="wave"></div>
      </div>
      <div class="bullshit">
        <div class="bullshit__oops">Oops! 页面漂走了...</div>
        <div class="bullshit__headline">你探索的海域似乎不存在</div>
        <div class="bullshit__info">这页漂流书可能已经被海浪卷走，或者你输入的地址有误。请检查网址，或者回到港口重新出发。</div>
        <el-button type="primary" round class="bullshit__return-home" @click="$router.push('/')">
          返回首页港口
        </el-button>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.wscn-http404-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f2f5;

  .content-wrapper {
    display: flex;
    align-items: center;
    background: #fff;
    padding: 60px 80px;
    border-radius: 24px;
    box-shadow: 0 10px 40px rgba(0,0,0,0.05);
  }

  .pic-404 {
    position: relative;
    width: 300px;
    height: 200px;
    display: flex;
    justify-content: center;
    align-items: center;
    margin-right: 50px;

    .icon-boat {
      font-size: 120px;
      color: #409eff;
      animation: float 3s ease-in-out infinite;
    }
  }

  .bullshit {
    max-width: 400px;
    &__oops { font-size: 32px; font-weight: bold; line-height: 40px; color: #409eff; margin-bottom: 20px; }
    &__headline { font-size: 20px; line-height: 24px; color: #222; font-weight: bold; margin-bottom: 10px; }
    &__info { font-size: 14px; line-height: 21px; color: #808080; margin-bottom: 30px; }
    &__return-home { padding: 12px 30px; font-weight: 600; background: #409eff; border: none; transition: transform 0.2s; &:hover { transform: translateY(-2px); } }
  }
}

@keyframes float {
  0% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-15px) rotate(2deg); }
  100% { transform: translateY(0px) rotate(0deg); }
}
</style>
