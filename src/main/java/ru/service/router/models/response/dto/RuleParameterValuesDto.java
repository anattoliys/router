package ru.service.router.models.response.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.query.criteria.internal.predicate.ComparisonPredicate;

@Getter
@Setter
public class RuleParameterValuesDto {
    private String value;
    private ComparisonPredicate.ComparisonOperator comparisonType;
}
