package bgu.spl.mics.application.objects;

import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.*;

public class CPUTest {
    private CPU cpu;
    private DataBatch dataBatch;

    @Before
    public void Before() {
        cpu = new CPU(32);
        GPU gpu = new GPU("RTX3090");
        Data data = new Data("Tabular", 100000);
        dataBatch = new DataBatch(data,0, gpu);
    }

    @Test
    public void testGetCores(){
        assertEquals(cpu.getCores(), 32);
    }
    @Test
    public void getTime(){
        assertEquals(cpu.getTime(),0);
    }
    @Test
    public void testGetCurrentTime(){
        assertEquals(cpu.getCurrentTime(),0);
    }
    @Test
    public void testUpdateTime(){
        cpu.updateTime();
        assertEquals(cpu.getCurrentTime(),1);
    }
    @Test
    public void testUpdateWorkTime(){
        cpu.updateWorkTime();
        assertEquals(cpu.getTime(),1);
    }
    @Test
    public void testGetDataBatch(){
        assertNull(cpu.getDataBatch());
        cpu.setDataBatch(dataBatch);
        assertEquals(dataBatch, cpu.getDataBatch());
    }
    @Test
    public void testSetDataBatch(){
        cpu.setDataBatch(dataBatch);
        assertEquals(dataBatch, cpu.getDataBatch());
    }
    @Test
    public void testGetProcessingTime(){
        cpu.setDataBatch(dataBatch);
        assertEquals(cpu.getProcessingTime(), 1);
    }
    @Test
    public void testGetBatchesProcessed(){
        assertEquals(cpu.getBatchesProcessed(), 0);
        cpu.incrementBatches();
        assertEquals(cpu.getBatchesProcessed(), 1);
    }

}