package ru.service.router.services;

import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;
import ru.service.router.exception.ConflictException;
import ru.service.router.exception.ResourceNotFoundException;
import ru.service.router.models.entities.Parameter;
import ru.service.router.models.mapper.ParameterMapper;
import ru.service.router.models.request.ParameterRequest;
import ru.service.router.models.response.dto.ParameterDto;
import ru.service.router.repositories.ParameterRepository;
import ru.service.router.repositories.RuleParametersRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для работы с параметрами
 */
@Service
@RequiredArgsConstructor
public class ParameterServiceImpl implements ParameterService {
    private final ParameterRepository parameterRepository;
    private final RuleParametersRepository ruleParametersRepository;
    private final ParameterMapper parameterMapper;
    private final ResourceBundleMessageSource messageSource;

    /**
     * Получить параметр по id
     *
     * @param id - Идентификатор параметра
     * @return - Параметр
     */
    public ParameterDto get(Long id) {
        return parameterRepository.findById(id)
                .map(parameterMapper::parameterToParameterDto)
                .orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("C007", new Object[]{id}, LocaleContextHolder.getLocale())));
    }

    /**
     * Создать новый параметр
     *
     * @param request - Поля параметра
     * @return - Новый параметр
     */
    public ParameterDto add(ParameterRequest request) {
        boolean isParameterExists = parameterRepository.existsByName(request.getName());

        if (isParameterExists) {
            throw new ConflictException(messageSource.getMessage("C003", new Object[]{request.getName()}, LocaleContextHolder.getLocale()));
        }

        return save(new Parameter(), request);
    }

    /**
     * Обновить параметр
     *
     * @param id      - Идентификатор параметра
     * @param request - Поля параметра
     * @return - Измененный параметр
     */
    public ParameterDto update(Long id, ParameterRequest request) {
        Parameter parameter = parameterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("C007", null, LocaleContextHolder.getLocale())));

        return save(parameter, request);
    }

    /**
     * Удалить параметр
     *
     * @param id - Идентификатор параметра
     * @return - Сообщение, об успешном удалении
     */
    public String delete(Long id) {
        boolean isRuleParameterExists = ruleParametersRepository.existsByParameterId(id);

        if (isRuleParameterExists) {
            throw new ConflictException(messageSource.getMessage("C004", new Object[]{id}, LocaleContextHolder.getLocale()));
        }

        parameterRepository.deleteById(id);

        return messageSource.getMessage("parameters.delete", new Object[]{id}, LocaleContextHolder.getLocale());
    }

    /**
     * Получить все параметры
     *
     * @return - Список параметров
     */
    public List<ParameterDto> getAll() {
        return parameterRepository.findAll().stream()
                .map(parameterMapper::parameterToParameterDto)
                .collect(Collectors.toList());
    }

    private ParameterDto save(Parameter parameter, ParameterRequest request) {
        parameter.setName(request.getName());
        parameter.setType(request.getType());
        parameter.setDescription(request.getDescription());
        parameter.setRank(request.getRank());

        return parameterMapper.parameterToParameterDto(parameterRepository.save(parameter));
    }
}
