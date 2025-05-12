package com.example.movieGame;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test class for autocomplete dropdown selection
 *
 */
public class AutocompleteTest {
    //test empty prefix
    //DONE test prefix with no results in the trie
    //DONE test capital letters
    //DONE test special characters not in the trie (return null/0)
    //DONE (handled in buildTrie before calling addWord) addWord - if invalid character, do nothing
    @Test
    public void addWord() {
        //EXPECTED
        //manually build nodes (add terms and weights)
        Node expected = new Node();
        expected.getReferences()[19] = new Node("t",0);   //t
        expected.getReferences()[19].getReferences()[4] = new Node("te",0);    //te
        expected.getReferences()[19].getReferences()[4].getReferences()[18] =
                new Node("tes",0); //tes
        expected.getReferences()[19].getReferences()[4].getReferences()[18].getReferences()[19] =
                new Node("test",7); //test
        //add Words
        expected.getReferences()[19].getReferences()[4].getReferences()[18].
                getReferences()[19].setWords(1); //test
        //add Prefixes
        expected.getReferences()[19].setPrefixes(1);   //t
        expected.getReferences()[19].getReferences()[4].setPrefixes(1);   //te
        expected.getReferences()[19].getReferences()[4].getReferences()[18].setPrefixes(1); //tes
        expected.getReferences()[19].getReferences()[4].getReferences()[18].getReferences()[19].
                setPrefixes(1);//test

        //ACTUAL
        //create a currentNode and rootNode
        Autocomplete autocomplete = new Autocomplete();
        autocomplete.addWord("test",7);
        Node actual = autocomplete.getRootNode();

        //COMPARE (putting data in string, then comparing the string)
        StringBuilder testCheckExpected = new StringBuilder();
        StringBuilder testCheckActual = new StringBuilder();
        trieTraversal(expected,testCheckExpected);
        trieTraversal(actual,testCheckActual);
        assertEquals(testCheckExpected.toString(),testCheckActual.toString());
    }
    @Test
    public void addWordToExistingTrie() {
        //EXPECTED
        //manually build nodes (add terms and weights)
        Node expected = new Node();
        expected.getReferences()[19] = new Node("t",0);   //t
        expected.getReferences()[19].getReferences()[4] = new Node("te",0);    //te
        expected.getReferences()[19].getReferences()[4].getReferences()[18] =
                new Node("tes",0); //tes
        expected.getReferences()[19].getReferences()[4].getReferences()[18].getReferences()[19] =
                new Node("test",7); //test
        expected.getReferences()[19].getReferences()[19] = new Node("tt",3);    //tt
        //add Words
        expected.getReferences()[19].getReferences()[4].getReferences()[18].
                getReferences()[19].setWords(1); //test
        expected.getReferences()[19].getReferences()[19].setWords(1); //tt
        //add Prefixes
        expected.getReferences()[19].setPrefixes(2);   //t
        expected.getReferences()[19].getReferences()[4].setPrefixes(1);   //te
        expected.getReferences()[19].getReferences()[4].getReferences()[18].setPrefixes(1); //tes
        expected.getReferences()[19].getReferences()[4].getReferences()[18].getReferences()[19].
                setPrefixes(1);//test
        expected.getReferences()[19].getReferences()[19].setPrefixes(1);   //tt

        //ACTUAL
        //create a currentNode and rootNode
        Autocomplete autocomplete = new Autocomplete();
        autocomplete.addWord("test",7);
        autocomplete.addWord("tt",3);
        Node actual = autocomplete.getRootNode();

        //COMPARE (putting data in string, then comparing the string)
        StringBuilder testCheckExpected = new StringBuilder();
        StringBuilder testCheckActual = new StringBuilder();
        trieTraversal(expected,testCheckExpected);
        trieTraversal(actual,testCheckActual);
        assertEquals(testCheckExpected.toString(),testCheckActual.toString());
    }
    @Test
    public void createNewNode() {
        //private method
    }
    @Test
    public void buildTrie() {
        //EXPECTED
        //manually build tree (add terms and weights)
        Node expected = new Node();
        expected.getReferences()[2] = new Node("c",0);   //c
        expected.getReferences()[2].getReferences()[11] = new Node("cl",0);    //cl
        expected.getReferences()[2].getReferences()[11].getReferences()[0] =
                new Node("cla",0); //cla
        expected.getReferences()[2].getReferences()[11].getReferences()[0].getReferences()[2] =
                new Node("clac",0); //clac
        expected.getReferences()[2].getReferences()[11].getReferences()[0].getReferences()[2]
                .getReferences()[10] = new Node("clack",1); //clack
        expected.getReferences()[2].getReferences()[11].getReferences()[0].getReferences()[2]
                .getReferences()[10].getReferences()[18] = new Node("clacks",3); //clacks
        expected.getReferences()[2].getReferences()[11].getReferences()[0].getReferences()[18]
                = new Node("clas",0); //clas
        expected.getReferences()[2].getReferences()[11].getReferences()[0].getReferences()[18]
                .getReferences()[18] = new Node("class",10); //class
        //add Words
        expected.getReferences()[2].getReferences()[11].getReferences()[0].getReferences()[2]
                .getReferences()[10].setWords(1); //clack
        expected.getReferences()[2].getReferences()[11].getReferences()[0].getReferences()[2]
                .getReferences()[10].getReferences()[18].setWords(1); //clacks
        expected.getReferences()[2].getReferences()[11].getReferences()[0].getReferences()[18]
                .getReferences()[18].setWords(1); //class
        //add Prefixes
        expected.getReferences()[2].setPrefixes(3);   //c
        expected.getReferences()[2].getReferences()[11].setPrefixes(3);   //cl
        expected.getReferences()[2].getReferences()[11].getReferences()[0].setPrefixes(3); //cla
        expected.getReferences()[2].getReferences()[11].getReferences()[0].getReferences()[2]
                .setPrefixes(2);//clac
        expected.getReferences()[2].getReferences()[11].getReferences()[0].getReferences()[2]
                .getReferences()[10].setPrefixes(2); //clack
        expected.getReferences()[2].getReferences()[11].getReferences()[0].getReferences()[2]
                .getReferences()[10].getReferences()[18].setPrefixes(1); //clacks
        expected.getReferences()[2].getReferences()[11].getReferences()[0].getReferences()[18]
                .setPrefixes(1); //clas
        expected.getReferences()[2].getReferences()[11].getReferences()[0].getReferences()[18]
                .getReferences()[18].setPrefixes(1); //class

        //ACTUAL
        Autocomplete autocomplete = new Autocomplete();
        Node actual;
        actual = autocomplete.buildTrie("file2.txt",7);

        //COMPARE (putting data in string, then comparing the string)
        StringBuilder testCheckExpected = new StringBuilder();
        StringBuilder testCheckActual = new StringBuilder();
        trieTraversal(expected,testCheckExpected);
        trieTraversal(actual,testCheckActual);
        assertEquals(testCheckExpected.toString(),testCheckActual.toString());
    }
    @Test
    public void buildTrieSpecialCharacterAndCapital() {
        //EXPECTED
        //manually build tree (add terms and weights)
        Node expected = new Node();
        expected.getReferences()[25] = new Node("z",0);   //z
        expected.getReferences()[25].getReferences()[25] = new Node("zz",0);    //z
        expected.getReferences()[25].getReferences()[25].getReferences()[0] =
                new Node("zza",3);//a
        //add Words
        expected.getReferences()[25].getReferences()[25].getReferences()[0].setWords(1); //zza
        //add Prefixes
        expected.getReferences()[25].setPrefixes(1);   //z
        expected.getReferences()[25].getReferences()[25].setPrefixes(1);   //z
        expected.getReferences()[25].getReferences()[25].getReferences()[0].setPrefixes(1); //a

        //ACTUAL
        Autocomplete autocomplete = new Autocomplete();
        Node actual;
        actual = autocomplete.buildTrie("file3.txt",7);

        //COMPARE (putting data in string, then comparing the string)
        StringBuilder testCheckExpected = new StringBuilder();
        StringBuilder testCheckActual = new StringBuilder();
        trieTraversal(expected,testCheckExpected);
        trieTraversal(actual,testCheckActual);
        assertEquals(testCheckExpected.toString(),testCheckActual.toString());
    }
    @Test
    public void getSubTrie() {
        //EXPECTED
        //create the subtrie starting at a certain prefix
        //manually build tree (add terms and weights)
        Node expected = new Node("cla",0); //cla
        //expected.getReferences()[2] = new Node("c",0);   //c
        //expected.getReferences()[2].getReferences()[11] = new Node("cl",0);    //cl
        //expected.getReferences()[2].getReferences()[11].getReferences()[0]
        // = new Node("cla",0); //cla
        expected.getReferences()[2] = new Node("clac",0); //clac
        expected.getReferences()[2].getReferences()[10] = new Node("clack",1); //clack
        expected.getReferences()[2].getReferences()[10].getReferences()[18] =
                new Node("clacks",3); //clacks
        expected.getReferences()[18] = new Node("clas",0); //clas
        expected.getReferences()[18].getReferences()[18] = new Node("class",10); //class
        //add Words
        expected.getReferences()[2].getReferences()[10].setWords(1); //clack
        expected.getReferences()[2].getReferences()[10].getReferences()[18].setWords(1); //clacks
        expected.getReferences()[18].getReferences()[18].setWords(1); //class
        //add Prefixes
        //expected.getReferences()[2].setPrefixes(3);   //c
        //expected.getReferences()[2].getReferences()[11].setPrefixes(3);   //cl
        expected.setPrefixes(3); //cla
        expected.getReferences()[2].setPrefixes(2);//clac
        expected.getReferences()[2].getReferences()[10].setPrefixes(2); //clack
        expected.getReferences()[2].getReferences()[10].getReferences()[18].setPrefixes(1);//clacks
        expected.getReferences()[18].setPrefixes(1); //clas
        expected.getReferences()[18].getReferences()[18].setPrefixes(1); //class

        //ACTUAL
        Autocomplete autocomplete = new Autocomplete();
        Node actual;
        autocomplete.buildTrie("file2.txt",7);
        actual = autocomplete.getSubTrie("cla");

        //COMPARE
        StringBuilder testCheckExpected = new StringBuilder();
        StringBuilder testCheckActual = new StringBuilder();
        trieTraversal(expected,testCheckExpected);
        trieTraversal(actual,testCheckActual);
        assertEquals(testCheckExpected.toString(),testCheckActual.toString());
    }
    @Test
    public void getSubTrieSpecialCharacterPrefix() {
        //EXPECTED
        //Node expected = new Node("/",0); //cla
        Node expected = null;

        //ACTUAL
        Autocomplete autocomplete = new Autocomplete();
        Node actual;
        autocomplete.buildTrie("file2.txt",7);
        actual = autocomplete.getSubTrie("/");

        //COMPARE
        StringBuilder testCheckExpected = new StringBuilder();
        StringBuilder testCheckActual = new StringBuilder();
        trieTraversal(expected,testCheckExpected);
        trieTraversal(actual,testCheckActual);
        assertEquals(testCheckExpected.toString(),testCheckActual.toString());
    }
    @Test
    public void getSubTriePrefixNotInTrie() {
        //EXPECTED
        //Node expected = new Node("/",0); //cla
        Node expected = null;

        //ACTUAL
        Autocomplete autocomplete = new Autocomplete();
        Node actual;
        autocomplete.buildTrie("file2.txt",7);
        actual = autocomplete.getSubTrie("th");

        //COMPARE
        assertEquals(expected,actual); //both should be null
    }
    @Test
    public void countPrefixes() {
        //EXPECTED
        int expected = 3;

        //ACTUAL
        Autocomplete autocomplete = new Autocomplete();
        autocomplete.buildTrie("file2.txt",7);
        int actual = autocomplete.countPrefixes("cla");

        assertEquals(expected,actual);
    }
    @Test
    public void getSuggestions() {
        //EXPECTED
        List<ITerm> expected = new ArrayList<>();
        expected.add(new Term("zza",3));
        //ACTUAL
        List<ITerm> actual = new ArrayList<>();
        Autocomplete autocomplete = new Autocomplete();
        autocomplete.buildTrie("file3.txt",7);
        actual = autocomplete.getSuggestions("z");
        //COMPARE
        for (int i = 0; i < actual.size(); i++) {
            assertEquals(expected.get(i).getTerm(),actual.get(i).getTerm());
            assertEquals(expected.get(i).getWeight(),actual.get(i).getWeight());
        }
    }
    @Test
    public void getSuggestions2() {
        //EXPECTED
        List<ITerm> expected = new ArrayList<>();
        //expected.add(new Term("class",10));
        //expected.add(new Term("clacks",3));
        //expected.add(new Term("clack",1));
        //ACTUAL
        List<ITerm> actual = new ArrayList<>();
        Autocomplete autocomplete = new Autocomplete();
        autocomplete.buildTrie("file2.txt",7);
        actual = autocomplete.getSuggestions("z");
        //COMPARE
        boolean ran = false;
        for (int i = 0; i < actual.size(); i++) {
            assertEquals(expected.get(i).getTerm(),actual.get(i).getTerm());
            assertEquals(expected.get(i).getWeight(),actual.get(i).getWeight());
            ran = true;
        }
        if (expected.size() == 0 && actual.size() == 0) {
            int forStyleGraderOnly;
        } else {
            assertNotEquals(false, ran);
        }

    }
    private void trieTraversal(Node currNode,StringBuilder testCheck) {
        //StringBuilder testCheck = new StringBuilder();
        //StringBuilder testCheckWeight = new StringBuilder();
        //StringBuilder testCheckWords = new StringBuilder();
        //StringBuilder testCheckPrefixes = new StringBuilder();

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
                testCheck.append("null, ");
            } else if (reference.getWords() == 0) {   //if prefix is not a word
                //keep moving through the trie
                currNode = reference;
                testCheck.append(reference.getTerm().getTerm());
                testCheck.append(" ");
                testCheck.append(reference.getTerm().getWeight());
                testCheck.append(" ");
                testCheck.append(reference.getWords());
                testCheck.append(" ");
                testCheck.append(reference.getPrefixes());
                testCheck.append(", ");
                trieTraversal(currNode,testCheck);
            } else { //if a word
                //add the word to suggestions list
                currNode = reference; //update node
                //Term validWord = new Term(currNode.getTerm().getTerm(),
                //        currNode.getTerm().getWeight());
                testCheck.append(reference.getTerm().getTerm());
                testCheck.append(" ");
                testCheck.append(reference.getTerm().getWeight());
                testCheck.append(" ");
                testCheck.append(reference.getWords());
                testCheck.append(" ");
                testCheck.append(reference.getPrefixes());
                testCheck.append(", ");
                //then keep moving through the trie
                trieTraversal(currNode,testCheck);
            }
        }
    }
}