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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InnexoApiController {
  @Autowired UserService userService;

  @Autowired EncounterService encounterService;

  @Autowired LocationService locationService;

  static final ResponseEntity<?> BAD_REQUEST = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  static final ResponseEntity<?> INTERNAL_SERVER_ERROR =
      new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
  static final ResponseEntity<?> OK = new ResponseEntity<>(HttpStatus.OK);
  static final ResponseEntity<?> NOT_FOUND = new ResponseEntity<>(HttpStatus.NOT_FOUND);

  Function<String, Integer> parseInteger = (str) -> str == null ? null : Integer.parseInt(str);
  Function<String, Boolean> parseBoolean = (str) -> str == null ? null : Boolean.parseBoolean(str);
  Function<String, Timestamp> parseTimestamp =
      (str) -> str == null ? null : Timestamp.from(Instant.ofEpochSecond(Long.parseLong(str)));

  Function<Encounter, Encounter> fillEncounter = (e) -> {
    e.location = locationService.getById(e.locationId);
    e.user = userService.getById(e.userId);
    return e;
  };

  @RequestMapping(value = "encounter/new/")
  public ResponseEntity<?> newEncounter(@RequestParam("userId") Integer userId,
      @RequestParam("locationId") Integer locationId, @RequestParam("type") String type) {
    if (locationId != null && locationId != null && type != null
        && locationService.exists(locationId) && userService.exists(userId)) {
      Encounter encounter = new Encounter();
      encounter.locationId = locationId;
      encounter.userId = userId;
      encounter.time = new Timestamp(System.currentTimeMillis());
      encounter.type = type;
      encounterService.add(encounter);
      return OK;
    } else {
      return BAD_REQUEST;
    }
  }

  @RequestMapping(value = "user/new/")
  public ResponseEntity<?> newUser(@RequestParam("userId") Integer userId,
      @RequestParam("name") String name, @RequestParam("administrator") Boolean administrator,
      @RequestParam("trustedUser") Boolean trustedUser, @RequestParam("password") String password) {
    if (!userService.exists(userId)) {
      User u = new User();
      u.id = userId;
      u.name = name;
      u.passwordHash = new BCryptPasswordEncoder().encode(password);
      u.permissionId = 0; // TODO auth
      userService.add(u);
      return OK;
    } else {
      return BAD_REQUEST;
    }
  }

  @RequestMapping(value = "location/new/")
  public ResponseEntity<?> newLocation(
      @RequestParam("name") String name, @RequestParam("tags") String tags) {
    Location location = new Location();
    location.name = name;
    location.tags = tags;
    locationService.add(location);
    return OK;
  }

  @RequestMapping(value = "encounter/delete/")
  public ResponseEntity<?> deleteEncounter(
      @RequestParam(value = "encounterId") Integer encounterId) {
    encounterService.delete(encounterId);
    return OK;
  }

  @RequestMapping(value = "user/delete/")
  public ResponseEntity<?> deleteStudent(@RequestParam(value = "userId") Integer userId) {
    userService.delete(userId);
    return OK;
  }

  @RequestMapping(value = "location/delete/")
  public ResponseEntity<?> deleteLocation(@RequestParam(value = "locationId") Integer locationId) {
    locationService.delete(locationId);
    return OK;
  }

  @RequestMapping(value = "encounter/")
  public ResponseEntity<?> viewEncounter(@RequestParam Map<String, String> allRequestParam) {
    List<Encounter> els = encounterService
                              .query(parseInteger.apply(allRequestParam.get("count")),
                                  parseInteger.apply(allRequestParam.get("encounterId")),
                                  parseInteger.apply(allRequestParam.get("userId")),
                                  parseInteger.apply(allRequestParam.get("locationId")),
                                  parseTimestamp.apply(allRequestParam.get("minDate")),
                                  parseTimestamp.apply(allRequestParam.get("maxDate")),
                                  allRequestParam.get("userName"), 
                                  allRequestParam.get("type"))
                              .stream()
                              .map(fillEncounter)
                              .collect(Collectors.toList());
    return new ResponseEntity<>(els, HttpStatus.OK);
  }

  @RequestMapping(value = "user/")
  public ResponseEntity<?> viewStudent(@RequestParam Map<String, String> allRequestParam) {
    if (allRequestParam.containsKey("userId")) {
      return new ResponseEntity<>(
          Arrays.asList(userService.getById(Integer.parseInt(allRequestParam.get("userId")))),
          HttpStatus.OK);
    } else if (allRequestParam.containsKey("name")) {
      return new ResponseEntity<>(
          userService.getByName(allRequestParam.get("name")), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(userService.getAll(), HttpStatus.OK);
    }
  }

  @RequestMapping(value = "location/")
  public ResponseEntity<?> viewLocation(@RequestParam Map<String, String> allRequestParam) {
    if (allRequestParam.containsKey("locationId")) {
      return new ResponseEntity<>(Arrays.asList(locationService.getById(
                                      Integer.parseInt(allRequestParam.get("locationId")))),
          HttpStatus.OK);
    } else {
      return new ResponseEntity<>(locationService.getAll(), HttpStatus.OK);
    }
  }
}
