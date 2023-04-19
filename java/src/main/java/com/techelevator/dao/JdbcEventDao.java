package com.techelevator.dao;

import com.techelevator.model.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcEventDao implements EventDao {
    private JdbcTemplate jdbcTemplate;
    private PlaylistDao playlistDao;

    public JdbcEventDao(JdbcTemplate jdbcTemplate, PlaylistDao playlistDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.playlistDao = playlistDao;
    }

    @Override
    public List<Event> getAllEvents() {
        List<Event> allEvents = new ArrayList<>();
        String sql = "SELECT events.event_id, events.event_name, events.description, events.playlist_id, events.date, events.\"time\", events.theme, events.dj_id, users.username " +
                "FROM public.events " +
                "JOIN users on events.dj_id = users.user_id";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);

        while (results.next()) {

            Event event = mapRowToEvent(results);

            String hostSql = "SELECT users.user_id, users.username " +
                    "FROM public.users " +
                    "JOIN host_event on users.user_id = host_event.user_id " +
                    "WHERE host_event.event_id = ?";

            SqlRowSet hostResults = jdbcTemplate.queryForRowSet(hostSql, event.getId());

            List<Host> hosts = new ArrayList<>();

            while (hostResults.next()) {
                hosts.add(mapRowToHost(hostResults));
            }
            event.setHosts(hosts);

            Playlist playlist = playlistDao.getPlayListById(results.getInt("playlist_id"), results.getInt("dj_id"));
            event.setPlaylist(playlist);

            allEvents.add(event);
        }

        return allEvents;
    }

    @Override
    public Event createEvent(Event event, int userId) {
        int playlistId = playlistDao.createPlaylist(event.getPlaylist()).getPlaylistId();

        String sqlEvent = "INSERT INTO public.events( " +
                "event_name, dj_id, description, playlist_id, date, time, theme) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING event_id";

        int eventId = jdbcTemplate.queryForObject(sqlEvent, int.class, event.getName(), userId, event.getDescription(), playlistId, event.getDate(), event.getTime(), event.getTheme());

        return this.getEventById(eventId);
    }

    @Override
    public Event getEventById(int eventId) {
        Event event = new Event();

        String sqlEvent = "SELECT events.event_id, events.event_name, events.description, events.playlist_id, events.date, events.\"time\", events.theme, events.dj_id, users.username " +
                "FROM public.events " +
                "JOIN users on events.dj_id = users.user_id " +
                "WHERE events.event_id = ?";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlEvent, eventId);

        if (results.next()) {
            event = mapRowToEvent(results);
        }

        String hostSql = "SELECT users.user_id, users.username " +
                "FROM public.users " +
                "JOIN host_event on users.user_id = host_event.user_id " +
                "WHERE host_event.event_id = ?";

        SqlRowSet hostResults = jdbcTemplate.queryForRowSet(hostSql, event.getId());

        List<Host> hosts = new ArrayList<>();

        while (hostResults.next()) {
            Host host = mapRowToHost(hostResults);
            hosts.add(host);
        }
        event.setHosts(hosts);

        Playlist playlist = playlistDao.getPlayListById(results.getInt("playlist_id"), results.getInt("dj_id"));
        event.setPlaylist(playlist);

        return event;
    }

    @Override
    public void updateEvent(EventDto eventInfo, int eventId) {
        String sql = "UPDATE public.events " +
                "SET event_name=?, description=?, date=?, \"time\"=?, theme=? " +
                "WHERE event_id = ?;";
        jdbcTemplate.update(sql, eventInfo.getName(), eventInfo.getDescription(), eventInfo.getDate(), eventInfo.getTime(), eventInfo.getTheme(), eventId);
    }

    @Override
    public void deleteEvent(int eventId) {
        int playlistId = jdbcTemplate.queryForObject("SELECT playlist_id FROM events WHERE event_id = ?", int.class, eventId);

        String sql = "DELETE FROM public.host_event " +
                "   WHERE event_id = ?; " +
                "DELETE FROM public.events " +
                "   WHERE event_id = ?; " +
                "DELETE FROM public.playlist_song " +
                "   WHERE playlist_id = ?; " +
                "DELETE FROM public.playlist_genre " +
                "   WHERE playlist_id = ?; " +
                "DELETE FROM public.playlists " +
                "   WHERE playlist_id = ?;";

        jdbcTemplate.update(sql, eventId, eventId, playlistId, playlistId, playlistId);
    }

    @Override
    public void addHost(int eventId, int userId) {
        String sql = "INSERT INTO public.host_event( " +
                "user_id, event_id) " +
                "VALUES (?, ?) " +
                "ON CONFLICT DO NOTHING;";
        jdbcTemplate.update(sql, userId, eventId);

        String roleSql = "UPDATE public.users " +
                "SET role='ROLE_HOST' " +
                "WHERE user_id = ?;";
        jdbcTemplate.update(roleSql, userId);
    }

    @Override
    public void deleteHost(int eventId, int userId) {
        String sql = "DELETE FROM public.host_event  " +
                "WHERE user_id = ? AND event_id = ?";
        jdbcTemplate.update(sql, userId, eventId);

        String roleSql = "SELECT COUNT(user_id) as num " +
                "FROM host_event " +
                "WHERE user_id = ?";
        int count = jdbcTemplate.queryForObject(roleSql, int.class, userId);

        if (count == 0) {
            roleSql = "UPDATE public.users " +
                    "SET role='ROLE_USER' " +
                    "WHERE user_id = ?;";
            jdbcTemplate.update(roleSql, userId);
        }
    }

    @Override
    public Playlist getPlaylistForEventById(int playlistId, int userId) {
            Playlist playlist = new Playlist();

            String sql = "SELECT playlist_id, name, description, spotify_id " +
                    "FROM public.playlists " +
                    "WHERE playlist_id = ?";

            SqlRowSet playlistResults = jdbcTemplate.queryForRowSet(sql, playlistId);

            if (playlistResults.next()) {
                playlist = mapRowToPlaylist(playlistResults);
            }

            String sqlSong = "SELECT song_id " +
                    "FROM playlist_song " +
                    "WHERE playlist_id = ?";

            SqlRowSet songIdResults = jdbcTemplate.queryForRowSet(sqlSong, playlistId);

            List<Song> songs = new ArrayList<>();

            while (songIdResults.next()) {
                String songId = songIdResults.getString("song_id");

                String songSql = "SELECT songs.song_id, songs.title, songs.spotify_link, songs.preview, playlist_song.vetoed, playlist_song.submitted, playlist_song.likes, playlist_song.dislikes, dj_song.song_rating " +
                        "FROM songs " +
                        "LEFT JOIN dj_song ON songs.song_id = dj_song.song_id " +
                        "LEFT JOIN playlist_song ON songs.song_id = playlist_song.song_id " +
                        "WHERE songs.song_id = ? " +
                        "AND playlist_song.playlist_id = ? ";

                SqlRowSet songResults = jdbcTemplate.queryForRowSet(songSql, songId, playlistId);

                Song song = new Song();

                if (songResults.next()) {
                    song = mapRowToSong(songResults);

                    String artistSql = "SELECT artists.artist_id, artists.name " +
                            "FROM public.artists " +
                            "JOIN artist_song ON artist_song.artist_id = artists.artist_id " +
                            "WHERE artist_song.song_id = ?";

                    SqlRowSet artistResults = jdbcTemplate.queryForRowSet(artistSql, songId);

                    List<Artist> artists = new ArrayList<>();

                    while (artistResults.next()) {
                        Artist artist = mapRowToArtist(artistResults);

                        artists.add(artist);
                    }

                    song.setArtists(artists);

                    String genreSql = "SELECT genres.genre_id, genres.name " +
                            "FROM public.genres " +
                            "JOIN genre_song ON genre_song.genre_id = genres.genre_id " +
                            "WHERE genre_song.song_id = ?";

                    SqlRowSet genreResults = jdbcTemplate.queryForRowSet(genreSql, songId);

                    List<Genre> genres = new ArrayList<>();

                    while (genreResults.next()) {
                        Genre genre = mapRowToGenre(genreResults);

                        genres.add(genre);
                    }
                    song.setGenres(genres);
                    songs.add(song);
                }
            }

            playlist.setSongs(songs);

            String sqlGenres = "SELECT genres.genre_id, genres.name " +
                    "FROM playlist_genre " +
                    "JOIN genres ON genres.genre_id = playlist_genre.genre_id " +
                    "WHERE playlist_genre.playlist_id = ?";

            SqlRowSet genreResults = jdbcTemplate.queryForRowSet(sqlGenres, playlist.getPlaylistId());

            List<Genre> genres = new ArrayList<>();

            while (genreResults.next()) {
                genres.add(mapRowToGenre(genreResults));
            }

            playlist.setGenres(genres);

            return playlist;
    }

    private Event mapRowToEvent(SqlRowSet rs) {
        Event event = new Event();

        event.setId(rs.getInt("event_id"));
        event.setName(rs.getString("event_name"));
        event.setDescription(rs.getString("description"));
        event.setDate(LocalDate.parse(rs.getString("date")));
        event.setTime(rs.getTime("time"));
        event.setTheme(rs.getString("theme"));
        event.setDjUsername(rs.getString("username"));

        return event;
    }

    private Host mapRowToHost(SqlRowSet rs) {
        Host host = new Host();

        host.setId(rs.getInt("user_id"));
        host.setName(rs.getString("username"));

        return host;
    }

    private Playlist mapRowToPlaylist(SqlRowSet rs){
        Playlist playlist = new Playlist();

        playlist.setName(rs.getString("name"));
        playlist.setDescription(rs.getString("description"));
        playlist.setPlaylistId(rs.getInt("playlist_id"));
        playlist.setSpotifyId(rs.getString("spotify_id"));

        return playlist;
    }

    private Genre mapRowToGenre(SqlRowSet rs) {
        Genre genre = new Genre();

        genre.setId((rs.getInt("genre_id")));
        genre.setName(rs.getString("name"));

        return genre;
    }

    private Artist mapRowToArtist(SqlRowSet rs) {
        Artist artist = new Artist();

        artist.setId((rs.getString("artist_id")));
        artist.setName(rs.getString("name"));

        return artist;
    }

    private Song mapRowToSong(SqlRowSet rs) {
        Song song = new Song();

        song.setId(rs.getString("song_id"));
        song.setName(rs.getString("title"));
        try {
            song.setRating(rs.getInt("song_rating"));
        } catch (Exception e) {
            song.setRating(0);
        }
        try {
            song.setLikes(rs.getInt("likes"));
        } catch (Exception e) {
            song.setLikes(0);
        }
        try {
            song.setDislikes(rs.getInt("dislikes"));
        } catch (Exception e) {
            song.setDislikes(0);
        }
        song.setPreview(rs.getString("preview"));
        song.setVetoed(rs.getBoolean("vetoed"));
        song.setSubmitted(rs.getBoolean("submitted"));
        song.setSpotifyUri(rs.getString("spotify_link"));

        return song;
    }
}
