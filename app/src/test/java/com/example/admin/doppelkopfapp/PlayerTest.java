package com.example.admin.doppelkopfapp;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Admin on 07.07.2017.
 */

public class PlayerTest {

    @Test
    public void testPointsPositive() {
        Player p = new Player("Test");
        p.addPoints(200);
        p.addPoints(-10);

        assertThat(p.getPoints(), is(190) );
    }

    @Test
    public void testPointsNegative() {
        Player p = new Player("Test");
        p.addPoints(-90);
        p.addPoints(-1);

        assertThat(p.getPoints(), is(-91) );
    }

    @Test
    public void testPointsLost() {
        Player p = new Player("Test");
        p.addPoints(20);
        p.addPoints(-12);
        p.addPoints(-40);

        assertThat(p.getPointsLost(), is(52));

        Player p2 = new Player("Test2");
        p2.addPoints(-23);
        p2.addPoints(2003);
        p2.addPoints(-0);

        assertThat(p2.getPointsLost(), is(23) );
    }

}
