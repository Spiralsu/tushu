/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80030
 Source Host           : localhost:3306
 Source Schema         : db_book

 Target Server Type    : MySQL
 Target Server Version : 80030
 File Encoding         : 65001

 Date: 03/03/2026 12:14:29
*/

-- 新增：创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS db_book CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci;
-- 新增：使用指定的数据库
USE db_book;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for book_info
-- ----------------------------
DROP TABLE IF EXISTS `book_info`;
CREATE TABLE `book_info`  (
  `bookId` int(0) NOT NULL AUTO_INCREMENT,
  `bookName` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `bookAuthor` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `bookPrice` decimal(10, 2) NOT NULL,
  `bookTypeId` int(0) NOT NULL COMMENT '所属院系ID',
  `bookDesc` varchar(1000) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `isBorrow` tinyint(0) NOT NULL DEFAULT 0 COMMENT '0:在库 1:漂流中 2:申请中',
  `bookImage` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `uploaderId` int(0) NULL DEFAULT NULL COMMENT '上传/捐赠者ID',
  `auditStatus` tinyint(0) NULL DEFAULT 1 COMMENT '0:待审核 1:已通过',
  `bookCount` int(0) NULL DEFAULT 1 COMMENT '图书总数',
  `inventory` int(0) NULL DEFAULT 1 COMMENT '当前剩余库存',
  PRIMARY KEY (`bookId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '图书信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of book_info
-- ----------------------------
INSERT INTO `book_info` VALUES (1, '深入理解计算机系统', 'Randal E.Bryant', 139.00, 3, '计算机科学经典教材，程序员必读之书。', 0, '/files/1704111437840fb5b0a0646c268e05f192a0c940c4623.jpg', 1, 1, 5, 5);
INSERT INTO `book_info` VALUES (2, 'Java编程思想', 'Bruce Eckel', 108.00, 3, 'Java语言的经典著作，涵盖了Java编程的方方面面。', 0, '/files/1704111453260dc18e7a4d407b807b949385b05c0e73b.jpg', 1, 1, 5, 5);
INSERT INTO `book_info` VALUES (3, '三体', '刘慈欣', 58.00, 9, '中国科幻文学的里程碑之作，讲述了人类与三体文明的博弈。', 0, '/files/1704111487191c756e96d278e218b1ced1c5690612f1b.jpg', 1, 1, 5, 5);
INSERT INTO `book_info` VALUES (4, '活着', '余华', 28.00, 9, '讲述了一个人一生的故事，展现了生命的韧性。', 0, '/files/17041115105547c3b0a4801d52ee1ea662435175b2a98.jpg', 1, 1, 5, 5);
INSERT INTO `book_info` VALUES (5, '高等数学（上册）', '同济大学数学系', 45.00, 5, '理工科大学生必修的基础课程教材。', 0, '/files/17041115137710e4055cd4fc20c2482037013c8d56af0.jpg', 1, 1, 5, 5);
INSERT INTO `book_info` VALUES (6, '经济学原理', '曼昆', 68.00, 8, '经济学入门经典，深入浅出地阐述了经济学基本原理。', 0, '/files/17041115208750e4055cd4fc20c2482037013c8d56af0.jpg', 1, 1, 5, 5);
INSERT INTO `book_info` VALUES (7, '明朝那些事儿', '当年明月', 39.80, 9, '用幽默风趣的语言讲述了明朝三百年的历史故事。', 0, '/files/17041115246174e8965305e8d45a5ec2d5313ec192fa5.jpg', 1, 1, 5, 5);

-- ----------------------------
-- Table structure for book_type
-- ----------------------------
DROP TABLE IF EXISTS `book_type`;
CREATE TABLE `book_type`  (
  `bookTypeId` int(0) NOT NULL AUTO_INCREMENT,
  `bookTypeName` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '院系名称',
  `bookTypeDesc` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '院系英文名或描述',
  PRIMARY KEY (`bookTypeId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of book_type
-- ----------------------------
INSERT INTO `book_type` VALUES (1, '机械工程系', 'Mechanical Engineering');
INSERT INTO `book_type` VALUES (2, '电气与控制工程系', 'Electrical & Control Engineering');
INSERT INTO `book_type` VALUES (3, '大数据与智能工程系', 'Big Data & Intelligent Engineering');
INSERT INTO `book_type` VALUES (4, '土木工程系', 'Civil Engineering');
INSERT INTO `book_type` VALUES (5, '地球科学与工程系', 'Earth Science & Engineering');
INSERT INTO `book_type` VALUES (6, '矿业工程系', 'Mining Engineering');
INSERT INTO `book_type` VALUES (7, '管理科学与工程系', 'Management Science & Engineering');
INSERT INTO `book_type` VALUES (8, '经济与管理系', 'Economics & Management');
INSERT INTO `book_type` VALUES (9, '艺术与设计科学系', 'Art & Design Science');
INSERT INTO `book_type` VALUES (10, '材料科学与工程系', 'Materials Science & Engineering');

-- ----------------------------
-- Table structure for borrow
-- ----------------------------
DROP TABLE IF EXISTS `borrow`;
CREATE TABLE `borrow`  (
  `borrowId` int(0) NOT NULL AUTO_INCREMENT,
  `userId` int(0) NOT NULL COMMENT '申请人ID',
  `bookId` int(0) NOT NULL,
  `borrowTime` datetime(0) NULL DEFAULT NULL COMMENT '实际交接时间',
  `returnTime` datetime(0) NULL DEFAULT NULL COMMENT '归还时间',
  `applyTime` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '申请时间',
  `borrowReason` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '使用理由',
  `state` tinyint(0) NOT NULL DEFAULT 0 COMMENT '0:审核中 1:待交接 2:漂流中 3:已归还 4:已驳回',
  PRIMARY KEY (`borrowId`) USING BTREE,
  INDEX `fk_user`(`userId`) USING BTREE,
  INDEX `fk_book`(`bookId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '漂流记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of borrow
-- ----------------------------
INSERT INTO `borrow` VALUES (4, 2, 1, '2026-02-12 15:55:14', '2026-02-12 15:56:06', '2026-02-12 15:54:58', '用户申请漂流', 2);

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`  (
  `messageId` int(0) NOT NULL AUTO_INCREMENT,
  `userId` int(0) NOT NULL COMMENT '接收用户ID',
  `content` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `isRead` tinyint(0) NULL DEFAULT 0,
  `createTime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`messageId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of message
-- ----------------------------
INSERT INTO `message` VALUES (1, 2, '您的漂流申请《Java编程思想》已通过审核，请前往领取。', 0, '2026-01-29 18:42:39');
INSERT INTO `message` VALUES (2, 2, '您的漂流申请《Java编程思想》被驳回。原因：无', 0, '2026-01-29 19:07:54');
INSERT INTO `message` VALUES (3, 2, '您的漂流申请《深入理解计算机系统》已通过审核，请前往领取。', 0, '2026-01-30 18:35:57');
INSERT INTO `message` VALUES (4, 2, '您的漂流申请《深入理解计算机系统》已通过审核，请前往领取。', 0, '2026-02-12 15:55:14');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `userId` int(0) NOT NULL AUTO_INCREMENT,
  `userName` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '姓名',
  `studentId` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '学号(唯一登录凭证)',
  `userPassword` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '校园网密码',
  `isAdmin` tinyint(0) NOT NULL DEFAULT 0 COMMENT '1:管理员 0:普通用户',
  `status` tinyint(0) NOT NULL DEFAULT 0 COMMENT '0:待审核 1:正常 2:禁用',
  PRIMARY KEY (`userId`) USING BTREE,
  UNIQUE INDEX `uk_student_id`(`studentId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '用户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, '系统管理员', 'admin', '123456', 1, 1);
INSERT INTO `user` VALUES (2, '王某', '243121201', '123456', 0, 1);

SET FOREIGN_KEY_CHECKS = 1;