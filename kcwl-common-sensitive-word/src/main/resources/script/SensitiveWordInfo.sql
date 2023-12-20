CREATE TABLE `sensitive_word_info`
(
    `id`              bigint      NOT NULL,
    `sensitive_word`  varchar(50) NOT NULL COMMENT '敏感词',
    `sensitive_level` varchar(10) NOT NULL COMMENT '敏感级别',
    `type_name`       varchar(10) NOT NULL COMMENT '类型名称',
    `word_status`     tinyint NULL DEFAULT 1 COMMENT '状态',
    `create_time`     datetime NULL COMMENT '创建时间',
    `create_user_id`  bigint NULL DEFAULT 0 COMMENT '创建人',
    `update_time`     datetime NULL COMMENT '更新时间',
    `update_user_id`  bigint NULL DEFAULT 0 COMMENT '更新人',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `u_level_type_word`(`sensitive_level`, `type_name`, `sensitive_word`) USING BTREE COMMENT '敏感级别、类型、词 唯一索引'
) ENGINE = InnoDB;