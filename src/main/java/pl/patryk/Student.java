package pl.patryk;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import org.bson.types.ObjectId;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.annotations.*;
import org.mongodb.morphia.query.Query;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;
import java.util.List;

/**
 * Created by Pyra on 2016-04-19.
 */
@XmlRootElement
@Entity
//@Indexes(@Index(fields = @Field(value = "ajdi")))
public class Student {

    @InjectLinks({
            @InjectLink(
                    value = "/app/students",
                    style = InjectLink.Style.ABSOLUTE,
                    rel = "self"
            ),
            @InjectLink(
                    value = "/app/students/{id}",
                    style = InjectLink.Style.ABSOLUTE,
                    bindings = @Binding(name = "id", value = "${instance.index}"),
                    rel = "student"
            ),
            @InjectLink(
                    value = "/app/students/{id}/grades",
                    style = InjectLink.Style.ABSOLUTE,
                    bindings = @Binding(name = "id", value = "${instance.index}"),
                    rel = "grades"
            )
    })
    @XmlElement(name = "link")
    @XmlElementWrapper(name = "links")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    List<Link> links;

    @Id
    @XmlTransient
    private ObjectId id;
    private boolean hasGrades = false;
//    private String birthDate;
    @JsonFormat(shape=JsonFormat.Shape.STRING,
        pattern="yyyy-MM-dd", timezone="UTC")
    private Date birthDate;
    @Indexed(options = @IndexOptions(unique = true))
    private int index;
    private String name;
    private String lastName;

    public Student() {
//        int lastIndex = 0;
//        Model mdl = new Model();
//        Datastore ds = mdl.ds;
//        List<Student> query = ds.find(Student.class).order("-index").asList();
//        if(!query.isEmpty())
//            lastIndex = query.get(0).getIndex();
//        this.index = ++lastIndex;
    }

    public Student(Date birthDate, String name, String lastName) {
        int lastIndex = 0;
        this.birthDate = birthDate;
        Model mdl = new Model();
        Datastore ds = mdl.ds;
        List<Student> query = ds.find(Student.class).order("-index").asList();
        if(!query.isEmpty())
            lastIndex = query.get(0).getIndex();
        this.index = ++lastIndex;
        this.lastName = lastName;
        this.name = name;
    }

    public Student(Date birthDate, String name, String lastName, boolean x) {
        int lastIndex = 0;
        this.birthDate = birthDate;
        Model mdl = new Model();
        if(mdl.getStudentList().size() > 0)
            lastIndex = mdl.getStudentList().get(mdl.getStudentList().size()-1).getIndex();
        this.index = ++lastIndex;
        this.lastName = lastName;
        this.name = name;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setBirthDate(Date value) {
        this.birthDate = value;
    }

    public void setIndex(int value) {
        this.index = value;
    }

    public void setName(String value) {
        this.name = value;
    }

    public void setLastName(String value) {
        this.lastName = value;
    }

    public boolean checkProperties() {
        if ((this.getName() != null)
                && (this.getBirthDate() != null)
                && (this.getLastName() != null))
            return true;
        else
            return false;
    }

    public void hasGrades() {
        Model mdl = new Model();
        for(Subject subject : mdl.getSubjectList())
        {
            subject.getGradeList().stream().
                    filter(grade -> grade.getStudent().getIndex() == this.getIndex()).
                    forEach(grade -> hasGrades = true);
        }
    }

    @XmlTransient
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }
}