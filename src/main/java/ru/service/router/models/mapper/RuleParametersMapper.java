package ru.service.router.models.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.service.router.models.entities.Parameter;
import ru.service.router.models.entities.RuleParameters;
import ru.service.router.models.response.dto.RuleParametersDto;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {
        RuleMapper.class,
        RuleParameterValuesMapper.class
})
public interface RuleParametersMapper {
    @Mapping(source = "ruleParameters", target = "values")
    RuleParametersDto ruleParameterToRuleParameterDto(Parameter parameter, List<RuleParameters> ruleParameters);

    @Named("mapRuleParameters")
    default Set<RuleParametersDto> map(Set<RuleParameters> ruleParameters) {
        Map<Parameter, List<RuleParameters>> mapParameters =
                ruleParameters.stream().collect(Collectors.groupingBy(RuleParameters::getParameter));

        Set<RuleParametersDto> ruleParametersDto = new HashSet<>();

        for (Map.Entry<Parameter, List<RuleParameters>> entry : mapParameters.entrySet()) {
            ruleParametersDto.add(ruleParameterToRuleParameterDto(entry.getKey(), entry.getValue()));
        }

        return ruleParametersDto;
    }
}
