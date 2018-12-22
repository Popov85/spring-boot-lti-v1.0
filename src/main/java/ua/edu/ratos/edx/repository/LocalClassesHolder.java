package ua.edu.ratos.edx.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.edu.ratos.edx.domain.Class;
import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class LocalClassesHolder {

    @Autowired
    private LocalFacultiesHolder localFacultiesHolder;

    private Map<Long, Class> classes = new HashMap<>();

    @PostConstruct
    public void init() {
        Class c1 = new Class();
        c1.setClassId(1L);
        c1.setName("1st class");
        c1.setFaculty(localFacultiesHolder.getById(1L).get());
        this.classes.put(1L, c1);

        Class c2 = new Class();
        c2.setClassId(2L);
        c2.setName("2d class");
        c2.setFaculty(localFacultiesHolder.getById(1L).get());
        this.classes.put(2L, c2);

        Class c3 = new Class();
        c3.setClassId(3L);
        c3.setName("3d class");
        c3.setFaculty(localFacultiesHolder.getById(1L).get());
        this.classes.put(3L, c3);
    }

    public Optional<Class> getById(Long orgId) {
        return Optional.ofNullable(this.classes.get(orgId));
    }

    public Set<Class> getAll() {
        return this.classes.values().stream().collect(Collectors.toSet());
    }

    public Set<Class> getAll(Long facId) {
        return this.classes.values().stream().filter(clazz->clazz.getFaculty().getFacId().equals(facId)).collect(Collectors.toSet());
    }
}
