<template>
    <div class="song" :class="vetoClass">
      <div class="song-info">
        <div>
          <div class="p5"><strong>{{ song.name }}</strong></div>
          <span class="stars"><img
              v-for="n in song.rating"
               :key="n"
               class="gold-star"
               src="../images/Gold Star.png"
               alt="Gold star"/></span>
          <div class="artist-wrapper">
            <p v-for="artist in song.artists" :key="artist.id">{{ artist.name }}</p>
          </div>
        </div>
      </div>
      <div class="song-links">
        <slot></slot>
        <audio controls @play="setVolume">
          <source :src="song.preview_url" type="audio/mpeg" /><p>
            <a href="song.preview_url">Preview{{ song.preview_url }}</a></p>
        </audio>
        <a :href="song.spotify" target="_blank">
          <img src="../images\image-gallery-spotify-logo-21.png" alt="Spotify" height="56"/>
        </a>
      </div>
    </div>
</template>

<script>
export default {
  name: "SongList",
  props: ["song"],
  data() {
    return {
    };
  },
  methods: {
    setVolume(event) {
      event.target.volume = 0.2;
    },
  },
  computed: {
    vetoClass() {
      if (this.song.vetoed) {
        return "unavailable-song";
      }
      return "default"
    }
  }
};
</script>

<style scoped>
.unavailable-song{
  opacity: 0.5;
  pointer-events: none;
}
.likesDislikes{
  display: flex;
  flex-direction: column;
}
.song {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  background: linear-gradient(to bottom, #f2f8f1, #cecfcb);
  border-radius: 19px;
  margin-top: 10px;
  margin-bottom: 8px;
  margin-right: 10px;
  box-shadow: 0px 0px 20px rgba(0, 0, 0, 0.3);
}
.Stars {
  display: flex;
  flex-grow: 2;
  align-items: right;
  margin-left: 10;
}
.song-info {
  display: flex;
  flex-direction: row;
  align-items: center;
}
.p5 {
  display: inline-block;
  margin-top: 15px;
  margin-left: 15px;
  margin-right: 10px;
  font-size: 18px;

}
.artist-wrapper {
  display: flex;
  flex-wrap: wrap;
  margin-left: 15px;
  font-size: 15px;
}
.song-links {
  display: flex;
  align-items: center;
}
.gold-star {
  width: 15px;
  height: 15px;
  margin-right: 1px;
}

audio::-webkit-media-controls-panel {
  background: radial-gradient(#c9c6c6, #c2c2c2);
}
audio::-webkit-media-controls-play-button {
  background-color: #898d86;
  border-radius: 50%;
}
audio::-webkit-media-controls-play-button:hover {
  background-color: #9da09b;
}
audio::-webkit-media-controls-current-time-display {
  color: black;
}
audio::-webkit-media-controls-time-remaining-display {
  color: black(255, 255, 255);
}
audio::-webkit-media-controls-mute-button {
  background-color: #898d86;
  border-radius: 50%;
}
audio::-webkit-media-controls-volume-slider {
  background-color: #898d86;
  border-radius: 25px;
  padding-left: 8px;
  padding-right: 8px;
}
</style>