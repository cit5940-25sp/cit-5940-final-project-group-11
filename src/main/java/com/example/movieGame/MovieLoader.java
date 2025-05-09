package com.example.movieGame;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class MovieLoader {


    private static List<Movie> movies;
    private static HashSet<Integer> idSet;
    private static Map<Integer, List<String>> actorMap;
    private static Map<Integer, List<String>> directorMap;
    private static Map<Integer, List<String>> writerMap;
    private static Map<Integer, List<String>> cinematographerMap;
    private static Map<Integer, List<String>> composerMap;
    private static Map<Integer, String> title;
    private static Map<Integer, Long> year;
    private static Map<Integer, List<String>> genreSet;


    public static void creditCSVRead() throws IOException {

        //Load in the credit csv file
        InputStream creditsStream = MovieLoader.class.getClassLoader().getResourceAsStream("tmdb_5000_credits.csv");
        InputStreamReader creditsReader = new InputStreamReader(creditsStream, StandardCharsets.UTF_8);
        BufferedReader creditsBReader = new BufferedReader(creditsReader);

        //Create Hashmaps to store details
        actorMap = new HashMap<>();
        directorMap = new HashMap<>();
        writerMap = new HashMap<>();
        cinematographerMap = new HashMap<>();
        composerMap = new HashMap<>();
        idSet = new HashSet<>();

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
                if ("Director of Photography".equals(crewMember.getString("job"))) {
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


    //Note all columns have been deleted from the CSV file except for ID, title, year and genre
    public static void moviesCSVRead() throws IOException {

        //Load in the movies csv file
        InputStream moviesStream = MovieLoader.class.getClassLoader().getResourceAsStream("tmdb_5000_movies.csv");
        InputStreamReader moviesReader = new InputStreamReader(moviesStream, StandardCharsets.UTF_8);
        BufferedReader moviesBReader = new BufferedReader(moviesReader);

        //Store title, year and genre
        title = new HashMap<>();
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

            //Extract the id, title,  year and genre
            int id = Integer.parseInt(fields[1].trim());
            String titleString = fields[2].trim();
            title.put(id, titleString);

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

    public static List<Movie> createMovieFromFiles() {

        //Initialise movies
        movies = new ArrayList<>();

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

        }

        //Return movie object
        return movies;

    }


}





