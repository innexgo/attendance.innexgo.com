# Innexgo

Innexgo is an attendance system for schools that is currently at a very early stage in development.

**Setup instructions**

Currently, personal setup procedures do not exist. If you do attempt to set it up, you will have to read the source code.

First, you need to create and add a database with tables: api_key, card, course, encounter, irregularity, location, period, schedule, session, student, and user. The root key can be found in `/rootApiKey.txt`. Obviously, this should be changed if you were to actually deploy this. Similarly, the password and username for the mysql server, found in `/src/main/resources/application.properties` should be changed to be something more secure as well. It will default to port 8080.

An example of setup follows. Change as your own system setup requires.

```bash
# you'll need to create the innexgo database first, of course
./gradlew bootRun
```
Use the root api key to run some queries.

Releases follow [Semantic Versioning](https://semver.org/spec/v2.0.0.html).