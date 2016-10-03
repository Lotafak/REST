package pl.patryk;

import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.mongodb.morphia.Datastore;

import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Pyra on 2016-04-19.
 */
public class Model {
    private List<Student> studentList;
    private List<Subject> subjectList;
    private List<Student> studentListMongo;
    private List<Subject> subjectListMongo;
    Datastore ds;


    public Model() {
        Singleton singleton = Singleton.getInstance();
        studentList = singleton.studentList;
        subjectList = singleton.subjectList;
        studentListMongo = singleton.studentListMongo;
        subjectListMongo = singleton.subjectListMongo;
        ds = singleton.ds;
    }

    public List<Student> getStudentList() {

        Singleton singleton = Singleton.getInstance();
        //singleton.studentList.sort(student -> student.getIndex());
        singleton.studentList.forEach(Student::hasGrades);
        return singleton.studentList;
    }

    public List<Subject> getSubjectList() {
        Singleton singleton = Singleton.getInstance();
        return  singleton.subjectList;
    }

//    public void addData() {
//        Calendar cal = Calendar.getInstance();
//        cal.set(Calendar.MONTH, 6);
//        cal.set(Calendar.DAY_OF_MONTH, 17);
//        cal.set(Calendar.YEAR, 1993);
//
//        Student patryk = new Student(cal.getTime(), "Patryk", "Kudla", true);
//        studentList.add(patryk);
//        Student maciej = new Student(cal.getTime(), "Maciej", "Konieczny", true);
//        studentList.add(maciej);
//
//        Grade grade = new Grade(patryk, 4, "13.03.2016", true);
//        List<Grade> grdList = new ArrayList<Grade>();
//        grdList.add(grade);
//        subjectList.add(new Subject(grdList, "Se", "Ba"));
//        Grade grade1 = new Grade(maciej, 5, "13.03.2016", true);
//        grdList.add(grade1);
//        subjectList.add(new Subject(grdList,"Dąbrowski", "Algebra"));
//    }

    public void addMongoData() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, 6);
        cal.set(Calendar.DAY_OF_MONTH, 17);
        cal.set(Calendar.YEAR, 1993);

        Singleton singleton = Singleton.getInstance();
        Student patryk = new Student(cal.getTime(), "Pierwszy", "Osobnik" );
        studentListMongo.add(patryk);
        ds.save(patryk);
        cal.add(Calendar.DATE, 3);
        Student maciej = new Student(cal.getTime(), "Drugi", "Osobnik");
        studentListMongo.add(maciej);
        ds.save(maciej);

        Calendar cal2 = Calendar.getInstance();
        cal2.set(Calendar.MONTH, 4);
        cal2.set(Calendar.DAY_OF_MONTH, 15);
        cal2.set(Calendar.YEAR, 2014);

        Grade grade = new Grade(patryk, 4, cal2.getTime());
        List<Grade> grdList = new ArrayList<Grade>();
        grdList.add(grade);
        subjectListMongo.add(new Subject(grdList, "Wykladowca1", "Przedmiot1"));
        ds.save(new Subject(grdList, "Wykladowca1", "Przedmiot1"));
        cal2.add(Calendar.DATE, 15);
        Grade grade1 = new Grade(maciej, 5, cal2.getTime());
        grdList.clear();
        grdList.add(grade1);
        subjectListMongo.add(new Subject(grdList,"Wykladowca2", "Przedmiot2"));
        ds.save(new Subject(grdList,"Wykladowca2", "Przedmiot2"));
    }

    public List<Student> getStudentListMongo() {
        return studentListMongo;
    }

    public void setStudentListMongo(List<Student> studentListMongo) {
        this.studentListMongo = studentListMongo;
    }

    public List<Subject> getSubjectListMongo() {
        return subjectListMongo;
    }

    public void setSubjectListMongo(List<Subject> subjectListMongo) {
        this.subjectListMongo = subjectListMongo;
    }
}
