package com.example.movieGame;

/**
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
        references = new Node[26];
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

    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    public int getWords() {
        return words;
    }

    public void setWords(int words) {
        this.words = words;
    }

    public int getPrefixes() {
        return prefixes;
    }

    public void setPrefixes(int prefixes) {
        this.prefixes = prefixes;
    }

    public Node[] getReferences() {
        return references;
    }

    public void setReferences(Node[] references) {
        this.references = references;
    }
}
