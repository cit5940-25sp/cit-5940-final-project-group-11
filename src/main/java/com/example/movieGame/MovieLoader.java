package com.example.movieGame;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Loads the data from the csv files into collections
 * Data pulled from https://www.kaggle.com/datasets/tmdb/tmdb-movie-metadata
 *
 */
public class MovieLoader {


    private static List<Movie> movies;
    private static HashMap<Integer, Movie> moviesHashMap;
    private static HashSet<Integer> idSet;
    private static HashSet<String> actorSet; //for actor win condition user entry checking
    private static Map<Integer, List<String>> actorMap;
    private static Map<Integer, List<String>> directorMap;
    private static Map<Integer, List<String>> writerMap;
    private static Map<Integer, List<String>> cinematographerMap;
    private static Map<Integer, List<String>> composerMap;
    private static Map<Integer, String> title;
    private static Map<Integer, Long> year;
    private static Map<Integer, List<String>> genreSet;
    private static boolean loaded = false;

    public static void loadAllDataIfNeeded() {
        if (!loaded) {
            try {
                creditCSVRead();
                moviesCSVRead();
                createMovieFromFiles();
                loaded = true;
                System.out.println("✅ Movie and credit data loaded into memory.");
            } catch (IOException e) {
                System.err.println("❌ Failed to load movie data.");
                e.printStackTrace();
            }
        }
    }


    /**
     * Loads in the credit csv file, stores the details in hashmaps.
     * Then sets the cast/crew data into JSON arrays and iterates through
     * Each type of cast/crew is added to hashmap.
     * @throws IOException if there's an issue reading the file
     */
    public static void creditCSVRead() throws IOException {

        //Load in the credit csv file
        InputStream creditsStream = MovieLoader.class.getClassLoader().getResourceAsStream("tmdb_5000_credits.csv");
        InputStreamReader creditsReader = new InputStreamReader(creditsStream, StandardCharsets.UTF_8);
        BufferedReader creditsBReader = new BufferedReader(creditsReader);

        //Create Hashmaps to store details
        title = new HashMap<>();
        actorMap = new HashMap<>();
        directorMap = new HashMap<>();
        writerMap = new HashMap<>();
        cinematographerMap = new HashMap<>();
        composerMap = new HashMap<>();
        idSet = new HashSet<>();
        actorSet = new HashSet<>();

        //Create string for one row of the spreadsheet
        StringBuilder rowBuilder = new StringBuilder();
        String line;
        int lineNum = 1;
        creditsBReader.readLine(); // Skip header

        //While there is still lines to read, keep going through and parsing
        while ((line = creditsBReader.readLine()) != null) {

            //Create a field from a single row
            rowBuilder.append(line).append("\n");
            String fullLine = rowBuilder.toString().trim();
            rowBuilder.setLength(0);
            String[] fields = parseCsvLine(fullLine);

            //Get the id
            //Get the column that contains the cast and crew JSON strings
            int id = Integer.parseInt(fields[0].trim());
            idSet.add(id);
            String titleString = fields[1].trim();
            title.put(id, titleString);
            String castString = fields[2].trim();
            String crewString = fields[3].trim();

            //Cast and crew as JSON arrays
            JSONArray castArray = new JSONArray(castString);
            JSONArray crewArray = new JSONArray(crewString);

            //Iterate over JSON objects and extract relevant information
            //Actors, directors, writers, cinematographers, composers

            //Iterate over cast
            List<String> actorNames = new ArrayList<>();
            for (int i = 0; i < castArray.length(); i++) {
                JSONObject castMember = castArray.getJSONObject(i);
                actorNames.add(castMember.getString("name"));
                actorSet.add(castMember.getString("name"));
            }
            actorMap.put(id, actorNames);

            //Iterate over crew
            List<String> directorNames = new ArrayList<>();
            List<String> writerNames = new ArrayList<>();
            List<String> cinematographerNames = new ArrayList<>();
            List<String> composerNames = new ArrayList<>();

            for (int i = 0; i < crewArray.length(); i++) {
                JSONObject crewMember = crewArray.getJSONObject(i);
                if ("Director".equals(crewMember.getString("job"))) {
                    directorNames.add(crewMember.getString("name"));
                }
                if ("Screenplay".equals(crewMember.getString("job"))) {
                    writerNames.add(crewMember.getString("name"));
                }
                if ("Cinematography".equals(crewMember.getString("job"))) {
                    cinematographerNames.add(crewMember.getString("name"));
                }
                if ("Original Music Composer".equals(crewMember.getString("job"))) {
                    composerNames.add(crewMember.getString("name"));
                };

                //Put the list of names into the maps
                directorMap.put(id, directorNames);
                writerMap.put(id, writerNames);
                cinematographerMap.put(id, cinematographerNames);
                composerMap.put(id, composerNames);

            }

        }

    }


