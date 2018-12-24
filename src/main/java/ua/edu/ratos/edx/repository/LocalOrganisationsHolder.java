package ua.edu.ratos.edx.repository;

import org.springframework.stereotype.Component;
import ua.edu.ratos.edx.domain.Organisation;
import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class LocalOrganisationsHolder {

    private Map<Long, Organisation> organisations = new HashMap<>();

    @PostConstruct
    public void init() {
        Organisation o = new Organisation();
        o.setOrgId(1L);
        o.setName("IT University");
        this.organisations.put(1L, o);

        Organisation o1 = new Organisation();
        o1.setOrgId(2L);
        o1.setName("Medical University");
        this.organisations.put(2L, o1);
    }

    public Optional<Organisation> getById(Long orgId) {
        return Optional.ofNullable(this.organisations.get(orgId));
    }

    public Set<Organisation> getAll() {
        return this.organisations.values().stream().collect(Collectors.toSet());
    }

}
