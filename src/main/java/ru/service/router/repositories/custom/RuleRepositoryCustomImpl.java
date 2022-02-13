package ru.service.router.repositories.custom;

import lombok.RequiredArgsConstructor;
import org.hibernate.query.criteria.internal.predicate.ComparisonPredicate;
import ru.service.router.config.RuleConstants;
import ru.service.router.models.entities.*;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class RuleRepositoryCustomImpl implements RuleRepositoryCustom {
    private final EntityManager em;

    /**
     * Получить все правила, у которых есть входные параметры
     */
    public List<Rule> filter(Map<String, String> params) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Rule> cq = cb.createQuery(Rule.class);
        Root<Rule> root = cq.from(Rule.class);
        cq.select(root);
        List<Predicate> predicates = new ArrayList<>();

        Subquery<Long> cqCount = cq.subquery(Long.class);
        Root<RuleParameters> rootCount = cqCount.from(RuleParameters.class);
        cqCount.select(cb.countDistinct(rootCount.join(RuleParameters_.PARAMETER).get(Parameter_.ID)));
        cqCount.where(cb.equal(rootCount.join(RuleParameters_.RULE).get(Rule_.ID), root.get(RuleParameters_.ID)));

        predicates.add(cb.equal(cqCount, params.keySet().size()));

        params.forEach((name, value) -> {
            Join<Rule, RuleParameters> joinParameters = root.join(Rule_.PARAMETERS);

            predicates.add(
                    cb.and(
                            cb.equal(joinParameters.get(RuleParameters_.PARAMETER).get(Parameter_.NAME), name),
                            cb.or(
                                    cb.and(
                                            cb.equal(joinParameters.get(RuleParameters_.PARAMETER_VALUE), value),
                                            cb.equal(joinParameters.get(RuleParameters_.COMPARISON_TYPE), ComparisonPredicate.ComparisonOperator.EQUAL)
                                    ),
                                    cb.equal(joinParameters.get(RuleParameters_.PARAMETER_VALUE), RuleConstants.ALL)
                            )
                    )
            );
        });

        cq.distinct(true);
        cq.where(predicates.toArray(new Predicate[0]));
        TypedQuery<Rule> query = em.createQuery(cq);

        return query.getResultList();
    }
}
