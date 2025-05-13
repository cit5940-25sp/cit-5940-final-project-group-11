[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/nK589Lr0)
[![Open in Visual Studio Code](https://classroom.github.com/assets/open-in-vscode-2e0aaae1b6195c2367325f4f02e2d04e9abb55f0b24a779b69b11b9e10269abc.svg)](https://classroom.github.com/online_ide?assignment_repo_id=18841718&assignment_repo_type=AssignmentRepo)

# Final Project CIT 5940

## Data
This project mimics the Cine2Nerdle game found at this link: https://www.cinenerdle2.app/battle
Data used came from the following Kaggle database: https://www.kaggle.com/datasets/tmdb/tmdb-movie-metadata

## Game Play
In this game design two players take turns entering movies that have a connection to the previous movie.
After each round - if the user has entered a valid movie - the screen updates to show (a) the active player (b) the win condition selected at the beginning (c) the number of rounds played (d) the timer (e) the last five movies played (f) each player's progress towards the win condition
If the user's selection is invalid, they may continue to enter different movies until the timer runs out.
The game ends either when the timer runs out, or when a player has satisfied the win condition.
Examples of invalid selections include:
- The movie has been used before
- The connection has been used too many times
- The movie does not have a valid connection to the previous movie

Assumptions include:
* Both players are taking turns playing on the same laptop
* The only relevant movies for consideration are those from the Kaggle database

## Class Structure
Classes are grouped in the primary following categories:

**User Interface and logic to trigger changes to the view:**
* ViewUI.html
* ControllerClass

**Primary game logic:**
* GamePlay (checks connections based on user entry)
* MovieLoader (loads the data)
* MoveResult (validates the move)

**Objects:**
* Movie
* Player
* SingleConnection

**Autocomplete functionality:**
* Autocomplete
* IAutocomplete
* ITerm
* Node
* Term

**Win condition logic:**
* ActorWinStrategy
* GenreWinStrategy
* WinStrategy

## Design Patterns
The design patterns employed were:
* **Strategy Design Pattern:** This was utilized to allow the user to control the win condition. An interface checks whether the win condition has been satisfied, based on the win condition selected by the user 
* **Model View Controller:** This was utilized to manage the user interface and keep the View separate from the game logic.

## Additional Features
Optional additional features employed include:
* A win condition based on actor (instead of just genre)
* HTML user interface for improved user experience
* JavaDocs

## Authors
This game was created by Angela Pan, Sam Pollock, and Rachel Draper.

## File Structure
Here is the current project file structure being used (to employ Spring Boot)
```text
cit-5940-final-project-group-11/
├── pom.xml
├── README.md
├── .gitignore
├── autocompleteTesting.txt
├── src/
│   └── main/
│       ├── java/
│       │   └── com/example/movieGame/
│       │       ├── ActorWinStrategy.java
│       │       ├── ActorWinStrategyTest.java
│       │       ├── Autocomplete.java
│       │       ├── AutocompleteTest.java
│       │       ├── ControllerClass.java
│       │       ├── GamePlay.java
│       │       ├── GamePlayTest.java
│       │       ├── GenreWinStrategy.java
│       │       ├── GenreWinStrategyTest.java
│       │       ├── IAutocomplete.java
│       │       ├── ITerm.java
│       │       ├── MoveResult.java
│       │       ├── Movie.java
│       │       ├── MovieLoader.java
│       │       ├── MovieLoaderTest.java
│       │       ├── MovieTest.java
│       │       ├── Node.java
│       │       ├── NodeTest.java
│       │       ├── Player.java
│       │       ├── PlayerTest.java
│       │       ├── RunGame.java
│       │       ├── SingleConnection.java
│       │       ├── SingleConnectionTest.java
│       │       ├── Term.java
│       │       ├── TermTest.java
│       │       └── WinStrategy.java
│       └── resources/
│           ├── HelloWorld.java
│           ├── tmdb_5000_credits.csv
│           ├── tmdb_5000_movies.csv
│           └── templates/
│               └── ViewUI.html
├── test/
├── target/
└── *.iml files
