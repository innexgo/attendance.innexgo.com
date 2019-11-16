package innexgo;

import java.time.*;
import java.time.temporal.*;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.csv.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping(value = {"/api"})
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
   * Fills in jackson objects (User) for ApiKey
   *
   * @param apiKey - ApiKey object
   * @return apiKey with filled jackson objects
   */
  ApiKey fillApiKey(ApiKey apiKey) {
    apiKey.user = fillUser(userService.getById(apiKey.userId));
    return apiKey;
  }

  /**
   * Fills in jackson objects(Student) in Card
   *
   * @param card - Card object
   * @return Card object with filled jackson objects
   */
  Card fillCard(Card card) {
    card.student = fillStudent(studentService.getById(card.studentId));
    return card;
  }

  /**
   * Fills in jackson objects(Teacher and Location) in Course
   *
   * @param course - Course object
   * @return Course object with filled jackson objects
   */
  Course fillCourse(Course course) {
    course.teacher = fillUser(userService.getById(course.teacherId));
    course.location = fillLocation(locationService.getById(course.locationId));
    return course;
  }

  /**
   * Fills in jackson objects(Location and Student) in Encounter
   *
   * @param encounter - Encounter object
   * @return Encounter object with filled jackson objects
   */
  Encounter fillEncounter(Encounter encounter) {
    encounter.location = fillLocation(locationService.getById(encounter.locationId));
    encounter.student = fillStudent(studentService.getById(encounter.studentId));
    return encounter;
  }

  /**
   * Fills in jackson objects(Course, Period, and Student) in Irregularity
   *
   * @param irregularity - Irregularity object
   * @return Irregularity object with filled jackson objects
   */
  Irregularity fillIrregularity(Irregularity irregularity) {
    irregularity.course = fillCourse(courseService.getById(irregularity.courseId));
    irregularity.period = fillPeriod(periodService.getById(irregularity.periodId));
    irregularity.student = fillStudent(studentService.getById(irregularity.studentId));
    return irregularity;
  }

  /**
   * Fills in jackson objects (none at the moment) for Location
   *
   * @param location - Location object
   * @return Location object with filled jackson objects
   */
  Location fillLocation(Location location) {
    return location;
  }

  /**
   * Fills in jackson objects (none at the moment) for Period
   *
   * @param period - Period object
   * @return Period object with filled jackson objects
   */
  Period fillPeriod(Period period) {
    return period;
  }

  /**
   * Fills in jackson objects(Student, Course) for Schedule
   *
   * @param schedule - Schedule object
   * @return Schedule object with filled jackson objects
   */
  Schedule fillSchedule(Schedule schedule) {
    schedule.student = fillStudent(studentService.getById(schedule.studentId));
    schedule.course = fillCourse(courseService.getById(schedule.courseId));
    return schedule;
  }

  /**
   * Fills in jackson objects (none at the moment) for Semester
   *
   * @param semester - Semester object
   * @return Semester object with filled jackson objects
   */
  Semester fillSemester(Semester semester) {
    return semester;
  }


  /**
   * Fills in jackson objects (Student, Course, inEncounter, and outEncounter) for Session
   *
   * @param session - Session object
   * @return Session object with filled jackson objects
   */
  Session fillSession(Session session) {
    session.student = fillStudent(studentService.getById(session.studentId));
    session.inEncounter = fillEncounter(encounterService.getById(session.inEncounterId));
    if (session.complete) {
      session.outEncounter = fillEncounter(encounterService.getById(session.outEncounterId));
    }
    return session;
  }

  /**
   * Fills in jackson objects (none at the moment) for Student
   *
   * @param student - Student object
   * @return Student object with filled jackson objects
   */
  Student fillStudent(Student student) {
    return student;
  }

  /**
   * Fills in jackson objects (none at the moment) for User
   *
   * @param user - User object
   * @return User object with filled jackson objects
   */
  User fillUser(User user) {
    return user;
  }

  /**
   * Returns a user if valid
   *
   * @param key - apikey code of the User
   * @return User or null if invalid
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
   *
   * @param key - apikey code of a User
   * @return true if administrator; false if not administrator or invalid
   */
  boolean isAdministrator(String key) {
    if (key == null) {
      return false;
    }
    User user = getUserIfValid(key);
    return user != null && (user.ring == User.ADMINISTRATOR);
  }

  /**
   * Checks whether a User is trusted
   *
   * @param key - apikey code of User
   * @return true if User is trusted; false if User not trusted
   */
  boolean isTrusted(String key) {
    if (key == null) {
      return false;
    }
    User user = getUserIfValid(key);
    return user != null && (user.ring <= User.TEACHER);
  }

  /**
   * Each day at midnight, log everyone out and give a "forgot to sign out" irregularity if they had
   * a session open
   */
  @Scheduled(fixedDelay = 5000)
  public void signOutAtMidnight() {
    System.out.println("Starting sign out at midnight process");
    while (true) {
      try {
        ZonedDateTime now = ZonedDateTime.now(Utils.TIMEZONE);
        ZonedDateTime tomorrowStart = now.plusDays(1).truncatedTo(ChronoUnit.DAYS);
        long millisTillMidnight = Duration.between(now, tomorrowStart).toMillis();
        System.out.println("Next mass sign out at: " + millisTillMidnight / 1000);
        Thread.sleep(millisTillMidnight);
      } catch (Exception e) {
        e.printStackTrace();
      }

      System.out.println("Signing everyone out");
      // get list of open sessions
      List<Session> openSessionList =
          sessionService.query(
              null, // Long id
              null, // Long inEncounterId
              null, // Long outEncounterId
              null, // Long anyEncounterId
              null, // Long periodId
              null, // Long period
              null, // Long courseId
              false, // Boolean complete
              null, // Long locationId
              null, // Long studentId
              null, // Long teacherId
              null, // Long time
              null, // Long inTimeBegin
              null, // Long inTimeEnd
              null, // Long outTimeBegin
              null, // Long outTimeEnd
              null // Long count
              );

      for (Session openSession : openSessionList) {
        // Virtually close session by generating a fake (virtual) encounter and insert it in.
        // We know they must have somehow left from here

        // grab old encounter
        Encounter inEncounter = encounterService.getById(openSession.inEncounterId);
        // make new encounter
        Encounter virtualEncounter = new Encounter();
        virtualEncounter.locationId = inEncounter.locationId;
        virtualEncounter.studentId = openSession.studentId;
        virtualEncounter.time = System.currentTimeMillis();
        virtualEncounter.virtual = true;
        encounterService.add(virtualEncounter);

        // now close session
        openSession.outEncounterId = virtualEncounter.id;
        openSession.complete = true;
        sessionService.update(openSession);

        // period and course are that of the first period with a course that the session intersected
        List<Period> intersectedPeriods =
            periodService.query(
                null, // Long id,
                null, // Long time,
                null, // Long minDuration,
                null, // Long maxDuration,
                null, // Long initialTimeBegin,
                null, // Long initialTimeEnd,
                null, // Long startTimeBegin,
                null, // Long startTimeEnd,
                null, // Long endTimeBegin,
                null, // Long endTimeEnd,
                null, // Integer period,
                null, // Long courseId
                null // Long teacherId
                );
        // Find first period with a course at this location
        Period irregPeriod = null;
        Course irregCourse = null;
        for (Period period : intersectedPeriods) {
          List<Course> courses =
              courseService.query(
                  null, // Long id,
                  null, // Long teacherId,
                  inEncounter.locationId, // Long locationId,
                  openSession.studentId, // Long studentId,
                  period.period, // Integer period,
                  null, // String subject,
                  null, // Long time,
                  Utils.getCurrentGraduatingYear() // Integer year
                  );
          if (courses.size() > 0) {
            irregPeriod = period;
            irregCourse = courses.get(0);
          }
        }

        if (irregPeriod != null && irregCourse != null) {
          // Now add irregularity about forgetting to sign out
          Irregularity forgotToSignOut = new Irregularity();
          forgotToSignOut.studentId = openSession.studentId;
          forgotToSignOut.courseId = irregCourse.id;
          forgotToSignOut.periodId = irregPeriod.id;
          forgotToSignOut.type = Irregularity.TYPE_FORGOT_SIGN_OUT;
          forgotToSignOut.time = irregPeriod.startTime;
          forgotToSignOut.timeMissing = 0;
          irregularityService.add(forgotToSignOut);
        }
      }
    }
  }

  /**
   * For all the courses at the current time, create irregularities { If the student has not signed
   * in yet before or during the period { generate an absent irregularity } }
   */
  @Scheduled(fixedDelay = 5000)
  public void insertAbsences() {
    System.out.println("Starting insert absences process");
    // the list of periods that havent started yet
    List<Period> periodList =
        periodService.query(
            null, // id
            null, // time
            null, // minDuration
            null, // maxDuration
            System.currentTimeMillis(), // initialTimeBegin
            null, // initialTimeEnd
            null, // startTimeBegin
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
        long timeToSleep = Math.max(0, period.initialTime - System.currentTimeMillis());
        System.out.println(
            "Inserting absences in: " +  timeToSleep / 1000);
        Thread.sleep(timeToSleep);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      System.out.println("Period " + period.id + " started, inserting absences");
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
        // subtract present students from all students taking the course
        List<Student> studentAbsentList =
            studentService.query(
                null, // Long id
                null, // Long cardId
                course.id, // Long courseId
                null, // Integer graduatingYear
                null, // String name
                null, // String partialName
                null // String tags
                );
        studentAbsentList.removeAll(studentService.present(course.id, period.id));

        // mark all students not there as absent
        for (Student student : studentAbsentList) {
          // Check if already absent. if not, don't add
          boolean alreadyAbsent =
              irregularityService
                      .query(
                          null,
                          student.id,
                          null,
                          period.id,
                          null,
                          Irregularity.TYPE_ABSENT,
                          null,
                          null,
                          null)
                      .size()
                  > 0;
          // if not already absent
          if (!alreadyAbsent) {
            Irregularity irregularity = new Irregularity();
            irregularity.studentId = student.id;
            irregularity.courseId = course.id;
            irregularity.periodId = period.id;
            irregularity.type = Irregularity.TYPE_ABSENT;
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
   *
   * @param userId the id of the User
   * @param email email of the User
   * @param expirationTime time in milliseconds since 1970 when this key is due to expire
   * @param password User password
   * @return ResponseEntity with ApiKey of User and HttpStatus.OK code if successful
   * @throws ResponseEntity with HttpStatus.UNAUTHORIZED if the User is unauthorized
   * @throws ResponseEntity with HttpStatus.BAD_REQUEST if the process is unsuccessful
   */
  @RequestMapping("/apiKey/new/")
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
   *
   * @param cardId - unique identifier of the card
   * @param studentId - student id (the id on the card given by the school)
   * @param apiKey - api key of the user trying to make a new card
   * @return ResponseEntity with a Card and HttpStatus.OK
   * @throws ResponseEntity with HttpStatus.UNAUTHORIZED if the User is unauthorized
   * @throws ResponseEntity with HttpStatus.BAD_REQUEST if the process if unsuccessful
   */
  @RequestMapping("/card/new/")
  public ResponseEntity<?> newCard(
      @RequestParam("cardId") Long cardId,
      @RequestParam("studentId") Long studentId,
      @RequestParam("apiKey") String apiKey) {
    if (isTrusted(apiKey)) {
      if (studentService.existsById(studentId) && !cardService.existsById(cardId)) {
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
   *
   * @param teacherId - id of teacher that normally teaches the class
   * @param locationId - id of location where class is normally held
   * @param period - period of the class (usually 1-7)
   * @param subject - Subject of class, Ex. "Math 3"
   * @param apiKey - of User creating new Course
   * @return ResponseEntity with a Course and HttpStatus.OK
   * @throws ResponseEntity with HttpStatus.UNAUTHORIZED if the User is not an administrator
   * @throws ResponseEntity with HttpStatus.BAD_REQUEST if the process if unsuccessful
   */
  @RequestMapping("/course/new/")
  public ResponseEntity<?> newCourse(
      @RequestParam("userId") Long teacherId,
      @RequestParam("locationId") Long locationId,
      @RequestParam("period") Integer period,
      @RequestParam("subject") String subject,
      @RequestParam("apiKey") String apiKey) {
    if (isAdministrator(apiKey)) {
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
   *
   * @param studentId - student id number
   * @param cardId - unique identifier for the card
   * @param locationId - location id of the location where this course is normally taught
   * @param courseId - course id
   * @param apiKey - apiKey of the user creating the encounter
   * @return ResponseEntity with Encounter and HttpStatus.OK
   * @throws ResponseEntity with HttpStatus.BAD_REQUEST if process is unsuccessful
   * @throws ResponseEntity with HttpStatus.UNAUTHORIZED if the User is not trusted
   */
  @RequestMapping("/encounter/new/")
  public ResponseEntity<?> newEncounter(
      @RequestParam(value = "studentId", defaultValue = "-1") Long studentId,
      @RequestParam(value = "cardId", defaultValue = "-1") Long cardId,
      @RequestParam("locationId") Long locationId,
      @RequestParam(value = "noSession", defaultValue = "false") Boolean noSession,
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

      if (locationService.existsById(locationId)) {
        Encounter encounter = new Encounter();
        encounter.locationId = locationId;
        encounter.studentId = student.id;
        encounter.time = System.currentTimeMillis();
        encounter.virtual = false;
        encounterService.add(encounter);

        // Now we update sessions
        if (!noSession) {

          // if school is currently going on, represents the current period
          Period currentPeriod = null;
          // if there is currently a course going on, represents the current course
          Course currentCourse = null;

          List<Period> currentPeriods =
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

          currentPeriod = currentPeriods.isEmpty() ? null : currentPeriods.get(0);

          if (currentPeriod != null) {
            List<Course> currentCourses =
                courseService.query(
                    null, // Long id
                    null, // Long teacherId
                    locationId, // Long locationId
                    null, // Long studentId
                    currentPeriod.period, // Integer period
                    null, // String subject
                    null, // Long time
                    Utils.getCurrentGraduatingYear() // Integer year
                    );

            currentCourse = currentCourses.isEmpty() ? null : currentCourses.get(0);
          } else {
            currentCourse = null;
          }


          List<Session> openSessions =
              sessionService.query(
                  null, // Long id
                  null, // Long inEncounterId
                  null, // Long outEncounterId
                  null, // Long anyEncounterId
                  null, // Long periodId
                  null, // Long period
                  null, // Long courseId
                  false, // Boolean complete
                  null, // Long locationId
                  student.id, // Long studentId
                  null, // Long teacherId
                  null, // Long time
                  null, // Long inTimeBegin
                  null, // Long inTimeEnd
                  null, // Long outTimeBegin
                  null, // Long outTimeEnd
                  null // Long count
                  );


          // If the encounter was used to close a session properly
          boolean usedToClose = false;


          for (Session openSession : openSessions) {
            Encounter inEncounter = encounterService.getById(openSession.inEncounterId);
            // if it's at the same location
            if (locationId == inEncounter.locationId) {
              // Then close this session naturally
              openSession.outEncounterId = encounter.id;
              openSession.complete = true;
              sessionService.update(openSession);

              usedToClose = true;

              if (currentCourse != null) {
                // if it is in the middle of class, add a leaveEarly irregularity
                if (System.currentTimeMillis() < currentPeriod.endTime) {
                  Irregularity irregularity = new Irregularity();
                  irregularity.studentId = student.id;
                  irregularity.courseId = currentCourse.id;
                  irregularity.periodId = currentPeriod.id;
                  // if before the period has actually started, make absent instead of left early
                  irregularity.type =
                      System.currentTimeMillis() < currentPeriod.startTime
                          ? Irregularity.TYPE_ABSENT
                          : Irregularity.TYPE_LEFT_EARLY;
                  irregularity.time = System.currentTimeMillis();
                  irregularity.timeMissing = currentPeriod.endTime - System.currentTimeMillis();
                  irregularityService.add(irregularity);
                }
              }
            } else {
              // its not at the same location as the beginning
              // Virtually close session by generating a fake (virtual) encounter and insert it in.
              // We know they must have somehow left from here
              Encounter virtualEncounter = new Encounter();
              virtualEncounter.locationId = inEncounter.locationId;
              virtualEncounter.studentId = student.id;
              virtualEncounter.time = System.currentTimeMillis();
              virtualEncounter.virtual = true;
              encounterService.add(virtualEncounter);

              openSession.outEncounterId = virtualEncounter.id;
              openSession.complete = true;
              sessionService.update(openSession);

              // period and course are that of the first period with a course that the session
              // intersected
              List<Period> intersectedPeriods =
                  periodService.query(
                      null, // Long id,
                      null, // Long time,
                      null, // Long minDuration,
                      null, // Long maxDuration,
                      null, // Long initialTimeBegin,
                      inEncounter.time, // Long initialTimeEnd,
                      null, // Long startTimeBegin,
                      null, // Long startTimeEnd,
                      System.currentTimeMillis(), // Long endTimeBegin,
                      null, // Long endTimeEnd,
                      null, // Integer period,
                      null, // Long courseId
                      null // Long teacherId
                      );
              // Find first period with a course at this location
              Period irregPeriod = null;
              Course irregCourse = null;
              for (Period period : intersectedPeriods) {
                List<Course> courses =
                    courseService.query(
                        null, // Long id,
                        null, // Long teacherId,
                        inEncounter.locationId, // Long locationId,
                        openSession.studentId, // Long studentId,
                        period.period, // Integer period,
                        null, // String subject,
                        null, // Long time,
                        Utils.getCurrentGraduatingYear() // Integer year
                        );
                if (courses.size() > 0) {
                  irregPeriod = period;
                  irregCourse = courses.get(0);
                }
              }

              if (irregPeriod != null && irregCourse != null) {
                // Now add irregularity about forgetting to sign out
                Irregularity forgotToSignOut = new Irregularity();
                forgotToSignOut.studentId = openSession.studentId;
                forgotToSignOut.courseId = irregCourse.id;
                forgotToSignOut.periodId = irregPeriod.id;
                forgotToSignOut.type = Irregularity.TYPE_FORGOT_SIGN_OUT;
                forgotToSignOut.time = irregPeriod.startTime;
                forgotToSignOut.timeMissing = 0;
                irregularityService.add(forgotToSignOut);
              }
            }
          }

          // If the encounter wasn't used to close, we must make a new one
          if (!usedToClose) {
            // make new open session
            Session session = new Session();
            session.studentId = student.id;
            session.complete = false;
            session.inEncounterId = encounter.id;
            session.outEncounterId = 0;
            sessionService.add(session);

            if (currentCourse != null) {
              // now we check if they arent there, and fix it
              List<Irregularity> irregularities =
                irregularityService.query(
                    null, // id
                    student.id, // studentId
                    currentCourse.id, // courseId
                    currentPeriod.id, // periodId
                    null, // teacherId
                    null, // type
                    null, // time
                    null, // timeMissing
                    null // count
                    );

              for (Irregularity irregularity : irregularities) {
                if (irregularity.type.equals(Irregularity.TYPE_ABSENT)) {
                  // if there is absence, convert it to a tardy or delete it
                  if (System.currentTimeMillis() > currentPeriod.startTime) {
                    irregularity.type = Irregularity.TYPE_TARDY;
                    irregularity.timeMissing =
                      System.currentTimeMillis() - currentPeriod.startTime;
                    irregularityService.update(irregularity);
                  } else {
                    // if they're present before the startTime
                    irregularityService.deleteById(irregularity.id);
                  }
                } else if (irregularity.type.equals(Irregularity.TYPE_LEFT_EARLY)) {
                  // if there is a leftEarly, convert it to a leftTemporarily
                  irregularity.type = Irregularity.TYPE_LEFT_TEMPORARILY;
                  irregularity.timeMissing = System.currentTimeMillis() - irregularity.time;
                  irregularityService.update(irregularity);
                }
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
   *
   * @param name name of location, eg. "Room 105"
   * @param tags attributes associated with location, such as "classroom" or "restricted"
   * @param apiKey - apiKey of the User creating the location
   * @return ResponseEntity with location and HttpStatus.OK
   * @throws ResponseEntity with HttpStatus.BAD_REQUEST if process if unsuccessful
   * @throws ResponseEntity with HttpStatus.UNAUTHORIZED if the User is not an administrator
   */
  @RequestMapping("/location/new/")
  public ResponseEntity<?> newLocation(
      @RequestParam("name") String name,
      @RequestParam("tags") String tags,
      @RequestParam("apiKey") String apiKey) {
    if (isAdministrator(apiKey)) {
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

  @RequestMapping("/period/new/")
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

  @RequestMapping("/schedule/new/")
  public ResponseEntity<?> newSchedule(
      @RequestParam("studentId") Long studentId,
      @RequestParam("courseId") Long courseId,
      @RequestParam("apiKey") String apiKey) {
    if (isAdministrator(apiKey)) {
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

  @RequestMapping("/student/new/")
  public ResponseEntity<?> newStudent(
      @RequestParam("studentId") Long id,
      @RequestParam("graduatingYear") Integer graduatingYear,
      @RequestParam("name") String name,
      @RequestParam(value = "tags", defaultValue = "") String tags,
      @RequestParam("apiKey") String apiKey) {
    if (isAdministrator(apiKey)) {
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

  @RequestMapping("/user/new/")
  public ResponseEntity<?> newUser(
      @RequestParam("userName") String name,
      @RequestParam("email") String email,
      @RequestParam("password") String password,
      @RequestParam("ring") Integer ring,
      @RequestParam("apiKey") String apiKey) {
    if (isAdministrator(apiKey)) {
      if (!Utils.isEmpty(name)
          && !Utils.isEmpty(password)
          && !Utils.isEmpty(email)
          && !userService.existsByEmail(email)) {
        User u = new User();
        u.name = name;
        u.email = email;
        u.passwordHash = Utils.encodePassword(password);
        u.ring = ring;
        userService.add(u);
        return new ResponseEntity<>(fillUser(u), HttpStatus.OK);
      } else {
        return BAD_REQUEST;
      }
    } else {
      return UNAUTHORIZED;
    }
  }

  // This method updates the password for same user only
  @RequestMapping("/user/updatePassword/")
  public ResponseEntity<?> updatePassword(
      @RequestParam("userId") Long userId,
      @RequestParam("oldPassword") String oldPassword,
      @RequestParam("newPassword") String newPassword) {
    if (!Utils.isEmpty(oldPassword)
        && userService.existsById(userId)
        && Utils.matchesPassword(oldPassword, userService.getById(userId).passwordHash)) {

      if (!Utils.isEmpty(newPassword)) {
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

  @RequestMapping("/apiKey/delete/")
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

  @RequestMapping("/card/delete/")
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

  @RequestMapping("/irregularity/delete/")
  public ResponseEntity<?> deleteIrregularity(
      @RequestParam("irregularityId") Long irregularityId, @RequestParam("apiKey") String apiKey) {
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

  @RequestMapping("/schedule/delete/")
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

  @RequestMapping("/apiKey/")
  public ResponseEntity<?> viewApiKey(@RequestParam Map<String, String> allRequestParam) {
    if (allRequestParam.containsKey("apiKey") && isTrusted(allRequestParam.get("apiKey"))) {
      List<ApiKey> list =
          apiKeyService
              .query(
                  Utils.parseLong(allRequestParam.get("apiKeyId")),
                  Utils.parseLong(allRequestParam.get("userId")),
                  Utils.parseLong(allRequestParam.get("minCreationTime")),
                  Utils.parseLong(allRequestParam.get("maxCreationTime")),
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

  @RequestMapping("/card/")
  public ResponseEntity<?> viewCard(@RequestParam Map<String, String> allRequestParam) {
    if (allRequestParam.containsKey("apiKey") && isTrusted(allRequestParam.get("apiKey"))) {
      List<Card> list =
          cardService
              .query(
                  Utils.parseLong(allRequestParam.get("cardId")),
                  Utils.parseLong(allRequestParam.get("studentId")))
              .stream()
              .map(x -> fillCard(x))
              .collect(Collectors.toList());
      return new ResponseEntity<>(list, HttpStatus.OK);
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("/course/")
  public ResponseEntity<?> viewCourse(@RequestParam Map<String, String> allRequestParam) {
    String apiKey = allRequestParam.get("apiKey");
    if (isTrusted(apiKey)) {

      List<Course> els =
          courseService
              .query(
                  Utils.parseLong(allRequestParam.get("courseId")),
                  Utils.parseLong(allRequestParam.get("teacherId")),
                  Utils.parseLong(allRequestParam.get("locationId")),
                  Utils.parseLong(allRequestParam.get("studentId")),
                  Utils.parseInteger(allRequestParam.get("period")),
                  allRequestParam.get("subject"),
                  Utils.parseLong(allRequestParam.get("time")),
                  Utils.parseInteger(
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

  @RequestMapping("/encounter/")
  public ResponseEntity<?> viewEncounter(@RequestParam Map<String, String> allRequestParam) {
    String apiKey = allRequestParam.get("apiKey");
    if (isTrusted(apiKey)) {
      List<Encounter> els =
          encounterService
              .query(
                  Utils.parseLong(allRequestParam.get("count")),
                  Utils.parseLong(allRequestParam.get("encounterId")),
                  Utils.parseLong(allRequestParam.get("studentId")),
                  Utils.parseLong(allRequestParam.get("locationId")),
                  Utils.parseLong(allRequestParam.get("minTime")),
                  Utils.parseLong(allRequestParam.get("maxTime")),
                  Utils.parseBoolean(allRequestParam.get("virtual")),
                  allRequestParam.get("studentName"))
              .stream()
              .map(x -> fillEncounter(x))
              .collect(Collectors.toList());
      return new ResponseEntity<>(els, HttpStatus.OK);
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("/irregularity/")
  public ResponseEntity<?> viewIrregularity(@RequestParam Map<String, String> allRequestParam) {
    String apiKey = allRequestParam.get("apiKey");
    if (isTrusted(apiKey)) {
      List<Irregularity> els =
          irregularityService
              .query(
                  Utils.parseLong(allRequestParam.get("irregularityId")),
                  Utils.parseLong(allRequestParam.get("studentId")),
                  Utils.parseLong(allRequestParam.get("courseId")),
                  Utils.parseLong(allRequestParam.get("periodId")),
                  Utils.parseLong(allRequestParam.get("teacherId")),
                  allRequestParam.get("type"),
                  Utils.parseLong(allRequestParam.get("time")),
                  Utils.parseLong(allRequestParam.get("timeMissing")),
                  Utils.parseLong(allRequestParam.get("count")))
              .stream()
              .map(x -> fillIrregularity(x))
              .collect(Collectors.toList());
      return new ResponseEntity<>(els, HttpStatus.OK);
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("/location/")
  public ResponseEntity<?> viewLocation(@RequestParam Map<String, String> allRequestParam) {
    if (allRequestParam.containsKey("apiKey") && isTrusted(allRequestParam.get("apiKey"))) {
      List<Location> list =
          locationService
              .query(
                  Utils.parseLong(allRequestParam.get("locationId")),
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

  @RequestMapping("/period/")
  public ResponseEntity<?> viewPeriod(@RequestParam Map<String, String> allRequestParam) {
    String apiKey = allRequestParam.get("apiKey");
    if (isTrusted(apiKey)) {
      List<Period> els =
          periodService
              .query(
                  Utils.parseLong(allRequestParam.get("periodId")),
                  Utils.parseLong(allRequestParam.get("time")),
                  Utils.parseLong(allRequestParam.get("minDuration")),
                  Utils.parseLong(allRequestParam.get("maxDuration")),
                  Utils.parseLong(allRequestParam.get("initialTimeBegin")),
                  Utils.parseLong(allRequestParam.get("initialTimeEnd")),
                  Utils.parseLong(allRequestParam.get("startTimeBegin")),
                  Utils.parseLong(allRequestParam.get("startTimeEnd")),
                  Utils.parseLong(allRequestParam.get("endTimeBegin")),
                  Utils.parseLong(allRequestParam.get("endTimeEnd")),
                  Utils.parseInteger(allRequestParam.get("period")),
                  Utils.parseLong(allRequestParam.get("courseId")),
                  Utils.parseLong(allRequestParam.get("teacherId")))
              .stream()
              .map(x -> fillPeriod(x))
              .collect(Collectors.toList());
      return new ResponseEntity<>(els, HttpStatus.OK);
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("/schedule/")
  public ResponseEntity<?> viewSchedule(@RequestParam Map<String, String> allRequestParam) {
    if (allRequestParam.containsKey("apiKey") && isTrusted(allRequestParam.get("apiKey"))) {
      List<Schedule> list =
          scheduleService
              .query(
                  Utils.parseLong(allRequestParam.get("scheduleId")),
                  Utils.parseLong(allRequestParam.get("studentId")),
                  Utils.parseLong(allRequestParam.get("courseId")),
                  Utils.parseLong(allRequestParam.get("teacherId")),
                  Utils.parseLong(allRequestParam.get("locationId")),
                  Utils.parseInteger(allRequestParam.get("period")))
              .stream()
              .map(x -> fillSchedule(x))
              .collect(Collectors.toList());
      return new ResponseEntity<>(list, HttpStatus.OK);
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("/session/")
  public ResponseEntity<?> viewSession(@RequestParam Map<String, String> allRequestParam) {
    if (allRequestParam.containsKey("apiKey") && isTrusted(allRequestParam.get("apiKey"))) {
      List<Session> list =
          sessionService
              .query(
                  Utils.parseLong(allRequestParam.get("id")),
                  Utils.parseLong(allRequestParam.get("inEncounterId")),
                  Utils.parseLong(allRequestParam.get("outEncounterId")),
                  Utils.parseLong(allRequestParam.get("anyEncounterId")),
                  Utils.parseLong(allRequestParam.get("periodId")),
                  Utils.parseLong(allRequestParam.get("period")),
                  Utils.parseLong(allRequestParam.get("courseId")),
                  Utils.parseBoolean(allRequestParam.get("complete")),
                  Utils.parseLong(allRequestParam.get("locationId")),
                  Utils.parseLong(allRequestParam.get("studentId")),
                  Utils.parseLong(allRequestParam.get("teacherId")),
                  Utils.parseLong(allRequestParam.get("time")),
                  Utils.parseLong(allRequestParam.get("inTimeBegin")),
                  Utils.parseLong(allRequestParam.get("inTimeEnd")),
                  Utils.parseLong(allRequestParam.get("outTimeBegin")),
                  Utils.parseLong(allRequestParam.get("outTimeEnd")),
                  Utils.parseLong(allRequestParam.get("count")))
              .stream()
              .map(x -> fillSession(x))
              .collect(Collectors.toList());
      return new ResponseEntity<>(list, HttpStatus.OK);
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("/student/")
  public ResponseEntity<?> viewStudent(@RequestParam Map<String, String> allRequestParam) {
    if (allRequestParam.containsKey("apiKey") && isTrusted(allRequestParam.get("apiKey"))) {
      List<Student> list =
          studentService
              .query(
                  Utils.parseLong(allRequestParam.get("studentId")),
                  Utils.parseLong(allRequestParam.get("cardId")),
                  Utils.parseLong(allRequestParam.get("courseId")),
                  Utils.parseInteger(allRequestParam.get("graduatingYear")),
                  allRequestParam.get("name"),
                  allRequestParam.get("partialName"),
                  allRequestParam.get("tags"))
              .stream()
              .map(x -> fillStudent(x))
              .collect(Collectors.toList());
      return new ResponseEntity<>(list, HttpStatus.OK);
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("/user/")
  public ResponseEntity<?> viewUser(@RequestParam Map<String, String> allRequestParam) {
    if (allRequestParam.containsKey("apiKey") && isTrusted(allRequestParam.get("apiKey"))) {
      List<User> list =
          userService
              .query(
                  Utils.parseLong(allRequestParam.get("userId")),
                  allRequestParam.get("name"),
                  allRequestParam.get("email"),
                  Utils.parseInteger(allRequestParam.get("ring")))
              .stream()
              .map(x -> fillUser(x))
              .collect(Collectors.toList());
      return new ResponseEntity<>(list, HttpStatus.OK);
    } else {
      return UNAUTHORIZED;
    }
  }

  /* SPECIAL METHODS */
  @RequestMapping("/validate/")
  public ResponseEntity<?> validateTrusted(@RequestParam("apiKey") String apiKey) {
    return isTrusted(apiKey) ? OK : UNAUTHORIZED;
  }
}
