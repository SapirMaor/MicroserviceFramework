package bgu.spl.mics;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class FutureTest {
    private Future future;
    @Before
    public void Before(){
        future = new Future();
    }
    @Test
    public void testGet(){
        Object result = new Object();
        future.resolve(result);
        assertEquals(result,future.get());
    }
    @Test
    public void testResolve() {
        Object result = new Object();
        future.resolve(result);
        assertEquals(result,future.get());
    }
    @Test
    public void testIsDone(){
        assertFalse(future.isDone());
        future.resolve(new Object());
        assertTrue(future.isDone());
    }
    @Test
    public void testGetTimeout() {
        Object result = new Object();
        Object obj = future.get(50, TimeUnit.MILLISECONDS);
        assertNull(obj);
        future.resolve(result);
        obj = future.get(50, TimeUnit.MILLISECONDS);
        assertEquals(result,obj);
    }
}