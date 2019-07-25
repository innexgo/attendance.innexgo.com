package innexo;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InnexoApiController {

  @Autowired ApiKeyService apiKeyService;
  @Autowired CourseService courseService;
  @Autowired EncounterService encounterService;
  @Autowired LocationService locationService;
  @Autowired PeriodService periodService;
  @Autowired ScheduleService scheduleService;
  @Autowired StudentService studentService;
  @Autowired UserService userService;

  static final ResponseEntity<?> BAD_REQUEST = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  static final ResponseEntity<?> INTERNAL_SERVER_ERROR =
      new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
  static final ResponseEntity<?> OK = new ResponseEntity<>(HttpStatus.OK);
  static final ResponseEntity<?> NOT_FOUND = new ResponseEntity<>(HttpStatus.NOT_FOUND);

  Integer parseInteger(String str) {
    return str == null ? null : Integer.parseInt(str);
  }

  Boolean parseBoolean(String str) {
    return str == null ? null : Boolean.parseBoolean(str);
  }

  ApiKey fillApiKey(ApiKey apiKey) {
    apiKey.user = fillUser(userService.getById(apiKey.userId));
    return apiKey;
  }

  Course fillCourse(Course course) {
    course.teacher = fillUser(userService.getById(course.teacherId));
    course.location = fillLocation(locationService.getById(course.locationId));
    return course;
  }

  Encounter fillEncounter(Encounter encounter) {
    encounter.location = fillLocation(locationService.getById(encounter.locationId));
    encounter.student = fillStudent(studentService.getById(encounter.studentId));
    return encounter;
  }

  Location fillLocation(Location location) {
    return location;
  }


  Period fillPeriod(Period period) {
    return period;
  }

  Schedule fillSchedule(Schedule schedule) {
    schedule.student = fillStudent(studentService.getById(schedule.studentId));
    schedule.course = fillCourse(courseService.getById(schedule.courseId));
    return schedule;
  }

  Student fillStudent(Student student) {
    return student;
  }

  User fillUser(User user) {
    return user;
  }

  User getUserIfValid(String key) {
    String hash = Utils.encodeApiKey(key);
    if (apiKeyService.existsByKeyHash(hash)) {
      ApiKey apiKey = apiKeyService.getByKeyHash(hash);
      if (apiKey.expirationTime > Instant.now().getEpochSecond()) {
        if (userService.exists(apiKey.userId)) {
          return userService.getById(apiKey.userId);
        }
      }
    }
    return null;
  }

  boolean isAdministrator(String key) {
    User user = getUserIfValid(key);
    return user != null && (user.ring == UserService.ADMINISTRATOR);
  }

  boolean isTrusted(String key) {
    User user = getUserIfValid(key);
    return user != null && (user.ring <= UserService.TEACHER);
  }

  @RequestMapping("apiKey/new/")
  public ResponseEntity<?> newApiKey(
      @RequestParam(value = "userId", defaultValue = "-1") Integer userId,
      @RequestParam(value = "email", defaultValue = "") String email,
      @RequestParam("expirationTime") Integer expirationTime,
      @RequestParam("password") String password) {

    // if they gave a username instead of a userId
    if (userId == -1 && !Utils.isEmpty(email)) {

      // if the email is registered
      if(userService.existsByEmail(email)) {
        // get email
        userId = userService.getByEmail(email).id;
      }
    }

    // now actually make apiKey
    if (userService.exists(userId)) {
      User u = userService.getById(userId);
      if (Utils.matchesPassword(password, u.passwordHash)) {
        ApiKey apiKey = new ApiKey();
        apiKey.userId = userId;
        apiKey.creationTime = (int) Instant.now().getEpochSecond();
        apiKey.expirationTime = expirationTime;
        apiKey.key = Utils.generateKey();
        apiKey.keyHash = Utils.encodeApiKey(apiKey.key);
        apiKeyService.add(apiKey);
        return new ResponseEntity<>(fillApiKey(apiKey), HttpStatus.OK);
      }
    } else {
    }
    return BAD_REQUEST;
  }

  @RequestMapping("course/new/")
  public ResponseEntity<?> newCourse(
      @RequestParam("userId") Integer teacherId,
      @RequestParam("locationId") Integer locationId,
      @RequestParam("period") Integer period,
      @RequestParam("subject") String subject,
      @RequestParam("apiKey") String apiKey) {
    if (!Utils.isEmpty(subject)
        && locationService.exists(locationId)
        && userService.exists(teacherId)
        && isAdministrator(apiKey)) {
      Course course = new Course();
      course.teacherId = teacherId;
      course.locationId = locationId;
      course.period = period;
      course.subject = subject;
      courseService.add(course);
      // return the filled course on success
      return new ResponseEntity<>(fillCourse(course), HttpStatus.OK);
    } else {
      return BAD_REQUEST;
    }
  }

  @RequestMapping("encounter/new/")
  public ResponseEntity<?> newEncounter(
      @RequestParam("studentId") Integer studentId,
      @RequestParam("locationId") Integer locationId,
      @RequestParam(value="courseId", defaultValue="-1") Integer courseId,
      @RequestParam("type") String type,
      @RequestParam("apiKey") String apiKey) {
    if (!Utils.isEmpty(type)
        && locationService.exists(locationId)
        && studentService.exists(studentId)
        && (courseId == -1 ? true : courseService.exists(courseId))
        && !Utils.isEmpty(apiKey)
        && isTrusted(apiKey)) {
      Encounter encounter = new Encounter();
      encounter.locationId = locationId;
      encounter.studentId = studentId;
      encounter.courseId = courseId == -1 ? null : courseId;
      encounter.studentId = studentId;
      encounter.time = (int) Instant.now().getEpochSecond();
      encounter.type = type;
      encounterService.add(encounter);
      // return the filled encounter on success
      return new ResponseEntity<>(fillEncounter(encounter), HttpStatus.OK);
    } else {
      return BAD_REQUEST;
    }
  }

  @RequestMapping("location/new/")
  public ResponseEntity<?> newLocation(
      @RequestParam("startTime") String name,
      @RequestParam("tags") String tags,
      @RequestParam("apiKey") String apiKey) {
    if (!Utils.isEmpty(name) && !Utils.isEmpty(tags) && isAdministrator(apiKey)) {
      Location location = new Location();
      location.name = name;
      location.tags = tags;
      locationService.add(location);
      return new ResponseEntity<>(fillLocation(location), HttpStatus.OK);
    } else {
      return BAD_REQUEST;
    }
  }

  @RequestMapping("period/new/")
  public ResponseEntity<?> newPeriod(
      @RequestParam("startTime") Integer startTime,
      @RequestParam("endTime") Integer endTime,
      @RequestParam("period") Integer period,
      @RequestParam("apiKey") String apiKey) {
    if (isAdministrator(apiKey)) {
      Period p = new Period();
      p.startTime = startTime;
      p.endTime = endTime;
      p.period = period;
      periodService.add(p);
      return new ResponseEntity<>(fillPeriod(p), HttpStatus.OK);
    } else {
      return BAD_REQUEST;
    }
  }

  @RequestMapping("schedule/new/")
  public ResponseEntity<?> newSchedule(
      @RequestParam("studentId") Integer studentId,
      @RequestParam("courseId") Integer courseId,
      @RequestParam("apiKey") String apiKey) {
    if (studentService.exists(studentId)
        && courseService.exists(courseId)
        && isTrusted(apiKey)) {
      Schedule schedule = new Schedule();
      schedule.studentId = studentId;
      schedule.courseId = courseId;
      scheduleService.add(schedule);
      return new ResponseEntity<>(fillSchedule(schedule), HttpStatus.OK);
    } else {
      return BAD_REQUEST;
    }
  }

  @RequestMapping("student/new/")
  public ResponseEntity<?> newStudent(
      @RequestParam("studentId") Integer id,
      @RequestParam(value="tags", defaultValue="") String tags,
      @RequestParam("apiKey") String apiKey) {
    if (!studentService.exists(id)
        && isAdministrator(apiKey)) {
      Student student = new Student();
      student.id = id;
      student.tags = tags;
      studentService.add(student);
      return new ResponseEntity<>(fillStudent(student), HttpStatus.OK);
    } else {
      return BAD_REQUEST;
    }
  }

  @RequestMapping("user/new/")
  public ResponseEntity<?> newUser(
      @RequestParam("userName") String name,
      @RequestParam("email") String email,
      @RequestParam("password") String password,
      @RequestParam("ring") Integer ring,
      @RequestParam("prefstring") String prefstring,
      @RequestParam("apiKey") String apiKey) {
    if (!Utils.isEmpty(name)
        && !Utils.isEmpty(password)
        && !Utils.isEmpty(email)
        && ring <= UserService.TEACHER
        && !userService.existsByEmail(email)
        && isAdministrator(apiKey)) {
      User u = new User();
      u.name = name;
      u.email = email;
      u.passwordHash = Utils.encodePassword(password);
      u.ring = ring;
      u.prefstring = prefstring;
      userService.add(u);
      return new ResponseEntity<>(fillUser(u), HttpStatus.OK);
    } else {
      return BAD_REQUEST;
    }
  }

  @RequestMapping("user/update/")
  public ResponseEntity<?> updateUser(@RequestParam Map<String, String> allRequestParam) {
    if (   // make sure changer is admin
        isAdministrator(allRequestParam.getOrDefault("apiKey","invalid"))
           // make sure user exists
        && userService.exists(parseInteger(allRequestParam.getOrDefault("userId","-1")))
           // if they are trying to set a name, it cannot be blank
        && !Utils.isEmpty(allRequestParam.getOrDefault("userName", "default"))
           // if they are trying to set a password, it cannot be blank
        && !Utils.isEmpty(allRequestParam.getOrDefault("password", "default"))
           // if they are trying to set an email, it cannot be blank
        && !Utils.isEmpty(allRequestParam.getOrDefault("email", "default"))
           // if they are trying to set an email, it cannot be taken already
        && (allRequestParam.containsKey("email") ? !userService.existsByEmail("email") : true)
    ) {
      User user = userService.getById(parseInteger(allRequestParam.get("userId")));

      // if it is specified, set the name
      if (allRequestParam.containsKey("userName")) {
        user.name = allRequestParam.get("userName");
      }
      // if it is specified, set the password
      if (allRequestParam.containsKey("password")) {
        user.passwordHash = Utils.encodePassword(allRequestParam.get("password"));
      }

      // if it is specified, set the email
      if (allRequestParam.containsKey("email")) {
        user.email = allRequestParam.get("email");
      }

      // if it is specified, set the ring level
      if (allRequestParam.containsKey("ring")) {
        user.ring = parseInteger(allRequestParam.get("ring"));
      }

      userService.update(user);
      return new ResponseEntity<>(fillUser(user), HttpStatus.OK);
    } else {
      return BAD_REQUEST;
    }
  }

  // This method updates the password for same user only
  @RequestMapping("user/updatePassword")
  public ResponseEntity<?> updatePassword(
      @RequestParam("userId") Integer userId,
      @RequestParam("oldPassword") String oldPassword,
      @RequestParam("newPassword") String newPassword) {
    if (!Utils.isEmpty(oldPassword)
        && !Utils.isEmpty(newPassword)
        && userService.exists(userId)
        && Utils.matchesPassword(oldPassword, userService.getById(userId).passwordHash)) {
      User user = userService.getById(userId);
      user.passwordHash = Utils.encodePassword(newPassword);
      userService.update(user);
      return new ResponseEntity<>(fillUser(user), HttpStatus.OK);
    } else {
      return BAD_REQUEST;
    }
  }

  @RequestMapping("apiKey/delete/")
  public ResponseEntity<?> deleteApiKey(
      @RequestParam("apiKeyId") Integer apiKeyId, @RequestParam("apiKey") String apiKey) {
    if (isAdministrator(apiKey)) {
      return new ResponseEntity<>(fillApiKey(apiKeyService.delete(apiKeyId)), HttpStatus.OK);
    } else {
      return BAD_REQUEST;
    }
  }

  @RequestMapping("course/delete/")
  public ResponseEntity<?> deleteCourse(
      @RequestParam("courseId") Integer courseId, @RequestParam("apiKey") String apiKey) {
    if (isAdministrator(apiKey)) {
      return new ResponseEntity<>(
          fillCourse(courseService.delete(courseId)), HttpStatus.OK);
    } else {
      return BAD_REQUEST;
    }
  }

  @RequestMapping("encounter/delete/")
  public ResponseEntity<?> deleteEncounter(
      @RequestParam("encounterId") Integer encounterId, @RequestParam("apiKey") String apiKey) {
    if (isAdministrator(apiKey)) {
      return new ResponseEntity<>(
          fillEncounter(encounterService.delete(encounterId)), HttpStatus.OK);
    } else {
      return BAD_REQUEST;
    }
  }

  @RequestMapping("location/delete/")
  public ResponseEntity<?> deleteLocation(
      @RequestParam("locationId") Integer locationId, @RequestParam("apiKey") String apiKey) {
    if (isAdministrator(apiKey)) {
      return new ResponseEntity<>(fillLocation(locationService.delete(locationId)), HttpStatus.OK);
    } else {
      return BAD_REQUEST;
    }
  }

  @RequestMapping("period/delete/")
  public ResponseEntity<?> deletePeriod(
      @RequestParam("periodId") Integer periodId, @RequestParam("apiKey") String apiKey) {
    if (isAdministrator(apiKey)) {
      return new ResponseEntity<>(fillPeriod(periodService.delete(periodId)), HttpStatus.OK);
    } else {
      return BAD_REQUEST;
    }
  }

  @RequestMapping("schedule/delete/")
  public ResponseEntity<?> deleteSchedule(
      @RequestParam("scheduleId") Integer scheduleId, @RequestParam("apiKey") String apiKey) {
    if (isTrusted(apiKey)) {
      return new ResponseEntity<>(fillSchedule(scheduleService.delete(scheduleId)), HttpStatus.OK);
    } else {
      return BAD_REQUEST;
    }
  }

  @RequestMapping("student/delete/")
  public ResponseEntity<?> deleteStudent(
      @RequestParam("studentId") Integer studentId, @RequestParam("apiKey") String apiKey) {
    if (isAdministrator(apiKey)) {
      return new ResponseEntity<>(fillStudent(studentService.delete(studentId)), HttpStatus.OK);
    } else {
      return BAD_REQUEST;
    }
  }

  @RequestMapping("user/delete/")
  public ResponseEntity<?> deleteUser(
      @RequestParam("userId") Integer userId, @RequestParam("apiKey") String apiKey) {
    if (isAdministrator(apiKey)) {
      return new ResponseEntity<>(fillUser(userService.delete(userId)), HttpStatus.OK);
    } else {
      return BAD_REQUEST;
    }
  }



  @RequestMapping("apiKey/")
  public ResponseEntity<?> viewApiKey(@RequestParam Map<String, String> allRequestParam) {
    if (allRequestParam.containsKey("apiKey") && isTrusted(allRequestParam.get("apiKey"))) {
      List<ApiKey> list =
          apiKeyService
              .query(
                  parseInteger(allRequestParam.get("apiKeyId")),
                  parseInteger(allRequestParam.get("userId")),
                  parseInteger(allRequestParam.get("minCreationTime")),
                  parseInteger(allRequestParam.get("maxCreationTime")),
                  Utils.encodeApiKey(allRequestParam.get("apiKeyData")))
              .stream()
              .map(x -> fillApiKey(x))
              .collect(Collectors.toList());
      return new ResponseEntity<>(list, HttpStatus.OK);
    } else {
      return BAD_REQUEST;
    }
  }

  @RequestMapping("course/")
  public ResponseEntity<?> viewCourse(@RequestParam Map<String, String> allRequestParam) {
    if (allRequestParam.containsKey("apiKey") && isTrusted(allRequestParam.get("apiKey"))) {
      if (allRequestParam.containsKey("courseId")) {
        return new ResponseEntity<>(
            Arrays.asList(courseService.getById(Integer.parseInt(allRequestParam.get("courseId")))),
            HttpStatus.OK);
      } else {
        return new ResponseEntity<>(courseService.getAll(), HttpStatus.OK);
      }
    } else {
      return BAD_REQUEST;
    }
  }

  @RequestMapping("encounter/")
  public ResponseEntity<?> viewEncounter(@RequestParam Map<String, String> allRequestParam) {
    String apiKey = allRequestParam.get("apiKey");
    if (!Utils.isEmpty(apiKey) && isTrusted(apiKey)) {
      List<Encounter> els =
          encounterService
              .query(
                  parseInteger(allRequestParam.get("count")),
                  parseInteger(allRequestParam.get("encounterId")),
                  parseInteger(allRequestParam.get("studentId")),
                  parseInteger(allRequestParam.get("locationId")),
                  parseInteger(allRequestParam.get("teacherId")),
                  parseInteger(allRequestParam.get("minTime")),
                  parseInteger(allRequestParam.get("maxTime")),
                  allRequestParam.get("studentName"),
                  allRequestParam.get("type"))
              .stream()
              .map(x -> fillEncounter(x))
              .collect(Collectors.toList());
      return new ResponseEntity<>(els, HttpStatus.OK);
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
          stream.map(x -> fillLocation(x)).collect(Collectors.toList()), HttpStatus.OK);
    } else {
      return BAD_REQUEST;
    }
  }

  @RequestMapping("period/")
  public ResponseEntity<?> viewPeriod(@RequestParam Map<String, String> allRequestParam) {
    String apiKey = allRequestParam.get("apiKey");
    if (isTrusted(apiKey)) {
      List<Period> els =
          periodService
              .query(
                  parseInteger(allRequestParam.get("periodId")),
                  parseInteger(allRequestParam.get("minTime")),
                  parseInteger(allRequestParam.get("maxtime")),
                  parseInteger(allRequestParam.get("period")),
                  parseInteger(allRequestParam.get("courseId")),
                  parseInteger(allRequestParam.get("teacherId")))
              .stream()
              .map(x -> fillPeriod(x))
              .collect(Collectors.toList());
      return new ResponseEntity<>(els, HttpStatus.OK);
    } else {
      return BAD_REQUEST;
    }
  }

  @RequestMapping("schedule/")
  public ResponseEntity<?> viewSchedule(@RequestParam Map<String, String> allRequestParam) {
    if (allRequestParam.containsKey("apiKey") && isTrusted(allRequestParam.get("apiKey"))) {
      List<Schedule> list =
          scheduleService
              .query(
                  parseInteger(allRequestParam.get("scheduleId")),
                  parseInteger(allRequestParam.get("studentId")),
                  parseInteger(allRequestParam.get("courseId")),
                  parseInteger(allRequestParam.get("teacherId")),
                  parseInteger(allRequestParam.get("locationId")),
                  parseInteger(allRequestParam.get("period")))
              .stream()
              .map(x -> fillSchedule(x))
              .collect(Collectors.toList());
      return new ResponseEntity<>(list, HttpStatus.OK);
    } else {
      return BAD_REQUEST;
    }
  }

  @RequestMapping("student/")
  public ResponseEntity<?> viewStudent(@RequestParam Map<String, String> allRequestParam) {
    if (allRequestParam.containsKey("apiKey") && isTrusted(allRequestParam.get("apiKey"))) {
      Stream<Student> stream = null;
      if (allRequestParam.containsKey("studentId")) {
        stream =
            Stream.of(studentService.getById(Integer.parseInt(allRequestParam.get("studentId"))));
      } else {
        stream = studentService.getAll().stream();
      }

      return new ResponseEntity<>(
          stream.map(x -> fillStudent(x)).collect(Collectors.toList()), HttpStatus.OK);
    } else {
      return BAD_REQUEST;
    }
  }


  @RequestMapping("user/")
  public ResponseEntity<?> viewUser(@RequestParam Map<String, String> allRequestParam) {
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

  @RequestMapping("validate/")
  public ResponseEntity<?> validateTrusted(@RequestParam("apiKey") String apiKey) {
    return isTrusted(apiKey) ? OK : BAD_REQUEST;
  }

  @RequestMapping("populatePeriods")
  public ResponseEntity<?> populatePeriods() {

    return OK;
  }
}
