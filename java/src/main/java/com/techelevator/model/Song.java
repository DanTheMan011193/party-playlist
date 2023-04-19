package com.techelevator.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Song {
    private String id;
    private String name;
    private List<Artist> artists;
    private List<Genre> genres;
    private int rating;
    private int likes;
    private int dislikes;
    private boolean vetoed;
    private boolean submitted;
    @JsonProperty("preview_url")
    private String preview;
    @JsonProperty("spotify")
    private String spotifyUri;

    public Song() {
    }

    public Song(String id, String name, List<Artist> artists, List<Genre> genres, int rating, int likes, int dislikes, boolean vetoed, boolean submitted, String preview, String spotifyUri) {
        this.id = id;
        this.name = name;
        this.artists = artists;
        this.genres = genres;
        this.rating = rating;
        this.likes = likes;
        this.dislikes = dislikes;
        this.vetoed = vetoed;
        this.submitted = submitted;
        this.preview = preview;
        this.spotifyUri = spotifyUri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public String getSpotifyUri() {
        return spotifyUri;
    }

    public void setSpotifyUri(String spotifyUri) {
        this.spotifyUri = spotifyUri;
    }

    public boolean isVetoed() {
        return vetoed;
    }

    public void setVetoed(boolean vetoed) {
        this.vetoed = vetoed;
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    @Override
    public String toString() {
        return "Song{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", artists=" + artists +
                ", genres=" + genres +
                ", rating=" + rating +
                ", likes=" + likes +
                ", dislikes=" + dislikes +
                ", vetoed=" + vetoed +
                ", submitted=" + submitted +
                ", preview='" + preview + '\'' +
                ", spotifyUri='" + spotifyUri + '\'' +
                '}';
    }
}
