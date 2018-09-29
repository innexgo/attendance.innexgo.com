package com.tutorialproject;

import java.sql.Timestamp;
import java.util.List;

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
		
		@RequestMapping(value="events/{eventId}/")
		public @ResponseBody ResponseEntity<?> viewEvent(@PathVariable Long eventId)
		{
			if(eventCrudRepository.existsById(eventId))
			{
				Event e = eventCrudRepository.findById(eventId).get();
				return new ResponseEntity<>(e,HttpStatus.OK);
			}
			else
			{
				return NOT_FOUND;
			}
		}
		
		@RequestMapping(value="students/{studentId}/")
		public @ResponseBody ResponseEntity<?> viewStudent(@PathVariable Long studentId)
		{
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
		
		@RequestMapping(value="locations/{locationId}/")
		public @ResponseBody ResponseEntity<?> viewLocation(@PathVariable Long locationId)
		{
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
