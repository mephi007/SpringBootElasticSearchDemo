package com.example.springboot_elastic_search.springboot_elastic_search.constants;

import java.text.SimpleDateFormat;

import org.springframework.stereotype.Component;

@Component
public final class ConstantInterface {

    public ConstantInterface(){}

    public static final String QUERY = "Query";

    public static final String EMPLOYEE_COMPENSATION_1 = "employee_compensation_1";

    public static final String ADDITIONAL_SALARY_INFO = "additional_salary_info";

    public static final String ADDITIONAL_JOB_RESPONSIBILITY = "additional_job_responsibility";

    public static final String CURRENT_COMPANY = "current_company";

    public static final String CURRENCY = "currency";

    public static final String ANNUAL_SALARY = "annual_salary";

    public static final String JOB_TITLE = "job_title";

    public static final String TOTAL_YEARS_OF_EXPERIENCE = "total_years_of_experience";

    public static final String CURRENT_LOCATION = "current_location";

    public static final String CURRENT_WORKING_INDUSTRY = "current_working_industry";

    public static final String AGE = "age";

    public static final String TIMESTAMP = "timestamp";

    public static final String FIELDS = "fields";

    public static final String ONE = "1";

    public static final String SIZE = "size";

    public static final String ID = "id";

    public static final String LT = "lt";

    public static final String LTE = "lte";

    public static final String GT = "gt";

    public static final String GTE = "gte";

    public static final String DOT = ".";

    public static final String DESC = "desc";
    
    public static final String ASC = "asc";

    public static final String SALARY_SURVEY_1_CSV = "salary_survey-1.csv";

    public static final SimpleDateFormat formatter1=new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"); 
}
