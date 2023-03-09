package com.example.demographql.controller;

import com.example.demographql.dao.CountryRepository;
import com.example.demographql.entity.Country;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
public class CountriesController {
  @Autowired
  private CountryRepository repository;

  @Value("classpath:countries.graphqls")
  private Resource schemaResource;

  private GraphQL graphQL;

  @PostConstruct
  public void loadSchema() throws IOException {
    File schemaFile = schemaResource.getFile();
    TypeDefinitionRegistry registry = new SchemaParser().parse(schemaFile);
    RuntimeWiring wiring = buildWiring();
    GraphQLSchema schema = new SchemaGenerator().makeExecutableSchema(registry, wiring);
    graphQL = GraphQL.newGraphQL(schema).build();
  }

  private RuntimeWiring buildWiring() {
    DataFetcher<List<Country>> fetcher1 = data -> {
      return (List<Country>) repository.findAll();
    };

    DataFetcher<Optional<Country>> fetcher2 = data -> {
      return repository.findById(data.getArgument("id"));
    };

    DataFetcher<Country> fetcher3 = data -> {
      return repository.findByName(data.getArgument("name"));
    };

    return RuntimeWiring.newRuntimeWiring()
        .type("Query",
            typeWriting -> typeWriting
                .dataFetcher("getCountries", fetcher1)
                .dataFetcher("getCountriesWithId", fetcher2)
                .dataFetcher("getCountriesWithName", fetcher3)
        )
        .build();
  }

  @PostMapping(path="addCountry")
  public String addCountry(@RequestBody List<Country> addedCountry) {
    repository.saveAll(addedCountry);
    return "record inserted " + addedCountry.size();
  }

  @PostMapping("/getCountries")
  public ResponseEntity<Object> getCountries(@RequestBody String query) {
    ExecutionResult result = graphQL.execute(query);
    return new ResponseEntity<Object>(result, HttpStatus.OK);
  }

  @PostMapping("/getCountriesWithId")
  public ResponseEntity<Object> getCountriesWithId(@RequestBody String query) {
    ExecutionResult result = graphQL.execute(query);
    return new ResponseEntity<Object>(result, HttpStatus.OK);
  }

  @PostMapping("/getCountriesWithName")
  public ResponseEntity<Object> getCountriesWithName(@RequestBody String query) {
    ExecutionResult result = graphQL.execute(query);
    return new ResponseEntity<Object>(result, HttpStatus.OK);
  }
}
