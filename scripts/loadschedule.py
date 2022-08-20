#!/usr/bin/python3

import sys
import time
import json
import getpass
import requests
import pandas as pd

INT32_MAX = 0x7FFFFFFF

def currentMillis():
    return round(1000 * time.time())

def prompt(text):
    print(text)
    return input()

def alpha(str):
    return ''.join([i for i in str if i.isalpha()])

def printJson(d):
    json.dumps(d, sort_keys=True, indent=4)

# Get while throwing error
def getJSON(url, parameters):
    try:
        r = requests.get(url, params=parameters)
        r.raise_for_status()
        return r.json()
    except requests.exceptions.HTTPError as e:
        print('===============ERROR=================')
        print(e)
        print('Quitting...')
        sys.exit(1)

def getApiKey(hostname):
    apiKey = getJSON(f'{hostname}/api/apiKey/new/',
                 {
                     'userEmail': prompt(f'Enter an user email to log onto {hostname}:'),
                     'userPassword': prompt(f'Enter password to log onto {hostname}:'),
                     'expirationTime':currentMillis()+60*60*1000 # One hour
                 })
    return apiKey['key']


# Bail if not correct
if len(sys.argv) != 3:
    print('===> Error: Need 2 arguments: file as xlsx and the url of the server')
    sys.exit(1)




filepath = sys.argv[1]
hostname = sys.argv[2]

apiKey = getApiKey(hostname)
df = pd.read_excel(filepath)


if prompt('===> Use Current Semester? (y/n)') == 'y':
    current_semester = getJSON(f'{hostname}/api/misc/getSemesterByTime/',
                                          {
                                              'time':currentMillis(),
                                              'apiKey':apiKey
                                          })
else:
    semesters = getJSON(f'{hostname}/api/semester/',
                                          {
                                              'offset':0,
                                              'count':INT32_MAX,
                                              'apiKey':apiKey
                                          })
    for i, item in enumerate(semesters):
        print(f'> Option {i}:')
        print(item)

    index = int(prompt(f'===> Select semester to load for. Enter integer 0 to {len(semesters)-1}'))
    current_semester = semesters[index]


domain_suffix = prompt(f'===> Please enter domain suffix for teacher emails...')




# Load the teachers into the db
def loadTeacher(row):
    lastName, firstName= tuple([x.strip() for x in row.Teacher.split(',')])
    # Generate necessary fields
    userName = f'{firstName} {lastName}'
    # Create email
    email = f'{alpha(lastName)}{alpha(firstName[0] if len(lastName) > 2 else firstName[0:2])}@{domain_suffix}'.lower()
    password = '1234'
    ring = 1 # Regular User
    existing = getJSON(f'{hostname}/api/user/',
                {
                    'userName': userName,
                    'offset':0,
                    'count':1,
                    'apiKey':apiKey
                })
    if len(existing) > 0:
        print(f'> A user with the name {userName} already exists. Skipping.')
        return existing[0]

    print(f'> Adding teacher {userName} with email {email}...')
    return getJSON(f'{hostname}/api/user/new/',
                   {
                       'userName': userName,
                       'userEmail': email,
                       'userPassword': password,
                       'userRing': ring,
                       'apiKey': apiKey
                   })

# Load the locations
def loadLocation(row):
    roomName = row.Room
    if str(roomName).isdigit():
        locationId = int(roomName)
        name = f'{locationId}'
    elif roomName == "ATTEND"
        locationId = 2000
    elif roomName == "COUNS"
        locationId = 3000
    elif roomName == "GYM":
        locationId = 4000
    elif roomName[0] == 'P': # A portable
        locationId = 5000 + int(roomName[1:])
        name = roomName
    elif roomName == "SVCTE"
        locationId = 6000
    elif roomName == "THTR"
        locationId = 7000
    elif roomName == "WORK"
        locationId = 8000
    else:
        locationId = int(prompt(f'===> Enter id for location {roomName} (could not autogenerate)'))
        name = roomName

    existingLocations = getJSON(f'{hostname}/api/location/',
                {
                    'locationId': locationId,
                    'offset':0,
                    'count':1,
                    'apiKey':apiKey
                })
    if len(existingLocations) > 0:
        print(f'> A location with id {locationId} already exists. Skipping.')
        return existingLocations[0]

    print(f'> Adding location {roomName} with id {locationId}...')
    return getJSON(f'{hostname}/api/location/new/',
                   {
                       'locationId':locationId,
                       'locationName':name,
                       'apiKey':apiKey
                   })

# Load the courses into the db
def loadCourse(row):
    teacherName = row.Teacher
    roomName = row.Room
    period = int(row.Per)
    locationId = int(row.locationJson['id'])
    userId = int(row.userJson['id'])
    courseName = row.CrsName

    existing = getJSON(f'{hostname}/api/course/',
                {
                    'userId': userId,
                    'locationId': locationId,
                    'courseSubject':courseName,
                    'periodNumber':period,
                    'offset':0,
                    'count':1,
                    'apiKey':apiKey
                })

    if len(existing) > 0:
        print(f'> A course named {courseName} is already taught by {teacherName} on period {period}. Skipping.')
        return existing[0]

    print(f'> Adding course {courseName} taught by {teacherName} at {roomName} on period {period}')
    return getJSON(f'{hostname}/api/course/new/',
               {
                   'userId':userId,
                   'locationId':locationId,
                   'periodNumber':period,
                   'courseSubject':courseName,
                   'apiKey':apiKey
               })

