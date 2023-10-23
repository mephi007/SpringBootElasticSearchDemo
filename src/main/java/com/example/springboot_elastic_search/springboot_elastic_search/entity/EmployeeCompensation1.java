package com.example.springboot_elastic_search.springboot_elastic_search.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "employee_compensation_1")
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeCompensation1 {
    @Id
    private long id;

    @Field(type = FieldType.Date, name = "timestamp")
    private Date timestamp;

    @Field(type = FieldType.Integer, name = "age")
    private int age;

    @Field(type = FieldType.Text, name = "current_working_industry")
    private String current_working_industry;

    @Field(type = FieldType.Keyword, name = "current_location")
    private String current_location;

    @Field(type = FieldType.Float, name = "total_years_of_experience")
    private float total_years_of_experience;

    @Field(type = FieldType.Text, name = "job_title")
    private String job_title;

    @Field(type = FieldType.Double, name = "annual_salary")
    private double annual_salary;

    @Field(type = FieldType.Keyword, name = "currency")
    private String currency;

    @Field(type = FieldType.Text, name = "current_company")
    private String current_company;

    @Field(type = FieldType.Text, name = "additional_job_responsibility")
    private String additional_job_responsibility;

    @Field(type = FieldType.Text, name = "additional_salary_info")
    private String additional_salary_info;
}
