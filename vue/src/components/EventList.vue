<template>
<body>
  <div>
    <div>
      <input type="text" v-model="searchText" placeholder="Search Events" />
      <br /><br />
      <table>
        <thead>
          <tr>
            <th>Event Name</th>
            <th>Date</th>
            <th>DJ</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="event in filterEvents" :key="event.id">
            <td class="event-title">
              <router-link :to="{ name: 'event-detail', params: { id: event.id } }" class="event-link">
                {{ event.name }}
              </router-link>
            </td>
            <td>{{ event.formattedDate }}</td>
            <td>{{ event.djUsername }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</body>
</template>

<script>
import eventService from "../services/EventService";
import moment from 'moment';
export default {
  name: "EventList",
  data() {
    return {
      searchText: "",
    };
  },
  computed: {
      formattedDate(){
     return moment(this.event.date).format('MMMM Do, YYYY');
  },
    filterEvents() {
      return this.$store.state.events.filter((event) => {
        return event.name.toLowerCase().includes(this.searchText.toLowerCase());
      }).map((event) => {
          return {
              ...event,
              formattedDate: moment(event.date).format('MMMM Do, YYYY'),
              dj: event.djUsername,
          }
      });
    },
  },
  methods: {
    getEvents() {
      eventService.getAllEvents().then((response) => {
        this.$store.commit("SET_EVENTS", response.data);
      });
    },
  },
  created() {
      this.getEvents();
    },
};
</script>

<style scoped>
.event-hosts {
  vertical-align: middle;
  margin-bottom: 10px;
  padding-top: 10px;
}
@media (max-width: 768px) {
  .event-title {
    font-size: 18px;
    margin-bottom: 10px;
  }
  table {
  width: 100%;
}
}
.banner-clickable {
  position: relative;
}
.banner img {
  max-width: 100%;
}

.event-list {
  margin-top: 20px;
  margin-bottom: 50px;
}

table {
  border-collapse: collapse;
  width: 100%;
  height: 100%;
}

th,
td {
  text-align: left;
  align-items: center;
  justify-content: center;
  padding: 10px;
}

th {
  background-color: #CCC6C4;
  color: black;
  font-size: 20px;
}
.event-title {
  font-size: 18px;
}
tr:link {
    text-decoration: none;
}
tr:hover {
  background-color: #e7e1e1;
  cursor: pointer;
  text-decoration: none;
}

.event-link {
    text-decoration: none;
    color: black !important;
}

</style>