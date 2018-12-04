package com.tutorialproject;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface TrackerEventCrudRepository extends CrudRepository<TrackerEvent, Long>{
	
	
	
	@Query("SELECT e FROM TrackerEvent e WHERE e.student.id = :#{#s.id} ORDER BY e.time DESC")
    public List<TrackerEvent> mostRecentEvents(@Param("s") Student s);


}
