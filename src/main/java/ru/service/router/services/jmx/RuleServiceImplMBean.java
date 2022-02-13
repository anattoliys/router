package ru.service.router.services.jmx;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.service.router.exception.ResourceNotFoundException;
import ru.service.router.models.entities.Rule;
import ru.service.router.models.entities.RuleParameters;
import ru.service.router.models.mapper.RuleMapper;
import ru.service.router.models.request.RuleRequest;
import ru.service.router.models.response.dto.RuleDto;
import ru.service.router.repositories.RuleRepository;
import ru.service.router.services.RuleService;
import ru.service.router.utils.Util;

import javax.management.MBeanException;
import javax.management.openmbean.*;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static org.hibernate.query.criteria.internal.predicate.ComparisonPredicate.ComparisonOperator.EQUAL;


@Service
@RequiredArgsConstructor
public class RuleServiceImplMBean implements RuleServiceMBean {
    private final RuleService ruleService;
    private final RuleRepository ruleRepository;
    private final RuleMapper ruleMapper;

    /**
     * Получить правило по id
     *
     * @param id - Идентификатор правила
     * @return - Правило
     * @throws MBeanException
     * @throws JsonProcessingException
     */
    @Transactional
    public String get(Long id) throws MBeanException, JsonProcessingException {
        RuleDto rule;

        try {
            rule = ruleService.get(id);
        } catch (ResourceNotFoundException e) {
            throw new MBeanException(new RuntimeException(), e.getMessage());
        }

        return Util.toJsonString(rule);
    }

    /**
     * Создать новое правило
     *
     * @param request - Поля правила
     * @return - Новое правило
     * @throws JsonProcessingException
     * @throws MBeanException
     */
    public String add(String request) throws JsonProcessingException, MBeanException {
        RuleRequest ruleRequest = Util.toObject(request, RuleRequest.class);
        RuleDto rule;

        try {
            rule = ruleService.add(ruleRequest);
        } catch (Exception e) {
            throw new MBeanException(new RuntimeException(), e.getMessage());
        }

        return Util.toJsonString(rule);
    }

    /**
     * Обновить правило
     *
     * @param id      - Идентификатор правила
     * @param request - Поля правила
     * @return - Измененное правило
     * @throws JsonProcessingException
     * @throws MBeanException
     */
    public String update(Long id, String request) throws JsonProcessingException, MBeanException {
        RuleRequest ruleRequest = Util.toObject(request, RuleRequest.class);
        RuleDto rule;

        try {
            rule = ruleService.update(id, ruleRequest);
        } catch (Exception e) {
            throw new MBeanException(new RuntimeException(), e.getMessage());
        }

        return Util.toJsonString(rule);
    }

    /**
     * Удалить правило
     *
     * @param id - Идентификатор правила
     * @return - Сообщение, об успешном удалении
     * @throws MBeanException
     */
    public String delete(Long id) throws MBeanException {
        String result;

        try {
            result = ruleService.delete(id);
        } catch (Exception e) {
            throw new MBeanException(new RuntimeException(), e.getMessage());
        }

        return result;
    }

    /**
     * Найти правила по входным параметрам
     *
     * @param params - Входные параметры
     * @return - Список равил
     * @throws OpenDataException
     * @throws UnsupportedEncodingException
     * @throws MBeanException
     */
    @Transactional
    public String find(String params) throws MBeanException {
        String rule;

        try {
            rule = ruleService.find(params);
        } catch (ResourceNotFoundException e) {
            throw new MBeanException(new RuntimeException(), e.getMessage());
        }

        return rule;
    }

    /**
     * Получить все правила
     *
     * @return - Список правил
     * @throws JsonProcessingException
     */
    @Transactional
    public TabularData getAll() throws JsonProcessingException, MBeanException, OpenDataException {
        List<Rule> rules = ruleRepository.findAll();
        Rule firstRule = rules.get(0);

        List<String> listNames = Arrays.stream(firstRule.getClass().getDeclaredFields()).filter(f -> !Objects.equals(f.getName(), "id") && !Objects.equals(f.getName(), "parameters")).map(Field::getName).collect(Collectors.toList());
        for (Rule item : rules) {
            List<RuleParameters> sortedParams = item.getParameters().stream().sorted(Comparator.comparing(p -> p.getParameter().getId())).collect(Collectors.toList());

            for (RuleParameters param : sortedParams) {
                String paramName = "parameter " + param.getParameter().getName();

                if (!listNames.contains(paramName)) {
                    listNames.add(paramName);
                }
            }
        }
        String[] fieldNames = listNames.toArray(String[]::new);

        List<SimpleType> listTypes = new ArrayList<>();
        for (int i = 0; i < fieldNames.length; i++) {
            listTypes.add(SimpleType.STRING);
        }
        OpenType[] fieldTypes = listTypes.toArray(OpenType[]::new);

        CompositeType compositeType = new CompositeType(
                "All rules",
                "All rules",
                fieldNames,
                fieldNames,
                fieldTypes
        );

        TabularType tableType = new TabularType("All rules", "All rules", compositeType, new String[]{fieldNames[0], fieldNames[1]});
        TabularData table = new TabularDataSupport(tableType);

        for (Rule item : rules) {
            List<String> listValues = new ArrayList<>(List.of(item.getName(), item.getUrl()));

            Map<Long, List<RuleParameters>> mapParameters = item.getParameters().stream().collect(Collectors.groupingBy(RuleParameters::getParameterId));

            mapParameters.forEach((key, value) -> {
                listValues.add(value.stream()
                        .map(r -> {
                            String equal = r.getComparisonType().equals(EQUAL) ? "=" : "<>";

                            return equal + r.getParameterValue();
                        }).collect(Collectors.joining(", "))
                );
            });

            List<String> parameterNames = Arrays.stream(fieldNames).filter(n -> n.contains("parameter")).collect(Collectors.toList());
            if (mapParameters.size() < parameterNames.size()) {
                for (int i = 0; i < parameterNames.size() - mapParameters.size(); i++) {
                    listValues.add("");
                }
            }
            Object[] fieldValues = listValues.toArray(Object[]::new);

            CompositeData data = new CompositeDataSupport(
                    compositeType,
                    fieldNames,
                    fieldValues
            );

            table.put(data);
        }

        return table;
    }
}
