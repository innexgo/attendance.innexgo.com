package innexo;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RestController
public class InnexoApiController{

	@Autowired
	UserService userService;

	@Autowired
	EncounterService encounterService;

	@Autowired
	LocationService locationService;
	
	@Autowired
	PermissionService permissionService;
	
	@Autowired
	RequestService requestService;
	
	@Autowired
	TargetService targetService;

	static final ResponseEntity<?> BAD_REQUEST = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	static final ResponseEntity<?> INTERNAL_SERVER_ERROR = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	static final ResponseEntity<?> OK = new ResponseEntity<>(HttpStatus.OK);
	static final ResponseEntity<?> NOT_FOUND = new ResponseEntity<>(HttpStatus.NOT_FOUND);


	@RequestMapping(value="encounter/new/")
	public ResponseEntity<?> newEncounter(
			@RequestParam("userId")Integer userId, 
			@RequestParam("locationId")Integer locationId, 
			@RequestParam("type")String type)
	{
		if(locationId != null && locationId != null && type != null &&
				locationService.exists(locationId) && userService.exists(userId)) {
			Encounter encounter = new Encounter();
			encounter.locationId = locationId;
			encounter.userId = userId;
			encounter.time = new Timestamp(System.currentTimeMillis());
			encounter.type = Utils.valString(type);
			encounterService.add(encounter);
			return OK;
		} else {
			return BAD_REQUEST ;
		}
	}

	@RequestMapping(value="user/new/")
	public ResponseEntity<?> newUser(
			@RequestParam("userId")Integer userId, 
			@RequestParam("name")String name,
			@RequestParam("password")String password)
	{
		if(!userService.exists(userId)) {
			User u = new User();
			u.id = userId;
			u.name = Utils.valString(name);
			u.passwordHash = new BCryptPasswordEncoder().encode(password);
			u.permissionId = 0; //TODO auth
			userService.add(u);
			return OK;
		} else {
			return BAD_REQUEST;
		}
	}

	@RequestMapping(value="location/new/")
	public ResponseEntity<?> newLocation(
			@RequestParam("name")String name, 
			@RequestParam("tags")String tags)
	{
		Location location = new Location();
		location.name = Utils.valString(name);
		location.tags = Utils.valString(tags);
		locationService.add(location);
		return OK;
	}
	
	
	@RequestMapping(value="permission/new/")
	public ResponseEntity<?> newPermission(
			@RequestParam("isTrustedUser")Boolean isTrustedUser,
			@RequestParam("isAdministrator")Boolean isAdministrator)
	{
		Permission permission = new Permission();
		permission.isAdministrator = isAdministrator;
		permission.isTrustedUser = isTrustedUser;
		permissionService.add(permission);
		return OK;
	}

	@RequestMapping(value="request/new/")
	public ResponseEntity<?> newRequest(
			@RequestParam("userId")Integer userId,
			@RequestParam("targetId")Integer targetId,
			@RequestParam("creatorId")Integer creatorId)
	{
		if(userId != null && targetId != null && creatorId != null 
				&& userService.exists(userId) && userService.exists(creatorId) && targetService.exists(targetId))
		{
			Request request = new Request();
			request.userId = userId;
			request.targetId = targetId;
			request.creatorId = creatorId;
			request.authorized = null;
			request.creationDate = Timestamp.from(Instant.now());
			requestService.add(request);
			return OK;
		} else {
			return BAD_REQUEST;
		}
	}
	
	@RequestMapping(value="target/new/")
	public ResponseEntity<?> newTarget(
			@RequestParam("userId")Integer userId,
			@RequestParam("locationId")Integer locationId,
			@RequestParam("name")String name,
			@RequestParam("minTime")Long minTime,
			@RequestParam("minTime")Long maxTime)
	{

		if(userId != null && locationId != null && name != null && minTime != null && maxTime != null
				&& userService.exists(userId) && locationService.exists(locationId))
		{
			Target target = new Target();
			target.userId= userId;
			target.locationId= locationId;
			target.name = name;
			target.minTime = Timestamp.from(Instant.ofEpochSecond(minTime));
			target.maxTime = Timestamp.from(Instant.ofEpochSecond(maxTime));
			targetService.add(target);
			return OK;
		} else {
			return BAD_REQUEST;
		}
	}
	
	
	
	@RequestMapping(value="encounter/delete/")
	public ResponseEntity<?> deleteEncounter(
			@RequestParam(value="encounterId")Integer encounterId) {
		encounterService.delete(encounterId);
		return OK;
	}

	@RequestMapping(value="user/delete/")
	public ResponseEntity<?> deleteStudent(@RequestParam(value="userId")Integer userId)
	{
		userService.delete(userId);
		return OK;
	}

	@RequestMapping(value="location/delete/")
	public ResponseEntity<?> deleteLocation(@RequestParam(value="locationId")Integer locationId)
	{
		locationService.delete(locationId);
		return OK;
	}
	
