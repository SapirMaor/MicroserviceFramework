package bgu.spl.mics.application.services;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.DataBatch;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;

import java.util.LinkedList;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {

    private final LinkedList<Model> toTrain;
    private final LinkedList<TrainModelEvent> events;
    private final GPU gpu;
    private DataBatch dataBatch;
    private int currTime;
    private int tickDuration;
    public GPUService(String name, GPU gpu) {
        super(name);
        this.gpu = gpu;
        toTrain = new LinkedList<>();
        events = new LinkedList<>();
        currTime = 1;
        tickDuration = gpu.tickDuration();

    }

    @Override
    protected void initialize() {

        subscribeBroadcast(TickBroadcast.class,(c) -> {
            if (gpu.getEvent() != null) { // if there's no event to train, get the next event.
                // process data in VRAM
                if (dataBatch != null) {
                    if (currTime == tickDuration) {
                        gpu.process();
                        if(!gpu.isDiskEmpty() && !getGpu().isVRAMFull()){ // send databatches for CPU to process
                            gpu.sendDataBatch();
                        }
                        dataBatch = gpu.getFromVRAM();
                        currTime = 1;
                        if (gpu.getModel().getData().isDone()) {
                            //System.out.println(getName()+ " has resolved model " + gpu.getModel().getName());
                            gpu.getModel().setStatus("Trained");
                            complete(gpu.getEvent(), gpu.getModel());
                            gpu.setModel(null);
                            gpu.setEvent(null);
                        }
                    } else {
                        currTime++;
                    }
                }  else {
                    dataBatch = gpu.getFromVRAM();
                }
            } else if (!events.isEmpty()) { // there is no current event, check if there's one in the list
                TrainModelEvent tme = events.poll();
                gpu.setEvent(tme);
                gpu.setModel(tme.getModel());
                gpu.sendDataBatch();
            }
        });
        subscribeEvent(TrainModelEvent.class,(c) -> {
            if(gpu.getEvent() == null) { // not working on a model currently
                gpu.setModel(c.getModel());
                gpu.setEvent(c);
                gpu.sendDataBatch();
            }else{ // working on a model currently, save the event in the list
                events.add(c);
        }
        });
        subscribeEvent(TestModelEvent.class,(c) -> {
            GPU.TestModel(c.getModel());
            complete(c, c.getModel());
        });

    }
    public GPU getGpu(){
        return gpu;
    }
}
