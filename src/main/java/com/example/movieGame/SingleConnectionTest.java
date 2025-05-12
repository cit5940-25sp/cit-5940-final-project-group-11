package com.example.movieGame;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

public class SingleConnectionTest {

    //Tests that the SingleConnection object is correctly initialised
    //Tests getters and toString
    @Test
    public void testConstructorWithoutOverused() {
        SingleConnection conn = new SingleConnection("Actor", "Tom Hanks");
        assertEquals("Actor", conn.getConnectionType());
        assertEquals("Tom Hanks", conn.getName());
        assertEquals("Actor: Tom Hanks", conn.toString());
    }

    //Tests that the SingleConnection object is correctly initialised with overuse status
    //Tests getters and toString
    @Test
    public void testConstructorWithOverusedTrue() {
        SingleConnection conn = new SingleConnection("Director", "Christopher Nolan", true);
        assertEquals("Director", conn.getConnectionType());
        assertEquals("Christopher Nolan", conn.getName());
        assertEquals("Director: Christopher Nolan: overused ", conn.toString());
    }


    //Test setOverused status
    @Test
    public void testSetOverused() {
        SingleConnection conn = new SingleConnection("Composer", "Hans Zimmer");
        assertEquals("Composer: Hans Zimmer", conn.toString());

        conn.setOverused(true);
        assertEquals("Composer: Hans Zimmer: overused ", conn.toString());
    }

}
