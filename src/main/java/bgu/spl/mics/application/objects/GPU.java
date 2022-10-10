package bgu.spl.mics.application.objects;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.services.GPUService;
import sun.util.resources.cldr.bas.CalendarData_bas_CM;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
    /**
     * Enum representing the type of the GPU.
     */
    enum Type {RTX3090, RTX2080, GTX1080}

    private Type type;
    private Model model = null;
    private final Cluster cluster;
    private final LinkedList<DataBatch> Disk = new LinkedList<>();
    private LinkedBlockingQueue<DataBatch> VRAM;
    private Event<Model> event;
    private int gpuTime = 0;
    public GPU(String _type){
        switch (_type){
            case "RTX3090": type = Type.RTX3090;
                VRAM = new LinkedBlockingQueue<>(32);
                break;
            case "RTX2080": type = Type.RTX2080;
                VRAM = new LinkedBlockingQueue<>(16);
                break;
            case "GTX1080": type = Type.GTX1080;
                VRAM = new LinkedBlockingQueue<>(8);
                break;
        }
        //VRAM = new LinkedBlockingQueue<>();
        cluster = Cluster.getInstance();
    }

    /**
     * @return returns the GPU's type
     * @pre: None
     * @post: None
     * @inv: this.getType() == RTX3090 || this.getType() == RTX2080 || this.getType() == GTX1080
     */
    public Type getType(){
        return type;
    }

    /**
     * @return The compute cluster
     * @pre: None
     * @post: None
     */
    public final Cluster getCluster(){
        return cluster;
    }

    /**
     * @param: The computed cluster
     * @pre: None
     * @post: this.cluster == _cluster
     */

    /**
     * @return returns the model the GPU is currently working on
     * @pre: None
     * @post: None
     */
    public Model getModel(){
        return model;
    }

    /**
     * @param: The model the GPU is currently working on
     * @pre: None
     * @post: this.model == _model
     */
    public void setEvent(Event<Model> event){
        this.event = event;
    }
    public void setModel(Model _model){
            model = _model;
            if(model != null)
                splitData(_model.getData());
    }

    /**
     * .
     * @param _data to split into the disk
     */
    public void splitData(Data _data){
        for (int i = 0; i < _data.getSize(); i+=1000) {
            Disk.add(new DataBatch(_data, i,this));
        }
    }
    public static void TestModel(Model _model){
        Random random = new Random();
        double prob = random.nextDouble(); // random value between 0.0 and 1.0

        if(_model.getStudent().getStatus()==(Student.Degree.MSc)) { // student's degree is MSc
            if (prob < 0.6) {
                _model.setResult("Good");
            } else
                _model.setResult("Bad");
        }
        else { // student is PhD
            if (prob < 0.8) {
                _model.setResult("Good");
            } else
                _model.setResult("Bad");
        }
        _model.setStatus("Tested");
    }
    public Event<Model> getEvent() {
        return event;
    }

    public void insertToVRAM(DataBatch dataBatch) throws InterruptedException {
        VRAM.put(dataBatch);
    }
    public DataBatch getFromVRAM() {
        return VRAM.poll();
    }
    public boolean isVRAMEmpty(){
        return VRAM.isEmpty();
    }
    public int tickDuration(){
        switch (type){
            case RTX3090: return 1;
            case RTX2080: return 2;
            case GTX1080: return 4;
        }
        return 0;
    }
    public void process(){
        model.getData().process();
        gpuTime++;
    }

    public boolean isDiskEmpty(){
        return Disk.size() == 0;
    }
    public void sendDataBatches(int data){
        if(Disk.size()>= data) {
            int max = Math.max(Disk.size(),data);
            cluster.addBatches(Disk.subList(0, max));
            Disk.subList(0, max).clear();
        }
    }
    public void sendDataBatch(){
        if(Disk.size() > 2 && VRAM.remainingCapacity() > 3) {
            cluster.addBatch(Disk.remove());
            cluster.addBatch(Disk.remove());
            cluster.addBatch(Disk.remove());
        }
        if(Disk.size() > 1 && VRAM.remainingCapacity() > 2 ) {
            cluster.addBatch(Disk.remove());
            cluster.addBatch(Disk.remove());
        }
        if(Disk.size() > 0 && VRAM.remainingCapacity() > 0) {
            cluster.addBatch(Disk.remove());
        }
    }

    public int getGpuTime(){
        return gpuTime;
    }
    public int getVRAMSize(){
        return VRAM.size();
    }
    public boolean isVRAMFull(){
        return VRAM.remainingCapacity() == 0;
    }
    public void terminate(){
        VRAM.clear();
    }
}
