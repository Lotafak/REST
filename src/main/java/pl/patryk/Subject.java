package pl.patryk;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.mongodb.DBRef;
import org.bson.types.ObjectId;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinkNoFollow;
import org.glassfish.jersey.linking.InjectLinks;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pyra on 2016-04-19.
 */
@XmlRootElement
@Entity
public class Subject {

    @InjectLinks({
            @InjectLink(
                    value = "/app/subjects",
                    style = InjectLink.Style.ABSOLUTE,
                    rel = "self"
            ),
            @InjectLink(
                    value = "/app/subjects/{name}",
                    style = InjectLink.Style.ABSOLUTE,
                    bindings = @Binding(name = "name", value = "${instance.name}"),
                    rel = "student"
            ),
            @InjectLink(
                    value = "/app/subjects/{name}/grades",
                    style = InjectLink.Style.ABSOLUTE,
                    bindings = @Binding(name = "id", value = "${instance.name}"),
                    rel = "grades"
            )

    })
    @XmlElement(name = "link")
    @XmlElementWrapper(name = "links")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    @InjectLinkNoFollow
    List<Link> links;

    @Id
//    @XmlJavaTypeAdapter(ObjectIdJaxbAdapter.class)
    @XmlTransient
    private ObjectId id;
//    @Reference
    @Embedded
    @XmlTransient
    private List<Grade> gradeList = new ArrayList<Grade>();
    private String lecturer;
    private String name;

    Subject() {
        gradeList = new ArrayList<Grade>();
    }

    Subject(List<Grade> gradeList, String lecturer, String name)  {
        for(Grade grade : gradeList)
            this.gradeList.add(grade);
        this.lecturer = lecturer;
        this.name = name;
    }


    @XmlTransient
    public List<Grade> getGradeList() {return gradeList;}
    public String getName() { return name;}
    public String getLecturer() {return lecturer; }

    public void setGradeList(List<Grade> gradeList) { this.gradeList = gradeList; }
    public void setName(String name) {this.name = name;}
    public void setLecturer(String lecturer) { this.lecturer = lecturer; }

    public boolean checkProperties() {
        if((this.getName() != null ) && (this.getGradeList() != null)
                &&(this.getLecturer() != null))
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
