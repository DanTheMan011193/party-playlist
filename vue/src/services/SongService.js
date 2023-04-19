import axios from "axios";

export default {
    getSongs(name) {
        return axios.put('/songs/list', {username: name});
      },
    getSong(id) {
        return axios.get(`/songs/${id}`);
    },
    addSongToDjList(song) {
        return axios.post('/songs', song);
    },
    updateSong(id, song) {
        return axios.put(`/songs/${id}`, song)
    },
    deleteSongFromDjList(id) {
        return axios.delete(`/songs/${id}`);
    },
    getGenres() {
        return axios.get('/songs/genres');
    }
}