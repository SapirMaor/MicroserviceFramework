package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.messages.TrainModelEvent;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GPUTest {
    private GPU gpu;
    private Model model;
    private TrainModelEvent event;
    private DataBatch dataBatch;
    private Data data;
    @Before
    public void Before() {
        gpu = new GPU(GPU.Type.RTX3090.toString());
         data = new Data("Tabular", 1000);
        Student student =  new Student("Test", "CS","MSc");
        model = new Model("Testdata",data,student);
        event = new TrainModelEvent(model);
        dataBatch = new DataBatch(data,0, gpu);
    }

    @Test
    public void testSetModel() {
        gpu.setModel(model);
        assertEquals(model,gpu.getModel());
    }
    @Test
    public void testGetModel(){
        gpu.setModel(model);
        assertEquals(model,gpu.getModel());
    }
    @Test
    public void testGetEvent(){
        assertNull(gpu.getEvent());
        gpu.setEvent(event);
        assertEquals(event,gpu.getEvent());
    }
    @Test
    public void testSetEvent(){
        gpu.setEvent(event);
        assertEquals(event,gpu.getEvent());
    }
    @Test
    public void testTestModel(){
        GPU.TestModel(model);
        assertTrue(model.getResult() == Model.Results.Good ||model.getResult() == Model.Results.Bad);
        assertEquals(model.getStatus(),Model.Status.Tested);
    }
    @Test
    public void testInsertToVRAM() throws InterruptedException {
        gpu.insertToVRAM(dataBatch);
        assertEquals(gpu.getVRAMSize(),1);
    }
    @Test
    public void testGetFromVRAM() throws InterruptedException {
        gpu.insertToVRAM(dataBatch);
        assertEquals(gpu.getFromVRAM(),dataBatch);
    }
    @Test
    public void testSendDataBatch() throws InterruptedException {
        gpu.setModel(model);
        gpu.sendDataBatch();
        DataBatch datab = new DataBatch(data,0,gpu);
        assertEquals(Cluster.getInstance().getBatch(32),datab);
    }
    @Test
    public void testTickDuration(){
        assertEquals(1,gpu.tickDuration());
    }
    @Test
    public void testProcess(){
        gpu.setModel(model);
        gpu.process();
        assertEquals(gpu.getGpuTime(), 1);
        assertEquals(model.getData().getProcessed(),1000);
    }

}