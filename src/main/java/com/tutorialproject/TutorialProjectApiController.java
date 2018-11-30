package com.tutorialproject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TutorialProjectApiController {

	@Autowired
	LocationCrudRepository locationCrudRepository;

	@Autowired
	StudentCrudRepository studentCrudRepository;

	@Autowired
	EventCrudRepository eventCrudRepository;

	@Autowired
	SessionCrudRepository sessionCrudRepository;

	static final ResponseEntity<?> BAD_REQUEST = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	static final ResponseEntity<?> INTERNAL_SERVER_ERROR = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	static final ResponseEntity<?> OK = new ResponseEntity<>(HttpStatus.OK);
	static final ResponseEntity<?> NOT_FOUND = new ResponseEntity<>(HttpStatus.NOT_FOUND);


	@RequestMapping(value="events/new/")
	public ResponseEntity<?> newEvent(@RequestParam("studentId")Long studentId, @RequestParam("locationId")Long locationId)
	{
		Event event = new Event();
		event.setLocation(locationCrudRepository.findById(locationId).get());
		event.setStudent(studentCrudRepository.findById(studentId).get());
		event.setTime(new Timestamp(System.currentTimeMillis()));

		List<Event> eventList = eventCrudRepository.mostRecentEvents(event.student);
		if(eventList.size() > 0)
		{	
			Event oldEvent = eventList.get(0);
			if(oldEvent.getLocation().getId() != event.getLocation().getId())
			{
				Session session = new Session();
				session.setLocation(oldEvent.getLocation());
				session.setStudent(oldEvent.getStudent());
				session.setStartTime(oldEvent.getTime());
				session.setEndTime(event.getTime());
				sessionCrudRepository.save(session);
			}
		}

		eventCrudRepository.save(event);
		return OK;
	}

	@RequestMapping(value="students/new/")
	public ResponseEntity<?> newStudent(@RequestParam("studentId")Long studentId, @RequestParam("name")String name)
	{
		Student s = new Student();
		s.setName(name);
		s.setId(studentId);
		studentCrudRepository.save(s);
		return OK;
	}

	@RequestMapping(value="locations/new/")
	public ResponseEntity<?> newLocation(@RequestParam("locationId")Long locationId, @RequestParam("name")String name)
	{
		Location location = new Location();
		location.setName(name);
		location.setId(locationId);
		locationCrudRepository.save(location);
		return OK;
	}

	@RequestMapping(value="events/delete/")
	public ResponseEntity<?> deleteEvent(@RequestParam(value="eventId")Long eventId)
	{
		eventCrudRepository.deleteById(eventId);
		return OK;
	}

	@RequestMapping(value="students/delete/")
	public ResponseEntity<?> deleteStudent(@RequestParam(value="studentId")Long studentId)
	{
		studentCrudRepository.deleteById(studentId);
		return OK;
	}

	@RequestMapping(value="locations/delete/")
	public ResponseEntity<?> deleteLocation(@RequestParam(value="locationId")Long locationId)
	{
		locationCrudRepository.deleteById(locationId);
		return OK;
	}

	@RequestMapping(value="events/")
	public @ResponseBody ResponseEntity<?> viewEvent(@RequestParam Map<String,String> allRequestParam)
	{
		boolean hasMaxDate = false;
		boolean hasMinDate = false;

		long maxDate = Long.MAX_VALUE;
		long minDate = Long.MIN_VALUE;
		
		boolean hasEventId = false;
		boolean hasLocationId = false;
		boolean hasStudentId = false;

		long eventId = 0;
		long locationId = 0;
		long studentId = 0;
		
		
		if(allRequestParam.containsKey("maxDate"))
		{
			hasMaxDate=true;
			maxDate = Long.parseLong(allRequestParam.get("maxDate"));
		}

		if(allRequestParam.containsKey("minDate"))
		{
			hasMinDate=true;
			minDate = Long.parseLong(allRequestParam.get("minDate"));
		}
		
		if(allRequestParam.containsKey("eventId"))
		{
			hasEventId = true;
			eventId = Long.parseLong(allRequestParam.get("eventId"));
		}
		
		if(allRequestParam.containsKey("locationId"))
		{
			hasLocationId = true;
			locationId = Long.parseLong(allRequestParam.get("locationId"));
		}
		
		if(allRequestParam.containsKey("studentId"))
		{
			hasStudentId = true;
			studentId = Long.parseLong(allRequestParam.get("studentId"));
		}

		ArrayList<Event> eventReturnList = new ArrayList<Event>(); 
		
		for(Event e : eventCrudRepository.findAll())
		{
			
			//filter by criteria
			if(hasMaxDate && e.getTime().getTime() > maxDate)
			{
				continue;
			}
			
			if(hasMinDate && e.getTime().getTime() < minDate)
			{
				continue;
			}
			
			if(hasEventId && e.getId() != eventId)
			{
				continue;
			}
			
			if(hasLocationId && e.getLocation().getId() != locationId)
			{
				continue;
			}
			
			if(hasStudentId && e.getStudent().getId() != studentId)
			{
				continue;
			}
			//add to list;
			eventReturnList.add(e);
		}
		
		return new ResponseEntity<>(eventReturnList,HttpStatus.OK);
	}

	@RequestMapping(value="students/")
	public @ResponseBody ResponseEntity<?> viewStudent(@RequestParam Map<String,String> allRequestParam)
	{
		Long studentId = Long.parseLong(allRequestParam.get("studentId"));
		if(studentCrudRepository.existsById(studentId))
		{
			Student e = studentCrudRepository.findById(studentId).get();
			return new ResponseEntity<>(e,HttpStatus.OK);
		}
		else
		{
			return NOT_FOUND;
		}
	}

	@RequestMapping(value="locations/")
	public @ResponseBody ResponseEntity<?> viewLocation(@RequestParam Map<String,String> allRequestParam)
	{
		Long locationId = Long.parseLong(allRequestParam.get("locationId"));

		if(locationCrudRepository.existsById(locationId))
		{	
			Location e = locationCrudRepository.findById(locationId).get();
			return new ResponseEntity<>(e,HttpStatus.OK);
		}
		else
		{
			return NOT_FOUND;
		}
	}
}
