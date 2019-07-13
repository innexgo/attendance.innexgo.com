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
) ENGINE=InnoDB AUTO_INCREMENT=83 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `api_key`
--

LOCK TABLES `api_key` WRITE;
/*!40000 ALTER TABLE `api_key` DISABLE KEYS */;
INSERT INTO `api_key` VALUES (1,1,'2019-07-07 18:36:41','2038-01-19 03:14:07','03VOkeBEto1idLaHZERYaNcLWntwXv8-IXae2iUB2Zs='),(62,1,'2019-07-13 14:49:37','2019-07-13 15:19:36','04A7HSi4ODAZkCCFZ-LJV3bh4aQLO4oF4IqEYhQVUVU='),(63,2,'2019-07-13 14:50:45','2019-07-13 15:20:44','glIh5xGlNtcaRICXA6O-fo998Jq--resMiDby3sB8JM='),(64,30090004,'2019-07-13 14:51:00','2019-07-13 15:21:00','3p1_lwVUeT6-sdEjU3qPc_ycvCh_dUZc2rQ656961rE='),(65,30090004,'2019-07-13 14:57:58','2019-07-13 15:27:58','35QNBX873xkS8dFTp1A3NLTlWQfHqLwU39jXg_1JXXI='),(66,3,'2019-07-13 15:02:21','2019-07-13 15:32:21','sm70aVNtxxsVBzOqTKmk8-nwC92SkLTGQ7hicwj1x5s='),(67,1,'2019-07-13 15:03:15','2019-07-13 15:33:14','q2hN4Bewra0PXML3elNtiIbI6DjVVsMR8fPTHLRdJeQ='),(68,3,'2019-07-13 15:03:30','2019-07-13 15:33:30','v1kXAoEJqqVz6fCoSBHm8TWWLBgMeeL3HEgq8oUoDcw='),(69,3,'2019-07-13 15:04:24','2019-07-13 15:34:24','t93DeV-rpm9WriSx29cmJ5qKN9jgN7qLfAB9PGgg7YM='),(70,1,'2019-07-13 15:08:55','2019-07-13 15:38:55','HlKHS7aPVUjBe24-9hcOQ3NdObAzSFR9j_XgPhN_aBQ='),(71,2,'2019-07-13 15:11:41','2019-07-13 15:41:41','QcM8Exq5bds2U2Hws7_hSOCmYNz_luJyQKQoi38lquM='),(72,1,'2019-07-13 15:12:35','2019-07-13 15:42:35','MH8zPERMDHwkQ5TgiDAvbpERH26ub9NSasMKW140JsE='),(73,30090004,'2019-07-13 15:12:49','2019-07-13 15:42:49','4NCrSrrvRXAminZKtibT30IiSGRl29yVazSdgmhtIEg='),(74,2,'2019-07-13 15:13:21','2019-07-13 15:43:21','TIs6Ux2SWwlfdoT5sS0GxUCIQ5lJQNt0twaGQ-uuev4='),(75,1,'2019-07-13 15:13:40','2019-07-13 15:43:40','-C76zYef4eOx2ICtxFTyJhHrVSVFDYuUAa6L_TEPiJU='),(76,3,'2019-07-13 15:13:49','2019-07-13 15:43:48','nDitENppC3EXhXlS4IDg_pCuhkoVteU4rsvtsGqpn6A='),(77,2,'2019-07-13 15:32:26','2019-07-13 16:02:26','uEvyL_BftemhAyK2Gi5BYQe6cxJnOLdJpusu6NgMJ8w='),(78,1,'2019-07-13 15:40:37','2019-07-13 16:10:37','PY62NwZksrMWsKb0frhmrb4TvhO9SsJ-zb4u_8Mugu8='),(79,2,'2019-07-13 15:43:52','2019-07-13 16:13:52','cTY7HGHd8orIQd6muGVkbX8f7_5MQIWSMJ3ztbo9vTg='),(80,1,'2019-07-13 15:45:43','2019-07-13 16:15:43','24Q0gY-Owbglmcum7We7UQ4hcRGOOSuL4Ws7xpazNcQ='),(81,2,'2019-07-13 15:55:49','2019-07-13 16:25:49','LGGcuv8BnMja406NKLmhE0-xbASwmGIumRtCWooFEIs='),(82,2,'2019-07-13 15:57:27','2019-07-13 16:27:27','x253VMiiH_x0xJoLEhq2uJHViOwLEJjlRLu87gFJJrE=');
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
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `encounter`
--

LOCK TABLES `encounter` WRITE;
/*!40000 ALTER TABLE `encounter` DISABLE KEYS */;
INSERT INTO `encounter` VALUES (1,'2019-06-29 13:50:39',1,1,'in'),(2,'2019-06-29 13:50:55',1,30090001,'in'),(3,'2019-06-29 13:51:01',1,30090002,'in'),(4,'2019-06-29 13:51:04',1,30090003,'in'),(5,'2019-06-29 13:51:08',1,30090004,'in'),(6,'2019-06-29 13:51:10',1,30090004,'out'),(7,'2019-06-29 13:51:13',1,30090003,'out'),(8,'2019-06-29 13:51:17',1,30090002,'out'),(9,'2019-06-29 13:51:20',1,30090001,'out'),(10,'2019-06-29 13:51:23',1,1,'out'),(11,'2019-06-29 13:51:28',1,2,'in'),(12,'2019-06-29 13:51:33',1,2,'out'),(13,'2019-07-12 20:07:03',1,2,'out'),(14,'2019-07-12 20:07:05',1,2,'in'),(15,'2019-07-12 20:10:02',1,1,'in'),(16,'2019-07-12 20:56:28',1,1,'out'),(17,'2019-07-12 21:15:27',1,30090004,'in'),(18,'2019-07-12 21:15:31',1,30090004,'out'),(21,'2019-07-13 03:35:33',1,1,'in'),(22,'2019-07-13 03:35:35',1,1,'out'),(23,'2019-07-13 03:36:34',1,1,'out'),(24,'2019-07-13 03:37:18',1,1,'in'),(25,'2019-07-13 03:37:20',1,1,'out'),(26,'2019-07-13 15:09:16',1,30090001,'in'),(27,'2019-07-13 15:09:18',1,30090001,'in'),(28,'2019-07-13 15:09:20',1,30090001,'out'),(29,'2019-07-13 15:09:20',1,30090001,'out');
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

-- Dump completed on 2019-07-13  9:08:45
