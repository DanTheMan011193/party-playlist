import axios from 'axios';

let SpotifyWebApi = require('spotify-web-api-node');

const spotifyApi = new SpotifyWebApi();

export default {
    getToken() {
        return axios.get('/spotify/token');
    },
    setAccessToken(token) {
        spotifyApi.setAccessToken(token);
    },
    searchTracks(params) {
        return spotifyApi.searchTracks(params, {limit: 10, offset: 0});
    }
}