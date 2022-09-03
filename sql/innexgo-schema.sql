create database innexgo_attendance;
\c innexgo_attendance;
--
-- table structure for table api_key
--

drop table if exists api_key;
create table api_key (
  id bigserial primary key,
  user_id bigint not null,
  creation_time bigint not null,
  expiration_time bigint not null,
  key_hash text not null
);

--
-- dumping data for table api_key
--

--
-- table structure for table course
--

drop table if exists course;
create table course (
  id bigserial primary key,
  teacher_id bigint not null,
  location_id bigint not null,
  period bigint not null,
  subject text not null
);

--
-- dumping data for table course
--

--
-- table structure for table encounter
--

drop type if exists encounter_kind cascade;
create type encounter_kind as enum('virtual','manual','default');

drop table if exists encounter;
create table encounter (
  id bigserial primary key,
  time bigint not null,
  kind encounter_kind not null,
  location_id bigint not null,
  student_id bigint not null
);

--
-- dumping data for table encounter
--

--
-- table structure for table grade
--

drop table if exists grade;
create table grade (
  id bigserial primary key,
  semester_start_time bigint not null,
  student_id bigint not null,
  numbering bigint not null
);

--
-- dumping data for table grade
--

--
-- table structure for table irregularity
--

drop type if exists irregularity_kind cascade;
create type irregularity_kind as enum('absent','tardy','leave_noreturn','leave_return','forgot_sign_out');

drop table if exists irregularity;
create table irregularity (
  id bigserial primary key,
  student_id bigint not null,
  course_id bigint not null,
  period_start_time bigint not null,
  kind irregularity_kind not null,
  time bigint not null,
  time_missing bigint not null
);

--
-- dumping data for table irregularity
--

--
-- table structure for table location
--

drop table if exists location;
create table location (
  id bigint not null primary key,
  name text not null
);

--
-- dumping data for table location
--

--
-- table structure for table offering
--

drop table if exists offering;
create table offering (
  id bigserial primary key,
  course_id bigint not null,
  semester_start_time bigint not null
);

--
-- dumping data for table offering
--

--
-- table structure for table period
--

drop type if exists period_kind cascade;
create type period_kind as enum('passing','class','break','lunch','tutorial','none');

drop table if exists period;
create table period (
  start_time bigint not null primary key,
  numbering bigint not null,
  kind period_kind not null,
  temp bool not null
);

--
-- dumping data for table period
--

--
-- table structure for table schedule
--

drop table if exists schedule;
create table schedule (
  id bigserial primary key,
  student_id bigint not null,
  course_id bigint not null,
  has_start bool not null,
  start_time bigint not null,
  has_end bool not null,
  end_time bigint not null
);

--
-- dumping data for table schedule
--

--
-- table structure for table semester
--

drop type if exists semester_kind cascade;
create type semester_kind as enum('summer','fall','spring');

drop table if exists semester;
create table semester (
  start_time bigint not null primary key,
  year bigint not null,
  kind semester_kind not null
);

--
-- dumping data for table semester
--

--
-- table structure for table session
--

drop table if exists session;
create table session (
  id bigserial primary key,
  in_encounter_id bigint not null,
  out_encounter_id bigint not null,
  complete bool not null
);

--
-- dumping data for table session
--

--
-- table structure for table student
--

drop table if exists student;
create table student (
  id bigint not null primary key,
  name text not null
);

--
-- table structure for table appuser
--

drop table if exists appuser;
create table appuser (
  id bigserial primary key,
  name text not null,
  email text not null,
  password_hash text not null,
  ring bigint not null
);

