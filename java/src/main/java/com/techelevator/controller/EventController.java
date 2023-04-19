package com.techelevator.controller;

import com.techelevator.dao.EventDao;
import com.techelevator.dao.PlaylistDao;
import com.techelevator.dao.SongDao;
import com.techelevator.dao.UserDao;
import com.techelevator.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/events")
public class EventController {
    private UserDao userDao;
    private SongDao songDao;
    private PlaylistDao playlistDao;
    private EventDao eventDao;

    public EventController(UserDao userDao, SongDao songDao, PlaylistDao playlistDao, EventDao eventDao) {
        this.userDao = userDao;
        this.songDao = songDao;
        this.playlistDao = playlistDao;
        this.eventDao = eventDao;
    }

    @GetMapping("")
    public List<Event> getAllEvents() {
        return eventDao.getAllEvents();
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Event createEvent(@RequestBody Event event, Principal principal) {
        event.setId(0);
        event.getPlaylist().setPlaylistId(0);
        event.getPlaylist().setSongs(new ArrayList<>());
        if (event.getHosts() == null) {
            event.setHosts(new ArrayList<>());
        }
        int userId = userDao.findIdByUsername(principal.getName());
        return eventDao.createEvent(event, userId);
    }

    @PutMapping("/{eventId}")
    public void updateEvent(@RequestBody EventDto eventInfo, @PathVariable int eventId) {
        eventDao.updateEvent(eventInfo, eventId);
    }

    @GetMapping("/{eventId}")
    public Event getEventById(@PathVariable int eventId) {
        return eventDao.getEventById(eventId);
    }

    @DeleteMapping("/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable int eventId) {
        eventDao.deleteEvent(eventId);
    }

    @PostMapping("/{eventId}/hosts")
    public void setUserAsHost(@RequestBody UserDto user, @PathVariable int eventId) {
        int userId = userDao.findIdByUsername(user.getUsername());
        eventDao.addHost(eventId, userId);
    }

    @PostMapping("/{eventId}/hosts/delete")
    public void deleteHost(@RequestBody UserDto user, @PathVariable int eventId) {
        int userId = userDao.findIdByUsername(user.getUsername());
        eventDao.deleteHost(eventId, userId);
    }

    @GetMapping("/{eventId}/playlists/{playlistId}")
    public Playlist getPlaylistByForEventById(@PathVariable int playlistId, Principal principal) {
        int userId = userDao.findIdByUsername(principal.getName());
        return playlistDao.getPlayListById(playlistId, userId);
    }
}
