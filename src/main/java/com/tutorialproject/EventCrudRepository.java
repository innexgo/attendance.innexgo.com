package com.tutorialproject;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface EventCrudRepository extends CrudRepository<Event, Long>{
	@Query("SELECT e FROM Event e WHERE e.student.id = :#{#s.id} ORDER BY e.time DESC")
    public List<Event> mostRecentEvents(@Param("s") Student s);
}
