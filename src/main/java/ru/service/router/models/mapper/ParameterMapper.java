package ru.service.router.models.mapper;

import org.mapstruct.Mapper;
import ru.service.router.models.entities.Parameter;
import ru.service.router.models.response.dto.ParameterDto;

@Mapper(componentModel = "spring")
public interface ParameterMapper {
    ParameterDto parameterToParameterDto(Parameter parameter);
}
