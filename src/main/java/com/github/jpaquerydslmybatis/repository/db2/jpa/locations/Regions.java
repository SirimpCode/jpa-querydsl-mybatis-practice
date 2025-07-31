package com.github.jpaquerydslmybatis.repository.db2.jpa.locations;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Regions {
    @Id
    private Long regionId;
    private String regionName;
}
