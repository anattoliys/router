package ru.service.router.models.request;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.query.criteria.internal.predicate.ComparisonPredicate;

@Getter
@Setter
public class RuleParameterValuesRequest {
    private String value;
    private ComparisonPredicate.ComparisonOperator comparisonType;
}
