# Innexo

Innexo is an attendance system for schools that is currently at a very early stage in development. It may become useful later on.

#### Setup instructions

To help with quick set up, innexo.db is filled with example data. The root key can be found in `/rootApiKey.txt`. Obviously, this should be changed if you were to actually deploy this. Similarly, the password and username for the mysql server, found in `/src/main/resources/application.properties` should be changed to be more secure as well. 

An example of setup follows. Change as your own system setup requires.

```
$ # you'll need to create the innexo database first, of course
$ mysql innexo -u root -p < innexo.sql
$ ./gradlew bootRun
```
Use the root api key to run some queries.

