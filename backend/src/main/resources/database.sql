-- MySQL dump 10.13  Distrib 8.0.34, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: my-project-blog
-- ------------------------------------------------------
-- Server version	8.0.34

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `account`
--

DROP TABLE IF EXISTS `account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户唯一标识',
  `username` varchar(255) NOT NULL DEFAULT (concat(_utf8mb4'user_',date_format(now(),_utf8mb4'%Y%m%d%H%i%s'))) COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `email` varchar(255) NOT NULL COMMENT '电子邮箱',
  `role` varchar(255) NOT NULL DEFAULT 'USER' COMMENT '角色',
  `registerTime` datetime NOT NULL DEFAULT (now()) COMMENT '注册时间',
  `active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '账号是否可以使用，1为true，0为false，默认值为1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `account_email_uindex` (`email`),
  UNIQUE KEY `account_username_uindex` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account`
--

LOCK TABLES `account` WRITE;
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
INSERT INTO `account` VALUES (1,'test','$2a$10$KKU6gea8vsYIwExYdkyrhuquVOzFxbHKi/NNqDdbYAPoNLG.59JAW','test@qq.com','ADMIN','2024-08-12 15:03:26',1),(14,'user_20240816201341','$2a$10$P0K9Pd6Kk1ys3iwIvR07W.77B5mPTz0DZhUVjhH0IOWSn4SrT6WJu','test@example.com','USER','2024-08-16 20:13:41',1),(15,'user_20240825231724','$2a$10$Tp0WtUq1kkK7v2CyvBMjpuWDwKA1bCmZK7jWD.CQh7i4su05UawyO','1137656957@qq.com','USER','2024-08-25 23:17:24',1);
/*!40000 ALTER TABLE `account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `article`
--

DROP TABLE IF EXISTS `article`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '文章唯一标识',
  `title` varchar(255) NOT NULL COMMENT '标题',
  `summary` text NOT NULL COMMENT '摘要',
  `content` text NOT NULL COMMENT '内容',
  `authorId` bigint NOT NULL COMMENT '作者唯一标识',
  `createdAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后一次更新时间',
  `status` enum('draft','pending_review','reviewing','approved','archived') DEFAULT 'draft' COMMENT '状态',
  `view` bigint DEFAULT '0' COMMENT '浏览量',
  `like` bigint DEFAULT '0' COMMENT '点赞数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文章';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `article`
--

LOCK TABLES `article` WRITE;
/*!40000 ALTER TABLE `article` DISABLE KEYS */;
INSERT INTO `article` VALUES (3,'test_title','summary_summary','test_content',1,'2024-08-17 16:46:39','2024-08-18 23:52:14','draft',0,0),(7,'hello','summary2','<h1>hello, world!</h1>',1,'2024-08-26 14:05:48','2024-08-26 14:05:48','draft',0,0),(9,'a_title','summary4','<h1>hello, world!</h1>',15,'2024-08-26 15:37:11','2024-08-26 15:37:11','draft',0,0),(10,'b_title','summary5','<h1>hello, world!</h1>',15,'2024-08-26 15:37:11','2024-08-26 15:37:11','draft',0,0),(11,'c_title','summary6','<h1>hello, world!</h1>',15,'2024-08-26 15:37:11','2024-08-26 15:37:11','draft',0,0),(12,'d_title','summary7','<h1>hello, world!</h1>',1,'2024-08-26 15:37:11','2024-08-26 15:37:11','draft',0,0),(13,'e_title','summary8','<h1>hello, world!</h1>',1,'2024-08-26 15:38:15','2024-08-26 15:38:15','draft',0,0),(14,'f_titleasdfasdfsadfsadffffffffffffffffffvcasdfsdafsadfsaefewfsadfffffffffffffffffffffffffffffffffffffffffffffffffffffffff','summary9111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111fdsafasdfffffffffffffffffffffffffffffffffffffffffffffffffsadfsafeqwfewgewgewgewgewgewgewgew','<h1>hello, world!</h1>',15,'2024-08-26 15:38:15','2024-08-26 15:38:15','draft',0,0),(15,'testTitle','summary, summary, summary, summary, summary, summary, summary, summary, summary, summary, summary, summary, summary, summary,','<p><strong><em>hello world!!!!!</em></strong></p>',15,'2024-08-28 13:36:42','2024-08-28 13:36:42','draft',0,0),(16,'newTitlenwTitlecx','thissummarydsfsadnv, fsdjafiehjwio         sadkjfiweuohjfweiofjiweovkniodsfgjviweo\nfdsajfkldjskfljweiofjweinfsakdjfiwesaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaddddddadsaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaadskfjeiwnviwdsoshnvijv','<p><strong class=\"ql-size-large\">cxjhvjuwisngviowuhgbvnweiouhuuuuuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhasdf</strong></p><p><strong class=\"ql-size-large\">adsfjkslad</strong></p><p><br></p><blockquote><strong class=\"ql-size-large\"><em><u>asdfjadslkfjeiowfjewiofhjewsad</u></em></strong></blockquote><p><span style=\"background-color: rgb(255, 255, 0);\">fsdnfljadksfn</span></p><p><br></p><p><br></p><p>newupdated</p><p><span class=\"ql-size-small\">sdffsadf</span></p><p><br></p><p>dsafjdksalfjsadkolfjadkosfjsadkl</p><p>safjsdaokfjio</p>',1,'2024-08-29 16:30:06','2024-08-29 16:34:37','draft',0,0);
/*!40000 ALTER TABLE `article` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-02-06  7:09:27