	@RequestMapping(value="permission/delete/")
	public ResponseEntity<?> deletePermission(@RequestParam(value="permissionId")Integer permissionId)
	{
		permissionService.delete(permissionId);
		return OK;
	}
	
	@RequestMapping(value="target/delete/")
	public ResponseEntity<?> deleteTarget(@RequestParam(value="targetId")Integer targetId)
	{
		targetService.delete(targetId);
		return OK;
	}
	
	@RequestMapping(value="request/delete/")
	public ResponseEntity<?> deleteRequest(@RequestParam(value="requestId")Integer requestId)
	{
		requestService.delete(requestId);
		return OK;
	}
	
	@RequestMapping(value="encounter/")
	public ResponseEntity<?> viewEvent(@RequestParam Map<String,String> allRequestParam)
	{
		Function<String, Integer> parseInteger = (str) -> str == null ? null : Integer.parseInt(str);
		Function<String, Timestamp> parseTimestamp = (str) -> 
		str == null ? null : Timestamp.from(Instant.ofEpochSecond(Long.parseLong(str)));
		List<Encounter> els = encounterService.query(
				parseInteger.apply(allRequestParam.get("count")),
				parseInteger.apply(allRequestParam.get("encounterId")), 
				parseInteger.apply(allRequestParam.get("userId")),
				parseInteger.apply(allRequestParam.get("locationId")), 
				parseTimestamp.apply(allRequestParam.get("minDate")), 
				parseTimestamp.apply(allRequestParam.get("maxDate")),
				Utils.valString(allRequestParam.get("userName")),
				Utils.valString(allRequestParam.get("type")))
		.stream()
		.map((e) -> {
			e.location = locationService.getById(e.locationId);
			e.user = userService.getById(e.userId);
			return e;
		}).collect(Collectors.toList());
		return new ResponseEntity<>(els, HttpStatus.OK);
	}

	@RequestMapping(value="user/")
	public ResponseEntity<?> viewStudent(@RequestParam Map<String,String> allRequestParam)
	{

		if(allRequestParam.containsKey("userId")) {
			return new ResponseEntity<>(
					Arrays.asList(userService.getById(Integer.parseInt(allRequestParam.get("userId")))),
					HttpStatus.OK
					);
		} else {
			return new ResponseEntity<>(
					userService.getAll(),
					HttpStatus.OK
					);
		}
	}

	@RequestMapping(value="location/")
	public ResponseEntity<?> viewLocation(@RequestParam Map<String,String> allRequestParam)
	{
		if(allRequestParam.containsKey("locationId")) {
			return new ResponseEntity<>(
					Arrays.asList(locationService.getById(Integer.parseInt(allRequestParam.get("locationId")))),
					HttpStatus.OK
					);
		} else {
			return new ResponseEntity<>(
					locationService.getAll(), 
					HttpStatus.OK
					);
		}
	}
	
	@RequestMapping(value="permission/")
	public ResponseEntity<?> viewPermission(@RequestParam Map<String,String> allRequestParam)
	{
		if(allRequestParam.containsKey("permissionId")) {
			return new ResponseEntity<>(
					Arrays.asList(permissionService.getById(Integer.parseInt(allRequestParam.get("permissionId")))),
					HttpStatus.OK
					);
		} else {
			return new ResponseEntity<>(
					permissionService.getAll(), 
					HttpStatus.OK
					);
		}
	}
	
	@RequestMapping(value="target/")
	public ResponseEntity<?> viewTarget(@RequestParam Map<String,String> allRequestParam)
	{
		if(allRequestParam.containsKey("targetId")) {
			return new ResponseEntity<>(
					Arrays.asList(targetService.getById(Integer.parseInt(allRequestParam.get("targetId")))),
					HttpStatus.OK
					);
		} else {
			return new ResponseEntity<>(
					targetService.getAll(), 
					HttpStatus.OK
					);
		}
	}
	
	@RequestMapping(value="request/")
	public ResponseEntity<?> viewRequest(@RequestParam Map<String,String> allRequestParam)
	{
		if(allRequestParam.containsKey("requestId")) {
			return new ResponseEntity<>(
					Arrays.asList(requestService.getById(Integer.parseInt(allRequestParam.get("requestId")))),
					HttpStatus.OK
					);
		} else {
			return new ResponseEntity<>(
					requestService.getAll(), 
					HttpStatus.OK
					);
		}
	}
	
	//Special methods
	@RequestMapping(value="request/authorize/")
	public ResponseEntity<?> approveRequest(@RequestParam(value="requestId")Integer requestId, @RequestParam(value="authorized")Boolean authorized)
	{
		if(requestService.exists(requestId)) {
			Request r = requestService.getById(requestId);
			r.authorizationDate = Timestamp.from(Instant.now());
			r.authorized = authorized;
			requestService.update(r);
			return OK;
		}
		return BAD_REQUEST;
	}
	
}
