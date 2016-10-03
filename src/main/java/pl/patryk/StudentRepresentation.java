package pl.patryk;

import jdk.nashorn.internal.objects.annotations.Getter;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;

import java.util.List;

/**
 * Created by Pyra on 2016-04-24.
 */
public class StudentRepresentation {
    private List<Student> students;

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }


}
