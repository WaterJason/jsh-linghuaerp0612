<template>
  <a-config-provider :locale="locale">
    <div id="app">
      <router-view/>
      <!-- stagewise-toolbar 暂时禁用以解决依赖冲突 -->
      <!-- <stagewise-toolbar v-if="isDev" :config="stagewiseConfig" /> -->
    </div>
  </a-config-provider>
</template>
<script>
  // import { StagewiseToolbar } from '@stagewise/toolbar-vue'
  // import { VuePlugin } from '@stagewise-plugins/vue'
  import zhCN from 'ant-design-vue/lib/locale-provider/zh_CN'
  import enquireScreen from '@/utils/device'

  export default {
    components: {
      // StagewiseToolbar
    },
    data () {
      return {
        locale: zhCN,
        isDev: process.env.NODE_ENV === 'development'
        // stagewiseConfig: {
        //   plugins: [VuePlugin()]
        // }
      }
    },
    created () {
      let that = this
      enquireScreen(deviceType => {
        // tablet
        if (deviceType === 0) {
          that.$store.commit('TOGGLE_DEVICE', 'mobile')
          that.$store.dispatch('setSidebar', false)
        }
        // mobile
        else if (deviceType === 1) {
          that.$store.commit('TOGGLE_DEVICE', 'mobile')
          that.$store.dispatch('setSidebar', false)
        } else {
          that.$store.commit('TOGGLE_DEVICE', 'desktop')
          that.$store.dispatch('setSidebar', true)
        }
      })
    }
  }
</script>
<style>
  #app {
    height: 100%;
  }
</style>