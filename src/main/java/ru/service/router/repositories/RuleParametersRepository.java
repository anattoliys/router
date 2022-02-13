package ru.service.router.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.service.router.models.entities.Parameter;
import ru.service.router.models.entities.RuleParameters;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface RuleParametersRepository extends JpaRepository<RuleParameters, Long> {
    boolean existsByParameterAndParameterValueIn(Parameter parameter, Collection<String> values);

    boolean existsByParameterId(Long id);

    List<RuleParameters> findAllByRuleId(Long ruleId);

    List<RuleParameters> findAllByRuleIdAndParameterId(Long ruleId, Long parameterId);

    Optional<RuleParameters> findByRuleIdNotAndParameterNameAndParameterValue(Long ruleId, String parameterName, String parameterValue);

    Optional<RuleParameters> findByRuleIdAndParameterIdAndParameterValue(Long ruleId, Long parameterId, String parameterValue);

    void deleteAllByRuleIdAndParameterId(Long ruleId, Long parameterId);

    void deleteAllByRuleIdAndParameterIdIn(Long ruleId, Collection<Long> parameterId);
}
