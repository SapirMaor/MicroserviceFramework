package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.GPUService;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {
    private final Data data;
    private final int start_index;

    private final GPU gpu;
    public DataBatch(Data _data, int _start_index, GPU _gpu){
        data = _data;
        start_index = _start_index;
        gpu = _gpu;
    }
    public Data.Type getType(){
        return data.getType();
    }
    public int getStart_index(){
        return start_index;
    }
    public Data getData(){
        return data;
    }

    public GPU getGpu() {
        return gpu;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataBatch dataBatch = (DataBatch) o;
        return start_index == dataBatch.start_index && data.equals(dataBatch.data) && gpu.equals(dataBatch.gpu);
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
