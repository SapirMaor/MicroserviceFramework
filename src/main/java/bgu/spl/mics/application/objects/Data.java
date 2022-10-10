package bgu.spl.mics.application.objects;


import java.util.Locale;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Data {

    /**
     * Enum representing the Data type.
     */
    enum Type {
        Images, Text, Tabular
    }

    private Type type;
    private int processed;
    private final int size;

    public Data(String _type, int _size) {
        switch (_type.toLowerCase()){
            case "images": type = Type.Images;
                break;
            case "text": type = Type.Text;
                break;
            case "tabular": type = Type.Tabular;
                break;
        }
        processed =0;
        size = _size;
    }
    public Type getType(){
        return type;
    }
    public int getSize(){
        return size;
    }
    public void process(){
        processed+= 1000;
    }
    public boolean isDone(){
        return processed == size;
    }
    public int getProcessed(){
        return processed;
    }
}
