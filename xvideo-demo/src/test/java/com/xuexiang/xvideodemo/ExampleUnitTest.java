package com.xuexiang.xvideodemo;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {

        assertEquals(4, 2 + 2);

        double s = Math.sqrt(Math.pow(1024D / 170D, 2) + Math.pow(600D / 170D, 2));
        System.out.println(s);
    }
}