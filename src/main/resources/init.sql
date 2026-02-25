CREATE TABLE `bill_item`
(
    `id`       int NOT NULL AUTO_INCREMENT,
    `username` varchar(32)  DEFAULT NULL,
    `items`    varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tags` (`username`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `bill_record`
(
    `id`        int NOT NULL AUTO_INCREMENT,
    `username`  varchar(32)  DEFAULT NULL,
    `flow_type` int          DEFAULT NULL COMMENT '0支出 1收入',
    `amount`    double       DEFAULT NULL,
    `top_type`  tinyint      DEFAULT '1' COMMENT '一级分类',
    `items`     varchar(255) DEFAULT '',
    `recurring` int          DEFAULT '0' COMMENT '1周期性支出 2非周期性支出',
    `necessity` int          DEFAULT '1' COMMENT '1必须 2舒适 3享受 4浪费',
    `deal_time` datetime     DEFAULT NULL,
    `product`   varchar(255) DEFAULT NULL,
    `location`  varchar(64)  DEFAULT NULL,
    `dealer`    varchar(255) DEFAULT NULL,
    `deal_type` varchar(32)  DEFAULT NULL,
    `deal_no`   varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_deal_no` (`username`, `deal_no`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci