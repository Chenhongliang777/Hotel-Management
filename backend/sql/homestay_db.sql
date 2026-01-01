/*
 Navicat Premium Data Transfer

 Source Server         : Pika
 Source Server Type    : MySQL
 Source Server Version : 80017
 Source Host           : localhost:3306
 Source Schema         : homestay_db

 Target Server Type    : MySQL
 Target Server Version : 80017
 File Encoding         : 65001

 Date: 01/01/2026 03:17:13
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
USE homestay_db;

-- ----------------------------
-- Table structure for booking_order
-- ----------------------------
DROP TABLE IF EXISTS `booking_order`;
CREATE TABLE `booking_order`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单编号',
  `guest_id` bigint(20) NULL DEFAULT NULL COMMENT '宾客ID',
  `room_id` bigint(20) NULL DEFAULT NULL COMMENT '房间ID',
  `room_type_id` bigint(20) NOT NULL COMMENT '房型ID',
  `check_in_date` date NOT NULL COMMENT '入住日期',
  `check_out_date` date NOT NULL COMMENT '退房日期',
  `nights` int(11) NOT NULL COMMENT '入住晚数',
  `guest_count` int(11) NULL DEFAULT 1 COMMENT '入住人数',
  `room_price` decimal(10, 2) NOT NULL COMMENT '房间单价',
  `total_price` decimal(10, 2) NOT NULL COMMENT '订单总价',
  `deposit` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '保证金',
  `paid_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '已付金额',
  `extra_charges` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '额外消费',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'pending' COMMENT '状态: pending-待确认, confirmed-已确认, checked_in-已入住, checked_out-已退房, cancelled-已取消',
  `guest_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '入住人姓名',
  `guest_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '入住人电话',
  `guest_id_card` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '入住人身份证',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '备注',
  `check_in_time` datetime NULL DEFAULT NULL COMMENT '实际入住时间',
  `check_out_time` datetime NULL DEFAULT NULL COMMENT '实际退房时间',
  `operator_id` bigint(20) NULL DEFAULT NULL COMMENT '操作员ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '删除标记',
  `modify_count` int(11) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_guest_id`(`guest_id` ASC) USING BTREE,
  INDEX `idx_room_id`(`room_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_check_in_date`(`check_in_date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of booking_order
-- ----------------------------
INSERT INTO `booking_order` VALUES (1, 'ORD202412250001', 1, 3, 2, '2024-12-25', '2024-12-27', 2, 2, 298.00, 596.00, 178.80, 596.00, 0.00, 'checked_out', '王建国', '13900000001', '110101199001011234', NULL, NULL, NULL, NULL, '2024-12-20 10:30:00', '2025-12-30 03:28:24', 0, 0);
INSERT INTO `booking_order` VALUES (2, 'ORD202412260001', 2, 5, 3, '2024-12-26', '2024-12-28', 2, 2, 268.00, 536.00, 160.80, 536.00, 0.00, 'checked_out', '李梅', '13900000002', '110101199202022345', NULL, NULL, NULL, NULL, '2024-12-22 14:20:00', '2025-12-30 03:28:24', 0, 0);
INSERT INTO `booking_order` VALUES (3, 'ORD202412280001', 3, 8, 4, '2024-12-28', '2024-12-31', 3, 3, 458.00, 1374.00, 412.20, 1374.00, 5.00, 'checked_out', '张伟', '13900000003', '110101198803033456', NULL, NULL, '2026-01-01 02:26:33', 1, '2024-12-25 09:15:00', '2025-12-30 03:28:24', 0, 0);
INSERT INTO `booking_order` VALUES (4, 'ORD202412300001', 4, 4, 2, '2024-12-30', '2025-01-02', 3, 2, 368.00, 1104.00, 331.20, 331.20, 0.00, 'confirmed', '陈静', '13900000004', '110101199504044567', NULL, NULL, NULL, NULL, '2024-12-28 16:45:00', '2025-12-30 03:28:24', 0, 0);
INSERT INTO `booking_order` VALUES (5, 'ORD202412300002', 5, 1, 1, '2024-12-30', '2024-12-31', 1, 1, 198.00, 198.00, 59.40, 59.40, 0.00, 'confirmed', '刘洋', '13900000005', '110101199205055678', NULL, NULL, NULL, NULL, '2024-12-29 11:00:00', '2025-12-30 03:28:24', 0, 0);
INSERT INTO `booking_order` VALUES (6, 'ORD202512300001', NULL, NULL, 1, '2025-12-30', '2025-12-31', 1, 1, 198.00, 198.00, 59.40, 237.60, 0.00, 'pending', '杰', '15915452638', '440372200110269875', '', NULL, NULL, NULL, '2025-12-30 03:40:49', '2025-12-30 03:40:49', 0, 0);
INSERT INTO `booking_order` VALUES (7, 'ORD202512300002', NULL, NULL, 2, '2025-12-30', '2025-12-31', 1, 2, 298.00, 298.00, 89.40, 357.60, 0.00, 'cancelled', '杰', '15916238394', '440938200212238746', '', NULL, NULL, NULL, '2025-12-30 03:46:31', '2025-12-30 03:47:54', 0, 0);
INSERT INTO `booking_order` VALUES (8, 'ORD202512300003', NULL, NULL, 2, '2025-12-30', '2025-12-31', 1, 2, 298.00, 298.00, 89.40, 0.00, 0.00, 'pending', '杰', '15628392022', '440392200302123494', '', NULL, NULL, NULL, '2025-12-30 03:49:23', '2025-12-30 03:49:23', 0, 0);
INSERT INTO `booking_order` VALUES (9, 'ORD202512300004', NULL, NULL, 1, '2025-12-30', '2025-12-31', 1, 2, 198.00, 198.00, 59.40, 0.00, 0.00, 'pending', '杰', '15728390223', '440525200212233903', '', NULL, NULL, NULL, '2025-12-30 03:53:30', '2025-12-30 03:53:30', 0, 0);
INSERT INTO `booking_order` VALUES (10, 'ORD202512300005', NULL, NULL, 2, '2025-12-30', '2025-12-31', 1, 2, 298.00, 298.00, 89.40, 268.20, 0.00, 'pending', '杰', '15267382299', '440922200112243789', '', NULL, NULL, NULL, '2025-12-30 03:56:33', '2025-12-30 03:56:33', 0, 0);
INSERT INTO `booking_order` VALUES (11, 'ORD202512300006', NULL, NULL, 2, '2025-12-30', '2025-12-31', 1, 2, 298.00, 298.00, 89.40, 89.40, 0.00, 'pending', '杰', '15817238928', '440292200112239836', '', NULL, NULL, NULL, '2025-12-30 03:59:12', '2025-12-30 03:59:12', 0, 0);
INSERT INTO `booking_order` VALUES (12, 'ORD202512300007', NULL, NULL, 4, '2025-12-30', '2025-12-31', 1, 2, 458.00, 458.00, 137.40, 458.00, 0.00, 'pending', '杰', '15816273823', '440293200112233938', '', NULL, NULL, NULL, '2025-12-30 04:01:57', '2025-12-30 04:01:57', 0, 0);
INSERT INTO `booking_order` VALUES (13, 'ORD202512300008', 1, NULL, 3, '2025-12-30', '2025-12-31', 1, 2, 268.00, 268.00, 80.40, 268.00, 0.00, 'pending', '杰', '15928283782', '440512200211233048', '[已改订]', NULL, NULL, NULL, '2025-12-30 04:07:05', '2025-12-30 04:07:16', 0, 0);
INSERT INTO `booking_order` VALUES (14, 'ORD202512300009', 1, NULL, 1, '2025-12-30', '2025-12-31', 1, 2, 198.00, 198.00, 59.40, 198.00, 0.00, 'pending', '杰', '15828373920', '440512200212230938', '', NULL, NULL, NULL, '2025-12-30 04:19:58', '2025-12-30 04:19:58', 0, 0);
INSERT INTO `booking_order` VALUES (15, 'ORD202512300010', 1, NULL, 1, '2025-12-30', '2025-12-31', 1, 1, 198.00, 198.00, 59.40, 0.00, 0.00, 'pending', '王建国', '13900000001', NULL, '', NULL, NULL, NULL, '2025-12-30 14:20:28', '2025-12-30 14:20:28', 0, 0);
INSERT INTO `booking_order` VALUES (16, 'ORD202512300011', 1, NULL, 2, '2025-12-30', '2025-12-31', 1, 1, 298.00, 298.00, 89.40, 387.40, 0.00, 'pending', '王建国', '13900000001', '110101199001011234', '', NULL, NULL, NULL, '2025-12-30 14:29:23', '2025-12-30 14:29:23', 0, 0);
INSERT INTO `booking_order` VALUES (17, 'ORD202512300012', 1, NULL, 2, '2025-12-30', '2025-12-31', 1, 1, 298.00, 298.00, 89.40, 476.80, 0.00, 'pending', '王建国', '13900000001', '110101199001011234', '', NULL, NULL, NULL, '2025-12-30 14:30:42', '2025-12-30 14:30:42', 0, 0);
INSERT INTO `booking_order` VALUES (18, 'ORD202512300013', 1, NULL, 3, '2025-12-30', '2025-12-31', 1, 1, 268.00, 268.00, 80.40, 160.80, 0.00, 'confirmed', '王建国', '13900000001', '110101199001011234', '', NULL, NULL, NULL, '2025-12-30 14:33:12', '2025-12-30 14:33:12', 0, 0);
INSERT INTO `booking_order` VALUES (19, 'ORD202512300014', 1, NULL, 2, '2025-12-30', '2025-12-31', 1, 1, 298.00, 298.00, 89.40, 476.80, 0.00, 'confirmed', '王建国', '13900000001', '110101199001011234', '', NULL, NULL, NULL, '2025-12-30 14:41:07', '2025-12-30 14:41:07', 0, 0);
INSERT INTO `booking_order` VALUES (20, 'ORD202512300015', 1, 7, 3, '2025-12-30', '2025-12-31', 1, 1, 268.00, 268.00, 80.40, 428.80, 0.00, 'checked_in', '王建国', '13900000001', '110101199001011234', '', '2026-01-01 01:06:07', NULL, 3, '2025-12-30 14:49:40', '2025-12-30 14:49:40', 0, 0);
INSERT INTO `booking_order` VALUES (21, 'ORD202601010001', 1, 3, 2, '2026-01-01', '2026-01-02', 1, 1, 298.00, 298.00, 89.40, 387.40, 0.00, 'checked_out', '王建国', '13900000001', '110101199001011234', '', '2026-01-01 02:08:39', '2026-01-01 02:11:13', 1, '2026-01-01 02:08:11', '2026-01-01 02:08:11', 0, 0);
INSERT INTO `booking_order` VALUES (22, 'ORD202601010002', 1, NULL, 2, '2026-01-01', '2026-01-02', 1, 2, 298.00, 298.00, 89.40, 387.40, 0.00, 'pending', '杰', '15913256789', '440512200212234520', '', NULL, NULL, NULL, '2026-01-01 02:37:02', '2026-01-01 02:37:14', 0, 1);
INSERT INTO `booking_order` VALUES (23, 'ORD202601010003', 1, NULL, 1, '2026-01-01', '2026-01-02', 1, 1, 198.00, 198.00, 59.40, 316.80, 0.00, 'pending', '王建国', '13900000001', '110101199001011234', '', NULL, NULL, NULL, '2026-01-01 02:52:14', '2026-01-01 02:52:14', 0, 0);
INSERT INTO `booking_order` VALUES (24, 'ORD202601010004', 1, NULL, 3, '2026-01-02', '2026-01-03', 1, 1, 268.00, 268.00, 80.40, 348.40, 0.00, 'pending', '王建国', '13900000001', '110101199001011234', '', NULL, NULL, NULL, '2026-01-01 03:00:49', '2026-01-01 03:09:05', 0, 1);
INSERT INTO `booking_order` VALUES (25, 'ORD202601010005', 1, 1, 1, '2026-01-01', '2026-01-02', 1, 1, 198.00, 198.00, 39.60, 237.60, 0.00, 'checked_in', '王建国', '13900000001', '110101199001011234', '', '2026-01-01 03:15:14', NULL, 1, '2026-01-01 03:11:00', '2026-01-01 03:11:00', 0, 0);
INSERT INTO `booking_order` VALUES (26, 'ORD202601010006', 1, NULL, 1, '2026-01-01', '2026-01-02', 1, 1, 198.00, 198.00, 59.40, 59.40, 0.00, 'cancelled', '王建国', '13900000001', '110101199001011234', '', NULL, NULL, NULL, '2026-01-01 03:15:53', '2026-01-01 03:15:53', 0, 0);

-- ----------------------------
-- Table structure for cleaning_task
-- ----------------------------
DROP TABLE IF EXISTS `cleaning_task`;
CREATE TABLE `cleaning_task`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `room_id` bigint(20) NOT NULL COMMENT '房间ID',
  `room_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '房间号',
  `task_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'checkout' COMMENT '任务类型: checkout-退房清洁, daily-日常清洁, deep-深度清洁',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'pending' COMMENT '状态: pending-待分配, assigned-已分配, in_progress-进行中, completed-已完成',
  `assignee_id` bigint(20) NULL DEFAULT NULL COMMENT '分配给员工ID',
  `assignee_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分配给员工姓名',
  `assign_time` datetime NULL DEFAULT NULL COMMENT '分配时间',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `finish_time` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_room_id`(`room_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_assignee_id`(`assignee_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '清洁任务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cleaning_task
-- ----------------------------
INSERT INTO `cleaning_task` VALUES (1, 3, '103', 'checkout', 'completed', 5, '刘阿姨', '2024-12-27 12:30:00', '2024-12-27 13:00:00', '2024-12-27 14:00:00', NULL, '2024-12-27 12:00:00', '2025-12-30 03:28:24');
INSERT INTO `cleaning_task` VALUES (2, 5, '202', 'checkout', 'completed', 6, '陈阿姨', '2024-12-28 12:30:00', '2024-12-28 13:15:00', '2024-12-28 14:30:00', NULL, '2024-12-28 12:00:00', '2025-12-30 03:28:24');
INSERT INTO `cleaning_task` VALUES (3, 1, '101', 'daily', 'assigned', 5, '刘阿姨', '2026-01-01 01:51:25', NULL, NULL, NULL, '2024-12-30 08:00:00', '2025-12-30 03:28:24');
INSERT INTO `cleaning_task` VALUES (4, 2, '102', 'daily', 'assigned', 5, '刘阿姨', '2024-12-30 09:00:00', NULL, NULL, NULL, '2024-12-30 08:00:00', '2025-12-30 03:28:24');
INSERT INTO `cleaning_task` VALUES (5, 1, '101', '日常清洁', 'completed', NULL, NULL, NULL, '2026-01-01 01:41:43', '2026-01-01 01:41:45', NULL, '2026-01-01 01:23:13', '2026-01-01 01:23:13');
INSERT INTO `cleaning_task` VALUES (6, 1, '101', '日常清洁', 'completed', NULL, NULL, NULL, '2026-01-01 01:41:40', '2026-01-01 01:41:42', NULL, '2026-01-01 01:30:39', '2026-01-01 01:30:39');
INSERT INTO `cleaning_task` VALUES (7, 1, '101', '日常清洁', 'completed', NULL, NULL, NULL, '2026-01-01 01:41:36', '2026-01-01 01:41:39', NULL, '2026-01-01 01:35:28', '2026-01-01 01:35:28');
INSERT INTO `cleaning_task` VALUES (8, 1, '101', '日常清洁', 'completed', NULL, NULL, NULL, '2026-01-01 01:53:54', '2026-01-01 01:53:56', NULL, '2026-01-01 01:51:33', '2026-01-01 01:51:33');
INSERT INTO `cleaning_task` VALUES (9, 1, '101', '日常清洁', 'cancelled', NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-01 01:54:01', '2026-01-01 01:54:01');
INSERT INTO `cleaning_task` VALUES (10, 1, '101', '日常清洁', 'cancelled', NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-01 02:06:49', '2026-01-01 02:06:49');
INSERT INTO `cleaning_task` VALUES (11, 3, '103', '退房清洁', 'cancelled', NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-01 02:11:13', '2026-01-01 02:11:13');
INSERT INTO `cleaning_task` VALUES (12, 8, '302', '退房清洁', 'cancelled', NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-01 02:26:33', '2026-01-01 02:26:33');

-- ----------------------------
-- Table structure for inventory_item
-- ----------------------------
DROP TABLE IF EXISTS `inventory_item`;
CREATE TABLE `inventory_item`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '物品ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '物品名称',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '类别: toiletry-洗浴用品, beverage-饮料, food-食品, supplies-日用品, other-其他',
  `unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '单位',
  `quantity` int(11) NULL DEFAULT 0 COMMENT '当前库存',
  `min_stock` int(11) NULL DEFAULT 10 COMMENT '最低库存预警值',
  `price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '单价',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态: 0-停用 1-启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_quantity`(`quantity` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '库存物品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of inventory_item
-- ----------------------------
INSERT INTO `inventory_item` VALUES (1, '洗发水', 'toiletry', '瓶', 200, 50, 5.00, '便携装洗发水 30ml', 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);
INSERT INTO `inventory_item` VALUES (2, '沐浴露', 'toiletry', '瓶', 200, 50, 5.00, '便携装沐浴露 30ml', 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);
INSERT INTO `inventory_item` VALUES (3, '护发素', 'toiletry', '瓶', 150, 30, 4.00, '便携装护发素 20ml', 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);
INSERT INTO `inventory_item` VALUES (4, '牙刷', 'toiletry', '支', 300, 100, 2.00, '一次性软毛牙刷', 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);
INSERT INTO `inventory_item` VALUES (5, '牙膏', 'toiletry', '支', 300, 100, 3.00, '便携装牙膏', 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);
INSERT INTO `inventory_item` VALUES (6, '浴巾', 'toiletry', '条', 100, 20, 25.00, '纯棉浴巾', 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);
INSERT INTO `inventory_item` VALUES (7, '毛巾', 'toiletry', '条', 150, 30, 10.00, '纯棉毛巾', 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);
INSERT INTO `inventory_item` VALUES (8, '拖鞋', 'toiletry', '双', 200, 50, 5.00, '一次性拖鞋', 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);
INSERT INTO `inventory_item` VALUES (9, '矿泉水', 'beverage', '瓶', 500, 100, 3.00, '550ml矿泉水', 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);
INSERT INTO `inventory_item` VALUES (10, '可乐', 'beverage', '瓶', 99, 30, 5.00, '330ml罐装可乐', 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);
INSERT INTO `inventory_item` VALUES (11, '雪碧', 'beverage', '瓶', 100, 30, 5.00, '330ml罐装雪碧', 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);
INSERT INTO `inventory_item` VALUES (12, '橙汁', 'beverage', '瓶', 80, 20, 8.00, '300ml瓶装橙汁', 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);
INSERT INTO `inventory_item` VALUES (13, '方便面', 'food', '桶', 100, 30, 6.00, '桶装方便面', 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);
INSERT INTO `inventory_item` VALUES (14, '薯片', 'food', '包', 80, 20, 8.00, '膨化薯片', 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);
INSERT INTO `inventory_item` VALUES (15, '巧克力', 'food', '盒', 50, 15, 15.00, '盒装巧克力', 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);
INSERT INTO `inventory_item` VALUES (16, '纸巾', 'supplies', '包', 300, 80, 3.00, '抽纸', 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);
INSERT INTO `inventory_item` VALUES (17, '垃圾袋', 'supplies', '卷', 100, 30, 5.00, '加厚垃圾袋', 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);
INSERT INTO `inventory_item` VALUES (18, '洗衣液', 'supplies', '袋', 50, 15, 8.00, '小袋洗衣液', 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);

-- ----------------------------
-- Table structure for inventory_record
-- ----------------------------
DROP TABLE IF EXISTS `inventory_record`;
CREATE TABLE `inventory_record`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `item_id` bigint(20) NOT NULL COMMENT '物品ID',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '类型: in-入库, out-出库, consume-消耗',
  `quantity` int(11) NOT NULL COMMENT '变动数量',
  `before_quantity` int(11) NULL DEFAULT NULL COMMENT '变动前数量',
  `after_quantity` int(11) NULL DEFAULT NULL COMMENT '变动后数量',
  `room_id` bigint(20) NULL DEFAULT NULL COMMENT '关联房间ID',
  `order_id` bigint(20) NULL DEFAULT NULL COMMENT '关联订单ID',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `operator_id` bigint(20) NULL DEFAULT NULL COMMENT '操作员ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_item_id`(`item_id` ASC) USING BTREE,
  INDEX `idx_type`(`type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 36 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '库存变动记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of inventory_record
-- ----------------------------
INSERT INTO `inventory_record` VALUES (1, 1, 'in', 100, 100, 200, NULL, NULL, '月度采购入库', 1, '2024-12-01 09:00:00');
INSERT INTO `inventory_record` VALUES (2, 2, 'in', 100, 100, 200, NULL, NULL, '月度采购入库', 1, '2024-12-01 09:00:00');
INSERT INTO `inventory_record` VALUES (3, 4, 'in', 150, 150, 300, NULL, NULL, '月度采购入库', 1, '2024-12-01 09:05:00');
INSERT INTO `inventory_record` VALUES (4, 5, 'in', 150, 150, 300, NULL, NULL, '月度采购入库', 1, '2024-12-01 09:05:00');
INSERT INTO `inventory_record` VALUES (5, 9, 'in', 300, 200, 500, NULL, NULL, '饮料批量采购', 1, '2024-12-01 09:10:00');
INSERT INTO `inventory_record` VALUES (6, 10, 'in', 50, 50, 100, NULL, NULL, '饮料批量采购', 1, '2024-12-01 09:10:00');
INSERT INTO `inventory_record` VALUES (7, 11, 'in', 50, 50, 100, NULL, NULL, '饮料批量采购', 1, '2024-12-01 09:10:00');
INSERT INTO `inventory_record` VALUES (8, 16, 'in', 200, 100, 300, NULL, NULL, '日用品补货', 1, '2024-12-15 10:00:00');
INSERT INTO `inventory_record` VALUES (9, 1, 'consume', 2, 200, 198, 3, 1, '103房入住补充', 5, '2024-12-25 13:30:00');
INSERT INTO `inventory_record` VALUES (10, 2, 'consume', 2, 200, 198, 3, 1, '103房入住补充', 5, '2024-12-25 13:30:00');
INSERT INTO `inventory_record` VALUES (11, 4, 'consume', 2, 300, 298, 3, 1, '103房入住补充', 5, '2024-12-25 13:30:00');
INSERT INTO `inventory_record` VALUES (12, 5, 'consume', 2, 300, 298, 3, 1, '103房入住补充', 5, '2024-12-25 13:30:00');
INSERT INTO `inventory_record` VALUES (13, 8, 'consume', 2, 200, 198, 3, 1, '103房入住补充', 5, '2024-12-25 13:30:00');
INSERT INTO `inventory_record` VALUES (14, 9, 'consume', 2, 500, 498, 3, 1, '103房入住补充', 5, '2024-12-25 13:30:00');
INSERT INTO `inventory_record` VALUES (15, 16, 'consume', 1, 300, 299, 3, 1, '103房入住补充', 5, '2024-12-25 13:30:00');
INSERT INTO `inventory_record` VALUES (16, 1, 'consume', 2, 198, 196, 5, 2, '202房入住补充', 6, '2024-12-26 13:00:00');
INSERT INTO `inventory_record` VALUES (17, 2, 'consume', 2, 198, 196, 5, 2, '202房入住补充', 6, '2024-12-26 13:00:00');
INSERT INTO `inventory_record` VALUES (18, 4, 'consume', 2, 298, 296, 5, 2, '202房入住补充', 6, '2024-12-26 13:00:00');
INSERT INTO `inventory_record` VALUES (19, 5, 'consume', 2, 298, 296, 5, 2, '202房入住补充', 6, '2024-12-26 13:00:00');
INSERT INTO `inventory_record` VALUES (20, 8, 'consume', 2, 198, 196, 5, 2, '202房入住补充', 6, '2024-12-26 13:00:00');
INSERT INTO `inventory_record` VALUES (21, 9, 'consume', 2, 498, 496, 5, 2, '202房入住补充', 6, '2024-12-26 13:00:00');
INSERT INTO `inventory_record` VALUES (22, 1, 'consume', 3, 196, 193, 8, 3, '302房入住补充(家庭套房)', 5, '2024-12-28 13:30:00');
INSERT INTO `inventory_record` VALUES (23, 2, 'consume', 3, 196, 193, 8, 3, '302房入住补充(家庭套房)', 5, '2024-12-28 13:30:00');
INSERT INTO `inventory_record` VALUES (24, 4, 'consume', 4, 296, 292, 8, 3, '302房入住补充(家庭套房)', 5, '2024-12-28 13:30:00');
INSERT INTO `inventory_record` VALUES (25, 5, 'consume', 4, 296, 292, 8, 3, '302房入住补充(家庭套房)', 5, '2024-12-28 13:30:00');
INSERT INTO `inventory_record` VALUES (26, 6, 'consume', 3, 100, 97, 8, 3, '302房入住补充(家庭套房)', 5, '2024-12-28 13:30:00');
INSERT INTO `inventory_record` VALUES (27, 7, 'consume', 4, 150, 146, 8, 3, '302房入住补充(家庭套房)', 5, '2024-12-28 13:30:00');
INSERT INTO `inventory_record` VALUES (28, 8, 'consume', 4, 196, 192, 8, 3, '302房入住补充(家庭套房)', 5, '2024-12-28 13:30:00');
INSERT INTO `inventory_record` VALUES (29, 9, 'consume', 4, 496, 492, 8, 3, '302房入住补充(家庭套房)', 5, '2024-12-28 13:30:00');
INSERT INTO `inventory_record` VALUES (30, 9, 'out', 4, 492, 488, 3, 1, 'POS销售-矿泉水', 3, '2024-12-25 20:30:00');
INSERT INTO `inventory_record` VALUES (31, 10, 'out', 2, 100, 98, 3, 1, 'POS销售-可乐', 3, '2024-12-26 15:20:00');
INSERT INTO `inventory_record` VALUES (32, 13, 'out', 1, 100, 99, 5, 2, 'POS销售-方便面', 3, '2024-12-26 22:45:00');
INSERT INTO `inventory_record` VALUES (33, 9, 'out', 6, 488, 482, 8, 3, 'POS销售-矿泉水', 3, '2024-12-28 18:00:00');
INSERT INTO `inventory_record` VALUES (34, 15, 'out', 1, 50, 49, 8, 3, 'POS销售-巧克力', 4, '2024-12-29 10:30:00');
INSERT INTO `inventory_record` VALUES (35, 3, 'out', 5, 150, 145, NULL, NULL, '库存盘点损耗', 1, '2024-12-20 17:00:00');
INSERT INTO `inventory_record` VALUES (36, 17, 'out', 3, 100, 97, NULL, NULL, '库存盘点损耗', 1, '2024-12-20 17:00:00');
INSERT INTO `inventory_record` VALUES (37, 10, 'out', 1, 100, 99, 8, 3, 'POS消费扣减', 1, '2026-01-01 01:02:19');

-- ----------------------------
-- Table structure for payment_record
-- ----------------------------
DROP TABLE IF EXISTS `payment_record`;
CREATE TABLE `payment_record`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '支付ID',
  `payment_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '支付单号',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '订单编号',
  `guest_id` bigint(20) NULL DEFAULT NULL COMMENT '宾客ID',
  `amount` decimal(10, 2) NOT NULL COMMENT '金额',
  `payment_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '类型: deposit-保证金, room_fee-房费, extra-额外消费, refund-退款',
  `payment_method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '支付方式: cash-现金, wechat-微信, alipay-支付宝, card-银行卡',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'success' COMMENT '状态: pending-待支付, success-成功, failed-失败, refunded-已退款',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `operator_id` bigint(20) NULL DEFAULT NULL COMMENT '操作员ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `payment_no`(`payment_no` ASC) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_payment_type`(`payment_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 39 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '支付记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of payment_record
-- ----------------------------
INSERT INTO `payment_record` VALUES (1, 'PAY202412200001', 1, 'ORD202412250001', 1, 178.80, 'deposit', 'wechat', 'success', NULL, NULL, '2024-12-20 10:35:00', '2025-12-30 03:28:24');
INSERT INTO `payment_record` VALUES (2, 'PAY202412250001', 1, 'ORD202412250001', 1, 417.20, 'room_fee', 'wechat', 'success', NULL, 3, '2024-12-25 14:20:00', '2025-12-30 03:28:24');
INSERT INTO `payment_record` VALUES (3, 'PAY202412220001', 2, 'ORD202412260001', 2, 160.80, 'deposit', 'alipay', 'success', NULL, NULL, '2024-12-22 14:25:00', '2025-12-30 03:28:24');
INSERT INTO `payment_record` VALUES (4, 'PAY202412260001', 2, 'ORD202412260001', 2, 375.20, 'room_fee', 'alipay', 'success', NULL, 3, '2024-12-26 14:00:00', '2025-12-30 03:28:24');
INSERT INTO `payment_record` VALUES (5, 'PAY202412250002', 3, 'ORD202412280001', 3, 412.20, 'deposit', 'wechat', 'success', NULL, NULL, '2024-12-25 09:20:00', '2025-12-30 03:28:24');
INSERT INTO `payment_record` VALUES (6, 'PAY202412280001', 3, 'ORD202412280001', 3, 961.80, 'room_fee', 'cash', 'success', NULL, 3, '2024-12-28 14:30:00', '2025-12-30 03:28:24');
INSERT INTO `payment_record` VALUES (7, 'PAY202412280002', 4, 'ORD202412300001', 4, 331.20, 'deposit', 'wechat', 'success', NULL, NULL, '2024-12-28 16:50:00', '2025-12-30 03:28:24');
INSERT INTO `payment_record` VALUES (8, 'PAY202412290001', 5, 'ORD202412300002', 5, 59.40, 'deposit', 'alipay', 'success', NULL, NULL, '2024-12-29 11:05:00', '2025-12-30 03:28:24');
INSERT INTO `payment_record` VALUES (9, 'PAY202512300001', 6, 'ORD202512300001', NULL, 59.40, 'deposit', 'wechat', 'success', '支付保证金', 1, '2025-12-30 03:40:59', '2025-12-30 03:40:59');
INSERT INTO `payment_record` VALUES (10, 'PAY202512300002', 6, 'ORD202512300001', NULL, 59.40, 'deposit', 'wechat', 'success', '支付保证金', 1, '2025-12-30 03:41:01', '2025-12-30 03:41:01');
INSERT INTO `payment_record` VALUES (11, 'PAY202512300003', 6, 'ORD202512300001', NULL, 59.40, 'deposit', 'wechat', 'success', '支付保证金', 1, '2025-12-30 03:41:04', '2025-12-30 03:41:04');
INSERT INTO `payment_record` VALUES (12, 'PAY202512300004', 6, 'ORD202512300001', NULL, 59.40, 'deposit', 'wechat', 'success', '支付保证金', 1, '2025-12-30 03:41:08', '2025-12-30 03:41:08');
INSERT INTO `payment_record` VALUES (13, 'PAY202512300005', 7, 'ORD202512300002', NULL, 89.40, 'deposit', 'wechat', 'success', '支付保证金', 1, '2025-12-30 03:46:41', '2025-12-30 03:46:41');
INSERT INTO `payment_record` VALUES (14, 'PAY202512300006', 7, 'ORD202512300002', NULL, 89.40, 'deposit', 'wechat', 'success', '支付保证金', 1, '2025-12-30 03:46:48', '2025-12-30 03:46:48');
INSERT INTO `payment_record` VALUES (15, 'PAY202512300007', 7, 'ORD202512300002', NULL, 89.40, 'deposit', 'wechat', 'success', '支付保证金', 1, '2025-12-30 03:47:03', '2025-12-30 03:47:03');
INSERT INTO `payment_record` VALUES (16, 'PAY202512300008', 7, 'ORD202512300002', NULL, 89.40, 'deposit', 'wechat', 'success', '支付保证金', 1, '2025-12-30 03:47:07', '2025-12-30 03:47:07');
INSERT INTO `payment_record` VALUES (17, 'PAY202512300009', 10, 'ORD202512300005', NULL, 89.40, 'deposit', 'wechat', 'success', '支付保证金', 1, '2025-12-30 03:56:44', '2025-12-30 03:56:44');
INSERT INTO `payment_record` VALUES (18, 'PAY202512300010', 10, 'ORD202512300005', NULL, 89.40, 'deposit', 'wechat', 'success', '支付保证金', 1, '2025-12-30 03:56:52', '2025-12-30 03:56:52');
INSERT INTO `payment_record` VALUES (19, 'PAY202512300011', 10, 'ORD202512300005', NULL, 89.40, 'deposit', 'wechat', 'success', '支付保证金', 1, '2025-12-30 03:56:55', '2025-12-30 03:56:55');
INSERT INTO `payment_record` VALUES (20, 'PAY202512300012', 11, 'ORD202512300006', NULL, 89.40, 'deposit', 'wechat', 'success', '支付保证金', 1, '2025-12-30 03:59:21', '2025-12-30 03:59:21');
INSERT INTO `payment_record` VALUES (21, 'PAY202512300013', 12, 'ORD202512300007', NULL, 137.40, 'deposit', 'wechat', 'success', '支付保证金', 1, '2025-12-30 04:02:03', '2025-12-30 04:02:03');
INSERT INTO `payment_record` VALUES (22, 'PAY202512300014', 12, 'ORD202512300007', NULL, 320.60, 'balance', 'wechat', 'success', '补差价', 1, '2025-12-30 04:02:05', '2025-12-30 04:02:05');
INSERT INTO `payment_record` VALUES (23, 'PAY202512300015', 13, 'ORD202512300008', 1, 80.40, 'deposit', 'wechat', 'success', '支付保证金', 1, '2025-12-30 04:07:08', '2025-12-30 04:07:08');
INSERT INTO `payment_record` VALUES (24, 'PAY202512300016', 13, 'ORD202512300008', 1, 187.60, 'balance', 'wechat', 'success', '补差价', 1, '2025-12-30 04:07:10', '2025-12-30 04:07:10');
INSERT INTO `payment_record` VALUES (25, 'PAY202512300017', 14, 'ORD202512300009', 1, 59.40, 'deposit', 'wechat', 'success', '支付保证金', 1, '2025-12-30 04:20:02', '2025-12-30 04:20:02');
INSERT INTO `payment_record` VALUES (26, 'PAY202512300018', 14, 'ORD202512300009', 1, 138.60, 'balance', 'wechat', 'success', '补差价', 1, '2025-12-30 04:20:04', '2025-12-30 04:20:04');
INSERT INTO `payment_record` VALUES (27, 'PAY202512300019', 16, 'ORD202512300011', 1, 89.40, 'deposit', 'wechat', 'success', '', 1, '2025-12-30 14:29:29', '2025-12-30 14:29:29');
INSERT INTO `payment_record` VALUES (28, 'PAY202512300020', 16, 'ORD202512300011', 1, 298.00, 'room_fee', 'wechat', 'success', '', 1, '2025-12-30 14:29:34', '2025-12-30 14:29:34');
INSERT INTO `payment_record` VALUES (29, 'PAY202512300021', 17, 'ORD202512300012', 1, 89.40, 'deposit', 'wechat', 'success', '', 1, '2025-12-30 14:30:46', '2025-12-30 14:30:46');
INSERT INTO `payment_record` VALUES (30, 'PAY202512300022', 17, 'ORD202512300012', 1, 89.40, 'deposit', 'alipay', 'success', '', 1, '2025-12-30 14:30:49', '2025-12-30 14:30:49');
INSERT INTO `payment_record` VALUES (31, 'PAY202512300023', 17, 'ORD202512300012', 1, 298.00, 'room_fee', 'card', 'success', '', 1, '2025-12-30 14:30:52', '2025-12-30 14:30:52');
INSERT INTO `payment_record` VALUES (32, 'PAY202512300024', 18, 'ORD202512300013', 1, 80.40, 'deposit', 'wechat', 'success', '', 1, '2025-12-30 14:33:15', '2025-12-30 14:33:15');
INSERT INTO `payment_record` VALUES (33, 'PAY202512300025', 18, 'ORD202512300013', 1, 80.40, 'deposit', 'alipay', 'success', '', 1, '2025-12-30 14:33:17', '2025-12-30 14:33:17');
INSERT INTO `payment_record` VALUES (34, 'PAY202512300026', 19, 'ORD202512300014', 1, 89.40, 'deposit', 'alipay', 'success', '', 1, '2025-12-30 14:41:11', '2025-12-30 14:41:11');
INSERT INTO `payment_record` VALUES (35, 'PAY202512300027', 19, 'ORD202512300014', 1, 89.40, 'deposit', 'wechat', 'success', '', 1, '2025-12-30 14:41:14', '2025-12-30 14:41:14');
INSERT INTO `payment_record` VALUES (36, 'PAY202512300028', 19, 'ORD202512300014', 1, 298.00, 'room_fee', 'card', 'success', '', 1, '2025-12-30 14:41:17', '2025-12-30 14:41:17');
INSERT INTO `payment_record` VALUES (37, 'PAY202512300029', 20, 'ORD202512300015', 1, 80.40, 'deposit', 'alipay', 'success', '', 1, '2025-12-30 14:49:43', '2025-12-30 14:49:43');
INSERT INTO `payment_record` VALUES (38, 'PAY202512300030', 20, 'ORD202512300015', 1, 80.40, 'deposit', 'wechat', 'success', '', 1, '2025-12-30 14:49:45', '2025-12-30 14:49:45');
INSERT INTO `payment_record` VALUES (39, 'PAY202512300031', 20, 'ORD202512300015', 1, 268.00, 'room_fee', 'card', 'success', '', 1, '2025-12-30 14:49:50', '2025-12-30 14:49:50');
INSERT INTO `payment_record` VALUES (40, 'PAY202601010001', 21, 'ORD202601010001', 1, 89.40, 'deposit', 'wechat', 'success', '', 1, '2026-01-01 02:08:16', '2026-01-01 02:08:16');
INSERT INTO `payment_record` VALUES (41, 'PAY202601010002', 21, 'ORD202601010001', 1, 298.00, 'room_fee', 'wechat', 'success', '', 1, '2026-01-01 02:08:20', '2026-01-01 02:08:20');
INSERT INTO `payment_record` VALUES (42, 'PAY202601010003', 22, 'ORD202601010002', 1, 89.40, 'deposit', 'wechat', 'success', '支付保证金', 1, '2026-01-01 02:37:07', '2026-01-01 02:37:07');
INSERT INTO `payment_record` VALUES (43, 'PAY202601010004', 22, 'ORD202601010002', 1, 298.00, 'balance', 'wechat', 'success', '补差价', 1, '2026-01-01 02:37:09', '2026-01-01 02:37:09');
INSERT INTO `payment_record` VALUES (44, 'PAY202601010005', 23, 'ORD202601010003', 1, 59.40, 'deposit', 'wechat', 'success', '', 1, '2026-01-01 02:52:17', '2026-01-01 02:52:17');
INSERT INTO `payment_record` VALUES (45, 'PAY202601010006', 23, 'ORD202601010003', 1, 59.40, 'deposit', 'alipay', 'success', '', 1, '2026-01-01 02:52:21', '2026-01-01 02:52:21');
INSERT INTO `payment_record` VALUES (46, 'PAY202601010007', 23, 'ORD202601010003', 1, 198.00, 'room_fee', 'alipay', 'success', '', 1, '2026-01-01 02:52:26', '2026-01-01 02:52:26');
INSERT INTO `payment_record` VALUES (47, 'PAY202601010008', 24, 'ORD202601010004', 1, 80.40, 'deposit', 'wechat', 'success', '', 1, '2026-01-01 03:00:56', '2026-01-01 03:00:56');
INSERT INTO `payment_record` VALUES (48, 'PAY202601010009', 24, 'ORD202601010004', 1, 268.00, 'room_fee', 'alipay', 'success', '', 1, '2026-01-01 03:01:03', '2026-01-01 03:01:03');
INSERT INTO `payment_record` VALUES (49, 'PAY202601010010', 25, 'ORD202601010005', 1, 39.60, 'deposit', 'wechat', 'success', '', 1, '2026-01-01 03:11:05', '2026-01-01 03:11:05');
INSERT INTO `payment_record` VALUES (50, 'PAY202601010011', 25, 'ORD202601010005', 1, 198.00, 'room_fee', 'cash', 'success', '', 1, '2026-01-01 03:11:08', '2026-01-01 03:11:08');
INSERT INTO `payment_record` VALUES (51, 'PAY202601010012', 26, 'ORD202601010006', 1, 59.40, 'deposit', 'wechat', 'success', '', 1, '2026-01-01 03:15:56', '2026-01-01 03:15:56');

-- ----------------------------
-- Table structure for pos_record
-- ----------------------------
DROP TABLE IF EXISTS `pos_record`;
CREATE TABLE `pos_record`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `order_id` bigint(20) NULL DEFAULT NULL COMMENT '关联订单ID',
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '订单编号',
  `guest_id` bigint(20) NULL DEFAULT NULL COMMENT '宾客ID',
  `room_id` bigint(20) NULL DEFAULT NULL COMMENT '房间ID',
  `item_id` bigint(20) NULL DEFAULT NULL COMMENT '物品ID',
  `item_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '物品名称',
  `quantity` int(11) NULL DEFAULT 1 COMMENT '数量',
  `unit_price` decimal(10, 2) NOT NULL COMMENT '单价',
  `total_price` decimal(10, 2) NOT NULL COMMENT '总价',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '消费类别',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `operator_id` bigint(20) NULL DEFAULT NULL COMMENT '操作员ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_guest_id`(`guest_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'POS消费记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pos_record
-- ----------------------------
INSERT INTO `pos_record` VALUES (1, 1, 'ORD202412250001', 1, 3, 9, '矿泉水', 4, 3.00, 12.00, 'beverage', NULL, 3, '2024-12-25 20:30:00');
INSERT INTO `pos_record` VALUES (2, 1, 'ORD202412250001', 1, 3, 10, '可乐', 2, 5.00, 10.00, 'beverage', NULL, 3, '2024-12-26 15:20:00');
INSERT INTO `pos_record` VALUES (3, 2, 'ORD202412260001', 2, 5, 13, '方便面', 1, 6.00, 6.00, 'food', NULL, 3, '2024-12-26 22:45:00');
INSERT INTO `pos_record` VALUES (4, 3, 'ORD202412280001', 3, 8, 9, '矿泉水', 6, 3.00, 18.00, 'beverage', NULL, 3, '2024-12-28 18:00:00');
INSERT INTO `pos_record` VALUES (5, 3, 'ORD202412280001', 3, 8, 15, '巧克力', 1, 15.00, 15.00, 'food', NULL, 4, '2024-12-29 10:30:00');
INSERT INTO `pos_record` VALUES (6, 3, 'ORD202412280001', 3, 8, 10, '可乐', 1, 5.00, 5.00, 'beverage', '', 1, '2026-01-01 01:02:19');

-- ----------------------------
-- Table structure for room
-- ----------------------------
DROP TABLE IF EXISTS `room`;
CREATE TABLE `room`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '房间ID',
  `room_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '房间号',
  `room_type_id` bigint(20) NOT NULL COMMENT '房型ID',
  `floor` int(11) NULL DEFAULT NULL COMMENT '楼层',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'available' COMMENT '状态: available-可用, occupied-已入住, reserved-已预订, maintenance-维修中',
  `clean_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'clean' COMMENT '清洁状态: clean-已清洁, dirty-待清洁, cleaning-清洁中',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `room_number`(`room_number` ASC) USING BTREE,
  INDEX `idx_room_type`(`room_type_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '房间表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of room
-- ----------------------------
INSERT INTO `room` VALUES (1, '101', 1, 1, 'occupied', 'clean', '朝南，采光好', '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);
INSERT INTO `room` VALUES (2, '102', 1, 1, 'available', 'clean', '朝北，安静', '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);
INSERT INTO `room` VALUES (3, '103', 2, 1, 'available', 'clean', '朝南，景观房', '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);
INSERT INTO `room` VALUES (4, '201', 2, 2, 'available', 'clean', '朝南，阳台房', '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);
INSERT INTO `room` VALUES (5, '202', 3, 2, 'available', 'clean', '朝东，早晨阳光充足', '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);
INSERT INTO `room` VALUES (6, '203', 3, 2, 'available', 'clean', '朝西', '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);
INSERT INTO `room` VALUES (7, '301', 3, 3, 'occupied', 'clean', '朝南', '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);
INSERT INTO `room` VALUES (8, '302', 4, 3, 'available', 'clean', '家庭套房，空间大', '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);
INSERT INTO `room` VALUES (9, '303', 4, 3, 'available', 'clean', '家庭套房，带阳台', '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);
INSERT INTO `room` VALUES (10, '501', 5, 5, 'available', 'clean', '总统套房，顶层景观', '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);

-- ----------------------------
-- Table structure for room_type
-- ----------------------------
DROP TABLE IF EXISTS `room_type`;
CREATE TABLE `room_type`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '房型ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '房型名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '房型描述',
  `base_price` decimal(10, 2) NOT NULL COMMENT '基础价格',
  `weekend_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '周末价格',
  `holiday_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '节假日价格',
  `max_guests` int(11) NULL DEFAULT 2 COMMENT '最大入住人数',
  `bed_count` int(11) NULL DEFAULT 1 COMMENT '床位数',
  `bed_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '床型',
  `area` double NULL DEFAULT NULL COMMENT '房间面积(平方米)',
  `facilities` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '设施(JSON格式)',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '图片(JSON数组)',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态: 0-下架 1-上架',
  `sort_order` int(11) NULL DEFAULT 0 COMMENT '排序',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '房型表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of room_type
-- ----------------------------
INSERT INTO `room_type` VALUES (1, '标准单人间', '温馨舒适的单人房间，配备独立卫浴、空调、电视等设施，适合商务出行。', 198.00, 258.00, 358.00, 1, 1, '单人床 1.2m', 18, '[\"空调\",\"电视\",\"独立卫浴\",\"WiFi\",\"热水壶\",\"衣柜\"]', '[\"https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=800&h=600&fit=crop\",\"https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?w=800&h=600&fit=crop\",\"https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=800&h=600&fit=crop\"]', 1, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);
INSERT INTO `room_type` VALUES (2, '豪华大床房', '宽敞明亮的大床房，配备1.8米大床，适合情侣或夫妻入住。', 298.00, 368.00, 498.00, 2, 1, '大床 1.8m', 25, '[\"空调\",\"电视\",\"独立卫浴\",\"WiFi\",\"热水壶\",\"衣柜\",\"迷你冰箱\",\"保险箱\"]', '[\"https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=800&h=600&fit=crop\",\"https://images.unsplash.com/photo-1596394516093-501ba68a0ba6?w=800&h=600&fit=crop\",\"https://images.unsplash.com/photo-1590073242678-70ee3fc28e8e?w=800&h=600&fit=crop\"]', 1, 2, '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);
INSERT INTO `room_type` VALUES (3, '温馨双床房', '舒适的双床房间，配备两张1.2米单人床，适合朋友或同事入住。', 268.00, 328.00, 458.00, 2, 2, '单人床 1.2m x2', 28, '[\"空调\",\"电视\",\"独立卫浴\",\"WiFi\",\"热水壶\",\"衣柜\",\"书桌\"]', '[\"https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=800&h=600&fit=crop\",\"https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=800&h=600&fit=crop\",\"https://images.unsplash.com/photo-1615874959471-d37d6ec6f74c?w=800&h=600&fit=crop\"]', 1, 3, '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);
INSERT INTO `room_type` VALUES (4, '家庭套房', '宽敞的家庭套房，含独立客厅和卧室，适合家庭出游。', 458.00, 558.00, 698.00, 4, 2, '大床1.8m + 双床1.2m', 45, '[\"空调\",\"电视\",\"独立卫浴\",\"WiFi\",\"热水壶\",\"衣柜\",\"迷你冰箱\",\"保险箱\",\"沙发\",\"茶几\",\"微波炉\"]', '[\"https://images.unsplash.com/photo-1555854877-bab0e564b8d5?w=800&h=600&fit=crop\",\"https://images.unsplash.com/photo-1560185007-c5ca9d2c014d?w=800&h=600&fit=crop\",\"https://images.unsplash.com/photo-1551882547-ff40c63fe5fa?w=800&h=600&fit=crop\"]', 1, 4, '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);
INSERT INTO `room_type` VALUES (5, '总统套房', '顶级奢华套房，含独立客厅、卧室、书房，配备高端设施，尊享私密与舒适。', 888.00, 1088.00, 1388.00, 2, 1, '特大床 2.0m', 80, '[\"中央空调\",\"65寸智能电视\",\"豪华卫浴\",\"高速WiFi\",\"意式咖啡机\",\"衣帽间\",\"迷你吧台\",\"保险箱\",\"浴缸\",\"独立书房\",\"智能家居系统\"]', '[\"https://images.unsplash.com/photo-1590490360182-c33d57733427?w=800&h=600&fit=crop\",\"https://images.unsplash.com/photo-1571896349842-33c89424de2d?w=800&h=600&fit=crop\",\"https://images.unsplash.com/photo-1562436260-9570a88ec114?w=800&h=600&fit=crop\"]', 1, 5, '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置键',
  `config_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '配置值',
  `config_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '配置名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '配置描述',
  `config_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'string' COMMENT '配置类型: string, number, boolean, json',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `config_key`(`config_key` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES (1, 'deposit_rate', '0.3', '保证金比例', '预订时需支付的保证金比例，如0.3表示30%', 'number', '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_config` VALUES (2, 'cancel_free_hours', '24', '免费取消时限', '入住前多少小时可免费取消（小时）', 'number', '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_config` VALUES (3, 'cancel_penalty_rate', '0.5', '取消扣款比例', '超过免费取消时限后取消的扣款比例', 'number', '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_config` VALUES (4, 'check_in_time', '14:00', '入住时间', '标准入住时间', 'string', '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_config` VALUES (5, 'check_out_time', '12:00', '退房时间', '标准退房时间', 'string', '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_config` VALUES (6, 'late_checkout_fee', '50', '延迟退房费用', '每小时延迟退房费用', 'number', '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_config` VALUES (7, 'homestay_name', '悦居民宿', '民宿名称', '民宿品牌名称', 'string', '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_config` VALUES (8, 'homestay_address', '浙江省杭州市西湖区龙井路88号', '民宿地址', '民宿详细地址', 'string', '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_config` VALUES (9, 'homestay_phone', '0571-88888888', '联系电话', '民宿联系电话', 'string', '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_config` VALUES (10, 'homestay_description', '悦居民宿位于风景秀丽的西湖景区，环境优美，交通便利，是您休闲度假的理想选择。', '民宿介绍', '民宿简介描述', 'string', '2025-12-30 03:28:24', '2025-12-30 03:28:24');

-- ----------------------------
-- Table structure for sys_employee
-- ----------------------------
DROP TABLE IF EXISTS `sys_employee`;
CREATE TABLE `sys_employee`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '员工ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码(明文)',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'staff' COMMENT '角色: owner-经营者, receptionist-前台, housekeeper-房务',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '删除标记: 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  INDEX `idx_username`(`username` ASC) USING BTREE,
  INDEX `idx_role`(`role` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '员工表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_employee
-- ----------------------------
INSERT INTO `sys_employee` VALUES (1, 'admin', '123456', '系统管理员', '13800000001', 'admin@homestay.com', NULL, 'owner', 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);
INSERT INTO `sys_employee` VALUES (2, 'owner', '123456', '张老板', '13800000002', 'owner@homestay.com', NULL, 'owner', 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);
INSERT INTO `sys_employee` VALUES (3, 'reception1', '123456', '李小红', '13800000003', 'lixiaohong@homestay.com', NULL, 'receptionist', 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);
INSERT INTO `sys_employee` VALUES (4, 'reception2', '123456', '王小明', '13800000004', 'wangxiaoming@homestay.com', NULL, 'receptionist', 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);
INSERT INTO `sys_employee` VALUES (5, 'housekeeper1', '123456', '刘阿姨', '13800000005', 'liuayi@homestay.com', NULL, 'housekeeper', 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);
INSERT INTO `sys_employee` VALUES (6, 'housekeeper2', '123456', '陈阿姨', '13800000006', 'chenayi@homestay.com', NULL, 'housekeeper', 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);

-- ----------------------------
-- Table structure for sys_employee_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_employee_role`;
CREATE TABLE `sys_employee_role`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `employee_id` bigint(20) NOT NULL COMMENT '员工ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_employee_role`(`employee_id` ASC, `role_id` ASC) USING BTREE,
  INDEX `idx_employee_id`(`employee_id` ASC) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '员工角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_employee_role
-- ----------------------------
INSERT INTO `sys_employee_role` VALUES (1, 1, 1, '2025-12-30 03:28:24');
INSERT INTO `sys_employee_role` VALUES (2, 2, 1, '2025-12-30 03:28:24');
INSERT INTO `sys_employee_role` VALUES (3, 3, 2, '2025-12-30 03:28:24');
INSERT INTO `sys_employee_role` VALUES (4, 4, 2, '2025-12-30 03:28:24');
INSERT INTO `sys_employee_role` VALUES (5, 5, 3, '2025-12-30 03:28:24');
INSERT INTO `sys_employee_role` VALUES (6, 6, 3, '2025-12-30 03:28:24');

-- ----------------------------
-- Table structure for sys_guest
-- ----------------------------
DROP TABLE IF EXISTS `sys_guest`;
CREATE TABLE `sys_guest`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '宾客ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码(明文)',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '昵称',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像',
  `id_card` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '身份证号',
  `gender` tinyint(4) NULL DEFAULT 0 COMMENT '性别: 0-未知 1-男 2-女',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  INDEX `idx_username`(`username` ASC) USING BTREE,
  INDEX `idx_phone`(`phone` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '宾客表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_guest
-- ----------------------------
INSERT INTO `sys_guest` VALUES (1, 'guest1', '123456', '旅行者小王', '王建国', '13900000001', 'wangjianguo@qq.com', NULL, '110101199001011234', 1, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);
INSERT INTO `sys_guest` VALUES (2, 'guest2', '123456', '快乐出游', '李梅', '13900000002', 'limei@qq.com', NULL, '110101199202022345', 2, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);
INSERT INTO `sys_guest` VALUES (3, 'guest3', '123456', '商旅达人', '张伟', '13900000003', 'zhangwei@qq.com', NULL, '110101198803033456', 1, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);
INSERT INTO `sys_guest` VALUES (4, 'guest4', '123456', '度假小公主', '陈静', '13900000004', 'chenjing@qq.com', NULL, '110101199504044567', 2, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);
INSERT INTO `sys_guest` VALUES (5, 'guest5', '123456', '背包客', '刘洋', '13900000005', 'liuyang@qq.com', NULL, '110101199205055678', 1, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);

-- ----------------------------
-- Table structure for sys_operation_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_operation_log`;
CREATE TABLE `sys_operation_log`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户名',
  `user_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户类型: employee, guest',
  `module` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '模块',
  `operation` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作',
  `method` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '方法',
  `params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '参数',
  `ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `execution_time` bigint(20) NULL DEFAULT NULL COMMENT '执行时长(ms)',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态: 0-失败 1-成功',
  `error_msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '错误信息',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_module`(`module` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_operation_log
-- ----------------------------
INSERT INTO `sys_operation_log` VALUES (1, 1, 'admin', 'employee', '系统管理', '员工登录', 'POST /auth/login', NULL, '127.0.0.1', 125, 1, NULL, '2024-12-30 08:00:00');
INSERT INTO `sys_operation_log` VALUES (2, 3, 'reception1', 'employee', '订单管理', '办理入住', 'POST /order/checkin', NULL, '192.168.1.100', 230, 1, NULL, '2024-12-28 14:30:00');
INSERT INTO `sys_operation_log` VALUES (3, 5, 'housekeeper1', 'employee', '清洁管理', '完成清洁任务', 'PUT /cleaning/complete', NULL, '192.168.1.101', 85, 1, NULL, '2024-12-27 14:00:00');
INSERT INTO `sys_operation_log` VALUES (4, 1, 'admin', 'employee', '订单管理', '确认订单', 'OrderController.confirmOrder', NULL, '0:0:0:0:0:0:0:1', 26, 1, NULL, '2026-01-01 02:13:57');
INSERT INTO `sys_operation_log` VALUES (5, 1, 'admin', 'employee', '订单管理', '确认订单 [ID:18]', 'OrderController.confirmOrder', NULL, '0:0:0:0:0:0:0:1', 32, 1, NULL, '2026-01-01 02:19:25');
INSERT INTO `sys_operation_log` VALUES (6, 1, 'admin', 'employee', '订单管理', '办理退房 [ID:3]', 'OrderController.checkOut', NULL, '0:0:0:0:0:0:0:1', 16, 1, NULL, '2026-01-01 02:26:33');
INSERT INTO `sys_operation_log` VALUES (7, 1, 'admin', 'employee', '房间管理', '更新房间清洁状态 [ID:3]', 'RoomController.updateCleanStatus', NULL, '0:0:0:0:0:0:0:1', 17, 1, NULL, '2026-01-01 02:26:44');
INSERT INTO `sys_operation_log` VALUES (8, 1, 'admin', 'employee', '房间管理', '更新房间清洁状态 [ID:8]', 'RoomController.updateCleanStatus', NULL, '0:0:0:0:0:0:0:1', 13, 1, NULL, '2026-01-01 02:27:04');
INSERT INTO `sys_operation_log` VALUES (9, 1, 'admin', 'employee', '订单管理', '创建订单 [宾客:杰]', 'OrderController.createOrder', NULL, '0:0:0:0:0:0:0:1', 36, 1, NULL, '2026-01-01 02:37:02');
INSERT INTO `sys_operation_log` VALUES (10, 1, 'admin', 'employee', '其他', '创建支付记录', 'PaymentController.createPayment', NULL, '0:0:0:0:0:0:0:1', 31, 1, NULL, '2026-01-01 02:37:07');
INSERT INTO `sys_operation_log` VALUES (11, 1, 'admin', 'employee', '其他', '创建支付记录', 'PaymentController.createPayment', NULL, '0:0:0:0:0:0:0:1', 22, 1, NULL, '2026-01-01 02:37:09');
INSERT INTO `sys_operation_log` VALUES (12, 1, 'admin', 'employee', '订单管理', '更新订单 [ID:22]', 'OrderController.updateOrder', NULL, '0:0:0:0:0:0:0:1', 8, 1, NULL, '2026-01-01 02:37:14');
INSERT INTO `sys_operation_log` VALUES (13, 1, 'guest1', 'guest', '订单管理', '创建订单 [宾客:王建国]', 'OrderController.createOrder', NULL, '0:0:0:0:0:0:0:1', 44, 1, NULL, '2026-01-01 02:52:14');
INSERT INTO `sys_operation_log` VALUES (14, 1, 'guest1', 'guest', '其他', '创建支付记录', 'PaymentController.createPayment', NULL, '0:0:0:0:0:0:0:1', 33, 1, NULL, '2026-01-01 02:52:17');
INSERT INTO `sys_operation_log` VALUES (15, 1, 'guest1', 'guest', '其他', '创建支付记录', 'PaymentController.createPayment', NULL, '0:0:0:0:0:0:0:1', 23, 1, NULL, '2026-01-01 02:52:21');
INSERT INTO `sys_operation_log` VALUES (16, 1, 'guest1', 'guest', '其他', '创建支付记录', 'PaymentController.createPayment', NULL, '0:0:0:0:0:0:0:1', 15, 1, NULL, '2026-01-01 02:52:26');
INSERT INTO `sys_operation_log` VALUES (17, 1, 'guest1', 'guest', '订单管理', '创建订单 [宾客:王建国]', 'OrderController.createOrder', NULL, '0:0:0:0:0:0:0:1', 51, 1, NULL, '2026-01-01 03:00:49');
INSERT INTO `sys_operation_log` VALUES (18, 1, 'guest1', 'guest', '其他', '创建支付记录', 'PaymentController.createPayment', NULL, '0:0:0:0:0:0:0:1', 37, 1, NULL, '2026-01-01 03:00:56');
INSERT INTO `sys_operation_log` VALUES (19, 1, 'guest1', 'guest', '其他', '创建支付记录', 'PaymentController.createPayment', NULL, '0:0:0:0:0:0:0:1', 25, 1, NULL, '2026-01-01 03:01:03');
INSERT INTO `sys_operation_log` VALUES (20, NULL, NULL, NULL, '认证管理', '宾客登录 [用户名:guest1]', 'AuthController.guestLogin', NULL, '0:0:0:0:0:0:0:1', 26, 1, NULL, '2026-01-01 03:06:38');
INSERT INTO `sys_operation_log` VALUES (21, NULL, NULL, NULL, '认证管理', '宾客登录 [用户名:guest1]', 'AuthController.guestLogin', NULL, '0:0:0:0:0:0:0:1', 3, 1, NULL, '2026-01-01 03:08:39');
INSERT INTO `sys_operation_log` VALUES (22, 1, 'guest1', 'guest', '订单管理', '更新订单 [ID:24]', 'OrderController.updateOrder', NULL, '0:0:0:0:0:0:0:1', 18, 1, NULL, '2026-01-01 03:09:05');
INSERT INTO `sys_operation_log` VALUES (23, 1, 'admin', 'employee', '系统管理', '批量更新配置', 'SystemConfigController.batchUpdateConfig', NULL, '0:0:0:0:0:0:0:1', 52, 1, NULL, '2026-01-01 03:10:41');
INSERT INTO `sys_operation_log` VALUES (24, 1, 'guest1', 'guest', '订单管理', '创建订单 [宾客:王建国]', 'OrderController.createOrder', NULL, '0:0:0:0:0:0:0:1', 44, 1, NULL, '2026-01-01 03:11:00');
INSERT INTO `sys_operation_log` VALUES (25, 1, 'guest1', 'guest', '其他', '创建支付记录', 'PaymentController.createPayment', NULL, '0:0:0:0:0:0:0:1', 21, 1, NULL, '2026-01-01 03:11:05');
INSERT INTO `sys_operation_log` VALUES (26, 1, 'guest1', 'guest', '其他', '创建支付记录', 'PaymentController.createPayment', NULL, '0:0:0:0:0:0:0:1', 18, 1, NULL, '2026-01-01 03:11:08');
INSERT INTO `sys_operation_log` VALUES (27, 1, 'admin', 'employee', '系统管理', '批量更新配置', 'SystemConfigController.batchUpdateConfig', NULL, '0:0:0:0:0:0:0:1', 36, 1, NULL, '2026-01-01 03:11:15');
INSERT INTO `sys_operation_log` VALUES (28, 1, 'admin', 'employee', '员工管理', '修改密码', 'EmployeeController.updatePassword', NULL, '0:0:0:0:0:0:0:1', 8, 1, NULL, '2026-01-01 03:12:02');
INSERT INTO `sys_operation_log` VALUES (29, 1, 'admin', 'employee', '员工管理', '修改密码', 'EmployeeController.updatePassword', NULL, '0:0:0:0:0:0:0:1', 16, 1, NULL, '2026-01-01 03:12:32');
INSERT INTO `sys_operation_log` VALUES (30, 1, 'guest1', 'guest', '宾客管理', '修改密码', 'GuestController.updatePassword', NULL, '0:0:0:0:0:0:0:1', 13, 1, NULL, '2026-01-01 03:12:57');
INSERT INTO `sys_operation_log` VALUES (31, NULL, NULL, NULL, '认证管理', '宾客登录 [用户名:guest1]', 'AuthController.guestLogin', NULL, '0:0:0:0:0:0:0:1', 3, 1, NULL, '2026-01-01 03:13:02');
INSERT INTO `sys_operation_log` VALUES (32, 1, 'guest1', 'guest', '宾客管理', '修改密码', 'GuestController.updatePassword', NULL, '0:0:0:0:0:0:0:1', 11, 1, NULL, '2026-01-01 03:13:26');
INSERT INTO `sys_operation_log` VALUES (33, NULL, NULL, NULL, '认证管理', '宾客登录 [用户名:guest1]', 'AuthController.guestLogin', NULL, '0:0:0:0:0:0:0:1', 3, 1, NULL, '2026-01-01 03:13:29');
INSERT INTO `sys_operation_log` VALUES (34, NULL, NULL, NULL, '认证管理', '宾客登录 [用户名:guest1]', 'AuthController.guestLogin', NULL, '0:0:0:0:0:0:0:1', 4, 1, NULL, '2026-01-01 03:14:29');
INSERT INTO `sys_operation_log` VALUES (35, 1, 'admin', 'employee', '订单管理', '确认订单 [ID:25]', 'OrderController.confirmOrder', NULL, '0:0:0:0:0:0:0:1', 13, 1, NULL, '2026-01-01 03:15:09');
INSERT INTO `sys_operation_log` VALUES (36, 1, 'admin', 'employee', '订单管理', '办理入住 [ID:25, 房间ID:1]', 'OrderController.checkIn', NULL, '0:0:0:0:0:0:0:1', 13, 1, NULL, '2026-01-01 03:15:14');
INSERT INTO `sys_operation_log` VALUES (37, NULL, NULL, NULL, '认证管理', '员工登录 [用户名:admin]', 'AuthController.employeeLogin', NULL, '0:0:0:0:0:0:0:1', 4, 1, NULL, '2026-01-01 03:15:41');
INSERT INTO `sys_operation_log` VALUES (38, 1, 'guest1', 'guest', '订单管理', '创建订单 [宾客:王建国]', 'OrderController.createOrder', NULL, '0:0:0:0:0:0:0:1', 9, 1, NULL, '2026-01-01 03:15:53');
INSERT INTO `sys_operation_log` VALUES (39, 1, 'guest1', 'guest', '其他', '创建支付记录', 'PaymentController.createPayment', NULL, '0:0:0:0:0:0:0:1', 14, 1, NULL, '2026-01-01 03:15:56');
INSERT INTO `sys_operation_log` VALUES (40, 1, 'guest1', 'guest', '订单管理', '取消订单 [ID:26]', 'OrderController.cancelOrder', NULL, '0:0:0:0:0:0:0:1', 6, 1, NULL, '2026-01-01 03:16:05');

-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `permission_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限编码',
  `permission_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限名称',
  `module` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '所属模块',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '权限描述',
  `parent_id` bigint(20) NULL DEFAULT 0 COMMENT '父权限ID',
  `sort_order` int(11) NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `permission_code`(`permission_code` ASC) USING BTREE,
  INDEX `idx_module`(`module` ASC) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 90 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_permission
-- ----------------------------
INSERT INTO `sys_permission` VALUES (1, 'employee', '员工管理', 'employee', '员工管理模块', 0, 1, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (2, 'employee:list', '员工列表', 'employee', '查看员工列表', 1, 1, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (3, 'employee:create', '新增员工', 'employee', '创建新员工账号', 1, 2, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (4, 'employee:update', '编辑员工', 'employee', '修改员工信息', 1, 3, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (5, 'employee:delete', '删除员工', 'employee', '删除员工账号', 1, 4, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (6, 'employee:role:assign', '分配角色', 'employee', '为员工分配角色(RBAC)', 1, 5, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (7, 'room_manage', '房间管理', 'room_manage', '房间与房型管理模块', 0, 2, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (8, 'room_manage:type:list', '房型列表', 'room_manage', '查看房型列表', 7, 1, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (9, 'room_manage:type:create', '新增房型', 'room_manage', '创建新房型', 7, 2, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (10, 'room_manage:type:update', '编辑房型', 'room_manage', '修改房型信息', 7, 3, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (11, 'room_manage:type:delete', '删除房型', 'room_manage', '删除房型', 7, 4, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (12, 'room_manage:room:list', '房间列表', 'room_manage', '查看房间列表', 7, 5, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (13, 'room_manage:room:create', '新增房间', 'room_manage', '创建新房间', 7, 6, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (14, 'room_manage:room:update', '编辑房间', 'room_manage', '修改房间信息', 7, 7, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (15, 'room_manage:room:delete', '删除房间', 'room_manage', '删除房间', 7, 8, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (16, 'room_manage:price:set', '价格设置', 'room_manage', '设置房间价格及动态调价策略', 7, 9, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (17, 'room_manage:facility:set', '设施设置', 'room_manage', '设置房间设施', 7, 10, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (18, 'report', '经营分析', 'report', '经营分析报表模块', 0, 3, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (19, 'report:revenue', '营收报表', 'report', '查看营收统计报表', 18, 1, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (20, 'report:cost', '成本报表', 'report', '查看成本统计报表', 18, 2, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (21, 'report:profit', '利润报表', 'report', '查看利润分析报表', 18, 3, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (22, 'report:occupancy', '入住率报表', 'report', '查看入住率统计', 18, 4, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (23, 'report:export', '数据导出', 'report', '导出报表数据', 18, 5, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (24, 'system', '系统设置', 'system', '系统配置模块', 0, 4, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (25, 'system:config:deposit', '保证金规则', 'system', '配置保证金比例等规则', 24, 1, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (26, 'system:config:cancel', '取消政策', 'system', '配置订单取消政策', 24, 2, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (27, 'system:config:checkin', '入退房时间', 'system', '配置入住退房时间', 24, 3, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (28, 'system:config:basic', '基础信息', 'system', '配置民宿基础信息', 24, 4, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (29, 'audit', '权限审计', 'audit', '审计日志模块', 0, 5, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (30, 'audit:log:view', '操作日志', 'audit', '查看系统操作日志', 29, 1, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (31, 'audit:permission:view', '权限变更记录', 'audit', '查看权限变更记录', 29, 2, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (32, 'inventory', '库存管理', 'inventory', '库存物资管理模块', 0, 6, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (33, 'inventory:item:list', '物品列表', 'inventory', '查看库存物品列表', 32, 1, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (34, 'inventory:item:create', '新增物品', 'inventory', '新增库存物品', 32, 2, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (35, 'inventory:item:update', '编辑物品', 'inventory', '修改库存物品信息', 32, 3, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (36, 'inventory:item:delete', '删除物品', 'inventory', '删除库存物品', 32, 4, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (37, 'inventory:stock:in', '入库操作', 'inventory', '物品入库', 32, 5, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (38, 'inventory:stock:out', '出库操作', 'inventory', '物品出库', 32, 6, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (39, 'inventory:stock:consume', '消耗记录', 'inventory', '记录物品消耗', 32, 7, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (40, 'inventory:alert:view', '库存预警', 'inventory', '查看低库存预警', 32, 8, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (41, 'inventory:record:view', '变动记录', 'inventory', '查看库存变动记录', 32, 9, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (42, 'checkin', '入住办理', 'checkin', '入住办理模块', 0, 7, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (43, 'checkin:with_booking', '预订入住', 'checkin', '为有预订的宾客办理入住', 42, 1, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (44, 'checkin:walk_in', '散客入住', 'checkin', '为无预订的散客办理入住', 42, 2, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (45, 'checkin:room:assign', '分配房间', 'checkin', '为宾客分配房间', 42, 3, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (46, 'checkout', '结账退房', 'checkout', '结账退房模块', 0, 8, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (47, 'checkout:bill:create', '生成账单', 'checkout', '生成退房账单', 46, 1, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (48, 'checkout:deposit:deduct', '押金抵扣', 'checkout', '处理押金抵扣', 46, 2, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (49, 'checkout:refund', '退款处理', 'checkout', '处理退款', 46, 3, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (50, 'checkout:complete', '完成退房', 'checkout', '完成退房流程', 46, 4, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (51, 'order', '订单管理', 'order', '订单管理模块', 0, 9, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (52, 'order:list', '订单列表', 'order', '查看订单列表', 51, 1, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (53, 'order:create', '创建订单', 'order', '创建新订单', 51, 2, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (54, 'order:update', '修改订单', 'order', '修改订单信息', 51, 3, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (55, 'order:cancel', '取消订单', 'order', '取消订单', 51, 4, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (56, 'order:detail', '订单详情', 'order', '查看订单详情', 51, 5, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (57, 'pos', 'POS消费', 'pos', 'POS消费录入模块', 0, 10, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (58, 'pos:record:create', '消费录入', 'pos', '录入宾客消费', 57, 1, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (59, 'pos:record:list', '消费记录', 'pos', '查看消费记录', 57, 2, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (60, 'pos:inventory:consume', '物品消耗', 'pos', '记录饮料食品等消耗', 57, 3, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (61, 'room_status', '房态管理', 'room_status', '房态查看与更新模块', 0, 11, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (62, 'room_status:view', '房态查看', 'room_status', '实时查看房间状态', 61, 1, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (63, 'room_status:update', '房态更新', 'room_status', '更新房间状态(FR-04)', 61, 2, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (64, 'notification', '通知管理', 'notification', '通知提醒模块', 0, 12, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (65, 'notification:view', '通知查看', 'notification', '查看通知消息', 64, 1, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (66, 'notification:order', '订单提醒', 'notification', '接收新订单提醒', 64, 2, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (67, 'cleaning', '清洁管理', 'cleaning', '清洁任务管理模块', 0, 13, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (68, 'cleaning:task:list', '任务列表', 'cleaning', '查看待清洁房间列表', 67, 1, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (69, 'cleaning:task:receive', '接收任务', 'cleaning', '接收清洁任务', 67, 2, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (70, 'cleaning:task:complete', '完成任务', 'cleaning', '标记清洁任务完成', 67, 3, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (71, 'cleaning:supply:consume', '物资扣除', 'cleaning', '记录清洁时物资消耗(FR-09)', 67, 4, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (72, 'guest_room', '房间查询', 'guest', '宾客房间查询模块', 0, 14, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (73, 'guest_room:search', '搜索房间', 'guest', '按日期人数偏好查询可用房间', 72, 1, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (74, 'guest_room:detail', '房间详情', 'guest', '查看房型价格设施照片等', 72, 2, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (75, 'guest_booking', '在线预订', 'guest', '宾客在线预订模块', 0, 15, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (76, 'guest_booking:create', '创建预订', 'guest', '选择房型填写信息', 75, 1, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (77, 'guest_booking:deposit:pay', '支付保证金', 'guest', '支付预订保证金', 75, 2, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (78, 'guest_order', '我的订单', 'guest', '宾客订单管理模块', 0, 16, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (79, 'guest_order:list', '订单列表', 'guest', '查看我的订单', 78, 1, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (80, 'guest_order:detail', '订单详情', 'guest', '查看订单详情', 78, 2, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (81, 'guest_order:cancel', '取消订单', 'guest', '取消订单(按保证金规则)', 78, 3, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (82, 'guest_order:modify', '修改订单', 'guest', '改订(补差价/退款)', 78, 4, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (83, 'guest_payment', '在线支付', 'guest', '宾客支付模块', 0, 17, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (84, 'guest_payment:deposit', '支付保证金', 'guest', '支付保证金', 83, 1, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (85, 'guest_payment:extra', '支付差价', 'guest', '补差价支付', 83, 2, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (86, 'guest_payment:refund', '接收退款', 'guest', '接收退款', 83, 3, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (87, 'guest_profile', '个人信息', 'guest', '宾客个人信息管理模块', 0, 18, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (88, 'guest_profile:view', '查看信息', 'guest', '查看个人信息', 87, 1, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (89, 'guest_profile:update', '修改信息', 'guest', '修改个人信息', 87, 2, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');
INSERT INTO `sys_permission` VALUES (90, 'guest_profile:history', '历史订单', 'guest', '查看历史订单', 87, 3, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24');

-- ----------------------------
-- Table structure for sys_permission_audit
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission_audit`;
CREATE TABLE `sys_permission_audit`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `operator_id` bigint(20) NULL DEFAULT NULL COMMENT '操作人ID',
  `operator_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作人姓名',
  `target_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '目标类型: role-角色, permission-权限, employee_role-员工角色',
  `target_id` bigint(20) NULL DEFAULT NULL COMMENT '目标ID',
  `action` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作: create-创建, update-修改, delete-删除, grant-授权, revoke-撤销',
  `before_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '变更前值(JSON)',
  `after_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '变更后值(JSON)',
  `ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_operator_id`(`operator_id` ASC) USING BTREE,
  INDEX `idx_target_type`(`target_type` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '权限变更记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_permission_audit
-- ----------------------------

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色编码',
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '角色描述',
  `role_level` int(11) NULL DEFAULT 0 COMMENT '角色级别: 数字越大权限越高',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `role_code`(`role_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, 'owner', '民宿经营者', '最高权限，可管理所有功能', 100, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);
INSERT INTO `sys_role` VALUES (2, 'receptionist', '前台员工', '中等权限，负责入住、退房、订单管理', 50, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);
INSERT INTO `sys_role` VALUES (3, 'housekeeper', '房务员工', '基础权限，负责清洁任务和房态更新', 10, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);
INSERT INTO `sys_role` VALUES (4, 'guest', '宾客', '宾客端权限，可预订房间和管理个人订单', 1, 1, '2025-12-30 03:28:24', '2025-12-30 03:28:24', 0);

-- ----------------------------
-- Table structure for sys_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `permission_id` bigint(20) NOT NULL COMMENT '权限ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_permission`(`role_id` ASC, `permission_id` ASC) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE,
  INDEX `idx_permission_id`(`permission_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 192 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色权限关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_permission
-- ----------------------------
INSERT INTO `sys_role_permission` VALUES (1, 1, 1, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (2, 1, 2, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (3, 1, 3, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (4, 1, 4, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (5, 1, 5, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (6, 1, 6, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (7, 1, 7, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (8, 1, 8, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (9, 1, 9, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (10, 1, 10, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (11, 1, 11, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (12, 1, 12, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (13, 1, 13, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (14, 1, 14, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (15, 1, 15, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (16, 1, 16, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (17, 1, 17, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (18, 1, 18, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (19, 1, 19, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (20, 1, 20, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (21, 1, 21, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (22, 1, 22, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (23, 1, 23, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (24, 1, 24, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (25, 1, 25, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (26, 1, 26, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (27, 1, 27, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (28, 1, 28, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (29, 1, 29, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (30, 1, 30, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (31, 1, 31, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (32, 1, 32, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (33, 1, 33, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (34, 1, 34, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (35, 1, 35, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (36, 1, 36, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (37, 1, 37, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (38, 1, 38, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (39, 1, 39, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (40, 1, 40, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (41, 1, 41, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (42, 1, 42, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (43, 1, 43, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (44, 1, 44, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (45, 1, 45, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (46, 1, 46, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (47, 1, 47, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (48, 1, 48, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (49, 1, 49, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (50, 1, 50, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (51, 1, 51, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (52, 1, 52, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (53, 1, 53, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (54, 1, 54, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (55, 1, 55, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (56, 1, 56, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (57, 1, 57, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (58, 1, 58, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (59, 1, 59, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (60, 1, 60, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (61, 1, 61, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (62, 1, 62, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (63, 1, 63, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (64, 1, 64, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (65, 1, 65, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (66, 1, 66, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (67, 1, 67, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (68, 1, 68, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (69, 1, 69, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (70, 1, 70, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (71, 1, 71, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (128, 2, 42, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (129, 2, 45, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (130, 2, 44, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (131, 2, 43, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (132, 2, 46, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (133, 2, 47, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (134, 2, 50, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (135, 2, 48, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (136, 2, 49, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (137, 2, 32, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (138, 2, 40, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (139, 2, 33, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (140, 2, 64, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (141, 2, 66, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (142, 2, 65, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (143, 2, 51, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (144, 2, 55, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (145, 2, 53, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (146, 2, 56, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (147, 2, 52, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (148, 2, 54, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (149, 2, 57, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (150, 2, 60, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (151, 2, 58, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (152, 2, 59, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (153, 2, 61, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (154, 2, 62, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (159, 3, 67, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (160, 3, 71, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (161, 3, 70, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (162, 3, 68, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (163, 3, 69, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (164, 3, 32, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (165, 3, 33, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (166, 3, 39, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (167, 3, 61, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (168, 3, 63, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (169, 3, 62, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (174, 4, 75, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (175, 4, 76, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (176, 4, 77, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (177, 4, 78, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (178, 4, 81, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (179, 4, 80, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (180, 4, 79, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (181, 4, 82, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (182, 4, 83, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (183, 4, 84, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (184, 4, 85, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (185, 4, 86, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (186, 4, 87, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (187, 4, 90, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (188, 4, 89, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (189, 4, 88, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (190, 4, 72, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (191, 4, 74, '2025-12-30 03:28:24');
INSERT INTO `sys_role_permission` VALUES (192, 4, 73, '2025-12-30 03:28:24');

SET FOREIGN_KEY_CHECKS = 1;
