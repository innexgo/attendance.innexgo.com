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
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TutorialProjectApiController {

	@Autowired
	LocationCrudRepository locationCrudRepository;

	@Autowired
	StudentCrudRepository studentCrudRepository;

	@Autowired
	TrackerEventCrudRepository trackerEventCrudRepository;

	@Autowired
	TrackerSessionCrudRepository trackerSessionCrudRepository;

	static final ResponseEntity<?> BAD_REQUEST = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	static final ResponseEntity<?> INTERNAL_SERVER_ERROR = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	static final ResponseEntity<?> OK = new ResponseEntity<>(HttpStatus.OK);
	static final ResponseEntity<?> NOT_FOUND = new ResponseEntity<>(HttpStatus.NOT_FOUND);


	@RequestMapping(value="events/new/")
	public  ResponseEntity<?> newEvent(@RequestParam("studentId")Long studentId, @RequestParam("locationId")Long locationId, @RequestParam("type")String type)
	{
		TrackerEvent trackerEvent = new TrackerEvent();
		trackerEvent.setLocation(locationCrudRepository.findById(locationId).get());
		trackerEvent.setStudent(studentCrudRepository.findById(studentId).get());
		trackerEvent.setTime(new Timestamp(System.currentTimeMillis()));
		trackerEvent.setType(type);
		List<TrackerEvent> eventList = trackerEventCrudRepository.mostRecentEvents(trackerEvent.student);
		if(eventList.size() > 0)
		{	
			TrackerEvent oldEvent = eventList.get(0);
			if(oldEvent.getLocation().getId() != trackerEvent.getLocation().getId())
			{
				TrackerSession trackerSession = new TrackerSession();
				trackerSession.setLocation(oldEvent.getLocation());
				trackerSession.setStudent(oldEvent.getStudent());
				trackerSession.setStartTime(oldEvent.getTime());
				trackerSession.setEndTime(trackerEvent.getTime());
				trackerSessionCrudRepository.save(trackerSession);
			}
		}

		trackerEventCrudRepository.save(trackerEvent);
		return OK;
	}

	@RequestMapping(value="students/new/")
	public  ResponseEntity<?> newStudent(@RequestParam("studentId")Long studentId, @RequestParam("name")String name)
	{
		Student s = new Student();
		s.setName(name);
		s.setId(studentId);
		studentCrudRepository.save(s);
		return OK;
	}

	@RequestMapping(value="locations/new/")
	public  ResponseEntity<?> newLocation(@RequestParam("locationId")Long locationId, @RequestParam("name")String name)
	{
		Location location = new Location();
		location.setName(name);
		location.setId(locationId);
		locationCrudRepository.save(location);
		return OK;
	}

	@RequestMapping(value="events/delete/")
	public  ResponseEntity<?> deleteEvent(@RequestParam(value="eventId")Long eventId)
	{
		trackerEventCrudRepository.deleteById(eventId);
		return OK;
	}

	@RequestMapping(value="students/delete/")
	public  ResponseEntity<?> deleteStudent(@RequestParam(value="studentId")Long studentId)
	{
		studentCrudRepository.deleteById(studentId);
		return OK;
	}

	@RequestMapping(value="locations/delete/")
	public  ResponseEntity<?> deleteLocation(@RequestParam(value="locationId")Long locationId)
	{
		locationCrudRepository.deleteById(locationId);
		return OK;
	}

	@RequestMapping(value="events/")
	public  ResponseEntity<?> viewEvent(@RequestParam Map<String,String> allRequestParam)
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

		List<TrackerEvent> eventReturnList = (List<TrackerEvent>)trackerEventCrudRepository.findAll();

		for(TrackerEvent e : trackerEventCrudRepository.findAll())
		{
			//filter by criteria
			if(hasMaxDate && e.getTime().getTime() > maxDate)
			{
				eventReturnList.remove(e);
				continue;
			}

			if(hasMinDate && e.getTime().getTime() < minDate)
			{
				eventReturnList.remove(e);
				continue;
			}

			if(hasEventId && e.getId() != eventId)
			{
				eventReturnList.remove(e);
				continue;
			}

			if(hasLocationId && e.getLocation().getId() != locationId)
			{
				eventReturnList.remove(e);
				continue;
			}

			if(hasStudentId && e.getStudent().getId() != studentId)
			{
				eventReturnList.remove(e);
				continue;
			}
		}

		return new ResponseEntity<>(eventReturnList,HttpStatus.OK);
	}

	@RequestMapping(value="students/")
	public  ResponseEntity<?> viewStudent(@RequestParam Map<String,String> allRequestParam)
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
	public  ResponseEntity<?> viewLocation(@RequestParam Map<String,String> allRequestParam)
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
