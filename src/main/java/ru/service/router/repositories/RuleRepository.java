package ru.service.router.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.service.router.models.entities.Rule;
import ru.service.router.repositories.custom.RuleRepositoryCustom;

@Repository
public interface RuleRepository extends JpaRepository<Rule, Long>, RuleRepositoryCustom {
}
