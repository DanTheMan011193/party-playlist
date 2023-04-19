<template>
  <div class="about-us">
    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Varela+Round">
    <h1 class="about-us-heading">About Us</h1>
    <div class="about-us-content">
      <div class="about-us-banner">
       <img src="../images/topaboutus.png" alt="about-us-banner" width="100%">
      </div>
      <div class="about-us-text">
        <p>Welcome to Party Pals Playlist, the ultimate party planning app!</p>
        <p>Our app is designed to help DJs manage their business by creating events, adding songs to their playlist, and assigning customers as hosts. Guests can vote on their favorite songs, ensuring that everyone has a great time at the party.</p>
        <p>Our app is easy to use, and we offer a variety of features to make your party planning experience as smooth as possible. Sign up today to start planning your next party!</p>
      </div>
      <div class="about-us-image">
        <img src="../images/bottomaboutus.png" alt="Lower-banner" width="100%">
      </div>
    </div>
  </div>
</template>

<script>
export default {
name: 'AboutUs',
}
</script>

<style scoped>
  @import url('https://fonts.googleapis.com/css2?family=Fira+Sans:wght@500&display=swap');
.about-us {
  max-width: 1200px;
    margin: 0 auto;
    padding: 50px;
    font-size: 30px;
    position: relative;
    text-align: center;
}
.about-us-heading {
  font-size:36px;
  margin-bottom: 30px;
}
.about-us-content {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 30px;
}
.about-us-text {
 font-family: 'Fira Sans', sans-serif;
  max-width: 800px;
  width: 100%;
  text-align: center;
  line-height: 1.5;
  margin:0 auto;
}
</style>