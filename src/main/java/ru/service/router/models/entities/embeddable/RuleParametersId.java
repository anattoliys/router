package ru.service.router.models.entities.embeddable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class RuleParametersId implements Serializable {
    private Long id;
    private Long ruleId;
    private Long parameterId;
}
