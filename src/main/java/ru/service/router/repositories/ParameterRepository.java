package ru.service.router.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.service.router.models.entities.Parameter;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ParameterRepository extends JpaRepository<Parameter, Long> {
    Optional<Parameter> findByName(String name);

    List<Parameter> findAllByNameIn(Collection<String> names);

    boolean existsByName(String name);
}
