package com.example.springboot_elastic_search.springboot_elastic_search.service;

import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.springboot_elastic_search.springboot_elastic_search.entity.EmployeeCompensation1;
import com.example.springboot_elastic_search.springboot_elastic_search.repository.EmployeeCompensation1Repository;
import com.example.springboot_elastic_search.springboot_elastic_search.util.ElasticSearchUtil;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.SourceConfig;
import static com.example.springboot_elastic_search.springboot_elastic_search.constants.ConstantInterface.*;

@Service
public class ElasticSearchService {
    
    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @Autowired
    private EmployeeCompensation1Repository eCompensation1Repository;

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchService.class);

    /*
     * service method that would get elastic query from ElasticSearchUtil interface
     * and return the Employees as list to CompensationDataController class.
     */
    public List<EmployeeCompensation1> matchAllEmployeeCompensation1Service() throws ElasticsearchException, IOException{
        Supplier<Query> supplier = ElasticSearchUtil.supplier();
        SearchResponse<EmployeeCompensation1> searchResponse = elasticsearchClient.search(s -> s.index(EMPLOYEE_COMPENSATION_1).query(supplier.get()), EmployeeCompensation1.class);
        logger.info(String.format("searched elasticsearch query is --> %s", supplier.get().toString()));
        return getListOfEmployees(searchResponse);
    }

    /*
     * service method that would get elastic query from ElasticSearchUtil interface based on query params passed in map.
     * and return the Employees as list to CompensationDataController class.
     */
    public List<EmployeeCompensation1> getCompensationData(Map<String, String> map) throws ElasticsearchException, IOException {
        Supplier<Query> supplier = ElasticSearchUtil.supplierBoolQuery(map);
        List<SortOptions> sortList =  ElasticSearchUtil.getSortingList(map);
        SearchResponse<EmployeeCompensation1> searchResponse = elasticsearchClient.search(s -> s.index(EMPLOYEE_COMPENSATION_1).query(supplier.get()).sort(sortList), EmployeeCompensation1.class);
        logger.info(String.format("searched elasticsearch query is --> %s", supplier.get().toString()));
        return getListOfEmployees(searchResponse);
    }

    /*
     * service method that would get elastic query from ElasticSearchUtil interface based on id
     * and return the Employee to CompensationDataController class.
     */
    public EmployeeCompensation1 matchEmployeeById(String id) throws ElasticsearchException, IOException {
        Supplier<Query> supplier = ElasticSearchUtil.supplierWithId(id);
        SearchResponse<EmployeeCompensation1> searchResponse = elasticsearchClient.search(s -> s.index(EMPLOYEE_COMPENSATION_1).query(supplier.get()), EmployeeCompensation1.class);
        logger.info(String.format("searched elasticsearch query is --> %s", supplier.get().toString()));
        List<EmployeeCompensation1> listOfEmps = getListOfEmployees(searchResponse);
        return listOfEmps.size() == 0 ? new EmployeeCompensation1() : listOfEmps.get(0);
    }

    /*
     * service method that would get elastic query from EmployeeCompensation1Repository based on id
     * and return the Employee to CompensationDataController class.
     */
    public EmployeeCompensation1 findById(String id) {
        Optional<EmployeeCompensation1> op = null;
        try {
            op = eCompensation1Repository.findById(Long.parseLong(id)); 
        } catch (Exception e) {
            logger.error(String.format("failed to get compensation data on id --> %s", e.getLocalizedMessage()));
        }
        return (op != null && op.isPresent()) ? op.get() : new EmployeeCompensation1();
    }


    public List<EmployeeCompensation1> getCompensationDataToSpecificFieldsOnly(Map<String, String> map) throws ElasticsearchException, IOException {
        Supplier<Query> supplier = ElasticSearchUtil.supplier();
        final int size = Integer.parseInt(map.getOrDefault(SIZE, ONE));
        final List<String> fields = map.containsKey(FIELDS) ? List.of(map.get(FIELDS).split(",")) : new ArrayList<>();
        SourceConfig sparseFields = SourceConfig.of(sc -> sc.filter(f -> f.includes(fields)));
        SearchResponse<EmployeeCompensation1> searchResponse = elasticsearchClient.search(s -> s.index(EMPLOYEE_COMPENSATION_1).source(sparseFields).size(size).query(supplier.get()), EmployeeCompensation1.class);
        logger.info(String.format("searched elasticsearch query is --> %s", supplier.get().toString()));
        return getListOfEmployees(searchResponse);
    }

    private List<EmployeeCompensation1> getListOfEmployees(SearchResponse<EmployeeCompensation1> searchResponse) {
        List<Hit<EmployeeCompensation1>>  listOfHits= searchResponse.hits().hits();
        List<EmployeeCompensation1> listOfEmps = new ArrayList<>();
        for(Hit<EmployeeCompensation1> hit : listOfHits){
            listOfEmps.add(hit.source());
        }
        return listOfEmps;
    }
}
