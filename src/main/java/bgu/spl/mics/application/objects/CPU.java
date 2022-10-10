package bgu.spl.mics.application.objects;


/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
    private final int cores;
    private final Cluster cluster;
    private int time;
    private int currentTime;
    DataBatch dataBatch;
    private int batchesProcessed;


    public CPU(int _cores) {
        time = 0;
        cores = _cores;
        currentTime = 0;
        cluster = Cluster.getInstance();
        dataBatch = null;
        batchesProcessed = 0;
    }
    /**
     * @return returns number of cores
     * @pre: None
     * @post: None
     * @inv: this.getCores>0
     */
    public int getCores(){
        return cores;
    }

    /**
     * @return The computed cluster
     * @pre: None
     * @post: None
     */
    public final Cluster getCluster(){
        return  cluster;
    }

    public void updateTime(){
        currentTime++;
    }
    public void updateWorkTime(){
        time++;
    }
    public int getTime() {
        return time;
    }
    public DataBatch getDataBatch() {
        return dataBatch;
    }

    public void setDataBatch(DataBatch dataBatch) {
        this.dataBatch = dataBatch;
        currentTime = 0;
    }

    public int getCurrentTime(){
        return currentTime;
    }

    public int getProcessingTime(){
        int numOfTicks;
        if(dataBatch != null) {
            if (dataBatch.getType() == Data.Type.Images)
                numOfTicks = (32 / cores) * 4;
            else {
                if (dataBatch.getType() == Data.Type.Text)
                    numOfTicks = (32 / cores) * 2;
                else
                    numOfTicks = (32 / cores);
            }
            return numOfTicks;
        }
        else return -1;
    }
    public void incrementBatches(){
        batchesProcessed++;
        //allBatchesProcessed.addAndGet(1);
    }
    public int getBatchesProcessed(){
        return batchesProcessed;
    }
}
