<template>
  <div class="Events">
    <h1>My Events</h1>
    <events />
    <DjEvent />  
  </div>
</template>

<script>
import DjEvent from "../components/DJEventView.vue";
import events from "../components/EventList.vue";

export default {
  name: "MyEvents",
  components: {
     events,
    DjEvent,
  },
  data() {
    return {
      songs: [],
    };
  },
};
</script>

<style scoped>
h1 {
  text-align: center;
}
</style>