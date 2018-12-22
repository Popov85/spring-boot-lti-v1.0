package ua.edu.ratos.edx.domain;

import javax.validation.Valid;
import javax.validation.constraints.Min;

public class Student {

    @Valid
    private User user;

    private Class studentClass;

    @Min(2018)
    private int entranceYear;

    public Student(User user) {
        this.user = user;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Class getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(Class studentClass) {
        this.studentClass = studentClass;
    }

    public int getEntranceYear() {
        return entranceYear;
    }

    public void setEntranceYear(int entranceYear) {
        this.entranceYear = entranceYear;
    }

    @Override
    public String toString() {
        return "Student{" +
                "user=" + user +
                ", studentClass=" + studentClass +
                ", entranceYear=" + entranceYear +
                '}';
    }
}
