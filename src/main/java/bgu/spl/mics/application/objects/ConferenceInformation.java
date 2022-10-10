package bgu.spl.mics.application.objects;

import java.util.ArrayList;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConferenceInformation {

    private String name;
    private int date;
    private int realDate;
    private ArrayList<Model> models;
    public ConferenceInformation() {
        models = new ArrayList<>();
    }

    /**
     *
     * @param name Conference name
     * @param date Conference Date
     */
    public ConferenceInformation(String name, int date) {
        this.name = name;
        this.date = date;
        realDate = date;
        models = new ArrayList<>();
    }

    /**
     *
     * @return Conference's name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return Conference's date
     */
    public int getDate() {
        return date;
    }
    public int getRealDate(){
        return realDate;
    }
    public void updateDate(){
        date--;
    }
    public void add(Model model){
        models.add(model);
    }
    public ArrayList<Model> getModels() {
        return models;
    }
}
