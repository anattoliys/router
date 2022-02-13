package ru.service.router.models.response.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class RuleDto {
    private Long id;
    private String name;
    private String url;
    private Set<RuleParametersDto> parameters;
}
