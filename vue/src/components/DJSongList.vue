<template>
  <div>
    <div id="current-songs">
        <h2>Current Playlist</h2>
        <song-display v-for="song in this.songs" :key="song.song_id" :song="song">
            <div style=" display: flex; flex-direction: column; justify-content: space-between;">
           </div>
        </song-display>
    </div>

    <div class="addToPlaylist">
        <song-display v-for="song in event.playlist.songs" :key="song.song_id" :song="song">
           <div style=" display: flex; flex-direction: column; justify-content: space-between;">
           </div>
        </song-display>
    </div>
  
    <div class="song-info">
        <song-display v-for="song in event.playlist.songs" :key="song.song_id" :song="song">
            <div style="display: flex; flex-direction: column; justify-content: space-between;">
                <div id="likes">
                    <span>{{ song.likes }}</span>
                    <img src="../images/thumbs-up.png" alt="Likes" width="15" height="15" class="thumb" style="margin-bottom: 10px; cursor: pointer" @click="incrementLikes(song.id)" :class="{disabled: song.clicked || clickedSongs.includes(song.id),}"/>
                </div>
                <div id="dislikes">
                    <span>{{ song.dislikes }}</span>
                    <img src="../images/thumbs-down.png" alt="Dislikes" width="15" height="15" class="thumb" style="margin-bottom: 10px; cursor: pointer" @click="decrementLikes(song.id)" :class="{ disabled: song.clicked || clickedSongs.includes(song.id),}"/>
                </div>
            </div>
        </song-display>
    </div>

  </div>
</template>

<script>
import songService from "../services/SongService";
import SongDisplay from "@/components/SongDisplay.vue";

export default {
  name: "dj-song-list",
  components: {
    SongDisplay,
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
    }
  }
}
</script>

<style>

</style>