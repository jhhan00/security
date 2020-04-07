<template>
  <div id="app">
    <img alt="Vue logo" src="./assets/logo.png"><br/>
<!--    <HelloWorld msg="Welcome to Your Vue.js App"/>-->
    {{msg}}
    <global-component></global-component>
    <my_component></my_component>
    <parent-component></parent-component>
    <h2>View Router Practice</h2>
    <p>
      <router-link to="/main">Main</router-link>
      <router-link to="/login">login</router-link>
    </p>
    <router-view></router-view>
  </div>
</template>

<script>
// import HelloWorld from './components/HelloWorld.vue'
import my_component from './components/local_component'
import GlobalComponent from "./components/global-component";
import ParentComponent from "./components/parent-component";

export default {
  name: 'App',
  components: {
    ParentComponent,
    GlobalComponent,
    // HelloWorld
    my_component
  },
  data: function() {
    return {
      msg: 'Hello Vuejs!'
    }
  }
}

</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
