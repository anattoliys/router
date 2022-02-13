package ru.service.router.models.request;

import lombok.Getter;
import lombok.Setter;
import ru.service.router.models.entities.ParameterType;

@Getter
@Setter
public class ParameterRequest {
    private String name;
    private ParameterType type = ParameterType.STRING;
    private String description;
    private Integer rank;
}
