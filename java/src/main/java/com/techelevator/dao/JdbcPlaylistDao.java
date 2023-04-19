package com.techelevator.dao;

import com.techelevator.model.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcPlaylistDao implements PlaylistDao {
    private final JdbcTemplate jdbcTemplate;
    private final SongDao songDao;

    public JdbcPlaylistDao(JdbcTemplate jdbcTemplate, SongDao songDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.songDao = songDao;
    }

    @Override
    public Playlist getPlayListById(int playlistId, int userId) {
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

    @Override
    public Playlist createPlaylist(Playlist playlist) {
        String sql = "INSERT INTO public.playlists( " +
                "name, description, spotify_id) " +
                "VALUES (?, ?, ?) RETURNING playlist_id";
        int playlistId = jdbcTemplate.queryForObject(sql, Integer.class, playlist.getName(), playlist.getDescription(), playlist.getSpotifyId());
        playlist.setPlaylistId(playlistId);

        List<Genre> genres = playlist.getGenres();

        String sqlGenre = "INSERT INTO public.playlist_genre( " +
                "playlist_id, genre_id) " +
                "VALUES (?, ?);";

        for (Genre genre : genres) {
            jdbcTemplate.update(sqlGenre, playlist.getPlaylistId(), genre.getId());
        }

        return playlist;
    }

    @Override
    public void addSongToPlaylist(int playlistId, String songId, int userId) {
            String sql = "INSERT INTO public.playlist_song( " +
                    "playlist_id, song_id, vetoed, submitted, likes, dislikes) " +
                    "VALUES (?, ?, false, false, 0, 0)";

            jdbcTemplate.update(sql, playlistId, songId);
    }

    @Override
    public void deleteSongFromPlaylist(int playlistId, String songId, int userId) {
            String sql = "DELETE FROM public.playlist_song\n" +
                    "\tWHERE playlist_id = ? AND song_id = ?";

            jdbcTemplate.update(sql, playlistId, songId);
    }

    @Override
    public void updateLikes(int playlistId, String songId) {
        String sql = "UPDATE playlist_song SET likes = likes + 1 " +
                "WHERE playlist_id = ? AND song_id = ?";

        jdbcTemplate.update(sql, playlistId, songId);
    }

    @Override
    public void updateDislikes(int playlistId, String songId) {
        String sql = "UPDATE playlist_song SET dislikes = dislikes + 1 " +
                "WHERE playlist_id = ? AND song_id = ?";

        jdbcTemplate.update(sql, playlistId, songId);
    }

    @Override
    public void updatePlaylist(int playlistId, String name, String description, int userId) {
            String sql = "UPDATE public.playlists " +
                    "SET name=?, description=? " +
                    "WHERE playlist_id=?";

            jdbcTemplate.update(sql, name, description, playlistId);
    }

    @Override
    public void vetoSong(int playlistId, String songId) {
        String sql = "UPDATE playlist_song SET vetoed = true WHERE playlist_id = ? AND song_id = ?";
        jdbcTemplate.update(sql, playlistId, songId);
    }

    @Override
    public void submitSong(int playlistId, String songId) {
        String sql = "UPDATE playlist_song SET submitted = true WHERE playlist_id = ? AND song_id = ?";
        jdbcTemplate.update(sql, playlistId, songId);
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
        song.setVetoed(rs.getBoolean("vetoed"));
        song.setSubmitted(rs.getBoolean("submitted"));
        song.setPreview(rs.getString("preview"));
        song.setSpotifyUri(rs.getString("spotify_link"));

        return song;
    }

    private Playlist mapRowToPlaylist(SqlRowSet rs) {
        Playlist playlist = new Playlist();

        playlist.setName(rs.getString("name"));
        playlist.setDescription(rs.getString("description"));
        playlist.setPlaylistId(rs.getInt("playlist_id"));
        playlist.setSpotifyId(rs.getString("spotify_id"));

        return playlist;
    }

    private Artist mapRowToArtist(SqlRowSet rs) {
        Artist artist = new Artist();

        artist.setId((rs.getString("artist_id")));
        artist.setName(rs.getString("name"));

        return artist;
    }

    private Genre mapRowToGenre(SqlRowSet rs) {
        Genre genre = new Genre();

        genre.setId((rs.getInt("genre_id")));
        genre.setName(rs.getString("name"));

        return genre;
    }
}
