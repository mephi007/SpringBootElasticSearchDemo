package com.example.springboot_elastic_search.springboot_elastic_search.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.example.springboot_elastic_search.springboot_elastic_search.entity.EmployeeCompensation1;

@Repository
public interface EmployeeCompensation1Repository extends ElasticsearchRepository<EmployeeCompensation1, Long> {
    
}
