package ru.service.router.models.response.dto;

import lombok.Getter;
import lombok.Setter;
import ru.service.router.models.entities.ParameterType;

@Getter
@Setter
public class ParameterDto {
    private Long id;
    private String name;
    private ParameterType type = ParameterType.STRING;
    private String description;
    private Integer rank;
}
