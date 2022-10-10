package bgu.spl.mics.application.services;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.CPU;

/**
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class CPUService extends MicroService {
    private CPU cpu;
    public CPUService(String name, CPU cpu) {
        super(name);
        this.cpu = cpu;
    }
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, (c) -> {
            if (cpu.getDataBatch() == null) {
                cpu.setDataBatch(cpu.getCluster().getBatch(cpu.getCores()));
            }
            cpu.updateTime();
            if(cpu.getDataBatch()!=null)
                cpu.updateWorkTime();
            if (cpu.getDataBatch() != null && cpu.getCurrentTime() == cpu.getProcessingTime()) {
                cpu.getCluster().postProcess(cpu.getDataBatch());
                cpu.incrementBatches();
                cpu.setDataBatch(cpu.getCluster().getBatch(cpu.getCores())); // recalculate processing time, zero out currTime, set new databatch
            }
        });
    }

    public CPU getCpu() {
        return cpu;
    }
}
