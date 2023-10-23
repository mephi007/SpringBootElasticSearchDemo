package com.example.springboot_elastic_search.springboot_elastic_search.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.springboot_elastic_search.springboot_elastic_search.entity.EmployeeCompensation1;
import com.example.springboot_elastic_search.springboot_elastic_search.service.ElasticSearchService;

import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import graphql.GraphQL;
import jakarta.annotation.PostConstruct;
import graphql.ExecutionResult;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

@RestController
public class CompensationDataController {

    @Autowired
    private ElasticSearchService elasticSearchService;
    
    @Value("classpath:compensationData.graphqls")
    private Resource schemaResource;

    private GraphQL graphQl;

    //load grapgql schema
    @PostConstruct
    public void loadSchema() throws IOException {
        File schemaFile = schemaResource.getFile();
        TypeDefinitionRegistry registry = new SchemaParser().parse(schemaFile);
        RuntimeWiring wiring = buildWiring();
        GraphQLSchema schema = new SchemaGenerator().makeExecutableSchema(registry, wiring);
        graphQl = GraphQL.newGraphQL(schema).build();
    }

    //binding method with graphql query
    private RuntimeWiring buildWiring() {
        DataFetcher<EmployeeCompensation1> fetcher = data->{
            return this.getFindById(data.getArgument("id"));
        };
        return RuntimeWiring.newRuntimeWiring().type("Query", typeWriting->
            typeWriting.dataFetcher("getCompensationDataById", fetcher)).build();
    }

    //to get all the docs from index
    @RequestMapping(value="/compensation_data/getAllEmployee", method= RequestMethod.GET)
    public List<EmployeeCompensation1> matchAllEmployeeCompensation() throws IOException{
        return elasticSearchService.matchAllEmployeeCompensation1Service();
    }

    
    @RequestMapping(value="/compensation_data/filter", method= RequestMethod.GET)
    public List<EmployeeCompensation1> getCompensationData(@RequestParam Map<String,String> map) throws ElasticsearchException, IOException{
        return elasticSearchService.getCompensationData(map);
    }

    @RequestMapping(value="/compensation_data/{id}", method= RequestMethod.GET)
    public EmployeeCompensation1 getSingleCompensationData(@PathVariable String id ) throws ElasticsearchException, IOException{
        return elasticSearchService.matchEmployeeById(id);
        
    }

    @RequestMapping(value="/compensation_data/findById/{id}", method= RequestMethod.GET)
    public EmployeeCompensation1 getFindById(@PathVariable String id ) throws ElasticsearchException, IOException{
        return elasticSearchService.findById(id);
        
    }

    @RequestMapping(value="/compensation_data/findById", method= RequestMethod.POST)
    public ResponseEntity<Object> getCompensationDataFindById(@RequestBody String query) throws ElasticsearchException, IOException{
       ExecutionResult result = graphQl.execute(query);
       return new ResponseEntity<Object>(result, HttpStatus.OK);
    }

    @RequestMapping(value="/compensation_data/sparse", method= RequestMethod.GET)
    public List<EmployeeCompensation1> getCompensationDataToSpecificFieldOnly(@RequestParam Map<String,String> map) throws ElasticsearchException, IOException{
        return elasticSearchService.getCompensationDataToSpecificFieldsOnly(map);
    }

}
