package ua.edu.ratos.edx.domain;

public class Faculty {

    private Long facId;

    private String name;

    private Organisation organisation;

    public Long getFacId() {
        return facId;
    }

    public void setFacId(Long facId) {
        this.facId = facId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    @Override
    public String toString() {
        return "Faculty{" +
                "facId=" + facId +
                ", name='" + name + '\'' +
                '}';
    }
}
