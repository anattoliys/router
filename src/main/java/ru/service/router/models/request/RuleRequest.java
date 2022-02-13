package ru.service.router.models.request;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class RuleRequest {
    private String name;
    private String url;
    private Set<RuleParametersRequest> parameters = new HashSet<>();
}
