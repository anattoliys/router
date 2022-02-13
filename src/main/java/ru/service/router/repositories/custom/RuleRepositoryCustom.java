package ru.service.router.repositories.custom;

import ru.service.router.models.entities.Rule;

import java.util.List;
import java.util.Map;

public interface RuleRepositoryCustom {
    List<Rule> filter(Map<String, String> params);
}
