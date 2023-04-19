<template>
  <div class="EventContainer">
    <button v-if="isDJ" @click="createForm" class="create">Create Event</button>
          
    <div class="create" v-if="isCreating">
      <form v-on:submit.prevent="createEvent" id="create-event">
        <div class="create-input">
          <label>Event Title:&nbsp;&nbsp;&nbsp;</label>
          <input type="text" v-model="event.name" />
        </div>
        <div class="create-input">  
          <label>Event Description:&nbsp;&nbsp;&nbsp;</label>
          <input type="text" v-model="event.description" />
        </div>
        <div class="create-input">
          <label>Event Time:&nbsp;&nbsp;&nbsp;</label>
          <input type="time" v-model="event.time" />
        </div>
        <div class="create-input">
          <label>Event Date:&nbsp;&nbsp;&nbsp;</label>
          <input type="date" v-model="event.date" />
        </div>
        <div class="create-input">
          <label>Event Theme:&nbsp;&nbsp;&nbsp;</label>
          <input type="text" v-model="event.theme" />
        </div>
        <div class="create-input">
          <label>Playlist Name:&nbsp;&nbsp;&nbsp;</label>
          <input type="text" v-model="event.playlist.name" />
        </div>
        <div class="create-input">
          <label>Playlist Description:&nbsp;&nbsp;&nbsp;</label>
          <input type="text" v-model="event.playlist.description" />
        </div>
        <div class="create-input">
          <select id="genre-create" v-model="event.playlist.genres" multiple>
                <option value="">Choose a genre</option>
                <option
                  v-for="genre in allGenres"
                  :key="genre.id"
                  v-bind:value="genre"
                >
                  {{ genre.name }}
                </option>
              </select>
          </div>
        <div class="create-input">
          <button class="submit-created" type="submit" >Submit</button>
        </div>
      </form>
    </div>
      <h1>My Songs</h1>
    <div id="song-container">
      <div id="current-songs">
        <h2>Current Playlist</h2>
        <div v-for="song in this.songs" :key="song.song_id">
          <song-display  :song="song"/>
          <button type="button" @click="deleteSong(song.id)">
            Delete
          </button>
        </div>
      </div>
      <div id="search-page">
        <h2>Add Songs</h2>
        <div id="search-submit">
          <div id="search">
            <form v-on:submit.prevent="searchForTrack">
              <label for="input"
                >Search for a song on Spotify by title or artist:</label
              >
              <br /><br />
              <input
                type="text"
                name="input"
                placeholder="Track"
                v-model="search.track"
                class="search-input"
              />
              <input type="text" placeholder="Artist" v-model="search.artist" class="search-input"/>
              <br /><br />
              <button type="submit">
                Search
              </button>
              <button type="button" @click="falseIsShowing(); resetMenus()">Cancel</button>
            </form>
          </div>
        </div>

        <div v-if="isShowing">
          <div v-for="song in searchTracks.tracks.items" :key="song.id">
            <div>
              <song-display v-bind:song="song" />
              <label for="add" class="checkbox-labels"
                >Add Song</label
              >
              <input
                type="radio"
                id="add"
                v-bind:value="song.id"
                v-model="songId"
              />
              <form id="add-song" v-on:submit.prevent="addSong(getSongs)" v-if="song.id === songId">
              <select name="rating" id="rating" v-model="rating">
                <option value="">Rating</option>
                <option value="1">1 Star</option>
                <option value="2">2 Stars</option>
                <option value="3">3 Stars</option>
                <option value="4">4 Stars</option>
                <option value="5">5 Stars</option>
              </select>
              <select id="genre-song" v-model="event.playlist.genres" multiple>
                <option value="">Choose a genre</option>
                <option
                  v-for="genre in allGenres"
                  :key="genre.id"
                  v-bind:value="genre"
                >
                  {{ genre.name }}
                </option>
              </select>
              <button type="submit" id="save">Save</button>
            </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import eventService from "../services/EventService";
import spotifyService from "../services/SpotifyService.js";
import songService from "../services/SongService";
import SongDisplay from "@/components/SongDisplay.vue";

