package ru.service.router.models.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.service.router.models.entities.Rule;
import ru.service.router.models.response.dto.RuleDto;

@Mapper(componentModel = "spring", uses = RuleParametersMapper.class)
public interface RuleMapper {
    @Mapping(source = "rule.parameters", target = "parameters", qualifiedByName = "mapRuleParameters")
    RuleDto ruleToRuleDto(Rule rule);
}
