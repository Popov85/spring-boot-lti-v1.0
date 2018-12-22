package ua.edu.ratos.edx.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.edu.ratos.edx.domain.Faculty;
import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class LocalFacultiesHolder {

    @Autowired
    private LocalOrganisationsHolder localOrganisationsHolder;

    private Map<Long, Faculty> faculties = new HashMap<>();

    @PostConstruct
    public void init() {
        Faculty f1 = new Faculty();
        f1.setFacId(1L);
        f1.setName("1st Faculty");
        f1.setOrganisation(localOrganisationsHolder.getById(1L).get());
        this.faculties.put(1L, f1);

        Faculty f2 = new Faculty();
        f2.setFacId(2L);
        f2.setName("2d Faculty");
        f2.setOrganisation(localOrganisationsHolder.getById(1L).get());
        this.faculties.put(2L, f2);

        Faculty f3 = new Faculty();
        f3.setFacId(3L);
        f3.setName("3d Faculty");
        f3.setOrganisation(localOrganisationsHolder.getById(1L).get());
        this.faculties.put(3L, f3);
    }

    public Optional<Faculty> getById(Long orgId) {
        return Optional.ofNullable(this.faculties.get(orgId));
    }

    public Set<Faculty> getAll() {
        return this.faculties.values().stream().collect(Collectors.toSet());
    }

    public Set<Faculty> getAll(Long orgId) {
        return this.faculties.values().stream().filter(faculty->faculty.getOrganisation().getOrgId().equals(orgId)).collect(Collectors.toSet());
    }
}
