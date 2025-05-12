package com.example.movieGame;

/**
 * Node used in the trie that is used for Autocomplete dropdown suggestions
 * @author Harry Smith
 */

public class Node {

    private Term term;  //String word and its int weight
                        // (if not a complete word, value is "" and weight is 0)
    private int words;  //0 if not a word; 1 if is a word
    private int prefixes; //# of words with the prefix of the node (ex: mew has mew and mewtwo = 2)
    private Node[] references;  //Array of references to this node’s children.
    // Each vertex must have references to all his possible children (in our case 26).
    // You must initialize all references to null in your constructor.

    /**
     * Initialize a Node with an empty string and 0 weight; useful for
     * writing tests.
     */
    public Node() {
        //initialize the node
        term = new Term("",0);
        words = 0;
        prefixes = 0;
        references = new Node[255];
    }

    /**
     * Initialize a Node with the given query string and weight.
     * @throws IllegalArgumentException if query is null or if weight is negative.
     */
    public Node(String query, long weight) {
        if (query == null || weight < 0) {
            throw new IllegalArgumentException();
        }
        //initialize the node
        term = new Term(query,weight);
        words = 0;
        prefixes = 0;
        references = new Node[26];
    }

    /**
     * Getter for term
     * @return Term
     */
    public Term getTerm() {
        return term;
    }

    /**
     * Setter for term
     * @param term Term
     */
    public void setTerm(Term term) {
        this.term = term;
    }

    /**
     * Getter for words (word = 1 if Node is a word, and 0 if not a word)
     * @return word (word = 1 if Node is a word, and 0 if not a word)
     */
    public int getWords() {
        return words;
    }

    /**
     * Setter for words (word = 1 if Node is a word, and 0 if not a word)
     * @param words word (word = 1 if Node is a word, and 0 if not a word)
     */
    public void setWords(int words) {
        this.words = words;
    }

    /**
     * Getter for words (word = 1 if Node is a word, and 0 if not a word)
     * @return prefixes
     */
    public int getPrefixes() {
        return prefixes;
    }

    /**
     * Setter for the prefix
     * @param prefixes prefix
     */
    public void setPrefixes(int prefixes) {
        this.prefixes = prefixes;
    }

    /**
     * Getter for the array of node references
     * Every Node gets an array of nodes - one for each character (255 slots)
     * @return array of reference nodes (one step down the trie)
     */
    public Node[] getReferences() {
        return references;
    }

    /**
     * Setter for the array of node references
     * @param references array of reference nodes (one step down the trie)
     */
    public void setReferences(Node[] references) {
        this.references = references;
    }
}
