#!/usr/bin/python3

import sys
import time
import getpass
import requests
import pandas as pd

# Get while throwing error
def getJSON(url, parameterss):
    try:
        r = requests.get(url, params=params)
        r.raise_for_status()
        return r.json()
    except requests.exceptions.HTTPError as e:
        print('===============ERROR=================')
        print(e)
        print('===> Error occurred. Quit? (y/n)')
        response = input()
        if response != 'y':
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
if len(sys.argv) != 2:
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

df = pd.read_excel(filepath)

# First make list of teachers, load it. Skip teachers who are already in the DB
teachers = df['Teacher'].drop_duplicates()
# Then make list of each Per-CrsName-Room-Teacher unique combo
courses = df[['Per', 'CrsName', 'Teacher', 'Room']].drop_duplicates()
# Then make list of unique StuId-LastName-Firstname-Grade pairs
students = df[['StuID', 'LastName', 'FirstName', 'GR']].drop_duplicates()

# Make user check off iffy ones
# TODO

# Load the teachers into the db
domain_suffix = prompt(f'===> Please enter domain suffix for teacher emails...')
for index, row in teachers.iterrows():
    print('===> Loading Teachers to Database...')
    firstName, lastName = tuple([x.strip in row['Teacher'].split(',')])
    # Generate necessary fields
    userName = firstName + ' ' + lastName
    email = (lastName + firstName[0]).lower()
    password = '1234'
    ring = 1 # Regular User
    print(f'> Adding teacher {userName} with email {email}...')
    getJSON(f'{hostname}/api/user/new/',
            {
                'userName': userName,
                'email': email,
                'password': password,
                'ring': ring,
                'apiKey': apiKey
            })

# Load the courses into the db
for index, row in courses.iterrows():
    print('===> Loading Courses to Database...')
    
# Load the students into the db


for row in df.itertuples():
    name = row[1] + ' ' +  row[2]
    studentId = int(row[3])
    graduatingYear = currentAcademicYear+12-int(row[5])

    # Add schedule entry
    try:
        r = requests.get(f'{hostname}/api/schedule/new/', params={
            'studentId':studentId,
            'courseId':courseId,
            'apiKey':apiKey
        })
        r.raise_for_status()
    except requests.exceptions.HTTPError as e:
        print(e)

