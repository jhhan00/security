import Vue from 'vue'
import App from './App.vue'
import VueRouter from 'vue-router'

Vue.config.productionTip = false

import GlobalComponent from './components/global-component'

Vue.component(GlobalComponent.name, GlobalComponent)

new Vue({
  VueRouter,
  render: h => h(App),
}).$mount('#app')
