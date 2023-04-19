import axios from 'axios';

export default {
  getAllEvents() {
    return axios.get('/events');
  },
  getEventById(eventId) {
    return axios.get(`/events/${eventId}`);
  },
  createEvent(event) {
    return axios.post('/events', event);
  },
  updateEvent(eventDto, eventId) {
    return axios.put(`/events/${eventId}`, eventDto);
  },
  deleteEvent(eventId) {
    return axios.delete(`/events/${eventId}`);
  },
  addHostToEvent(eventId, name) {
    return axios.post(`/events/${eventId}/hosts`, { username: name });
  },
  removeHostFromEvent(eventId, name) {
    return axios.post(`/events/${eventId}/hosts/delete`, { username: name });
  },
  saveLikes(song) {
    return axios.put(`/events/playlists/songs/${song.song_id}`, { votes: song.likes });
  },
}

