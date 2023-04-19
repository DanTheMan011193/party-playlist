<template>
    <div id="app">
      <header>
        <a href="/">
        <img src="./images\DJ Performance Illustration Logo (11).png" alt="Header Image"></a>
        <h1 class="p1" style="align: center;">Party Pals Playlists</h1>
      </header>
      <div id="MainView">
        <navigation class="navbar"/>
        <router-view class="main"/>
      </div>
      <head>
        <title/>
      </head>
    </div>
</template>
<script>
import Navigation from './views/Navigation.vue'

export default {
  name: 'App',
  components: {
    Navigation,
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Righteous&display=swap');
@import url('https://fonts.googleapis.com/css2?family=Fira+Sans:wght@500&display=swap');
#MainView {
  display: flex;
  flex-grow: 2;
  position:relative;
  margin: 0;
  padding: 0;
  font-family: 'Fira Sans', sans-serif;
  background:url('C:\Users\Student\workspace\nlr-12-module-3-final-capstone-party-playlist\vue\src\images\background.png') no-repeat center center fixed;
  background-size:cover;
  }
.p1{
  font-family: 'Righteous', cursive;
  display:flex;
  position: absolute;
  text-align: center;
  margin-left: 150px;
}
.main {
  flex-grow: 1;
  margin-left: 115px;
  margin-top: 100px;
}
.title {
  flex-grow: 1;
  margin-left: 100px;
}
.navbar {
  position: fixed;
  width: 100px !important;
  z-index: 0;
}
header {
  display: flex;
  position: fixed;
  align-items: center;
  height: 100px;
  width: 100%;
  background-color: #73caa0;
  text-align: center;
  top: 0px;
  z-index: 1; 
}
.p1{
  font-family: 'Righteous', cursive;
}
img {
  margin-right: 10px;
  max-width: 150px;
}
</style>