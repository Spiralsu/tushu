<template>
  <div class="dashboard-container">
    <div class="dashboard-welcome">
      <div class="welcome-content">
        <h1>欢迎使用图书管理系统</h1>
        <p>你好，{{ name }}，祝你有美好的一天！</p>
        <div class="welcome-actions">
          <el-button type="primary" icon="el-icon-position" @click="navigateTo('/bookmanage/bookinfo?openDonate=true')">去漂流</el-button>
          <el-button type="success" icon="el-icon-document" @click="navigateTo('/bookmanage/borrow')">我的漂流进度</el-button>
        </div>
      </div>
      <div class="welcome-image">
        <i class="el-icon-collection"></i>
      </div>
    </div>

    <el-row :gutter="24" class="data-overview">
      <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6">
        <el-card shadow="hover" class="data-card book-card">
          <div class="card-content">
            <div class="card-icon">
              <i class="el-icon-reading"></i>
            </div>
            <div class="card-info">
              <div class="card-title">图书总数</div>
              <div class="card-value">{{ bookCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6">
        <el-card shadow="hover" class="data-card borrow-card">
          <div class="card-content">
            <div class="card-icon">
              <i class="el-icon-document"></i>
            </div>
            <div class="card-info">
              <div class="card-title">漂流总数</div>
              <div class="card-value">{{ borrowCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6">
        <el-card shadow="hover" class="data-card user-card">
          <div class="card-content">
            <div class="card-icon">
              <i class="el-icon-user"></i>
            </div>
            <div class="card-info">
              <div class="card-title">用户总数</div>
              <div class="card-value">{{ userCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6">
        <el-card shadow="hover" class="data-card type-card">
          <div class="card-content">
            <div class="card-icon">
              <i class="el-icon-collection-tag"></i>
            </div>
            <div class="card-info">
              <div class="card-title">图书类型</div>
              <div class="card-value">{{ typeCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="24" class="chart-section">
      <el-col :span="24">
        <div class="section-header">
          <h2 class="section-title">统计分析</h2>
          <el-divider></el-divider>
        </div>
      </el-col>
      <el-col :xs="24" :sm="24" :md="12" :lg="8" :xl="8">
        <el-card shadow="hover" class="chart-card">
          <div slot="header" class="card-header">
            <span>图书类型分布</span>
          </div>
          <div class="chart-container" ref="pieChartContainer"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="12" :lg="8" :xl="8">
        <el-card shadow="hover" class="chart-card">
          <div slot="header" class="card-header">
            <span>漂流状态分布</span>
          </div>
          <div class="chart-container" ref="barChartContainer"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="24" :lg="8" :xl="8">
        <el-card shadow="hover" class="chart-card">
          <div slot="header" class="card-header">
            <span>高价图书排行</span>
          </div>
          <div class="chart-container" ref="lineChartContainer"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { mapGetters } from "vuex";
import { getCount as getBookCount } from "@/api/bookinfo";
import { getCount as getBorrowCount } from "@/api/borrow";
import { getCount as getUserCount } from "@/api/user";
import { getCount as getTypeCount } from "@/api/booktype";
import { getBookTypeDistribution, getBorrowStatusDistribution, getHighPriceBooks } from "@/api/dashboard";
import request from "@/utils/request";

export default {
  name: "Dashboard",
  data() {
    return {
      bookCount: 0,
      borrowCount: 0,
      userCount: 0,
      typeCount: 0,
      chartData: {
        pieData: [],
        barData: { categories: [], values: [] },
        lineData: { books: [], values: [] }
      },
      charts: { pieChart: null, barChart: null, lineChart: null },
    };
  },
  computed: {
    ...mapGetters(["id", "name", "roles"]),
  },
  mounted() {
    this.fetchData();
    this.$nextTick(() => {
      this.initCharts();
      this.fetchChartData();
    });
    window.addEventListener('resize', this.handleResize);
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize);
  },
  methods: {
    handleResize() {
      Object.values(this.charts).forEach(chart => chart && chart.resize());
    },
    fetchData() {
      getBookCount().then(res => { this.bookCount = res; }).catch(() => { this.bookCount = 0; });
      // 使用正确的漂流统计接口
      request({url:'/borrow/getCount', method:'get'}).then(res => { this.borrowCount = res; }).catch(() => { this.borrowCount = 0; });
      getUserCount().then(res => { this.userCount = res; }).catch(() => { this.userCount = 0; });
      getTypeCount().then(res => { this.typeCount = res; }).catch(() => { this.typeCount = 0; });
    },
    async fetchChartData() {
      try {
        const pieData = await getBookTypeDistribution();
        this.chartData.pieData = pieData;
        if (this.charts.pieChart) this.updatePieChart();

        const barData = await getBorrowStatusDistribution();
        this.chartData.barData = barData;
        if (this.charts.barChart) this.updateBarChart();

        const lineData = await getHighPriceBooks();
        this.chartData.lineData = lineData;
        if (this.charts.lineChart) this.updateLineChart();
      } catch (error) {
        console.error('获取图表数据失败:', error);
      }
    },
    navigateTo(path) {
      this.$router.push(path);
    },
    initCharts() {
      this.charts.pieChart = this.$echarts.init(this.$refs.pieChartContainer);
      this.charts.barChart = this.$echarts.init(this.$refs.barChartContainer);
      this.charts.lineChart = this.$echarts.init(this.$refs.lineChartContainer);
      Object.values(this.charts).forEach(chart => chart.showLoading());
    },
    updatePieChart() {
      this.charts.pieChart.hideLoading();
      const option = {
        tooltip: { trigger: 'item', formatter: '{a} <br/>{b}: {c} ({d}%)' },
        series: [{
          name: '图书类型', type: 'pie', radius: ['35%', '60%'], center: ['50%', '45%'],
          data: this.chartData.pieData,
          itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 }
        }]
      };
      this.charts.pieChart.setOption(option);
    },
    updateBarChart() {
      this.charts.barChart.hideLoading();
      const option = {
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        xAxis: [{ type: 'category', data: this.chartData.barData.categories }],
        yAxis: [{ type: 'value' }],
        series: [{
          name: '数量', type: 'bar', barWidth: '60%',
          data: this.chartData.barData.values,
          itemStyle: {
            color: function(params) {
              return params.dataIndex === 0 ? '#67C23A' : '#E6A23C';
            }
          }
        }]
      };
      this.charts.barChart.setOption(option);
    },
    updateLineChart() {
      this.charts.lineChart.hideLoading();
      const option = {
        tooltip: { trigger: 'axis' },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'value' },
        yAxis: {
          type: 'category',
          data: this.chartData.lineData.books,
          // 【问题2修复】：限制文字长度，防止遮挡
          axisLabel: {
            formatter: function(value) {
              if (value.length > 5) {
                return value.substring(0, 5) + '...';
              }
              return value;
            }
          }
        },
        series: [{
          name: '图书价格', type: 'bar', data: this.chartData.lineData.values,
          itemStyle: { color: '#F56C6C' }
        }]
      };
      this.charts.lineChart.setOption(option);
    }
  }
};
</script>

<style lang="scss" scoped>
.dashboard-container { padding: 24px; background-color: #f5f7fa; min-height: calc(100vh - 50px); }
.dashboard-welcome { display: flex; align-items: center; justify-content: space-between; margin-bottom: 32px; background: linear-gradient(to right, #1989fa, #5cbcff); border-radius: 8px; padding: 30px; color: #fff; box-shadow: 0 4px 12px rgba(25, 137, 250, 0.2); h1 { font-size: 28px; font-weight: 600; margin: 0 0 12px 0; } p { font-size: 16px; margin: 0 0 20px 0; opacity: 0.9; } .welcome-actions { margin-top: 20px; .el-button { margin-right: 15px; } } .welcome-image i { font-size: 120px; opacity: 0.2; } }
.data-card { height: 120px; border-radius: 8px; overflow: hidden; transition: all 0.3s; .card-content { display: flex; align-items: center; height: 100%; padding: 20px; } .card-icon { width: 64px; height: 64px; border-radius: 12px; display: flex; align-items: center; justify-content: center; margin-right: 20px; i { font-size: 30px; color: #fff; } } .card-info { flex: 1; } .card-title { font-size: 16px; color: #606266; margin-bottom: 10px; } .card-value { font-size: 30px; font-weight: bold; color: #303133; } }
.book-card .card-icon { background: linear-gradient(135deg, #409EFF, #53a8ff); }
.borrow-card .card-icon { background: linear-gradient(135deg, #67C23A, #85ce61); }
.user-card .card-icon { background: linear-gradient(135deg, #E6A23C, #f5a657); }
.type-card .card-icon { background: linear-gradient(135deg, #F56C6C, #f78989); }
.chart-card { margin-bottom: 20px; .chart-container { height: 300px; width: 100%; } }
</style>
