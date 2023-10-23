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
import static com.example.springboot_elastic_search.springboot_elastic_search.constants.ConstantInterface.*;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = EMPLOYEE_COMPENSATION_1)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeCompensation1 {
    
    @Id
    private long id;

    @Field(type = FieldType.Date, name = TIMESTAMP)
    private Date timestamp;

    @Field(type = FieldType.Integer, name = AGE)
    private int age;

    @Field(type = FieldType.Text, name = CURRENT_WORKING_INDUSTRY)
    private String current_working_industry;

    @Field(type = FieldType.Keyword, name = CURRENT_LOCATION)
    private String current_location;

    @Field(type = FieldType.Float, name = TOTAL_YEARS_OF_EXPERIENCE)
    private float total_years_of_experience;

    @Field(type = FieldType.Text, name = JOB_TITLE)
    private String job_title;

    @Field(type = FieldType.Double, name = ANNUAL_SALARY)
    private double annual_salary;

    @Field(type = FieldType.Keyword, name = CURRENCY)
    private String currency;

    @Field(type = FieldType.Text, name = CURRENT_COMPANY)
    private String current_company;

    @Field(type = FieldType.Text, name = ADDITIONAL_JOB_RESPONSIBILITY)
    private String additional_job_responsibility;

    @Field(type = FieldType.Text, name = ADDITIONAL_SALARY_INFO)
    private String additional_salary_info;
}
