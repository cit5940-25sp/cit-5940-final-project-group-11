package com.example.movieGame;

public class Term implements ITerm {

    private String term;
    private long weight;

    /**
     * Initialize a Term with a given query String and weight
     */
    public Term(String term, long weight) {
        if (term == null || weight < 0) {
            throw new IllegalArgumentException();
        }
        this.term = term;
        this.weight = weight;
    }

    // Compares the two terms in lexicographic order by query.
    @Override
    public int compareTo(ITerm that) {
        //compares the term to the query term
        return this.term.compareTo(that.getTerm()); //if <0, this.term is earlier alphabetically
    }

    @Override
    public String toString() {
        // Returns a string representation of this term in the following format:
        // the weight, followed by a tab, followed by the query.
        String str = weight + "\t" + term;
        return str;
    }

    @Override
    public long getWeight() {
        return weight;
    }

    @Override
    public String getTerm() {
        return term;
    }

    @Override
    public void setWeight(long weight) {
        this.weight = weight;
    }

    //note: seems odd that this both sets and returns...
    @Override
    public String setTerm(String term) {
        this.term = term;
        return this.term;
    }

}
