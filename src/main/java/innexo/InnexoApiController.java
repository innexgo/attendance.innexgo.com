package innexo;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
  @Autowired ApiKeyService apiKeyService;

  static final ResponseEntity<?> BAD_REQUEST = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  static final ResponseEntity<?> INTERNAL_SERVER_ERROR =
      new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
  static final ResponseEntity<?> OK = new ResponseEntity<>(HttpStatus.OK);
  static final ResponseEntity<?> NOT_FOUND = new ResponseEntity<>(HttpStatus.NOT_FOUND);

  Function<String, Integer> parseInteger = (str) -> str == null ? null : Integer.parseInt(str);
  Function<String, Boolean> parseBoolean = (str) -> str == null ? null : Boolean.parseBoolean(str);
  Function<String, Timestamp> parseTimestamp =
      (str) -> str == null ? null : Timestamp.from(Instant.ofEpochSecond(Long.parseLong(str)));

  Function<User, User> fillUser = (u) -> u;
  Function<Location, Location> fillLocation = (l) -> l;
  Function<ApiKey, ApiKey> fillApiKey = (k) -> {
    k.creator = fillUser.apply(userService.getById(creatorId));
    return k;
  };

  Function<Encounter, Encounter> fillEncounter = (e) -> {
    e.location = fillLocation.apply(locationService.getById(e.locationId));
    e.user = fillUser.apply(userService.getById(e.userId));
    return e;
  };

  @RequestMapping(value = "encounter/new/")
  public ResponseEntity<?> newEncounter(@RequestParam("userId") Integer userId,
      @RequestParam("locationId") Integer locationId, @RequestParam("type") String type) {
    if (!Utils.isBlank(type) && locationService.exists(locationId) && userService.exists(userId)) {
      Encounter encounter = new Encounter();
      encounter.locationId = locationId;
      encounter.userId = userId;
      encounter.time = new Timestamp(System.currentTimeMillis());
      encounter.type = type;
      encounterService.add(encounter);
      // return the filled encounter on success
      return new ResponseEntity<>(fillEncounter.apply(encounter), HttpStatus.OK);
    } else {
      return BAD_REQUEST;
    }
  }

  @RequestMapping(value = "user/new/")
  public ResponseEntity<?> newUser(@RequestParam("userId") Integer userId,
      @RequestParam(value = "managerId", defaultValue = "-1") Integer managerId,
      @RequestParam("name") String name,
      @RequestParam(value = "administrator", defaultValue = "false") Boolean administrator,
      @RequestParam(value = "trustedUser", defaultValue = "false") Boolean trustedUser,
      @RequestParam("password") String password) {
    if (!Utils.isBlank(name) && !Utils.isBlank(password) && !userService.exists(userId)) {
      User u = new User();
      u.id = userId;
      if (managerId != -1 && userService.exists(managerId)) {
        u.managerId = managerId;
      }
      u.name = name;
      u.passwordHash = new BCryptPasswordEncoder().encode(password);
      u.administrator = administrator;
      u.trustedUser = !administrator && trustedUser; // false if administrator is enabled
      userService.add(u);
      return new ResponseEntity<>(fillUser.apply(u), HttpStatus.OK);
    } else {
      return BAD_REQUEST;
    }
  }

  @RequestMapping(value = "location/new/")
  public ResponseEntity<?> newLocation(
      @RequestParam("name") String name, @RequestParam("tags") String tags) {
    if (!Utils.isBlank(name) && !Utils.isBlank(tags)) {
      Location location = new Location();
      location.name = name;
      location.tags = tags;
      locationService.add(location);
      return new ResponseEntity<>(fillLocation.apply(location), HttpStatus.OK);
    } else {
      return BAD_REQUEST;
    }
  }

  @RequestMapping(value = "apiKey/new/")
  public ResponseEntity<?> newApiKey(@RequestParam("creatorId") Integer creatorId,
      @RequestParam("expirationTime") Integer expirationTime) {
    if (!Utils.isBlank(name) && !Utils.isBlank(tags)) {
      ApiKey apiKey = new ApiKey();
      apiKey.creatorId = creatorId;
      apiKey.creationTime = new Timestamp(System.currentTimeMillis());
      apiKey.expirationTime = new Timestamp(expirationTime * 1000); // epoch time to milis
      apiKey.key = UUID.randomUUID().toString(); // quick hacks, please replace
      apiKeyService.add(apiKey);
      return new ResponseEntity<>(fillApiKey.apply(apiKey), HttpStatus.OK);
    } else {
      return BAD_REQUEST;
    }
  }

  @RequestMapping(value = "encounter/delete/")
  public ResponseEntity<?> deleteEncounter(
      @RequestParam(value = "encounterId") Integer encounterId) {
    return new ResponseEntity<>(
        fillEncounter.apply(encounterService.delete(encounterId)), HttpStatus.OK);
  }

  @RequestMapping(value = "user/delete/")
  public ResponseEntity<?> deleteStudent(@RequestParam(value = "userId") Integer userId) {
    return new ResponseEntity<>(fillUser.apply(userService.delete(userId)), HttpStatus.OK);
  }

  @RequestMapping(value = "location/delete/")
  public ResponseEntity<?> deleteLocation(@RequestParam(value = "locationId") Integer locationId) {
    return new ResponseEntity<>(
        fillLocation.apply(locationService.delete(locationId)), HttpStatus.OK);
  }

  @RequestMapping(value = "apiKey/delete/")
  public ResponseEntity<?> deleteApiKey(@RequestParam(value = "apiKeyId") Integer apiKeyId) {
    return new ResponseEntity<>(fillApiKey.apply(apiKeyService.delete(apiKeyId)), HttpStatus.OK);
  }

  @RequestMapping(value = "encounter/")
  public ResponseEntity<?> viewEncounter(@RequestParam Map<String, String> allRequestParam) {
    List<Encounter> els = encounterService
                              .query(parseInteger.apply(allRequestParam.get("count")),
                                  parseInteger.apply(allRequestParam.get("encounterId")),
                                  parseInteger.apply(allRequestParam.get("userId")),
                                  parseInteger.apply(allRequestParam.get("userManagerId")),
                                  parseInteger.apply(allRequestParam.get("locationId")),
                                  parseTimestamp.apply(allRequestParam.get("minDate")),
                                  parseTimestamp.apply(allRequestParam.get("maxDate")),
                                  allRequestParam.get("userName"), allRequestParam.get("type"))
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

  @RequestMapping(value = "apiKey/")
  public ResponseEntity<?> viewApiKey(@RequestParam Map<String, String> allRequestParam) {
    Stream stream = null;
    if (allRequestParam.containsKey("apiKeyId")) {
      stream = Stream.of(
          apiKeyService.getById(
            Integer.parseInt(allRequestParam.get("apiKeyId"))
          )
        );
    } else {
      stream = apiKeyService
                .getAll()
                .stream();
    }

    return new ResponseEntity<>(
          s
          .map(fillApiKey)
          .collect(Collectors.toList), 
        HttpStatus.OK
      );
  }
}
