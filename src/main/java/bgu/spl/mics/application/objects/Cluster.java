package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.GPUService;

import javax.swing.*;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {

	private LinkedBlockingQueue<CPU> CPUList;
	private LinkedBlockingQueue<GPU> GPUList;
	private LinkedBlockingQueue<DataBatch> dataBatches;
	private LinkedBlockingQueue<DataBatch> imageDB;
	private LinkedBlockingQueue<DataBatch> textDB;
	private LinkedBlockingQueue<DataBatch> tabularDB;

	private static Cluster instance;

	/**
     * Retrieves the single instance of this class.
     */
	public static synchronized Cluster getInstance() {
		if(instance == null){
			instance = new Cluster();
		}
		return instance;
	}
	private Cluster(){
		CPUList = new LinkedBlockingQueue<>();
		GPUList = new LinkedBlockingQueue<>();
		dataBatches = new LinkedBlockingQueue<>();
		imageDB = new LinkedBlockingQueue<>();
		textDB = new LinkedBlockingQueue<>();
		tabularDB = new LinkedBlockingQueue<>();
	}
	public void setCPUList(Collection<CPU> CPUList) {
		this.CPUList.addAll(CPUList);
	}

	public void setGPUList(Collection<GPU> GPUList) {
		this.GPUList.addAll(GPUList);
	}
	public void addBatches(Collection<DataBatch> batches){
//		synchronized(dataBatches) {
//			dataBatches.addAll(batches);
//		}
	}
	public void addBatch(DataBatch batch) {
		switch (batch.getType().toString()) {
			case "Images":
				imageDB.add(batch);
				break;
			case "Text":
				textDB.add(batch);
				break;
			case "Tabular":
				tabularDB.add(batch);
				break;
			default:
				imageDB.add(batch);
				break;
		}
	}
	public DataBatch getBatch() throws InterruptedException {
			return dataBatches.poll();
	}
	public DataBatch getBatch(int cores) throws InterruptedException {
		Random random = new Random();
		DataBatch batch = null;
		if(cores > 16) {
			 batch = imageDB.poll();
			if (batch == null)
				batch = textDB.poll();
			if (batch == null)
				return tabularDB.poll();

		} else {
			batch = textDB.poll();
			if (batch == null)
				batch = imageDB.poll();
			if (batch == null)
				return tabularDB.poll();
		}
		return batch;
	}
	public void postProcess(DataBatch dataBatch) throws InterruptedException {
		for (GPU gpu : GPUList) {
			if (dataBatch != null && gpu.getModel() != null && dataBatch.getGpu() == gpu) {
				gpu.insertToVRAM(dataBatch);
			}
		}
	}
	public void print(){
		System.out.println(dataBatches.size());
	}
	public int getSize(){
		return dataBatches.size();
	}
}
