package pl.patryk;

import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.xml.crypto.Data;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Created by Pyra on 2016-04-19.
 */
@Path("/app")
public class Controller {

    @GET
    @Path("/students")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getStudents(@DefaultValue("") @QueryParam("name") String name,
                                @DefaultValue("") @QueryParam("lastName") String lastName,
                                @DefaultValue("0") @QueryParam("index") int index,
                                @DefaultValue("") @QueryParam("before") String before,
                                @DefaultValue("") @QueryParam("after") String after,
                                @DefaultValue("") @QueryParam("equal") String equal) {
        Model mdl = new Model();
        Datastore ds = mdl.ds;
        List<Student> tempList = new ArrayList<>();
        tempList = ds.find(Student.class).asList();
        if(!lastName.isEmpty()) {
            tempList = tempList
                    .stream()
                    .filter(student -> student.getLastName().toLowerCase().contains(lastName.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (!name.isEmpty()){
            tempList = tempList
                    .stream()
                    .filter(student -> student.getName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
        }

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        if (!equal.isEmpty()) {
            try {
                java.util.Date queryDate = df.parse(equal);

                tempList = tempList.stream().
                        filter(student -> compareDates(student.getBirthDate(), queryDate)).
                        collect(Collectors.toList());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else if(!before.isEmpty())
        {
            try {
                java.util.Date queryDate = df.parse(before);

                tempList = tempList.stream().
                        filter(student -> compareBeforeDates(student.getBirthDate(), queryDate)).
                        collect(Collectors.toList());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else if (!after.isEmpty())
        {
            try {
                java.util.Date queryDate = df.parse(after);

                tempList = tempList.stream().
                        filter(student -> compareAfterDates(student.getBirthDate(), queryDate)).
                        collect(Collectors.toList());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if(index != 0){
            if(!tempList.isEmpty()){
                tempList = tempList.stream().filter(x -> x.getIndex() == index).collect(Collectors.toList());
            }
        }

        return Response.ok(tempList, MediaType.APPLICATION_JSON).build();
    }

    public boolean compareDates(java.util.Date date1, java.util.Date date2)
    {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(date1);
        c2.setTime(date2);

        return ((c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)) &&
                (c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)));
    }

    public boolean compareBeforeDates(java.util.Date date1, java.util.Date date2)
    {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(date1);
        c2.setTime(date2);

        return ((c1.get(Calendar.YEAR) < c2.get(Calendar.YEAR)) ||
                ( (c1.get(Calendar.DAY_OF_YEAR) < c2.get(Calendar.DAY_OF_YEAR) &&
                        (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR))) ));
    }

    public boolean compareAfterDates(java.util.Date date1, java.util.Date date2)
    {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(date1);
        c2.setTime(date2);

        return ((c1.get(Calendar.YEAR) > c2.get(Calendar.YEAR)) ||
                ( (c1.get(Calendar.DAY_OF_YEAR) > c2.get(Calendar.DAY_OF_YEAR) &&
                        (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR))) ));
    }


    @GET
    @Path("students/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Student getMessage(@PathParam("id") int id) {
        Model mdl = new Model();
        Datastore ds = mdl.ds;

        return ds.find(Student.class, "index", id).get();
    }

    @GET
    @Path("/students/{id}/grades")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<Grade> getGrades(@PathParam("id") int id,
                                 @DefaultValue("") @QueryParam("course") String course,
                                 @DefaultValue("") @QueryParam("date") String date,
                                 @DefaultValue("") @QueryParam("value") String value,
                                 @DefaultValue("") @QueryParam("less") String less,
                                 @DefaultValue("") @QueryParam("more") String more) {

        Model mdl = new Model();
        Datastore ds = mdl.ds;
        List<Grade> tempList = new ArrayList<>();
        double gradeValue = 0;
        if(!value.isEmpty())
            gradeValue = Double.parseDouble(value);
        else if (value.equals(""))
            gradeValue = 0.0;
        else if (value.equals("0"))
            gradeValue = 0.0;

        List<Subject> tempSubjectList = ds.find(Subject.class).asList();
        for (Subject gl : tempSubjectList) {
            for (Grade grade : gl.getGradeList()) {
                if (grade.getStudent().getIndex() == id) {
                    grade.setSubject(gl);
                    try {
//                        if (less.isEmpty() && !more.isEmpty()) {
//                            if (grade.getValue() >= Double.parseDouble(more))
//                                tempList.add(grade);
//                        } else if (more.isEmpty() && !less.isEmpty()) {
//                            if (grade.getValue() <= Double.parseDouble(less))
//                                tempList.add(grade);
//                        } else if (!less.isEmpty() && !more.isEmpty()) {
//                            if((grade.getValue() >= Double.parseDouble(more)) && (grade.getValue() <= Double.parseDouble(less)))
//                                tempList.add(grade);
//                        }else if (less.isEmpty() && more.isEmpty()) {
//                            tempList.add(grade);
                        if(gradeValue == 0.0)
                            tempList.add(grade);
                        else if(gradeValue > 2.0)
                        {
                            if(grade.getValue() == Double.parseDouble(value))
                                tempList.add(grade);
                        }
                    }
                    catch(NumberFormatException ex ) { }
                }
            }
        }
//
//        if(!index.isEmpty())
//        {
//            if(!tempList.isEmpty()){
//                tempList = tempList.stream().filter(x -> x.getIndex() == Integer.valueOf(index)).collect(Collectors.toList());
//            }
//        }

        if(!course.isEmpty())
        {
            if(!tempList.isEmpty())
            {
                tempList = tempList.stream().filter(x -> x.getSubject().getName().toLowerCase().contains(course.toLowerCase())).collect(Collectors.toList());
            }
        }

        if(!date.isEmpty()) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            try {

                java.util.Date queryDate = df.parse(date);
                tempList = tempList.stream().
                        filter(grade -> compareDates(grade.getIssueDate(), queryDate)).
                        collect(Collectors.toList());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return tempList;
    }

    @GET
    @Path("/subjects")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<Subject> getSubjects(@DefaultValue("all") @QueryParam("name") String name) {
        Model mdl = new Model();
        Datastore ds = mdl.ds;

        List<Subject> tempList;

        if(name.equals("all"))
            tempList = ds.find(Subject.class).asList();
        else
            tempList = ds.find(Subject.class).filter("lecturer", name).asList();

        return tempList;
    }

    @GET
    @Path("subjects/{subject}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Subject getSubject(@PathParam("subject") String name) {
        Model mdl = new Model();
        Datastore ds = mdl.ds;

        for (Subject sbj : ds.find(Subject.class).asList()) {
            if (sbj.getName().equals(name))
                return sbj;
        }
        return null;
    }

    @GET
    @Path("subjects/{subject}/grades")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<Grade> getGrades(@PathParam("subject") String name,
                                 @DefaultValue("all") @QueryParam("student") String student) {
        Model mdl = new Model();
        List<Grade> grades = new ArrayList<Grade>();

        Datastore ds = mdl.ds;


            for (Subject subject : ds.find(Subject.class).asList()) {
                if (subject.getName().equals(name)) {
                    for (Grade grade : subject.getGradeList()) {
                        try {
                            if (student.equals("all")) {
                                grades.add(grade);
                            }
                            else if (grade.getStudent().getIndex() == Integer.parseInt(student))
                                grades.add(grade);
                        }
                        catch (NumberFormatException ex) {}
                    }
                }
            }
        return grades;
    }

    @GET
    @Path("subjects/{subject}/grades/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Grade getGrade(@PathParam("subject") String name,
                          @PathParam("id") int id) {
        Model mdl = new Model();
        Grade grade = null;

        Datastore ds = mdl.ds;

        for (Subject subject : ds.find(Subject.class).asList()) {
            if (subject.getName().equals(name))
                for (Grade grd : subject.getGradeList()) {
                    if(grd.getIndex() == id)
                        grade = grd;
                }
        }

        return grade;
    }

    @GET
    @Path("students/{student}/grades/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Grade getGrade(@PathParam("student") int student,
                          @PathParam("id") int id) {
        Model mdl = new Model();
        Grade grade = null;

        Datastore ds = mdl.ds;

        for (Subject subject : ds.find(Subject.class).asList()) {
                for (Grade grd : subject.getGradeList()) {
                    if(grd.getStudent().getIndex() == student) {
                        grade = grd;
//                        grade.setSubject(subject);
                    }
                }
        }
        return grade;
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/students")
    public Response studentPost(Student student) {
        try {
            Model mdl = new Model();
            int lastIndex = 0;
            Datastore ds = mdl.ds;

            List<Student> query = ds.find(Student.class).order("-index").asList();
            if (!query.isEmpty())
                lastIndex = query.get(0).getIndex();
            student.setIndex(++lastIndex);
            ds.save(student);
            System.out.println(Integer.toString(student.getIndex()));
            return Response.created(UriBuilder.fromPath("/students/" + Integer.toString(student.getIndex())).build()).build();
        }catch (Exception e){
            return Response.status(500).build();
        }
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/subjects")
    public Response subjectPost(Subject subject) {
        Model mdl = new Model();
        Datastore ds = mdl.ds;
        ds.save(subject);

        System.out.println(subject.getName());
        return Response.created(UriBuilder.fromPath("/subjects/" + subject.getName()).build()).build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("subjects/{name}/grades")
    public Response gradePost(@PathParam("name") String name,
                              Grade grade) {
        Model mdl = new Model();
        Datastore ds = mdl.ds;
        int lastIndex = 0;

        Query<Subject> q = ds.createQuery(Subject.class).filter("name", name);
        ArrayList<Subject> subjects = new ArrayList<>(q.asList());
        for(Subject subject : subjects )
        {
            ArrayList<Grade> grades = new ArrayList<>(subject.getGradeList());
            if(grade.getStudent().getIndex() == 0) {
                List<Student> studentList = ds.find(Student.class).order("-index").asList();
                if (!studentList.isEmpty())
                    lastIndex = studentList.get(0).getIndex();
                grade.getStudent().setIndex(++lastIndex);
                ds.save(grade.getStudent());
            }else{
                Student newStudent = ds.find(Student.class)
                        .filter("index", grade.getStudent().getIndex())
                        .get();
                grade.setStudent(newStudent);
            }
            grade.setSubject(subject);
            grade.setIndex(grade.getIndex());
            grades.add(grade);
            subject.setGradeList(grades);
            ds.save(subject);
        }
        if(subjects.isEmpty())
        {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.created(UriBuilder.fromPath("/subjects/" + name + "/grades" + Integer.toString(grade.getIndex())).build()).build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("students/{studentId}/grades")
    public Response gradePost(@PathParam("studentId") int studentId, Grade grade) {
        Model mdl = new Model();

        Subject searchedSubject = null;
        Student searchedStudent = null;
        Datastore ds = mdl.ds;
        int lastIndex = 0;

        for (Subject subject : ds.find(Subject.class).asList()) {
            for (Grade grd : subject.getGradeList()) {
                lastIndex++;
                if(grd.getStudent().getIndex() == studentId) {
                    searchedSubject = subject;
                    searchedStudent = grd.getStudent();
                }
            }
        }
        grade.setStudent(searchedStudent);
        grade.setIndex(++lastIndex);
        searchedSubject.getGradeList().add(grade);
        ds.save(searchedSubject);

        return Response.created(UriBuilder.fromPath("/students/" + studentId + "/grades" + Integer.toString(grade.getIndex())).build()).build();
    }

    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("students/{id}")
    public Response studentPut(@PathParam("id") int id,
                               Student student) {
        Model mdl = new Model();
        Datastore ds = mdl.ds;

        try {
            if(!student.checkProperties())
                throw new IllegalArgumentException();
            Query<Student> q = ds.createQuery(Student.class).filter("index", id); //NAPRAWIć
            Student newStudent = q.get();
            newStudent.setBirthDate(student.getBirthDate());
            newStudent.setLastName(student.getLastName());
            newStudent.setName(student.getName());
            ds.save(newStudent);
            return Response.ok().build();
        } catch (NoSuchElementException | IllegalArgumentException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e )
        {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("subjects/{name}")
    public Response subjectPut(Subject newSubject,
                               @PathParam("name") String name) {
        Model mdl = new Model();
        Datastore ds = mdl.ds;

        try {
            Subject subject = ds.createQuery(Subject.class).filter("name", name).get();
            subject.setLecturer(newSubject.getLecturer());
            ds.save(subject);
            return Response.ok().build();
        } catch (NoSuchElementException | IllegalArgumentException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e )
        {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("subjects/{studentId}/grades/{id}")
    public Response gradePut(@PathParam("studentId") int studentId,
                             @PathParam("id") int id,
                             Grade grade) {
        Model mdl = new Model();
        Datastore ds = mdl.ds;

        try {
            Query<Subject> query = ds.createQuery(Subject.class).field("gradeList.index").equal(id);

            Subject tempSubject = query.get();
            List<Grade> gradeList = new ArrayList<>(tempSubject.getGradeList());
            for (Grade grd : tempSubject.getGradeList())
            {
                if(grd.getIndex() == id)
                {
                    gradeList.remove(grd);
//                    if(grade.getStudent().getIndex() == 0)
//                    {
//                        Student newStudent = new Student(null,"", "");
//                        grd = grade;
//                        grd.getStudent().setIndex(newStudent.getIndex());
//                        ds.save(grd.getStudent());
//                    } else {
                        Student newStudent = ds.find(Student.class)
                                .filter("index", studentId)
                                .get();
                        grd.setStudent(newStudent);
                    //}

                    grd.setValue(grade.getValue());
                    grd.setIssueDate(grade.getIssueDate());
                    grd.setIndex(id);
                    gradeList.add(grd);
                }
            }
            tempSubject.setGradeList(gradeList);
            ds.save(tempSubject);
            return Response.ok().build();
        } catch (NoSuchElementException | IllegalArgumentException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e )
        {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @DELETE
    @Path("students/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response deleteStudents(@PathParam("id") int id) {
        try {
            Model mdl = new Model();
            Datastore ds = mdl.ds;

            Query<Student> student = ds.createQuery(Student.class).filter("index", id);
            Query<Subject> subject = ds.createQuery(Subject.class).filter("gradeList.student",student.get());
            ArrayList<Subject> subjects = new ArrayList<>(subject.asList());
            for(Subject sbj : subjects)
            {
                for(Grade grade : sbj.getGradeList())
                    grade.setStudent(null);
            }

            ds.delete(student);
            ds.save(subject);
            return Response.ok().build();
        } catch (NoSuchElementException | IllegalArgumentException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e )
        {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @DELETE
    @Path("subjects/{name}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response deleteSubjects(@PathParam("name") String name) {
        try {
            Model mdl = new Model();
            Datastore ds = mdl.ds;

            ds.delete(ds.createQuery(Subject.class).filter("name", name));
            return Response.ok().build();
        } catch (NoSuchElementException | IllegalArgumentException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e )
        {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @DELETE
    @Path("subjects/{name}/grades/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response deleteSubjects(@PathParam("name") String name,
                                   @PathParam("id") int id) {
        try {
            Model mdl = new Model();
            Datastore ds = mdl.ds;

            Query<Subject> q = ds.createQuery(Subject.class).filter("name", name);
            q.field("gradeList.index").equal(id);
            Subject subject = q.get();
            ArrayList<Grade> tempGradeList = new ArrayList<>(subject.getGradeList());

            for (Grade grade : subject.getGradeList()) {
                if (grade.getIndex() == id)
                    tempGradeList.remove(grade);
            }

            subject.setGradeList(tempGradeList);
            ds.save(subject);
            return Response.ok().build();
        } catch (NoSuchElementException | IllegalArgumentException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e )
        {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @DELETE
    @Path("students/{studentId}/grades/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response deleteSubjects(@PathParam("studentId") int studentId,
                                   @PathParam("id") int gradeId) {
        try {
            Model mdl = new Model();
            Datastore ds = mdl.ds;
            Subject searchedSubject = null;

            for (Subject subject : ds.find(Subject.class).asList()) {
                for (Grade grd : subject.getGradeList()) {
                    if(grd.getStudent().getIndex() == studentId) {
                        searchedSubject = subject;
                    }
                }
            }

            ArrayList<Grade> tempGradeList = new ArrayList<>(searchedSubject.getGradeList());

            for (Grade grade : searchedSubject.getGradeList()) {
                if (grade.getIndex() == gradeId)
                    tempGradeList.remove(grade);
            }

            searchedSubject.setGradeList(tempGradeList);
            ds.save(searchedSubject);
            return Response.ok().build();
        } catch (NoSuchElementException | IllegalArgumentException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e )
        {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
}