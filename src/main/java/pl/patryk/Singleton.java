package pl.patryk;

import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pyra on 2016-04-19.
 */
public class Singleton {

    private static Singleton instance = null;

    List<Student> studentList;
    List<Subject> subjectList;
    List<Student> studentListMongo;
    List<Subject> subjectListMongo;
    MongoClient client;
    final Morphia morphia;
    Datastore ds;

    private Singleton() {
        // Exists only to defeat instantiation.
        studentList = new ArrayList<Student>();
        subjectList = new ArrayList<Subject>();
        studentListMongo = new ArrayList<Student>();
        subjectListMongo = new ArrayList<Subject>();
        client = new MongoClient("localhost", 8002);
        morphia = new Morphia();
        morphia.mapPackage("pl.patryk");
        ds = morphia.createDatastore( client, "exampledb");
    }

    public static Singleton getInstance() {
        if(instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}