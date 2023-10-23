package com.example.springboot_elastic_search.springboot_elastic_search;


import java.beans.JavaBean;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Component;

import com.example.springboot_elastic_search.springboot_elastic_search.entity.EmployeeCompensation1;
import com.example.springboot_elastic_search.springboot_elastic_search.repository.EmployeeCompensation1Repository;

@Component
public class IndexInititalization {
    
    public static final SimpleDateFormat formatter1=new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"); 

	@Autowired
	private EmployeeCompensation1Repository compensationRepository;

	@Autowired
	private ElasticsearchOperations esOps;

    //iniatizing steps of index to work with later using API calls
    public void initializeIndex(){
        esOps.indexOps(EmployeeCompensation1.class).refresh();
		compensationRepository.deleteAll();
		compensationRepository.saveAll(parseCSV());
    }

    //parsing csv file 'salary_survey-1.csv'
    private Collection<EmployeeCompensation1> parseCSV(){
		Resource resource = new ClassPathResource("salary_survey-1.csv");
		// Resource resource = new ClassPathResource("salary_test.csv");
		List<EmployeeCompensation1> emps = new ArrayList<>();
		try(
			BufferedReader fileReader = new BufferedReader(new InputStreamReader(resource.getInputStream(), "UTF-8"));
			CSVParser csvParser = new CSVParser(fileReader,
            CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
			){
				Iterable<CSVRecord> records = csvParser.getRecords();
				for(CSVRecord record : records){
					Optional<EmployeeCompensation1> emp = null;
					try{
						emp = recordPreparation(record);
					}catch(Exception e){
						System.out.println(" not correct formatting --> " + e);
					}
					if(emp != null && emp.isPresent()){
						// System.out.println("adding record to list -> " + emp);
						emps.add(emp.get());
					}
				}
			
		}catch(Exception e){
			System.out.println("exception -> " + e);
		}
		System.out.println("records inserted --> " + emps.size());
		return emps;

	}
    /* Processing to make it easy and feasable to query later
     * age : taking avg of age mentioned in the file
     *        xx-xx
     *        xx or more
     * annual_salary: taking avg of compensations mentioned in the file depending on different
     *                discoved patterns.
     *                 $xxxx
     *                 xx-xxk
     *                 xxxx abcd xyz
     * total_years_of_experience: taking avg of mentioned years of experience
     */
    //mapping csv header with index fields and processing them as required
	private Optional<EmployeeCompensation1> recordPreparation(CSVRecord record) throws ParseException{
			long id = record.getRecordNumber();
			Date timeStamp = formatter1.parse(record.get("Timestamp"));
			Integer age = processAge(record.get("How old are you?"));
			String currentWorkingIndustry = record.get("What industry do you work in?");
			String jobTitle = record.get("Job title");
			Double annualSalary = processAnnualSalary(record.get("What is your annual salary?"));
			String currency = record.get("Please indicate the currency");
			String currentLocation = record.get("Where are you located? (City/state/country)");
			Float totalExp = processAnnualExperience(record.get("How many years of post-college professional work experience do you have?"));
			String additionalJobResponsibility = record.get("If your job title needs additional context, please clarify here:");
			String additionalSalaryInfo = record.get("If \"Other,\" please indicate the currency here:");
			return Optional.of(
				EmployeeCompensation1.builder().
				id(id).
				timestamp(timeStamp).
				age(age).
				current_working_industry(currentWorkingIndustry).
				job_title(jobTitle).
				annual_salary(annualSalary).
				currency(currency).
				current_location(currentLocation).
				total_years_of_experience(totalExp).
				additional_job_responsibility(additionalJobResponsibility).
				additional_salary_info(additionalSalaryInfo).
				build());
	}

    //process annual_salary for easy query reterival
	private Double processAnnualSalary(String next) {
		next = next.toLowerCase();
		String[] salaryDist = next.split(" ");
		double compensation = 0.0;
		for(String salary: salaryDist){
			boolean kFlag = false;
			Double addition = 0.0;
			salary = salary.replaceAll("[^\\d.k-]", "");
			if(salary.equals("") || salary.length() == 0) continue;
			if(salary.matches("[0-9]+")){
				compensation += Double.parseDouble(salary);
			}else{
				
				String[] curr = salary.replaceAll("[^\\d.k-]", "").split("-");
				if(curr.length == 2 ){
					if(curr[0].contains("k") ||  curr[1].contains("k") )
						{
							kFlag =true;
						}
					curr[0] = curr[0].replaceAll("[^\\d.]", "");
					curr[1] = curr[1].replaceAll("[^\\d.]", "");
					addition += (Double.parseDouble(curr[0])+Double.parseDouble(curr[1]))/2;
					compensation = kFlag ? compensation+(addition*1000) : compensation+addition;
					
				}else if(curr.length == 1){
					if(curr[0].contains("k") )
						kFlag =true;
					curr[0] = curr[0].replaceAll("[^\\d.]", "");
					addition += Double.parseDouble(curr[0]);
					compensation = kFlag ? compensation+(addition*1000) : compensation+addition;
				}
			}
		}
		return compensation;
	}

    //process total_years_of_experience for easy query reterival
	private Float processAnnualExperience(String next){
		String[] years = next.replaceAll("[^\\d-]", "").split("-");
		if(years.length == 0) return null;
		return years.length == 1 ? Float.parseFloat(years[0]) : (Float.parseFloat(years[0])+Float.parseFloat(years[1]))/2;
	}

    //process age for easy query reterival
	private Integer processAge(String next) {
		String[] age = next.replaceAll("[^\\d-]", "").split("-");
		if(age.length == 0) return null;
		return age.length == 1? Integer.parseInt(age[0]): (Integer.parseInt(age[0])+Integer.parseInt(age[1]))/2;
	}
}
