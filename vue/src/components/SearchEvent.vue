<template>
    <div class="search-event">
        <h1>Search Event</h1>
        <form @submit.prevent="searchEvents">
            <label for="fromDate">From Date:</label>
            <input type="date" v-model="fromDate" id="fromDate">

            <label for="playlistId">Playlist:</label>
            <select v-model="playlistId" id="playlistId">
                <option v-for="playlist in playlists" :key="playlist.id">{{ playlist.name }}</option>
            </select>

            <button type="submit">Search</button>
        </form>

        <div v-if="searchResults.length > 0">
            <h2>Search Results:</h2>

            <ul>
                <li v-for="event in searchResults" :key="event.event_id">
                    {{ event.description }} - {{ event.date_time }} </li>
            </ul>
        </div>
    </div>
</template>

<script>
export default{
    data() {
        return {
            fromDate: null,
            toDate: null,
            playlistId: null,
            playlists: [],
            searchResults: [],
        };
    },
    async created() {
        try{
            const response = await fetch('/api/playlists');
            this.playlists = await response.json();
        } catch (error) {
            console.error(error);
        }
    },
    methods: {
        async searchEvents() {
            try {
                const params = new URLSearchParams ({
                    fromDate: this.fromDate,
                    toDate: this.toDate,
                    playlistId: this.playlistId,
                });

                const response = await fetch(`/api/events?${params}`);
                this.searchResults = await response.json();
            } catch (error) {
                console.error(error);
            }
            },
        },
    };
</script>

<style>

</style>
