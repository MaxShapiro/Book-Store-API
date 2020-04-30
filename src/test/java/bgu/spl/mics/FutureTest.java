package bgu.spl.mics;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class FutureTest {
    private Future<Integer> futureTest;

    @Before
    public void setUp() throws Exception {
        futureTest = new Future<>();
    }

    @Test
    public void get() {
        futureTest.resolve(1);
        assertEquals((Integer)1, futureTest.get());
    }

    @Test
    public void resolve() {
        futureTest.resolve(1);
        assertEquals((Integer)1, futureTest.get());
        assertTrue(futureTest.isDone());
    }

    @Test
    public void isDone() {
        assertFalse(futureTest.isDone());
        futureTest.resolve(1);
        assertTrue(futureTest.isDone());
    }

    @Test
    public void get_withTimeout() {
        futureTest.resolve(5);
        futureTest.get(1, TimeUnit.NANOSECONDS);
        assertEquals(futureTest.get().intValue(),5);
    }

    @After
    public void tearDown() throws Exception {
    }
}