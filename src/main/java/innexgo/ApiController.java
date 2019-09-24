package innexgo;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.csv.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ApiController {

  @Autowired ApiKeyService apiKeyService;
  @Autowired CardService cardService;
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

  /**
   * Parses string for Integer
   * @param   str - string to be parsed
   * @return        Integer parsed from str; null if the string is null or cannot be parsed
   */
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
  /**
   * Parses string for Long
   * @param   str - string to be parsed
   * @return        Long parsed from str; null if string is null or cannot be parsed
   */
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
  /**
   * Parses string for Boolean
   * @param   str - string to be parsed
   * @return        Boolean parsed from str; null if string is null or cannot be parsed
   */
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

  /**
   * Fills in jackson objects (User) for ApiKey
   * @param     apiKey - ApiKey object
   * @return    apiKey with filled jackson objects
   */
  ApiKey fillApiKey(ApiKey apiKey) {
    apiKey.user = fillUser(userService.getById(apiKey.userId));
    return apiKey;
  }

  /**
   * Fills in jackson objects(Student) in Card
   * @param    card - Card object
   * @return   Card object with filled jackson objects
   */
  Card fillCard(Card card) {
    card.student = fillStudent(studentService.getById(card.studentId));
    return card;
  }

  /**
   * Fills in jackson objects(Teacher and Location) in Course
   * @param   course - Course object
   * @return  Course object with filled jackson objects
   */
  Course fillCourse(Course course) {
    course.teacher = fillUser(userService.getById(course.teacherId));
    course.location = fillLocation(locationService.getById(course.locationId));
    return course;
  }

  /**
   * Fills in jackson objects(Location and Student) in Encounter
   * @param   encounter - Encounter object
   * @return  Encounter object with filled jackson objects
   */
  Encounter fillEncounter(Encounter encounter) {
    encounter.location = fillLocation(locationService.getById(encounter.locationId));
    encounter.student = fillStudent(studentService.getById(encounter.studentId));
    return encounter;
  }

  /**
   * Fills in jackson objects(Course, Period, and Student) in Irregularity
   * @param   irregularity - Irregularity object
   * @return  Irregularity object with filled jackson objects
   */
  Irregularity fillIrregularity(Irregularity irregularity) {
    irregularity.course = fillCourse(courseService.getById(irregularity.courseId));
    irregularity.period = fillPeriod(periodService.getById(irregularity.periodId));
    irregularity.student = fillStudent(studentService.getById(irregularity.studentId));
    return irregularity;
  }

  /**
   * Fills in jackson objects (none at the moment) for Location
   * @param   location - Location object
   * @return  Location object with filled jackson objects
   */
  Location fillLocation(Location location) {
    return location;
  }

  /**
   * Fills in jackson objects (none at the moment) for Period
   * @param   period - Period object
   * @return  Period object with filled jackson objects
   */
  Period fillPeriod(Period period) {
    return period;
  }

  /**
   * Fills in jackson objects(Student, Course) for Schedule
   * @param   schedule - Schedule object
   * @return  Schedule object with filled jackson objects
   */
  Schedule fillSchedule(Schedule schedule) {
    schedule.student = fillStudent(studentService.getById(schedule.studentId));
    schedule.course = fillCourse(courseService.getById(schedule.courseId));
    return schedule;
  }

  /**
   * Fills in jackson objects (Student, Course, inEncounter, and outEncounter) for Session
   * @param   session - Session object
   * @return  Session object with filled jackson objects
   */
  Session fillSession(Session session) {
    session.student = fillStudent(studentService.getById(session.studentId));
    session.course = fillCourse(courseService.getById(session.courseId));
    session.inEncounter = fillEncounter(encounterService.getById(session.inEncounterId));
    if (session.hasOut) {
      session.outEncounter = fillEncounter(encounterService.getById(session.outEncounterId));
    }
    return session;
  }

  /**
   * Fills in jackson objects (none at the moment) for Student
   * @param   student - Student object
   * @return  Student object with filled jackson objects
   */
  Student fillStudent(Student student) {
    return student;
  }

  /**
   * Fills in jackson objects (none at the moment) for User
   * @param   user - User object
   * @return  User object with filled jackson objects
   */
  User fillUser(User user) {
    return user;
  }

  /**
   * Returns a user if valid
   * @param   key - apikey code of the User
   * @return  User or null if invalid
   */
  User getUserIfValid(String key) {
    String hash = Utils.encodeApiKey(key);
    if (apiKeyService.existsByKeyHash(hash)) {
      ApiKey apiKey = apiKeyService.getByKeyHash(hash);
      if (apiKey.expirationTime > System.currentTimeMillis()) {
        if (userService.existsById(apiKey.userId)) {
          return userService.getById(apiKey.userId);
        }
      }
    }
    return null;
  }

  /**
   * Checks if a user is an administrator
   * @param   key - apikey code of a User
   * @return  true if administrator; false if not administrator or invalid
   */
  boolean isAdministrator(String key) {
    if (key == null) {
      return false;
    }
    User user = getUserIfValid(key);
    return user != null && (user.ring == UserService.ADMINISTRATOR);
  }

  /**
   * Checks whether a User is trusted
   * @param   key - apikey code of User
   * @return  true if User is trusted; false if User not trusted
   */
  boolean isTrusted(String key) {
    if (key == null) {
      return false;
    }
    User user = getUserIfValid(key);
    return user != null && (user.ring <= UserService.TEACHER);
  }

  /**
   * For all the courses at the current time, create irregularities
   *     If the student has not signed in yet before or during the period,
   *        generate an absent irregularity
   */
  @Scheduled(fixedDelay = 5000)
  public void irregularityGenerator() {
    // the list of periods that havent started yet
    List<Period> periodList =
        periodService.query(
            null, // id
            null, // time
            null, // minDuration
            null, // maxDuration
            null, // initialTimeBegin
            null, // initialTimeEnd
            System.currentTimeMillis(), // startTimeBegin
            null, // startTimeEnd
            null, // endTimeBegin
            null, // endTimeEnd
            null, // period
            null, // courseId
            null // teacherId
            );

    Collections.sort(periodList, Comparator.comparingLong(p -> p.startTime));

    for (int i = 0; i < periodList.size(); i++) {
      Period period = periodList.get(i);
      // wait till we are at the right time
      try {
        System.out.println((period.initialTime - System.currentTimeMillis()) / 1000);
        Thread.sleep(Math.max(0, period.initialTime - System.currentTimeMillis()));
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      // get courses at this period
      List<Course> courseList =
          courseService.query(
              null, // id
              null, // teacherId
              null, // locationId
              null, // studentId
              period.period, // period
              null, // subject
              null, // time
              Utils.getCurrentGraduatingYear() // year
            );

      // for all courses at this time
      for (Course course : courseList) {
        List<Student> studentAbsentList = studentService.absent(course.id, period.id);
        // mark all students not there as absent
        for (Student student : studentAbsentList) {
          boolean alreadyAbsent = irregularityService.query(
              null,
              student.id,
              null,
              period.id,
              null,
              "absent",
              null,
              null
            ).size() > 0;
          // if not already absent
          if(!alreadyAbsent) {
            Irregularity irregularity = new Irregularity();
            irregularity.studentId = student.id;
            irregularity.courseId = course.id;
            irregularity.periodId = period.id;
            irregularity.type = "absent";
            irregularity.time = period.startTime;
            irregularity.timeMissing = period.endTime - period.startTime;
            irregularityService.add(irregularity);
          }
        }
      }
    }
  }

  /**
   * Create a new apiKey for a User
   * @param userId the id of the User
   * @param email email of the User
   * @param expirationTime  //TODO what is expiration time?
   * @param password User password
   * @return ResponseEntity with ApiKey of User and HttpStatus.OK code if successful
   * @throws ResponseEntity with HttpStatus.UNAUTHORIZED if the User is unauthorized
   * @throws ResponseEntity with HttpStatus.BAD_REQUEST if the process is unsuccessful
   */
  @RequestMapping("apiKey/new/")
  public ResponseEntity<?> newApiKey(
      @RequestParam(value = "userId", defaultValue = "-1") Long userId,
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
    if (userService.existsById(userId)) {
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

  /**
   * Create a new Student ID Card and can be done by a trusted User
   * @param cardId - unique identifier of the card
   * @param studentId - student id (the id on the card given by the school)
   * @param apiKey - api key of the user trying to make a new card
   * @return ResponseEntity with a Card and HttpStatus.OK
   * @throws ResponseEntity with HttpStatus.UNAUTHORIZED if the User is unauthorized
   * @throws ResponseEntity with HttpStatus.BAD_REQUEST if the process if unsuccessful
   */
  @RequestMapping("card/new/")
  public ResponseEntity<?> newCard(
      @RequestParam("cardId") Long cardId,
      @RequestParam("studentId") Long studentId,
      @RequestParam("apiKey") String apiKey) {
    if(isTrusted(apiKey)) {
      if (studentService.existsById(studentId)
          && !cardService.existsById(cardId)) {
        Card card = new Card();
        card.id = cardId;
        card.studentId = studentId;
        cardService.add(card);
        return new ResponseEntity<>(fillCard(card), HttpStatus.OK);
      } else {
        return BAD_REQUEST;
      }
    } else {
      return UNAUTHORIZED;
    }
  }

  /**
   * Create a new course and can only be done by an administrator
   * @param teacherId - id of teacher that normally teaches the class
   * @param locationId - id of location where class is normally held
   * @param period - period of the class //TODO what are the values of period?
   * @param subject                      //TODO what are possible values of subject?
   * @param apiKey - of User creating new Course
   * @return ResponseEntity with a Course and HttpStatus.OK
   * @throws ResponseEntity with HttpStatus.UNAUTHORIZED if the User is not an administrator
   * @throws ResponseEntity with HttpStatus.BAD_REQUEST if the process if unsuccessful
   */
  @RequestMapping("course/new/")
  public ResponseEntity<?> newCourse(
      @RequestParam("userId") Long teacherId,
      @RequestParam("locationId") Long locationId,
      @RequestParam("period") Integer period,
      @RequestParam("subject") String subject,
      @RequestParam("apiKey") String apiKey) {
    if(isAdministrator(apiKey)) {
      if (!Utils.isEmpty(subject)
          && locationService.existsById(locationId)
          && userService.existsById(teacherId)) {
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
    } else {
      return UNAUTHORIZED;
    }
  }

  /**
   * Creates a new encounter and can be done by a trusted user
   * @param studentId - student id number
   * @param cardId - unique identifier for the card
   * @param locationId - location id of the location where this course is normally taught
   * @param courseId - course id
   * @param apiKey - apiKey of the user creating the encounter
   * @return ResponseEntity with Encounter and HttpStatus.OK
   * @throws ResponseEntity with HttpStatus.BAD_REQUEST if process is unsuccessful
   * @throws ResponseEntity with HttpStatus.UNAUTHORIZED if the User is not trusted
   */
  @RequestMapping("encounter/new/")
  public ResponseEntity<?> newEncounter(
      @RequestParam(value = "studentId", defaultValue = "-1") Long studentId,
      @RequestParam(value = "cardId", defaultValue = "-1") Long cardId,
      @RequestParam("locationId") Long locationId,
      @RequestParam(value = "courseId", defaultValue = "-1") Long courseId,
      @RequestParam("apiKey") String apiKey) {
    if (isTrusted(apiKey)) {
      Student student;
      if (cardService.existsById(cardId)) {
        student = studentService.getById(cardService.getById(cardId).studentId);
      } else if (studentService.existsById(studentId)) {
        student = studentService.getById(studentId);
      } else {
        return BAD_REQUEST;
      }

      if (locationService.existsById(locationId)
          && (courseId == -1 ? true : courseService.existsById(courseId))) {
        Encounter encounter = new Encounter();
        encounter.locationId = locationId;
        encounter.studentId = student.id;
        encounter.time = System.currentTimeMillis();
        encounterService.add(encounter);

        List<Period> plist =
            periodService.query(
                null, // id,
                System.currentTimeMillis(), // time,
                null, // minDuration,
                null, // maxDuration,
                null, // initialTimeBegin,
                null, // initialTimeEnd,
                null, // startTimeBegin,
                null, // startTimeEnd,
                null, // endTimeBegin,
                null, // endTimeEnd,
                null, // period,
                null, // courseId,
                null // teacherId
                );

        // get the current period if it exists
        Period currentPeriod = plist.size() == 0 ? null : plist.get(0);

        // check for sessions + irregularities
        if (currentPeriod != null && courseId != -1) {
          boolean newLogin = false;

          // search for open session with this student at the course
          List<Session> openSessions =
              sessionService.query(
                  null, // id
                  null, // in encounter id
                  null, // out encounter id
                  null, // any encounter id
                  courseId, // course id
                  false, // complete
                  null, // hasOut
                  null, // location id
                  student.id, // student id
                  null, // time
                  null, // in time begin
                  null, // in time end
                  null, // out time begin
                  null // out time end
                  );

          if(openSessions.size() == 0) {
            newLogin = true;
          }

          for(Session openSession : openSessions) {
            if(locationId ==
                encounterService.getById(openSession.inEncounterId).locationId) {
              // if it's at the same location
              openSession.outEncounterId = encounter.id;
              openSession.complete = true;
              openSession.hasOut = true;
              sessionService.update(openSession);

              // if it is in the middle of class, add a leaveEarly irregularity
              if (System.currentTimeMillis() < currentPeriod.endTime) {
                Irregularity irregularity = new Irregularity();
                irregularity.studentId = student.id;
                irregularity.courseId = courseId;
                irregularity.periodId = currentPeriod.id;
                irregularity.type =
                  System.currentTimeMillis() > currentPeriod.startTime
                    ? "left_early"
                    : "absent";
                irregularity.time = System.currentTimeMillis();
                irregularity.timeMissing = currentPeriod.endTime - System.currentTimeMillis();
                irregularityService.add(irregularity);
              }
            } else {
              // its not at the same location as the beginning
              // end that session
              openSession.complete = true;
              sessionService.update(openSession);
              newLogin = true;
            }
          }

          if (newLogin) {
            // make new open session
            Session session = new Session();
            session.studentId = student.id;
            session.courseId = courseId;
            session.complete = false;
            session.hasOut = false;
            session.inEncounterId = encounter.id;
            session.outEncounterId = 0;
            sessionService.add(session);

            // now we check if they arent there, and fix it
            List<Irregularity> irregularities =
                irregularityService.query(
                    null, // id
                    student.id, // studentId
                    courseId, // courseId
                    currentPeriod.id, // periodId
                    null, // teacherId
                    null, // type
                    null, // time
                    null // timeMissing
                    );

            for (Irregularity irregularity : irregularities) {
              if (irregularity.type.equals("absent")) {
                // if there is absence, convert it to a tardy or delete it
                if(System.currentTimeMillis() > currentPeriod.startTime) {
                  irregularity.type = "tardy";
                  irregularity.timeMissing = System.currentTimeMillis() - currentPeriod.startTime;
                  irregularityService.update(irregularity);
                } else {
                  // if they're present before the startTime
                  irregularityService.deleteById(irregularity.id);
                }
              } else if (irregularity.type.equals("left_early")) {
                // if there is a leftEarly, convert it to a leftTemporarily
                irregularity.type = "left_temporarily";
                irregularity.timeMissing = System.currentTimeMillis() - irregularity.time;
                irregularityService.update(irregularity);
              }
            }
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

  /**
   * Creates a new location and can only be done by an Administrator
   * @param name //TODO what do you mean by name
   * @param tags //TODO what do you mean by tags
   * @param apiKey - apiKey of the User creating the location
   * @return ResponseEntity with location and HttpStatus.OK
   * @throws ResponseEntity with HttpStatus.BAD_REQUEST if process if unsuccessful
   * @throws ResponseEntity with HttpStatus.UNAUTHORIZED if the User is not an administrator
   */
  @RequestMapping("location/new/")
  public ResponseEntity<?> newLocation(
      @RequestParam("name") String name,
      @RequestParam("tags") String tags,
      @RequestParam("apiKey") String apiKey) {
    if(isAdministrator(apiKey)) {
      if (!Utils.isEmpty(name) && !Utils.isEmpty(tags)) {
        Location location = new Location();
        location.name = name;
        location.tags = tags;
        locationService.add(location);
        return new ResponseEntity<>(fillLocation(location), HttpStatus.OK);
      } else {
        return BAD_REQUEST;
      }
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("period/new/")
  public ResponseEntity<?> newPeriod(
      @RequestParam("initialTime") Long initialTime,
      @RequestParam("startTime") Long startTime,
      @RequestParam("endTime") Long endTime,
      @RequestParam("period") Integer period,
      @RequestParam("apiKey") String apiKey) {
    if (isAdministrator(apiKey)) {
      Period p = new Period();
      p.initialTime = initialTime;
      p.startTime = startTime;
      p.endTime = endTime;
      p.period = period;
      periodService.add(p);
      return new ResponseEntity<>(fillPeriod(p), HttpStatus.OK);
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("schedule/new/")
  public ResponseEntity<?> newSchedule(
      @RequestParam("studentId") Long studentId,
      @RequestParam("courseId") Long courseId,
      @RequestParam("apiKey") String apiKey) {
    if (isTrusted(apiKey)) {
      if (studentService.existsById(studentId)
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
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("student/new/")
  public ResponseEntity<?> newStudent(
      @RequestParam("studentId") Long id,
      @RequestParam("graduatingYear") Integer graduatingYear,
      @RequestParam("name") String name,
      @RequestParam(value = "tags", defaultValue = "") String tags,
      @RequestParam("apiKey") String apiKey) {
    if(isAdministrator(apiKey)) {
      if (!studentService.existsById(id) && !Utils.isEmpty(name)) {
        Student student = new Student();
        student.id = id;
        student.graduatingYear = graduatingYear;
        student.name = name.toUpperCase();
        student.tags = tags;
        studentService.add(student);
        return new ResponseEntity<>(fillStudent(student), HttpStatus.OK);
      } else {
        return BAD_REQUEST;
      }
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("user/new/")
  public ResponseEntity<?> newUser(
      @RequestParam("userName") String name,
      @RequestParam("email") String email,
      @RequestParam("password") String password,
      @RequestParam("ring") Integer ring,
      @RequestParam("apiKey") String apiKey) {
    if (!isAdministrator(apiKey)) {
      if (!Utils.isEmpty(name)
          && !Utils.isEmpty(password)
          && !Utils.isEmpty(email)
          && !userService.existsByEmail(email)) {
        User u = new User();
        u.name = name;
        u.email = email;
        u.passwordHash = Utils.encodePassword(password);
        u.ring = ring;
        u.prefstring = "";
        userService.add(u);
        return new ResponseEntity<>(fillUser(u), HttpStatus.OK);
      } else {
        return BAD_REQUEST;
      }
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("student/update")
  public ResponseEntity<?> updateStudent(@RequestParam Map<String, String> allRequestParam) {
    // make sure changer is admin
    if (isAdministrator(allRequestParam.getOrDefault("apiKey", "invalid"))) {
      // make sure student exists
      // if they are trying to set a name, it cannot be blank
      // if they are setting the graduating year, it must be a valid integer
      if(studentService.existsById(parseInteger(allRequestParam.getOrDefault("studentId", "-1")))
          && !Utils.isEmpty(allRequestParam.getOrDefault("studentName", "default"))
          && parseLong(allRequestParam.getOrDefault("graduatingYear", "0")) != null) {
        Student student = studentService.getById(parseLong(allRequestParam.get("studentId")));

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
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("user/update/")
  public ResponseEntity<?> updateUser(@RequestParam Map<String, String> allRequestParam) {
    // make sure changer is admin

    if (isAdministrator(allRequestParam.getOrDefault("apiKey", "invalid"))) {
      // make sure user exists
      if(userService.existsById(parseLong(allRequestParam.getOrDefault("userId", "-1")))
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
        User user = userService.getById(parseLong(allRequestParam.get("userId")));

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
    } else {
      return UNAUTHORIZED;
    }
  }

  // This method updates the password for same user only
  @RequestMapping("user/updatePassword/")
  public ResponseEntity<?> updatePassword(
      @RequestParam("userId") Long userId,
      @RequestParam("oldPassword") String oldPassword,
      @RequestParam("newPassword") String newPassword) {
    if (!Utils.isEmpty(oldPassword)
        && userService.existsById(userId)
        && Utils.matchesPassword(oldPassword, userService.getById(userId).passwordHash)) {

      if(!Utils.isEmpty(newPassword)) {
        User user = userService.getById(userId);
        user.passwordHash = Utils.encodePassword(newPassword);
        userService.update(user);
        return new ResponseEntity<>(fillUser(user), HttpStatus.OK);
      } else {
        return BAD_REQUEST;
      }
    } else {
      return UNAUTHORIZED;
    }
  }

  // This method updates the prefstring for same user only
  @RequestMapping("user/updatePrefs/")
  public ResponseEntity<?> updatePrefs(
      @RequestParam("userId") Long userId,
      @RequestParam("prefstring") String prefstring,
      @RequestParam("apiKey") String apiKey) {
    if (!Utils.isEmpty(apiKey) && userService.existsById(userId)) {
      User apiUser = getUserIfValid(apiKey);
      User user = userService.getById(userId);
      if (apiUser != null && apiUser.id == user.id) {
        user.prefstring = prefstring;
        userService.update(user);
        return new ResponseEntity<>(fillUser(user), HttpStatus.OK);
      }
    }
    return UNAUTHORIZED;
  }

  @RequestMapping("apiKey/delete/")
  public ResponseEntity<?> deleteApiKey(
      @RequestParam("apiKeyId") Long apiKeyId, @RequestParam("apiKey") String apiKey) {
    if (isAdministrator(apiKey)) {
      if (apiKeyService.existsById(apiKeyId)) {
        return new ResponseEntity<>(fillApiKey(apiKeyService.deleteById(apiKeyId)), HttpStatus.OK);
      } else {
        return BAD_REQUEST;
      }
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("card/delete/")
  public ResponseEntity<?> deleteCard(
      @RequestParam("cardId") Long cardId, @RequestParam("apiKey") String apiKey) {
    if (isAdministrator(apiKey)) {
      if (cardService.existsById(cardId)) {
        return new ResponseEntity<>(fillCard(cardService.deleteById(cardId)), HttpStatus.OK);
      } else {
        return BAD_REQUEST;
      }
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("course/delete/")
  public ResponseEntity<?> deleteCourse(
      @RequestParam("courseId") Long courseId, @RequestParam("apiKey") String apiKey) {
    if (isAdministrator(apiKey)) {
      if (courseService.existsById(courseId)) {
        return new ResponseEntity<>(fillCourse(courseService.deleteById(courseId)), HttpStatus.OK);
      } else {
        return BAD_REQUEST;
      }
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("irregularity/delete/")
  public ResponseEntity<?> deleteIrregularity(
      @RequestParam("irregularityId") Long irregularityId,
      @RequestParam("apiKey") String apiKey) {
    if (isAdministrator(apiKey)) {
      if (irregularityService.existsById(irregularityId)) {
        return new ResponseEntity<>(
            fillIrregularity(irregularityService.deleteById(irregularityId)), HttpStatus.OK);
      } else {
        return BAD_REQUEST;
      }
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("location/delete/")
  public ResponseEntity<?> deleteLocation(
      @RequestParam("locationId") Long locationId, @RequestParam("apiKey") String apiKey) {
    if (isAdministrator(apiKey)) {
      if (locationService.existsById(locationId)) {
        return new ResponseEntity<>(
            fillLocation(locationService.deleteById(locationId)), HttpStatus.OK);
      } else {
        return BAD_REQUEST;
      }
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("period/delete/")
  public ResponseEntity<?> deletePeriod(
      @RequestParam("periodId") Long periodId, @RequestParam("apiKey") String apiKey) {
    if (isAdministrator(apiKey)) {
      if (periodService.existsById(periodId)) {
        return new ResponseEntity<>(fillPeriod(periodService.deleteById(periodId)), HttpStatus.OK);
      } else {
        return BAD_REQUEST;
      }
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("schedule/delete/")
  public ResponseEntity<?> deleteSchedule(
      @RequestParam("scheduleId") Integer scheduleId, @RequestParam("apiKey") String apiKey) {
    if (isAdministrator(apiKey)) {
      if (scheduleService.existsById(scheduleId)) {
        return new ResponseEntity<>(
            fillSchedule(scheduleService.deleteById(scheduleId)), HttpStatus.OK);
      } else {
        return BAD_REQUEST;
      }
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("student/delete/")
  public ResponseEntity<?> deleteStudent(
      @RequestParam("studentId") Integer studentId, @RequestParam("apiKey") String apiKey) {
    if (isAdministrator(apiKey)) {
      if (studentService.existsById(studentId)) {
        return new ResponseEntity<>(fillStudent(studentService.deleteById(studentId)), HttpStatus.OK);
      } else {
        return BAD_REQUEST;
      }
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("user/delete/")
  public ResponseEntity<?> deleteUser(
      @RequestParam("userId") Integer userId, @RequestParam("apiKey") String apiKey) {
    if (isAdministrator(apiKey)) {
      if (userService.existsById(userId)) {
        return new ResponseEntity<>(fillUser(userService.deleteById(userId)), HttpStatus.OK);
      } else {
        return BAD_REQUEST;
      }
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("apiKey/")
  public ResponseEntity<?> viewApiKey(@RequestParam Map<String, String> allRequestParam) {
    if (allRequestParam.containsKey("apiKey") && isTrusted(allRequestParam.get("apiKey"))) {
      List<ApiKey> list =
          apiKeyService
              .query(
                  parseLong(allRequestParam.get("apiKeyId")),
                  parseLong(allRequestParam.get("userId")),
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
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("card/")
  public ResponseEntity<?> viewCard(@RequestParam Map<String, String> allRequestParam) {
    if (allRequestParam.containsKey("apiKey") && isTrusted(allRequestParam.get("apiKey"))) {
      List<Card> list =
          cardService
              .query(
                  parseLong(allRequestParam.get("cardId")),
                  parseLong(allRequestParam.get("studentId")))
              .stream()
              .map(x -> fillCard(x))
              .collect(Collectors.toList());
      return new ResponseEntity<>(list, HttpStatus.OK);
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("course/")
  public ResponseEntity<?> viewCourse(@RequestParam Map<String, String> allRequestParam) {
    String apiKey = allRequestParam.get("apiKey");
    if (isTrusted(apiKey)) {

      List<Course> els =
          courseService
              .query(
                  parseLong(allRequestParam.get("courseId")),
                  parseLong(allRequestParam.get("teacherId")),
                  parseLong(allRequestParam.get("locationId")),
                  parseLong(allRequestParam.get("studentId")),
                  parseInteger(allRequestParam.get("period")),
                  allRequestParam.get("subject"),
                  parseLong(allRequestParam.get("time")),
                  parseInteger(
                      allRequestParam.getOrDefault(
                          "year", Integer.toString(Utils.getCurrentGraduatingYear()))))
              .stream()
              .map(x -> fillCourse(x))
              .collect(Collectors.toList());
      return new ResponseEntity<>(els, HttpStatus.OK);
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("encounter/")
  public ResponseEntity<?> viewEncounter(@RequestParam Map<String, String> allRequestParam) {
    String apiKey = allRequestParam.get("apiKey");
    if (isTrusted(apiKey)) {
      List<Encounter> els =
          encounterService
              .query(
                  parseInteger(allRequestParam.get("count")),
                  parseLong(allRequestParam.get("encounterId")),
                  parseLong(allRequestParam.get("studentId")),
                  parseLong(allRequestParam.get("locationId")),
                  parseLong(allRequestParam.get("minTime")),
                  parseLong(allRequestParam.get("maxTime")),
                  allRequestParam.get("studentName"))
              .stream()
              .map(x -> fillEncounter(x))
              .collect(Collectors.toList());
      return new ResponseEntity<>(els, HttpStatus.OK);
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("irregularity/")
  public ResponseEntity<?> viewIrregularity(@RequestParam Map<String, String> allRequestParam) {
    String apiKey = allRequestParam.get("apiKey");
    if (isTrusted(apiKey)) {
      List<Irregularity> els =
          irregularityService
              .query(
                  parseLong(allRequestParam.get("irregularityId")),
                  parseLong(allRequestParam.get("studentId")),
                  parseLong(allRequestParam.get("courseId")),
                  parseLong(allRequestParam.get("periodId")),
                  parseLong(allRequestParam.get("teacherId")),
                  allRequestParam.get("type"),
                  parseLong(allRequestParam.get("time")),
                  parseLong(allRequestParam.get("timeMissing")))
              .stream()
              .map(x -> fillIrregularity(x))
              .collect(Collectors.toList());
      return new ResponseEntity<>(els, HttpStatus.OK);
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("location/")
  public ResponseEntity<?> viewLocation(@RequestParam Map<String, String> allRequestParam) {
    if (allRequestParam.containsKey("apiKey") && isTrusted(allRequestParam.get("apiKey"))) {
      List<Location> list =
          locationService
              .query(
                  parseLong(allRequestParam.get("locationId")),
                  allRequestParam.get("name"),
                  allRequestParam.get("tags"))
              .stream()
              .map(x -> fillLocation(x))
              .collect(Collectors.toList());
      return new ResponseEntity<>(list, HttpStatus.OK);
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("period/")
  public ResponseEntity<?> viewPeriod(@RequestParam Map<String, String> allRequestParam) {
    String apiKey = allRequestParam.get("apiKey");
    if (isTrusted(apiKey)) {
      List<Period> els =
          periodService
              .query(
                  parseLong(allRequestParam.get("periodId")),
                  parseLong(allRequestParam.get("time")),
                  parseLong(allRequestParam.get("minDuration")),
                  parseLong(allRequestParam.get("maxDuration")),
                  parseLong(allRequestParam.get("initialTimeBegin")),
                  parseLong(allRequestParam.get("initialTimeEnd")),
                  parseLong(allRequestParam.get("startTimeBegin")),
                  parseLong(allRequestParam.get("startTimeEnd")),
                  parseLong(allRequestParam.get("endTimeBegin")),
                  parseLong(allRequestParam.get("endTimeEnd")),
                  parseInteger(allRequestParam.get("period")),
                  parseLong(allRequestParam.get("courseId")),
                  parseLong(allRequestParam.get("teacherId")))
              .stream()
              .map(x -> fillPeriod(x))
              .collect(Collectors.toList());
      return new ResponseEntity<>(els, HttpStatus.OK);
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("schedule/")
  public ResponseEntity<?> viewSchedule(@RequestParam Map<String, String> allRequestParam) {
    if (allRequestParam.containsKey("apiKey") && isTrusted(allRequestParam.get("apiKey"))) {
      List<Schedule> list =
          scheduleService
              .query(
                  parseLong(allRequestParam.get("scheduleId")),
                  parseLong(allRequestParam.get("studentId")),
                  parseLong(allRequestParam.get("courseId")),
                  parseLong(allRequestParam.get("teacherId")),
                  parseLong(allRequestParam.get("locationId")),
                  parseInteger(allRequestParam.get("period")))
              .stream()
              .map(x -> fillSchedule(x))
              .collect(Collectors.toList());
      return new ResponseEntity<>(list, HttpStatus.OK);
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("session/")
  public ResponseEntity<?> viewSession(@RequestParam Map<String, String> allRequestParam) {
    if (allRequestParam.containsKey("apiKey") && isTrusted(allRequestParam.get("apiKey"))) {
      List<Session> list =
          sessionService
              .query(
                  parseLong(allRequestParam.get("id")),
                  parseLong(allRequestParam.get("inEncounterId")),
                  parseLong(allRequestParam.get("outEncounterId")),
                  parseLong(allRequestParam.get("anyEncounterId")),
                  parseLong(allRequestParam.get("courseId")),
                  parseBoolean(allRequestParam.get("complete")),
                  parseBoolean(allRequestParam.get("hasOut")),
                  parseLong(allRequestParam.get("locationId")),
                  parseLong(allRequestParam.get("studentId")),
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
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("student/")
  public ResponseEntity<?> viewStudent(@RequestParam Map<String, String> allRequestParam) {
    if (allRequestParam.containsKey("apiKey") && isTrusted(allRequestParam.get("apiKey"))) {
      List<Student> list =
          studentService
              .query(
                  parseLong(allRequestParam.get("studentId")),
                  parseLong(allRequestParam.get("cardId")),
                  parseLong(allRequestParam.get("courseId")),
                  parseInteger(allRequestParam.get("graduatingYear")),
                  allRequestParam.get("name"),
                  allRequestParam.get("tags"))
              .stream()
              .map(x -> fillStudent(x))
              .collect(Collectors.toList());
      return new ResponseEntity<>(list, HttpStatus.OK);
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("user/")
  public ResponseEntity<?> viewUser(@RequestParam Map<String, String> allRequestParam) {
    if (allRequestParam.containsKey("apiKey") && isTrusted(allRequestParam.get("apiKey"))) {
      List<User> list =
          userService
              .query(
                  parseLong(allRequestParam.get("userId")),
                  allRequestParam.get("name"),
                  allRequestParam.get("email"),
                  parseInteger(allRequestParam.get("ring")))
              .stream()
              .map(x -> fillUser(x))
              .collect(Collectors.toList());
      return new ResponseEntity<>(list, HttpStatus.OK);
    } else {
      return UNAUTHORIZED;
    }
  }

  /* SPECIAL METHODS */

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
            CSVFormat.DEFAULT.parse(
                new InputStreamReader(new ByteArrayInputStream(file.getBytes()), "UTF8"));
        int currentGraduatingYear = Utils.getCurrentGraduatingYear();
        for (CSVRecord record : parser) {
          Integer id = parseInteger(record.get(2));
          if (id != null && !studentService.existsById(id)) {
            Integer graduatingYear = currentGraduatingYear + (12 - parseInteger(record.get(4)));
            String name = record.get(0) + ' ' + record.get(1);
            if (graduatingYear != null && name != null) {
              Student student = new Student();
              student.id = id;
              student.graduatingYear = graduatingYear;
              student.name = name;
              student.tags = "";
              studentService.add(student);
            }
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
  public ResponseEntity<?> batchSetSchedule(
      @RequestParam("courseId") Integer courseId,
      @RequestParam("file") MultipartFile file,
      @RequestParam("apiKey") String apiKey,
      @RequestParam(value="remove", defaultValue="false") Boolean remove) {
    if (isTrusted(apiKey)) {
      if (courseService.existsById(courseId)) {
        try {
          CSVParser parser =
              CSVFormat.DEFAULT.parse(
                  new InputStreamReader(new ByteArrayInputStream(file.getBytes()), "UTF8"));
          int currentGraduatingYear = Utils.getCurrentGraduatingYear();
          for (CSVRecord record : parser) {
            Long studentId = parseLong(record.get(2));
            if (studentId != null && studentService.existsById(studentId)) {
              if (scheduleService
                      .query(
                          null, studentId, null, null, null, courseService.getById(courseId).period)
                      .size()
                  == 0) {
                if(remove) {
                  Schedule schedule = scheduleService.getScheduleByStudentIdCourseId(studentId, courseId);
                  if(schedule != null) {
                    scheduleService.deleteById(schedule.id);
                  }
                } else {
                  Schedule schedule = new Schedule();
                  schedule.studentId = studentId;
                  schedule.courseId = courseId;
                  scheduleService.add(schedule);
                }
              }
            }
          }
          return OK;
        } catch (Exception e) {
          e.printStackTrace();
          return INTERNAL_SERVER_ERROR;
        }
      } else {
        return BAD_REQUEST;
      }
    } else {
      return UNAUTHORIZED;
    }
  }

  /* TESTING */
  @RequestMapping("populateTestingPeriods")
  public ResponseEntity<?> populateTestingPeriods() {
    LocalDate today =
        ZonedDateTime.now(Utils.TIMEZONE).toLocalDate();

    long minute = 1*60*1000;
    long initialTime = System.currentTimeMillis();
    for(int i = 3; i < 7; i++) {
      Period period = new Period();
      period.period = i;
      period.initialTime = initialTime;
      period.startTime = initialTime + minute;
      period.endTime = initialTime + minute*3;
      initialTime += minute*3;
      periodService.add(period);
    }
    return OK;
  }

  // deletes periods with a length of less than 10 min
  @RequestMapping("deleteTestingPeriods")
  public ResponseEntity<?> deleteTestingPeriods() {
    List<Period> periodList =
        periodService.query(
            null, // id
            null, // time
            null, // minDuration
            10*60*1000, // maxDuration
            null, // initialTimeBegin
            null, // initialTimeEnd
            null, // startTimeBegin
            null, // startTimeEnd
            null, // endTimeBegin
            null, // endTimeEnd
            null, // period
            null, // courseId
            null // teacherId
          );
    for(Period period : periodList) {
      periodService.deleteById(period.id);
    }
  }


  @RequestMapping("populatePeriods")
  public ResponseEntity<?> populatePeriods() {
    periodService.deleteAll();
    LocalDate sunday =
        ZonedDateTime.now(Utils.TIMEZONE).toLocalDate().plusWeeks(-1).with(DayOfWeek.SUNDAY);

    // get weekdays
    LocalDate monday = sunday.plusDays(1);
    LocalDate tuesday = sunday.plusDays(2);
    LocalDate wednesday = sunday.plusDays(3);
    LocalDate thursday = sunday.plusDays(4);
    LocalDate friday = sunday.plusDays(5);
    LocalDate saturday = sunday.plusDays(6);

    for (int week = 0; week < 10; week++) {
      // collab
      LocalDate thisMonday = monday.plusWeeks(week);
      addPeriod(thisMonday, 1, "6:00", "7:15", "7:55");
      addPeriod(thisMonday, 2, "7:55", "8:00", "8:40");
      addPeriod(thisMonday, 3, "8:40", "8:45", "9:25");
      addPeriod(thisMonday, 4, "9:25", "9:45", "10:25");
      addPeriod(thisMonday, 0, "10:25", "10:25", "10:55");
      addPeriod(thisMonday, 5, "10:55", "11:00", "11:40");
      addPeriod(thisMonday, 6, "11:40", "12:15", "12:55");
      addPeriod(thisMonday, 7, "12:55", "13:00", "13:40");

      // S Day
      LocalDate thisTuesday = tuesday.plusWeeks(week);
      addPeriod(thisTuesday, 1, "6:00", "7:15", "8:55");
      addPeriod(thisTuesday, 3, "8:55", "9:15", "10:55");
      addPeriod(thisTuesday, 5, "10:55", "11:15", "12:55");
      addPeriod(thisTuesday, 7, "12:55", "13:30", "15:10");

      LocalDate thisThursday = thursday.plusWeeks(week);
      addPeriod(thisThursday, 1, "6:00", "7:15", "8:55");
      addPeriod(thisThursday, 3, "8:55", "9:15", "10:55");
      addPeriod(thisThursday, 5, "10:55", "11:15", "12:55");
      addPeriod(thisThursday, 7, "12:55", "13:30", "15:10");

      // T Day
      LocalDate thisWednesday = wednesday.plusWeeks(week);
      addPeriod(thisWednesday, 2, "6:00", "8:00", "9:40");
      addPeriod(thisWednesday, 4, "9:40", "10:00", "11:40");
      addPeriod(thisWednesday, 0, "11:40", "11:40", "12:30");
      addPeriod(thisWednesday, 6, "12:30", "13:05", "14:45");

      LocalDate thisFriday = friday.plusWeeks(week);
      addPeriod(thisFriday, 2, "6:00", "8:00", "9:40");
      addPeriod(thisFriday, 4, "9:40", "10:00", "11:40");
      addPeriod(thisFriday, 0, "11:40", "11:40", "12:30");
      addPeriod(thisFriday, 6, "12:30", "13:05", "14:45");
    }
    return OK;
  }


  void addPeriod(LocalDate day, int p, String initialTime, String startTime, String endTime) {
    String[] initialComponents = initialTime.split(":");
    String[] startComponents = startTime.split(":");
    String[] endComponents = endTime.split(":");
    Period period = new Period();
    period.period = p;
    period.initialTime =
        day.atTime(parseInteger(initialComponents[0]), parseInteger(initialComponents[1]))
            .atZone(Utils.TIMEZONE)
            .toInstant()
            .toEpochMilli();
    period.startTime =
        day.atTime(parseInteger(startComponents[0]), parseInteger(startComponents[1]))
            .atZone(Utils.TIMEZONE)
            .toInstant()
            .toEpochMilli();
    period.endTime =
        day.atTime(parseInteger(endComponents[0]), parseInteger(endComponents[1]))
            .atZone(Utils.TIMEZONE)
            .toInstant()
            .toEpochMilli();
    periodService.add(period);
  }
}
