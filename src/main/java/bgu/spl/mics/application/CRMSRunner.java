package bgu.spl.mics.application;
import bgu.spl.mics.*;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.*;
import sun.util.resources.cldr.es.CalendarData_es_PY;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    public static void main(String[] args) throws InterruptedException {
        //<editor-fold desc="Json">
        FileReader jsonReader = null;
        try{
            jsonReader = new FileReader(args[0]);
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException");
            System.exit(0);
        }
        JsonObject json = JsonParser.parseReader(jsonReader).getAsJsonObject();

        /* Create all objects from JSON */
        Cluster cluster = Cluster.getInstance();
        JsonArray array = json.getAsJsonArray("Students");
        Student[] students = createStudents(array);
        Model[] models = createModels(array,students);
        GPU[] GPUs = createGPUs(json.getAsJsonArray("GPUS")); // might need to add cluster to the function
        CPU[] CPUs = createCPUs(json.getAsJsonArray("CPUS")); // might need to add cluster to the function
        ConferenceInformation[] conferences = createConferences(json.getAsJsonArray("Conferences"));
        int TickTime = json.get("TickTime").getAsInt();
        int Duration = json.get("Duration").getAsInt();


        // Create services
        LinkedList<CPUService> CPUServices = new LinkedList<>();
        for (int i = 0; i <  CPUs.length ; i++) {
            CPUServices.add(new CPUService("CPU" + i,CPUs[i]));
        }
        LinkedList<GPUService> GPUServices = new LinkedList<>();
        for (int i = 0; i < GPUs.length ; i++) {
            GPUServices.add(new GPUService("GPU" + i,GPUs[i]));
        }
        LinkedList<StudentService> studentServices = new LinkedList<>();
        int index = 0;
        for (int i = 0; i < students.length; i++) {
            StudentService studentService = new StudentService(students[i].getName() + " " + i,students[i]);
            studentServices.add(studentService);
            int modelSize = json.getAsJsonArray("Students").get(i).getAsJsonObject().getAsJsonArray("models").size();
            for (int j = 0; j < modelSize; j++) {
                studentService.addModel(models[index + j]);
            }
            index+= modelSize;
        }
        cluster.setCPUList(Arrays.asList(CPUs));
        cluster.setGPUList(Arrays.asList(GPUs));
        LinkedList<ConferenceService> conferenceServices = new LinkedList<>();
        for (int i = 0; i < conferences.length; i++) {
            conferenceServices.add(new ConferenceService(conferences[i].getName() + " " + i, conferences[i]));
        }

        TimeService timeService = new TimeService(Duration,TickTime);
        //start services
        for (MicroService m: GPUServices) {
            (new Thread(m)).start();
        }
        Thread.sleep(10);
        LinkedList<Thread> cpuThreads = new LinkedList<>();
        for (MicroService m: CPUServices) {
            Thread t = new Thread(m);
            cpuThreads.add(t);
            t.start();
        }
        Thread.sleep(100);
        for (MicroService m: studentServices) {
            (new Thread(m)).start();
        }
        Thread.sleep(10);
         for(MicroService m: conferenceServices) {
             (new Thread(m)).start();
         }
        (new Thread(timeService)).start();

        Thread.sleep(Duration + 200);
        try {
            File file = createOutput(students,conferences,GPUs,CPUs,models);
            System.out.println("DONE");
        } catch (IOException e) {
            System.out.println("ESXCE");
        }
        int cpuruntime = 0;
        //.println("****************************");
        for (CPUService cpuService: CPUServices  ) {
            //System.out.println(cpuService.getName()+ " " + cpuService.getCpu().getCores() + " - " + cpuService.getCpu().getBatchesProcessed());
            cpuruntime+= cpuService.getCpu().getBatchesProcessed();
        }
        //System.out.println("Total CPUs- " + cpuruntime);
        int gpuvramsizes = 0;
        int gpuprocces = 0;
        for (GPUService gpu:GPUServices) {
            //System.out.println(gpu.getName() + "Runtime: " + gpu.getGpu().getGpuTime() + " VRAM size: " + gpu.getGpu().getVRAMSize());
            gpuvramsizes += gpu.getGpu().getVRAMSize();
            gpuprocces+=gpu.getGpu().getGpuTime();
        }
        //System.out.println("Total gpu processed: " + gpuprocces);
        //System.out.println("Total extra vram:" + gpuvramsizes);
        //System.out.println("Total: " + (gpuprocces+ gpuvramsizes));

        int modeldata = 0;
        for (Model m: models ) {
            //System.out.println(m.getName()+ " " + m.getData().getProcessed()+ "/" + m.getData().getSize());
            modeldata+= m.getData().getProcessed();
        }
        //System.out.println("Total modeldata = " + modeldata);
        MessageBusImpl.getInstance().print();
        for (int i = 0; i <20; i++) {
            Thread.sleep(1);
            for (GPU gpu : GPUs) {
                gpu.terminate();
            }}
        MessageBusImpl.getInstance().print();
        //</editor-fold
    }
    /* Functions to create objects from JSON */
    //<editor-fold desc="JsonCreators">
    private static Student[] createStudents(JsonArray array){
        Student[] students = new Student[array.size()];
        for (int i = 0; i < students.length; i++) {
            JsonObject temp = array.get(i).getAsJsonObject();
            students[i] = new Student(temp.get("name").getAsString(),temp.get("department").getAsString(),temp.get("status").getAsString());
        }
        return students;
    }
    private static Model[] createModels(JsonArray array, Student[] students ){
        ArrayList<Model> models = new ArrayList<>();
        for (int i = 0; i < students.length; i++) {
           JsonArray tempModels = array.get(i).getAsJsonObject().getAsJsonArray("models");
            for (int j = 0; j < tempModels.size(); j++) {
                JsonObject tempModel = (tempModels.get(j).getAsJsonObject());
                Data data = new Data(tempModel.get("type").getAsString(),tempModel.get("size").getAsInt());
                models.add(new Model(tempModel.get("name").getAsString(),data,students[i]));
            }
        }
        return models.toArray(new Model[0]);
    }
    private static CPU[] createCPUs(JsonArray array) throws InterruptedException {
        CPU[] cpus = new CPU[array.size()];
        for (int i = 0; i < cpus.length; i++) {
            cpus[i] = new CPU(array.get(i).getAsInt());
        }
        return cpus;
    }
    private static GPU[] createGPUs(JsonArray array){
        GPU[] gpus = new GPU[array.size()];
        for (int i = 0; i < gpus.length; i++) {
            gpus[i] = new GPU(array.get(i).getAsString());
        }
        return gpus;
    }
    private static ConferenceInformation[] createConferences(JsonArray array){
        ConferenceInformation[] cnfrs = new ConferenceInformation[array.size()];
        for (int i = 0; i < cnfrs.length; i++) {
            JsonObject cnfr = array.get(i).getAsJsonObject();
            cnfrs[i] = new ConferenceInformation(cnfr.get("name").getAsString(), cnfr.get("date").getAsInt());
        }
        return  cnfrs;
    }
    //</editor-fold>

    public static File createOutput(Student[] students,ConferenceInformation[] conferences,GPU[] gpus, CPU[] cpus, Model[] models) throws IOException {
        File file = new File("./output.json");
        FileWriter writer = new FileWriter(file);
        PrintWriter printer = new PrintWriter(writer);
        printer.println("{");
        printer.println("    \"students\": [");
        for (Student student: students) {
            printer.println("        {");
            printer.println("            \"name\": " + student.getName() + ",");
            printer.println("            \"department\": " + student.getDepartment() +" ,");
            printer.println("            \"status\": " + student.getStatus() + ",");
            printer.println("            \"publications\": " + student.getPublications()  + ",");
            printer.println("            \"papersRead\":" + student.getPapersRead() + ",");
            printer.println("            \"trainedModels\": [");
            for (Model model: models) {
                if(model.getStudent().getStudentID() == student.getStudentID()){
                    if(model.getResultAsString() != "None") {
                        printer.println("                {");
                        printer.println("                    \"name\": " + model.getName() + ",");
                        printer.println("                    \"data\": {");
                        printer.println("                        \"type\": " + model.getData().getType() + ",");
                        printer.println("                        \"size\": " + model.getData().getSize() + ",");
                        printer.println("                    },");
                        printer.println("                    \"status\":" + model.getStatus() + ",");
                        printer.println("                    \"results\":" + model.getResult());
                        printer.println("                },");
                    }
                }
            }
            printer.println("            ]");
            printer.println("        },");
        }
        printer.println("    ],");
        printer.println("    \"conferences\": [");
        for (ConferenceInformation conference : conferences) {
            printer.println("        {");
            printer.println("            \"name\": " + conference.getName() + ",");
            printer.println("            \"date\": " + conference.getRealDate() + ",");
            printer.println("            \"publications\": [");
            if (conference.getModels() != null && conference.getModels().size() > 0) {
                for (Model model : conference.getModels()) {
                    printer.println("                {");
                    printer.println("                    \"name\": " + model.getName() + ",");
                    printer.println("                    \"data\": {");
                    printer.println("                        \"type\": " + model.getData().getType() + ",");
                    printer.println("                        \"size\": " + model.getData().getSize() + ",");
                    printer.println("                    },");
                    printer.println("                    \"status\":" + model.getStatus() + ",");
                    printer.println("                    \"results\":" + model.getResult());
                    printer.println("                },");
                }
            }
        }
        printer.println("            ]");
        int cputime = 0;
        int totbatch = 0;
        for (CPU cpu: cpus) {
            cputime += cpu.getTime();
            totbatch+= cpu.getBatchesProcessed();
        }
        int gputime = 0;
        for (GPU gpu: gpus) {
            gputime+= gpu.getGpuTime();
        }
        printer.println("    \"cpuTimeUsed\": " + cputime);
        printer.println("    \"gpuTimeUsed\": " + gputime);
        printer.println("    \"batchesProcessed\": " + totbatch);
        printer.println("}");
        printer.close();
        return file;
    }
}


