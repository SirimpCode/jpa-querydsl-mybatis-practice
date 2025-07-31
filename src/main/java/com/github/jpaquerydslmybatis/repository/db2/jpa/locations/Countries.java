package com.github.jpaquerydslmybatis.repository.db2.jpa.locations;

import com.github.jpaquerydslmybatis.repository.db2.jpa.department.Departments;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

@Entity
@Getter
public class Countries {
    @Id
    private String countryId;
    private String countryName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="region_id")
    private Regions regions;

    @OneToMany(mappedBy = "countries")
    private List<Locations> locations;

}
