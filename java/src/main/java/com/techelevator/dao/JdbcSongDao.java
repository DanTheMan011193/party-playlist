package com.techelevator.dao;

import com.techelevator.model.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcSongDao implements SongDao {
    private final JdbcTemplate jdbcTemplate;

    public JdbcSongDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Song> getAllSongs(int userId) {
        List<Song> allSongs = new ArrayList<>();

        String songSql = "SELECT songs.song_id, songs.title, songs.spotify_link, songs.preview, dj_song.song_rating " +
                "FROM public.songs " +
                "LEFT JOIN dj_song ON dj_song.song_id = songs.song_id " +
                "WHERE dj_song.dj_id = ?;";

        SqlRowSet songResults = jdbcTemplate.queryForRowSet(songSql, userId);

        while (songResults.next()) {
            Song song = mapRowToSong(songResults);

            String songId = songResults.getString("song_id");

            String artistSql = "SELECT artist.artist_id, artist.name " +
                    "FROM public.artist " +
                    "JOIN artist_song ON artist_song.artist_id = artist.artist_id " +
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

            allSongs.add(song);
        }

        return allSongs;
    }

    @Override
    public Song addSong(int userId, Song song) {
        String sql = "INSERT INTO public.songs( " +
                " song_id, title, spotify_link, preview) " +
                " VALUES (?, ?, ?, ?)" +
                " ON CONFLICT DO NOTHING;";

        jdbcTemplate.update(sql, song.getId(), song.getName(), song.getSpotifyUri(), song.getPreview());

        for (Artist artist : song.getArtists()) {
            String sqlCreateArtist = "INSERT INTO public.artist( " +
                    " artist_id, name) " +
                    " VALUES (?, ?) " +
                    " ON CONFLICT DO NOTHING";

            jdbcTemplate.update(sqlCreateArtist, artist.getId(), artist.getName());

            String sqlLinkArtistSong = "INSERT INTO public.artist_song( " +
                    " artist_id, song_id) " +
                    " VALUES (?, ?) " +
                    " ON CONFLICT DO NOTHING;";

            jdbcTemplate.update(sqlLinkArtistSong, artist.getId(), song.getId());
        }

        for (Genre genre : song.getGenres()) {
            String sqlLinkGenreSong = "INSERT INTO public.genre_song( " +
                    " song_id, genre_id) " +
                    " VALUES (?, ?)" +
                    " ON CONFLICT DO NOTHING;";

            jdbcTemplate.update(sqlLinkGenreSong, song.getId(), genre.getId());
        }

        String sqlDjPlaylist = "INSERT INTO public.dj_song( " +
                " song_id, dj_id, song_rating) " +
                " VALUES (?, ?, ?) " +
                " ON CONFLICT DO NOTHING;";
        jdbcTemplate.update(sqlDjPlaylist, song.getId(), userId, song.getRating());

        //TODO work on try/catches

        return this.getSongById(song.getId());
    }

    @Override
    public void updateSong(SongDto songDto) {
        //TODO implement updateSong

    }

    @Override
    public void deleteSong(int songId, int userId) {

        String sql = "DELETE FROM public.songs\n" +
                "\tWHERE song_id = ?";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, songId);

        //TODO finish this method when brain works again



        //TODO implement deleteSong
    }

    @Override
    public Song getSongById(String songId) {
        String songSql = "SELECT songs.song_id, songs.title, songs.spotify_link, songs.preview " +
                "FROM public.songs " +
                "WHERE songs.song_id = ?;";

        SqlRowSet songResults = jdbcTemplate.queryForRowSet(songSql, songId);

        Song song = new Song();

        if (songResults.next()) {
            song = mapRowToSong(songResults);

            String artistSql = "SELECT artist.artist_id, artist.name " +
                    "FROM public.artist " +
                    "JOIN artist_song ON artist_song.artist_id = artist.artist_id " +
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
        }

        return song;
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
            song.setVotes(rs.getInt("votes"));
        } catch (Exception e) {
            song.setVotes(0);
        }
        song.setPreview(rs.getString("preview"));
        song.setSpotifyUri(rs.getString("spotify_link"));

        return song;
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
