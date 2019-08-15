package innexgo;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.csv.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ApiController {

  @Autowired ApiKeyService apiKeyService;
  @Autowired CourseService courseService;
  @Autowired EncounterService encounterService;
  @Autowired IrregularityService irregularityService;
  @Autowired LocationService locationService;
  @Autowired PeriodService periodService;
  @Autowired ScheduleService scheduleService;
  @Autowired SessionService sessionService;
  @Autowired StudentService studentService;
  @Autowired UserService userService;

  static final ResponseEntity<?> BAD_REQUEST = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  static final ResponseEntity<?> INTERNAL_SERVER_ERROR =
      new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
  static final ResponseEntity<?> OK = new ResponseEntity<>(HttpStatus.OK);
  static final ResponseEntity<?> UNAUTHORIZED = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
  static final ResponseEntity<?> NOT_FOUND = new ResponseEntity<>(HttpStatus.NOT_FOUND);

  Integer parseInteger(String str) {
    if (str == null) {
      return null;
    } else {
      try {
        return Integer.parseInt(str);
      } catch (NumberFormatException e) {
        return null;
      }
    }
  }

  Long parseLong(String str) {
    if (str == null) {
      return null;
    } else {
      try {
        return Long.parseLong(str);
      } catch (NumberFormatException e) {
        return null;
      }
    }
  }

  Boolean parseBoolean(String str) {
    if (str == null) {
      return null;
    } else {
      try {
        return Boolean.parseBoolean(str);
      } catch (NumberFormatException e) {
        return null;
      }
    }
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

  Irregularity fillIrregularity(Irregularity irregularity) {
    irregularity.course = fillCourse(courseService.getById(irregularity.courseId));
    irregularity.period = fillPeriod(periodService.getById(irregularity.periodId));
    irregularity.student = fillStudent(studentService.getById(irregularity.studentId));
    return irregularity;
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

  Session fillSession(Session session) {
    session.inEncounter = fillEncounter(encounterService.getById(session.inEncounterId));
    if (session.outEncounterId != 0) {
      session.outEncounter = fillEncounter(encounterService.getById(session.outEncounterId));
    }
    session.course = fillCourse(courseService.getById(session.courseId));
    return session;
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
      if (apiKey.expirationTime > System.currentTimeMillis()) {
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
      @RequestParam("expirationTime") Long expirationTime,
      @RequestParam("password") String password) {

    // if they gave a username instead of a userId
    if (userId == -1 && !Utils.isEmpty(email)) {

      // if the email is registered
      if (userService.existsByEmail(email)) {
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
        apiKey.creationTime = System.currentTimeMillis();
        apiKey.expirationTime = expirationTime;
        apiKey.key = Utils.generateKey();
        apiKey.keyHash = Utils.encodeApiKey(apiKey.key);
        apiKeyService.add(apiKey);
        return new ResponseEntity<>(fillApiKey(apiKey), HttpStatus.OK);
      } else {
        return UNAUTHORIZED;
      }
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
        && locationService.existsById(locationId)
        && userService.exists(teacherId)
        && isAdministrator(apiKey)) {
      Course course = new Course();
      course.teacherId = teacherId;
      course.locationId = locationId;
      course.period = period;
      course.subject = subject;
      course.year = Utils.getCurrentGraduatingYear();
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
      @RequestParam(value = "courseId", defaultValue = "-1") Integer courseId,
      @RequestParam("apiKey") String apiKey) {
    if (isTrusted(apiKey)) {
      if (locationService.existsById(locationId)
          && studentService.exists(studentId)
          && (courseId == -1 ? true : courseService.existsById(courseId))) {
        Encounter encounter = new Encounter();
        encounter.locationId = locationId;
        encounter.studentId = studentId;
        encounter.time = System.currentTimeMillis();
        encounterService.add(encounter);

        if (courseId != -1) {
          // search for open session with this student
          List<Session> openSessions =
              sessionService.query(
                  null, // id
                  null, // in encounter id
                  null, // out encounter id
                  null, // any encounter id
                  courseId, // course id
                  false, // complete
                  null, // location id
                  studentId, // student id
                  null, // time
                  null, // in time begin
                  null, // in time end
                  null, // out time begin
                  null // out time end
                  );
          if (openSessions.size() > 0) {
            // complete session
            Session session = openSessions.get(0);
            session.outEncounterId = encounter.id;
            session.complete = true;
            sessionService.update(session);
          } else {
            // make new open session
            Session session = new Session();
            session.courseId = courseId;
            session.complete = false;
            session.inEncounterId = encounter.id;
            sessionService.add(session);
          }
        }
        // return the filled encounter on success
        return new ResponseEntity<>(fillEncounter(encounter), HttpStatus.OK);
      } else {
        return BAD_REQUEST;
      }
    } else {
      return UNAUTHORIZED;
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
      @RequestParam("startTime") Long startTime,
      @RequestParam("endTime") Long endTime,
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
    if (isTrusted(apiKey)
        && studentService.exists(studentId)
        && courseService.existsById(courseId)
        && scheduleService
                .query(null, studentId, null, null, null, courseService.getById(courseId).period)
                .size()
            == 0) {
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
      @RequestParam("graduatingYear") Integer graduatingYear,
      @RequestParam("name") String name,
      @RequestParam(value = "tags", defaultValue = "") String tags,
      @RequestParam("apiKey") String apiKey) {
    if (!studentService.exists(id) && isAdministrator(apiKey)) {
      Student student = new Student();
      student.id = id;
      student.graduatingYear = graduatingYear;
      student.name = name;
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
        && ring == UserService.ADMINISTRATOR
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

  @RequestMapping("student/update")
  public ResponseEntity<?> updateStudent(@RequestParam Map<String, String> allRequestParam) {
    if ( // make sure changer is admin
    isAdministrator(allRequestParam.getOrDefault("apiKey", "invalid"))
        // make sure student exists
        && studentService.exists(parseInteger(allRequestParam.getOrDefault("studentId", "-1")))
        // if they are trying to set a name, it cannot be blank
        && !Utils.isEmpty(allRequestParam.getOrDefault("studentName", "default"))
        // if they are setting the graduating year, it must be a valid integer
        && parseInteger(allRequestParam.getOrDefault("graduatingYear", "0")) != null) {
      Student student = studentService.getById(parseInteger(allRequestParam.get("studentId")));

      // if it is specified, set the name
      if (allRequestParam.containsKey("studentName")) {
        student.name = allRequestParam.get("studentName");
      }

      // if it is specified, set the tags
      if (allRequestParam.containsKey("tags")) {
        student.tags = allRequestParam.get("tags");
      }

      // if it is specified, set the graduatingYear
      if (allRequestParam.containsKey("graduatingYear")) {
        student.graduatingYear = parseInteger(allRequestParam.get("graduatingYear"));
      }

      studentService.update(student);
      return new ResponseEntity<>(fillStudent(student), HttpStatus.OK);
    } else {
      return BAD_REQUEST;
    }
  }

  @RequestMapping("user/update/")
  public ResponseEntity<?> updateUser(@RequestParam Map<String, String> allRequestParam) {
    if ( // make sure changer is admin
    isAdministrator(allRequestParam.getOrDefault("apiKey", "invalid"))
        // make sure user exists
        && userService.exists(parseInteger(allRequestParam.getOrDefault("userId", "-1")))
        // if they are trying to set a name, it cannot be blank
        && !Utils.isEmpty(allRequestParam.getOrDefault("userName", "default"))
        // if they are trying to set a password, it cannot be blank
        && !Utils.isEmpty(allRequestParam.getOrDefault("password", "default"))
        // if they are trying to set an email, it cannot be blank
        && !Utils.isEmpty(allRequestParam.getOrDefault("email", "default"))
        // if they are trying to set an email, it cannot be taken already
        && (allRequestParam.containsKey("email") ? !userService.existsByEmail("email") : true)
        // if they are trying to set the ring, it must be a valid integer
        && parseInteger(allRequestParam.getOrDefault("ring", "0")) != null) {
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
  @RequestMapping("user/updatePassword/")
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

  // This method updates the prefstring for same user only
  @RequestMapping("user/updatePrefs/")
  public ResponseEntity<?> updatePrefs(
      @RequestParam("userId") Integer userId,
      @RequestParam("prefstring") String prefstring,
      @RequestParam("apiKey") String apiKey) {
    if (!Utils.isEmpty(apiKey) && userService.exists(userId)) {
      User apiUser = getUserIfValid(apiKey);
      User user = userService.getById(userId);
      if (apiUser != null && apiUser.id == user.id) {
        user.prefstring = prefstring;
        userService.update(user);
        return new ResponseEntity<>(fillUser(user), HttpStatus.OK);
      }
    }
    return BAD_REQUEST;
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
      return new ResponseEntity<>(fillCourse(courseService.delete(courseId)), HttpStatus.OK);
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
                  parseLong(allRequestParam.get("minCreationTime")),
                  parseLong(allRequestParam.get("maxCreationTime")),
                  allRequestParam.containsKey("apiKeyData")
                      ? Utils.encodeApiKey(allRequestParam.get("apiKeyData"))
                      : null)
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
    String apiKey = allRequestParam.get("apiKey");
    if (!Utils.isEmpty(apiKey) && isTrusted(apiKey)) {
      List<Course> els =
          courseService
              .query(
                  parseInteger(allRequestParam.get("courseId")),
                  parseInteger(allRequestParam.get("teacherId")),
                  parseInteger(allRequestParam.get("locationId")),
                  parseInteger(allRequestParam.get("studentId")),
                  allRequestParam.get("subject"),
                  parseInteger(
                      allRequestParam.getOrDefault(
                          "year", Integer.toString(Utils.getCurrentGraduatingYear()))))
              .stream()
              .map(x -> fillCourse(x))
              .collect(Collectors.toList());
      return new ResponseEntity<>(els, HttpStatus.OK);
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
                  parseLong(allRequestParam.get("minTime")),
                  parseLong(allRequestParam.get("maxTime")),
                  allRequestParam.get("studentName"))
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
      List<Location> list =
          locationService
              .query(
                  parseInteger(allRequestParam.get("locationId")),
                  allRequestParam.get("name"),
                  allRequestParam.get("tags"))
              .stream()
              .map(x -> fillLocation(x))
              .collect(Collectors.toList());
      return new ResponseEntity<>(list, HttpStatus.OK);
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
                  parseLong(allRequestParam.get("minTime")),
                  parseLong(allRequestParam.get("maxTime")),
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

  @RequestMapping("session/")
  public ResponseEntity<?> viewSession(@RequestParam Map<String, String> allRequestParam) {
    if (allRequestParam.containsKey("apiKey") && isTrusted(allRequestParam.get("apiKey"))) {
      List<Session> list =
          sessionService
              .query(
                  parseInteger(allRequestParam.get("id")),
                  parseInteger(allRequestParam.get("inEncounterId")),
                  parseInteger(allRequestParam.get("outEncounterId")),
                  parseInteger(allRequestParam.get("anyEncounterId")),
                  parseInteger(allRequestParam.get("courseId")),
                  parseBoolean(allRequestParam.get("complete")),
                  parseInteger(allRequestParam.get("locationId")),
                  parseInteger(allRequestParam.get("studentId")),
                  parseLong(allRequestParam.get("time")),
                  parseLong(allRequestParam.get("inTimeBegin")),
                  parseLong(allRequestParam.get("inTimeEnd")),
                  parseLong(allRequestParam.get("outTimeBegin")),
                  parseLong(allRequestParam.get("outTimeEnd")))
              .stream()
              .map(x -> fillSession(x))
              .collect(Collectors.toList());
      return new ResponseEntity<>(list, HttpStatus.OK);
    } else {
      return BAD_REQUEST;
    }
  }

  @RequestMapping("student/")
  public ResponseEntity<?> viewStudent(@RequestParam Map<String, String> allRequestParam) {
    if (allRequestParam.containsKey("apiKey") && isTrusted(allRequestParam.get("apiKey"))) {
      List<Student> list =
          studentService
              .query(
                  parseInteger(allRequestParam.get("studentId")),
                  parseInteger(allRequestParam.get("graduatingYear")),
                  allRequestParam.get("name"),
                  allRequestParam.get("tags"),
                  parseInteger(allRequestParam.get("courseId")))
              .stream()
              .map(x -> fillStudent(x))
              .collect(Collectors.toList());
      return new ResponseEntity<>(list, HttpStatus.OK);
    } else {
      return BAD_REQUEST;
    }
  }

  @RequestMapping("user/")
  public ResponseEntity<?> viewUser(@RequestParam Map<String, String> allRequestParam) {
    if (allRequestParam.containsKey("apiKey") && isTrusted(allRequestParam.get("apiKey"))) {
      List<User> list =
          userService
              .query(
                  parseInteger(allRequestParam.get("userId")),
                  allRequestParam.get("name"),
                  allRequestParam.get("email"),
                  parseInteger(allRequestParam.get("ring")))
              .stream()
              .map(x -> fillUser(x))
              .collect(Collectors.toList());
      return new ResponseEntity<>(list, HttpStatus.OK);
    } else {
      return BAD_REQUEST;
    }
  }

  // Special Methods

  @RequestMapping("validate/")
  public ResponseEntity<?> validateTrusted(@RequestParam("apiKey") String apiKey) {
    return isTrusted(apiKey) ? OK : UNAUTHORIZED;
  }

  @RequestMapping("batchUploadStudent/")
  public ResponseEntity<?> batchUploadStudent(
      @RequestParam("file") MultipartFile file, @RequestParam("apiKey") String apiKey) {
    if (isAdministrator(apiKey)) {
      try {
        CSVParser parser =
            CSVFormat.newFormat('\t')
                .parse(new InputStreamReader(new ByteArrayInputStream(file.getBytes()), "UTF8"));
        int currentGraduatingYear = Utils.getCurrentGraduatingYear();
        for (CSVRecord record : parser) {
          Integer id = parseInteger(record.get(2));
          Integer graduatingYear = currentGraduatingYear + (12 - parseInteger(record.get(4)));
          String name = record.get(1) + ' ' + record.get(0);
          if (id != null && graduatingYear != null && name != null) {
            newStudent(id, graduatingYear, name, "", apiKey);
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      return OK;
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("batchSetSchedule/")
  public ResponseEntity<?> batchSetCourse(
      @RequestParam("courseId") Integer courseId,
      @RequestParam("file") MultipartFile file,
      @RequestParam("apiKey") String apiKey) {
    if (isTrusted(apiKey)) {
      try {
        CSVParser parser =
            CSVFormat.newFormat('\t')
                .parse(new InputStreamReader(new ByteArrayInputStream(file.getBytes()), "UTF8"));
        int currentGraduatingYear = Utils.getCurrentGraduatingYear();
        for (CSVRecord record : parser) {
          Integer id = parseInteger(record.get(2));
          if (id != null) {
            newSchedule(id, courseId, apiKey);
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      return OK;
    } else {
      return UNAUTHORIZED;
    }
  }

  /* SPECIAL METHODS */

  @RequestMapping("getCourseStatus/")
  public ResponseEntity<?> getCurrentStatus(
      @RequestParam("courseId") Integer courseId,
      @RequestParam("periodId") Integer periodId,
      @RequestParam("apiKey") String apiKey) {

    // first ensure authorization
    if (isTrusted(apiKey)) {
      // then ensure that the course and period specified are valid
      if (courseService.existsById(courseId) && periodService.existsById(periodId)) {
        // load them
        Course course = courseService.getById(courseId);
        Period period = periodService.getById(periodId);
        // ensure that it is self consistent
        if (course.period == period.period) {
          // get a list of students
          return new ResponseEntity<>(
              studentService
                  .query(
                      null, // id
                      null, // graduatingYear
                      null, // name
                      null, // tags
                      courseId // courseId
                      )
                  .stream()
                  .map(
                      s ->
                          new Object() {
                            public Student student = fillStudent(s);
                            public String status =
                                sessionService
                                            .query(
                                                null, // id
                                                null, // in encounter id
                                                null, // out encounter id
                                                null, // any encounter id
                                                courseId, // course id
                                                null, // complete
                                                null, // location id
                                                s.id, // student id
                                                period.startTime, // time
                                                null, // in time begin
                                                null, // in time end
                                                null, // out time begin
                                                null // out time end
                                                )
                                            .size()
                                        != 0
                                    ? "present" // return present if there is a session
                                    : sessionService
                                                .query(
                                                    null, // id
                                                    null, // in encounter id
                                                    null, // out encounter id
                                                    null, // any encounter id
                                                    courseId, // course id
                                                    null, // complete
                                                    null, // location id
                                                    s.id, // student id
                                                    null, // time
                                                    period.startTime, // in time begin
                                                    period.endTime, // in time end
                                                    null, // out time begin
                                                    null // out time end
                                                    )
                                                .size()
                                            != 0
                                        ? "tardy" // return tardy if there is such a session
                                        : "absent";
                          })
                  .collect(Collectors.toList()),
              HttpStatus.OK);
        }
      }
      return BAD_REQUEST;
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("populatePeriods")
  public ResponseEntity<?> populatePeriods() {
    long initialTime = System.currentTimeMillis();
    for (int i = 0; i < 1000; i++) {
      Period period = new Period();
      period.startTime = initialTime + i * 60_000;
      period.endTime = (long) (initialTime + ((i + 0.7) * 60_000));
      period.period = i % 2 + 1;
      periodService.add(period);
    }
    return OK;
  }
}
