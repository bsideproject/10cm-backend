

CREATE TABLE `image` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_date` datetime DEFAULT NULL,
    `modified_date` datetime DEFAULT NULL,
    `created_by` bigint(20) DEFAULT NULL,
    `last_modified_by` bigint(20) DEFAULT NULL,
    `file_path` varchar(255) DEFAULT NULL,
    `name` varchar(255) DEFAULT NULL,
    `original_name` varchar(255) DEFAULT NULL,
    `size` bigint(20) DEFAULT NULL,
    `type` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `place` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_date` datetime DEFAULT NULL,
    `modified_date` datetime DEFAULT NULL,
    `address` varchar(255) DEFAULT NULL,
    `address_detail` varchar(255) DEFAULT NULL,
    `description` varchar(255) DEFAULT NULL,
    `image` varchar(255) DEFAULT NULL,
    `latitude` varchar(255) NOT NULL,
    `longitude` varchar(255) NOT NULL,
    `name` varchar(255) NOT NULL,
    `phone` varchar(255) DEFAULT NULL,
    `user_id` bigint(20) DEFAULT NULL,
    `road_address` varchar(255) DEFAULT NULL,
    `homepage` varchar(500) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FKt4pcyljfytbpfv02egbo9xyas` (`user_id`),
    CONSTRAINT `FKt4pcyljfytbpfv02egbo9xyas` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `place_tag` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `place_id` bigint(20) DEFAULT NULL,
    `tag_id` bigint(20) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `tag` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `name` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `trip` (
    `trip_id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_date` datetime DEFAULT NULL,
    `modified_date` datetime DEFAULT NULL,
    `created_by` bigint(20) DEFAULT NULL,
    `last_modified_by` bigint(20) DEFAULT NULL,
    `description` varchar(1000) DEFAULT NULL,
    `end_date` date DEFAULT NULL,
    `share_yn` char(1) DEFAULT 'N',
    `start_date` date DEFAULT NULL,
    `trip_image_url` varchar(1000) DEFAULT NULL,
    `trip_name` varchar(300) DEFAULT NULL,
    `user_id` bigint(20) DEFAULT NULL,
    `trip_image_name` varchar(500) DEFAULT NULL,
    PRIMARY KEY (`trip_id`),
    KEY `FKaushvpni36yq75fpqqj1ki4ap` (`user_id`),
    CONSTRAINT `FKaushvpni36yq75fpqqj1ki4ap` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `trip_entry` (
    `trip_entry_id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_date` datetime DEFAULT NULL,
    `modified_date` datetime DEFAULT NULL,
    `created_by` bigint(20) DEFAULT NULL,
    `last_modified_by` bigint(20) DEFAULT NULL,
    `entry_sn` int(11) NOT NULL,
    `trip_id` bigint(20) DEFAULT NULL,
    PRIMARY KEY (`trip_entry_id`),
    KEY `FKi72c0dcdxxq8odr0mmfpmgn35` (`trip_id`),
    CONSTRAINT `FKi72c0dcdxxq8odr0mmfpmgn35` FOREIGN KEY (`trip_id`) REFERENCES `trip` (`trip_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `trip_place` (
    `trip_place_id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_date` datetime DEFAULT NULL,
    `modified_date` datetime DEFAULT NULL,
    `created_by` bigint(20) DEFAULT NULL,
    `last_modified_by` bigint(20) DEFAULT NULL,
    `address` varchar(500) DEFAULT NULL,
    `address_detail` varchar(500) DEFAULT NULL,
    `description` varchar(1000) DEFAULT NULL,
    `latitude` varchar(255) DEFAULT NULL,
    `longitude` varchar(255) DEFAULT NULL,
    `name` varchar(300) DEFAULT NULL,
    `phone` varchar(255) DEFAULT NULL,
    `place_sn` int(11) NOT NULL,
    `trip_entry_id` bigint(20) DEFAULT NULL,
    `place_uid` varchar(20) DEFAULT NULL,
    `road_address` varchar(500) DEFAULT NULL,
    `homepage` varchar(500) DEFAULT NULL,
    PRIMARY KEY (`trip_place_id`),
    KEY `FK9duxdj1cudxe7jtw600292rbg` (`trip_entry_id`),
    CONSTRAINT `FK9duxdj1cudxe7jtw600292rbg` FOREIGN KEY (`trip_entry_id`) REFERENCES `trip_entry` (`trip_entry_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `user` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_date` datetime DEFAULT NULL,
    `modified_date` datetime DEFAULT NULL,
    `email` varchar(500) DEFAULT NULL,
    `name` varchar(50) DEFAULT NULL,
    `nickname` varchar(200) DEFAULT NULL,
    `profile_image` varchar(500) DEFAULT NULL,
    `profile_upload_yn` char(1) DEFAULT 'N',
    `social_id` varchar(200) DEFAULT NULL,
    `social_type` varchar(50) DEFAULT NULL,
    `phone` varchar(20) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `email` (`email`),
    UNIQUE KEY `social_id` (`social_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;