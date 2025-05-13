package com.example.movieGame;

/**
 * Used in autocomplete to track terms used in Nodes
 */
public class Term implements ITerm {

    private String term;
    private long weight;

    /**
     * Constructor to initialize a Term with a given query String and weight
     *
     * @param term the prefix/word
     * @param weight the weight of the prefix/word
     */
    public Term(String term, long weight) {
        if (term == null || weight < 0) {
            throw new IllegalArgumentException();
        }
        this.term = term;
        this.weight = weight;
    }

    /**
     * Compares the two terms in lexicographic order by query.
     *
     * @param that the term object to be compared
     */
    @Override
    public int compareTo(ITerm that) {
        //compares the term to the query term
        return this.term.compareTo(that.getTerm()); //if <0, this.term is earlier alphabetically
    }

    /**
     * Returns a string representation of this term in the following format:
     * the weight, followed by a tab, followed by the query.
     *
     * @return comparator Object
     */
    @Override
    public String toString() {
        // Returns a string representation of this term in the following format:
        // the weight, followed by a tab, followed by the query.
        String str = weight + "\t" + term;
        return str;
    }

    /**
     * Getter to return the weight of the term
     *
     * @return weight of the term
     */
    @Override
    public long getWeight() {
        return weight;
    }

    /**
     * Getter to return the term
     *
     * @return term
     */
    @Override
    public String getTerm() {
        return term;
    }

    /**
     * Setter to set the weight of the term
     *
     * @param weight weight of the term to set
     */
    @Override
    public void setWeight(long weight) {
        this.weight = weight;
    }

    /**
     * Setter to set the term
     *
     * @param term String term
     * @return term
     */
    @Override
    public String setTerm(String term) {
        //note: seems odd that this both sets and returns...
        this.term = term;
        return this.term;
    }

}
