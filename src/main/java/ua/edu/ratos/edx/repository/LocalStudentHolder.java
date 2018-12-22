package ua.edu.ratos.edx.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.edu.ratos.edx.domain.Student;
import ua.edu.ratos.edx.domain.User;
import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Exceptionally for testing purposes
 */
@Component
public class LocalStudentHolder {

    @Autowired
    private LocalClassesHolder localClassesHolder;

    private Map<String, Student> students = new HashMap<>();

    public void addStudent(String email, Student student) {
        this.students.put(email, student);
    }

    public Optional<Student> getByEmail(String email) {
        return Optional.ofNullable(this.students.get(email));
    }

    @PostConstruct
    public  void init() {
        User u = new User();
        u.setUserId(1L);
        u.setEmail("user@example.com");
        u.setName("name");
        u.setSurname("surname");
        u.setPassword("{noop}password".toCharArray());
        u.setRoles(new HashSet<>(Arrays.asList("ROLE_STUDENT")));

        Student s = new Student(u);
        s.setEntranceYear(2018);
        s.setStudentClass(localClassesHolder.getById(1L).get());
        s.setUser(u);

        students.put("user@example.com", s);
    }

}
