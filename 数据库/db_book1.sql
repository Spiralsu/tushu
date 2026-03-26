/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80044 (8.0.44)
 Source Host           : localhost:3306
 Source Schema         : db_book

 Target Server Type    : MySQL
 Target Server Version : 80044 (8.0.44)
 File Encoding         : 65001

 Date: 23/03/2026 21:43:11
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for book_info
-- ----------------------------
DROP TABLE IF EXISTS `book_info`;
CREATE TABLE `book_info`  (
  `bookId` int NOT NULL AUTO_INCREMENT,
  `bookName` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `bookAuthor` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `bookPrice` decimal(10, 2) NOT NULL,
  `bookTypeId` int NOT NULL COMMENT '所属院系ID',
  `bookDesc` varchar(1000) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `isBorrow` tinyint NOT NULL DEFAULT 0 COMMENT '0:在库 1:漂流中 2:申请中',
  `bookImage` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `uploaderId` int NULL DEFAULT NULL COMMENT '上传/捐赠者ID',
  `auditStatus` tinyint NULL DEFAULT 1 COMMENT '0:待审核 1:已通过',
  `bookCount` int NULL DEFAULT 1 COMMENT '图书总数',
  `inventory` int NULL DEFAULT 1 COMMENT '当前剩余库存',
  `contactInfo` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '交接联系方式(仅审核通过后可见)',
  PRIMARY KEY (`bookId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '图书信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of book_info
-- ----------------------------
INSERT INTO `book_info` VALUES (1, '机械原理', '武照云', 0.00, 1, '', 1, '/files/1773027308032_机械原理.jpg', 2, 1, 1, 0, '此书已遗憾退出漂流');
INSERT INTO `book_info` VALUES (3, '三体', '刘慈欣', 0.00, 11, '', 1, '/files/1773068360902_三体.jpg', 3, 1, 1, 0, '尚能A-XX');
INSERT INTO `book_info` VALUES (4, '明朝那些事儿', '当年明月 ', 0.00, 11, '', 0, '/files/1773068440736_明朝那些事儿.jpg', 1, 1, 1, 1, 'A');
INSERT INTO `book_info` VALUES (5, '经济学原理', 'N.格里高利·曼昆', 0.00, 8, '', 1, '/files/1773070403846_经济学原理.jpg', 3, 1, 1, 0, '此书已遗憾退出漂流');
INSERT INTO `book_info` VALUES (6, '计算机组成原理', '谭志虎', 0.00, 3, '', 1, '/files/1773070454436_计算机组成原理.jpg', 3, 1, 1, 0, '此书已遗憾退出漂流');
INSERT INTO `book_info` VALUES (7, '深入理解计算机系统', '兰德尔', 0.00, 3, '', 1, '/files/1773131701247_深入理解计算机系统.jpg', 4, 1, 1, 0, '此书已遗憾退出漂流');
INSERT INTO `book_info` VALUES (8, '计算机网络', '谢希仁', 0.00, 3, '', 0, '/files/1773131765291_计算机网络.jpg', 4, 1, 1, 1, '尚能C-XX');
INSERT INTO `book_info` VALUES (9, '机械制图与CAD习题集', '中国煤炭教育协会职业教育教材编审委员会', 0.00, 1, '', 0, '/files/1773131845211_机械制图与CAD习题集.jpg', 4, 1, 1, 1, '尚能C-xxx');
INSERT INTO `book_info` VALUES (10, '机械制图项目教程', '朱春香', 0.00, 1, '', 1, '/files/1773131937806_机械制图项目教程.jpg', 4, 1, 1, 0, '此书已遗憾退出漂流');
INSERT INTO `book_info` VALUES (11, '机械制图习题集(第3版）', '胡胜', 0.00, 1, '好好读书', 0, '/files/1773671446929_机械制图习题集(第3版）.jpg', 3, 1, 1, 1, '123');
INSERT INTO `book_info` VALUES (12, '机械制图典型习题及解答（第二版）', '李三', 0.00, 1, '', 0, '/files/1773671530588_机械制图典型习题及解答（第二版）.jpg', 3, 1, 1, 1, '1');
INSERT INTO `book_info` VALUES (13, '活着', '余华', 0.00, 1, '', 1, '/files/1773671553357_活着.jpg', 3, 1, 1, -1, '此书已遗憾退出漂流');

-- ----------------------------
-- Table structure for book_type
-- ----------------------------
DROP TABLE IF EXISTS `book_type`;
CREATE TABLE `book_type`  (
  `bookTypeId` int NOT NULL AUTO_INCREMENT,
  `bookTypeName` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '院系名称',
  `bookTypeDesc` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '院系英文名或描述',
  PRIMARY KEY (`bookTypeId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

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
INSERT INTO `book_type` VALUES (11, '其他', '其他未分类的漂流书籍');

-- ----------------------------
-- Table structure for book_wish
-- ----------------------------
DROP TABLE IF EXISTS `book_wish`;
CREATE TABLE `book_wish`  (
  `wish_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `book_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `wish_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `state` int NULL DEFAULT 0 COMMENT '0-求书中, 1-已满足',
  `create_time` datetime NULL DEFAULT NULL,
  `fulfill_user_id` int NULL DEFAULT NULL,
  PRIMARY KEY (`wish_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of book_wish
-- ----------------------------
INSERT INTO `book_wish` VALUES (1, 2, '王某', '计算机组成原理', '希望能借到这本书！', 1, '2026-03-09 23:31:24', 0);

-- ----------------------------
-- Table structure for borrow
-- ----------------------------
DROP TABLE IF EXISTS `borrow`;
CREATE TABLE `borrow`  (
  `borrowId` int NOT NULL AUTO_INCREMENT,
  `userId` int NOT NULL COMMENT '申请人ID',
  `bookId` int NOT NULL,
  `borrowTime` datetime NULL DEFAULT NULL COMMENT '实际交接时间',
  `returnTime` datetime NULL DEFAULT NULL COMMENT '归还时间',
  `applyTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `borrowReason` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '使用理由',
  `state` tinyint NOT NULL DEFAULT 0 COMMENT '0:审核中 1:待交接 2:漂流中 3:已归还 4:已驳回',
  `returnMsg` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '归还寄语',
  `secretCode` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '交接验证暗号',
  `borrow_days` int NULL DEFAULT 30 COMMENT '申请借阅的天数',
  PRIMARY KEY (`borrowId`) USING BTREE,
  INDEX `fk_user`(`userId` ASC) USING BTREE,
  INDEX `fk_book`(`bookId` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '漂流记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of borrow
-- ----------------------------
INSERT INTO `borrow` VALUES (1, 4, 1, NULL, NULL, '2026-03-09 12:28:28', '使用一月', 5, NULL, '499828', 30);
INSERT INTO `borrow` VALUES (2, 4, 1, NULL, NULL, '2026-03-09 13:11:48', '申请使用一周', 5, NULL, '915212', 30);
INSERT INTO `borrow` VALUES (3, 4, 1, '2026-03-09 13:20:26', '2026-03-09 13:37:47', '2026-03-09 13:19:25', '用一周', 2, '很有用', '965154', 30);
INSERT INTO `borrow` VALUES (4, 2, 1, '2026-03-09 13:40:29', '2026-03-09 13:41:27', '2026-03-09 13:38:49', '用一年', 2, '不错', '159081', 30);
INSERT INTO `borrow` VALUES (5, 4, 1, '2026-03-09 13:46:47', '2026-03-09 13:52:23', '2026-03-09 13:45:47', '需要使用一周', 2, '很有用', '477287', 30);
INSERT INTO `borrow` VALUES (6, 2, 1, NULL, NULL, '2026-03-09 20:10:46', '需要使用一月', 3, NULL, NULL, 30);
INSERT INTO `borrow` VALUES (7, 3, 1, '2026-03-09 20:21:42', '2026-03-09 20:22:35', '2026-03-09 20:21:02', '需要使用一月', 2, '很不错', '900866', 30);
INSERT INTO `borrow` VALUES (8, 2, 1, '2026-03-09 20:29:48', '2026-03-09 20:32:16', '2026-03-09 20:25:13', '我要使用一月', 2, '很不错', '225169', 30);
INSERT INTO `borrow` VALUES (9, 2, 1, NULL, NULL, '2026-03-09 20:33:13', '需要使用一月', 3, NULL, NULL, 30);
INSERT INTO `borrow` VALUES (10, 2, 1, NULL, NULL, '2026-03-09 20:34:28', '使用一月', 3, NULL, NULL, 30);
INSERT INTO `borrow` VALUES (11, 3, 1, NULL, NULL, '2026-03-09 20:48:58', '用户申请漂流', 3, NULL, NULL, 30);
INSERT INTO `borrow` VALUES (12, 4, 1, NULL, NULL, '2026-03-09 21:48:07', '需要使用一月', 7, '【系统强制取消】超出处理期限，系统已自动释放该申请。', '259624', 30);
INSERT INTO `borrow` VALUES (13, 3, 1, NULL, NULL, '2026-03-09 21:48:26', '需要使用一月', 7, '【系统强制取消】超出处理期限，系统已自动释放该申请。', NULL, 30);
INSERT INTO `borrow` VALUES (14, 3, 4, NULL, NULL, '2026-03-09 23:02:41', '我', 3, NULL, NULL, NULL);
INSERT INTO `borrow` VALUES (15, 2, 6, '2026-03-09 23:35:26', '2026-03-15 20:15:48', '2026-03-09 23:34:52', '需要使用一月', 6, '【遗失登记】1', '849193', NULL);
INSERT INTO `borrow` VALUES (16, 2, 5, '2026-03-09 23:37:38', '2026-03-15 20:15:51', '2026-03-09 23:36:47', '我', 6, '【遗失登记】2', '788830', NULL);
INSERT INTO `borrow` VALUES (17, 1, 4, '2026-03-10 15:30:20', '2026-03-10 16:27:34', '2026-03-10 15:28:23', '1', 2, '不错', '276272', NULL);
INSERT INTO `borrow` VALUES (18, 4, 3, '2026-03-10 15:41:14', NULL, '2026-03-10 15:40:16', '1', 1, NULL, '113258', NULL);
INSERT INTO `borrow` VALUES (19, 2, 10, '2026-03-10 16:48:08', '2026-03-16 22:21:36', '2026-03-10 16:47:42', '1', 6, '【遗失登记】1', '500614', NULL);
INSERT INTO `borrow` VALUES (20, 2, 4, NULL, NULL, '2026-03-10 16:56:26', '1', 5, '【用户放弃】个人原因取消', '648075', NULL);
INSERT INTO `borrow` VALUES (21, 2, 2, '2026-03-10 17:55:09', '2026-03-10 18:05:42', '2026-03-10 17:54:34', '0', 2, '1', '779274', NULL);
INSERT INTO `borrow` VALUES (22, 3, 2, '2026-03-10 18:06:13', NULL, '2026-03-10 18:05:56', '0', 1, NULL, '676136', 0);
INSERT INTO `borrow` VALUES (23, 2, 9, NULL, NULL, '2026-03-10 19:54:46', '测试', 7, '【系统强制取消】超出处理期限，系统已自动释放该申请。', NULL, 30);
INSERT INTO `borrow` VALUES (24, 3, 7, '2026-03-16 22:34:47', '2026-03-16 22:35:06', '2026-03-16 22:34:36', '1', 6, '【遗失登记】1', '306482', 2);
INSERT INTO `borrow` VALUES (25, 3, 1, '2026-03-16 22:39:05', '2026-03-16 22:39:44', '2026-03-16 22:38:57', '1', 6, '【遗失登记】1', '958643', 1);
INSERT INTO `borrow` VALUES (26, 2, 13, '2026-03-16 22:57:37', '2026-03-16 22:57:47', '2026-03-16 22:57:29', '1', 6, '【遗失登记】1', '241773', 1);

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`  (
  `messageId` int NOT NULL AUTO_INCREMENT,
  `userId` int NOT NULL COMMENT '接收用户ID',
  `content` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `isRead` tinyint NULL DEFAULT 0,
  `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`messageId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 190 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of message
-- ----------------------------
INSERT INTO `message` VALUES (1, 3, '【系统通知】恭喜！您的书籍《机械原理》已成功发布到漂流大厅！\n感谢您为校园旧书漂流做出的贡献。当有同学申请借阅时，系统会第一时间通知您。', 0, '2026-03-09 11:35:10');
INSERT INTO `message` VALUES (2, 3, '【系统通知】有同学申请借阅了您发布的书籍《机械原理》！\nTA 的申请理由是：[使用一月]\n目前正在等待管理员审核，请留意后续通知。', 0, '2026-03-09 12:28:28');
INSERT INTO `message` VALUES (3, 4, '【系统通知】你的漂流申请《机械原理》已通过！\n对方交接说明：【尚能X:X-XXX】\n你的专属提货暗号为：【499828】。碰面拿到书后，请将此暗号出示给发布者，由发布者在系统中进行核销。', 0, '2026-03-09 12:29:08');
INSERT INTO `message` VALUES (4, 3, '【系统通知】您发布的书籍《机械原理》借阅申请已通过审核！\n请准备好书籍等待交接。对方在拿到书后会向您出示 6 位数字暗号，请您在“借阅信息管理”的【我借出的】列表中点击核销暗号，完成最终交接！', 0, '2026-03-09 12:29:08');
INSERT INTO `message` VALUES (5, 3, '【紧急交接协商】您发布的《机械原理》的借阅者遇到了交接困难（如异性宿舍无法进入等）。\nTA 给您的紧急留言是：【我是男生进去，可以手机号联系吗？1XXXXXXXXXX】\n请您通过上述方式联系 TA，或约定一个新的公共地点完成交接（对方的提书暗号依然有效，记得在此系统中核销哦）。', 0, '2026-03-09 12:33:47');
INSERT INTO `message` VALUES (6, 4, '【系统通知】您已成功撤销对《机械原理》的借阅。', 0, '2026-03-09 12:34:48');
INSERT INTO `message` VALUES (7, 3, '【系统通知】遗憾！对方因 [联系不到] 撤销了对《机械原理》的借阅。书籍已自动为您重新上架。', 0, '2026-03-09 12:34:49');
INSERT INTO `message` VALUES (8, 3, '【系统通知】有同学申请借阅了您发布的书籍《机械原理》！\nTA 的申请理由是：[申请使用一周]\n目前正在等待管理员审核，请留意后续通知。', 0, '2026-03-09 13:11:48');
INSERT INTO `message` VALUES (9, 4, '【系统通知】你的漂流申请《机械原理》已通过！\n对方交接说明：【尚能X:X-XXX】\n你的专属提货暗号为：【915212】。碰面拿到书后，请将此暗号出示给发布者，由发布者在系统中进行核销。', 0, '2026-03-09 13:12:13');
INSERT INTO `message` VALUES (10, 3, '【系统通知】您发布的书籍《机械原理》借阅申请已通过审核！\n请准备好书籍等待交接。对方在拿到书后会向您出示 6 位数字暗号，请您在“借阅信息管理”的【我借出的】列表中点击核销暗号，完成最终交接！', 0, '2026-03-09 13:12:13');
INSERT INTO `message` VALUES (11, 4, '【系统通知】您已成功撤销对《机械原理》的借阅。', 0, '2026-03-09 13:12:49');
INSERT INTO `message` VALUES (12, 3, '【系统通知】遗憾！对方因 [无法交接] 撤销了对《机械原理》的借阅。书籍已自动为您重新上架。', 0, '2026-03-09 13:12:49');
INSERT INTO `message` VALUES (13, 3, '【系统通知】有同学申请借阅了您发布的书籍《机械原理》！\nTA 的申请理由是：[用一周]\n目前正在等待管理员审核，请留意后续通知。', 0, '2026-03-09 13:19:25');
INSERT INTO `message` VALUES (14, 4, '【系统通知】你的漂流申请《机械原理》已通过！\n对方交接说明：【尚能X:X-XXX】\n你的专属提货暗号为：【965154】。碰面拿到书后，请将此暗号出示给发布者，由发布者在系统中进行核销。', 0, '2026-03-09 13:19:38');
INSERT INTO `message` VALUES (15, 3, '【系统通知】您发布的书籍《机械原理》借阅申请已通过审核！\n请准备好书籍等待交接。对方在拿到书后会向您出示 6 位数字暗号，请您在“借阅信息管理”的【我借出的】列表中点击核销暗号，完成最终交接！', 0, '2026-03-09 13:19:38');
INSERT INTO `message` VALUES (16, 4, '【漂流接力】恭喜！您已读完《机械原理》。\n由于这是漂流书籍，书本物理上现在由您保管。系统已自动将该书转移到了您的【我借出的】列表中。\n等待下一位有缘人申请时，您将作为新的传递者与TA当面交接暗号！让知识继续流动吧！', 0, '2026-03-09 13:37:47');
INSERT INTO `message` VALUES (17, 3, '【系统通知】您最初发布的《机械原理》已经被前一位读者读完，并成功接力成为了下一站的火种。感谢您的分享！', 0, '2026-03-09 13:37:47');
INSERT INTO `message` VALUES (18, 4, '【系统通知】有同学申请借阅了您发布的书籍《机械原理》！\nTA 的申请理由是：[用一年]\n目前正在等待管理员审核，请留意后续通知。', 0, '2026-03-09 13:38:50');
INSERT INTO `message` VALUES (19, 2, '【系统通知】你的漂流申请《机械原理》已通过！\n对方交接说明：【尚能XX：XXXX-XXX】\n你的专属提货暗号为：【159081】。碰面拿到书后，请将此暗号出示给发布者，由发布者在系统中进行核销。', 0, '2026-03-09 13:39:28');
INSERT INTO `message` VALUES (20, 4, '【系统通知】您发布的书籍《机械原理》借阅申请已通过审核！\n请准备好书籍等待交接。对方在拿到书后会向您出示 6 位数字暗号，请您在“借阅信息管理”的【我借出的】列表中点击核销暗号，完成最终交接！', 0, '2026-03-09 13:39:28');
INSERT INTO `message` VALUES (21, 2, '【漂流接力】恭喜！您已读完《机械原理》。\n由于这是漂流书籍，书本物理上现在由您保管。系统已自动将该书转移到了您的【我借出的】列表中。\n等待下一位有缘人申请时，您将作为新的传递者与TA当面交接暗号！让知识继续流动吧！', 0, '2026-03-09 13:41:27');
INSERT INTO `message` VALUES (22, 4, '【系统通知】您最初发布的《机械原理》已经被前一位读者读完，并成功接力成为了下一站的火种。感谢您的分享！', 0, '2026-03-09 13:41:27');
INSERT INTO `message` VALUES (23, 2, '【系统通知】有同学申请借阅了您发布的书籍《机械原理》！\nTA 的申请理由是：[需要使用一周]\n目前正在等待管理员审核，请留意后续通知。', 0, '2026-03-09 13:45:47');
INSERT INTO `message` VALUES (24, 4, '【系统通知】你的漂流申请《机械原理》已通过！\n对方交接说明：【尚能A：X-XXXX】\n你的专属提货暗号为：【477287】。碰面拿到书后，请将此暗号出示给发布者，由发布者在系统中进行核销。', 0, '2026-03-09 13:46:15');
INSERT INTO `message` VALUES (25, 2, '【系统通知】您发布的书籍《机械原理》借阅申请已通过审核！\n请准备好书籍等待交接。对方在拿到书后会向您出示 6 位数字暗号，请您在“借阅信息管理”的【我借出的】列表中点击核销暗号，完成最终交接！', 0, '2026-03-09 13:46:15');
INSERT INTO `message` VALUES (26, 4, '【漂流接力】恭喜！您已读完《机械原理》。\n由于这是漂流书籍，书本物理上现在由您保管。系统已自动将该书转移到了您的【我借出的】列表中。\n等待下一位有缘人申请时，您将作为新的传递者与TA当面交接暗号！让知识继续流动吧！', 0, '2026-03-09 13:52:23');
INSERT INTO `message` VALUES (27, 2, '【系统通知】您最初发布的《机械原理》已经被前一位读者读完，并成功接力成为了下一站的火种。感谢您的分享！', 0, '2026-03-09 13:52:23');
INSERT INTO `message` VALUES (28, 4, '【系统通知】有同学申请借阅了您发布的书籍《机械原理》！\nTA 的申请理由是：[需要使用一月]\n目前正在等待管理员审核，请留意后续通知。', 0, '2026-03-09 20:10:47');
INSERT INTO `message` VALUES (29, 2, '【系统通知】您的漂流申请《机械原理》被驳回。原因：无', 0, '2026-03-09 20:17:37');
INSERT INTO `message` VALUES (30, 4, '【系统通知】有同学申请借阅了您发布的书籍《机械原理》！\nTA 的申请理由是：[需要使用一月]\n目前正在等待管理员审核，请留意后续通知。', 0, '2026-03-09 20:21:03');
INSERT INTO `message` VALUES (31, 3, '【系统通知】你的漂流申请《机械原理》已通过！\n对方交接说明：【我是女生，宿舍号：XX-XX】\n你的专属提货暗号为：【900866】。碰面拿到书后，请将此暗号出示给发布者，由发布者在系统中进行核销。', 0, '2026-03-09 20:21:30');
INSERT INTO `message` VALUES (32, 4, '【系统通知】您发布的书籍《机械原理》借阅申请已通过审核！\n请准备好书籍等待交接。对方在拿到书后会向您出示 6 位数字暗号，请您在“借阅信息管理”的【我借出的】列表中点击核销暗号，完成最终交接！', 0, '2026-03-09 20:21:30');
INSERT INTO `message` VALUES (33, 3, '【催还通知】⏰ 您借阅的《机械原理》已经漂流很久啦，尽快阅读并传递哦！', 0, '2026-03-09 20:21:51');
INSERT INTO `message` VALUES (34, 3, '【漂流接力】恭喜！您已读完《机械原理》。\n由于这是漂流书籍，书本物理上现在由您保管。系统已自动将该书转移到了您的【我借出的】列表中。\n等待下一位有缘人申请时，您将作为新的传递者与TA当面交接暗号！让知识继续流动吧！', 0, '2026-03-09 20:22:35');
INSERT INTO `message` VALUES (35, 4, '【系统通知】您最初发布的《机械原理》已经被前一位读者读完，并成功接力成为了下一站的火种。感谢您的分享！', 0, '2026-03-09 20:22:35');
INSERT INTO `message` VALUES (36, 3, '【系统通知】有同学申请借阅了您发布的书籍《机械原理》！\nTA 的申请理由是：[我要使用一月]\n目前正在等待管理员审核，请留意后续通知。', 0, '2026-03-09 20:25:13');
INSERT INTO `message` VALUES (37, 2, '【系统通知】你的漂流申请《机械原理》已通过！\n对方交接说明：【尚能XX-X】\n你的专属提货暗号为：【225169】。碰面拿到书后，请将此暗号出示给发布者，由发布者在系统中进行核销。', 0, '2026-03-09 20:26:33');
INSERT INTO `message` VALUES (38, 3, '【系统通知】您发布的书籍《机械原理》借阅申请已通过审核！\n请准备好书籍等待交接。对方在拿到书后会向您出示 6 位数字暗号，请您在“借阅信息管理”的【我借出的】列表中点击核销暗号，完成最终交接！', 0, '2026-03-09 20:26:34');
INSERT INTO `message` VALUES (39, 3, '【紧急交接协商】您发布的《机械原理》的借阅者遇到了交接困难。\nTA 的紧急留言是：【无法进入，请联系1XXXXXXX】\n请您通过上述方式联系 TA，或约定一个新的公共地点完成交接（对方的提书暗号依然有效，记得在此系统中核销哦）。', 0, '2026-03-09 20:28:02');
INSERT INTO `message` VALUES (40, 2, '【催还通知】⏰ 您借阅的《机械原理》已经漂流很久啦，尽快阅读并传递哦！', 0, '2026-03-09 20:31:45');
INSERT INTO `message` VALUES (41, 2, '【漂流接力】恭喜！您已读完《机械原理》。\n由于这是漂流书籍，书本物理上现在由您保管。系统已自动将该书转移到了您的【我借出的】列表中。\n等待下一位有缘人申请时，您将作为新的传递者与TA当面交接暗号！让知识继续流动吧！', 0, '2026-03-09 20:32:16');
INSERT INTO `message` VALUES (42, 3, '【系统通知】您最初发布的《机械原理》已经被前一位读者读完，并成功接力成为了下一站的火种。感谢您的分享！', 0, '2026-03-09 20:32:16');
INSERT INTO `message` VALUES (43, 2, '【系统通知】有同学申请借阅了您发布的书籍《机械原理》！\nTA 的申请理由是：[需要使用一月]\n目前正在等待管理员审核，请留意后续通知。', 0, '2026-03-09 20:33:13');
INSERT INTO `message` VALUES (44, 2, '【系统通知】您的漂流申请《机械原理》被驳回。原因：无', 0, '2026-03-09 20:33:33');
INSERT INTO `message` VALUES (45, 2, '【系统通知】有同学申请借阅了您发布的书籍《机械原理》！\nTA 的申请理由是：[使用一月]\n目前正在等待管理员审核，请留意后续通知。', 0, '2026-03-09 20:34:29');
INSERT INTO `message` VALUES (46, 2, '【系统通知】您的漂流申请《机械原理》被驳回。原因：无', 0, '2026-03-09 20:35:22');
INSERT INTO `message` VALUES (47, 2, '【系统通知】有同学申请借阅了您发布的书籍《机械原理》！\nTA 的申请理由是：[用户申请漂流]\n目前正在等待管理员审核，请留意后续通知。', 0, '2026-03-09 20:48:58');
INSERT INTO `message` VALUES (48, 3, '【系统通知】您的漂流申请《机械原理》被驳回。原因：无', 0, '2026-03-09 20:49:07');
INSERT INTO `message` VALUES (49, 2, '【系统通知】有同学申请借阅了您发布的书籍《机械原理》！\nTA 的申请理由是：[需要使用一月]\n目前正在等待管理员审核，请留意后续通知。', 0, '2026-03-09 21:48:07');
INSERT INTO `message` VALUES (50, 2, '【系统通知】有同学申请借阅了您发布的书籍《机械原理》！\nTA 的申请理由是：[需要使用一月]\n目前正在等待管理员审核，请留意后续通知。', 0, '2026-03-09 21:48:27');
INSERT INTO `message` VALUES (51, 4, '【系统通知】你的漂流申请《机械原理》已通过！\n对方交接说明：【尚能XX-XXX】\n你的专属提货暗号为：【259624】。碰面拿到书后，请将此暗号出示给发布者，由发布者在系统中进行核销。', 0, '2026-03-09 21:48:50');
INSERT INTO `message` VALUES (52, 2, '【系统通知】您发布的书籍《机械原理》借阅申请已通过审核！\n请准备好书籍等待交接。对方在拿到书后会向您出示 6 位数字暗号，请您在“借阅信息管理”的【我借出的】列表中点击核销暗号，完成最终交接！', 0, '2026-03-09 21:48:50');
INSERT INTO `message` VALUES (53, 3, '【系统通知】恭喜！您的书籍《数据结构》已成功发布到漂流大厅！\n感谢您为校园旧书漂流做出的贡献。当有同学申请借阅时，系统会第一时间通知您。', 0, '2026-03-09 22:21:42');
INSERT INTO `message` VALUES (54, 3, '【系统通知】恭喜！您的书籍《三体》已成功发布到漂流大厅！\n感谢您为校园旧书漂流做出的贡献。当有同学申请借阅时，系统会第一时间通知您。', 0, '2026-03-09 22:59:22');
INSERT INTO `message` VALUES (55, 2, '【系统通知】恭喜！您的书籍《明朝那些事儿》已成功发布到漂流大厅！\n感谢您为校园旧书漂流做出的贡献。当有同学申请借阅时，系统会第一时间通知您。', 0, '2026-03-09 23:00:44');
INSERT INTO `message` VALUES (56, 2, '【系统通知】有同学申请借阅了您发布的书籍《明朝那些事儿》！\nTA 的申请理由是：[我]\n目前正在等待管理员审核，请留意后续通知。', 0, '2026-03-09 23:02:42');
INSERT INTO `message` VALUES (57, 3, '【系统通知】您的漂流申请《明朝那些事儿》被驳回。原因：无', 0, '2026-03-09 23:03:02');
INSERT INTO `message` VALUES (58, 3, '【系统通知】恭喜！您的书籍《经济学原理》已成功发布到漂流大厅！\n感谢您为校园旧书漂流做出的贡献。当有同学申请借阅时，系统会第一时间通知您。', 0, '2026-03-09 23:33:25');
INSERT INTO `message` VALUES (59, 3, '【系统通知】恭喜！您的书籍《计算机组成原理》已成功发布到漂流大厅！\n感谢您为校园旧书漂流做出的贡献。当有同学申请借阅时，系统会第一时间通知您。', 0, '2026-03-09 23:34:16');
INSERT INTO `message` VALUES (60, 2, '好消息！您在心愿广场求购的书籍《计算机组成原理》刚刚被发布啦，快去漂流大厅看看吧！', 0, '2026-03-09 23:34:16');
INSERT INTO `message` VALUES (61, 3, '【系统通知】有同学申请借阅了您发布的书籍《计算机组成原理》！\nTA 的申请理由是：[需要使用一月]\n目前正在等待管理员审核，请留意后续通知。', 0, '2026-03-09 23:34:53');
INSERT INTO `message` VALUES (62, 2, '【系统通知】你的漂流申请《计算机组成原理》已通过！\n对方交接说明：【尚能A-XXXX】\n你的专属提货暗号为：【849193】。碰面拿到书后，请将此暗号出示给发布者，由发布者在系统中进行核销。', 0, '2026-03-09 23:35:13');
INSERT INTO `message` VALUES (63, 3, '【系统通知】您发布的书籍《计算机组成原理》借阅申请已通过审核！\n请准备好书籍等待交接。对方在拿到书后会向您出示 6 位数字暗号，请您在“借阅信息管理”的【我借出的】列表中点击核销暗号，完成最终交接！', 0, '2026-03-09 23:35:13');
INSERT INTO `message` VALUES (64, 3, '【系统通知】有同学申请借阅了您发布的书籍《经济学原理》！\nTA 的申请理由是：[我]\n目前正在等待管理员审核，请留意后续通知。', 0, '2026-03-09 23:36:48');
INSERT INTO `message` VALUES (65, 2, '【系统通知】你的漂流申请《经济学原理》已通过！\n对方交接说明：【尚能A-XXX】\n你的专属提货暗号为：【788830】。碰面拿到书后，请将此暗号出示给发布者，由发布者在系统中进行核销。', 0, '2026-03-09 23:37:19');
INSERT INTO `message` VALUES (66, 3, '【系统通知】您发布的书籍《经济学原理》借阅申请已通过审核！\n请准备好书籍等待交接。对方在拿到书后会向您出示 6 位数字暗号，请您在“借阅信息管理”的【我借出的】列表中点击核销暗号，完成最终交接！', 0, '2026-03-09 23:37:19');
INSERT INTO `message` VALUES (67, 2, '【系统通知】有同学申请借阅了您发布的书籍《明朝那些事儿》！\nTA 的申请理由是：[1]\n目前正在等待管理员审核，请留意后续通知。', 0, '2026-03-10 15:28:24');
INSERT INTO `message` VALUES (68, 1, '【系统通知】你的漂流申请《明朝那些事儿》已通过！\n对方交接说明：【尚能B-XXX】\n你的专属提货暗号为：【276272】。碰面拿到书后，请将此暗号出示给发布者，由发布者在系统中进行核销。', 0, '2026-03-10 15:28:34');
INSERT INTO `message` VALUES (69, 2, '【系统通知】您发布的书籍《明朝那些事儿》借阅申请已通过审核！\n请准备好书籍等待交接。对方在拿到书后会向您出示 6 位数字暗号，请您在“借阅信息管理”的【我借出的】列表中点击核销暗号，完成最终交接！', 0, '2026-03-10 15:28:34');
INSERT INTO `message` VALUES (70, 3, '【系统通知】有同学申请借阅了您发布的书籍《三体》！\nTA 的申请理由是：[1]\n目前正在等待管理员审核，请留意后续通知。', 0, '2026-03-10 15:40:16');
INSERT INTO `message` VALUES (71, 4, '【系统通知】你的漂流申请《三体》已通过！\n对方交接说明：【尚能A-XX】\n你的专属提货暗号为：【113258】。碰面拿到书后，请将此暗号出示给发布者，由发布者在系统中进行核销。', 0, '2026-03-10 15:41:00');
INSERT INTO `message` VALUES (72, 3, '【系统通知】您发布的书籍《三体》借阅申请已通过审核！\n请准备好书籍等待交接。对方在拿到书后会向您出示 6 位数字暗号，请您在“借阅信息管理”的【我借出的】列表中点击核销暗号，完成最终交接！', 0, '2026-03-10 15:41:00');
INSERT INTO `message` VALUES (73, 1, '【漂流接力】恭喜！您已读完《明朝那些事儿》。\n由于这是漂流书籍，书本物理上现在由您保管。系统已自动将该书转移到了您的【我借出的】列表中。\n等待下一位有缘人申请时，您将作为新的传递者与TA当面交接暗号！让知识继续流动吧！', 0, '2026-03-10 16:27:34');
INSERT INTO `message` VALUES (74, 2, '【系统通知】您最初发布的《明朝那些事儿》已经被前一位读者读完，并成功接力成为了下一站的火种。感谢您的分享！', 0, '2026-03-10 16:27:34');
INSERT INTO `message` VALUES (75, 4, '【系统通知】您的书籍《深入理解计算机系统》已发布！', 0, '2026-03-10 16:35:02');
INSERT INTO `message` VALUES (76, 4, '【信用奖励】感谢您发布书籍！您的信用分已恢复 2 分，当前信用分：42 分。', 0, '2026-03-10 16:35:02');
INSERT INTO `message` VALUES (77, 4, '【系统通知】您的书籍《计算机网络》已发布！', 0, '2026-03-10 16:36:07');
INSERT INTO `message` VALUES (78, 4, '【信用奖励】感谢您发布书籍！您的信用分已恢复 2 分，当前信用分：44 分。', 0, '2026-03-10 16:36:07');
INSERT INTO `message` VALUES (79, 4, '【系统通知】您的书籍《机械制图与CAD习题集》已发布！', 0, '2026-03-10 16:37:26');
INSERT INTO `message` VALUES (80, 4, '【信用奖励】感谢您发布书籍！您的信用分已恢复 2 分，当前信用分：46 分。', 0, '2026-03-10 16:37:26');
INSERT INTO `message` VALUES (81, 4, '【系统通知】您的书籍《机械制图项目教程》已发布！', 0, '2026-03-10 16:38:59');
INSERT INTO `message` VALUES (82, 4, '【信用奖励】感谢您发布书籍！您的信用分已恢复 2 分，当前信用分：48 分。', 0, '2026-03-10 16:38:59');
INSERT INTO `message` VALUES (83, 4, '【系统通知】有同学申请借阅了您发布的书籍《机械制图项目教程》！\nTA 的申请理由是：[1]\n目前正在等待管理员审核，请留意后续通知。', 0, '2026-03-10 16:47:42');
INSERT INTO `message` VALUES (84, 2, '【系统通知】你的漂流申请《机械制图项目教程》已通过！\n对方交接说明：【尚能C--XX】\n你的专属提货暗号为：【500614】。碰面拿到书后，请将此暗号出示给发布者，由发布者在系统中进行核销。', 0, '2026-03-10 16:47:56');
INSERT INTO `message` VALUES (85, 4, '【系统通知】您发布的书籍《机械制图项目教程》借阅申请已通过审核！\n请准备好书籍等待交接。对方在拿到书后会向您出示 6 位数字暗号，请您在“借阅信息管理”的【我借出的】列表中点击核销暗号，完成最终交接！', 0, '2026-03-10 16:47:56');
INSERT INTO `message` VALUES (86, 4, '【信用奖励】成功交接给下一位书友！信用分恢复 3 分，当前：51 分。', 0, '2026-03-10 16:48:08');
INSERT INTO `message` VALUES (87, 1, '【系统通知】有同学申请借阅了您发布的书籍《明朝那些事儿》！\nTA 的申请理由是：[1]\n目前正在等待管理员审核，请留意后续通知。', 0, '2026-03-10 16:56:27');
INSERT INTO `message` VALUES (88, 2, '【系统通知】你的漂流申请《明朝那些事儿》已通过！\n对方交接说明：【A】\n你的专属提货暗号为：【648075】。碰面拿到书后，请将此暗号出示给发布者，由发布者在系统中进行核销。', 0, '2026-03-10 16:56:35');
INSERT INTO `message` VALUES (89, 1, '【系统通知】您发布的书籍《明朝那些事儿》借阅申请已通过审核！\n请准备好书籍等待交接。对方在拿到书后会向您出示 6 位数字暗号，请您在“借阅信息管理”的【我借出的】列表中点击核销暗号，完成最终交接！', 0, '2026-03-10 16:56:35');
INSERT INTO `message` VALUES (90, 2, '【系统通知】您已撤销对《明朝那些事儿》的借阅。', 0, '2026-03-10 17:13:14');
INSERT INTO `message` VALUES (91, 1, '【通知】对方在【交接阶段】撤销了借阅。原因：[个人原因取消]', 0, '2026-03-10 17:13:14');
INSERT INTO `message` VALUES (92, 3, '【系统通知】有同学申请借阅了您发布的书籍《数据结构》！\nTA 的申请理由是：[0]\n目前正在等待管理员审核，请留意后续通知。', 0, '2026-03-10 17:54:34');
INSERT INTO `message` VALUES (93, 2, '【系统通知】你的漂流申请《数据结构》已通过！\n对方交接说明：【尚能A-XXX】\n你的专属提货暗号为：【779274】。碰面拿到书后，请将此暗号出示给发布者，由发布者在系统中进行核销。', 0, '2026-03-10 17:54:46');
INSERT INTO `message` VALUES (94, 3, '【系统通知】您发布的书籍《数据结构》借阅申请已通过审核！\n请准备好书籍等待交接。对方在拿到书后会向您出示 6 位数字暗号，请您在“借阅信息管理”的【我借出的】列表中点击核销暗号，完成最终交接！', 0, '2026-03-10 17:54:46');
INSERT INTO `message` VALUES (95, 2, '【漂流接力】恭喜！您已读完《数据结构》。\n由于这是漂流书籍，书本物理上现在由您保管。系统已自动将该书转移到了您的【我借出的】列表中。\n等待下一位有缘人申请时，您将作为新的传递者与TA当面交接暗号！让知识继续流动吧！', 0, '2026-03-10 18:05:42');
INSERT INTO `message` VALUES (96, 3, '【系统通知】您最初发布的《数据结构》已经被前一位读者读完，并成功接力成为了下一站的火种。感谢您的分享！', 0, '2026-03-10 18:05:42');
INSERT INTO `message` VALUES (97, 2, '【系统通知】有同学申请借阅了您发布的书籍《数据结构》！\nTA 的申请理由是：[0]\n目前正在等待管理员审核，请留意后续通知。', 0, '2026-03-10 18:05:56');
INSERT INTO `message` VALUES (98, 3, '【系统通知】你的漂流申请《数据结构》已通过！\n对方交接说明：【1】\n你的专属提货暗号为：【676136】。碰面拿到书后，请将此暗号出示给发布者，由发布者在系统中进行核销。', 0, '2026-03-10 18:06:01');
INSERT INTO `message` VALUES (99, 2, '【系统通知】您发布的书籍《数据结构》借阅申请已通过审核！\n请准备好书籍等待交接。对方在拿到书后会向您出示 6 位数字暗号，请您在“借阅信息管理”的【我借出的】列表中点击核销暗号，完成最终交接！', 0, '2026-03-10 18:06:01');
INSERT INTO `message` VALUES (100, 2, '【信用奖励】成功交接给下一位书友！本次恢复 3 分 (今日已累计恢复 3/5 分)，当前：73 分。', 0, '2026-03-10 18:06:13');
INSERT INTO `message` VALUES (101, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：98 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:07:00');
INSERT INTO `message` VALUES (102, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：96 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:08:00');
INSERT INTO `message` VALUES (103, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：94 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:09:00');
INSERT INTO `message` VALUES (104, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：92 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:10:00');
INSERT INTO `message` VALUES (105, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：90 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:11:00');
INSERT INTO `message` VALUES (106, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：88 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:12:00');
INSERT INTO `message` VALUES (107, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：86 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:13:00');
INSERT INTO `message` VALUES (108, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：84 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:14:00');
INSERT INTO `message` VALUES (109, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：82 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:15:00');
INSERT INTO `message` VALUES (110, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：80 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:16:00');
INSERT INTO `message` VALUES (111, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：78 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:17:00');
INSERT INTO `message` VALUES (112, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：76 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:18:00');
INSERT INTO `message` VALUES (113, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：74 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:19:00');
INSERT INTO `message` VALUES (114, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：72 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:20:00');
INSERT INTO `message` VALUES (115, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：70 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:21:00');
INSERT INTO `message` VALUES (116, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：68 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:22:00');
INSERT INTO `message` VALUES (117, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：66 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:23:00');
INSERT INTO `message` VALUES (118, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：64 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:24:00');
INSERT INTO `message` VALUES (119, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：62 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:25:00');
INSERT INTO `message` VALUES (120, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：60 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:26:00');
INSERT INTO `message` VALUES (121, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：58 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:27:00');
INSERT INTO `message` VALUES (122, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：56 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:28:00');
INSERT INTO `message` VALUES (123, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：54 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:29:00');
INSERT INTO `message` VALUES (124, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：52 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:30:00');
INSERT INTO `message` VALUES (125, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：50 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:31:00');
INSERT INTO `message` VALUES (126, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：48 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:32:00');
INSERT INTO `message` VALUES (127, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：46 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:33:00');
INSERT INTO `message` VALUES (128, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：44 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:34:00');
INSERT INTO `message` VALUES (129, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：42 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:35:00');
INSERT INTO `message` VALUES (130, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：40 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:36:00');
INSERT INTO `message` VALUES (131, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：38 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:37:00');
INSERT INTO `message` VALUES (132, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：36 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:38:00');
INSERT INTO `message` VALUES (133, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：34 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:39:00');
INSERT INTO `message` VALUES (134, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：32 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:40:00');
INSERT INTO `message` VALUES (135, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：30 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:41:00');
INSERT INTO `message` VALUES (136, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：28 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:42:00');
INSERT INTO `message` VALUES (137, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：26 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:43:00');
INSERT INTO `message` VALUES (138, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：24 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:44:00');
INSERT INTO `message` VALUES (139, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：22 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:45:00');
INSERT INTO `message` VALUES (140, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：20 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:46:00');
INSERT INTO `message` VALUES (141, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：18 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:47:00');
INSERT INTO `message` VALUES (142, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：16 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:48:00');
INSERT INTO `message` VALUES (143, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：14 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:49:00');
INSERT INTO `message` VALUES (144, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：12 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:50:00');
INSERT INTO `message` VALUES (145, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：10 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:51:00');
INSERT INTO `message` VALUES (146, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：8 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:52:00');
INSERT INTO `message` VALUES (147, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：6 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:53:00');
INSERT INTO `message` VALUES (148, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：4 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:54:00');
INSERT INTO `message` VALUES (149, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：2 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:55:00');
INSERT INTO `message` VALUES (150, 3, '【逾期惩罚】您借阅的《数据结构》已逾期！系统今日已自动扣除 2 信用分。当前剩余：0 分。低于60分将彻底锁死权限，请速归还！', 0, '2026-03-10 18:56:00');
INSERT INTO `message` VALUES (151, 4, '【系统通知】有同学申请借阅了您发布的书籍《机械制图与CAD习题集》！\nTA 的申请理由是：[测试]\n目前正在等待管理员审核，请留意后续通知。', 0, '2026-03-10 19:54:47');
INSERT INTO `message` VALUES (152, 2, '【系统通知】您对《机械制图与CAD习题集》的申请因超时未处理，已被系统自动取消。', 0, '2026-03-10 19:58:00');
INSERT INTO `message` VALUES (153, 4, '【系统通知】由于超时未处理，针对《机械制图与CAD习题集》的借阅申请已被系统强制释放，若已扣库存则现已回退。', 0, '2026-03-10 19:58:00');
INSERT INTO `message` VALUES (154, 3, '【系统通知】您对《机械原理》的申请因超时未处理，已被系统自动取消。', 0, '2026-03-13 16:12:00');
INSERT INTO `message` VALUES (155, 2, '【系统通知】由于超时未处理，针对《机械原理》的借阅申请已被系统强制释放，若已扣库存则现已回退。', 0, '2026-03-13 16:12:00');
INSERT INTO `message` VALUES (156, 4, '【系统通知】您对《机械原理》的申请因超时未处理，已被系统自动取消。', 0, '2026-03-13 16:12:00');
INSERT INTO `message` VALUES (157, 2, '【系统通知】由于超时未处理，针对《机械原理》的借阅申请已被系统强制释放，若已扣库存则现已回退。', 0, '2026-03-13 16:12:00');
INSERT INTO `message` VALUES (158, 3, '【催还通知】⏰ 您借阅的《数据结构》已经到期啦，请尽快阅读并传递哦！', 0, '2026-03-15 18:45:11');
INSERT INTO `message` VALUES (159, 3, '【遗失通知】很遗憾，您发布的《计算机组成原理》被读者登记为遗失/损毁。原因：1', 0, '2026-03-15 20:15:48');
INSERT INTO `message` VALUES (160, 3, '【遗失通知】很遗憾，您发布的《经济学原理》被读者登记为遗失/损毁。原因：2', 0, '2026-03-15 20:15:51');
INSERT INTO `message` VALUES (161, 4, '【遗失通知】很遗憾，您发布的《机械制图项目教程》被读者登记为遗失/损毁。原因：1', 0, '2026-03-16 22:21:36');
INSERT INTO `message` VALUES (162, 3, '【系统通知】您的书籍《机械制图习题集(第3版）》已发布！', 0, '2026-03-16 22:30:53');
INSERT INTO `message` VALUES (163, 3, '【信用奖励】发布书籍奖励生效！本次恢复 2 分 (今日已累计恢复 2/5 分)，当前信用分：2 分。', 0, '2026-03-16 22:30:53');
INSERT INTO `message` VALUES (164, 3, '【逾期惩罚】您的借阅已逾期！系统已扣除您 2 信用分。当前剩余：0 分。', 0, '2026-03-16 22:31:00');
INSERT INTO `message` VALUES (165, 3, '【系统通知】您的书籍《机械制图典型习题及解答（第二版）》已发布！', 0, '2026-03-16 22:32:20');
INSERT INTO `message` VALUES (166, 3, '【信用奖励】发布书籍奖励生效！本次恢复 2 分 (今日已累计恢复 4/5 分)，当前信用分：2 分。', 0, '2026-03-16 22:32:20');
INSERT INTO `message` VALUES (167, 3, '【系统通知】您的书籍《活着》已发布！', 0, '2026-03-16 22:32:40');
INSERT INTO `message` VALUES (168, 3, '【信用奖励】发布书籍奖励生效！本次恢复 1 分 (今日已累计恢复 5/5 分)，当前信用分：3 分。', 0, '2026-03-16 22:32:40');
INSERT INTO `message` VALUES (169, 4, '【系统通知】有同学申请借阅了您发布的书籍《深入理解计算机系统》！\nTA 的申请理由是：[1]\n目前正在等待管理员审核，请留意后续通知。', 0, '2026-03-16 22:34:15');
INSERT INTO `message` VALUES (170, 3, '【系统通知】你的漂流申请《深入理解计算机系统》已通过！\n对方交接说明：【尚能C-XXX】\n你的专属提货暗号为：【306482】。碰面拿到书后，请将此暗号出示给发布者，由发布者在系统中进行核销。', 0, '2026-03-16 22:34:36');
INSERT INTO `message` VALUES (171, 4, '【系统通知】您发布的书籍《深入理解计算机系统》借阅申请已通过审核！请等待交接。', 0, '2026-03-16 22:34:37');
INSERT INTO `message` VALUES (172, 4, '【信用奖励】成功交接给下一位书友！本次恢复 3 分 (今日已累计恢复 3/5 分)，当前：54 分。', 0, '2026-03-16 22:34:47');
INSERT INTO `message` VALUES (173, 4, '【遗失通知】很遗憾，您发布的《深入理解计算机系统》被读者登记为遗失。原因：1', 0, '2026-03-16 22:35:06');
INSERT INTO `message` VALUES (174, 3, '【扣分通知】因登记书籍遗失，系统已自动扣除您 10 信誉分。保护好每一本书是对漂流社区的尊重！', 0, '2026-03-16 22:35:06');
INSERT INTO `message` VALUES (175, 2, '【系统通知】有同学申请借阅了您发布的书籍《机械原理》！\nTA 的申请理由是：[1]\n目前正在等待管理员审核，请留意后续通知。', 0, '2026-03-16 22:38:45');
INSERT INTO `message` VALUES (176, 3, '【系统通知】你的漂流申请《机械原理》已通过！\n对方交接说明：【尚能XX-XXX】\n你的专属提货暗号为：【958643】。碰面拿到书后，请将此暗号出示给发布者，由发布者在系统中进行核销。', 0, '2026-03-16 22:38:57');
INSERT INTO `message` VALUES (177, 2, '【系统通知】您发布的书籍《机械原理》借阅申请已通过审核！请等待交接。', 0, '2026-03-16 22:38:58');
INSERT INTO `message` VALUES (178, 2, '【信用奖励】成功交接给下一位书友！本次恢复 3 分 (今日已累计恢复 3/5 分)，当前：76 分。', 0, '2026-03-16 22:39:05');
INSERT INTO `message` VALUES (179, 2, '【遗失通知】很遗憾，您发布的《机械原理》被读者登记为遗失。原因：1', 0, '2026-03-16 22:39:44');
INSERT INTO `message` VALUES (180, 3, '【扣分通知】因登记书籍遗失，系统已自动扣除您 10 信誉分。保护好每一本书是对漂流社区的尊重！', 0, '2026-03-16 22:39:44');
INSERT INTO `message` VALUES (181, 3, '【系统通知】有同学申请借阅了您发布的书籍《活着》！\nTA 的申请理由是：[1]\n目前正在等待管理员审核，请留意后续通知。', 0, '2026-03-16 22:56:14');
INSERT INTO `message` VALUES (182, 2, '【系统通知】你的漂流申请《活着》已通过！\n对方交接说明：【1】\n你的专属提货暗号为：【241773】。碰面拿到书后，请将此暗号出示给发布者，由发布者在系统中进行核销。', 0, '2026-03-16 22:57:29');
INSERT INTO `message` VALUES (183, 3, '【系统通知】您发布的书籍《活着》借阅申请已通过审核！请等待交接。', 0, '2026-03-16 22:57:29');
INSERT INTO `message` VALUES (184, 3, '【遗失通知】很遗憾，您发布的《活着》被读者登记为遗失。原因：1', 0, '2026-03-16 22:57:47');
INSERT INTO `message` VALUES (185, 2, '【扣分通知】因登记书籍遗失，系统已自动扣除您 10 信誉分。保护好每一本书是对漂流社区的尊重！', 0, '2026-03-16 22:57:47');
INSERT INTO `message` VALUES (186, 3, '【逾期惩罚】您的借阅已逾期！系统已扣除您 2 信用分。当前剩余：49 分。', 0, '2026-03-16 22:59:00');
INSERT INTO `message` VALUES (187, 3, '【逾期惩罚】您的借阅已逾期！系统已扣除您 2 信用分。当前剩余：47 分。', 0, '2026-03-16 23:00:00');
INSERT INTO `message` VALUES (188, 3, '【逾期惩罚】您的借阅已逾期！系统已扣除您 2 信用分。当前剩余：45 分。', 0, '2026-03-23 20:59:00');
INSERT INTO `message` VALUES (189, 3, '【逾期惩罚】您的借阅已逾期！系统已扣除您 2 信用分。当前剩余：43 分。', 0, '2026-03-23 21:00:00');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `userId` int NOT NULL AUTO_INCREMENT,
  `userName` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '姓名',
  `studentId` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '学号(唯一登录凭证)',
  `userPassword` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '校园网密码',
  `isAdmin` tinyint NOT NULL DEFAULT 0 COMMENT '1:管理员 0:普通用户',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '0:待审核 1:正常 2:禁用',
  `gender` tinyint(1) NULL DEFAULT 2 COMMENT '1:男 0:女 2:保密',
  `open_id` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '微信测试号的OpenID',
  `department` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '所属系部',
  `credit_score` int NULL DEFAULT 100 COMMENT '信用分',
  `today_add_score` int NULL DEFAULT 0 COMMENT '今日已加信用分',
  `score_update_date` date NULL DEFAULT NULL COMMENT '信用分最后增加日期',
  PRIMARY KEY (`userId`) USING BTREE,
  UNIQUE INDEX `uk_student_id`(`studentId` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '用户信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, '系统管理员', 'admin', '123456', 1, 1, 2, NULL, NULL, 100, 0, NULL);
INSERT INTO `user` VALUES (2, '王某', '243121201', '123456', 0, 1, 1, 'UID_2CDO4DAZsd9E1Vn3O0NCNJhxEry2', '大数据与智能工程系', 66, 3, '2026-03-16');
INSERT INTO `user` VALUES (3, '李某', '243121202', '123456', 0, 1, 1, 'UID_AqWFQ9ImI1tEWIrrUL39tvP8KzFg', '大数据与智能工程系', 43, 5, '2026-03-16');
INSERT INTO `user` VALUES (4, '刘某', '243121203', '123456', 0, 1, 0, NULL, '大数据与智能工程系', 54, 3, '2026-03-16');

SET FOREIGN_KEY_CHECKS = 1;
