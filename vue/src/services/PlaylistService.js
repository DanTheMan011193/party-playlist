import axios from 'axios';

export default {
    getPlaylistById(playlistId) {
        return axios.get(`/playlists/${playlistId}`)
    },

    addLikes(playlistId, songId) {
        return axios.put(`/playlists/${playlistId}/songs/${songId}/likes`)
    },

    deleteLikes(playlistId, songId) {
        return axios.delete(`/playlists/${playlistId}/songs/${songId}/likes`)
    },

    removeSongFromPlaylist(playlistId, songId) {
        return axios.delete(`/playlists/${playlistId}/songs/${songId}`)
    },

    addSongToPlaylist(playlistId, songId) {
        return axios.put(`/playlists/${playlistId}/songs/${songId}`)
    },

    updatePlaylistInfo(playlistId, playlistDto) {
        return axios.put(`/playlists/${playlistId}`, playlistDto)
    },

    vetoSong(playlistId, songId) {
        return axios.put(`/playlists/${playlistId}/songs/${songId}/veto`)
    },

    submitSong(playlistId, songId) {
        return axios.put(`/playlists/${playlistId}/songs/${songId}/submit`)
    }
};