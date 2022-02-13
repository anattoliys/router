package ru.service.router.jmx;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;
import ru.service.router.services.jmx.RuleServiceMBean;

import javax.management.MBeanException;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.TabularData;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Component
@RequiredArgsConstructor
@ManagedResource(description = "Rule config")
public class RuleMBean {
    private final RuleServiceMBean ruleServiceMBean;

    @ManagedOperation(description = "Получить правило по id")
    public String get(long id) throws MBeanException, JsonProcessingException {
        return ruleServiceMBean.get(id);
    }

    @ManagedOperation(description = "Добавить новое правило")
    @ManagedOperationParameter(name = "json", description = "json")
    public String add(String request) throws JsonProcessingException, MBeanException {
        return ruleServiceMBean.add(request);
    }

    @ManagedOperation(description = "Обновить правило")
    @ManagedOperationParameters({
            @ManagedOperationParameter(name = "id", description = "Id правила"),
            @ManagedOperationParameter(name = "json", description = "json")
    })
    public String update(long id, String request) throws MBeanException, JsonProcessingException {
        return ruleServiceMBean.update(id, request);
    }

    @ManagedOperation(description = "Удалить правило")
    public String delete(long id) throws MBeanException {
        return ruleServiceMBean.delete(id);
    }

    @ManagedOperation(description = "Найти правило")
    public String find(String params) throws OpenDataException, UnsupportedEncodingException, MBeanException {
        return ruleServiceMBean.find(params);
    }

    @ManagedOperation(description = "Получить все правила")
    public TabularData getAll() throws JsonProcessingException, MBeanException, OpenDataException {
        return ruleServiceMBean.getAll();
    }
}
