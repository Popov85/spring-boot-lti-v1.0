package ua.edu.ratos.edx.converter;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ua.edu.ratos.edx.domain.Class;
import ua.edu.ratos.edx.repository.LocalClassesHolder;

@Component
public class ClassConverter implements Converter<String, Class> {

    @Autowired
    private LocalClassesHolder localClassesHolder;

    @Override
    public Class convert(String classId) {
        return this.localClassesHolder.getById(Long.parseLong(classId)).get();
    }
}
