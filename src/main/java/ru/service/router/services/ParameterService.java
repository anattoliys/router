package ru.service.router.services;

import ru.service.router.models.request.ParameterRequest;
import ru.service.router.models.response.dto.ParameterDto;

import java.util.List;

public interface ParameterService {
    ParameterDto get(Long id);

    ParameterDto add(ParameterRequest request);

    ParameterDto update(Long id, ParameterRequest request);

    String delete(Long id);

    List<ParameterDto> getAll();
}
