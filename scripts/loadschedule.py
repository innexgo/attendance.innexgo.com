#!/usr/bin/python3

import sys
import time
import getpass
import requests
import pandas as pd

# Get while throwing error
def getJSON(url, parameters):
    try:
        r = requests.get(url, params=parameters)
        r.raise_for_status()
        return r.json()
    except requests.exceptions.HTTPError as e:
        print('===============ERROR=================')
        print(e)
        print('===> Error occurred. Quit? (y/n)')
        response = input()
        if response == 'y':
            print('Quitting...')
            sys.exit(1)
        else:
            return None

def currentMillis():
    return round(1000 * time.time())

def prompt(text):
    print(text)
    return input()

# Bail if not correct
if len(sys.argv) != 3:
    print('===> Error: Need 2 arguments: file as csv and hostname')
    sys.exit(1)




filepath = sys.argv[1]
hostname = sys.argv[2]
apiKey = getJSON(f'{hostname}/api/apiKey/new/',
                 {
                     'email': prompt(f'Enter email for {hostname}:'),
                     'password': prompt(f'Enter password for {hostname}:'),
                     'expirationTime':currentMillis()+30*60*1000
                 })['key']
current_semester_start_time = getJSON(f'{hostname}/api/misc/currentSemester/',
                                      {
                                          'apiKey':apiKey
                                      })['startTime']
domain_suffix = prompt(f'===> Please enter domain suffix for teacher emails...')

df = pd.read_excel(filepath)


# Load the teachers into the db
def loadTeacher(row):
    print('===> Loading Teachers to Database...')
    firstName, lastName = tuple([x.strip() for x in row['Teacher'].split(',')])
    # Generate necessary fields
    userName = f'{firstName} {lastName}'
    email = (lastName + firstName[0]).lower()
    password = '1234'
    ring = 1 # Regular User
    print(f'> Adding teacher {userName} with email {email}...')
    return getJSON(f'{hostname}/api/user/new/',
                   {
                       'userName': userName,
                       'email': email,
                       'password': password,
                       'ring': ring,
                       'apiKey': apiKey
                   })

# Load the locations
def loadLocation(row):
    roomName = row['Room']
    print('===> Loading locations to Database...')
    if roomName.isdigit():
        id = int(roomName)
        name = f'Room {id}'
    elif roomName[0] == 'P': # A portable
        id = 10000 + int(roomName)
        name = roomName
    else:
        id = int(prompt(f'===> Enter id for room named {roomName} (could not autogenerate)'))
        name = roomName
        print(f'> Adding location {roomName} with id {id}...')
        return getJSON(f'{hostname}/api/location/new/',
                       {
                           'locationId':id,
                           'name':name,
                           'apiKey':apiKey
                       })

# Load the courses into the db
def loadCourse(row):
    teacherName = row['Teacher']
    roomName = row['Room']
    print('===> Loading Courses to Database...')
    period = int(row['Per'])
    locationId = int(remote_location_ids[roomName])
    userId = int(remote_teacher_ids[teacherName])
    subject = row['CrsName']
    print(f'> Adding course {subject} taught by {teacherName} at {roomName} on period {period}...')
    return getJSON(f'{hostname}/api/course/new/',
               {
                   'userId':userId,
                   'locationId':locationId,
                   'period':period,
                   'subject':subject,
                   'apiKey':apiKey
               })

# Load the offerings into the db
def loadOffering(row):
    courseId = row['courseJson']['id']
    return getJSON(f'{hostname}/api/offering/new/',
               {
                   'courseId':courseId,
                   'semesterStartTime':current_semester_start_time,
                   'apiKey':apiKey
               })


def loadStudent(row):
    print('===> Loading Students to Database...')
    studentId = int(row['StuID'])
    name = row['FirstName'] + ' ' + row['LastName']
    print(f'> Adding student {name} with id {studentId}...')
    return getJSON(f'{hostname}/api/student/new/',
                   {
                       'studentId':studentId,
                       'name':name,
                       'apiKey':apiKey
                   })
def loadGrade(row):
    studentId = int(row['StuID'])
    number = int(row['GR'])
    print(f'> Adding grade {grade} to student with id {studentId}')
    return getJSON(f'{hostname}/api/grade/new/',
                   {
                       'studentId':studentId,
                       'semesterStartTime':current_semester_start_time,
                       'number':number,
                       'apiKey':apiKey
                   })

def loadSchedule(row):
    print('===> Loading Schedules to Database...')
    studentId = int(row['StuID'])
    period = int(row['Per'])
    courseId = int(row['courseJson']['id'])
    return getJSON(f'{hostname}/api/schedule/new/',
                   {
                       'studentId':studentId,
                       'courseId':courseId,
                       'apiKey':apiKey
                   })


# First make list of teachers, load it. Skip teachers who are already in the DB
teachers = df[['Teacher']].dropna().drop_duplicates()
teachers['userJson'] = teachers.apply(loadTeacher)

# Make list of locations, load it. Skip locations already in DB
locations = df['Room'].dropna().drop_duplicates()
locations['locationJson'] = locations.apply(loadLocation)

# Then make list of each Per-CrsName-Room-Teacher unique combo
courses = df[['Per', 'CrsName', 'Teacher', 'Room']].dropna().drop_duplicates()
courses['courseJson'] = courses.apply(loadCourse)
courses['offeringJson'] = courses.apply(loadOffering)

# Then make list of unique StuId-LastName-Firstname-Grade pairs
students = df[['StuID', 'LastName', 'FirstName', 'GR']].dropna().drop_duplicates()
students['studentJson'] = students.apply(loadStudent)
students['gradeJson'] = students.apply(loadGrade)

# Then make list of schedules
schedules = df[['StuID', 'Per', 'CrsName', 'Teacher', 'Room']] \
        .dropna() \
        .drop_duplicates() \
        .merge(courses, left_on=['Per', 'CrsName', 'Teacher', 'Room'], right_on=['Per', 'CrsName', 'Teacher', 'Room'])
schedules['scheduleJson'] = schedules.apply(loadSchedule)
