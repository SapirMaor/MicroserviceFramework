package bgu.spl.mics;

import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.services.CPUService;
import bgu.spl.mics.application.services.GPUService;
import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MessageBusTest {
    private MessageBus msgbus = MessageBusImpl.getInstance();
    private MicroService testMicroService;
    private TrainModelEvent testEvent;
    private TerminateBroadcast testBroadcast;

    @Before
    public void setUp() throws Exception {
        testMicroService = new GPUService("testMicroService",new GPU("RTX3090")) ;
        testEvent = new TrainModelEvent(new Model());
        testBroadcast = new TerminateBroadcast();
    }

    @Test
    public void testRegister() {
        assertFalse(msgbus.isMicroServiceRegistered(testMicroService));
        msgbus.register(testMicroService);
        assertTrue(msgbus.isMicroServiceRegistered(testMicroService));
        msgbus.unregister(testMicroService);
    }

    @Test
    public void subscribeEventTest() throws InterruptedException {
        msgbus.register(testMicroService);
        assertFalse(msgbus.isMicroServiceSubscribedEvent(testEvent.getClass(), testMicroService));
        msgbus.subscribeEvent(testEvent.getClass(), testMicroService);
        assertTrue(msgbus.isMicroServiceSubscribedEvent(testEvent.getClass(), testMicroService));
        msgbus.unregister(testMicroService);
    }

    @Test
    public void subscribeBroadcastTest() throws InterruptedException {
        msgbus.register(testMicroService);
        assertFalse(msgbus.isMicroServiceSubscribedBroadcast(testBroadcast.getClass(), testMicroService));
        msgbus.subscribeBroadcast(testBroadcast.getClass(), testMicroService);
        assertTrue(msgbus.isMicroServiceSubscribedBroadcast(testBroadcast.getClass(), testMicroService));
        msgbus.unregister(testMicroService);
    }

    @Test
    public void sendBroadcastTest() throws InterruptedException {
        msgbus.register(testMicroService);
        msgbus.subscribeBroadcast(testBroadcast.getClass(), testMicroService);
        msgbus.sendBroadcast(testBroadcast);
        Message testMSg = msgbus.awaitMessage(testMicroService);
        assertEquals(testMSg,testBroadcast);
        msgbus.unregister(testMicroService);
    }

    @Test
    public void sendEventTest() throws InterruptedException {
        msgbus.register(testMicroService);
        msgbus.subscribeEvent(testEvent.getClass(), testMicroService);
        msgbus.sendEvent(testEvent);
        Message testMsg = msgbus.awaitMessage(testMicroService);
        assertEquals(testMsg, testEvent);
        msgbus.unregister(testMicroService);
    }


    @Test
    public void unregisterTest() {
        msgbus.register(testMicroService);
        assertTrue(msgbus.isMicroServiceRegistered(testMicroService));
        msgbus.unregister(testMicroService);
        assertFalse(msgbus.isMicroServiceRegistered(testMicroService));
    }

    @Test
    public void awaitMessageTest() throws InterruptedException {
        assertThrows(NullPointerException.class, () -> msgbus.awaitMessage(testMicroService));
        msgbus.register(testMicroService);
        msgbus.subscribeEvent(testEvent.getClass(), testMicroService);
        msgbus.sendEvent(testEvent);
        Message testMsg = msgbus.awaitMessage(testMicroService);
        assertEquals(testMsg,testEvent);
    }
}