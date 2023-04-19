<template>
<nav class="nav-bar">
  <div id="nav">
    <ul>
      <li></li>
      <li v-if="!loggedIn"><router-link v-bind:to="{ name: 'login' }">Login</router-link></li>        
      <li v-if="loggedIn"><a href="#" @click="logout">Logout</a></li>
      <li><router-link v-bind:to="{ name: 'home' }">Home</router-link></li>
      <li><router-link v-bind:to="{ name: 'events' }">Events</router-link ></li>
      <li v-if="loggedIn && this.$store.state.user.authorities[0].name.includes('ROLE_DJ')"><router-link v-bind:to="{ name: 'myevents' }">My Events</router-link></li>
      </ul>
  </div>
</nav>
</template>

<script>
import { mapState } from "vuex";
export default {
  name: "navbar",
  computed: {
  ...mapState(["token"]),
  loggedIn() {
    return this.token !== "";
  },
},
methods: {
  logout() {
    this.$store.commit("LOGOUT");
    this.$router.push("/login");
  }
}
}
</script>

<style scoped>
.nav-bar {
  font-family:sans-serif;
  top: 0;
  left: 0;
  bottom: 0;
  width: 200px;
  background-color: #73caa0;
  border-color: black 20px;
  font-weight: bold;  
}
ul {
  display: flex;
  flex-direction: column;
}
.nav-bar ul {
  display: flex;
  list-style: none;
  padding: 0;
  margin-top: 80px;
}
.nav-bar li {
  padding: 10px;
  margin-top: 10px;
}
.nav-bar a {
  text-decoration: none;
  color: rgb(5, 0, 0);
}
.nav-bar a:hover {
  color: #36584b;
}

.nav {
  margin-top: 40px;
}

</style>






