<template>
  <div>
    <div class="event-detail">
      <button v-if="isDj || isHost" @click="editEvent" class="edit-cancel">
        {{ isEditing ? "Cancel" : "Edit Event" }}
      </button>

      <div class="edit" v-if="isEditing">
        <label>Event Title</label>
        <input type="text" v-model="event.name" />
        <label>Event Description</label>
        <input type="text" v-model="event.description" />
        <label>Event Time</label>
        <input type="time" v-model="event.time" />
        <label>Event Date</label>
        <input type="date" v-model="event.date" />
        <label>Event Theme</label>
        <input type="text" v-model="event.theme" />
        <button class="submit-edit" @click="updateEventDetails" type="submit">
          Submit
        </button>
      </div>
      <div class="title-dj-descript">
        <h1>{{ event.name }}</h1>
        <p>{{ event.description }}</p>
        <h3>DJ {{ event.djUsername }}</h3>
      </div>

      <div class="smallerBoxes">
        <div class="theme-date-time">
          <div>
            <p>Theme: {{ event.theme }}</p>
            <p></p>
            <p>Date: {{ formattedDate }}</p>
            <p></p>
            <p>Time: {{ formattedTime }}</p>
          </div>
        </div>
        <div class="hosts">
          <p v-if="event.hosts.length <= 1">Your host is:</p>
          <p v-else-if="event.hosts.length > 1">Your hosts are:</p>
          <div>
            <div v-for="host in event.hosts" :key="host.id" style="display: flex; justify-content: space-evenly;">
              <p class="host-name">{{ host.name }}</p>
              <p
                style="color: #8b0000; cursor: pointer"
                v-on:click="deleteHost(host.name)"
                v-if="isDj"
                >x</p>
            </div>
          </div>
        </div>
      </div>

      <div v-if="isDj">
        <label for="host-select">Add Host:</label>
        <select id="host-select" v-model="selectedHost">
          <option
            v-for="user in availableHosts"
            :value="user.username"
            v-bind:key="user.id"
          >
            {{ user.username }}
          </option>
        </select>
        <button @click="addHost">Add</button>
      </div>
    </div>
    <div id="song-container">
      <div class="song-info">
        <h2>{{ event.playlist.name }}</h2>
        <div v-for="song in event.playlist.songs" :key="song.id">
          <song-display :song="song">
            <div
              style="
                display: flex;
                flex-direction: column;
                justify-content: space-between;
              "
            >
              <div id="likes">
                <span>{{ song.likes }}</span>
                <img
                  src="../images/thumbs-up.png"
                  alt="Likes"
                  width="15"
                  height="15"
                  class="thumb"
                  style="margin-bottom: 10px; cursor: pointer"
                  @click="incrementLikes(song.id)"
                  :class="{
                    disabled:
                      song.clicked || clickedSongs.includes(song.id) || isDj,
                  }"
                />
              </div>
              <div id="dislikes">
                <span>{{ song.dislikes }}</span>
                <img
                  src="../images/thumbs-down.png"
                  alt="Dislikes"
                  width="15"
                  height="15"
                  class="thumb"
                  style="margin-bottom: 10px; cursor: pointer"
                  @click="decrementLikes(song.id)"
                  :class="{
                    disabled:
                      song.clicked || clickedSongs.includes(song.id) || isDj,
                  }"
                />
              </div>
              <button v-if="isHost" @click="vetoSong(song.id)">Veto</button>
            </div>
          </song-display>
          <button @click="removeSong(song.id)" v-if="isDj">
            Remove from Playlist
          </button>
        </div>
      </div>
      <div id="dj-songs">
        <h2>Available Songs</h2>
        <div v-for="song in availableSongs" :key="song.id">
          <div class="song-item">
            <song-display :song="song"></song-display>
            <button @click="submitSong(song.id)" v-if="isDj || isHost">
              Submit to Playlist
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import eventService from "../services/EventService";
import playlistService from "../services/PlaylistService";
import SongDisplay from "@/components/SongDisplay.vue";
import authService from "../services/AuthService";
import moment from "moment";
import songService from "../services/SongService";

