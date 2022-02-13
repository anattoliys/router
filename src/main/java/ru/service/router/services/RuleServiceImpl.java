package ru.service.router.services;

import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;
import ru.service.router.config.RuleConstants;
import ru.service.router.exception.ConflictException;
import ru.service.router.exception.BadRequestException;
import ru.service.router.exception.ResourceNotFoundException;
import ru.service.router.models.entities.Parameter;
import ru.service.router.models.entities.Rule;
import ru.service.router.models.entities.RuleParameters;
import ru.service.router.models.mapper.RuleMapper;
import ru.service.router.models.request.RuleParameterValuesRequest;
import ru.service.router.models.request.RuleParametersRequest;
import ru.service.router.models.request.RuleRequest;
import ru.service.router.models.response.dto.RuleDto;
import ru.service.router.repositories.ParameterRepository;
import ru.service.router.repositories.RuleParametersRepository;
import ru.service.router.repositories.RuleRepository;

import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hibernate.query.criteria.internal.predicate.ComparisonPredicate.ComparisonOperator.EQUAL;
import static org.hibernate.query.criteria.internal.predicate.ComparisonPredicate.ComparisonOperator.NOT_EQUAL;

/**
 * Сервис для работы с правилами
 */
@Service
@RequiredArgsConstructor
public class RuleServiceImpl implements RuleService {
    private final RuleRepository ruleRepository;
    private final ParameterRepository parameterRepository;
    private final RuleParametersRepository ruleParametersRepository;
    private final RuleMapper ruleMapper;
    private final ResourceBundleMessageSource messageSource;

