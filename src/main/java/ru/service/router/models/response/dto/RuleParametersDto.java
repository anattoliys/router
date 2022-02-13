package ru.service.router.models.response.dto;

import lombok.Getter;
import lombok.Setter;
import ru.service.router.models.entities.ParameterType;

import java.util.List;

@Getter
@Setter
public class RuleParametersDto {
    private Long id;
    private String name;
    private ParameterType type;
    private String description;
    private Integer rank;
    private List<RuleParameterValuesDto> values;
}