# Load the offerings into the db
def loadOffering(row):
    courseId = row.courseJson['id']
    courseName = row.courseJson['subject']
    subject = row.CrsName
    semesterStartTime = current_semester['startTime']
    semesterName = f'{current_semester["year"]} {current_semester["kind"]}'


    existing = getJSON(f'{hostname}/api/offering/',
                {
                    'courseId': courseId,
                    'semesterStartTime': semesterStartTime,
                    'offset':0,
                    'count':1,
                    'apiKey':apiKey
                })

    if len(existing) > 0:
        print(f'> A offering for {courseName} in {semesterName} already exists. Skipping.')
        return existing[0]

    print(f'> Adding course offering for {subject} for {semesterName}')
    return getJSON(f'{hostname}/api/offering/new/',
               {
                   'courseId':courseId,
                   'semesterStartTime':semesterStartTime,
                   'apiKey':apiKey
               })


def loadStudent(row):
    studentId = int(row.StuID)
    name = row.FirstName + ' ' + row.LastName

    existing = getJSON(f'{hostname}/api/student/',
                {
                    'studentId': studentId,
                    'offset':0,
                    'count':1,
                    'apiKey':apiKey
                })
    if len(existing) > 0:
        print(f'> A student with id {studentId} already exists. Skipping.')
        return existing[0]

    print(f'> Adding student {name} with id {studentId}...')
    return getJSON(f'{hostname}/api/student/new/',
                   {
                       'studentId':studentId,
                       'studentName':name,
                       'apiKey':apiKey
                   })
def loadGrade(row):
    studentId = int(row.StuID)
    number = int(row.GR)
    studentName = row.studentJson['name']
    semesterStartTime = current_semester['startTime']
    semesterName = f'{current_semester["year"]} {current_semester["kind"]}'

    existing = getJSON(f'{hostname}/api/grade/',
                {
                    'studentId': studentId,
                    'semesterStartTime': studentId,
                    'offset':0,
                    'count':1,
                    'apiKey':apiKey
                })

    if len(existing) > 0:
        print(f'> A grade for {studentName} in {semesterName} exists. Skipping.')
        return existing[0]


    print(f'> Adding grade of {number} to {studentName}')
    return getJSON(f'{hostname}/api/grade/new/',
                   {
                       'studentId':studentId,
                       'semesterStartTime':semesterStartTime,
                       'gradeNumbering':number,
                       'apiKey':apiKey
                   })

def loadSchedule(row):
    studentId = int(row.StuID)
    period = int(row.Per)
    courseId = int(row.courseJson['id'])
    courseName = row.courseJson['subject']
    studentName = row.studentJson['name']

    existing = getJSON(f'{hostname}/api/schedule/',
                {
                    'studentId': studentId,
                    'courseId': courseId,
                    'periodNumber': period,
                    'offset':0,
                    'count':1,
                    'apiKey':apiKey
                })

    if len(existing) > 0:
        print(f'> A schedule for {studentName} in {courseName} at period {period} already exists. Skipping.')
        return existing[0]


    print(f'> Adding schedule linking {studentName} to {row.CrsName} for period {period}')
    return getJSON(f'{hostname}/api/schedule/new/',
                   {
                       'studentId':studentId,
                       'courseId':courseId,
                       'apiKey':apiKey
                   })


teachers = df[['Teacher']].dropna().drop_duplicates()
print('===> Loading Teachers to Database...')
teachers['userJson'] = teachers.apply(loadTeacher, axis=1)

# Make list of locations, load it. Skip locations already in DB
locations = df[['Room']].dropna().drop_duplicates()
print('===> Loading Locations to Database...')
locations['locationJson'] = locations.apply(loadLocation, axis=1)

# Then make list of unique StuId-LastName-Firstname-Grade pairs
students = df[['StuID', 'LastName', 'FirstName', 'GR']].dropna().drop_duplicates()
print('===> Loading Students to Database...')
students['studentJson'] = students.apply(loadStudent, axis=1)
print('===> Loading Grades to Database...')
students['gradeJson'] = students.apply(loadGrade, axis=1)


# Then make list of each Per-CrsName-Room-Teacher unique combo
courses = df[['Per', 'CrsName', 'Teacher', 'Room']] \
        .dropna() \
        .drop_duplicates() \
        .merge(locations, left_on=['Room'], right_on=['Room']) \
        .merge(teachers, left_on=['Teacher'], right_on=['Teacher'])
print('===> Loading Courses to Database...')
courses['courseJson'] = courses.apply(loadCourse, axis=1)
print('===> Loading Offering to Database...')
courses['offeringJson'] = courses.apply(loadOffering, axis=1)

# Then make list of schedules
schedules = df[['StuID', 'Per', 'CrsName', 'Teacher', 'Room']] \
        .dropna() \
        .drop_duplicates() \
        .merge(courses,
               left_on=['Per', 'CrsName', 'Teacher', 'Room'],
               right_on=['Per', 'CrsName', 'Teacher', 'Room']) \
        .merge(students, left_on=['StuID'], right_on=['StuID'])
print('===> Loading Schedules to Database...')
schedules['scheduleJson'] = schedules.apply(loadSchedule, axis=1)
# finished
print('===> Finished successfully!');
