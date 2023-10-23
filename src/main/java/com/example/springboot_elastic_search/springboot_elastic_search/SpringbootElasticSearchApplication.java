package com.example.springboot_elastic_search.springboot_elastic_search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class SpringbootElasticSearchApplication {

	@Autowired
	private IndexInititalization indexInititalization;

	public static void main(String[] args) {
		SpringApplication.run(SpringbootElasticSearchApplication.class, args);
	}
	//initialize index if not created already.
	// @PostConstruct
	// public void initializeIndex() {
	// 	indexInititalization.initializeIndex();
	// }
}
