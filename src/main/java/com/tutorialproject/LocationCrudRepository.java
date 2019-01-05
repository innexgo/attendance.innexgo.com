package com.tutorialproject;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface LocationCrudRepository extends CrudRepository<Location,Long> {
	
}
