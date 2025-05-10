package com.example.movieGame;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class TermTest {
    @Test
    public void compareTo() {
        //EXPECTED
        //create and sort list
        List<ITerm> expected = new ArrayList<>();
        expected.add(new Term("apple",18));
        expected.add(new Term("banana",50));
        expected.add(new Term("pear",3));
        expected.add(new Term("testing",17));

        //ACTUAL
        List<ITerm> actual = new ArrayList<>();
        actual.add(new Term("testing",17));
        actual.add(new Term("apple",18));
        actual.add(new Term("banana",50));
        actual.add(new Term("pear",3));
        //this will use the compareTo function because ITerm extends comparable
        //so collections.sort is used, it calls compareTo automatically
        Collections.sort(actual);

        //COMPARE
        int i = 0;
        for (ITerm term : expected) {
            assertEquals(term.getTerm(),actual.get(i).getTerm());
            assertEquals(term.getWeight(),actual.get(i).getWeight());
            i++;
        }
    }
    @Test
    public void reverseWeightOrder() {
        //EXPECTED
        //create and sort list
        List<ITerm> expected = new ArrayList<>();
        expected.add(new Term("banana",50));
        expected.add(new Term("apple",18));
        expected.add(new Term("testing",17));
        expected.add(new Term("pear",3));

        //ACTUAL
        List<ITerm> actual = new ArrayList<>();
        actual.add(new Term("testing",17));
        actual.add(new Term("apple",18));
        actual.add(new Term("banana",50));
        actual.add(new Term("pear",3));
        Collections.sort(actual,ITerm.byReverseWeightOrder());

        //COMPARE
        int i = 0;
        for (ITerm term : expected) {
            assertEquals(term.getTerm(),actual.get(i).getTerm());
            assertEquals(term.getWeight(),actual.get(i).getWeight());
            i++;
        }
    }
    @Test
    public void prefixOrder() {
        //EXPECTED
        //create and sort list
        List<ITerm> expected = new ArrayList<>();
        expected.add(new Term("apple",18));
        expected.add(new Term("banana",50));
        expected.add(new Term("pear",3));
        expected.add(new Term("testing",17));

        //ACTUAL
        List<ITerm> actual = new ArrayList<>();
        actual.add(new Term("testing",17));
        actual.add(new Term("apple",18));
        actual.add(new Term("banana",50));
        actual.add(new Term("pear",3));
        Collections.sort(actual,ITerm.byPrefixOrder(3));

        //COMPARE
        int i = 0;
        for (ITerm term : expected) {
            assertEquals(term.getTerm(),actual.get(i).getTerm());
            assertEquals(term.getWeight(),actual.get(i).getWeight());
            i++;
        }
    }
    @Test
    public void prefixOrderFirst4Same() {
        //EXPECTED
        //create and sort list
        List<ITerm> expected = new ArrayList<>();
        expected.add(new Term("apparent",17));
        expected.add(new Term("apple",18));
        expected.add(new Term("application",50));
        expected.add(new Term("apply",3));

        //ACTUAL
        List<ITerm> actual = new ArrayList<>();
        actual.add(new Term("apparent",17));
        actual.add(new Term("apple",18));
        actual.add(new Term("application",50));
        actual.add(new Term("apply",3));
        Collections.sort(actual,ITerm.byPrefixOrder(5));

        //COMPARE
        int i = 0;
        for (ITerm term : expected) {
            assertEquals(term.getTerm(),actual.get(i).getTerm());
            assertEquals(term.getWeight(),actual.get(i).getWeight());
            i++;
        }
    }
    @Test
    public void toStringTest() {
        //EXPECTED
        String expected = "17\ttesting";
        //ACTUAL
        Term term = new Term("testing",17);
        String actual = term.toString();
        //COMPARE
        assertEquals(expected, actual);
    }
    @Test
    public void getWeightTest() {
        //EXPECTED
        long expected = 2147483649L;
        //ACTUAL
        //"L is to explicitly tell it it's a Long not an int
        Term term = new Term("testing",2147483649L);
        long actual = term.getWeight();
        //COMPARE
        assertEquals(expected,actual);
    }
    @Test
    public void setWeightTest() {
        //EXPECTED
        long expected = 17;
        //ACTUAL
        //"L is to explicitly tell it it's a Long not an int
        Term term = new Term("testing",2147483649L);
        term.setWeight(17);
        long actual = term.getWeight();
        //COMPARE
        assertEquals(expected,actual);
    }
    @Test
    public void getTermTest() {
        //EXPECTED
        String expected = "testing";
        //ACTUAL
        //"L is to explicitly tell it it's a Long not an int
        Term term = new Term("testing",2147483649L);
        String actual = term.getTerm();
        //COMPARE
        assertEquals(expected,actual);
    }
    @Test
    public void setTermTest() {
        //EXPECTED
        String expected = "new term";
        //ACTUAL
        //"L is to explicitly tell it it's a Long not an int
        Term term = new Term("testing",2147483649L);
        term.setTerm("new term");
        String actual = term.getTerm();
        //COMPARE
        assertEquals(expected,actual);
    }
}
