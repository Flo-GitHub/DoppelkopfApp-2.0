package com.example.admin.doppelkopfapp;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

/**
 * Created by Admin on 07.07.2017.
 */

public class PlayerTest {

    @Test
    public void testEquals() {
        Player p1 = new Player(0, "Martin");
        p1.setPoints(23);
        Player p2 = new Player(0, "Willi");
        p2.setPoints(-2334);
        Player p3 = new Player(1, "1");
        Player p4 = new Player(2, "1");

        assertThat(p1, is(equalTo(p2)));
        assertThat(p2, is(equalTo(p1)));
        assertThat(p3, is(not(equalTo(p4))));
        assertThat(p4, is(not(equalTo(p3))));
        assertThat(p1, is(not(equalTo(p3))));
    }

    @Test
    public void testPointsPositive() {
        Player p = new Player(0, "Test");
        p.addPoints(200);
        p.addPoints(-10);

        assertThat(p.getPoints(), is(190) );
    }

    @Test
    public void testPointsNegative() {
        Player p = new Player(0, "Test");
        p.addPoints(-90);
        p.addPoints(-1);

        assertThat(p.getPoints(), is(-91) );
    }

    @Test
    public void testPointsLost() {
        Player p = new Player(0, "Test");
        p.addPoints(20);
        p.addPoints(-12);
        p.addPoints(-40);

        assertThat(p.getPointsLost(), is(52));

        Player p2 = new Player(0, "Test2");
        p2.addPoints(-23);
        p2.addPoints(2003);
        p2.addPoints(-0);

        assertThat(p2.getPointsLost(), is(23) );
    }

}
