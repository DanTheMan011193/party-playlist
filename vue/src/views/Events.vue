<template>
  <div class="Events">
    <h1>Events</h1>
    <events />
</div>
</template>

<script>
import events from '../components/EventList.vue'

export default {
  name: "Events",
 components: {
    events,
  },
};

</script>

<style>

</style>
