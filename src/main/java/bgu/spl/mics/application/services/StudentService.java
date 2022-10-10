package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {

    private final Student student;
    private final LinkedList<Model> modelList = new LinkedList<Model>();

    public StudentService(String name, Student student) {
        super(name);
        this.student = student;
        // todo: insert all of the models into modelList
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(PublishConferenceBroadcast.class, (c) -> {
            ArrayList<Model> publishedModels = c.getConferenceInformation().getModels();
            if(publishedModels != null){
            for (Model m : publishedModels) {
                if (m.getStudent().getStudentID() == student.getStudentID())
                    student.incrementPublications();
                else
                    student.incrementPapersRead();
            }
        }});

        Thread thread = new Thread() {
            @Override
            public void run() {
                while (!modelList.isEmpty() && !isTerminated()) {
                    Model model = modelList.poll();
                    // training the current model
                    TrainModelEvent train = new TrainModelEvent(model);
                    model.setStatus("Training");
                    Future<Model> futureAfterTrain = sendEvent(train);
                    synchronized (futureAfterTrain) {
                        try {
                            while (!futureAfterTrain.isDone() && !isTerminated())
                                futureAfterTrain.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    // testing the current model
                    try {
                        TestModelEvent test = new TestModelEvent(futureAfterTrain.get()); //TODO: maybe i need the other get function
                        Future<Model> futureAfterTest = sendEvent(test);
                        synchronized (futureAfterTest) {
                            while(!futureAfterTest.isDone() && !isTerminated())
                                futureAfterTest.wait();
                        }
                        // publishing the current model
                        if (futureAfterTest.get().toPublish()) {
                            PublishResultsEvent publish = new PublishResultsEvent(futureAfterTest.get());
                            sendEvent(publish);
                        }
                    } catch (Exception e) {
                        //System.out.println("test");
                    }
                }
            }
        };
        thread.start();
    }
    public void setModelList(LinkedList<Model> modelList){
        this.modelList.addAll(modelList);
    }
    public void addModel(Model model){
        modelList.add(model);
    }
}