    /**
     * Loads in the movies csv file, stores the details in hashmaps.
     * Extracts the id, year, and genre
     *
     * @throws IOException if there's an issue reading the file
     */
    public static void moviesCSVRead() throws IOException {

        //Load in the movies csv file
        InputStream moviesStream = MovieLoader.class.getClassLoader().getResourceAsStream("tmdb_5000_movies.csv");
        InputStreamReader moviesReader = new InputStreamReader(moviesStream, StandardCharsets.UTF_8);
        BufferedReader moviesBReader = new BufferedReader(moviesReader);

        //Store year and genre
        year = new HashMap<>();
        genreSet = new HashMap<>();

        //Create string for one row of the spreadsheet
        StringBuilder rowBuilder = new StringBuilder();
        String line;
        int lineNum = 1;
        moviesBReader.readLine(); // Skip header

        //While there is still lines to read, keep going through and parsing
        while ((line = moviesBReader.readLine()) != null) {

            //Create a field from a single row
            rowBuilder.append(line).append("\n");
            String fullLine = rowBuilder.toString().trim();
            rowBuilder.setLength(0);
            String[] fields = parseCsvLine(fullLine);

            //Extract the id, year and genre
            int id = Integer.parseInt(fields[1].trim());

            if (!(fields[3].isEmpty())) {
                Long rawYear = Long.valueOf(fields[3].trim().split("/")[2]);
                year.put(id, rawYear);
            }

            JSONArray genreArray = new JSONArray(fields[0]);
            List<String> genresList = new ArrayList<>();
            for (int j = 0; j < genreArray.length(); j++) {
                genresList.add(genreArray.getJSONObject(j).getString("name"));
            }
            genreSet.put(id, genresList);
        }
    }

    /**
     * Parses each line of the csv and returns an array of fields (ie each column)
     *
     * @param line csv line
     * @return String array of fields (ie each column)
     */
    public static String[] parseCsvLine(String line) {

        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder field = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                // Toggle inQuotes unless it's a double-quote escape
                if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    field.append('"');
                    i++; // skip next quote
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                result.add(field.toString().trim());
                field.setLength(0);
            } else {
                field.append(c);
            }
        }
        result.add(field.toString().trim()); // last field
        return result.toArray(new String[0]);
    }

    /**
     * Creates an arraylist and hashmap of Movie objects, pulled from the files
     * This information is later used for the autocomplete functionality, and to validate connections
     *
     * @return returns a list of movie objects
     */
    public static List<Movie> createMovieFromFiles() {

        //Initialise movies
        movies = new ArrayList<>();
        moviesHashMap = new HashMap<>();

        //Create list actors, directors,  writers, cinematographers and composers
        //Convert to HashSet<String>
        for (Integer id : idSet) {
            String titleId = title.get(id);
            Long yearId = year.get(id);
            HashSet<String> genres = new HashSet<>(genreSet.getOrDefault(id, Collections.emptyList()));
            HashSet<String> actors = new HashSet<>(actorMap.getOrDefault(id, Collections.emptyList()));
            HashSet<String> directors = new HashSet<>(directorMap.getOrDefault(id, Collections.emptyList()));
            HashSet<String> cinematographers = new HashSet<>(cinematographerMap.getOrDefault(id, Collections.emptyList()));
            HashSet<String> writers = new HashSet<>(writerMap.getOrDefault(id, Collections.emptyList()));
            HashSet<String> composers = new HashSet<>(composerMap.getOrDefault(id, Collections.emptyList()));

            //Create a movie object for each row
            Movie movie = new Movie(titleId, id, yearId, genres, actors, directors, writers, cinematographers, composers);
            movies.add(movie);
            moviesHashMap.put(id, movie);  //hashmap for searching
        }

        //Return movie object
        return movies;
    }

    /**
     * Getter for the movies hashmap
     * Used to look up the corresponding movie object (based on its id) after pulling it from autocomplete
     *
     * @return returns the hashmap of movies objects
     */
    public static HashMap<Integer, Movie> getMoviesHashMap() {
        return moviesHashMap;
    }

    /**
     * Getter for actor hashset
     * Used to look up the corresponding movie object (based on its id) after pulling it from autocomplete
     *
     * @return returns the hashset of movies objects
     */
    public static HashSet<String> getActorHashSet() {
        return actorSet;
    }


}