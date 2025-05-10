package com.example.movieGame;

import java.util.Comparator;

/**
 * @author ericfouh
 */
public interface ITerm
    extends Comparable<ITerm> {

    /**
     * Compares the two terms in descending order by weight.
     * 
     * @return comparator Object
     */
    public static Comparator<ITerm> byReverseWeightOrder() {
        return new Comparator<ITerm>() {
            @Override
            public int compare(ITerm term1, ITerm term2) {
                return Long.compare(term2.getWeight(), term1.getWeight());
            }
        };
    }

    /**
     * Compares the two terms in lexicographic order but using only the first r
     * characters of each query.
     * 
     * @param r
     * @return comparator Object
     */
    public static Comparator<ITerm> byPrefixOrder(int r) {
        return new Comparator<ITerm>() {
            @Override
            public int compare(ITerm term1, ITerm term2) {
                if (term1 == null || term2 == null) {
                    throw new IllegalArgumentException();
                }
                if (r < 0) {
                    throw new IllegalArgumentException();
                }
                //capture only the first r letters of the string
                String newTerm1 = "";
                String newTerm2 = "";
                for (int i = 0; i < r; i++) {
                    newTerm1 = newTerm1 + term1.getTerm().charAt(i);
                    newTerm2 = newTerm2 + term2.getTerm().charAt(i);
                }
                return newTerm1.compareTo(newTerm2);
                //test if the string is shorter than r
            }
        };
    }

    // Compares the two terms in lexicographic order by query.
    public int compareTo(ITerm that);


    // Returns a string representation of this term in the following format:
    // the weight, followed by a tab, followed by the query.
    public String toString();

    // Required getters.
    public long getWeight();
    public String getTerm();

    // Required setters (mostly for autograding purposes)
    public void setWeight(long weight);
    public String setTerm(String term);

}
