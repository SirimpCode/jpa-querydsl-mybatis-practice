package com.github.jpaquerydslmybatis.repository.db2.jpa.locations;

import com.github.jpaquerydslmybatis.repository.db2.jpa.department.Departments;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

@Entity
@Getter
public class Locations {
    @Id
    private Long locationId;
    private String streetAddress;
    private String postalCode;
    private String city;
    private String stateProvince;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Countries countries;

    @OneToMany(mappedBy = "locations")
    private List<Departments> departmentsList;

}
