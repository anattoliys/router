package ru.service.router.models.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "parameters")
public class Parameter {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "parameters_id_generator")
    @SequenceGenerator(name = "parameters_id_generator", sequenceName = "parameters_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private ParameterType type = ParameterType.STRING;

    @Column(name = "description")
    private String description;

    @Column(name = "rank")
    private Integer rank;
}
