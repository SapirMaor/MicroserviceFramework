package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {
    /**
     * Enum representing the Degree the student is studying for.
     */
    enum Degree {
        MSc, PhD
    }

    private static int id = 0;
    private String name;
    private String department;
    private Degree status;
    private int publications;
    private int papersRead;
    private int studentID;
    public Student(){
    }

    /**
     *
     * @param _name : Student's name
     * @param _department : Department's Name
     * @param _status : Student's degree
     */

    public Student(String _name, String _department, String _status){
        name = _name;
        department = _department;
        status = Degree.valueOf(_status);
        publications = 0;
        papersRead = 0;
        studentID = id;
        id++;
    }
    /**
     *
     * @return : Student's name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return Student's department
     */
    public String getDepartment() {
        return department;
    }

    /**
     *
     * @return Student's degree
     */
    public Degree getStatus() {
        return status;
    }

    /**
     *
     * @return number of publications
     */
    public int getPublications(){
        return publications;
    }

    /**
     * Increment publications
     */
    public void incrementPublications(){
        publications++;
    }

    /**
     *
     * @return number of papers read
     */
    public int getPapersRead(){
        return papersRead;
    }
    /**
     * Increment Papers Read
     */
    public void incrementPapersRead(){
        papersRead++;
    }

    /**
     * @return student's ID
     */
    public int getStudentID() {
        return studentID;
    }

}
