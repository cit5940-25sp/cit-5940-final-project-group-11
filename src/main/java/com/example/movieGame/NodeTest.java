package com.example.movieGame;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test class for Node class
 *
 */
public class NodeTest {
    @Test
    public void getTerm() {
        //EXPECTED
        Term expected = new Term("test",7);
        //ACTUAL
        Node node = new Node("test",7);
        Term actual = node.getTerm();
        assertEquals(expected.getTerm(),actual.getTerm());
        assertEquals(expected.getWeight(),actual.getWeight());
    }
    @Test
    public void setTerm() {
        Term term = new Term("test",7);
        //EXPECTED
        Term expected = term;
        //ACTUAL
        Node node = new Node("original",5);
        Term actual = new Term("test",7);
        node.setTerm(actual);
        assertEquals(expected.getTerm(),actual.getTerm());
        assertEquals(expected.getWeight(),actual.getWeight());
    }
    @Test
    public void getWords() {
        //EXPECTED
        int expected = 0;
        //ACTUAL
        Node node = new Node("test",7);
        int actual = node.getWords();
        assertEquals(expected,actual);
    }
    @Test
    public void setWords() {
        //EXPECTED
        int expected = 5;
        //ACTUAL
        Node node = new Node("test",7);
        node.setWords(5);
        int actual = node.getWords();
        assertEquals(expected,actual);
    }
    @Test
    public void getPrefixes() {
        //EXPECTED
        int expected = 0;
        //ACTUAL
        Node node = new Node("test",7);
        int actual = node.getPrefixes();
        assertEquals(expected,actual);
    }
    @Test
    public void setPrefixes() {
        //EXPECTED
        int expected = 5;
        //ACTUAL
        Node node = new Node("test",7);
        node.setPrefixes(5);
        int actual = node.getPrefixes();
        assertEquals(expected,actual);
    }
    @Test
    public void getReferencesNull() {
        //EXPECTED
        Node[] expected = new Node[25];
        //ACTUAL
        Node node = new Node("test",7);
        Node[] actual = node.getReferences();
        for (int i = 0; i < 25; i++) {
            assertEquals(expected[i],actual[i]);
        }
    }
    @Test
    public void getReferences() {
        //EXPECTED
        Node[] expected = new Node[25];
        expected[0] = new Node("a",17);
        //ACTUAL
        Node node = new Node("test",7);
        Node[] actual = node.getReferences();
        actual[0] = new Node("a",17);
        //CHECK
        assertEquals(expected[0].getTerm().getTerm(),actual[0].getTerm().getTerm());
        assertEquals(expected[0].getTerm().getWeight(),actual[0].getTerm().getWeight());
        assertEquals(expected[0].getWords(),actual[0].getWords());
        assertEquals(expected[0].getPrefixes(),actual[0].getPrefixes());
        for (int i = 1; i < 25; i++) {
            assertEquals(expected[i],actual[i]);
        }
    }
    @Test
    public void setReferences() {
        //EXPECTED
        Node[] expected = new Node[25];
        expected[0] = new Node("a",17);
        //ACTUAL
        Node node = new Node("test",7);
        node.setReferences(expected);
        Node[] actual = node.getReferences();
        actual[0] = new Node("a",17);
        //CHECK
        assertEquals(expected[0].getTerm().getTerm(),actual[0].getTerm().getTerm());
        assertEquals(expected[0].getTerm().getWeight(),actual[0].getTerm().getWeight());
        assertEquals(expected[0].getWords(),actual[0].getWords());
        assertEquals(expected[0].getPrefixes(),actual[0].getPrefixes());
        for (int i = 1; i < 25; i++) {
            assertEquals(expected[i],actual[i]);
        }
    }
}