export default {
  name: "my-events",
  components: {
    SongDisplay,
  },
  data() {
    return {
      isShowing: false,
      songs: [],
      searchTracks: {
        tracks: {
          items: [],
        },
      },
      search: {
        track: "",
        artist: "",
      },
      allGenres: [],
      rating: "",
      songId: "",
      isCreating: false,
      event: {
        name: "",
        description: "",
        time: "",
        date: "",
        theme: "",
        djUsername: this.$store.state.user.username,
        playlist: {
          name: "",
          description: "",
          spotifyId: " ",
          genres: []
        },
      },
    };
  },
  computed: {
    isDJ() {
      return this.$store.state.user.username === this.event.djUsername;
    },
  },
  methods: {
    getSongs() {
      songService.getSongs(this.$store.state.user.username).then((response) => {
        this.songs = response.data;
    });  
    },
    getAllEvents(){
    eventService.getAllEvents().then((response) => {
      if (response.status == 200) {
        const djEvents = response.data.filter(event => {
          return event.djUsername === this.$store.state.user.username;
        });
        this.$store.commit("SET_EVENTS", djEvents) 
      }
    });
    },
    searchForTrack() {
      if (this.search.track && this.search.artist) {
        spotifyService
          .searchTracks(
            `track:${this.search.track} artist:${this.search.artist}`
          )
          .then((response) => {
            this.searchTracks = response.body;
          });
      } else if (this.search.artist) {
        spotifyService
          .searchTracks(`artist:${this.search.artist}`)
          .then((response) => {
            this.searchTracks = response.body;
          });
      } else if (this.search.track) {
        spotifyService
          .searchTracks(`track:${this.search.track}`)
          .then((response) => {
            this.searchTracks = response.body;
          });
      }
      this.trueIsShowing();
    },
    addSong(callback) {
      const song = this.searchTracks.tracks.items.find((track) => {
        return track.id === this.songId;
      });
      if (!this.rating) {
        this.rating = 0;
      }
      const addedSong = {
        id: song.id,
        name: song.name,
        preview_url: song.preview_url,
        spotify: song.external_urls.spotify,
        rating: this.rating,
        likes: 0,
        dislikes: 0,
        genres: this.event.playlist.genres,
        artists: song.artists,
      };
      songService.addSongToDjList(addedSong).then((response) => {
        if (response.status === 201 || response.status === 200) {
          callback();
          window.alert("Song successfully added");
        } else {
          window.alert("There was an issue adding this song to your playlist");
        }
      });
      this.falseIsShowing();
      this.resetMenus();
      this.songId = 0;
    },
    deleteSong(songId) {
      const confirmation = confirm("Are you sure you want to delete this song?");
      if (confirmation) {
        songService.deleteSongFromDjList(songId).then(response => {
          if (response.status === 204) {
            alert("Song successfully deleted");
            this.getSongs();
          }
        })
      }
    },
    falseIsShowing() {
      this.isShowing = false;
    },
    trueIsShowing() {
      this.isShowing = true;
    },
    createForm() {
      this.isCreating = !this.isCreating;
    },
    createEvent() {
      const date = new Date(this.event.date + " " + this.event.time);
      const militaryTime = date.getHours() + ":" + date.getMinutes() + ":00";
      this.event.time = militaryTime;
      eventService
        .createEvent(this.event)
        .then((response) => {
          if (response.status === 201) {
            this.getAllEvents();
            this.isCreating = false;
            this.event = {
              name: "",
              description: "",
              time: "",
              date: "",
              theme: "",
              djUsername: this.$store.state.user.username,
              playlist: {
                name: "",
                description: "",
                spotifyId: " ",
                genres: []
              },
            };
          }
        })
        .catch((error) => {
          this.error = error.response.data.message;
        });
    },
    resetMenus() {
      this.event.playlist.genres = [];
      this.rating = 0;
      this.search = {};
    },
     getSpotifyToken() {
      spotifyService.getToken().then((response) => {
        spotifyService.setAccessToken(response.data.access_token);
      });
     },
     getAllGenres() {
       songService.getGenres().then((response) => {
        if (response.status === 200) {
          this.allGenres = response.data;
        }
      });
     }
  },
  created() {
    this.getSpotifyToken();
    this.getSongs();
    this.getAllEvents();
    this.getAllGenres();
  },
};
</script>

<style scoped>
.edit {
  display: grid;
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
}
label {
  font-family: arial;
  margin-top: 10px;
}
input {
  padding-top: 5px;
  padding-bottom: 5px;
  font-size: 16px;
}
.checkbox-labels {
  font-size: 13px;
}
#add-song {
  display: inline-block;
  margin-left: 20px;
}
#search {
  text-align: center;
}
#add {
  margin-right: 10px;
  margin-left: 5px;
}
#save {
  height: 20px;
  font-size: 13px;
  margin-bottom: 10px;
  margin-left: 10px;
}
#rating {
  min-width: 40px;
  margin-left: 10px;
  margin-top: 20px;
}
#genre-song {
  margin-top: 5px;
  margin-left: 10px;
}
#genre-label {
  display: inline-block;
  margin-left: 10px;
  margin-right: 10px;
  margin-bottom: 10px;
  font-size: 13px;
}
#search-submit {
  display: flex;
  flex-direction: row;
  justify-content: space-evenly;
}
#submit {
  display: flex;
  flex-direction: column;
  font-size: 20px;
}
#submit-song {
  text-align: center;
}
#save {
  display: inline-block;
  margin-left: 10px ;
}
#song-container {
  display: flex;
  justify-content: space-evenly;
}
h1 {
  text-align: center;
}
h2 {
  text-align: center;
}
button {
  margin-left: 5px;
  margin-right: 5px;
}
.search-input {
  margin-left: 5px;
  margin-right: 5px;
}
.create-input {
  display: block;
  margin-bottom: 5px;
}
.create {
  margin-top: 20px;
}
.submit-created {
  margin-top: 10px;
  width: 300px;
}
</style>