export default {
  name: "event-detail",
  components: {
    SongDisplay,
  },
  data() {
    return {
      isVisible: false,
      isLoading: true,
      isEditing: false,
      event: {},
      error: "",
      clickedSongs: [],
      selectedHost: "",
      users: [],
      availableSongs: [],
      playlist: [],
    };
  },
  computed: {
    formattedTime() {
      return moment(this.event.time, "HH:mm").format("h:mm A");
    },
    formattedDate() {
      return moment(this.event.date).format("MMMM Do, YYYY");
    },
    isDj() {
      return this.$store.state.user.username === this.event.djUsername;
    },
    isHost() {
      let isHost = false;
      this.event.hosts.forEach((host) => {
        if (host.name === this.$store.state.user.username) {
          isHost = true;
        }
      });
      return isHost;
    },
    availableHosts() {
      return this.users.filter((user) => {
        if (
          this.event.hosts.filter((a) => a.name === user.username).length ===
            0 &&
          user.authorities[0].name !== "ROLE_DJ"
        ) {
          return user;
        }
      });
    },
  },
  methods: {
    getAvailableSongs() {
      songService.getSongs(this.event.djUsername).then((response) => {
        if (response.status === 200) {
          this.availableSongs = response.data.filter((song) => {
            if (
              this.event.playlist.songs.filter((a) => a.id === song.id)
                .length === 0
            ) {
              return song;
            }
          });
        }
      });
    },
    vetoSong(songId) {
      playlistService
        .vetoSong(this.event.playlist.playlistId, songId)
        .then((response) => {
          if (response.status === 200) {
            this.getEvent();
          }
        });
    },
    getEvent(callBack) {
      const eventId = parseInt(this.$route.params.id);
      eventService
        .getEventById(eventId)
        .then((response) => {
          this.event = response.data;
          callBack();
        })
        .catch((error) => {
          console.log(error);
          this.isLoading = false;
          this.error = "Error loading event";
        });
    },
    getAllUsers() {
      authService.getAllUsers().then((response) => {
        if (response.status === 200) {
          this.users = response.data;
        }
      });
    },
    incrementLikes(songId) {
      if (this.clickedSongs.includes(songId)) {
        return;
      }
      playlistService.addLikes(this.event.playlist.playlistId, songId);
      this.clickedSongs.push(songId);
      this.getEvent();
    },
    decrementLikes(songId) {
      if (this.clickedSongs.includes(songId)) {
        return;
      }
      playlistService.deleteLikes(this.event.playlist.playlistId, songId);
      this.clickedSongs.push(songId);
      this.getEvent();
    },
    editEvent() {
      this.isEditing = !this.isEditing;
    },
    updateEventDetails() {
      const eventId = parseInt(this.$route.params.id);
      eventService
        .updateEvent(this.event, eventId)
        .then((response) => {
          console.log("Event updated successfully:", response);
          this.isEditing = false;
        })
        .catch((error) => {
          console.log("Error updating event:", error);
        });
        this.isEditing = false;
    },
    deleteHost(hostName) {
      eventService.removeHostFromEvent(this.event.id, hostName).then(() => {
        this.getEvent();
      });
    },
    addHost() {
      eventService.addHostToEvent(this.event.id, this.selectedHost).then(() => {
        this.getEvent();
      });
    },
    submitSong(songId) {
      playlistService
        .addSongToPlaylist(this.event.playlist.playlistId, songId)
        .then((response) => {
          if (response.status === 200) {
            alert("Song successfully added");
            this.getEvent(this.getAvailableSongs);
          }
        });
    },
    removeSong(songId) {
      playlistService
        .removeSongFromPlaylist(this.event.playlist.playlistId, songId)
        .then((response) => {
          if (response.status === 204) {
            alert("Song successfully removed");
            this.getEvent(this.getAvailableSongs);
          }
        });
    },
  },
  created() {
    this.getEvent(this.getAvailableSongs);
    this.getAllUsers();
  },
};
</script>

<style scoped>
h2 {
  text-align: center;
}
.smallerBoxes {
  display: flex;
  flex-direction: row;
  justify-content: space-evenly;
  font-size: 1.1rem;
}
.event-image {
  width: 100%;
  max-width: 600px;
  height: auto;
  margin-bottom: 1rem;
}
.event-details {
  display: grid;
  flex-direction: column;
  align-items: center;
  margin-bottom: 1rem;
}
#song-container {
  display: flex;
  justify-content: space-evenly;
}
.genres {
  display: inline-block;
  min-width: 100px;
  font-size: 1.25rem;
  text-align: center;
}
.hosts {
  background-color: #CCC6C4;
  border-radius: 5px;
  padding: 20px;
  margin-bottom: 20px;
}
.theme-date-time {
  justify-content: space-around;
  background-color: #CCC6C4;
  border-radius: 5px;
  padding: 20px;
  margin-bottom: 20px;
}

#theme-date-time h4 {
  font-size: 18px;
  font-weight: bold;
}

.event-detail {
  text-align: center;
  margin: 150px;
}

.disabled {
  opacity: 0.2;
  cursor: not-allowed;
}

.disabled img {
  filter: grayscale(1);
}

#likes {
  display: flex;
  font-size: 13px;
  justify-content: space-evenly;
}

#dislikes {
  display: flex;
  font-size: 13px;
  justify-content: space-between;
}

.thumb {
  display: inline-block;
  margin-right: 5px;
  margin-left: 5px;
}

.edit {
  display: grid;
  text-align: center;
}

.edit-cancel {
  font-family: arial;
  font-weight: bold;
  border-radius: 6px;
  cursor: pointer;
  padding: 3px 6px 3px 6px;
  font-size: 16px;
}

.submit-edit {
  font-family: arial;
  font-weight: bold;
  border-radius: 6px;
  cursor: pointer;
  padding: 3px 6px 3px 6px;
  margin-top: 10px;
  font-size: 16px;
  background-color: #ada09b;
  color: black;
  border: none;
  margin-bottom: 20px;
}

.submit-edit:hover {
  background-color: #bbb5b3;
}

.edit-cancel:hover {
  background-color: #bbb5b3;
}

label {
  font-family: arial;
  margin-top: 10px;
  font-size: 16px;
  font-weight: bold;
}

input {
  padding-top: 5px;
  padding-bottom: 5px;
  font-size: 16px;
  margin-bottom: 10px;
}

.title-dj-descript {
  background-color: #73caa0;
  padding: 20px;
  margin-bottom: 20px;
  border-radius: 5px;
}
.title-dj-descript h1 {
  font-size: 36px;
  font-weight: bold;
  margin-bottom: 10px;
}
.title-dj-descript h3 {
  font-size: 24px;
  font-weight: bold;
  margin-top: 30px;
  margin-bottom: 10px;
}

#host-select {
  font-size: 16px;
  margin-right: 10px;
}

.edit-cancel {
  font-size: 16px;
  padding: 5px 10px;
  margin-bottom: 10px;
  background-color: #ada09b;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

div[for="host-select"] {
  font-size: 16px;
  font-weight: bold;
  margin-right: 10px;
}

div[for="host-select"] + #host-select {
  margin-right: 10px;
}

div[for="host-select"] + #host-select + button {
  margin-top: 5px;
}
</style>
