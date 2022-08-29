--
-- table structure for table api_key
--

drop table if exists api_key;
/*!40101 set @saved_cs_client     = @@character_set_client */;
/*!40101 set character_set_client = utf8 */;
create table api_key (
  id bigserial primary key,
  user_id bigint not null,
  creation_time bigint not null,
  expiration_time bigint not null,
  key_hash text not null unique,
);
/*!40101 set character_set_client = @saved_cs_client */;

--
-- dumping data for table api_key
--

--
-- table structure for table course
--

drop table if exists course;
/*!40101 set @saved_cs_client     = @@character_set_client */;
/*!40101 set character_set_client = utf8 */;
create table course (
  id bigserial primary key,
  teacher_id bigint not null,
  location_id bigint not null,
  period bigint not null,
  subject text not null,
  unique key unique_index (teacher_id,location_id,period,subject)
);
/*!40101 set character_set_client = @saved_cs_client */;

--
-- dumping data for table course
--

--
-- table structure for table encounter
--

drop table if exists encounter;
/*!40101 set @saved_cs_client     = @@character_set_client */;
/*!40101 set character_set_client = utf8 */;
create table encounter (
  id bigserial primary key,
  time bigint not null,
  kind enum('virtual','manual','default') not null,
  location_id bigint not null,
  student_id bigint not null,
);
/*!40101 set character_set_client = @saved_cs_client */;

--
-- dumping data for table encounter
--

--
-- table structure for table grade
--

drop table if exists grade;
/*!40101 set @saved_cs_client     = @@character_set_client */;
/*!40101 set character_set_client = utf8 */;
create table grade (
  id bigserial primary key,
  semester_start_time bigint not null,
  student_id bigint not null,
  numbering bigint not null,
  unique key unique_index (semester_start_time,student_id)
);
/*!40101 set character_set_client = @saved_cs_client */;

--
-- dumping data for table grade
--

--
-- table structure for table irregularity
--

drop table if exists irregularity;
/*!40101 set @saved_cs_client     = @@character_set_client */;
/*!40101 set character_set_client = utf8 */;
create table irregularity (
  id bigserial primary key,
  student_id bigint not null,
  course_id bigint not null,
  period_start_time bigint not null,
  kind enum('absent','tardy','leave_noreturn','leave_return','forgot_sign_out') not null,
  time bigint not null,
  time_missing bigint not null,
);
/*!40101 set character_set_client = @saved_cs_client */;

--
-- dumping data for table irregularity
--

--
-- table structure for table location
--

drop table if exists location;
/*!40101 set @saved_cs_client     = @@character_set_client */;
/*!40101 set character_set_client = utf8 */;
create table location (
  id bigint not null primary key,
  name text not null,
);
/*!40101 set character_set_client = @saved_cs_client */;

--
-- dumping data for table location
--

--
-- table structure for table offering
--

drop table if exists offering;
/*!40101 set @saved_cs_client     = @@character_set_client */;
/*!40101 set character_set_client = utf8 */;
create table offering (
  id bigserial primary key,
  course_id bigint not null,
  semester_start_time bigint not null,
  unique key unique_index (course_id,semester_start_time)
);
/*!40101 set character_set_client = @saved_cs_client */;

--
-- dumping data for table offering
--

--
-- table structure for table period
--

drop table if exists period;
/*!40101 set @saved_cs_client     = @@character_set_client */;
/*!40101 set character_set_client = utf8 */;
create table period (
  start_time bigint not null primary key,
  numbering bigint not null,
  kind enum('passing','class','break','lunch','tutorial','none') not null,
  temp bool not null,
  primary key (start_time)
);
/*!40101 set character_set_client = @saved_cs_client */;

--
-- dumping data for table period
--

--
-- table structure for table schedule
--

drop table if exists schedule;
/*!40101 set @saved_cs_client     = @@character_set_client */;
/*!40101 set character_set_client = utf8 */;
create table schedule (
  id bigserial primary key,
  student_id bigint not null,
  course_id bigint not null,
  has_start bool not null,
  start_time bigint not null,
  has_end bool not null,
  end_time bigint not null,
  unique key unique_index (student_id,course_id)
);
/*!40101 set character_set_client = @saved_cs_client */;

--
-- dumping data for table schedule
--

--
-- table structure for table semester
--

drop table if exists semester;
/*!40101 set @saved_cs_client     = @@character_set_client */;
/*!40101 set character_set_client = utf8 */;
create table semester (
  start_time bigint not null,
  year bigint not null,
  kind enum('summer','fall','spring') not null,
  primary key (start_time)
);
/*!40101 set character_set_client = @saved_cs_client */;

--
-- dumping data for table semester
--

--
-- table structure for table session
--

drop table if exists session;
/*!40101 set @saved_cs_client     = @@character_set_client */;
/*!40101 set character_set_client = utf8 */;
create table session (
  id bigserial primary key,
  in_encounter_id bigint not null,
  out_encounter_id bigint not null,
  complete bool not null,
  primary key (id),
  unique key unique_index (in_encounter_id)
);
/*!40101 set character_set_client = @saved_cs_client */;

--
-- dumping data for table session
--

--
-- table structure for table student
--

drop table if exists student;
/*!40101 set @saved_cs_client     = @@character_set_client */;
/*!40101 set character_set_client = utf8 */;
create table student (
  id bigint not null primary key,
  name text not null,
);
/*!40101 set character_set_client = @saved_cs_client */;

--
-- table structure for table user
--

drop table if exists user;
/*!40101 set @saved_cs_client     = @@character_set_client */;
/*!40101 set character_set_client = utf8 */;
create table user (
  id bigserial primary key,
  name text not null,
  email text not null,
  password_hash text not null,
  ring bigint not null,
  unique key email (email)
);
/*!40101 set character_set_client = @saved_cs_client */;

