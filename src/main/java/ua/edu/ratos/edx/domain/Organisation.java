package ua.edu.ratos.edx.domain;

public class Organisation {
    private Long orgId;

    private String name;

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Organisation{" +
                "orgId=" + orgId +
                ", name='" + name + '\'' +
                '}';
    }
}
