-- MySQL dump 10.13  Distrib 5.7.44, for Linux (x86_64)
--
-- Host: localhost    Database: jsh_erp
-- ------------------------------------------------------
-- Server version	5.7.44-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `jsh_account`
--

DROP TABLE IF EXISTS `jsh_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jsh_account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(50) DEFAULT NULL COMMENT '名称',
  `serial_no` varchar(50) DEFAULT NULL COMMENT '编号',
  `initial_amount` decimal(24,6) DEFAULT NULL COMMENT '期初金额',
  `current_amount` decimal(24,6) DEFAULT NULL COMMENT '当前余额',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  `enabled` bit(1) DEFAULT NULL COMMENT '启用',
  `sort` varchar(10) DEFAULT NULL COMMENT '排序',
  `is_default` bit(1) DEFAULT NULL COMMENT '是否默认',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8 COMMENT='账户信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jsh_account`
--

LOCK TABLES `jsh_account` WRITE;
/*!40000 ALTER TABLE `jsh_account` DISABLE KEYS */;
INSERT INTO `jsh_account` VALUES (17,'账户1','zzz111',100.000000,829.000000,'aabb',_binary '',NULL,_binary '',63,'1'),(18,'账户2','1234131324',200.000000,-1681.000000,'bbbb',_binary '',NULL,_binary '\0',63,'1'),(24,'工商银行账户','3602894709100095455',0.000000,0.000000,'中国工商银行广州番禺德贤支行',_binary '',NULL,_binary '',63,'0'),(25,'现金账户','CASH-001',0.000000,0.000000,'日常现金收支',_binary '',NULL,_binary '\0',63,'0');
/*!40000 ALTER TABLE `jsh_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jsh_account_head`
--

DROP TABLE IF EXISTS `jsh_account_head`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jsh_account_head` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `type` varchar(50) DEFAULT NULL COMMENT '类型(支出/收入/收款/付款/转账)',
  `organ_id` bigint(20) DEFAULT NULL COMMENT '单位Id(收款/付款单位)',
  `hands_person_id` bigint(20) DEFAULT NULL COMMENT '经手人id',
  `creator` bigint(20) DEFAULT NULL COMMENT '操作员',
  `change_amount` decimal(24,6) DEFAULT NULL COMMENT '变动金额(优惠/收款/付款/实付)',
  `discount_money` decimal(24,6) DEFAULT NULL COMMENT '优惠金额',
  `total_price` decimal(24,6) DEFAULT NULL COMMENT '合计金额',
  `account_id` bigint(20) DEFAULT NULL COMMENT '账户(收款/付款)',
  `bill_no` varchar(50) DEFAULT NULL COMMENT '单据编号',
  `bill_time` datetime DEFAULT NULL COMMENT '单据日期',
  `remark` varchar(1000) DEFAULT NULL COMMENT '备注',
  `file_name` varchar(500) DEFAULT NULL COMMENT '附件名称',
  `status` varchar(1) DEFAULT NULL COMMENT '状态，0未审核、1已审核、9审核中',
  `source` varchar(1) DEFAULT '0' COMMENT '单据来源，0-pc，1-手机',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`),
  KEY `FK9F4C0D8DB610FC06` (`organ_id`),
  KEY `FK9F4C0D8DAAE50527` (`account_id`),
  KEY `FK9F4C0D8DC4170B37` (`hands_person_id`)
) ENGINE=InnoDB AUTO_INCREMENT=127 DEFAULT CHARSET=utf8 COMMENT='财务主表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jsh_account_head`
--

LOCK TABLES `jsh_account_head` WRITE;
/*!40000 ALTER TABLE `jsh_account_head` DISABLE KEYS */;
/*!40000 ALTER TABLE `jsh_account_head` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jsh_account_item`
--

DROP TABLE IF EXISTS `jsh_account_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jsh_account_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `header_id` bigint(20) NOT NULL COMMENT '表头Id',
  `account_id` bigint(20) DEFAULT NULL COMMENT '账户Id',
  `in_out_item_id` bigint(20) DEFAULT NULL COMMENT '收支项目Id',
  `bill_id` bigint(20) DEFAULT NULL COMMENT '单据id',
  `need_debt` decimal(24,6) DEFAULT NULL COMMENT '应收欠款',
  `finish_debt` decimal(24,6) DEFAULT NULL COMMENT '已收欠款',
  `each_amount` decimal(24,6) DEFAULT NULL COMMENT '单项金额',
  `remark` varchar(500) DEFAULT NULL COMMENT '单据备注',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`),
  KEY `FK9F4CBAC0AAE50527` (`account_id`),
  KEY `FK9F4CBAC0C5FE6007` (`header_id`),
  KEY `FK9F4CBAC0D203EDC5` (`in_out_item_id`)
) ENGINE=InnoDB AUTO_INCREMENT=152 DEFAULT CHARSET=utf8 COMMENT='财务子表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jsh_account_item`
--

LOCK TABLES `jsh_account_item` WRITE;
/*!40000 ALTER TABLE `jsh_account_item` DISABLE KEYS */;
/*!40000 ALTER TABLE `jsh_account_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jsh_depot`
--

DROP TABLE IF EXISTS `jsh_depot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jsh_depot` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(20) DEFAULT NULL COMMENT '仓库名称',
  `address` varchar(50) DEFAULT NULL COMMENT '仓库地址',
  `warehousing` decimal(24,6) DEFAULT NULL COMMENT '仓储费',
  `truckage` decimal(24,6) DEFAULT NULL COMMENT '搬运费',
  `type` int(10) DEFAULT NULL COMMENT '类型',
  `sort` varchar(10) DEFAULT NULL COMMENT '排序',
  `remark` varchar(100) DEFAULT NULL COMMENT '描述',
  `principal` bigint(20) DEFAULT NULL COMMENT '负责人',
  `enabled` bit(1) DEFAULT NULL COMMENT '启用',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `delete_Flag` varchar(1) DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  `is_default` bit(1) DEFAULT NULL COMMENT '是否默认',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8 COMMENT='仓库表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jsh_depot`
--

LOCK TABLES `jsh_depot` WRITE;
/*!40000 ALTER TABLE `jsh_depot` DISABLE KEYS */;
INSERT INTO `jsh_depot` VALUES (14,'仓库1','dizhi',12.000000,12.000000,0,'1','描述',131,_binary '',63,'1',_binary ''),(15,'仓库2','地址100',555.000000,666.000000,0,'2','dfdf',131,_binary '',63,'1',_binary '\0'),(17,'仓库3','123123',123.000000,123.000000,0,'3','123',131,_binary '',63,'1',_binary '\0'),(19,'广州原料仓','广州市番禺区小谷围街大学城外环西路1号',0.000000,0.000000,0,'10','存放生产原材料',19,_binary '',63,'0',_binary '\0'),(20,'广州半成品仓','广州市番禺区小谷围街大学城外环西路1号',0.000000,0.000000,0,'11','存放未配饰/装裱的半成品',19,_binary '',63,'0',_binary '\0'),(21,'广州成品仓','广州市番禺区小谷围街大学城外环西路1号',0.000000,0.000000,0,'12','存放已完成的成品',19,_binary '',63,'0',_binary ''),(22,'广西生产基地仓','广西南宁市',0.000000,0.000000,0,'20','广西生产基地的仓库',24,_binary '',63,'0',_binary '\0'),(23,'深圳景之蓝仓','深圳市',0.000000,0.000000,0,'30','深圳景之蓝的虚拟仓库',23,_binary '',63,'0',_binary '\0'),(24,'中山纪念堂寄售点','广州市',0.000000,0.000000,0,'31','中山纪念堂代销点',20,_binary '',63,'0',_binary '\0'),(25,'广州美术馆寄售点','广州市',0.000000,0.000000,0,'32','广州美术馆代销点',21,_binary '',63,'0',_binary '\0'),(26,'摩登百货寄售点','广州市',0.000000,0.000000,0,'33','摩登百货代销点',20,_binary '',63,'0',_binary '\0'),(27,'番禺新华书店寄售点','广州市番禺区',0.000000,0.000000,0,'34','番禺新华书店代销点',21,_binary '',63,'0',_binary '\0');
/*!40000 ALTER TABLE `jsh_depot` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jsh_depot_head`
--

DROP TABLE IF EXISTS `jsh_depot_head`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jsh_depot_head` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `type` varchar(50) DEFAULT NULL COMMENT '类型(出库/入库)',
  `sub_type` varchar(50) DEFAULT NULL COMMENT '出入库分类',
  `default_number` varchar(50) DEFAULT NULL COMMENT '初始票据号',
  `number` varchar(50) DEFAULT NULL COMMENT '票据号',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `oper_time` datetime DEFAULT NULL COMMENT '出入库时间',
  `organ_id` bigint(20) DEFAULT NULL COMMENT '供应商id',
  `creator` bigint(20) DEFAULT NULL COMMENT '操作员',
  `account_id` bigint(20) DEFAULT NULL COMMENT '账户id',
  `change_amount` decimal(24,6) DEFAULT NULL COMMENT '变动金额(收款/付款)',
  `back_amount` decimal(24,6) DEFAULT NULL COMMENT '找零金额',
  `total_price` decimal(24,6) DEFAULT NULL COMMENT '合计金额',
  `pay_type` varchar(50) DEFAULT NULL COMMENT '付款类型(现金、记账等)',
  `bill_type` varchar(50) DEFAULT NULL COMMENT '单据类型',
  `remark` varchar(1000) DEFAULT NULL COMMENT '备注',
  `file_name` varchar(1000) DEFAULT NULL COMMENT '附件名称',
  `sales_man` varchar(50) DEFAULT NULL COMMENT '销售员（可以多个）',
  `account_id_list` varchar(50) DEFAULT NULL COMMENT '多账户ID列表',
  `account_money_list` varchar(200) DEFAULT NULL COMMENT '多账户金额列表',
  `discount` decimal(24,6) DEFAULT NULL COMMENT '优惠率',
  `discount_money` decimal(24,6) DEFAULT NULL COMMENT '优惠金额',
  `discount_last_money` decimal(24,6) DEFAULT NULL COMMENT '优惠后金额',
  `other_money` decimal(24,6) DEFAULT NULL COMMENT '销售或采购费用合计',
  `deposit` decimal(24,6) DEFAULT NULL COMMENT '订金',
  `status` varchar(1) DEFAULT NULL COMMENT '状态，0未审核、1已审核、2完成采购|销售、3部分采购|销售、9审核中',
  `purchase_status` varchar(1) DEFAULT NULL COMMENT '采购状态，0未采购、2完成采购、3部分采购',
  `source` varchar(1) DEFAULT '0' COMMENT '单据来源，0-pc，1-手机',
  `link_number` varchar(50) DEFAULT NULL COMMENT '关联订单号',
  `link_apply` varchar(50) DEFAULT NULL COMMENT '关联请购单',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`),
  KEY `FK2A80F214B610FC06` (`organ_id`),
  KEY `FK2A80F214AAE50527` (`account_id`)
) ENGINE=InnoDB AUTO_INCREMENT=277 DEFAULT CHARSET=utf8 COMMENT='单据主表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jsh_depot_head`
--

LOCK TABLES `jsh_depot_head` WRITE;
/*!40000 ALTER TABLE `jsh_depot_head` DISABLE KEYS */;
/*!40000 ALTER TABLE `jsh_depot_head` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jsh_depot_item`
--

DROP TABLE IF EXISTS `jsh_depot_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jsh_depot_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `header_id` bigint(20) NOT NULL COMMENT '表头Id',
  `material_id` bigint(20) NOT NULL COMMENT '商品Id',
  `material_extend_id` bigint(20) DEFAULT NULL COMMENT '商品扩展id',
  `material_unit` varchar(20) DEFAULT NULL COMMENT '商品单位',
  `sku` varchar(50) DEFAULT NULL COMMENT '多属性',
  `oper_number` decimal(24,6) DEFAULT NULL COMMENT '数量',
  `basic_number` decimal(24,6) DEFAULT NULL COMMENT '基础数量，如kg、瓶',
  `unit_price` decimal(24,6) DEFAULT NULL COMMENT '单价',
  `purchase_unit_price` decimal(24,6) DEFAULT NULL COMMENT '采购单价',
  `tax_unit_price` decimal(24,6) DEFAULT NULL COMMENT '含税单价',
  `all_price` decimal(24,6) DEFAULT NULL COMMENT '金额',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `depot_id` bigint(20) DEFAULT NULL COMMENT '仓库ID',
  `another_depot_id` bigint(20) DEFAULT NULL COMMENT '调拨时，对方仓库Id',
  `tax_rate` decimal(24,6) DEFAULT NULL COMMENT '税率',
  `tax_money` decimal(24,6) DEFAULT NULL COMMENT '税额',
  `tax_last_money` decimal(24,6) DEFAULT NULL COMMENT '价税合计',
  `material_type` varchar(20) DEFAULT NULL COMMENT '商品类型',
  `sn_list` varchar(2000) DEFAULT NULL COMMENT '序列号列表',
  `batch_number` varchar(100) DEFAULT NULL COMMENT '批号',
  `expiration_date` datetime DEFAULT NULL COMMENT '有效日期',
  `link_id` bigint(20) DEFAULT NULL COMMENT '关联明细id',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`),
  KEY `FK2A819F475D61CCF7` (`material_id`),
  KEY `FK2A819F474BB6190E` (`header_id`),
  KEY `FK2A819F479485B3F5` (`depot_id`),
  KEY `FK2A819F47729F5392` (`another_depot_id`)
) ENGINE=InnoDB AUTO_INCREMENT=334 DEFAULT CHARSET=utf8 COMMENT='单据子表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jsh_depot_item`
--

LOCK TABLES `jsh_depot_item` WRITE;
/*!40000 ALTER TABLE `jsh_depot_item` DISABLE KEYS */;
/*!40000 ALTER TABLE `jsh_depot_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jsh_function`
--

DROP TABLE IF EXISTS `jsh_function`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jsh_function` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `number` varchar(50) DEFAULT NULL COMMENT '编号',
  `name` varchar(50) DEFAULT NULL COMMENT '名称',
  `parent_number` varchar(50) DEFAULT NULL COMMENT '上级编号',
  `url` varchar(100) DEFAULT NULL COMMENT '链接',
  `component` varchar(100) DEFAULT NULL COMMENT '组件',
  `state` bit(1) DEFAULT NULL COMMENT '收缩',
  `sort` varchar(50) DEFAULT NULL COMMENT '排序',
  `enabled` bit(1) DEFAULT NULL COMMENT '启用',
  `type` varchar(50) DEFAULT NULL COMMENT '类型',
  `push_btn` varchar(50) DEFAULT NULL COMMENT '功能按钮',
  `icon` varchar(50) DEFAULT NULL COMMENT '图标',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `url` (`url`)
) ENGINE=InnoDB AUTO_INCREMENT=262 DEFAULT CHARSET=utf8 COMMENT='功能模块表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jsh_function`
--

LOCK TABLES `jsh_function` WRITE;
/*!40000 ALTER TABLE `jsh_function` DISABLE KEYS */;
INSERT INTO `jsh_function` VALUES (1,'0001','系统管理','0','/system','/layouts/TabLayout',_binary '','0910',_binary '','电脑版','','setting','0'),(13,'000102','角色管理','0001','/system/role','/system/RoleList',_binary '\0','0130',_binary '','电脑版','1','profile','0'),(14,'000103','用户管理','0001','/system/user','/system/UserList',_binary '\0','0140',_binary '','电脑版','1','profile','0'),(15,'000104','日志管理','0001','/system/log','/system/LogList',_binary '\0','0160',_binary '','电脑版','','profile','0'),(16,'000105','功能管理','0001','/system/function','/system/FunctionList',_binary '\0','0166',_binary '','电脑版','1','profile','0'),(18,'000109','租户管理','0001','/system/tenant','/system/TenantList',_binary '\0','0167',_binary '','电脑版','1','profile','0'),(21,'0101','商品管理','0','/material','/layouts/TabLayout',_binary '\0','0620',_binary '','电脑版',NULL,'shopping','0'),(22,'010101','商品类别','0101','/material/material_category','/material/MaterialCategoryList',_binary '\0','0230',_binary '','电脑版','1','profile','0'),(23,'010102','商品信息','0101','/material/material','/material/MaterialList',_binary '\0','0240',_binary '','电脑版','1,3','profile','0'),(24,'0102','基础资料','0','/systemA','/layouts/TabLayout',_binary '\0','0750',_binary '','电脑版',NULL,'appstore','0'),(25,'01020101','供应商信息','0102','/system/vendor','/system/VendorList',_binary '\0','0260',_binary '','电脑版','1,3','profile','0'),(26,'010202','仓库信息','0102','/system/depot','/system/DepotList',_binary '\0','0270',_binary '','电脑版','1','profile','0'),(31,'010206','经手人管理','0102','/system/person','/system/PersonList',_binary '\0','0284',_binary '','电脑版','1','profile','0'),(32,'0502','采购管理','0','/bill','/layouts/TabLayout',_binary '\0','0330',_binary '','电脑版','','retweet','0'),(33,'050201','采购入库','0502','/bill/purchase_in','/bill/PurchaseInList',_binary '\0','0340',_binary '','电脑版','1,2,3,7','profile','0'),(38,'0603','销售管理','0','/billB','/layouts/TabLayout',_binary '\0','0390',_binary '','电脑版','','shopping-cart','0'),(40,'080107','调拨出库','0801','/bill/allocation_out','/bill/AllocationOutList',_binary '\0','0807',_binary '','电脑版','1,2,3,7','profile','0'),(41,'060303','销售出库','0603','/bill/sale_out','/bill/SaleOutList',_binary '\0','0394',_binary '','电脑版','1,2,3,7','profile','0'),(44,'0704','财务管理','0','/financial','/layouts/TabLayout',_binary '\0','0450',_binary '','电脑版','','money-collect','0'),(59,'030101','进销存统计','0301','/report/in_out_stock_report','/report/InOutStockReport',_binary '\0','0658',_binary '','电脑版','','profile','0'),(194,'010204','收支项目','0102','/system/in_out_item','/system/InOutItemList',_binary '\0','0282',_binary '','电脑版','1','profile','0'),(195,'010205','结算账户','0102','/system/account','/system/AccountList',_binary '\0','0283',_binary '','电脑版','1','profile','0'),(197,'070402','收入单','0704','/financial/item_in','/financial/ItemInList',_binary '\0','0465',_binary '','电脑版','1,2,3,7','profile','0'),(198,'0301','报表查询','0','/report','/layouts/TabLayout',_binary '\0','0570',_binary '','电脑版',NULL,'pie-chart','0'),(199,'050204','采购退货','0502','/bill/purchase_back','/bill/PurchaseBackList',_binary '\0','0345',_binary '','电脑版','1,2,3,7','profile','0'),(200,'060305','销售退货','0603','/bill/sale_back','/bill/SaleBackList',_binary '\0','0396',_binary '','电脑版','1,2,3,7','profile','0'),(201,'080103','其它入库','0801','/bill/other_in','/bill/OtherInList',_binary '\0','0803',_binary '','电脑版','1,2,3,7','profile','0'),(202,'080105','其它出库','0801','/bill/other_out','/bill/OtherOutList',_binary '\0','0805',_binary '','电脑版','1,2,3,7','profile','0'),(203,'070403','支出单','0704','/financial/item_out','/financial/ItemOutList',_binary '\0','0470',_binary '','电脑版','1,2,3,7','profile','0'),(204,'070404','收款单','0704','/financial/money_in','/financial/MoneyInList',_binary '\0','0475',_binary '','电脑版','1,2,3,7','profile','0'),(205,'070405','付款单','0704','/financial/money_out','/financial/MoneyOutList',_binary '\0','0480',_binary '','电脑版','1,2,3,7','profile','0'),(206,'070406','转账单','0704','/financial/giro','/financial/GiroList',_binary '\0','0490',_binary '','电脑版','1,2,3,7','profile','0'),(207,'030102','账户统计','0301','/report/account_report','/report/AccountReport',_binary '\0','0610',_binary '','电脑版','','profile','0'),(208,'030103','采购统计','0301','/report/buy_in_report','/report/BuyInReport',_binary '\0','0620',_binary '','电脑版','','profile','0'),(209,'030104','销售统计','0301','/report/sale_out_report','/report/SaleOutReport',_binary '\0','0630',_binary '','电脑版','','profile','0'),(210,'040102','零售出库','0401','/bill/retail_out','/bill/RetailOutList',_binary '\0','0405',_binary '','电脑版','1,2,3,7','profile','0'),(211,'040104','零售退货','0401','/bill/retail_back','/bill/RetailBackList',_binary '\0','0407',_binary '','电脑版','1,2,3,7','profile','0'),(212,'070407','收预付款','0704','/financial/advance_in','/financial/AdvanceInList',_binary '\0','0495',_binary '','电脑版','1,2,3,7','profile','0'),(217,'01020102','客户信息','0102','/system/customer','/system/CustomerList',_binary '\0','0262',_binary '','电脑版','1,3','profile','0'),(218,'01020103','会员信息','0102','/system/member','/system/MemberList',_binary '\0','0263',_binary '','电脑版','1,3','profile','0'),(220,'010103','多单位','0101','/system/unit','/system/UnitList',_binary '\0','0245',_binary '','电脑版','1','profile','0'),(225,'0401','零售管理','0','/billC','/layouts/TabLayout',_binary '\0','0101',_binary '','电脑版','','gift','0'),(226,'030106','入库明细','0301','/report/in_detail','/report/InDetail',_binary '\0','0640',_binary '','电脑版','','profile','0'),(227,'030107','出库明细','0301','/report/out_detail','/report/OutDetail',_binary '\0','0645',_binary '','电脑版','','profile','0'),(228,'030108','入库汇总','0301','/report/in_material_count','/report/InMaterialCount',_binary '\0','0650',_binary '','电脑版','','profile','0'),(229,'030109','出库汇总','0301','/report/out_material_count','/report/OutMaterialCount',_binary '\0','0655',_binary '','电脑版','','profile','0'),(232,'080109','组装单','0801','/bill/assemble','/bill/AssembleList',_binary '\0','0809',_binary '','电脑版','1,2,3,7','profile','0'),(233,'080111','拆卸单','0801','/bill/disassemble','/bill/DisassembleList',_binary '\0','0811',_binary '','电脑版','1,2,3,7','profile','0'),(234,'000105','系统配置','0001','/system/system_config','/system/SystemConfigList',_binary '\0','0164',_binary '','电脑版','1','profile','0'),(235,'030110','客户对账','0301','/report/customer_account','/report/CustomerAccount',_binary '\0','0660',_binary '','电脑版','','profile','0'),(236,'000106','商品属性','0001','/material/material_property','/material/MaterialPropertyList',_binary '\0','0165',_binary '','电脑版','1','profile','0'),(237,'030111','供应商对账','0301','/report/vendor_account','/report/VendorAccount',_binary '\0','0665',_binary '','电脑版','','profile','0'),(239,'0801','仓库管理','0','/billD','/layouts/TabLayout',_binary '\0','0420',_binary '','电脑版','','hdd','0'),(241,'050202','采购订单','0502','/bill/purchase_order','/bill/PurchaseOrderList',_binary '\0','0335',_binary '','电脑版','1,2,3,7','profile','0'),(242,'060301','销售订单','0603','/bill/sale_order','/bill/SaleOrderList',_binary '\0','0392',_binary '','电脑版','1,2,3,7','profile','0'),(243,'000108','机构管理','0001','/system/organization','/system/OrganizationList',_binary '','0150',_binary '','电脑版','1','profile','0'),(244,'030112','库存预警','0301','/report/stock_warning_report','/report/StockWarningReport',_binary '\0','0670',_binary '','电脑版','','profile','0'),(245,'000107','插件管理','0001','/system/plugin','/system/PluginList',_binary '\0','0170',_binary '','电脑版','1','profile','0'),(246,'030113','商品库存','0301','/report/material_stock','/report/MaterialStock',_binary '\0','0605',_binary '','电脑版','','profile','0'),(247,'010105','多属性','0101','/material/material_attribute','/material/MaterialAttributeList',_binary '\0','0250',_binary '','电脑版','1','profile','0'),(248,'030150','调拨明细','0301','/report/allocation_detail','/report/AllocationDetail',_binary '\0','0646',_binary '','电脑版','','profile','0'),(258,'000112','平台配置','0001','/system/platform_config','/system/PlatformConfigList',_binary '\0','0175',_binary '','电脑版','','profile','0'),(259,'030105','零售统计','0301','/report/retail_out_report','/report/RetailOutReport',_binary '\0','0615',_binary '','电脑版','','profile','0'),(261,'050203','请购单','0502','/bill/purchase_apply','/bill/PurchaseApplyList',_binary '\0','0330',_binary '','电脑版','1,2,3,7','profile','0');
/*!40000 ALTER TABLE `jsh_function` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jsh_in_out_item`
--

DROP TABLE IF EXISTS `jsh_in_out_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jsh_in_out_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(50) DEFAULT NULL COMMENT '名称',
  `type` varchar(20) DEFAULT NULL COMMENT '类型',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  `enabled` bit(1) DEFAULT NULL COMMENT '启用',
  `sort` varchar(10) DEFAULT NULL COMMENT '排序',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8 COMMENT='收支项目';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jsh_in_out_item`
--

LOCK TABLES `jsh_in_out_item` WRITE;
/*!40000 ALTER TABLE `jsh_in_out_item` DISABLE KEYS */;
INSERT INTO `jsh_in_out_item` VALUES (21,'快递费','支出','',_binary '',NULL,63,'1'),(22,'房租收入','收入','',_binary '',NULL,63,'1'),(23,'利息收入','收入','收入',_binary '',NULL,63,'1'),(28,'团建收入','收入','团建沙龙收入',_binary '',NULL,63,'0'),(29,'产品销售','收入','产品销售收入',_binary '',NULL,63,'0'),(30,'印象咖收入','收入','咖啡店收入',_binary '',NULL,63,'0'),(31,'原材料采购','支出','采购原材料支出',_binary '',NULL,63,'0'),(32,'生产加工费','支出','广西生产基地加工费',_binary '',NULL,63,'0'),(33,'销售提成','支出','销售人员提成',_binary '',NULL,63,'0');
/*!40000 ALTER TABLE `jsh_in_out_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jsh_log`
--

DROP TABLE IF EXISTS `jsh_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jsh_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `operation` varchar(500) DEFAULT NULL COMMENT '操作模块名称',
  `client_ip` varchar(200) DEFAULT NULL COMMENT '客户端IP',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `status` tinyint(4) DEFAULT NULL COMMENT '操作状态 0==成功，1==失败',
  `content` varchar(5000) DEFAULT NULL COMMENT '详情',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  PRIMARY KEY (`id`),
  KEY `FKF2696AA13E226853` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7650 DEFAULT CHARSET=utf8 COMMENT='操作日志';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jsh_log`
--

LOCK TABLES `jsh_log` WRITE;
/*!40000 ALTER TABLE `jsh_log` DISABLE KEYS */;
INSERT INTO `jsh_log` VALUES (7605,63,'用户','127.0.0.1','2025-05-07 00:54:06',0,'登录jsh',63),(7606,63,'商品','127.0.0.1','2025-05-07 02:14:43',0,'修改衣服',63),(7607,63,'用户','127.0.0.1','2025-05-07 02:31:48',0,'修改63',63),(7608,63,'关联关系','127.0.0.1','2025-05-07 02:31:48',0,'修改',63),(7609,63,'用户','0:0:0:0:0:0:0:1','2025-05-07 02:37:24',0,'登录waterxi',63),(7610,63,'用户','127.0.0.1','2025-05-07 02:40:30',0,'登录waterxi',63),(7611,63,'机构','0:0:0:0:0:0:0:1','2025-05-07 02:47:41',0,'删除[机构2]',63),(7612,63,'机构','0:0:0:0:0:0:0:1','2025-05-07 02:47:44',0,'删除[机构1]',63),(7613,63,'机构','0:0:0:0:0:0:0:1','2025-05-07 02:47:47',0,'删除[测试机构]',63),(7614,63,'用户','0:0:0:0:0:0:0:1','2025-05-07 02:47:58',0,'删除[test123]',63),(7615,63,'用户','0:0:0:0:0:0:0:1','2025-05-07 02:48:07',0,'修改63',63),(7616,63,'关联关系','0:0:0:0:0:0:0:1','2025-05-07 02:48:07',0,'修改',63),(7617,120,'用户','127.0.0.1','2025-05-07 07:42:35',0,'登录admin',0),(7618,63,'用户','0:0:0:0:0:0:0:1','2025-05-07 07:43:55',0,'登录waterxi',63),(7619,63,'用户','127.0.0.1','2025-05-07 10:09:36',0,'登录waterxi',63),(7620,63,'角色','0:0:0:0:0:0:0:1','2025-05-07 10:10:47',0,'新增合伙人',63),(7621,63,'关联关系','0:0:0:0:0:0:0:1','2025-05-07 10:11:10',0,'新增',63),(7622,63,'关联关系','127.0.0.1','2025-05-07 10:11:16',0,'修改角色的按钮权限',63),(7623,63,'商品','127.0.0.1','2025-05-07 10:12:33',0,'删除[商品1][商品2][商品3][商品8][商品17][序列号商品测试][商品test1][商品200]',63),(7624,63,'仓库','127.0.0.1','2025-05-07 10:13:02',0,'删除[仓库1][仓库2]',63),(7625,63,'仓库','127.0.0.1','2025-05-07 10:13:06',0,'删除[仓库3]',63),(7626,63,'仓库','0:0:0:0:0:0:0:1','2025-05-07 10:14:03',0,'更新状态',63),(7627,63,'用户','0:0:0:0:0:0:0:1','2025-05-07 10:16:54',0,'登录waterxi',63),(7628,63,'收支项目','0:0:0:0:0:0:0:1','2025-05-07 23:34:10',0,'删除[房租收入]',63),(7629,63,'收支项目','127.0.0.1','2025-05-07 23:34:14',0,'删除[利息收入]',63),(7630,63,'收支项目','127.0.0.1','2025-05-07 23:34:21',0,'删除[快递费]',63),(7631,63,'账户','0:0:0:0:0:0:0:1','2025-05-07 23:34:37',0,'删除[账户1][账户2]',63),(7632,63,'经手人','127.0.0.1','2025-05-07 23:35:04',0,'删除[小李][小军][小夏][小曹]',63),(7633,63,'商家','127.0.0.1','2025-05-07 23:35:16',0,'删除[客户1][客户2][客户3]',63),(7634,63,'商家','0:0:0:0:0:0:0:1','2025-05-07 23:35:30',0,'删除[供应商1][供应商3][供应商5]',63),(7635,63,'商品类型','0:0:0:0:0:0:0:1','2025-05-07 23:35:38',0,'删除[目录2]',63),(7636,63,'商品类型','0:0:0:0:0:0:0:1','2025-05-07 23:35:42',0,'删除[目录1]',63),(7637,120,'用户','127.0.0.1','2025-05-08 00:45:45',0,'登录admin',0),(7638,63,'用户','127.0.0.1','2025-05-10 11:14:08',0,'登录waterxi',63),(7639,63,'商品','127.0.0.1','2025-05-10 11:17:40',0,'修改印象奶茶-珍珠',63),(7640,63,'商品','127.0.0.1','2025-05-10 11:17:53',0,'修改印象奶茶-珍珠',63),(7641,120,'用户','127.0.0.1','2025-05-10 11:33:30',0,'登录admin',0),(7642,63,'用户','0:0:0:0:0:0:0:1','2025-05-10 21:40:37',0,'登录waterxi',63),(7643,63,'商品','127.0.0.1','2025-05-10 21:40:58',0,'修改印象奶茶-珍珠',63),(7644,63,'用户','0:0:0:0:0:0:0:1','2025-05-13 13:06:23',0,'登录waterxi',63),(7645,63,'用户','127.0.0.1','2025-05-18 05:43:26',0,'登录waterxi',63),(7646,63,'商品','127.0.0.1','2025-05-18 08:59:58',0,'修改印象咖啡-美式',63),(7647,120,'用户','0:0:0:0:0:0:0:1','2025-06-11 05:57:25',0,'登录admin',0),(7648,63,'用户','0:0:0:0:0:0:0:1','2025-06-11 05:57:52',0,'登录waterxi',63),(7649,63,'用户','0:0:0:0:0:0:0:1','2025-06-11 06:35:03',0,'登录waterxi',63);
/*!40000 ALTER TABLE `jsh_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jsh_material`
--

DROP TABLE IF EXISTS `jsh_material`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jsh_material` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `category_id` bigint(20) DEFAULT NULL COMMENT '产品类型id',
  `name` varchar(100) DEFAULT NULL COMMENT '名称',
  `mfrs` varchar(50) DEFAULT NULL COMMENT '制造商',
  `model` varchar(100) DEFAULT NULL COMMENT '型号',
  `standard` varchar(100) DEFAULT NULL COMMENT '规格',
  `brand` varchar(100) DEFAULT NULL COMMENT '品牌',
  `mnemonic` varchar(100) DEFAULT NULL COMMENT '助记码',
  `color` varchar(50) DEFAULT NULL COMMENT '颜色',
  `unit` varchar(50) DEFAULT NULL COMMENT '单位-单个',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `img_name` varchar(1000) DEFAULT NULL COMMENT '图片名称',
  `unit_id` bigint(20) DEFAULT NULL COMMENT '单位Id',
  `expiry_num` int(10) DEFAULT NULL COMMENT '保质期天数',
  `weight` decimal(24,6) DEFAULT NULL COMMENT '基础重量(kg)',
  `enabled` bit(1) DEFAULT NULL COMMENT '启用 0-禁用  1-启用',
  `other_field1` varchar(50) DEFAULT NULL COMMENT '自定义1',
  `other_field2` varchar(50) DEFAULT NULL COMMENT '自定义2',
  `other_field3` varchar(50) DEFAULT NULL COMMENT '自定义3',
  `enable_serial_number` varchar(1) DEFAULT '0' COMMENT '是否开启序列号，0否，1是',
  `enable_batch_number` varchar(1) DEFAULT '0' COMMENT '是否开启批号，0否，1是',
  `position` varchar(100) DEFAULT NULL COMMENT '仓位货架',
  `attribute` varchar(1000) DEFAULT NULL COMMENT '多属性信息',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`),
  KEY `FK675951272AB6672C` (`category_id`),
  KEY `UnitId` (`unit_id`)
) ENGINE=InnoDB AUTO_INCREMENT=642 DEFAULT CHARSET=utf8 COMMENT='产品表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jsh_material`
--

LOCK TABLES `jsh_material` WRITE;
/*!40000 ALTER TABLE `jsh_material` DISABLE KEYS */;
INSERT INTO `jsh_material` VALUES (568,17,'商品1','制1','sp1','',NULL,NULL,'','个','',NULL,NULL,NULL,NULL,_binary '','','','','0','0',NULL,NULL,63,'1'),(569,17,'商品2','','sp2','',NULL,NULL,'','只','',NULL,NULL,NULL,NULL,_binary '','','','','0','0',NULL,NULL,63,'1'),(570,17,'商品3','','sp3','',NULL,NULL,'','个','',NULL,NULL,NULL,NULL,_binary '','','','','0','0',NULL,NULL,63,'1'),(577,NULL,'商品8','','sp8','',NULL,NULL,'','','',NULL,15,NULL,NULL,_binary '','','','','0','0',NULL,NULL,63,'1'),(579,21,'商品17','','sp17','',NULL,NULL,'','','',NULL,15,NULL,NULL,_binary '','','','','0','0',NULL,NULL,63,'1'),(586,17,'序列号商品测试','','xlh123','',NULL,NULL,'','个','',NULL,NULL,NULL,NULL,_binary '','','','','1','0',NULL,NULL,63,'1'),(587,17,'商品test1','南通中远','','test1',NULL,NULL,'','个','',NULL,NULL,NULL,NULL,_binary '','','','','0','0',NULL,NULL,63,'1'),(588,21,'商品200','fafda','weqwe','300ml',NULL,NULL,'红色','个','aaaabbbbb',NULL,NULL,NULL,NULL,_binary '','','','','0','0',NULL,NULL,63,'1'),(619,NULL,'衣服',NULL,NULL,NULL,NULL,NULL,NULL,'件',NULL,'material/63/WechatIMG142_1746555280892.jpg',NULL,NULL,NULL,_binary '',NULL,NULL,NULL,'0','0',NULL,'{}',63,'0'),(620,35,'唐卡铜胎底料(小)','优质供应商','DT-TT-S',NULL,NULL,NULL,'铜色','个','用于小型掐丝珐琅唐卡的铜底胎','dt_tt_s.jpg',15,NULL,NULL,_binary '',NULL,NULL,NULL,'0','0','原料区-A01',NULL,63,'0'),(621,35,'唐卡铝塑板底料(中)','品质厂家','DT-LSB-M',NULL,NULL,NULL,'银白色','个','用于中型掐丝珐琅唐卡的铝塑板底胎','dt_lsb_m.jpg',15,NULL,NULL,_binary '',NULL,NULL,NULL,'0','0','原料区-A02',NULL,63,'0'),(622,35,'珐琅彩饰品玉石底料','玉石供应商','DT-YS-SP',NULL,NULL,NULL,'白玉色','个','用于珐琅彩饰品的玉石底胎','dt_ys_sp.jpg',15,NULL,NULL,_binary '',NULL,NULL,NULL,'0','0','原料区-A03',NULL,63,'0'),(623,35,'黑檀木底料','木材供应商','DT-HTM',NULL,NULL,NULL,'黑色','个','用于部分饰品和摆件的黑檀木底胎','dt_htm.jpg',15,NULL,NULL,_binary '',NULL,NULL,NULL,'0','0','原料区-A04',NULL,63,'0'),(624,36,'花梨木画框(中)','框艺坊','HK-HLM-M',NULL,NULL,NULL,'深红色','个','用于中型掐丝珐琅唐卡的装裱','hk_hlm_m.jpg',15,NULL,NULL,_binary '',NULL,NULL,NULL,'0','0','装裱区-B01',NULL,63,'0'),(625,36,'欧式摆画框(小)','艺框坊','HK-OS-S',NULL,NULL,NULL,'金色','个','用于小型掐丝珐琅作品的欧式画框','hk_os_s.jpg',15,NULL,NULL,_binary '',NULL,NULL,NULL,'0','0','装裱区-B02',NULL,63,'0'),(626,37,'贝壳珍珠(中)','饰品供应商','PS-BK-M',NULL,NULL,NULL,'珍珠白','个','用于珐琅彩饰品的点缀','ps_bk_m.jpg',15,NULL,NULL,_binary '',NULL,NULL,NULL,'0','0','配饰区-C01',NULL,63,'0'),(627,37,'玛瑙珠(小)','饰品供应商','PS-MN-S',NULL,NULL,NULL,'红色','个','用于珐琅彩饰品的点缀','ps_mn_s.jpg',15,NULL,NULL,_binary '',NULL,NULL,NULL,'0','0','配饰区-C02',NULL,63,'0'),(628,37,'挂穗(传统红)','饰品供应商','PS-GS-TR',NULL,NULL,NULL,'中国红','个','用于唐卡作品的装饰挂穗','ps_gs_tr.jpg',15,NULL,NULL,_binary '',NULL,NULL,NULL,'0','0','配饰区-C03',NULL,63,'0'),(629,37,'项链绳(绿)','饰品供应商','PS-XLS-G',NULL,NULL,NULL,'深绿色','个','用于珐琅彩项链的绳子','ps_xls_g.jpg',15,NULL,NULL,_binary '',NULL,NULL,NULL,'0','0','配饰区-C04',NULL,63,'0'),(630,38,'珐琅画包装盒(中)','包装供应商','BZ-HH-M',NULL,NULL,NULL,'黑色','个','中型珐琅画专用包装盒','bz_hh_m.jpg',15,NULL,NULL,_binary '',NULL,NULL,NULL,'0','0','包装区-D01',NULL,63,'0'),(631,38,'饰品盒(小)','包装供应商','BZ-SPH-S',NULL,NULL,NULL,'红色','个','小型珐琅彩饰品专用包装盒','bz_sph_s.jpg',15,NULL,NULL,_binary '',NULL,NULL,NULL,'0','0','包装区-D02',NULL,63,'0'),(632,33,'吉祥八宝掐丝珐琅唐卡','聆花文化','QSFL-TK-JX8B',NULL,NULL,NULL,'金色/蓝色','幅','传统唐卡图案，采用掐丝点蓝工艺，金色铜底胎，蓝色珐琅釉填充，吉祥图案寓意美好','qsfl_tk_jx8b.jpg',22,NULL,NULL,_binary '',NULL,NULL,NULL,'0','0','成品区-E01','{\"设计风格\":\"唐卡风\",\"制作工艺\":\"掐丝点蓝\",\"材质\":\"铜胎\"}',63,'0'),(633,33,'金刚橙掐丝珐琅唐卡','聆花文化','QSFL-TK-JGC',NULL,NULL,NULL,'金色/橙色','幅','传统唐卡图案，采用掐丝点蓝工艺，金色铜底胎，橙色珐琅釉填充，金刚造型，庄严威武','qsfl_tk_jgc.jpg',22,NULL,NULL,_binary '',NULL,NULL,NULL,'0','0','成品区-E02','{\"设计风格\":\"唐卡风\",\"制作工艺\":\"掐丝点蓝\",\"材质\":\"铜胎\"}',63,'0'),(634,33,'富贵牡丹掐丝珐琅画','聆花文化','QSFL-FSH-MD',NULL,NULL,NULL,'金色/红色','幅','中式风水画，采用掐丝点蓝工艺，铝塑板底胎，红色珐琅釉填充，寓意富贵','qsfl_fsh_md.jpg',22,NULL,NULL,_binary '',NULL,NULL,NULL,'0','0','成品区-E03','{\"设计风格\":\"中国风\",\"制作工艺\":\"掐丝点蓝\",\"材质\":\"铝塑板\"}',63,'0'),(635,33,'年年有余掐丝珐琅画','聆花文化','QSFL-FSH-NNYY',NULL,NULL,NULL,'金色/蓝色','幅','中式风水画，采用掐丝点蓝工艺，铝塑板底胎，蓝色珐琅釉填充，寓意年年有余','qsfl_fsh_nnyy.jpg',22,NULL,NULL,_binary '',NULL,NULL,NULL,'0','0','成品区-E04','{\"设计风格\":\"中国风\",\"制作工艺\":\"掐丝点蓝\",\"材质\":\"铝塑板\"}',63,'0'),(636,34,'祥云珐琅彩项链','聆花文化','FLCSP-XY-XL',NULL,NULL,NULL,'蓝色/金色','件','传统祥云图案珐琅彩项链，铜胎点蓝工艺，配贝壳珍珠点缀','flcsp_xy_xl.jpg',24,NULL,NULL,_binary '',NULL,NULL,NULL,'0','0','成品区-F01','{\"设计风格\":\"中国风\",\"制作工艺\":\"点蓝\",\"材质\":\"铜胎\"}',63,'0'),(637,34,'蝶恋花珐琅彩耳坠','聆花文化','FLCSP-DLH-EZ',NULL,NULL,NULL,'粉色/金色','件','蝴蝶造型珐琅彩耳坠，铜胎点蓝工艺，配玛瑙珠点缀','flcsp_dlh_ez.jpg',24,NULL,NULL,_binary '',NULL,NULL,NULL,'0','0','成品区-F02','{\"设计风格\":\"现代简约\",\"制作工艺\":\"点蓝\",\"材质\":\"铜胎\"}',63,'0'),(638,34,'荷花珐琅彩摆件','聆花文化','FLCSP-HH-BJ',NULL,NULL,NULL,'粉色/绿色','件','荷花造型珐琅彩摆件，黑檀木底座，铜胎点蓝工艺','flcsp_hh_bj.jpg',24,NULL,NULL,_binary '',NULL,NULL,NULL,'0','0','成品区-F03','{\"设计风格\":\"中国风\",\"制作工艺\":\"点蓝\",\"材质\":\"铜胎+黑檀木\"}',63,'0'),(639,34,'祥龙珐琅彩摆件','聆花文化','FLCSP-XL-BJ',NULL,NULL,NULL,'蓝色/金色','件','祥龙造型珐琅彩摆件，玉石底座，铜胎点蓝工艺','flcsp_xl_bj.jpg',24,NULL,NULL,_binary '',NULL,NULL,NULL,'0','0','成品区-F04','{\"设计风格\":\"中国风\",\"制作工艺\":\"点蓝\",\"材质\":\"铜胎+玉石\"}',63,'0'),(640,31,'印象咖啡-美式','印象咖','YXK-MS',NULL,NULL,NULL,'深棕色','杯','醇厚的美式咖啡，馆内特供','material/63/生成图片_1747529994978.png',NULL,NULL,NULL,_binary '',NULL,NULL,NULL,'0','0','饮品区-G01','{}',63,'0'),(641,31,'印象奶茶-珍珠','印象咖','YXK-ZZ',NULL,NULL,NULL,'棕色','杯','香浓的珍珠奶茶，馆内特供','material/63/WechatIMG1204_1746884451016.jpg',NULL,NULL,NULL,_binary '',NULL,NULL,NULL,'0','0','饮品区-G02','{}',63,'0');
/*!40000 ALTER TABLE `jsh_material` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jsh_material_attribute`
--

DROP TABLE IF EXISTS `jsh_material_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jsh_material_attribute` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `attribute_name` varchar(50) DEFAULT NULL COMMENT '属性名',
  `attribute_value` varchar(500) DEFAULT NULL COMMENT '属性值',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COMMENT='产品属性表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jsh_material_attribute`
--

LOCK TABLES `jsh_material_attribute` WRITE;
/*!40000 ALTER TABLE `jsh_material_attribute` DISABLE KEYS */;
INSERT INTO `jsh_material_attribute` VALUES (1,'多颜色','红色|橙色|黄色|绿色|蓝色|紫色',63,'1'),(2,'多尺寸','S|M|L|XL|XXL|XXXL',63,'1'),(3,'自定义1','小米|华为',63,'1'),(4,'自定义2',NULL,63,'1'),(5,'自定义3',NULL,63,'1'),(6,'设计风格','中国风|唐卡风|佛系风|现代简约|东方美学',63,'0'),(7,'制作工艺','掐丝点蓝|点蓝|手工艺|机器印刷',63,'0'),(8,'材质','铜胎|玉石|黑檀木|铝塑板|其他',63,'0');
/*!40000 ALTER TABLE `jsh_material_attribute` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jsh_material_category`
--

DROP TABLE IF EXISTS `jsh_material_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jsh_material_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(50) DEFAULT NULL COMMENT '名称',
  `category_level` smallint(6) DEFAULT NULL COMMENT '等级',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '上级id',
  `sort` varchar(10) DEFAULT NULL COMMENT '显示顺序',
  `serial_no` varchar(100) DEFAULT NULL COMMENT '编号',
  `remark` varchar(1024) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`),
  KEY `FK3EE7F725237A77D8` (`parent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8 COMMENT='产品类型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jsh_material_category`
--

LOCK TABLES `jsh_material_category` WRITE;
/*!40000 ALTER TABLE `jsh_material_category` DISABLE KEYS */;
INSERT INTO `jsh_material_category` VALUES (17,'目录1',NULL,NULL,'11','wae12','eee','2019-04-10 22:18:12','2025-05-07 23:35:42',63,'1'),(21,'目录2',NULL,17,'22','ada112','ddd','2020-07-20 23:08:44','2025-05-07 23:35:38',63,'1'),(29,'聆花艺术臻品',1,NULL,'10','LHWH-YSZP','掐丝珐琅唐卡、掐丝珐琅风水画、艺术家联名、东方美学家居','2025-05-07 02:08:27','2025-05-07 02:08:27',63,'0'),(30,'聆花非遗文创',1,NULL,'20','LHWH-FYWC','珐琅彩饰品、摆件系列、家居文创、企业定制','2025-05-07 02:08:27','2025-05-07 02:08:27',63,'0'),(31,'聆花手作馆',1,NULL,'30','LHWH-SZG','团建沙龙、传承教学、印象咖','2025-05-07 02:08:27','2025-05-07 02:08:27',63,'0'),(32,'原材料',1,NULL,'40','LHWH-YCL','底胎材料、装裱材料、配饰材料、包装材料','2025-05-07 02:08:27','2025-05-07 02:08:27',63,'0'),(33,'掐丝珐琅唐卡',2,29,'11','LHWH-YSZP-QSFL-TK','掐丝珐琅唐卡系列产品','2025-05-07 02:08:27','2025-05-07 02:08:27',63,'0'),(34,'珐琅彩饰品',2,30,'21','LHWH-FYWC-FLCSP','珐琅彩饰品系列产品','2025-05-07 02:08:27','2025-05-07 02:08:27',63,'0'),(35,'底胎材料',2,32,'41','LHWH-YCL-DTCL','唐卡饰品铜胎、玉石、黑檀木等','2025-05-07 02:08:27','2025-05-07 02:08:27',63,'0'),(36,'装裱材料',2,32,'42','LHWH-YCL-ZBCL','花梨木框、欧式摆画框等','2025-05-07 02:08:27','2025-05-07 02:08:27',63,'0'),(37,'配饰材料',2,32,'43','LHWH-YCL-PSCL','珠类、挂扣、挂穗、耳勾耳堵、项链绳等','2025-05-07 02:08:27','2025-05-07 02:08:27',63,'0'),(38,'包装材料',2,32,'44','LHWH-YCL-BZCL','珐琅画包装箱、饰品盒等','2025-05-07 02:08:27','2025-05-07 02:08:27',63,'0');
/*!40000 ALTER TABLE `jsh_material_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jsh_material_current_stock`
--

DROP TABLE IF EXISTS `jsh_material_current_stock`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jsh_material_current_stock` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `material_id` bigint(20) DEFAULT NULL COMMENT '产品id',
  `depot_id` bigint(20) DEFAULT NULL COMMENT '仓库id',
  `current_number` decimal(24,6) DEFAULT NULL COMMENT '当前库存数量',
  `current_unit_price` decimal(24,6) DEFAULT NULL COMMENT '当前单价',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='产品当前库存';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jsh_material_current_stock`
--

LOCK TABLES `jsh_material_current_stock` WRITE;
/*!40000 ALTER TABLE `jsh_material_current_stock` DISABLE KEYS */;
INSERT INTO `jsh_material_current_stock` VALUES (19,588,14,7.000000,NULL,63,'1'),(20,568,14,2.000000,NULL,63,'1'),(21,568,15,1.000000,NULL,63,'1'),(22,570,14,8.000000,NULL,63,'1'),(23,619,14,0.000000,NULL,63,'1'),(24,619,15,0.000000,NULL,63,'1'),(25,619,17,0.000000,NULL,63,'1'),(26,619,19,0.000000,NULL,63,'0'),(27,619,20,0.000000,NULL,63,'0'),(28,619,21,0.000000,NULL,63,'0'),(29,619,22,0.000000,NULL,63,'0'),(30,619,23,0.000000,NULL,63,'0'),(31,619,24,0.000000,NULL,63,'0'),(32,619,25,0.000000,NULL,63,'0'),(33,619,26,0.000000,NULL,63,'0'),(34,619,27,0.000000,NULL,63,'0'),(35,641,19,0.000000,NULL,63,'0'),(36,641,20,0.000000,NULL,63,'0'),(37,641,21,0.000000,NULL,63,'0'),(38,641,22,0.000000,NULL,63,'0'),(39,641,23,0.000000,NULL,63,'0'),(40,641,24,0.000000,NULL,63,'0'),(41,641,25,0.000000,NULL,63,'0'),(42,641,26,0.000000,NULL,63,'0'),(43,641,27,0.000000,NULL,63,'0'),(44,640,19,0.000000,NULL,63,'0'),(45,640,20,0.000000,NULL,63,'0'),(46,640,21,0.000000,NULL,63,'0'),(47,640,22,0.000000,NULL,63,'0'),(48,640,23,0.000000,NULL,63,'0'),(49,640,24,0.000000,NULL,63,'0'),(50,640,25,0.000000,NULL,63,'0'),(51,640,26,0.000000,NULL,63,'0'),(52,640,27,0.000000,NULL,63,'0');
/*!40000 ALTER TABLE `jsh_material_current_stock` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jsh_material_extend`
--

DROP TABLE IF EXISTS `jsh_material_extend`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jsh_material_extend` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `material_id` bigint(20) DEFAULT NULL COMMENT '商品id',
  `bar_code` varchar(50) DEFAULT NULL COMMENT '商品条码',
  `commodity_unit` varchar(50) DEFAULT NULL COMMENT '商品单位',
  `sku` varchar(50) DEFAULT NULL COMMENT '多属性',
  `purchase_decimal` decimal(24,6) DEFAULT NULL COMMENT '采购价格',
  `commodity_decimal` decimal(24,6) DEFAULT NULL COMMENT '零售价格',
  `wholesale_decimal` decimal(24,6) DEFAULT NULL COMMENT '销售价格',
  `low_decimal` decimal(24,6) DEFAULT NULL COMMENT '最低售价',
  `default_flag` varchar(1) DEFAULT '1' COMMENT '是否为默认单位，1是，0否',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `create_serial` varchar(50) DEFAULT NULL COMMENT '创建人编码',
  `update_serial` varchar(50) DEFAULT NULL COMMENT '更新人编码',
  `update_time` bigint(20) DEFAULT NULL COMMENT '更新时间戳',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `delete_Flag` varchar(1) DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=54 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='产品价格扩展';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jsh_material_extend`
--

LOCK TABLES `jsh_material_extend` WRITE;
/*!40000 ALTER TABLE `jsh_material_extend` DISABLE KEYS */;
INSERT INTO `jsh_material_extend` VALUES (1,587,'1000','个',NULL,11.000000,22.000000,22.000000,22.000000,'1','2020-02-20 23:22:03','jsh','jsh',1595263657135,63,'1'),(2,568,'1001','个',NULL,11.000000,15.000000,15.000000,15.000000,'1','2020-02-20 23:44:57','jsh','jsh',1595265439418,63,'1'),(3,569,'1002','只',NULL,10.000000,15.000000,15.000000,13.000000,'1','2020-02-20 23:45:15','jsh','jsh',1582213514731,63,'1'),(4,570,'1003','个',NULL,8.000000,15.000000,14.000000,13.000000,'1','2020-02-20 23:45:37','jsh','jsh',1587657604430,63,'1'),(5,577,'1004','个',NULL,10.000000,20.000000,20.000000,20.000000,'1','2020-02-20 23:46:36','jsh','jsh',1582213596494,63,'1'),(6,577,'1005','箱',NULL,120.000000,240.000000,240.000000,240.000000,'0','2020-02-20 23:46:36','jsh','jsh',1582213596497,63,'1'),(7,579,'1006','个',NULL,20.000000,30.000000,30.000000,30.000000,'1','2020-02-20 23:47:04','jsh','jsh',1595264270458,63,'1'),(8,579,'1007','箱',NULL,240.000000,360.000000,360.000000,360.000000,'0','2020-02-20 23:47:04','jsh','jsh',1595264270466,63,'1'),(9,586,'1008','个',NULL,12.000000,15.000000,15.000000,15.000000,'1','2020-02-20 23:47:23','jsh','jsh',1595254981896,63,'1'),(10,588,'1009','个',NULL,11.000000,22.000000,22.000000,22.000000,'1','2020-07-21 00:58:15','jsh','jsh',1614699799073,63,'1'),(36,619,'1014','件','橙色,M',12.000000,15.000000,14.000000,NULL,'1','2021-07-28 01:00:20','jsh','jsh',1746555283112,63,'0'),(37,619,'1015','件','橙色,L',12.000000,15.000000,14.000000,NULL,'0','2021-07-28 01:00:20','jsh','jsh',1746555283116,63,'0'),(38,619,'1016','件','绿色,M',12.000000,15.000000,14.000000,NULL,'0','2021-07-28 01:00:20','jsh','jsh',1746555283121,63,'0'),(39,619,'1017','件','绿色,L',12.000000,15.000000,14.000000,NULL,'0','2021-07-28 01:00:20','jsh','jsh',1746555283125,63,'0'),(40,632,'QSFL-TK-JX8B','幅',NULL,1800.000000,3600.000000,2880.000000,2160.000000,'1','2025-05-07 02:21:52','admin',NULL,1746555712000,63,'0'),(41,633,'QSFL-TK-JGC','幅',NULL,2000.000000,4200.000000,3360.000000,2520.000000,'1','2025-05-07 02:21:52','admin',NULL,1746555712000,63,'0'),(42,634,'QSFL-FSH-MD','幅',NULL,1500.000000,3000.000000,2400.000000,1800.000000,'1','2025-05-07 02:21:52','admin',NULL,1746555712000,63,'0'),(43,635,'QSFL-FSH-NNYY','幅',NULL,1600.000000,3200.000000,2560.000000,1920.000000,'1','2025-05-07 02:21:52','admin',NULL,1746555712000,63,'0'),(44,636,'FLCSP-XY-XL','件',NULL,380.000000,780.000000,624.000000,468.000000,'1','2025-05-07 02:21:52','admin',NULL,1746555712000,63,'0'),(45,637,'FLCSP-DLH-EZ','件',NULL,320.000000,680.000000,544.000000,408.000000,'1','2025-05-07 02:21:52','admin',NULL,1746555712000,63,'0'),(46,638,'FLCSP-HH-BJ','件',NULL,580.000000,1180.000000,944.000000,708.000000,'1','2025-05-07 02:21:52','admin',NULL,1746555712000,63,'0'),(47,639,'FLCSP-XL-BJ','件',NULL,680.000000,1380.000000,1104.000000,828.000000,'1','2025-05-07 02:21:52','admin',NULL,1746555712000,63,'0'),(48,640,'YXK-MS','杯','',8.000000,25.000000,25.000000,25.000000,'1','2025-05-07 02:21:52','admin','waterxi',1747529997545,63,'0'),(49,641,'11233','杯','',10.000000,28.000000,28.000000,28.000000,'1','2025-05-07 02:21:52','admin','waterxi',1746884457934,63,'0'),(50,620,'DT-TT-S','个',NULL,180.000000,180.000000,180.000000,180.000000,'1','2025-05-07 02:21:52','admin',NULL,1746555712000,63,'0'),(51,621,'DT-LSB-M','个',NULL,120.000000,120.000000,120.000000,120.000000,'1','2025-05-07 02:21:52','admin',NULL,1746555712000,63,'0'),(52,624,'HK-HLM-M','个',NULL,280.000000,280.000000,280.000000,280.000000,'1','2025-05-07 02:21:52','admin',NULL,1746555712000,63,'0'),(53,625,'HK-OS-S','个',NULL,180.000000,180.000000,180.000000,180.000000,'1','2025-05-07 02:21:52','admin',NULL,1746555712000,63,'0');
/*!40000 ALTER TABLE `jsh_material_extend` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jsh_material_initial_stock`
--

DROP TABLE IF EXISTS `jsh_material_initial_stock`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jsh_material_initial_stock` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `material_id` bigint(20) DEFAULT NULL COMMENT '产品id',
  `depot_id` bigint(20) DEFAULT NULL COMMENT '仓库id',
  `number` decimal(24,6) DEFAULT NULL COMMENT '初始库存数量',
  `low_safe_stock` decimal(24,6) DEFAULT NULL COMMENT '最低库存数量',
  `high_safe_stock` decimal(24,6) DEFAULT NULL COMMENT '最高库存数量',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=256 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='产品初始库存';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jsh_material_initial_stock`
--

LOCK TABLES `jsh_material_initial_stock` WRITE;
/*!40000 ALTER TABLE `jsh_material_initial_stock` DISABLE KEYS */;
INSERT INTO `jsh_material_initial_stock` VALUES (205,619,14,0.000000,NULL,NULL,63,'1'),(206,619,19,0.000000,NULL,NULL,63,'0'),(207,619,20,0.000000,NULL,NULL,63,'0'),(208,619,21,0.000000,NULL,NULL,63,'0'),(209,619,15,0.000000,NULL,NULL,63,'1'),(210,619,22,0.000000,NULL,NULL,63,'0'),(211,619,17,0.000000,NULL,NULL,63,'1'),(212,619,23,0.000000,NULL,NULL,63,'0'),(213,619,24,0.000000,NULL,NULL,63,'0'),(214,619,25,0.000000,NULL,NULL,63,'0'),(215,619,26,0.000000,NULL,NULL,63,'0'),(216,619,27,0.000000,NULL,NULL,63,'0'),(217,620,19,20.000000,5.000000,50.000000,63,'0'),(218,632,21,5.000000,2.000000,10.000000,63,'0'),(219,636,24,3.000000,1.000000,5.000000,63,'0'),(238,641,19,0.000000,NULL,NULL,63,'0'),(239,641,20,0.000000,NULL,NULL,63,'0'),(240,641,21,0.000000,NULL,NULL,63,'0'),(241,641,22,0.000000,NULL,NULL,63,'0'),(242,641,23,0.000000,NULL,NULL,63,'0'),(243,641,24,0.000000,NULL,NULL,63,'0'),(244,641,25,0.000000,NULL,NULL,63,'0'),(245,641,26,0.000000,NULL,NULL,63,'0'),(246,641,27,0.000000,NULL,NULL,63,'0'),(247,640,19,0.000000,NULL,NULL,63,'0'),(248,640,20,0.000000,NULL,NULL,63,'0'),(249,640,21,0.000000,NULL,NULL,63,'0'),(250,640,22,0.000000,NULL,NULL,63,'0'),(251,640,23,0.000000,NULL,NULL,63,'0'),(252,640,24,0.000000,NULL,NULL,63,'0'),(253,640,25,0.000000,NULL,NULL,63,'0'),(254,640,26,0.000000,NULL,NULL,63,'0'),(255,640,27,0.000000,NULL,NULL,63,'0');
/*!40000 ALTER TABLE `jsh_material_initial_stock` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jsh_material_property`
--

DROP TABLE IF EXISTS `jsh_material_property`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jsh_material_property` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `native_name` varchar(50) DEFAULT NULL COMMENT '原始名称',
  `enabled` bit(1) DEFAULT NULL COMMENT '是否启用',
  `sort` varchar(10) DEFAULT NULL COMMENT '排序',
  `another_name` varchar(50) DEFAULT NULL COMMENT '别名',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='产品扩展字段表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jsh_material_property`
--

LOCK TABLES `jsh_material_property` WRITE;
/*!40000 ALTER TABLE `jsh_material_property` DISABLE KEYS */;
/*!40000 ALTER TABLE `jsh_material_property` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jsh_msg`
--

DROP TABLE IF EXISTS `jsh_msg`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jsh_msg` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `msg_title` varchar(100) DEFAULT NULL COMMENT '消息标题',
  `msg_content` varchar(500) DEFAULT NULL COMMENT '消息内容',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `type` varchar(20) DEFAULT NULL COMMENT '消息类型',
  `user_id` bigint(20) DEFAULT NULL COMMENT '接收人id',
  `status` varchar(1) DEFAULT NULL COMMENT '状态，1未读 2已读',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `delete_Flag` varchar(1) DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='消息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jsh_msg`
--

LOCK TABLES `jsh_msg` WRITE;
/*!40000 ALTER TABLE `jsh_msg` DISABLE KEYS */;
INSERT INTO `jsh_msg` VALUES (2,'标题1','内容1','2019-09-10 00:11:39','类型1',63,'2',63,'0');
/*!40000 ALTER TABLE `jsh_msg` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jsh_orga_user_rel`
--

DROP TABLE IF EXISTS `jsh_orga_user_rel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jsh_orga_user_rel` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `orga_id` bigint(20) NOT NULL COMMENT '机构id',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `user_blng_orga_dspl_seq` varchar(20) DEFAULT NULL COMMENT '用户在所属机构中显示顺序',
  `delete_flag` char(1) DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `creator` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `updater` bigint(20) DEFAULT NULL COMMENT '更新人',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8 COMMENT='机构用户关系表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jsh_orga_user_rel`
--

LOCK TABLES `jsh_orga_user_rel` WRITE;
/*!40000 ALTER TABLE `jsh_orga_user_rel` DISABLE KEYS */;
INSERT INTO `jsh_orga_user_rel` VALUES (10,13,131,'2','0','2019-12-28 12:13:15',63,'2021-03-18 22:33:19',63,63),(11,24,63,'1','0','2020-09-13 18:42:45',63,'2025-05-07 02:48:07',63,63),(12,13,135,'9','0','2021-03-18 22:24:25',63,'2021-03-19 00:09:23',63,63),(13,13,134,'1','0','2021-03-18 22:31:39',63,'2021-03-18 23:59:55',63,63),(14,22,133,'22','0','2021-03-18 22:31:44',63,'2021-03-18 22:32:04',63,63),(15,12,144,NULL,'0','2021-03-19 00:00:40',63,'2021-03-19 00:08:07',63,63),(16,12,145,NULL,'0','2021-03-19 00:03:44',63,'2021-03-19 00:03:44',63,63);
/*!40000 ALTER TABLE `jsh_orga_user_rel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jsh_organization`
--

DROP TABLE IF EXISTS `jsh_organization`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jsh_organization` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `org_no` varchar(20) DEFAULT NULL COMMENT '机构编号',
  `org_abr` varchar(20) DEFAULT NULL COMMENT '机构简称',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父机构id',
  `sort` varchar(20) DEFAULT NULL COMMENT '机构显示顺序',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8 COMMENT='机构表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jsh_organization`
--

LOCK TABLES `jsh_organization` WRITE;
/*!40000 ALTER TABLE `jsh_organization` DISABLE KEYS */;
INSERT INTO `jsh_organization` VALUES (12,'001','测试机构',NULL,'2','aaaa2','2019-12-28 12:13:01','2025-05-07 02:47:47',63,'1'),(13,'jg1','机构1',12,'3','','2020-07-21 00:09:57','2025-05-07 02:47:44',63,'1'),(14,'12','机构2',13,'4','','2020-07-21 22:45:42','2025-05-07 02:47:41',63,'1'),(24,'LHWH-001','聆花文化',NULL,'10','广州聆花文化传播有限公司','2025-05-07 02:08:27','2025-05-07 02:08:27',63,'0'),(25,'LHWH-002','深圳景之蓝',24,'20','投资企业，不参与管理，由我方供货','2025-05-07 02:08:27','2025-05-07 02:08:27',63,'0'),(26,'LHWH-003','广西生产基地',24,'30','深度合作生产制作工厂，负责掐丝珐琅制作','2025-05-07 02:08:27','2025-05-07 02:08:27',63,'0'),(27,'LHWH-004','印象咖',24,'40','聆花掐丝珐琅馆中的窗口饮品店，主营咖啡、奶茶饮品','2025-05-07 02:08:27','2025-05-07 02:08:27',63,'0');
/*!40000 ALTER TABLE `jsh_organization` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jsh_person`
--

DROP TABLE IF EXISTS `jsh_person`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jsh_person` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `type` varchar(20) DEFAULT NULL COMMENT '类型',
  `name` varchar(50) DEFAULT NULL COMMENT '姓名',
  `enabled` bit(1) DEFAULT NULL COMMENT '启用',
  `sort` varchar(10) DEFAULT NULL COMMENT '排序',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8 COMMENT='经手人表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jsh_person`
--

LOCK TABLES `jsh_person` WRITE;
/*!40000 ALTER TABLE `jsh_person` DISABLE KEYS */;
INSERT INTO `jsh_person` VALUES (14,'销售员','小李',_binary '',NULL,63,'1'),(15,'仓管员','小军',_binary '',NULL,63,'1'),(16,'财务员','小夏',_binary '',NULL,63,'1'),(17,'财务员','小曹',_binary '',NULL,63,'1'),(18,'管理员','聆花老师',_binary '','10',63,'0'),(19,'管理员','梁朝伟',_binary '','11',63,'0'),(20,'销售员','龚锦华',_binary '','20',63,'0'),(21,'销售员','伍尚明',_binary '','21',63,'0'),(22,'生产员','莫智华',_binary '','30',63,'0'),(23,'合作方','关耳老师',_binary '','40',63,'0'),(24,'合作方','蒙东老师',_binary '','41',63,'0');
/*!40000 ALTER TABLE `jsh_person` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jsh_platform_config`
--

DROP TABLE IF EXISTS `jsh_platform_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jsh_platform_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `platform_key` varchar(100) DEFAULT NULL COMMENT '关键词',
  `platform_key_info` varchar(100) DEFAULT NULL COMMENT '关键词名称',
  `platform_value` varchar(200) DEFAULT NULL COMMENT '值',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8 COMMENT='平台参数';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jsh_platform_config`
--

LOCK TABLES `jsh_platform_config` WRITE;
/*!40000 ALTER TABLE `jsh_platform_config` DISABLE KEYS */;
INSERT INTO `jsh_platform_config` VALUES (1,'platform_name','平台名称','聆花文化ERP'),(2,'activation_code','激活码',''),(3,'platform_url','官方网站','http://www.linghuaart.com'),(4,'bill_print_flag','三联打印启用标记','0'),(5,'bill_print_url','三联打印地址',''),(6,'pay_fee_url','租户续费地址',''),(7,'register_flag','注册启用标记','1'),(8,'app_activation_code','手机端激活码',''),(9,'send_workflow_url','发起流程地址',''),(10,'weixinUrl','微信url',''),(11,'weixinAppid','微信appid',''),(12,'weixinSecret','微信secret',''),(13,'aliOss_endpoint','阿里OSS-endpoint',''),(14,'aliOss_accessKeyId','阿里OSS-accessKeyId',''),(15,'aliOss_accessKeySecret','阿里OSS-accessKeySecret',''),(16,'aliOss_bucketName','阿里OSS-bucketName',''),(17,'aliOss_linkUrl','阿里OSS-linkUrl',''),(18,'bill_excel_url','单据Excel地址','');
/*!40000 ALTER TABLE `jsh_platform_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jsh_role`
--

DROP TABLE IF EXISTS `jsh_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jsh_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(50) DEFAULT NULL COMMENT '名称',
  `type` varchar(50) DEFAULT NULL COMMENT '类型',
  `price_limit` varchar(50) DEFAULT NULL COMMENT '价格屏蔽 1-屏蔽采购价 2-屏蔽零售价 3-屏蔽销售价',
  `value` varchar(200) DEFAULT NULL COMMENT '值',
  `description` varchar(100) DEFAULT NULL COMMENT '描述',
  `enabled` bit(1) DEFAULT NULL COMMENT '启用',
  `sort` varchar(10) DEFAULT NULL COMMENT '排序',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8 COMMENT='角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jsh_role`
--

LOCK TABLES `jsh_role` WRITE;
/*!40000 ALTER TABLE `jsh_role` DISABLE KEYS */;
INSERT INTO `jsh_role` VALUES (4,'管理员','全部数据',NULL,NULL,NULL,_binary '',NULL,NULL,'0'),(10,'租户','全部数据',NULL,NULL,'',_binary '',NULL,NULL,'0'),(16,'销售经理','全部数据',NULL,NULL,'ddd',_binary '',NULL,63,'0'),(17,'销售代表','个人数据',NULL,NULL,'rrr',_binary '',NULL,63,'0'),(21,'合伙人','本机构数据',NULL,NULL,NULL,_binary '','3',63,'0');
/*!40000 ALTER TABLE `jsh_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jsh_sequence`
--

DROP TABLE IF EXISTS `jsh_sequence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jsh_sequence` (
  `seq_name` varchar(50) NOT NULL COMMENT '序列名称',
  `min_value` bigint(20) NOT NULL COMMENT '最小值',
  `max_value` bigint(20) NOT NULL COMMENT '最大值',
  `current_val` bigint(20) NOT NULL COMMENT '当前值',
  `increment_val` int(11) NOT NULL DEFAULT '1' COMMENT '增长步数',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`seq_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='单据编号表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jsh_sequence`
--

LOCK TABLES `jsh_sequence` WRITE;
/*!40000 ALTER TABLE `jsh_sequence` DISABLE KEYS */;
INSERT INTO `jsh_sequence` VALUES ('depot_number_seq',1,999999999999999999,677,1,'单据编号sequence');
/*!40000 ALTER TABLE `jsh_sequence` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jsh_serial_number`
--

DROP TABLE IF EXISTS `jsh_serial_number`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jsh_serial_number` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `material_id` bigint(20) DEFAULT NULL COMMENT '产品表id',
  `depot_id` bigint(20) DEFAULT NULL COMMENT '仓库id',
  `serial_number` varchar(64) DEFAULT NULL COMMENT '序列号',
  `is_sell` varchar(1) DEFAULT '0' COMMENT '是否卖出，0未卖出，1卖出',
  `in_price` decimal(24,6) DEFAULT NULL COMMENT '入库单价',
  `remark` varchar(1024) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `creator` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `updater` bigint(20) DEFAULT NULL COMMENT '更新人',
  `in_bill_no` varchar(50) DEFAULT NULL COMMENT '入库单号',
  `out_bill_no` varchar(50) DEFAULT NULL COMMENT '出库单号',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=110 DEFAULT CHARSET=utf8 COMMENT='序列号表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jsh_serial_number`
--

LOCK TABLES `jsh_serial_number` WRITE;
/*!40000 ALTER TABLE `jsh_serial_number` DISABLE KEYS */;
/*!40000 ALTER TABLE `jsh_serial_number` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jsh_supplier`
--

DROP TABLE IF EXISTS `jsh_supplier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jsh_supplier` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `supplier` varchar(255) NOT NULL COMMENT '供应商名称',
  `contacts` varchar(100) DEFAULT NULL COMMENT '联系人',
  `phone_num` varchar(30) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) DEFAULT NULL COMMENT '电子邮箱',
  `description` varchar(500) DEFAULT NULL COMMENT '备注',
  `isystem` tinyint(4) DEFAULT NULL COMMENT '是否系统自带 0==系统 1==非系统',
  `type` varchar(20) DEFAULT NULL COMMENT '类型',
  `enabled` bit(1) DEFAULT NULL COMMENT '启用',
  `advance_in` decimal(24,6) DEFAULT '0.000000' COMMENT '预收款',
  `begin_need_get` decimal(24,6) DEFAULT NULL COMMENT '期初应收',
  `begin_need_pay` decimal(24,6) DEFAULT NULL COMMENT '期初应付',
  `all_need_get` decimal(24,6) DEFAULT NULL COMMENT '累计应收',
  `all_need_pay` decimal(24,6) DEFAULT NULL COMMENT '累计应付',
  `fax` varchar(30) DEFAULT NULL COMMENT '传真',
  `telephone` varchar(30) DEFAULT NULL COMMENT '手机',
  `address` varchar(100) DEFAULT NULL COMMENT '地址',
  `tax_num` varchar(50) DEFAULT NULL COMMENT '纳税人识别号',
  `bank_name` varchar(50) DEFAULT NULL COMMENT '开户行',
  `account_number` varchar(50) DEFAULT NULL COMMENT '账号',
  `tax_rate` decimal(24,6) DEFAULT NULL COMMENT '税率',
  `sort` varchar(10) DEFAULT NULL COMMENT '排序',
  `creator` bigint(20) DEFAULT NULL COMMENT '操作员',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=99 DEFAULT CHARSET=utf8 COMMENT='供应商/客户信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jsh_supplier`
--

LOCK TABLES `jsh_supplier` WRITE;
/*!40000 ALTER TABLE `jsh_supplier` DISABLE KEYS */;
INSERT INTO `jsh_supplier` VALUES (57,'供应商1','小军','12345678','','',NULL,'供应商',_binary '',0.000000,0.000000,0.000000,0.000000,4.000000,'','15000000000','地址1','','','',12.000000,NULL,63,63,'1'),(58,'客户1','小李','12345678','','',NULL,'客户',_binary '',0.000000,0.000000,0.000000,-100.000000,NULL,'','','','','','',12.000000,NULL,63,63,'1'),(59,'客户2','小陈','','','',NULL,'客户',_binary '',0.000000,0.000000,0.000000,0.000000,NULL,'','','','','','',NULL,NULL,63,63,'1'),(60,'12312666','小曹','','','',NULL,'会员',_binary '',970.000000,0.000000,0.000000,NULL,NULL,'','13000000000','','','','',NULL,NULL,63,63,'0'),(68,'供应商3','晓丽','12345678','','fasdfadf',NULL,'供应商',_binary '',0.000000,0.000000,0.000000,0.000000,-35.000000,'','13000000000','aaaa','1341324','','',13.000000,NULL,63,63,'1'),(71,'客户3','小周','','','',NULL,'客户',_binary '',0.000000,0.000000,0.000000,0.000000,NULL,'','','','','','',NULL,NULL,63,63,'1'),(74,'供应商5','小季','77779999','','',NULL,'供应商',_binary '',0.000000,0.000000,5.000000,0.000000,5.000000,'','15806283912','','','','',3.000000,NULL,63,63,'1'),(90,'底胎供应商','陈经理','13800000001','dt@supplier.com','提供唐卡饰品铜胎、玉石、黑檀木等',NULL,'供应商',_binary '',0.000000,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,13.000000,NULL,NULL,63,'0'),(91,'装裱材料供应商','李经理','13800000002','zb@supplier.com','提供花梨木框、欧式摆画框等',NULL,'供应商',_binary '',0.000000,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,13.000000,NULL,NULL,63,'0'),(92,'配饰材料供应商','王经理','13800000003','ps@supplier.com','提供珠类、挂扣、挂穗、耳勾耳堵、项链绳等',NULL,'供应商',_binary '',0.000000,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,13.000000,NULL,NULL,63,'0'),(93,'包装材料供应商','张经理','13800000004','bz@supplier.com','提供珐琅画包装箱、饰品盒等',NULL,'供应商',_binary '',0.000000,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,13.000000,NULL,NULL,63,'0'),(94,'中山纪念堂','刘管理','13900000001','zsj@example.com','代销客户，按月销售开票结账',NULL,'客户',_binary '',0.000000,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,13.000000,NULL,NULL,63,'0'),(95,'广州美术馆','钱管理','13900000002','gzmsg@example.com','代销客户，按月销售开票结账',NULL,'客户',_binary '',0.000000,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,13.000000,NULL,NULL,63,'0'),(96,'摩登百货','孙管理','13900000003','mdbh@example.com','代销客户，按月销售开票结账',NULL,'客户',_binary '',0.000000,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,13.000000,NULL,NULL,63,'0'),(97,'番禺新华书店','赵管理','13900000004','pyxhsd@example.com','代销客户，按月销售开票结账',NULL,'客户',_binary '',0.000000,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,13.000000,NULL,NULL,63,'0'),(98,'深圳景之蓝','关耳老师','13900000005','szjzl@example.com','投资企业，由我方供货',NULL,'客户',_binary '',0.000000,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,13.000000,NULL,NULL,63,'0');
/*!40000 ALTER TABLE `jsh_supplier` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jsh_system_config`
--

DROP TABLE IF EXISTS `jsh_system_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jsh_system_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `company_name` varchar(50) DEFAULT NULL COMMENT '公司名称',
  `company_contacts` varchar(20) DEFAULT NULL COMMENT '公司联系人',
  `company_address` varchar(50) DEFAULT NULL COMMENT '公司地址',
  `company_tel` varchar(20) DEFAULT NULL COMMENT '公司电话',
  `company_fax` varchar(20) DEFAULT NULL COMMENT '公司传真',
  `company_post_code` varchar(20) DEFAULT NULL COMMENT '公司邮编',
  `sale_agreement` varchar(500) DEFAULT NULL COMMENT '销售协议',
  `depot_flag` varchar(1) DEFAULT '0' COMMENT '仓库启用标记，0未启用，1启用',
  `customer_flag` varchar(1) DEFAULT '0' COMMENT '客户启用标记，0未启用，1启用',
  `minus_stock_flag` varchar(1) DEFAULT '0' COMMENT '负库存启用标记，0未启用，1启用',
  `purchase_by_sale_flag` varchar(1) DEFAULT '0' COMMENT '以销定购启用标记，0未启用，1启用',
  `multi_level_approval_flag` varchar(1) DEFAULT '0' COMMENT '多级审核启用标记，0未启用，1启用',
  `multi_bill_type` varchar(200) DEFAULT NULL COMMENT '流程类型，可多选',
  `force_approval_flag` varchar(1) DEFAULT '0' COMMENT '强审核启用标记，0未启用，1启用',
  `update_unit_price_flag` varchar(1) DEFAULT '1' COMMENT '更新单价启用标记，0未启用，1启用',
  `over_link_bill_flag` varchar(1) DEFAULT '0' COMMENT '超出关联单据启用标记，0未启用，1启用',
  `in_out_manage_flag` varchar(1) DEFAULT '0' COMMENT '出入库管理启用标记，0未启用，1启用',
  `multi_account_flag` varchar(1) DEFAULT '0' COMMENT '多账户启用标记，0未启用，1启用',
  `move_avg_price_flag` varchar(1) DEFAULT '0' COMMENT '移动平均价启用标记，0未启用，1启用',
  `audit_print_flag` varchar(1) DEFAULT '0' COMMENT '先审核后打印启用标记，0未启用，1启用',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='系统参数';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jsh_system_config`
--

LOCK TABLES `jsh_system_config` WRITE;
/*!40000 ALTER TABLE `jsh_system_config` DISABLE KEYS */;
INSERT INTO `jsh_system_config` VALUES (11,'广州聆花文化传播有限公司','梁朝伟','广州市番禺区小谷围街大学城外环西路1号自编GJ-37-38栋102','1392888258',NULL,NULL,'注：本单为我公司与客户约定账期内结款的依据，由客户或其单位员工签字生效，并承担法律责任。','1','1','1','0','1','','0','1','0','0','1','0','0',63,'0');
/*!40000 ALTER TABLE `jsh_system_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jsh_tenant`
--

DROP TABLE IF EXISTS `jsh_tenant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jsh_tenant` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `login_name` varchar(255) DEFAULT NULL COMMENT '登录名',
  `user_num_limit` int(11) DEFAULT NULL COMMENT '用户数量限制',
  `type` varchar(1) DEFAULT '0' COMMENT '租户类型，0免费租户，1付费租户',
  `enabled` bit(1) DEFAULT b'1' COMMENT '启用 0-禁用  1-启用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `expire_time` datetime DEFAULT NULL COMMENT '到期时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8 COMMENT='租户';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jsh_tenant`
--

LOCK TABLES `jsh_tenant` WRITE;
/*!40000 ALTER TABLE `jsh_tenant` DISABLE KEYS */;
INSERT INTO `jsh_tenant` VALUES (13,63,'jsh',2000,'1',_binary '','2021-02-17 23:19:17','2099-02-17 23:19:17',NULL,'0');
/*!40000 ALTER TABLE `jsh_tenant` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jsh_unit`
--

DROP TABLE IF EXISTS `jsh_unit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jsh_unit` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(50) DEFAULT NULL COMMENT '名称，支持多单位',
  `basic_unit` varchar(50) DEFAULT NULL COMMENT '基础单位',
  `other_unit` varchar(50) DEFAULT NULL COMMENT '副单位',
  `other_unit_two` varchar(50) DEFAULT NULL COMMENT '副单位2',
  `other_unit_three` varchar(50) DEFAULT NULL COMMENT '副单位3',
  `ratio` decimal(24,3) DEFAULT NULL COMMENT '比例',
  `ratio_two` decimal(24,3) DEFAULT NULL COMMENT '比例2',
  `ratio_three` decimal(24,3) DEFAULT NULL COMMENT '比例3',
  `enabled` bit(1) DEFAULT NULL COMMENT '启用',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8 COMMENT='多单位表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jsh_unit`
--

LOCK TABLES `jsh_unit` WRITE;
/*!40000 ALTER TABLE `jsh_unit` DISABLE KEYS */;
INSERT INTO `jsh_unit` VALUES (15,'个/(箱=12个)','个','箱',NULL,NULL,12.000,NULL,NULL,_binary '',63,'0'),(19,'个/(盒=15个)','个','盒',NULL,NULL,15.000,NULL,NULL,_binary '',63,'0'),(20,'盒/(箱=8盒)','盒','箱',NULL,NULL,8.000,NULL,NULL,_binary '',63,'0'),(21,'瓶/(箱=12瓶)','瓶','箱',NULL,NULL,12.000,NULL,NULL,_binary '',63,'0'),(22,'幅','幅',NULL,NULL,NULL,NULL,NULL,NULL,_binary '',63,'0'),(23,'套','套',NULL,NULL,NULL,NULL,NULL,NULL,_binary '',63,'0'),(24,'件','件',NULL,NULL,NULL,NULL,NULL,NULL,_binary '',63,'0');
/*!40000 ALTER TABLE `jsh_unit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jsh_user`
--

DROP TABLE IF EXISTS `jsh_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jsh_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(255) NOT NULL COMMENT '用户姓名--例如张三',
  `login_name` varchar(255) NOT NULL COMMENT '登录用户名',
  `password` varchar(50) DEFAULT NULL COMMENT '登陆密码',
  `leader_flag` varchar(1) DEFAULT '0' COMMENT '是否经理，0否，1是',
  `position` varchar(200) DEFAULT NULL COMMENT '职位',
  `department` varchar(255) DEFAULT NULL COMMENT '所属部门',
  `email` varchar(100) DEFAULT NULL COMMENT '电子邮箱',
  `phonenum` varchar(100) DEFAULT NULL COMMENT '手机号码',
  `ismanager` tinyint(4) NOT NULL DEFAULT '1' COMMENT '是否为管理者 0==管理者 1==员工',
  `isystem` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否系统自带数据 ',
  `status` tinyint(4) DEFAULT '0' COMMENT '状态，0正常，2封禁',
  `description` varchar(500) DEFAULT NULL COMMENT '用户描述信息',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `weixin_open_id` varchar(100) DEFAULT NULL COMMENT '微信绑定',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=146 DEFAULT CHARSET=utf8 COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jsh_user`
--

LOCK TABLES `jsh_user` WRITE;
/*!40000 ALTER TABLE `jsh_user` DISABLE KEYS */;
INSERT INTO `jsh_user` VALUES (63,'管理员','waterxi','e10adc3949ba59abbe56e057f20f883e','0','CEO',NULL,'666666@qq.com','1123123123132',1,1,0,'',NULL,NULL,63,'0'),(120,'管理员','admin','e10adc3949ba59abbe56e057f20f883e','0',NULL,NULL,NULL,NULL,1,0,0,NULL,NULL,NULL,0,'0'),(131,'test123','test123','e10adc3949ba59abbe56e057f20f883e','0','总监',NULL,'7777777@qq.com','',1,0,0,'',NULL,NULL,63,'1');
/*!40000 ALTER TABLE `jsh_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jsh_user_business`
--

DROP TABLE IF EXISTS `jsh_user_business`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jsh_user_business` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `type` varchar(50) DEFAULT NULL COMMENT '类别',
  `key_id` varchar(50) DEFAULT NULL COMMENT '主id',
  `value` varchar(10000) DEFAULT NULL COMMENT '值',
  `btn_str` varchar(2000) DEFAULT NULL COMMENT '按钮权限',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=84 DEFAULT CHARSET=utf8 COMMENT='用户/角色/模块关系表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jsh_user_business`
--

LOCK TABLES `jsh_user_business` WRITE;
/*!40000 ALTER TABLE `jsh_user_business` DISABLE KEYS */;
INSERT INTO `jsh_user_business` VALUES (5,'RoleFunctions','4','[210][225][211][241][33][199][242][38][41][200][201][239][202][40][232][233][197][44][203][204][205][206][212][246][198][207][259][208][209][226][227][248][228][229][59][235][237][244][22][21][23][220][247][25][24][217][218][26][194][195][31][13][1][14][243][15][234][16][18][236][245][258][261][32]','[{\"funId\":13,\"btnStr\":\"1\"},{\"funId\":14,\"btnStr\":\"1\"},{\"funId\":243,\"btnStr\":\"1\"},{\"funId\":234,\"btnStr\":\"1\"},{\"funId\":16,\"btnStr\":\"1\"},{\"funId\":18,\"btnStr\":\"1\"},{\"funId\":236,\"btnStr\":\"1\"},{\"funId\":245,\"btnStr\":\"1\"},{\"funId\":22,\"btnStr\":\"1\"},{\"funId\":23,\"btnStr\":\"1,3\"},{\"funId\":220,\"btnStr\":\"1\"},{\"funId\":247,\"btnStr\":\"1\"},{\"funId\":25,\"btnStr\":\"1,3\"},{\"funId\":217,\"btnStr\":\"1,3\"},{\"funId\":218,\"btnStr\":\"1,3\"},{\"funId\":26,\"btnStr\":\"1\"},{\"funId\":194,\"btnStr\":\"1\"},{\"funId\":195,\"btnStr\":\"1\"},{\"funId\":31,\"btnStr\":\"1\"},{\"funId\":261,\"btnStr\":\"1,2,7,3\"},{\"funId\":241,\"btnStr\":\"1,2,7,3\"},{\"funId\":33,\"btnStr\":\"1,2,7,3\"},{\"funId\":199,\"btnStr\":\"1,2,7,3\"},{\"funId\":242,\"btnStr\":\"1,2,7,3\"},{\"funId\":41,\"btnStr\":\"1,2,7,3\"},{\"funId\":200,\"btnStr\":\"1,2,7,3\"},{\"funId\":210,\"btnStr\":\"1,2,7,3\"},{\"funId\":211,\"btnStr\":\"1,2,7,3\"},{\"funId\":197,\"btnStr\":\"1,7,2,3\"},{\"funId\":203,\"btnStr\":\"1,7,2,3\"},{\"funId\":204,\"btnStr\":\"1,7,2,3\"},{\"funId\":205,\"btnStr\":\"1,7,2,3\"},{\"funId\":206,\"btnStr\":\"1,2,7,3\"},{\"funId\":212,\"btnStr\":\"1,7,2,3\"},{\"funId\":201,\"btnStr\":\"1,2,7,3\"},{\"funId\":202,\"btnStr\":\"1,2,7,3\"},{\"funId\":40,\"btnStr\":\"1,2,7,3\"},{\"funId\":232,\"btnStr\":\"1,2,7,3\"},{\"funId\":233,\"btnStr\":\"1,2,7,3\"}]',NULL,'0'),(6,'RoleFunctions','5','[22][23][25][26][194][195][31][33][200][201][41][199][202]',NULL,NULL,'0'),(7,'RoleFunctions','6','[22][23][220][240][25][217][218][26][194][195][31][59][207][208][209][226][227][228][229][235][237][210][211][241][33][199][242][41][200][201][202][40][232][233][197][203][204][205][206][212]','[{\"funId\":\"33\",\"btnStr\":\"4\"}]',NULL,'0'),(9,'RoleFunctions','7','[168][13][12][16][14][15][189][18][19][132]',NULL,NULL,'0'),(10,'RoleFunctions','8','[168][13][12][16][14][15][189][18][19][132][22][23][25][26][27][157][158][155][156][125][31][127][126][128][33][34][35][36][37][39][40][41][42][43][46][47][48][49][50][51][52][53][54][55][56][57][192][59][60][61][62][63][65][66][68][69][70][71][73][74][76][77][79][191][81][82][83][85][89][161][86][176][165][160][28][134][91][92][29][94][95][97][104][99][100][101][102][105][107][108][110][111][113][114][116][117][118][120][121][131][135][123][122][20][130][146][147][138][148][149][153][140][145][184][152][143][170][171][169][166][167][163][164][172][173][179][178][181][182][183][186][187][247]',NULL,NULL,'0'),(11,'RoleFunctions','9','[168][13][12][16][14][15][189][18][19][132][22][23][25][26][27][157][158][155][156][125][31][127][126][128][33][34][35][36][37][39][40][41][42][43][46][47][48][49][50][51][52][53][54][55][56][57][192][59][60][61][62][63][65][66][68][69][70][71][73][74][76][77][79][191][81][82][83][85][89][161][86][176][165][160][28][134][91][92][29][94][95][97][104][99][100][101][102][105][107][108][110][111][113][114][116][117][118][120][121][131][135][123][122][20][130][146][147][138][148][149][153][140][145][184][152][143][170][171][169][166][167][163][164][172][173][179][178][181][182][183][186][187][188]',NULL,NULL,'0'),(12,'UserRole','1','[5]',NULL,NULL,'0'),(13,'UserRole','2','[6][7]',NULL,NULL,'0'),(14,'UserDepot','2','[1][2][6][7]',NULL,NULL,'0'),(15,'UserDepot','1','[1][2][5][6][7][10][12][14][15][17]',NULL,NULL,'0'),(16,'UserRole','63','[10]',NULL,63,'0'),(18,'UserDepot','63','[14][15]',NULL,63,'0'),(19,'UserDepot','5','[6][45][46][50]',NULL,NULL,'0'),(20,'UserRole','5','[5]',NULL,NULL,'0'),(21,'UserRole','64','[13]',NULL,NULL,'0'),(22,'UserDepot','64','[1]',NULL,NULL,'0'),(23,'UserRole','65','[5]',NULL,NULL,'0'),(24,'UserDepot','65','[1]',NULL,NULL,'0'),(25,'UserCustomer','64','[5][2]',NULL,NULL,'0'),(26,'UserCustomer','65','[6]',NULL,NULL,'0'),(27,'UserCustomer','63','[58]',NULL,63,'0'),(28,'UserDepot','96','[7]',NULL,NULL,'0'),(29,'UserRole','96','[6]',NULL,NULL,'0'),(30,'UserRole','113','[10]',NULL,NULL,'0'),(32,'RoleFunctions','10','[210][225][211][261][32][241][33][199][242][38][41][200][201][239][202][40][232][233][197][44][203][204][205][206][212][246][198][207][259][208][209][226][227][248][228][229][59][235][237][244][22][21][23][220][247][25][24][217][218][26][194][195][31][13][14][243][15][234][236]','[{\"funId\":13,\"btnStr\":\"1\"},{\"funId\":14,\"btnStr\":\"1\"},{\"funId\":243,\"btnStr\":\"1\"},{\"funId\":234,\"btnStr\":\"1\"},{\"funId\":236,\"btnStr\":\"1\"},{\"funId\":22,\"btnStr\":\"1\"},{\"funId\":23,\"btnStr\":\"1,3\"},{\"funId\":220,\"btnStr\":\"1\"},{\"funId\":247,\"btnStr\":\"1\"},{\"funId\":25,\"btnStr\":\"1,3\"},{\"funId\":217,\"btnStr\":\"1,3\"},{\"funId\":218,\"btnStr\":\"1,3\"},{\"funId\":26,\"btnStr\":\"1\"},{\"funId\":194,\"btnStr\":\"1\"},{\"funId\":195,\"btnStr\":\"1\"},{\"funId\":31,\"btnStr\":\"1\"},{\"funId\":261,\"btnStr\":\"1,2,7,3\"},{\"funId\":241,\"btnStr\":\"1,2,7,3\"},{\"funId\":33,\"btnStr\":\"1,2,7,3\"},{\"funId\":199,\"btnStr\":\"1,7,2,3\"},{\"funId\":242,\"btnStr\":\"1,2,7,3\"},{\"funId\":41,\"btnStr\":\"1,2,7,3\"},{\"funId\":200,\"btnStr\":\"1,2,7,3\"},{\"funId\":210,\"btnStr\":\"1,2,7,3\"},{\"funId\":211,\"btnStr\":\"1,2,7,3\"},{\"funId\":197,\"btnStr\":\"1,2,7,3\"},{\"funId\":203,\"btnStr\":\"1,7,2,3\"},{\"funId\":204,\"btnStr\":\"1,7,2,3\"},{\"funId\":205,\"btnStr\":\"1,2,7,3\"},{\"funId\":206,\"btnStr\":\"1,7,2,3\"},{\"funId\":212,\"btnStr\":\"1,2,7,3\"},{\"funId\":201,\"btnStr\":\"1,2,7,3\"},{\"funId\":202,\"btnStr\":\"1,2,7,3\"},{\"funId\":40,\"btnStr\":\"1,2,7,3\"},{\"funId\":232,\"btnStr\":\"1,2,7,3\"},{\"funId\":233,\"btnStr\":\"1,2,7,3\"}]',NULL,'0'),(34,'UserRole','115','[10]',NULL,NULL,'0'),(35,'UserRole','117','[10]',NULL,NULL,'0'),(36,'UserDepot','117','[8][9]',NULL,NULL,'0'),(37,'UserCustomer','117','[52]',NULL,NULL,'0'),(38,'UserRole','120','[4]',NULL,NULL,'0'),(41,'RoleFunctions','12','',NULL,NULL,'0'),(48,'RoleFunctions','13','[59][207][208][209][226][227][228][229][235][237][210][211][241][33][199][242][41][200]',NULL,NULL,'0'),(51,'UserRole','74','[10]',NULL,NULL,'0'),(52,'UserDepot','121','[13]',NULL,NULL,'0'),(54,'UserDepot','115','[13]',NULL,NULL,'0'),(56,'UserCustomer','115','[56]',NULL,NULL,'0'),(57,'UserCustomer','121','[56]',NULL,NULL,'0'),(67,'UserRole','131','[17]',NULL,63,'0'),(68,'RoleFunctions','16','[210]',NULL,63,'0'),(69,'RoleFunctions','17','[210][225][211][241][32][33][199][242][38][41][200][201][239][202][40][232][233][197][44][203][204][205][206][212]','[{\"funId\":\"241\",\"btnStr\":\"1,2\"},{\"funId\":\"33\",\"btnStr\":\"1,2\"},{\"funId\":\"199\",\"btnStr\":\"1,2\"},{\"funId\":\"242\",\"btnStr\":\"1,2\"},{\"funId\":\"41\",\"btnStr\":\"1,2\"},{\"funId\":\"200\",\"btnStr\":\"1,2\"},{\"funId\":\"210\",\"btnStr\":\"1,2\"},{\"funId\":\"211\",\"btnStr\":\"1,2\"},{\"funId\":\"197\",\"btnStr\":\"1\"},{\"funId\":\"203\",\"btnStr\":\"1\"},{\"funId\":\"204\",\"btnStr\":\"1\"},{\"funId\":\"205\",\"btnStr\":\"1\"},{\"funId\":\"206\",\"btnStr\":\"1\"},{\"funId\":\"212\",\"btnStr\":\"1\"},{\"funId\":\"201\",\"btnStr\":\"1,2\"},{\"funId\":\"202\",\"btnStr\":\"1,2\"},{\"funId\":\"40\",\"btnStr\":\"1,2\"},{\"funId\":\"232\",\"btnStr\":\"1,2\"},{\"funId\":\"233\",\"btnStr\":\"1,2\"}]',63,'0'),(83,'RoleFunctions','21','[225][210][211][32][261][241][33][199][38][242][41][200][239][201][202][40][232][233][44][197][203][204][205][206][212][198][246][207][259][208][209][226][227][248][228][229][59][235][237][244][21][22][23][220][247][24][25][217][218][26][194][195][31]','[{\"funId\":22,\"btnStr\":\"1\"},{\"funId\":23,\"btnStr\":\"1,3\"},{\"funId\":220,\"btnStr\":\"1\"},{\"funId\":247,\"btnStr\":\"1\"},{\"funId\":25,\"btnStr\":\"1,3\"},{\"funId\":217,\"btnStr\":\"1,3\"},{\"funId\":218,\"btnStr\":\"1,3\"},{\"funId\":26,\"btnStr\":\"1\"},{\"funId\":194,\"btnStr\":\"1\"},{\"funId\":195,\"btnStr\":\"1\"},{\"funId\":31,\"btnStr\":\"1\"},{\"funId\":261,\"btnStr\":\"1,2,3,7\"},{\"funId\":241,\"btnStr\":\"1,2,3,7\"},{\"funId\":33,\"btnStr\":\"1,2,3,7\"},{\"funId\":199,\"btnStr\":\"1,2,3,7\"},{\"funId\":242,\"btnStr\":\"1,2,3,7\"},{\"funId\":41,\"btnStr\":\"1,2,3,7\"},{\"funId\":200,\"btnStr\":\"1,2,3,7\"},{\"funId\":210,\"btnStr\":\"1,2,3,7\"},{\"funId\":211,\"btnStr\":\"1,2,3,7\"},{\"funId\":197,\"btnStr\":\"1,2,3,7\"},{\"funId\":203,\"btnStr\":\"1,2,3,7\"},{\"funId\":204,\"btnStr\":\"1,2,3,7\"},{\"funId\":205,\"btnStr\":\"1,2,3,7\"},{\"funId\":206,\"btnStr\":\"1,2,3,7\"},{\"funId\":212,\"btnStr\":\"1,2,3,7\"},{\"funId\":201,\"btnStr\":\"1,2,3,7\"},{\"funId\":202,\"btnStr\":\"1,2,3,7\"},{\"funId\":40,\"btnStr\":\"1,2,3,7\"},{\"funId\":232,\"btnStr\":\"1,2,3,7\"},{\"funId\":233,\"btnStr\":\"1,2,3,7\"}]',63,'0');
/*!40000 ALTER TABLE `jsh_user_business` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'jsh_erp'
--

--
-- Dumping routines for database 'jsh_erp'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-11  6:43:53
