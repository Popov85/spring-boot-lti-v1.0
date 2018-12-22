package ua.edu.ratos.edx.domain;

public class Class {

    private Long classId;

    private String name;

    private Faculty faculty;

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public void setFaculty(Faculty faculty) {
        this.faculty = faculty;
    }

    @Override
    public String toString() {
        return "Class{" +
                "classId=" + classId +
                ", name='" + name + '\'' +
                '}';
    }
}
