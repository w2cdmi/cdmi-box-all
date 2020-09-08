<template>
  <div>
    <div id="chartColumn" style="width:100%; height:400px;" ref="chartColumn"></div>
  </div>
</template>

<script>
import echarts from "echarts";
export default {
  props: ["list", "updates"],
  data() {
    return {
      chartColumn: null
    };
  },
  mounted() {
    this.init();
  },
  watch: {
    list() {
      this.update();
    },
    updates() {
      this.update();
    }
  },
  methods: {
    init() {
      this.chartColumn = echarts.init(this.$refs.chartColumn);
      this.chartColumn.on("click", params => {
        this.$emit("set-table", params.name);
      });
    },
    update() {
      if (!this.list) {
        return;
      }
      let series = this.list.legend.map((vuale, index) => {
        return {
          name: vuale,
          type: "bar",
          barWidth: "30%",
          itemStyle: {
            normal: {
              barBorderRadius: 5
            }
          },
          data: this.list.yData[index]
        };
      });
      this.chartColumn.setOption({
        color: ["#3398DB"],
        tooltip: {
          trigger: "axis",
          axisPointer: {
            // 坐标轴指示器，坐标轴触发有效
            type: "shadow" // 默认为直线，可选为：'line' | 'shadow'
          }
        },
        grid: {
          left: "3%",
          right: "4%",
          bottom: "3%",
          containLabel: true
        },
        xAxis: [
          {
            data: this.list.xData || [],
            axisTick: {
              alignWithLabel: true
            },
            splitLine: {
              show: true
            }
          }
        ],
        yAxis: [
          {
            splitLine: {
              show: false
            }
          }
        ],
        // dataZoom: [
        //   {
        //     show: true,
        //     start: 94,
        //     end: 100
        //   }
        // ],
        itemStyle: {
          normal: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              {
                offset: 0,
                color: "rgba(70, 181, 44, 0.3)"
              },
              {
                offset: 1,
                color: "rgba(70, 181, 44, 1)"
              }
            ]),
            shadowColor: "rgba(0, 0, 0, 0.1)",
            shadowBlur: 10
          }
        },
        series: series
      });
    }
  }
};
</script>
that.$store.dispatch('setuser_info', data);
          that.$message({
            type: 'success',
            message: '登录成功!'
          });
<style scoped>
.chart-container {
  width: 100%;
  float: left;
}

/*.chart div {
        height: 400px;
        float: left;
    }*/

.el-col {
  padding: 30px 20px;
}
</style>