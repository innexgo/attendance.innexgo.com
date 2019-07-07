-- MariaDB dump 10.17  Distrib 10.4.6-MariaDB, for Linux (x86_64)
--
-- Host: localhost    Database: innexo
-- ------------------------------------------------------
-- Server version	10.4.6-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `api_key`
--

DROP TABLE IF EXISTS `api_key`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `api_key` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creator_id` bigint(20) NOT NULL,
  `creation_time` datetime NOT NULL,
  `expiration_time` datetime NOT NULL,
  `key_hash` char(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `api_key`
--

LOCK TABLES `api_key` WRITE;
/*!40000 ALTER TABLE `api_key` DISABLE KEYS */;
INSERT INTO `api_key` VALUES (1,1,'2019-07-07 18:36:41','2038-01-19 03:14:07','03VOkeBEto1idLaHZERYaNcLWntwXv8-IXae2iUB2Zs='),(2,1,'2019-07-07 18:53:23','2038-01-19 03:14:07','0BbbabuLO8ULqr8yreEv8JZff-j5OANIC8qsTLSA5P8='),(3,1,'2019-07-07 18:53:28','2038-01-19 03:14:07','q4uVyLHx8fQ4ZIkuHdWE0Or4sYFt_YmNmahvyZh4xm0='),(4,1,'2019-07-07 18:53:29','2038-01-19 03:14:07','MEvYsYS4np9ii4M28aa3sCGbaIcoNq8qm22O8jBmFPU='),(5,1,'2019-07-07 18:53:29','2038-01-19 03:14:07','MttTTBOjUEYa3m8EPbUC67EY4XHqMOptQuzIn3peAtY='),(6,1,'2019-07-07 18:53:30','2038-01-19 03:14:07','X6YZlKlez2UtP-bbs5eF3XkOKBik501MR3qeC2dA5pU='),(7,1,'2019-07-07 18:53:31','2038-01-19 03:14:07','YB-Jzo2BQVktpmFyInnXmAW2DFRKsxlkEqAC8wbhm28=');
/*!40000 ALTER TABLE `api_key` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `encounter`
--

DROP TABLE IF EXISTS `encounter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `encounter` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `time` datetime NOT NULL,
  `location_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `type` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `encounter`
--

LOCK TABLES `encounter` WRITE;
/*!40000 ALTER TABLE `encounter` DISABLE KEYS */;
INSERT INTO `encounter` VALUES (1,'2019-06-29 13:50:39',1,1,'in'),(2,'2019-06-29 13:50:55',1,30090001,'in'),(3,'2019-06-29 13:51:01',1,30090002,'in'),(4,'2019-06-29 13:51:04',1,30090003,'in'),(5,'2019-06-29 13:51:08',1,30090004,'in'),(6,'2019-06-29 13:51:10',1,30090004,'out'),(7,'2019-06-29 13:51:13',1,30090003,'out'),(8,'2019-06-29 13:51:17',1,30090002,'out'),(9,'2019-06-29 13:51:20',1,30090001,'out'),(10,'2019-06-29 13:51:23',1,1,'out'),(11,'2019-06-29 13:51:28',1,2,'in'),(12,'2019-06-29 13:51:33',1,2,'out');
/*!40000 ALTER TABLE `encounter` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `location`
--

DROP TABLE IF EXISTS `location`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `location` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tags` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `location`
--

LOCK TABLES `location` WRITE;
/*!40000 ALTER TABLE `location` DISABLE KEYS */;
INSERT INTO `location` VALUES (1,'Main Office','restricted'),(2,'Room 503','classroom');
/*!40000 ALTER TABLE `location` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL,
  `manager_id` bigint(20) DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password_hash` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `administrator` tinyint(1) NOT NULL,
  `trusted_user` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,0,'Adam','$2a$10$QP.tNPcWees66yCZ3w03leoj8j16cXcGxvEcxehRCN7XsawCJGv8y',1,0),(2,0,'Bernard','$2a$10$TBVVpPTEYuR.7mkfOZuVAeoKtYroC0hf7WT8fGYbgRU3mPwQe5eVu',0,1),(30090001,2,'Richard','$2a$10$4iR3OYt7Geg7VzpKq9SpleWWDX/KlfT32hB27ugYuZepradOHnqSO',0,0),(30090002,2,'Dona','$2a$10$Ut6GtTU82WziJQKRFwkbJupE1r.ArBNUHAJ6KH0t/pTLIa3CxdYVy',0,0),(30090003,2,'Daniel','$2a$10$V7qPeZvMgyhw4cVLy/KpzuR.RZjKY//CD8OFT6EUUbmbpcMra8lbK',0,0),(30090004,2,'Govind','$2a$10$LHTj9JJdUFQMzre/gQ76p.5Jkzvl9nnb8x3odxvHVJFkQRRpEFR6y',0,0);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-07-07 11:55:31
