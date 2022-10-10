package bgu.spl.mics.application.objects;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {

    enum Status{
        PreTrained, Training, Trained, Tested
    }
    enum Results{
        None, Good, Bad
    }

    private String name;
    private Data data;
    private Student student;
    private Status status;
    private Results result;

    public Model(){
    }

    /**
     *
     * @param _name Model's name
     * @param _data Model's data
     * @param _student Student who created the Model
     */
    public Model(String _name, Data _data, Student _student){
        name = _name;
        data = _data;
        student = _student;
        status = Status.PreTrained;
        result = Results.None;

    }
    public Model(String name, String type, int size){
        this.name = name;
        data = new Data(type, size);
        status = Status.PreTrained;
    }

    /**
     *
     * @return Model's name
     */
    public String getName(){
        return name;
    }

    /**
     *
     * @return Model's Data
     */
    public Data getData() {
        return data;
    }

    /**
     *
     * @return The student who created the model
     */
    public Student getStudent() {
        return student;
    }
    public void setData(Data data) {
        this.data = data;
    }
    public void setStudent(Student student) {
        this.student = student;
    }
    public Status getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = Status.valueOf(status);
    }
    public Results getResult() {
        return result;
    }
    public String getResultAsString() {
        return result.toString();
    }
    public void setResult(String result) {
        this.result = Results.valueOf(result);
    }
    public boolean toPublish(){
        return result.equals(Results.Good);
    }
}
