package ru.service.router.models.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RuleParametersRequest {
    private String name;
    private List<RuleParameterValuesRequest> values;
}
