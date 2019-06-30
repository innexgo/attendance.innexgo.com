package innexo;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
  Function<ApiKey, ApiKey> fillApiKey =
      (k) -> {
        k.creator = fillUser.apply(userService.getById(k.creatorId));
        return k;
      };

  Function<Encounter, Encounter> fillEncounter =
      (e) -> {
        e.location = fillLocation.apply(locationService.getById(e.locationId));
        e.user = fillUser.apply(userService.getById(e.userId));
        return e;
      };

  User getUserIfValid(String key) {
    if (apiKeyService.existsByKey(key)) {
      ApiKey apiKey = apiKeyService.getByKey(key);
      if (apiKey.expirationTime.getTime() > System.currentTimeMillis()) {
        if (userService.exists(apiKey.creatorId)) {
          return userService.getById(apiKey.creatorId);
        }
      }
    }
    return null;
  }

  boolean isAdministrator(String key) {
    User user = getUserIfValid(key);
    return user != null && user.administrator;
  }

  boolean isTrusted(String key) {
    User user = getUserIfValid(key);
    return user != null && (user.trustedUser || user.administrator);
  }

  @RequestMapping("encounter/new/")
  public ResponseEntity<?> newEncounter(
      @RequestParam("userId") Integer userId,
      @RequestParam("locationId") Integer locationId,
      @RequestParam("type") String type,
      @RequestParam("apiKey") String apiKey) {
    if (!Utils.isBlank(type)
        && locationService.exists(locationId)
        && userService.exists(userId)
        && isTrusted(apiKey)) {
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

  @RequestMapping("user/new/")
  public ResponseEntity<?> newUser(
      @RequestParam("userId") Integer userId,
      @RequestParam(value = "managerId", defaultValue = "-1") Integer managerId,
      @RequestParam("name") String name,
      @RequestParam(value = "administrator", defaultValue = "false") Boolean administrator,
      @RequestParam(value = "trustedUser", defaultValue = "false") Boolean trustedUser,
      @RequestParam("password") String password,
      @RequestParam("apiKey") String apiKey) {
    if (!Utils.isBlank(name)
        && !Utils.isBlank(password)
        && !userService.exists(userId)
        && isAdministrator(apiKey)) {
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

  @RequestMapping("location/new/")
  public ResponseEntity<?> newLocation(
      @RequestParam("name") String name,
      @RequestParam("tags") String tags,
      @RequestParam("apiKey") String apiKey) {
    if (!Utils.isBlank(name) && !Utils.isBlank(tags) && isAdministrator(apiKey)) {
      Location location = new Location();
      location.name = name;
      location.tags = tags;
      locationService.add(location);
      return new ResponseEntity<>(fillLocation.apply(location), HttpStatus.OK);
    } else {
      return BAD_REQUEST;
    }
  }

  @RequestMapping("apiKey/new/")
  public ResponseEntity<?> newApiKey(
      @RequestParam("creatorId") Integer creatorId,
      @RequestParam("expirationTime") Integer expirationTime,
      @RequestParam("password") String password) {
    if (userService.exists(creatorId)) {
      User u = userService.getById(creatorId);
      BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
      if (encoder.matches(password, u.passwordHash)) {
        ApiKey apiKey = new ApiKey();
        apiKey.creatorId = creatorId;
        apiKey.creationTime = new Timestamp(System.currentTimeMillis());
        apiKey.expirationTime = new Timestamp((long) expirationTime * 1000); // epoch time to milis
        apiKey.keydata = UUID.randomUUID().toString(); // quick hacks, please replace
        apiKeyService.add(apiKey);
        return new ResponseEntity<>(fillApiKey.apply(apiKey), HttpStatus.OK);
      }
    }
    return BAD_REQUEST;
  }

  @RequestMapping("encounter/delete/")
  public ResponseEntity<?> deleteEncounter(
      @RequestParam("encounterId") Integer encounterId, @RequestParam("apiKey") String apiKey) {
    if (isAdministrator(apiKey)) {
      return new ResponseEntity<>(
          fillEncounter.apply(encounterService.delete(encounterId)), HttpStatus.OK);
    } else {
      return BAD_REQUEST;
    }
  }

  @RequestMapping("user/delete/")
  public ResponseEntity<?> deleteStudent(
      @RequestParam("userId") Integer userId, @RequestParam("apiKey") String apiKey) {
    if (isAdministrator(apiKey)) {
      return new ResponseEntity<>(fillUser.apply(userService.delete(userId)), HttpStatus.OK);
    } else {
      return BAD_REQUEST;
    }
  }

  @RequestMapping("location/delete/")
  public ResponseEntity<?> deleteLocation(
      @RequestParam("locationId") Integer locationId, @RequestParam("apiKey") String apiKey) {
    if (isAdministrator(apiKey)) {
      return new ResponseEntity<>(
          fillLocation.apply(locationService.delete(locationId)), HttpStatus.OK);
    } else {
      return BAD_REQUEST;
    }
  }

  @RequestMapping("apiKey/delete/")
  public ResponseEntity<?> deleteApiKey(
      @RequestParam("apiKeyId") Integer apiKeyId, @RequestParam("apiKey") String apiKey) {
    if (isAdministrator(apiKey)) {
      return new ResponseEntity<>(fillApiKey.apply(apiKeyService.delete(apiKeyId)), HttpStatus.OK);
    } else {
      return BAD_REQUEST;
    }
  }

  @RequestMapping("encounter/")
  public ResponseEntity<?> viewEncounter(@RequestParam Map<String, String> allRequestParam) {
    if (allRequestParam.containsKey("apiKey") && isTrusted(allRequestParam.get("apiKey"))) {
      List<Encounter> els =
          encounterService
              .query(
                  parseInteger.apply(allRequestParam.get("count")),
                  parseInteger.apply(allRequestParam.get("encounterId")),
                  parseInteger.apply(allRequestParam.get("userId")),
                  parseInteger.apply(allRequestParam.get("userManagerId")),
                  parseInteger.apply(allRequestParam.get("locationId")),
                  parseTimestamp.apply(allRequestParam.get("minTime")),
                  parseTimestamp.apply(allRequestParam.get("maxTime")),
                  allRequestParam.get("userName"),
                  allRequestParam.get("type"))
              .stream()
              .map(fillEncounter)
              .collect(Collectors.toList());
      return new ResponseEntity<>(els, HttpStatus.OK);
    } else {
      return BAD_REQUEST;
    }
  }

  @RequestMapping("user/")
  public ResponseEntity<?> viewStudent(@RequestParam Map<String, String> allRequestParam) {
    if (allRequestParam.containsKey("apiKey") && isTrusted(allRequestParam.get("apiKey"))) {
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
    } else {
      return BAD_REQUEST;
    }
  }

  @RequestMapping("location/")
  public ResponseEntity<?> viewLocation(@RequestParam Map<String, String> allRequestParam) {
    if (allRequestParam.containsKey("apiKey") && isTrusted(allRequestParam.get("apiKey"))) {
      Stream<Location> stream = null;
      if (allRequestParam.containsKey("locationId")) {
        stream =
            Stream.of(locationService.getById(Integer.parseInt(allRequestParam.get("locationId"))));
      } else {
        stream = locationService.getAll().stream();
      }

      return new ResponseEntity<>(
          stream.map(fillLocation).collect(Collectors.toList()), HttpStatus.OK);
    } else {
      return BAD_REQUEST;
    }
  }

  @RequestMapping("apiKey/")
  public ResponseEntity<?> viewApiKey(@RequestParam Map<String, String> allRequestParam) {
    if (allRequestParam.containsKey("apiKey") && isTrusted(allRequestParam.get("apiKey"))) {
      Stream<ApiKey> stream = null;
      if (allRequestParam.containsKey("apiKeyId")) {
        stream =
            Stream.of(apiKeyService.getById(Integer.parseInt(allRequestParam.get("apiKeyId"))));
      } else {
        stream = apiKeyService.getAll().stream();
      }

      return new ResponseEntity<>(
          stream.map(fillApiKey).collect(Collectors.toList()), HttpStatus.OK);
    } else {
      return BAD_REQUEST;
    }
  }
}
