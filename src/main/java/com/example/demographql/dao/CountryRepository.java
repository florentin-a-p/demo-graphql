package com.example.demographql.dao;

import com.example.demographql.entity.Country;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends CrudRepository<Country, Integer>  {
  Country findById(int id);
  Country findByName(String name);
}
