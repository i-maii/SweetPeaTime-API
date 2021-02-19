CREATE TABLE `Flower` (
      `id` int NOT NULL AUTO_INCREMENT,
      `flowerName` varchar(100) NOT NULL,
      `mainCategory` varchar(100) NOT NULL,
      `isStock` tinyint(1) NOT NULL,
      `lifeTime` int DEFAULT NULL,
      `unit` varchar(100) NOT NULL,
      `isFreeze` tinyint(1) DEFAULT NULL,
      `flowerCategory` varchar(100) NOT NULL,
      `flowerType` varchar(100) CHARACTER SET utf16 COLLATE utf16_general_ci DEFAULT NULL,
      `capacity` int DEFAULT NULL,
      PRIMARY KEY (`flowerId`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf16;