    /**
     * Получить правило по id
     *
     * @param id - Идентификатор правила
     * @return - Правило
     */
    public RuleDto get(Long id) {
        return ruleRepository.findById(id)
                .map(ruleMapper::ruleToRuleDto)
                .orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("C002", null, LocaleContextHolder.getLocale())));
    }

    /**
     * Создать новое правило
     *
     * @param request - Поля правила
     * @return - Новое правило
     */
    public RuleDto add(RuleRequest request) {
        checkInputParametersUnique(request);

        Rule rule = new Rule();
        rule.setName(request.getName());
        rule.setUrl(request.getUrl());
        Set<RuleParameters> newRuleParameters = new HashSet<>();

        checkRuleDuplicates(0L, request);

        ruleRepository.save(rule);

        if (request.getParameters() != null) {
            for (RuleParametersRequest parameter : request.getParameters()) {
                Parameter param = parameterRepository.findByName(parameter.getName())
                        .orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("C007", new Object[]{parameter.getName()}, LocaleContextHolder.getLocale())));

                for (RuleParameterValuesRequest parameterValue : parameter.getValues()) {
                    RuleParameters ruleParameters = new RuleParameters();
                    ruleParameters.setRuleId(rule.getId());
                    ruleParameters.setParameterId(param.getId());
                    ruleParameters.setRule(rule);
                    ruleParameters.setParameter(param);
                    ruleParameters.setParameterValue(parameterValue.getValue());
                    ruleParameters.setComparisonType(parameterValue.getComparisonType());

                    newRuleParameters.add(ruleParameters);
                }
            }

            rule.setParameters(newRuleParameters);

            ruleRepository.save(rule);
        }

        return ruleMapper.ruleToRuleDto(rule);
    }

    /**
     * Обновить правило
     *
     * @param id      - Идентификатор правила
     * @param request - Поля правила
     * @return - Измененное правило
     */
    @Transactional
    public RuleDto update(Long id, RuleRequest request) {
        checkInputParametersUnique(request);

        Rule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("C002", null, LocaleContextHolder.getLocale())));
        rule.setName(request.getName());
        rule.setUrl(request.getUrl());
        Set<RuleParameters> newRuleParameters = new HashSet<>();

        List<RuleParameters> ruleParametersByRule = ruleParametersRepository.findAllByRuleId(rule.getId());

        List<String> inputParametersList = request.getParameters().stream()
                .map(RuleParametersRequest::getName)
                .collect(Collectors.toList());

        List<Long> idsToDelete = ruleParametersByRule.stream()
                .filter(rp -> !inputParametersList.contains(rp.getParameter().getName()))
                .map(RuleParameters::getParameterId)
                .collect(Collectors.toList());

        ruleParametersRepository.deleteAllByRuleIdAndParameterIdIn(rule.getId(), idsToDelete);

        checkRuleDuplicates(id, request);

        for (RuleParametersRequest parameter : request.getParameters()) {
            Parameter param = parameterRepository.findByName(parameter.getName())
                    .orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("C007", new Object[]{parameter.getName()}, LocaleContextHolder.getLocale())));

            List<RuleParameters> ruleParametersByRuleAndParameter = ruleParametersRepository
                    .findAllByRuleIdAndParameterId(rule.getId(), param.getId());

            List<String> inputValuesList = parameter.getValues().stream()
                    .map(RuleParameterValuesRequest::getValue)
                    .collect(Collectors.toList());

            for (RuleParameters dbParam : ruleParametersByRuleAndParameter) {
                if (!inputValuesList.contains(dbParam.getParameterValue())) {
                    ruleParametersRepository
                            .deleteAllByRuleIdAndParameterId(dbParam.getRule().getId(), dbParam.getParameter().getId());
                }
            }

            for (RuleParameterValuesRequest parameterValue : parameter.getValues()) {
                Optional<RuleParameters> existingParameter = ruleParametersRepository
                        .findByRuleIdAndParameterIdAndParameterValue(rule.getId(), param.getId(), parameterValue.getValue());

                if (existingParameter.isPresent()) {
                    RuleParameters p = existingParameter.get();

                    if (p.getComparisonType() != parameterValue.getComparisonType()) {
                        p.setComparisonType(parameterValue.getComparisonType());
                    }
                } else {
                    RuleParameters ruleParameters = new RuleParameters();
                    ruleParameters.setRuleId(rule.getId());
                    ruleParameters.setParameterId(param.getId());
                    ruleParameters.setRule(rule);
                    ruleParameters.setParameter(param);
                    ruleParameters.setParameterValue(parameterValue.getValue());
                    ruleParameters.setComparisonType(parameterValue.getComparisonType());

                    newRuleParameters.add(ruleParameters);
                }
            }
        }

        newRuleParameters.addAll(rule.getParameters());

        rule.setParameters(newRuleParameters);

        ruleRepository.save(rule);

        return ruleMapper.ruleToRuleDto(rule);
    }

    /**
     * Удалить правило
     *
     * @param id - Идентификатор правила
     * @return - Сообщение, об успешном удалении
     */
    public String delete(Long id) {
        ruleRepository.deleteById(id);

        return messageSource.getMessage("rules.delete", new Object[]{id}, LocaleContextHolder.getLocale());
    }

    /**
     * Найти правила по входным параметрам
     *
     * @param request - Строка с входными параметрами
     * @return - Список равил
     */
    public String find(String request) {
        Map<String, String> params = getParams(request);

        List<Parameter> paramsFromDb = parameterRepository.findAllByNameIn(params.keySet());

        checkParameterType(paramsFromDb, params);

        if (paramsFromDb.size() < params.entrySet().size()) {
            throw new ResourceNotFoundException(messageSource.getMessage("C002", null, LocaleContextHolder.getLocale()));
        }

        for (Parameter parameter : paramsFromDb) {
            boolean isExistsParameterValueInRule = ruleParametersRepository.existsByParameterAndParameterValueIn(parameter, List.of(params.get(parameter.getName()), RuleConstants.ALL));

            if (!isExistsParameterValueInRule) {
                throw new ResourceNotFoundException(messageSource.getMessage("C002", null, LocaleContextHolder.getLocale()));
            }
        }

        sortByRank(paramsFromDb, params);

        List<Rule> rules = ruleRepository.filter(params);

        Stream<Rule> ruleStream = rules.stream();

        for (Parameter parameter : paramsFromDb) {
            ruleStream = ruleStream.filter(r -> r.getParameters().stream().anyMatch(rp ->
                            rp.getParameter().getName().equals(parameter.getName())
                                    && (rp.getParameterValue().equals(RuleConstants.ALL)
                                    || rp.getComparisonType().equals(EQUAL) && rp.getParameterValue().equals(params.get(rp.getParameter().getName()))
                                    || rp.getComparisonType().equals(NOT_EQUAL) && !rp.getParameterValue().equals(params.get(rp.getParameter().getName())))
                    )
            );
        }

        List<Rule> ruleList = ruleStream.collect(Collectors.toList());

        if (ruleList.size() > 1) {
            for (Parameter parameter : paramsFromDb) {
                Stream<Rule> subRuleStream = ruleList.stream();

                if (!ruleList.stream()
                        .allMatch(r -> r.getParameters().stream()
                                .filter(p -> p.getParameter().getName().equals(parameter.getName()))
                                .findFirst()
                                .orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("C002", null, LocaleContextHolder.getLocale())))
                                .getParameterValue().equals(RuleConstants.ALL))
                ) {
                    if (ruleList.stream()
                            .anyMatch(r -> r.getParameters().stream()
                                    .filter(p -> p.getParameter().getName().equals(parameter.getName()))
                                    .findFirst()
                                    .orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("C002", null, LocaleContextHolder.getLocale())))
                                    .getComparisonType().equals(EQUAL))
                    ) {
                        subRuleStream = subRuleStream.filter(r -> r.getParameters().stream()
                                .anyMatch(rp -> rp.getParameter().getName().equals(parameter.getName())
                                        && rp.getComparisonType().equals(EQUAL)
                                        && !rp.getParameterValue().equals(RuleConstants.ALL)));
                    } else {
                        subRuleStream = subRuleStream.filter(r -> r.getParameters().stream()
                                .anyMatch(rp -> rp.getParameter().getName().equals(parameter.getName())
                                        && (rp.getParameterValue().equals(RuleConstants.ALL))));
                    }

                    ruleList = subRuleStream.collect(Collectors.toList());
                }
            }
        }

        return ruleList.stream()
                .findFirst()
                .map(r -> r.getUrl() + "?" + request)
                .orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("C002", null, LocaleContextHolder.getLocale())));
    }

    /**
     * Получить все правила
     *
     * @return Список правил
     */
    public List<RuleDto> getAll() {
        return ruleRepository.findAll().stream()
                .map(ruleMapper::ruleToRuleDto)
                .collect(Collectors.toList());
    }

    private Rule save(Rule rule, RuleRequest request) {
        rule.setName(request.getName());
        rule.setUrl(request.getUrl());

        return ruleRepository.save(rule);
    }

    private void checkInputParametersUnique(RuleRequest request) {
        Map<String, List<RuleParametersRequest>> paramNames = request.getParameters().stream()
                .collect(Collectors.groupingBy(RuleParametersRequest::getName));

        paramNames.forEach((name, value) -> {
            if (value.size() > 1) {
                throw new ConflictException(messageSource.getMessage("C005", new Object[]{name}, LocaleContextHolder.getLocale()));
            }
        });
    }

    private void checkRuleDuplicates(Long id, RuleRequest request) {
        List<RuleParameters> paramsFromDb = new ArrayList<>();

        for (RuleParametersRequest parameter : request.getParameters()) {
            for (RuleParameterValuesRequest parameterValue : parameter.getValues()) {
                ruleParametersRepository
                        .findByRuleIdNotAndParameterNameAndParameterValue(id, parameter.getName(), parameterValue.getValue())
                        .ifPresent(paramsFromDb::add);
            }
        }

        Map<String, List<RuleParameters>> mapParamsFromDb = paramsFromDb.stream()
                .collect(Collectors.groupingBy(p -> p.getParameter().getName()));

        if (request.getParameters().size() == mapParamsFromDb.size()) {
            throw new ConflictException(messageSource.getMessage("C006", new Object[]{request.getParameters().iterator().next().getName()}, LocaleContextHolder.getLocale()));
        }
    }

    private void sortByRank(List<Parameter> paramsFromDb, Map<String, String> params) {
        Map<String, Integer> paramsOrder = new HashMap<>();
        AtomicInteger index = new AtomicInteger(0);
        params.forEach((k, v) -> paramsOrder.put(k, index.getAndIncrement()));

        paramsFromDb.sort((p1, p2) -> {
            int result;

            if (p1.getRank() == null) {
                result = p2.getRank() == null ? paramsOrder.get(p1.getName()).compareTo(paramsOrder.get(p2.getName())) : -1;
            } else {
                result = p2.getRank() != null ? p1.getRank().compareTo(p2.getRank()) : -1;
            }

            return result;
        });
    }

    private void checkParameterType(List<Parameter> paramsFromDb, Map<String, String> params) {
        for (Parameter parameter : paramsFromDb) {
            String value = params.get(parameter.getName());

            try {
                switch (parameter.getType()) {
                    case INTEGER:
                        Integer.valueOf(value);
                        break;
                    case DOUBLE:
                        Double.valueOf(value);
                        break;
                    case BOOLEAN:
                        Boolean.valueOf(value);
                        break;
                }
            } catch (Exception e) {
                throw new BadRequestException(messageSource.getMessage("parameters.incorrect-type", new Object[]{parameter.getName()}, LocaleContextHolder.getLocale()));
            }
        }
    }

    // Гарантирует порядок входных параметров
    private Map<String, String> getParams(String queryString) {
        Map<String, String> result = new LinkedHashMap<>();
        String[] params = queryString.split("&");
        for (String s : params) {
            String[] keyAndValue = s.split("=");
            result.putIfAbsent(keyAndValue[0], keyAndValue[1]);
        }
        return result;
    }
}
