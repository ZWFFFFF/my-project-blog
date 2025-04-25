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
                           `avatar` varchar(255) DEFAULT NULL COMMENT '头像url',
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `account_email_uindex` (`email`),
                           UNIQUE KEY `account_username_uindex` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account`
--

LOCK TABLES `account` WRITE;
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
INSERT INTO `account` VALUES (1,'admin_1','$2a$10$RmLLJez/P18Eg8y7kZRUpurVsMfRvY4Xf6hOEVgu8nOwfTddhwCoC','admin1@example.com','ADMIN','2024-08-12 15:03:26',1,NULL),(14,'user_20240816201341','$2a$10$P0K9Pd6Kk1ys3iwIvR07W.77B5mPTz0DZhUVjhH0IOWSn4SrT6WJu','test@example.com','USER','2024-08-16 20:13:41',1,NULL),(15,'ZWF','$2a$10$tKrLLG5TUcdH3RJf36faPO9dRAlUl9nNkZEeNfjP9QBJ40iFxre/G','1137656957@qq.com','USER','2024-08-25 23:17:24',1,'http://localhost:8080/avatar/5de076d4-3381-4434-ae5a-7b2d513f5116.jpg'),(16,'admin_2','$2a$10$7M9co.x5jyOEEFSNdHlJZOp1FHMtJZyckpxALwaPzB1pt6WLs4t7K','admin2@example.com','ADMIN','2025-03-17 17:04:05',1,NULL);
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
                           `status` enum('draft','pending_review','reviewing','approved','take_down') DEFAULT 'draft' COMMENT '状态',
                           `view` bigint DEFAULT '0' COMMENT '浏览量',
                           `like` bigint DEFAULT '0' COMMENT '点赞数',
                           `previewImage` varchar(255) DEFAULT NULL,
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=60 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文章';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `article`
--

LOCK TABLES `article` WRITE;
/*!40000 ALTER TABLE `article` DISABLE KEYS */;
INSERT INTO `article` VALUES (3,'test_title','summary_summary','test_content',1,'2024-08-17 16:46:39','2024-08-18 23:52:14','approved',0,14,NULL),(7,'hello','summary2','<h1>hello, world!</h1>',1,'2024-08-26 14:05:48','2024-08-26 14:05:48','approved',0,0,NULL),(12,'d_title','summary7','<h1>hello, world!</h1>',1,'2024-08-26 15:37:11','2024-08-26 15:37:11','take_down',0,0,NULL),(13,'e_title','summary8','<h1>hello, world!</h1>',1,'2024-08-26 15:38:15','2024-08-26 15:38:15','approved',0,0,NULL),(16,'newTitlenwTitlecx','thissummarydsfsadnv, fsdjafiehjwio         sadkjfiweuohjfweiofjiweovkniodsfgjviweo\nfdsajfkldjskfljweiofjweinfsakdjfiwesaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaddddddadsaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaadskfjeiwnviwdsoshnvijv','<p><strong class=\"ql-size-large\">cxjhvjuwisngviowuhgbvnweiouhuuuuuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhuhasdf</strong></p><p><strong class=\"ql-size-large\">adsfjkslad</strong></p><p><br></p><blockquote><strong class=\"ql-size-large\"><em><u>asdfjadslkfjeiowfjewiofhjewsad</u></em></strong></blockquote><p><span style=\"background-color: rgb(255, 255, 0);\">fsdnfljadksfn</span></p><p><br></p><p><br></p><p>newupdated</p><p><span class=\"ql-size-small\">sdffsadf</span></p><p><br></p><p>dsafjdksalfjsadkolfjadkosfjsadkl</p><p>safjsdaokfjio</p>',1,'2024-08-29 16:30:06','2024-08-29 16:34:37','approved',0,0,NULL),(20,'meow meowwwwwww!!!!!!','meow meow','<p>caonnnnca cat </p>',15,'2025-02-26 16:36:41','2025-03-20 22:47:09','pending_review',0,0,NULL),(21,'faaf','aaasdf','<p><strong>aa</strong></p>',15,'2025-02-26 16:40:34','2025-02-27 10:57:46','draft',0,0,NULL),(23,'submissionreview','summaryfasdfncccc','<p>test_contentasaaaaaaa</p>',15,'2025-03-04 12:03:44','2025-03-18 00:37:35','pending_review',0,0,NULL),(24,'test3','a','<p>编辑草稿-&gt;保存草稿</p>',15,'2025-03-04 12:34:23','2025-03-04 12:36:27','draft',0,0,NULL),(25,'test2','a','<p>新建文章-&gt;投稿审核</p>',15,'2025-03-04 12:35:10','2025-03-04 12:35:10','reviewing',0,0,NULL),(26,'tt4','a','<h1>编辑草稿-&gt;投稿审核</h1>',15,'2025-03-04 12:37:36','2025-03-04 15:57:43','pending_review',0,0,NULL),(27,'tt1fsadfsdafsdaffdsafssssssfdddddddddddddddddddddddddddddddddddddddddd','随着全球环境问题的日益严峻，人工智能（AI）技术逐渐成为环境保护的重要工具。本文探讨了AI在环境监测、资源管理和生态保护等领域的应用，并分析了其面临的挑战，如数据质量、算法透明性和伦理问题。通过案例分析和未来展望，本文旨在为AI在环境保护中的进一步发展提供参考。','<p>新建文章-&gt;存为草稿</p><p><a href=\"https://vueup.github.io/vue-quill/\" rel=\"noopener noreferrer\" target=\"_blank\">@quill</a></p><p><img src=\"data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAASABIAAD/2wBDAAcFBQYFBAcGBgYIBwcICxILCwoKCxYPEA0SGhYbGhkWGRgcICgiHB4mHhgZIzAkJiorLS4tGyIyNTEsNSgsLSz/2wBDAQcICAsJCxULCxUsHRkdLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCz/wAARCAEsASwDASIAAhEBAxEB/8QAFQABAQAAAAAAAAAAAAAAAAAAAAP/xAAUEAEAAAAAAAAAAAAAAAAAAAAA/8QAFQEBAQAAAAAAAAAAAAAAAAAAAAX/xAAWEQEBAQAAAAAAAAAAAAAAAAAAATH/2gAMAwEAAhEDEQA/ALgKqJoAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEAAAAAAAAAAADMAAAAAAAAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABH//2Q==\" width=\"202\" height=\"202\" style=\"display: block; margin: auto;\" data-align=\"center\">、</p><iframe class=\"ql-video\" frameborder=\"0\" allowfullscreen=\"true\" src=\"https://www.youtube.com/embed/-h6LFqyvF0E?showinfo=0\" data-blot-formatter-unclickable-bound=\"true\" data-align=\"center\" style=\"display: block; margin: auto;\" width=\"542\" height=\"271\"></iframe><p><br></p><p><br></p><iframe class=\"ql-video\" frameborder=\"0\" allowfullscreen=\"true\" src=\"https://www.bilibili.com/video/BV1wnPLeJEoe?t=1.1\" data-blot-formatter-unclickable-bound=\"true\" width=\"543\" height=\"271.5\" style=\"display: block; margin: auto; cursor: nwse-resize;\" data-align=\"center\"></iframe><p><br></p>',15,'2025-03-04 14:34:05','2025-03-25 16:21:45','draft',0,0,NULL),(28,'tt2','a','<p>新建文章-&gt;投稿审核</p>',15,'2025-03-04 14:34:56','2025-03-04 14:34:56','reviewing',0,0,NULL),(30,'文章标题','文章摘要','<h1>文章内容。。。。。<span class=\"ql-size-small\">。。。。</span><span style=\"color: rgb(230, 0, 0);\" class=\"ql-size-small\">。。</span><span style=\"color: rgb(230, 0, 0);\">。。。。。。。。</span>。。。。。。</h1><p><br></p><p>编辑</p><p><strong>dfhweuhgweughweoisdfsad</strong></p><pre class=\"ql-syntax\" spellcheck=\"false\">sdfskldfjsdag\n</pre>',15,'2025-03-12 16:18:26','2025-03-12 16:19:40','take_down',0,0,NULL),(31,'人工智能','在当今快速发展的科技时代，人工智能（AI）已经成为推动社会进步的重要力量。从自动驾驶汽车到智能家居，AI的应用无处不在。然而，随着技术的不断进步，我们也面临着许多挑战和机遇。首先，AI的发展需要大量的数据支持。这些数据不仅包括结构化数据，如数字和文本，还包括非结构化数据，如图像和视频。为了充分利用这些数据，研究人员开发了各种算法和模型，如深度学习、神经网络和自然语言处理（NLP）。这些技术使得机器能够理解和处理复杂的信息，从而做出更准确的决策。','<p>	<strong>在医疗领域，AI的应用已经取得了显著的成果。例如，AI可以帮助医生诊断疾病，如癌症和心脏病。通过分析大量的医疗数据，AI可以发现人类医生难以察觉的模式和趋势。此外，AI还可以用于药物研发，加速新药的发现和临床试验。这不仅节省了时间和成本，还提高了药物的成功率。然而，AI在医疗领域的应用也面临着一些挑战，如数据隐私和伦理问题。如何保护患者的隐私，同时充分利用数据，是一个亟待解决的问题。</strong></p><p>	<strong>在教育领域，AI也展现出了巨大的潜力。个性化学习是AI在教育中的一个重要应用。通过分析学生的学习行为和成绩，AI可以为每个学生提供定制化的学习计划。这不仅提高了学习效率，还激发了学生的学习兴趣。此外，AI还可以用于自动批改作业和考试，减轻教师的工作负担。然而，AI在教育中的应用也引发了一些争议。例如，一些人担心AI会取代教师，导致教育质量的下降。因此，如何在教育中合理使用AI，是一个需要深入探讨的问题。</strong></p><p>	<strong>在金融领域，AI的应用也非常广泛。例如，AI可以用于风险评估和欺诈检测。通过分析大量的交易数据，AI可以发现异常行为，从而预防金融欺诈。此外，AI还可以用于股票市场的预测，帮助投资者做出更明智的决策。然而，AI在金融领域的应用也面临着一些挑战，如数据安全和算法透明度。如何确保AI系统的安全性和可靠性，是一个重要的研究方向。</strong></p><p><strong>在制造业，AI的应用正在改变传统的生产模式。智能工厂是AI在制造业中的一个重要应用。通过使用AI技术，工厂可以实现自动化生产，提高生产效率和产品质量。此外，AI还可以用于预测性维护，通过分析设备的运行数据，预测设备故障，从而减少停机时间和维修成本。然而，AI在制造业中的应用也面临着一些挑战，如技术工人的培训和就业问题。如何在自动化生产中保持人类的就业机会，是一个需要解决的问题。</strong></p><p>	<strong style=\"color: rgb(230, 0, 0);\">在交通领域，AI的应用正在推动自动驾驶技术的发展。自动驾驶汽车是AI在交通中的一个重要应用。通过使用AI技术，自动驾驶汽车可以感知周围环境，做出驾驶决策，从而提高道路安全性和交通效率。此外，AI还可以用于交通管理，通过分析交通数据，优化交通流量，减少拥堵。然而，自动驾驶技术的应用也面临着一些挑战，如法律法规和道德问题。如何确保自动驾驶汽车的安全性和可靠性，是一个需要深入研究的问题</strong><strong>。</strong></p><p>	<strong>在娱乐领域，AI的应用正在改变传统的娱乐方式。例如，AI可以用于内容推荐，通过分析用户的观看历史和偏好，为用户推荐个性化的内容。此外，AI还可以用于游戏开发，通过生成逼真的虚拟世界和角色，提高游戏的沉浸感和互动性。然而，AI在娱乐领域的应用也引发了一些争议。例如，一些人担心AI会侵犯用户的隐私，导致数据滥用。因此，如何在娱乐中合理使用AI，是一个需要探讨的问题。</strong></p><p>	<strong style=\"background-color: rgb(255, 255, 0);\">总之，AI的应用正在改变我们的生活和工作方式。从医疗到教育，从金融到制造业，AI的应用无处不在。然而，随着技术的不断进步，我们也面临着许多挑战和机遇。如何在保护隐私和伦理的前提下，充分利用AI的潜力，是一个需要全社会共同努力的问题。只有通过不断的探索和创新，我们才能充分发挥AI的优势，推动社会的可持续发展。</strong></p>',15,'2025-03-12 16:35:21','2025-03-12 16:36:14','approved',0,0,NULL),(32,'人工智能在环境保护中的应用与挑战','随着全球环境问题的日益严峻，人工智能（AI）技术逐渐成为环境保护的重要工具。本文探讨了AI在环境监测、资源管理和生态保护等领域的应用，并分析了其面临的挑战，如数据质量、算法透明性和伦理问题。通过案例分析和未来展望，本文旨在为AI在环境保护中的进一步发展提供参考。','<blockquote>		<span class=\"ql-size-large\">近年来，全球气候变化、生物多样性丧失和环境污染等问题日益严重，给人类社会和生态系统带来了巨大威胁。为了应对这些挑战，科学家和政策制定者正在寻找创新的解决方案。人工智能（AI）作为一种强大的技术工具，正在环境保护领域展现出巨大的潜力。通过大数据分析、机器学习和自动化技术，AI能够帮助人类更高效地监测环境、管理资源并保护生态系统。</span></blockquote><p><br></p><p><span class=\"ql-size-large\">■</span></p><p><br></p><p><span class=\"ql-size-large\">环境监测是AI应用的一个重要领域。传统的环境监测方法通常依赖于人工采样和实验室分析，耗时且成本高昂。而AI技术可以通过传感器网络和卫星图像实时收集和分析环境数据。例如，AI可以用于监测空气质量、水质和土壤污染。通过机器学习算法，AI能够识别污染源并预测污染扩散趋势，从而帮助政府和企业采取及时的措施。</span></p><p><br></p><p><span class=\"ql-size-large\">■</span></p><p><br></p><p><span class=\"ql-size-large\">AI在资源管理中的应用主要体现在能源、水资源和森林资源的优化利用上。例如，在能源领域，AI可以通过智能电网技术优化电力分配，减少能源浪费。在水资源管理方面，AI可以分析降雨量、河流流量和地下水数据，帮助制定合理的水资源分配计划。此外，AI还可以用于森林资源管理，通过卫星图像和无人机监测森林砍伐和火灾风险。</span></p><p><br></p><p><span class=\"ql-size-large\">■</span></p><p><br></p><p><span class=\"ql-size-large\">AI在生态保护中的应用主要体现在生物多样性监测和濒危物种保护上。例如，AI可以通过图像识别技术自动识别野生动物种类并统计其数量。在海洋保护方面，AI可以分析声纳数据，监测海洋生物的活动和栖息地变化。此外，AI还可以用于预测气候变化对生态系统的影响，帮助制定适应性保护策略。</span></p><p><br></p><p><span class=\"ql-size-large\">■</span></p><p><br></p><p><span class=\"ql-size-large\">AI技术的有效性高度依赖于数据的质量和数量。然而，在环境保护领域，数据的获取往往面临诸多困难。例如，偏远地区的环境数据可能难以收集，或者数据可能存在噪声和不完整性。这些问题会影响AI模型的准确性和可靠性。</span></p><h3><br></h3><p><span class=\"ql-size-large\">■</span></p><p><br></p><p><span class=\"ql-size-large\">AI算法，尤其是深度学习模型，通常被认为是“黑箱”，其决策过程难以解释。在环境保护领域，这种不透明性可能导致公众对AI技术的不信任。例如，如果AI用于制定环境政策，其决策依据必须清晰透明，以确保公平性和可接受性。</span></p><p><br></p><p><span class=\"ql-size-large\">■</span></p><p><br></p><p><span class=\"ql-size-large\">AI在环境保护中的应用也引发了一些伦理问题。例如，AI技术可能被用于监控和控制自然资源，从而影响当地社区的生计。此外，AI技术的开发和部署可能加剧数字鸿沟，使发展中国家和弱势群体无法享受到技术带来的好处。</span></p><p><br></p><p><span class=\"ql-size-large\">■</span></p><p><br></p><p><span class=\"ql-size-large\">亚马逊雨林是全球最大的热带雨林，也是生物多样性最丰富的地区之一。然而，近年来，亚马逊雨林面临着严重的砍伐和火灾威胁。为了应对这一问题，科学家利用AI技术分析卫星图像，实时监测森林砍伐和火灾情况。通过AI的预警系统，政府和环保组织能够及时采取措施，保护雨林生态系统。</span></p><p><br></p><p><span class=\"ql-size-large\">■</span></p><p><br></p><p><span class=\"ql-size-large\">海洋塑料污染是当前全球环境问题之一。为了应对这一问题，研究人员开发了基于AI的海洋垃圾监测系统。通过无人机和卫星图像，AI能够识别海洋中的塑料垃圾并预测其扩散路径。这一技术为海洋塑料污染的治理提供了重要支持。</span></p><p><br></p><p><span class=\"ql-size-large\">■</span></p><p><br></p><p><span class=\"ql-size-large\">随着AI技术的不断进步，其在环境保护中的应用前景广阔。未来，AI可能会在以下几个方面取得突破：</span></p><ul><li><strong class=\"ql-size-large\">更高效的数据收集和处理技术</strong><span class=\"ql-size-large\">：通过物联网（IoT）和5G技术，AI将能够实时收集和分析更大规模的环境数据。</span></li><li><strong class=\"ql-size-large\">更透明的算法设计</strong><span class=\"ql-size-large\">：研究人员正在开发可解释的AI模型，以提高算法的透明性和可信度。</span></li><li><strong class=\"ql-size-large\">更广泛的社会参与</strong><span class=\"ql-size-large\">：通过公众科学（Citizen Science）项目，普通公众可以参与到环境数据的收集中，从而增强AI技术的应用效果。</span></li></ul><p><br></p><p><span class=\"ql-size-large\">■</span></p><p><br></p><p>		<span class=\"ql-size-large\">人工智能在环境保护中的应用为解决全球环境问题提供了新的思路和工具。然而，AI技术的应用也面临着数据质量、算法透明性和伦理问题等挑战。未来，需要政府、企业和科研机构的共同努力，推动AI技术在环境保护中的可持续发展。</span></p>',15,'2025-03-12 16:45:29','2025-03-26 23:36:43','approved',0,31,NULL),(33,'人工智能在医疗领域的应用','人工智能（AI）正在医疗领域掀起一场革命。','<p>		<span class=\"ql-size-large\">通过机器学习和大数据分析，AI能够帮助医生更准确地诊断疾病、制定治疗方案并提高患者护理质量。例如，AI可以通过分析医学影像（如X光片和CT扫描）来检测癌症、心脏病等疾病。研究表明，AI在某些疾病的诊断准确率上已经超过了人类医生。</span></p><p><span class=\"ql-size-large\">此外，AI还在药物研发中发挥着重要作用。传统的药物研发周期长、成本高，而AI可以通过分析大量化学和生物数据，快速筛选出潜在的药物分子，从而加速新药的开发。例如，在COVID-19疫情期间，AI被用于筛选可能的抗病毒药物，为疫苗和治疗方法的研发提供了重要支持。</span></p><p>		<span class=\"ql-size-large\">然而，AI在医疗领域的应用也面临一些挑战。首先是数据隐私问题。医疗数据涉及患者的敏感信息，如何在保护隐私的同时充分利用数据是一个难题。其次是算法的透明性和可解释性。医生和患者需要了解AI的决策过程，才能对其结果产生信任。未来，随着技术的进步和政策的完善，AI有望在医疗领域发挥更大的作用。</span></p>',15,'2025-03-12 16:55:48','2025-03-13 15:26:33','draft',0,0,NULL),(34,'气候变化对农业的影响','气候变化正在对全球农业产生深远的影响。','<p><span class=\"ql-size-large\">极端天气事件（如干旱、洪水和热浪）的频率和强度增加，导致农作物减产和粮食安全问题。例如，在撒哈拉以南非洲地区，干旱已经导致数百万农民面临粮食短缺的威胁。</span></p><p><span class=\"ql-size-large\">此外，气候变化还改变了农作物的生长周期和病虫害的分布。一些地区可能因为温度升高而适合种植新的作物，而另一些地区则可能因为水资源短缺而无法维持传统农业。例如，欧洲南部的葡萄种植区正在向北迁移，以应对气温上升的影响。</span></p><p><span class=\"ql-size-large\">为了应对这些挑战，农业部门正在采取多种措施。例如，推广耐旱作物品种、改进灌溉技术以及采用精准农业技术。精准农业利用传感器和数据分析工具，帮助农民更高效地管理水资源和肥料，从而减少对环境的影响。未来，农业需要更多的创新和合作，以应对气候变化带来的挑战。</span></p>',15,'2025-03-12 16:56:50','2025-03-12 16:56:50','approved',0,0,NULL),(35,'区块链技术的潜力与挑战','区块链技术自诞生以来，一直被认为是具有革命性潜力的技术。','<p><span class=\"ql-size-huge\">其核心特点是去中心化、透明性和不可篡改性，这使得区块链在金融、供应链管理和数据安全等领域具有广泛的应用前景。例如，比特币和其他加密货币就是区块链技术的典型应用。</span></p><p><span class=\"ql-size-huge\">在金融领域，区块链可以简化跨境支付流程，降低交易成本并提高效率。在供应链管理中，区块链可以追踪产品的来源和流通过程，确保产品的真实性和质量。此外，区块链还可以用于保护数据安全，例如在医疗和政府部门中存储敏感信息。</span></p><p><span class=\"ql-size-huge\">然而，区块链技术也面临一些挑战。首先是可扩展性问题。当前的区块链网络在处理大量交易时可能会变得缓慢且昂贵。其次是能源消耗问题。比特币挖矿需要大量的计算资源和电力，这对环境造成了负面影响。未来，随着技术的改进，区块链有望在更多领域得到应用。</span></p>',15,'2025-03-12 16:57:42','2025-03-12 16:57:42','approved',0,0,NULL),(36,'远程办公的兴起与未来','近年来，远程办公逐渐成为一种主流工作方式，尤其是在COVID-19疫情期间，许多企业被迫采用远程办公模式。远程办公不仅提高了员工的工作灵活性，还为企业节省了办公空间和运营成本。例如，科技公司如Twitter和Facebook已经宣布允许员工永久远程办公。','<p><span class=\"ql-size-large\" style=\"background-color: rgb(255, 255, 102);\">然而，远程办公也带来了一些挑战。首先是沟通和协作问题。面对面的交流往往比在线沟通更高效，而远程办公可能导致信息传递不畅和团队凝聚力下降。其次是工作与生活的平衡问题。许多员工在远程办公时难以区分工作时间和个人时间，导致工作压力增加。</span></p><p><span class=\"ql-size-large\" style=\"background-color: rgb(255, 255, 102);\">未来，远程办公可能会成为一种混合模式，即员工部分时间在办公室工作，部分时间在家工作。企业需要投资于更好的远程办公工具和技术，以提高员工的效率和满意度。同时，政府也需要制定相关政策，确保远程办公的公平性和可持续性。</span></p>',15,'2025-03-12 16:58:25','2025-03-12 16:58:25','approved',0,0,NULL),(37,'可再生能源的发展趋势','随着全球对气候变化的关注不断增加，可再生能源（如太阳能、风能和水能）正在成为能源领域的主流选择。与传统化石燃料相比，可再生能源具有清洁、可持续和环保的优势。例如，太阳能和风能的成本在过去十年中大幅下降，使得它们在经济上更具竞争力。','<p><strong class=\"ql-size-large\">然而，可再生能源的发展也面临一些挑战。首先是能源存储问题。太阳能和风能具有间歇性，即它们的供应受天气条件影响。因此，需要开发高效的能源存储技术，以解决能源供应不稳定的问题。其次是基础设施问题。许多地区的电网尚未完全适应可再生能源的接入，需要进行升级和改造。</strong></p><p><strong class=\"ql-size-large\">未来，随着技术的进步和政策的支持，可再生能源有望在全球能源结构中占据更大的份额。例如，电动汽车的普及将推动对清洁能源的需求，而智能电网技术将提高能源分配的效率。可再生能源的发展不仅是应对气候变化的关键，也是实现可持续发展目标的重要途径。</strong></p>',15,'2025-03-12 16:59:07','2025-03-12 16:59:07','approved',0,0,NULL),(38,'article1','summary','<h1>hello, world!</h1>',15,'2025-03-12 17:02:17','2025-03-12 17:02:17','approved',0,0,NULL),(39,'article2','summary','<h1>hello, world!</h1>',15,'2025-03-12 17:04:37','2025-03-12 17:04:37','approved',0,0,NULL),(40,'article3','summary','<h1>hello, world!</h1>',15,'2025-03-12 17:04:37','2025-03-12 17:04:37','approved',0,0,NULL),(41,'article4','summary','<h1>hello, world!</h1>',15,'2025-03-12 17:04:37','2025-03-12 17:04:37','approved',0,0,NULL),(42,'article5','summary','<h1>hello, world!</h1>',15,'2025-03-12 17:04:37','2025-03-12 17:04:37','approved',0,0,NULL),(43,'biaotisdgfsadgdfsggggggffffffffffffffffffffffffffffffffffffffffffffffffffffffffs','摘要受到法国军事大国囧撒旦发生大解放开绿灯撒JFK拉萨大家发撒赖扩大飞机是打开老夫艰苦拉萨的飞机深大路口附近上空砥砺奋进','<p><span class=\"ql-size-large\">文章内容</span></p>',15,'2025-03-12 20:09:43','2025-03-26 23:41:08','draft',0,1,NULL),(45,'文章标题','文章摘要','<p><span class=\"ql-size-large\">文章内容</span></p>',15,'2025-03-18 22:57:47','2025-03-18 22:59:07','pending_review',0,0,NULL),(47,'45测试','45测试','<p>45测试</p>',15,'2025-04-05 18:22:38','2025-04-05 18:22:38','draft',0,0,NULL),(48,'测试','测试','<p>46测试</p>',15,'2025-04-05 18:33:56','2025-04-05 19:53:06','approved',0,1,'http://localhost:8080/articlePreviewImage/dc134a61-2a50-4f97-8916-af94dadec554.jpg'),(50,'测试1','测试1','<p>测试1</p>',15,'2025-04-05 19:52:29','2025-04-05 19:52:29','approved',0,1,'http://localhost:8080/articlePreviewImage/12fce15a-b2ff-4ecf-967d-b9b85f3cb4a3.jpg'),(52,'标题','摘要','<p><strong>sdafsadkjfjksdalfjkalwssdklfjskladjfklsdafj</strong></p><p><strong><em>sdafjsakldf</em></strong></p><p><img src=\"data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAASABIAAD/2wBDAAcFBQYFBAcGBgYIBwcICxILCwoKCxYPEA0SGhYbGhkWGRgcICgiHB4mHhgZIzAkJiorLS4tGyIyNTEsNSgsLSz/2wBDAQcICAsJCxULCxUsHRkdLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCz/wAARCAEsASwDASIAAhEBAxEB/8QAFQABAQAAAAAAAAAAAAAAAAAAAAP/xAAUEAEAAAAAAAAAAAAAAAAAAAAA/8QAFQEBAQAAAAAAAAAAAAAAAAAAAAX/xAAWEQEBAQAAAAAAAAAAAAAAAAAAATH/2gAMAwEAAhEDEQA/ALgKqJoAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEAAAAAAAAAAADMAAAAAAAAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABH//2Q==\" data-align=\"center\" style=\"display: block; margin: auto;\"></p>',15,'2025-04-22 14:43:30','2025-04-22 14:43:40','approved',0,1,'http://localhost:8080/articlePreviewImage/22880422-86c1-4411-b70e-1fed63bbde88.jpg'),(53,'a1','a1','a1',15,'2025-04-23 16:14:59','2025-04-23 16:14:59','approved',0,0,NULL),(54,'a2','a2','a2',15,'2025-04-23 16:17:04','2025-04-23 16:17:04','approved',0,0,NULL),(55,'a3','a3','a3',15,'2025-04-23 16:17:04','2025-04-23 16:17:04','approved',0,0,NULL),(56,'a4','a4','a4',15,'2025-04-23 16:17:04','2025-04-23 16:17:04','approved',0,0,NULL),(57,'a5','a5','a5',15,'2025-04-23 16:17:04','2025-04-23 16:17:04','approved',0,0,NULL),(58,'a6','a6','a6',15,'2025-04-23 16:17:04','2025-04-23 16:17:04','approved',0,0,NULL);
/*!40000 ALTER TABLE `article` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `article_collect`
--

DROP TABLE IF EXISTS `article_collect`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article_collect` (
                                   `id` bigint NOT NULL AUTO_INCREMENT,
                                   `userId` bigint NOT NULL COMMENT '用户Id',
                                   `articleId` bigint NOT NULL COMMENT '文章Id',
                                   `collectedTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
                                   PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `article_collect`
--

LOCK TABLES `article_collect` WRITE;
/*!40000 ALTER TABLE `article_collect` DISABLE KEYS */;
INSERT INTO `article_collect` VALUES (5,15,3,'2025-04-01 23:43:35'),(6,15,50,'2025-04-06 17:13:08'),(7,15,48,'2025-04-06 17:13:16'),(8,15,32,'2025-04-22 14:40:57'),(9,15,52,'2025-04-22 14:50:25');
/*!40000 ALTER TABLE `article_collect` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `article_like`
--

DROP TABLE IF EXISTS `article_like`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article_like` (
                                `id` bigint NOT NULL AUTO_INCREMENT,
                                `userId` bigint NOT NULL,
                                `articleId` bigint NOT NULL,
                                `likeTime` datetime DEFAULT CURRENT_TIMESTAMP,
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `article_like`
--

LOCK TABLES `article_like` WRITE;
/*!40000 ALTER TABLE `article_like` DISABLE KEYS */;
INSERT INTO `article_like` VALUES (3,15,50,'2025-04-06 17:13:07'),(4,15,48,'2025-04-06 17:13:17'),(6,15,32,'2025-04-22 14:40:53'),(7,15,52,'2025-04-22 14:50:23');
/*!40000 ALTER TABLE `article_like` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment` (
                           `id` bigint NOT NULL AUTO_INCREMENT,
                           `parentId` bigint DEFAULT NULL,
                           `content` text,
                           `articleId` bigint DEFAULT NULL,
                           `userId` bigint DEFAULT NULL,
                           `createTime` datetime DEFAULT (now()),
                           `like` bigint DEFAULT '0',
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
INSERT INTO `comment` VALUES (4,NULL,'这是一条父评论',20,15,'2025-03-20 00:29:34',2),(5,NULL,'这是第一条父评论',32,15,'2025-03-28 09:15:20',2),(6,5,'这是第一条子评论',32,14,'2025-03-28 09:15:20',1),(7,5,'这是第二条子评论',32,14,'2025-03-28 09:15:20',1),(8,NULL,'这是第二条父评论',32,14,'2025-03-28 09:16:12',1),(11,NULL,'这是第三条父评论',32,15,'2025-03-31 15:07:03',1),(25,NULL,'测试评论1',32,15,'2025-04-23 13:44:00',0),(26,NULL,'测试评论2',32,15,'2025-04-23 13:44:09',0),(27,NULL,'测试评论3',32,15,'2025-04-23 13:44:14',0),(28,NULL,'测试评论4',32,15,'2025-04-23 13:44:18',0),(29,NULL,'测试评论5',32,15,'2025-04-23 13:44:26',0),(30,NULL,'测试评论6',32,15,'2025-04-23 13:44:32',0),(31,NULL,'测试评论7\n',32,15,'2025-04-23 13:44:49',0),(32,NULL,'测试评论8',32,15,'2025-04-23 13:44:55',0),(33,NULL,'测试评论9',32,15,'2025-04-23 13:45:00',0),(34,NULL,'测试评论10',32,15,'2025-04-23 13:45:09',0);
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `operation_log`
--

DROP TABLE IF EXISTS `operation_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `operation_log` (
                                 `id` bigint NOT NULL AUTO_INCREMENT,
                                 `operation_time` datetime DEFAULT (now()),
                                 `operatorId` bigint DEFAULT NULL,
                                 `operation_type` varchar(255) DEFAULT NULL,
                                 `operation_detail` varchar(255) DEFAULT NULL,
                                 `operation_result` varchar(255) DEFAULT NULL,
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=79 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `operation_log`
--

LOCK TABLES `operation_log` WRITE;
/*!40000 ALTER TABLE `operation_log` DISABLE KEYS */;
INSERT INTO `operation_log` VALUES (1,'2025-03-18 00:25:46',1,'BAN_USER','[15]','SUCCESS'),(2,'2025-03-18 00:26:42',1,'UNBAN_USER','[15]','SUCCESS'),(3,'2025-03-18 00:34:22',1,'TAKE_DOWN_ARTICLE','[[3, 7]]','SUCCESS'),(4,'2025-03-18 00:34:42',1,'RECOVER_ARTICLE','[[3]]','SUCCESS'),(5,'2025-03-18 00:34:47',1,'RECOVER_ARTICLE','[[7]]','SUCCESS'),(6,'2025-03-18 00:36:21',1,'REVIEW_REJECT_ARTICLE','[23]','SUCCESS'),(7,'2025-03-18 00:39:04',16,'BAN_USER','[15]','SUCCESS'),(8,'2025-03-18 00:39:06',16,'UNBAN_USER','[15]','SUCCESS'),(9,'2025-03-18 00:39:08',16,'BAN_USER','[14]','SUCCESS'),(10,'2025-03-18 00:39:10',16,'UNBAN_USER','[14]','SUCCESS'),(11,'2025-03-18 00:39:20',16,'TAKE_DOWN_ARTICLE','[[34, 32]]','SUCCESS'),(12,'2025-03-18 00:39:33',16,'RECOVER_ARTICLE','[[32, 34]]','SUCCESS'),(13,'2025-03-18 11:31:57',16,'TAKE_DOWN_ARTICLE','[[43, 42, 41, 40, 39, 38, 37, 36, 35, 34]]','SUCCESS'),(14,'2025-03-18 11:32:13',16,'RECOVER_ARTICLE','[[34, 35, 36, 37, 38, 39, 40, 41, 42, 43]]','SUCCESS'),(15,'2025-03-18 22:14:57',1,'BAN_USER','[15]','SUCCESS'),(16,'2025-03-18 22:14:57',1,'UNBAN_USER','[15]','SUCCESS'),(56,'2025-03-18 22:25:56',1,'BAN_USER','[15]','SUCCESS'),(57,'2025-03-18 22:26:02',1,'UNBAN_USER','[15]','SUCCESS'),(58,'2025-03-18 22:29:44',1,'TAKE_DOWN_ARTICLE','[[3, 7]]','SUCCESS'),(59,'2025-03-18 22:29:49',1,'RECOVER_ARTICLE','[[3, 7]]','SUCCESS'),(60,'2025-03-26 23:09:31',1,'REVIEW_APPROVE_ARTICLE','[32]','SUCCESS'),(61,'2025-03-26 23:37:22',16,'REVIEW_APPROVE_ARTICLE','[32]','SUCCESS'),(62,'2025-03-26 23:42:59',1,'REVIEW_REJECT_ARTICLE','[43]','SUCCESS'),(63,'2025-04-05 19:51:41',1,'REVIEW_APPROVE_ARTICLE','[49]','SUCCESS'),(64,'2025-04-05 19:51:47',1,'REVIEW_APPROVE_ARTICLE','[48]','SUCCESS'),(65,'2025-04-22 14:44:44',1,'REVIEW_APPROVE_ARTICLE','[52]','SUCCESS'),(66,'2025-04-22 14:45:05',1,'TAKE_DOWN_ARTICLE','[[52]]','SUCCESS'),(67,'2025-04-22 14:45:14',1,'RECOVER_ARTICLE','[[52]]','SUCCESS'),(68,'2025-04-22 14:45:23',1,'BAN_USER','[15]','SUCCESS'),(69,'2025-04-22 14:47:39',16,'UNBAN_USER','[15]','SUCCESS'),(70,'2025-04-24 15:26:18',16,'TAKE_DOWN_ARTICLE','[[31]]','SUCCESS'),(71,'2025-04-24 15:26:27',16,'RECOVER_ARTICLE','[[31]]','SUCCESS'),(72,'2025-04-24 15:26:37',16,'TAKE_DOWN_ARTICLE','[[40]]','SUCCESS'),(73,'2025-04-24 15:26:41',16,'RECOVER_ARTICLE','[[40]]','SUCCESS'),(74,'2025-04-25 13:19:55',1,'BAN_USER','[15]','SUCCESS'),(75,'2025-04-25 13:19:56',1,'UNBAN_USER','[15]','SUCCESS'),(76,'2025-04-25 13:20:14',1,'TAKE_DOWN_ARTICLE','[[54]]','SUCCESS'),(77,'2025-04-25 13:20:20',1,'RECOVER_ARTICLE','[[54]]','SUCCESS'),(78,'2025-04-25 17:20:26',16,'REVIEW_APPROVE_ARTICLE','[59]','SUCCESS');
/*!40000 ALTER TABLE `operation_log` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-04-25 17:37:25
