package ru.service.router.models.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.service.router.models.entities.RuleParameters;
import ru.service.router.models.response.dto.RuleParameterValuesDto;

@Mapper(componentModel = "spring")
public interface RuleParameterValuesMapper {
    @Mapping(source = "parameterValue", target = "value")
    RuleParameterValuesDto toDto(RuleParameters source);
}
