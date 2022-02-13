package ru.service.router.models.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.query.criteria.internal.predicate.ComparisonPredicate;
import ru.service.router.models.entities.embeddable.RuleParametersId;

import javax.persistence.*;

@Getter
@Setter
@IdClass(RuleParametersId.class)
@Entity
@Table(name = "rule_parameters")
public class RuleParameters {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rule_parameters_id_generator")
    @SequenceGenerator(name = "rule_parameters_id_generator", sequenceName = "rule_parameters_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Id
    @Column(name = "rule_id")
    private Long ruleId;

    @Id
    @Column(name = "parameter_id")
    private Long parameterId;

    @MapsId("ruleId")
    @ManyToOne
    @JoinColumn(name = "rule_id", insertable = false, updatable = false)
    private Rule rule;

    @MapsId("parameterId")
    @ManyToOne
    @JoinColumn(name = "parameter_id", insertable = false, updatable = false)
    private Parameter parameter;

    @Column(name = "parameter_value")
    private String parameterValue;

    @Column(name = "comparison_type")
    @Enumerated(EnumType.STRING)
    private ComparisonPredicate.ComparisonOperator comparisonType;
}
