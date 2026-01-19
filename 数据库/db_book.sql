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

 Date: 19/01/2026 13:29:24
*/

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
  PRIMARY KEY (`bookId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '图书信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of book_info
-- ----------------------------

-- ----------------------------
-- Table structure for book_type
-- ----------------------------
DROP TABLE IF EXISTS `book_type`;
CREATE TABLE `book_type`  (
  `bookTypeId` int(0) NOT NULL AUTO_INCREMENT,
  `bookTypeName` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '院系名称',
  `bookTypeDesc` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '院系英文名或描述',
  PRIMARY KEY (`bookTypeId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '漂流记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of borrow
-- ----------------------------

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
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of message
-- ----------------------------

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

SET FOREIGN_KEY_CHECKS = 1;
