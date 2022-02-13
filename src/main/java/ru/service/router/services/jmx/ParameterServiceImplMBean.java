package ru.service.router.services.jmx;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;
import ru.service.router.exception.ResourceNotFoundException;
import ru.service.router.models.entities.Parameter;
import ru.service.router.models.entities.ParameterType;
import ru.service.router.models.response.dto.ParameterDto;
import ru.service.router.repositories.ParameterRepository;
import ru.service.router.services.ParameterServiceImpl;
import ru.service.router.utils.Util;

import javax.management.MBeanException;
import javax.management.openmbean.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ParameterServiceImplMBean implements ParameterServiceMBean {
    private final ParameterServiceImpl parameterService;
    private final ParameterRepository parameterRepository;
    private final ResourceBundleMessageSource messageSource;

    /**
     * Получить параметр по id
     *
     * @param id - Идентификатор параметра
     * @return - Параметр
     * @throws OpenDataException
     * @throws MBeanException
     */
    public CompositeData get(Long id) throws OpenDataException, MBeanException {
        ParameterDto parameter;

        try {
            parameter = parameterService.get(id);
        } catch (ResourceNotFoundException e) {
            throw new MBeanException(new RuntimeException(), e.getMessage());
        }

        String[] fieldNames = Arrays.stream(parameter.getClass().getDeclaredFields()).map(Field::getName).toArray(String[]::new);
        OpenType[] fieldTypes = new OpenType[]{SimpleType.LONG, SimpleType.STRING, SimpleType.STRING, SimpleType.STRING, SimpleType.INTEGER};
        Object[] fieldValues = new Object[]{parameter.getId(), parameter.getName(), parameter.getType().toString(), parameter.getDescription(), parameter.getRank()};

        CompositeType compositeType = new CompositeType(
                "Parameter",
                "Parameter",
                fieldNames,
                fieldNames,
                fieldTypes
        );

        return new CompositeDataSupport(compositeType, fieldNames, fieldValues);
    }

    /**
     * Создать новый параметр
     *
     * @param name - Имя параметра
     * @param type - Тип параметра
     * @param description - Описание параметра
     * @param rank - Ранг параметра
     * @return - Новый параметр
     * @throws JsonProcessingException
     */
    public String add(String name, String type, String description, Integer rank) throws JsonProcessingException {
        return save(new Parameter(), name, type, description, rank);
    }

    /**
     * Обновить параметр
     *
     * @param id - Идентификатор параметра
     * @param name - Имя параметра
     * @param type - Тип параметра
     * @param description - Описание параметра
     * @param rank - Ранг параметра
     * @return - Измененный параметр
     * @throws JsonProcessingException
     */
    public String update(Long id, String name, String type, String description, Integer rank) throws JsonProcessingException {
        Parameter parameter = parameterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("C007", null, LocaleContextHolder.getLocale())));

        return save(parameter, name, type, description, rank);
    }

    /**
     * Удалить параметр
     *
     * @param id - Идентификатор параметра
     * @return - Сообщение, об успешном удалении
     * @throws MBeanException
     */
    public String delete(Long id) throws MBeanException {
        String result;

        try {
            result = parameterService.delete(id);
        } catch (Exception e) {
            throw new MBeanException(new RuntimeException(), e.getMessage());
        }

        return result;
    }

    /**
     * Получить все параметры
     *
     * @return - Список параметров
     * @throws OpenDataException
     */
    public TabularData getAll() throws OpenDataException {
        List<Parameter> parameters = parameterRepository.findAll();
        Parameter firstParameter = parameters.get(0);
        String[] fieldNames = Arrays.stream(firstParameter.getClass().getDeclaredFields()).map(Field::getName).toArray(String[]::new);
        OpenType[] fieldTypes = new OpenType[]{SimpleType.LONG, SimpleType.STRING, SimpleType.STRING, SimpleType.STRING, SimpleType.INTEGER};

        CompositeType compositeType = new CompositeType(
                "All parameters",
                "All parameters",
                fieldNames,
                fieldNames,
                fieldTypes
        );

        TabularType tableType = new TabularType("All parameters", "All parameters", compositeType, new String[]{fieldNames[0]});
        TabularData table = new TabularDataSupport(tableType);

        for (Parameter item : parameters) {
            CompositeData data = new CompositeDataSupport(
                    compositeType,
                    fieldNames,
                    new Object[]{item.getId(), item.getName(), item.getType().toString(), item.getDescription(), item.getRank()});

            table.put(data);
        }

        return table;
    }

    private String save(Parameter request, String name, String type, String description, Integer rank) throws JsonProcessingException {
        request.setName(name);
        request.setType(ParameterType.valueOf(type.toUpperCase(Locale.ROOT)));
        request.setDescription(description);
        request.setRank(rank);

        return Util.toJsonString(parameterRepository.save(request));
    }
}
