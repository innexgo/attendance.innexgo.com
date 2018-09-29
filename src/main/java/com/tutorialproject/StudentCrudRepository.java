package com.tutorialproject;

import org.springframework.data.repository.CrudRepository;

public interface StudentCrudRepository extends CrudRepository<Student, Long>{
	
}
