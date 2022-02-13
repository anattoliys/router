package ru.service.router.jmx;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;
import ru.service.router.services.jmx.ParameterServiceMBean;

import javax.management.MBeanException;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.TabularData;

@Component
@RequiredArgsConstructor
@ManagedResource(description = "Parameter config")
public class ParameterMBean {
    private final ParameterServiceMBean parameterServiceMBean;

    @ManagedOperation(description = "Получить параметр по id")
    public CompositeData get(long id) throws OpenDataException, MBeanException {
        return parameterServiceMBean.get(id);
    }

    @ManagedOperation(description = "Добавить новый параметр")
    @ManagedOperationParameters({
            @ManagedOperationParameter(name = "имя", description = "Имя параметра"),
            @ManagedOperationParameter(name = "тип", description = "Тип параметра"),
            @ManagedOperationParameter(name = "описание", description = "Описание параметра"),
            @ManagedOperationParameter(name = "ранг", description = "Ранг параметра")
    })
    public String add(String name, String type, String description, Integer rank) throws JsonProcessingException {
        return parameterServiceMBean.add(name, type, description, rank);
    }

    @ManagedOperation(description = "Обновить параметр")
    @ManagedOperationParameters({
            @ManagedOperationParameter(name = "id", description = "Id параметра"),
            @ManagedOperationParameter(name = "имя", description = "Имя параметра"),
            @ManagedOperationParameter(name = "тип", description = "Тип параметра"),
            @ManagedOperationParameter(name = "описание", description = "Описание параметра"),
            @ManagedOperationParameter(name = "ранг", description = "Ранг параметра")
    })
    public String update(long id, String name, String type, String description, Integer rank) throws JsonProcessingException {
        return parameterServiceMBean.update(id, name, type, description, rank);
    }

    @ManagedOperation(description = "Удалить параметр")
    public String delete(long id) throws MBeanException {
        return parameterServiceMBean.delete(id);
    }

    @ManagedOperation(description = "Получить все параметры")
    public TabularData getAll() throws OpenDataException {
        return parameterServiceMBean.getAll();
    }
}
