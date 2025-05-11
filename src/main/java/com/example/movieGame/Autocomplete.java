package com.example.movieGame;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Autocomplete implements IAutocomplete {
    //In this part, you will implement a data type that provides autocomplete
    // functionality for a given set of strings and corresponding weights, using Term and Node.
    // Your Autocomplete.java class will implement a Trie data structure.
    // Your class should keep a reference to the root Node of the Trie.
    //
    //Organize your program by creating a data type Autocomplete.java that implements
    // the IAutocomplete.java interface. Most of the implementation details for Autocomplete.java
    // are listed in the JavaDocs found in IAutocomplete.java.
    //
    //Some important things to keep in mind when implementing Autocomplete.java:
    //When building your trie from a file, you should convert all strings to lowercase.
    //Preserve encapsulation by returning copies of the Terms that you suggest instead
    // of returning references to the Term objects that constitute your Trie.
    //For inputs that contain special characters or prefixes that are not contained in the Trie`,
    // do nothing and return null/0.

    private Node currentNode; //node for tracking which node you're on when building the Trie
    private Node rootNode; //node for tracking the root
    private List<ITerm> suggestions = new ArrayList<>();

    //getters added for current and root node for testing
    public Node getCurrentNode() {
        return currentNode;
    }
    public Node getRootNode() {
        return rootNode;
    }

    @Override
    public void addWord(String word, long weight) {
        //if Node is null (ex: if being called before buildTrie)
        //initialize the first node
        if (rootNode == null) {
            rootNode = new Node();
        }


        // comment this out because we actually need numbers, weird chars, etc

        //check for special character
        boolean specialChar = false;
        for (char chr : word.toCharArray()) {
            if (!(Character.toLowerCase(chr) < 256)) {
            //if (Character.toLowerCase(chr) < 97 || Character.toLowerCase(chr) > 122) {
                specialChar = true;
                break;
            }
        }
        if (specialChar) {
            return;
        }

        word = word.toLowerCase();


        int len = word.length(); //capture length of word
        int charCounter = 1; //track which character in the word it is
        String prefixWord = "";
        word = word.toLowerCase(); //convert string to lowercase
        //rootNode.setPrefixes(rootNode.getPrefixes()+1); //increment root node prefix
        //increment prefix
        int newPrefix = rootNode.getPrefixes() + 1;
        rootNode.setPrefixes(newPrefix);
        //reset currentNode to rootNode
        currentNode = rootNode;

        //loop through each letter in the word
        for (char chr : word.toCharArray()) {
            prefixWord = prefixWord + chr;  //track the prefix of the word to enter as a term
            int alphabetPosition = chr;    //capture index position (0 to 25) of character
            //CHECK IF NODE FOR THAT LETTER HAS DATA
            //if the node has a node in its references for that letter
            Node[] references;
            references = currentNode.getReferences();
            if (references[alphabetPosition] != null) {
                currentNode = references[alphabetPosition]; //move down from the root node
                //CHECK IF THE LETTER BEING LOOPED IS THE LAST LETTER IN THE WORD
                //if last letter and words = 0
                if (charCounter == len && currentNode.getWords() == 0) {
                    //change words to 1 (i.e. if it wasn’t recognized as a word before,
                    // but this new word makes the prefix a word, change accordingly)
                    currentNode.setWords(1);
                    //update weight
                    currentNode.getTerm().setWeight(weight);
                    //increment prefix
                    newPrefix = currentNode.getPrefixes() + 1;
                    currentNode.setPrefixes(newPrefix);
                } else {
                    //if last letter and words is already 1 OR if not last letter in the word
                    //increment prefix
                    newPrefix = currentNode.getPrefixes() + 1;
                    currentNode.setPrefixes(newPrefix);
                }
                //update currentNode to refer to the next node
                currentNode = references[alphabetPosition];
            } else {    //IF THE NODE DOESN'T HAVE DATA
                //Check if the letter being looped is the last letter in the word
                if (charCounter == len) {   //if it's not the last letter in the word
                    //create a new node with words = 1
                    createNewNode(1, weight,alphabetPosition, prefixWord);
                } else {    //if not the last letter in the word
                    //create a new node with words = 0
                    createNewNode(0, weight,alphabetPosition, prefixWord);
                }
            }
            //increment character counter
            charCounter++;
        }
    }

    //NEW METHOD ADDED
    private void createNewNode(int words, long weight, int alphabetPosition, String prefixWord) {
        //create new node and set Term
        Node newNode;
        if (words == 0) {
            newNode = new Node(prefixWord, 0);
        } else {
            newNode = new Node(prefixWord, weight);    //only enter weight if it's a word
        }
        //set words
        newNode.setWords(words);
        //set prefix
        newNode.setPrefixes(1);
        //set references
        newNode.setReferences(new Node[255]);
        //place node in references array of currentNode
        currentNode.getReferences()[alphabetPosition] = newNode;
        //update currentNode to be the new Node just created
        currentNode = newNode;
    }

    //NOTE: buildTrie is not being used. addWords is called directly from GamePlay
    @Override
    public Node buildTrie(String filename, int k) {
        try {
            //initialize the first node
            rootNode = new Node();
            currentNode = rootNode;
            //loop through each line in the file
            FileInputStream fis = new FileInputStream(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line = br.readLine();
            while (line != null) {
                //split the line into term and weight
                String[] arr = new String[2];
                arr = line.trim().split("\t");
                //check for special character
                boolean specialChar = false;
                if (arr.length == 2 && !arr[0].equals("")) {   //account for blank lines
                    for (char chr : arr[1].toCharArray()) {
                        if (Character.toLowerCase(chr) < 97 || Character.toLowerCase(chr) > 122) {
                            specialChar = true;
                            break;
                        }
                    }
                    if (!specialChar) {     //if not a special character
                        //add the word and weight to the tree
                        addWord(arr[1], Long.parseLong(arr[0]));
                    }
                }
                //read next line
                line = br.readLine();
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return rootNode;
    }

    /**
     * @param prefix
     * @return the root of the subTrie corresponding to the last character of
     *         the prefix. If the prefix is not represented in the trie, return null.
     */
    @Override
    public Node getSubTrie(String prefix) {
        //move down the trie till you reach the node with the term that matches the prefix
        currentNode = rootNode;
        for (char chr : prefix.toCharArray()) {
            if (!(chr < 256)) { //if a special character (ie not a letter)
                return null;
            }
            if (currentNode == null) {
                return null;
            }
            currentNode = currentNode.getReferences()[chr];
        }
        return currentNode;
    }

    /**
     * @param prefix
     * @return the number of words that start with prefix.
     */
    @Override
    public int countPrefixes(String prefix) {
        //get the root node at that prefix
        Node prefixRootNode = getSubTrie(prefix);
        if (prefixRootNode == null) {
            return 0;
        }
        return prefixRootNode.getPrefixes();
    }

    /**
     * This method should not throw an exception.
     * Make sure that you preserve encapsulation by returning a list of copies of the
     * original Terms; otherwise, the user might be able to change the structure of your
     * Trie based on the values returned.
     *
     * @param prefix
     * @return a List containing all the ITerm objects with query starting with
     *         prefix. Return an empty list if there are no ITerm object starting
     *         with prefix.
     */
    @Override
    public List<ITerm> getSuggestions(String prefix) {
        //move down the trie till you reach the node with the term that matches the prefix
        //(note this happens using the getSubTrie function)

        //clear suggestions list
        // (otherwise it'll just keep adding to it for every query the user enters)
        suggestions.clear();

        //loop through the rest of the trie past the prefix, and return a list of terms
        // (with word and weight)
        //traversal through all 26 elements of each node
        Node prefixNode = getSubTrie(prefix.toLowerCase());

        // Check if the prefix itself is a word
        if (prefixNode != null && prefixNode.getWords() > 0) {
            String newTerm = prefixNode.getTerm().getTerm();
            long newWeight = prefixNode.getTerm().getWeight();
            Term validWord = new Term(String.valueOf(newTerm), newWeight);
            suggestions.add(validWord);
        }

        // Then continue traversal of children
        trieTraversal(prefixNode);
        return suggestions;
    }

    //NEW METHOD ADDED
    private void trieTraversal(Node currNode) {
        //base case
        if (currNode == null) {
            return;
        }
        //check every path down the node
        //TRAVERSE THROUGH references array
        for (Node reference : currNode.getReferences()) { //loop through references array
            if (reference == null) {   //if reference is null
                // nothing happens - move forward in the loop and increment i
                //because if it's null, there are no nodes underneath it
                int enteringThisJustForStyleChecker;
            } else if (reference.getWords() == 0) {   //if prefix is not a word
                //keep moving through the trie
                currNode = reference;
                trieTraversal(currNode);
            } else { //if a word
                //add the word to suggestions list
                currNode = reference; //update node
                //note that this line creates a copy of the Term, so that a copy of the object is
                //  added to the list, and not a reference to the Term object
                //  (the String.valueOf part specifically)
                String newTerm = currNode.getTerm().getTerm();
                long newWeight = currNode.getTerm().getWeight();
                Term validWord = new Term(String.valueOf(newTerm), newWeight);
                suggestions.add(validWord);
                //then keep moving through the trie
                trieTraversal(currNode);
            }
        }
    }

    public static void main(String[] args) {
        Autocomplete autocomplete = new Autocomplete();
        //autocomplete.addWord("test",2);
        //autocomplete.buildTrie("file4.txt",2);
        //autocomplete.getSuggestions("cla");


    }

}
