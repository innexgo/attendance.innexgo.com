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
  `user_id` bigint(20) DEFAULT NULL,
  `creation_time` datetime NOT NULL,
  `expiration_time` datetime NOT NULL,
  `key_hash` char(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=153 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `api_key`
--

LOCK TABLES `api_key` WRITE;
/*!40000 ALTER TABLE `api_key` DISABLE KEYS */;
INSERT INTO `api_key` VALUES (1,1,'2019-07-07 18:36:41','2038-01-19 03:14:07','03VOkeBEto1idLaHZERYaNcLWntwXv8-IXae2iUB2Zs='),(127,1,'2019-07-18 17:03:48','2019-07-18 17:33:48','KnJtEDj5UosX_jKxi-o9kUDM20C3XzrAHJM6GDGtmJ4='),(128,1,'2019-07-19 03:01:04','2019-07-19 03:31:04','zy5aLWFqmnKtY-eS37pRRonXZYoeyKkjZH3P5-LKBms='),(129,1,'2019-07-19 03:01:52','2019-07-19 03:31:51','CoCq8Sp6ThsH_7AB3jYerOUkT3nPae4gdZKCsxTlMU0='),(130,3,'2019-07-19 03:01:59','2019-07-19 03:31:59','QIKrXmP3p9xE6UCLAgMD1CH4xHBtXLQeVhC2XIkWKAk='),(131,2,'2019-07-19 03:32:20','2019-07-19 04:02:20','q-iozVdQLF82iFAii-mHF25UFqHdauApO3cEL8hd5-k='),(132,2,'2019-07-19 04:03:03','2019-07-19 04:33:03','bJXXCSC9jLzFn8wN-WalEI4Sk6F_A3u5OmeGoO3cEv4='),(133,1,'2019-07-19 04:37:00','2019-07-19 05:07:00','EJ2eUWFsh7hT1Qe73fjS1c9ux2vqrUALQTA3P0QV5KI='),(134,2,'2019-07-19 04:37:05','2019-07-19 05:07:05','RzOxi_9_CoAPbatIzn8ZKn8dPxaK924WVsPrUF9xhqw='),(135,2,'2019-07-19 04:42:27','2019-07-19 05:12:27','N_LrQkfMLNw9U9CcyxaW_B495li1eV7PsFi6JreU90U='),(136,1,'2019-07-19 05:02:19','2019-07-19 05:32:19','z-nAHcU-Jh8hZuQTNvibrdXmW-U_thWtkstjJ2nkk2U='),(137,2,'2019-07-19 13:43:57','2019-07-19 14:13:57','2OsKtFFow4PGBAmsB59ovwJhaQQf3zjWvsW2Xq3yEK8='),(138,2,'2019-07-19 17:14:56','2019-07-19 17:44:56','Y_b-E9-jey9oTqWwVwhlCJfgQLd33ZehN8UOaVxV_pI='),(139,2,'2019-07-19 17:15:28','2019-07-19 17:45:28','YT1Vmftm_x1FSst7FgdHTriVDbLiVzT0pp4XfEgsa5Y='),(140,1,'2019-07-19 17:29:10','2019-07-19 17:59:10','1fLQvua-BfvLfnWqBoWJWm5b5aX9FmmrgbXpVsfcDqA='),(141,2,'2019-07-19 17:29:17','2019-07-19 17:59:17','vKsE_r2yEcyF9BYZd7T2wGaClzC-ed03bo22iQAxVUE='),(142,2,'2019-07-19 18:02:18','2019-07-19 18:32:17','dJKDhvbYIP9gThk7Fmg0zynXo8O_x7a662Fs1G91igY='),(143,2,'2019-07-19 18:35:32','2019-07-19 19:05:32','a-FZ0K8cLoRnAukJpyQ1PSdeWzSWA-wGiQ7JgYHgIdk='),(144,2,'2019-07-19 19:05:43','2019-07-19 19:35:43','YzCbBLK-gNet8zmFNiA8R5JhfCsvhuHnbvPaCR9-MFU='),(145,2,'2019-07-19 19:37:13','2019-07-19 20:07:13','nQ7i8sew_sTb0HW6aJNQ4WLpt5H6rk6wUtg96uTYaCY='),(146,2,'2019-07-19 20:30:35','2019-07-19 21:00:35','VNkeOURcs3azZiVMvjOUlxGxDqqNWKMmWqoszolNA5s='),(147,1,'2019-07-19 21:05:50','2019-07-19 21:35:50','HvGnMa3p9t5HZVFDP4QxgCG6D0GsE6rC7qKh3MR_4fE='),(148,2,'2019-07-19 21:18:42','2019-07-19 21:48:42','qX6gm-W_7xOXUFfzjo6v8zny1Ec9-MuMYqC8U0v5bAM='),(149,2,'2019-07-19 21:24:34','2019-07-19 21:54:34','hDENZ6JyLn5hBmES3supgVvpCVTQ5_47sQVuushngD8='),(150,2,'2019-07-20 15:04:37','2019-07-20 15:34:37','TmxcivYzEHLTByQjenBfAtdDnOxqKVE7vCMoFiGx_2g='),(151,2,'2019-07-20 15:36:54','2019-07-20 16:06:54','U2c7ubG_M8djDcZqKYdEdktzldF_k7yWTJ7WglasCd4='),(152,2,'2019-07-20 15:51:08','2019-07-20 16:21:08','qpyoIAVeIAw9kCfinf2niAAPL-K_iDrk_TNC_0FgplA=');
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
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `encounter`
--

LOCK TABLES `encounter` WRITE;
/*!40000 ALTER TABLE `encounter` DISABLE KEYS */;
INSERT INTO `encounter` VALUES (1,'2019-06-29 13:50:39',1,1,'in'),(2,'2019-06-29 13:50:55',1,30090001,'in'),(3,'2019-06-29 13:51:01',1,30090002,'in'),(4,'2019-06-29 13:51:04',1,30090003,'in'),(5,'2019-06-29 13:51:08',1,30090004,'in'),(6,'2019-06-29 13:51:10',1,30090004,'out'),(7,'2019-06-29 13:51:13',1,30090003,'out'),(8,'2019-06-29 13:51:17',1,30090002,'out'),(9,'2019-06-29 13:51:20',1,30090001,'out'),(10,'2019-06-29 13:51:23',1,1,'out'),(11,'2019-06-29 13:51:28',1,2,'in'),(12,'2019-06-29 13:51:33',1,2,'out'),(13,'2019-07-12 20:07:03',1,2,'out'),(14,'2019-07-12 20:07:05',1,2,'in'),(15,'2019-07-12 20:10:02',1,1,'in'),(16,'2019-07-12 20:56:28',1,1,'out'),(17,'2019-07-12 21:15:27',1,30090004,'in'),(18,'2019-07-12 21:15:31',1,30090004,'out'),(21,'2019-07-13 03:35:33',1,1,'in'),(22,'2019-07-13 03:35:35',1,1,'out'),(23,'2019-07-13 03:36:34',1,1,'out'),(24,'2019-07-13 03:37:18',1,1,'in'),(25,'2019-07-13 03:37:20',1,1,'out'),(26,'2019-07-13 15:09:16',1,30090001,'in'),(27,'2019-07-13 15:09:18',1,30090001,'in'),(28,'2019-07-13 15:09:20',1,30090001,'out'),(29,'2019-07-13 15:09:20',1,30090001,'out'),(30,'2019-07-14 15:01:46',1,1,'in'),(31,'2019-07-14 15:02:08',1,30090004,'out');
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
-- Table structure for table `schedule`
--

DROP TABLE IF EXISTS `schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `schedule` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `location_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `period` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `schedule`
--

LOCK TABLES `schedule` WRITE;
/*!40000 ALTER TABLE `schedule` DISABLE KEYS */;
INSERT INTO `schedule` VALUES (1,1,1,1),(2,1,1,2),(3,2,2,2),(4,1,2,1);
/*!40000 ALTER TABLE `schedule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password_hash` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `permission_level` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'Adam','$2a$10$MEsWOlSnhITVH6aMORWJS.eIdX8YMxUfixlSXJJUvMoP8ETkZ/p0G',0),(2,'Bernard','$2a$10$TBVVpPTEYuR.7mkfOZuVAeoKtYroC0hf7WT8fGYbgRU3mPwQe5eVu',1),(3,'Clara','$2a$10$rs6A4mzNJ.CKPDBcN4/1LO003dgDMXd3k32sQToEpXTJmZ0b2sQcO',1),(30090001,'Richard','$2a$10$4iR3OYt7Geg7VzpKq9SpleWWDX/KlfT32hB27ugYuZepradOHnqSO',2),(30090002,'Dona','$2a$10$Ut6GtTU82WziJQKRFwkbJupE1r.ArBNUHAJ6KH0t/pTLIa3CxdYVy',2),(30090003,'Daniel','$2a$10$V7qPeZvMgyhw4cVLy/KpzuR.RZjKY//CD8OFT6EUUbmbpcMra8lbK',2),(30090004,'Govind','$2a$10$LHTj9JJdUFQMzre/gQ76p.5Jkzvl9nnb8x3odxvHVJFkQRRpEFR6y',2);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_relationship`
--

DROP TABLE IF EXISTS `user_relationship`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_relationship` (
  `manager_id` bigint(20) NOT NULL,
  `managed_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_relationship`
--

LOCK TABLES `user_relationship` WRITE;
/*!40000 ALTER TABLE `user_relationship` DISABLE KEYS */;
INSERT INTO `user_relationship` VALUES (1,30090004),(2,30090004),(2,30090001),(2,30090002),(2,30090003),(1,30090001),(1,30090002),(1,30090003),(3,30090004);
/*!40000 ALTER TABLE `user_relationship` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-07-20  8:56:47
