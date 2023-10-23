package com.example.springboot_elastic_search.springboot_elastic_search.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import graphql.GraphQL;
import jakarta.annotation.PostConstruct;
import graphql.ExecutionResult;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.example.springboot_elastic_search.springboot_elastic_search.constants.ConstantInterface.*;

@RestController
public class CompensationDataController {

    @Autowired
    private ElasticSearchService elasticSearchService;
    
    @Value("classpath:compensationData.graphqls")
    private Resource schemaResource;

    private GraphQL graphQl;

    private static final Logger logger = LoggerFactory.getLogger(CompensationDataController.class);


    //load grapgql schema
    @PostConstruct
    public void loadSchema() throws IOException {
        logger.info(String.format("loading graphql schema"));
        File schemaFile = schemaResource.getFile();
        TypeDefinitionRegistry registry = new SchemaParser().parse(schemaFile);
        RuntimeWiring wiring = buildWiring();
        GraphQLSchema schema = new SchemaGenerator().makeExecutableSchema(registry, wiring);
        graphQl = GraphQL.newGraphQL(schema).build();
    }

    //binding method with graphql query
    private RuntimeWiring buildWiring() {
        DataFetcher<EmployeeCompensation1> fetcher = data->{
            return this.getFindById(data.getArgument(ID));
        };
        return RuntimeWiring.newRuntimeWiring().type(QUERY, typeWriting->
            typeWriting.dataFetcher("getCompensationDataById", fetcher)).build();
    }

    /*
        to get all the docs from index
        uses es util search query
    */
    @RequestMapping(value="/compensation_data/getAllEmployee", method= RequestMethod.GET)
    public List<EmployeeCompensation1> matchAllEmployeeCompensation() {
        List<EmployeeCompensation1> result = new ArrayList<>();
        try{
            logger.info(String.format("getting all the compensation data"));
            result = elasticSearchService.matchAllEmployeeCompensation1Service();
        }catch(Exception e){
            logger.error(String.format("request failed"));
        }
        return result;
    }

    /*
     * to get filtered compensation data
     * eq: compensation_data/filter?annual_salary.gte=12000&current_location=NY&annual_salary.lt=100000&total_years_of_experience.gte=1&timestamp=asc
     *      compensation_data/filter?timestamp=desc
     * uses es util search boolquery
     */
    @RequestMapping(value="/compensation_data/filter", method= RequestMethod.GET)
    public List<EmployeeCompensation1> getCompensationData(@RequestParam Map<String,String> map) throws ElasticsearchException, IOException{
        List<EmployeeCompensation1> result = new ArrayList<>();
        try {
            logger.info(String.format("getting compensation data on filter"));
            result =  elasticSearchService.getCompensationData(map);
        } catch (Exception e) {
            logger.error(String.format("request failed"));
        }
        return result;
    }

    /*
     * to get compensation data on id
     * eq: compensation_data/1
     * uses es util search query
     *     
     */
    @RequestMapping(value="/compensation_data/{id}", method= RequestMethod.GET)
    public EmployeeCompensation1 getSingleCompensationData(@PathVariable String id ) throws ElasticsearchException, IOException{
        EmployeeCompensation1 emp = null;
        try {
            logger.info(String.format("getting compensation data on id"));
            emp = elasticSearchService.matchEmployeeById(id);
        } catch (Exception e) {
            logger.error(String.format("request failed"));
        }
        return emp;
    }

    /*
     * to get filtered compensation on id
     * eq: compensation_data/findById/1
     *      uses repository
     */
    @RequestMapping(value="/compensation_data/findById/{id}", method= RequestMethod.GET)
    public EmployeeCompensation1 getFindById(@PathVariable String id ) {
        EmployeeCompensation1 emp = null;
        try {
            logger.info(String.format("getting compensation data on id"));
            emp =  elasticSearchService.findById(id);
        } catch (Exception e) {
            logger.error(String.format("request failed --> %s", e.getLocalizedMessage()));
        }
        return emp;
    }

    /*
     * to get particular fields only compensation data
     * uses graphql
     * eq: Post Method
     * {
     *       getCompensationDataById(id: "2"){
     *           id,
     *           timestamp,
     *           age,
     *           annual_salary
     *       }
     *   }
     */ 
    @RequestMapping(value="/compensation_data/findById", method= RequestMethod.POST)
    public ResponseEntity<Object> getCompensationDataFindById(@RequestBody String query){
        ExecutionResult result = null;
        try {
            logger.info(String.format("getting compensation field only data on id using graphql"));
        result = graphQl.execute(query);
        } catch (Exception e) {
            logger.error(String.format("request failed --> %s", e.getLocalizedMessage()));
        }
       return new ResponseEntity<Object>(result, HttpStatus.OK);
    }

    /*
     * to get particular fields only compensation data
     * uses es util search query
     * Get Method
     * eg: http://localhost:8081/compensation_data/sparse?size=100
     *     http://localhost:8081/compensation_data/sparse?fields=id,age,current_working_industry&size=10
     *     http://localhost:8081/compensation_data/sparse?fields=id,age,current_working_industry
     */ 
    @RequestMapping(value="/compensation_data/sparse", method= RequestMethod.GET)
    public List<EmployeeCompensation1> getCompensationDataToSpecificFieldOnly(@RequestParam Map<String,String> map){
        List<EmployeeCompensation1> emp = null;
        try {
            logger.info(String.format("getting compensation field only data on id using es utils search and source"));
            emp = elasticSearchService.getCompensationDataToSpecificFieldsOnly(map);
        } catch (Exception e) {
            logger.error(String.format("request failed --> %s", e.getLocalizedMessage()));
        }
        return emp;
    }
}
