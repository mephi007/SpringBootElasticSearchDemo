package com.example.springboot_elastic_search.springboot_elastic_search.util;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.util.function.Supplier;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.json.JsonData;
import lombok.val;

public class ElasticSearchUtil {
    
    //supply back match all query to get all the employees
    public static Supplier<Query> supplier(){
        Supplier<Query> supplier = () -> Query.of(q -> q.matchAll(matchAllQuery()));
        return supplier;
    }

    //preparing match all query
    public static MatchAllQuery matchAllQuery(){
        MatchAllQuery.Builder matchAllQuery = new MatchAllQuery.Builder();
        return matchAllQuery.build();
    }
    
    //supply back bool query to filter out employees based on query params
    public static Supplier<Query> supplierBoolQuery(Map<String, String> map) {
        Supplier<Query> supplier = ()->Query.of(q->q.bool(boolQuery(map)));
        return supplier;
    }

    //preparing sort query to get resultant field based on query param
    public static List<SortOptions> getSortingList(Map<String,String> map){
        List<SortOptions> list = new ArrayList<>();
        //iterating map to check if any param having 'asc' or desc to sort depending on that
        for(String key: map.keySet()){
            SortOptions sort = null;
            String value = map.get(key);
            if(value.equals("asc")){
                sort = new SortOptions.Builder().field(f -> f.field(key).order(SortOrder.Asc)).build();
            }else if(value.equals("desc")){
                sort = new SortOptions.Builder().field(f -> f.field(key).order(SortOrder.Desc)).build();
            }
            if(null != sort){
                list.add(sort);
            }
        }
        return list;
    }

    //preaparing bool query with match, range and sort for filtering the employees based on query params
    public static BoolQuery boolQuery(Map<String, String> map){
        BoolQuery.Builder boolQuery = new BoolQuery.Builder();
        List<Query> queries = new ArrayList<>();
        //iterating through map to check fields to match on or put range condition
        for(String key: map.keySet()){
            if(map.get(key).equalsIgnoreCase(SortOrder.Asc.name()) || map.get(key).equalsIgnoreCase(SortOrder.Desc.name())){
                continue;
            }
            String fieldName = "";
            String rangeCondition = null;
            String value = "";
            if(key.contains(".")){
                String keyToSplit = key;
                int dotIndex = keyToSplit.indexOf(".");
                fieldName = keyToSplit.substring(0, dotIndex);
                rangeCondition = keyToSplit.substring(dotIndex+1, keyToSplit.length());
                value = map.get(key);
            }else{
                fieldName = key;
                value = map.get(key);
            }
            if(null == rangeCondition || rangeCondition.equals("")){
                queries.addAll(matchQueryWithName(fieldName, value));
                
            }else{
                queries.addAll(RangeQueryWithName(fieldName, rangeCondition, value));
            }
            
        }
        return boolQuery.filter(queries).build();
    }
    
    //preparing match query to be used in bool query above depending on the fields passed
    public static List<Query> matchQueryWithName(String fieldName, String fieldValue){
        final List<Query> matches = new ArrayList<>();
        val matchQuery = new MatchQuery.Builder();
        matches.add(Query.of(q->q.match(matchQuery.field(fieldName).query(fieldValue).build())));
        return matches;
    }

    //preparing range query to be used in bool query above depending on the fields passed
    private static List<Query> RangeQueryWithName(String fieldName, String rangeCondition, String value) {
        final List<Query> rangeQueries = new ArrayList<>();
        val rangeQuery = new RangeQuery.Builder();
        if(rangeCondition.equals("gte"))
            rangeQueries.add(Query.of(q-> q.range(rangeQuery.field(fieldName).gte(JsonData.of(value)).build())));
        else if(rangeCondition.equals("gt"))
            rangeQueries.add(Query.of(q-> q.range(rangeQuery.field(fieldName).gt(JsonData.of(value)).build())));
        else if(rangeCondition.equals("lte"))
            rangeQueries.add(Query.of(q-> q.range(rangeQuery.field(fieldName).lte(JsonData.of(value)).build())));
        else if(rangeCondition.equals("lt"))
            rangeQueries.add(Query.of(q-> q.range(rangeQuery.field(fieldName).lt(JsonData.of(value)).build())));
        return rangeQueries;
    }

    //supply back match query depending on id
    public static Supplier<Query> supplierWithId(String id){
        Supplier<Query> supplier = () -> Query.of(q -> q.match(matchQueryWithId(id)));
        return supplier;
    }

    //preparing match query depending on id
    private static MatchQuery matchQueryWithId(String id) {
        MatchQuery.Builder matchQuery = new MatchQuery.Builder();
        return matchQuery.field("id").query(id)
        .build();
    }
}
