package ru.service.router.services;

import ru.service.router.models.request.RuleRequest;
import ru.service.router.models.response.dto.RuleDto;

import java.util.List;

public interface RuleService {
    RuleDto get(Long id);

    RuleDto add(RuleRequest request);

    RuleDto update(Long id, RuleRequest request);

    String delete(Long id);

    String find(String params);

    List<RuleDto> getAll();
}
