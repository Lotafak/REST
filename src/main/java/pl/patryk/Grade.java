package pl.patryk;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.bson.types.ObjectId;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.*;

/**
 * Created by Pyra on 2016-04-19.
 */
@XmlRootElement
@Entity
public class Grade {

    @InjectLinks({
            @InjectLink(
//                    method = "getStudents",
                    value = "app/students/{name}/grades/{id}",
                    style = InjectLink.Style.ABSOLUTE,
                    bindings = {@Binding(name = "id", value = "${instance.index}"),
                            @Binding(name = "name", value = "${instance.student.index}")},
                    rel = "self"
            )
    })
@XmlElement(name = "link")
@XmlElementWrapper(name = "links")
@XmlJavaTypeAdapter(Link.JaxbAdapter.class)
List<Link> links;

    @JsonFormat(shape=JsonFormat.Shape.STRING,
            pattern="yyyy-MM-dd", timezone="UTC")
    private Date issueDate;
    private double value;
    @Reference
    private Student student;
    private int index;
    @Id
    @XmlTransient
    private ObjectId id;

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }
    @Reference
    private Subject subject;

    Grade() {
        Model mdl = new Model();
        int i = 0;
        for(Subject subject : mdl.getSubjectList())
        {
            for(Grade grade : subject.getGradeList())
            {
                i++;
            }
        }
        this.index = ++i;
    }

    Grade(Student student, double value, Date issueDate, boolean x)
    {
        Model mdl = new Model();
        int i = 0;
        for(Subject subject : mdl.getSubjectList())
        {
            for(Grade grade : subject.getGradeList())
            {
                i++;
            }
        }
        this.index = ++i;
        this.issueDate = issueDate;
        this.value = value;
        this.student = student;
    }

    Grade(Student student, double value, Date issueDate)
    {
        List<Grade> gradeList = new ArrayList<Grade>();
        int lastIndex = 0;
        Model mdl = new Model();
        Datastore ds = mdl.ds;
        List<Subject> query = ds.find(Subject.class).asList();

        for(int i=0; i<query.size(); i++)
        {
            gradeList.addAll(query.get(i).getGradeList());
        }

        gradeList.stream()
                .sorted((object1, object2) -> ((Integer)object1.getIndex()).compareTo(object2.getIndex()));

        Collections.sort(gradeList, (object1, object2) -> ((Integer)object1.getIndex()).compareTo(object2.getIndex()));

        if(gradeList.size() > 0)
            lastIndex = gradeList.get(gradeList.size()-1).getIndex();
        this.index = ++lastIndex;
        this.issueDate = issueDate;
        this.value = value;
        this.student = student;
    }

    public Date getIssueDate() { return issueDate; }
    public double getValue() {return value;}
    public Student getStudent() {return student;}
//    public int getIndex() { return index;}
public int getIndex() {
    if(this.index == 0 ) {
        List<Grade> gradeList = new ArrayList<Grade>();
        int lastIndex = 0;
        Model mdl = new Model();
        Datastore ds = mdl.ds;
        List<Subject> query = ds.find(Subject.class).asList();

        for (int i = 0; i < query.size(); i++) {
            gradeList.addAll(query.get(i).getGradeList());
        }

        gradeList.stream()
                .sorted((object1, object2) -> ((Integer) object1.getIndex()).compareTo(object2.getIndex()));

        Collections.sort(gradeList, (object1, object2) -> ((Integer) object1.getIndex()).compareTo(object2.getIndex()));

        if (gradeList.size() > 0)
            lastIndex = gradeList.get(gradeList.size() - 1).getIndex();
        return ++lastIndex;
    }
    return this.index;
}



    public void setIssueDate(Date issueDate) {this.issueDate = issueDate;}
    public void setValue(double value) {this.value = value;}
    public void setStudent(Student student) {this.student = student;}
    public void setIndex(int index) {this.index = index;}

    public boolean checkProperties() {
        if((this.getIssueDate() != null )
                && (this.getStudent() != null)
                && ((this.getValue() >= 2.0) && (this.getValue() <= 5.0 )))
            return true;
        else
            return false;
    }

    @XmlTransient
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }
}