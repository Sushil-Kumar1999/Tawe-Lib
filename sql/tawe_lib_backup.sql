-- phpMyAdmin SQL Dump
-- version 4.5.1
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Mar 16, 2019 at 07:17 PM
-- Server version: 10.1.16-MariaDB
-- PHP Version: 5.6.24

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `tawe_lib`
--

-- --------------------------------------------------------

--
-- Table structure for table `books`
--

CREATE TABLE `books` (
  `resourcesId` int(11) NOT NULL,
  `author` varchar(128) CHARACTER SET utf8 NOT NULL,
  `publisher` varchar(64) CHARACTER SET utf8 NOT NULL,
  `genre` varchar(64) CHARACTER SET utf8 NOT NULL,
  `ISBN` varchar(14) CHARACTER SET utf8 NOT NULL,
  `language` varchar(64) CHARACTER SET utf8 NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `books`
--

INSERT INTO `books` (`resourcesId`, `author`, `publisher`, `genre`, `ISBN`, `language`) VALUES
(1, 'Mary Shelley', 'Wordsworth Classics', 'Gothic Novel', '978-1853260230', 'English'),
(5, 'J.K.Rowling', 'Bloomsbury Publishing', 'Fantasy', '9781408855713', 'English'),
(7, 'Robert Louis Stevenson', 'Casell', 'Adventure', '9782017063964', 'English');

-- --------------------------------------------------------

--
-- Table structure for table `copies`
--

CREATE TABLE `copies` (
  `copiesId` int(11) NOT NULL,
  `resourceRef` int(11) NOT NULL,
  `loanDuration` int(11) DEFAULT NULL,
  `dueDate` datetime DEFAULT NULL,
  `custRef` int(11) DEFAULT NULL,
  `reservedBy` int(11) DEFAULT NULL,
  `loanDate` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `copies`
--

INSERT INTO `copies` (`copiesId`, `resourceRef`, `loanDuration`, `dueDate`, `custRef`, `reservedBy`, `loanDate`) VALUES
(1, 1, 1, NULL, 7, NULL, '2018-12-08 23:37:43'),
(2, 1, 1, '2019-03-05 13:24:47', 15, NULL, '2019-03-03 13:24:47'),
(3, 2, NULL, NULL, NULL, 7, NULL),
(4, 2, 7, '2018-12-08 00:00:00', 3, NULL, '2018-12-01 00:00:00'),
(5, 3, NULL, NULL, NULL, 7, NULL),
(6, 3, NULL, NULL, NULL, NULL, NULL),
(7, 3, NULL, NULL, NULL, NULL, NULL),
(8, 1, 1, '2019-03-04 22:52:09', 14, NULL, '2019-03-02 22:52:09'),
(9, 2, 1, NULL, 2, NULL, '2019-03-02 21:24:16'),
(10, 3, NULL, NULL, NULL, NULL, NULL),
(11, 3, NULL, NULL, NULL, NULL, NULL),
(12, 7, NULL, NULL, NULL, NULL, NULL),
(13, 7, NULL, NULL, NULL, NULL, NULL),
(14, 7, NULL, NULL, NULL, NULL, NULL),
(15, 7, NULL, NULL, NULL, NULL, NULL),
(16, 7, NULL, NULL, NULL, NULL, NULL),
(17, 7, NULL, NULL, NULL, NULL, NULL),
(18, 2, 1, NULL, 15, NULL, '2019-03-03 13:25:12'),
(19, 2, 1, NULL, 14, NULL, '2019-03-02 22:53:36'),
(20, 4, NULL, NULL, NULL, 7, NULL),
(21, 4, NULL, NULL, NULL, NULL, NULL),
(22, 5, 1, NULL, 15, NULL, '2019-03-03 13:24:26'),
(23, 5, 1, NULL, 14, NULL, '2019-03-02 22:52:43');

-- --------------------------------------------------------

--
-- Table structure for table `customers`
--

CREATE TABLE `customers` (
  `usersId` int(11) NOT NULL,
  `balance` int(11) NOT NULL DEFAULT '0',
  `LastSeen` timestamp NOT NULL DEFAULT '2038-01-19 03:14:07'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `customers`
--

INSERT INTO `customers` (`usersId`, `balance`, `LastSeen`) VALUES
(2, 0, '2038-01-19 03:14:07'),
(3, 25, '2038-01-19 03:14:07'),
(4, 0, '2038-01-19 03:14:07'),
(7, 0, '2038-01-19 03:14:07'),
(10, 0, '2038-01-19 03:14:07'),
(12, 0, '2038-01-19 03:14:07'),
(14, 0, '2038-01-19 03:14:07'),
(15, 0, '2038-01-19 03:14:07'),
(17, 0, '2038-01-19 03:14:07');

-- --------------------------------------------------------

--
-- Table structure for table `defaultavatars`
--

CREATE TABLE `defaultavatars` (
  `defaultAvatarsId` int(11) NOT NULL,
  `avatarName` varchar(99) CHARACTER SET utf8 DEFAULT NULL,
  `avatarPath` varchar(255) CHARACTER SET utf8 DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `defaultavatars`
--

INSERT INTO `defaultavatars` (`defaultAvatarsId`, `avatarName`, `avatarPath`) VALUES
(1, 'Avatar 1', 'Images/default_avatars/Avatar1.jpg'),
(2, 'Avatar 2', 'Images/default_avatars/Avatar2.jpg'),
(3, 'Avatar 3', 'Images/default_avatars/Avatar3.jpg'),
(4, 'Avatar 4', 'Images/default_avatars/Avatar4.jpg'),
(5, 'Avatar 5', 'Images/default_avatars/Avatar5.jpg'),
(6, 'Avatar 6', 'Images/default_avatars/Avatar6.jpg');

-- --------------------------------------------------------

--
-- Table structure for table `dvds`
--

CREATE TABLE `dvds` (
  `resourcesId` int(11) NOT NULL,
  `director` varchar(128) CHARACTER SET utf8 NOT NULL,
  `runtime` int(11) NOT NULL,
  `language` varchar(64) CHARACTER SET utf8 NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `dvds`
--

INSERT INTO `dvds` (`resourcesId`, `director`, `runtime`, `language`) VALUES
(2, 'Ridley Scott', 117, 'Enlgish');

-- --------------------------------------------------------

--
-- Table structure for table `dvdsubtitles`
--

CREATE TABLE `dvdsubtitles` (
  `resourcesId` int(11) NOT NULL,
  `subtitleLanguage` varchar(64) CHARACTER SET utf8 NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `dvdsubtitles`
--

INSERT INTO `dvdsubtitles` (`resourcesId`, `subtitleLanguage`) VALUES
(2, 'English'),
(2, 'English for the Hard of Hearing'),
(2, 'Korean');

-- --------------------------------------------------------

--
-- Table structure for table `eventbookings`
--

CREATE TABLE `eventbookings` (
  `eventsId` int(11) NOT NULL,
  `customerRef` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `eventbookings`
--

INSERT INTO `eventbookings` (`eventsId`, `customerRef`) VALUES
(1, 3),
(1, 15);

-- --------------------------------------------------------

--
-- Table structure for table `events`
--

CREATE TABLE `events` (
  `eventsId` int(11) NOT NULL,
  `title` varchar(128) CHARACTER SET utf8 NOT NULL,
  `eventDate` datetime NOT NULL,
  `maxAttendees` int(9) NOT NULL DEFAULT '2147483647',
  `description` varchar(4096) CHARACTER SET utf8 NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `events`
--

INSERT INTO `events` (`eventsId`, `title`, `eventDate`, `maxAttendees`, `description`) VALUES
(1, '''Meat'' the Author: Billy Bearmeat', '2019-12-12 02:00:00', 2, 'Billy Bearmeat''s an author now. Hopefully he decides to attend his own event');

-- --------------------------------------------------------

--
-- Table structure for table `laptopcomputers`
--

CREATE TABLE `laptopcomputers` (
  `resourcesId` int(11) NOT NULL,
  `manufacturer` varchar(64) CHARACTER SET utf8 NOT NULL,
  `model` varchar(64) CHARACTER SET utf8 NOT NULL,
  `OS` varchar(64) CHARACTER SET utf8 NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `laptopcomputers`
--

INSERT INTO `laptopcomputers` (`resourcesId`, `manufacturer`, `model`, `OS`) VALUES
(3, 'Dell', 'Latitude E7240', 'Windows 8'),
(4, 'HP', 'Pavilion', 'Windows 10 Home 64-bit'),
(6, 'Lenovo', 'YOGA', 'Windows 10 Home');

-- --------------------------------------------------------

--
-- Table structure for table `librarians`
--

CREATE TABLE `librarians` (
  `usersId` int(11) NOT NULL,
  `staffNumber` int(11) NOT NULL,
  `employmentDate` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `librarians`
--

INSERT INTO `librarians` (`usersId`, `staffNumber`, `employmentDate`) VALUES
(9, 1, '2018-12-02 22:35:11'),
(16, 234, '2018-12-05 00:00:00');

-- --------------------------------------------------------

--
-- Table structure for table `ratings`
--

CREATE TABLE `ratings` (
  `resourceRef` int(11) NOT NULL,
  `customerRef` int(11) NOT NULL,
  `rating` decimal(2,1) NOT NULL,
  `review` varchar(4096) CHARACTER SET utf8 DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `ratings`
--

INSERT INTO `ratings` (`resourceRef`, `customerRef`, `rating`, `review`) VALUES
(1, 7, '9.5', 'Super Duper. Please help me correct this.'),
(2, 2, '8.9', 'COOL MOVIE YO!!'),
(2, 3, '5.0', 'Hello everyone! heeeeeeeelo'),
(2, 15, '7.8', 'Woah! Wat a film');

-- --------------------------------------------------------

--
-- Table structure for table `resourcerequests`
--

CREATE TABLE `resourcerequests` (
  `resourceRef` int(11) NOT NULL,
  `customerRef` int(11) NOT NULL,
  `seq_resourceRequests` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `resourcerequests`
--

INSERT INTO `resourcerequests` (`resourceRef`, `customerRef`, `seq_resourceRequests`) VALUES
(1, 2, 1),
(1, 3, 2),
(1, 4, 3),
(1, 7, 4);

-- --------------------------------------------------------

--
-- Table structure for table `resources`
--

CREATE TABLE `resources` (
  `resourcesId` int(11) NOT NULL,
  `title` varchar(128) CHARACTER SET utf8 NOT NULL,
  `creationDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `year` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `resources`
--

INSERT INTO `resources` (`resourcesId`, `title`, `creationDate`, `year`) VALUES
(1, 'Frankenstein', '2018-12-01 00:00:00', 1818),
(2, 'Alien', '2018-12-01 00:00:00', 1979),
(3, 'Latitude E7240', '2018-12-01 00:00:00', 2016),
(4, 'Celine and Julie Go Boating', '2018-12-01 00:00:00', 1974),
(5, 'Goldeneye 007', '2019-02-22 20:07:48', 1997),
(6, 'Lenovo Yoga 5', '2019-03-16 18:08:07', 2001),
(7, 'Treasure Island', '2019-03-16 18:08:07', 1882);

-- --------------------------------------------------------

--
-- Stand-in structure for view `resourcetypes`
--
CREATE TABLE `resourcetypes` (
`resourcesId` int(11)
,`resourceType` int(6)
);

-- --------------------------------------------------------

--
-- Stand-in structure for view `resourcetypeslookup`
--
CREATE TABLE `resourcetypeslookup` (
`resourceTypeId` bigint(20)
,`resourceTypeString` varchar(15)
);

-- --------------------------------------------------------

--
-- Stand-in structure for view `resourcetypesnamed`
--
CREATE TABLE `resourcetypesnamed` (
`resourcesId` int(11)
,`resourceTypeString` varchar(15)
);

-- --------------------------------------------------------

--
-- Table structure for table `transactionfines`
--

CREATE TABLE `transactionfines` (
  `transactionFinesId` int(11) NOT NULL,
  `transactionDate` datetime NOT NULL,
  `amount` int(11) DEFAULT NULL,
  `copyRef` int(11) NOT NULL,
  `daysOverdue` int(11) NOT NULL,
  `custRef` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `transactionfines`
--

INSERT INTO `transactionfines` (`transactionFinesId`, `transactionDate`, `amount`, `copyRef`, `daysOverdue`, `custRef`) VALUES
(1, '2017-10-02 00:00:00', 100, 5, 222, 3),
(2, '2019-02-28 14:02:26', 100, 7, 80, 7);

-- --------------------------------------------------------

--
-- Table structure for table `transactionloans`
--

CREATE TABLE `transactionloans` (
  `transactionLoansId` int(11) NOT NULL,
  `transactionDate` datetime NOT NULL,
  `copyRef` int(11) NOT NULL,
  `custRef` int(11) NOT NULL,
  `returnDate` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `transactionloans`
--

INSERT INTO `transactionloans` (`transactionLoansId`, `transactionDate`, `copyRef`, `custRef`, `returnDate`) VALUES
(1, '2016-10-01 00:00:00', 5, 3, '2017-10-02 00:00:00'),
(2, '2018-12-08 23:37:43', 1, 7, NULL),
(3, '2018-12-08 23:44:37', 7, 7, '2019-02-28 14:02:27'),
(4, '2019-03-02 21:24:16', 9, 2, NULL),
(5, '2019-03-02 21:27:09', 19, 15, '2019-03-02 22:41:16'),
(6, '2019-03-02 21:51:47', 20, 15, '2019-03-02 22:41:51'),
(7, '2019-03-02 21:55:58', 22, 15, '2019-03-03 13:23:04'),
(8, '2019-03-02 22:45:24', 22, 15, NULL),
(9, '2019-03-02 22:45:57', 18, 15, '2019-03-03 13:23:19'),
(10, '2019-03-02 22:47:17', 21, 15, '2019-03-03 13:23:39'),
(11, '2019-03-02 22:52:09', 8, 14, NULL),
(12, '2019-03-02 22:52:43', 23, 14, NULL),
(13, '2019-03-02 22:53:36', 19, 14, NULL),
(14, '2019-03-03 13:24:27', 22, 15, NULL),
(15, '2019-03-03 13:24:48', 2, 15, NULL),
(16, '2019-03-03 13:25:12', 18, 15, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `transactionpayments`
--

CREATE TABLE `transactionpayments` (
  `transactionPaymentsId` int(11) NOT NULL,
  `transactionDate` datetime NOT NULL,
  `custRef` int(11) NOT NULL,
  `amount` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `transactionpayments`
--

INSERT INTO `transactionpayments` (`transactionPaymentsId`, `transactionDate`, `custRef`, `amount`) VALUES
(1, '2018-12-01 00:00:00', 3, 100);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `usersId` int(11) NOT NULL,
  `userName` varchar(64) CHARACTER SET utf8 DEFAULT NULL,
  `firstName` varchar(64) CHARACTER SET utf8 NOT NULL,
  `surname` varchar(64) CHARACTER SET utf8 NOT NULL,
  `mobileNumber` char(11) NOT NULL,
  `address` varchar(256) CHARACTER SET utf8 NOT NULL,
  `profileImagePath` varchar(256) CHARACTER SET utf8 NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`usersId`, `userName`, `firstName`, `surname`, `mobileNumber`, `address`, `profileImagePath`) VALUES
(2, 'PhoneBookEnthusiast', 'Aaron', 'Aaronsen', '05748391758', '4 A Road\r\nA Town\r\nAAA\r\nAI45 2FB', 'images/default_avatars/Avatar6.jpg'),
(3, 'BillyBearmeat', 'Boris', 'Blockwell', '05789321535', '1 Bog View\r\nBarcelona\r\nBC3 9AF', 'images/default_avatars/Avatar4.jpg'),
(4, 'CuboidQuent', 'Carlos', 'Santana', '03584917856', '20 Car Avenue\r\nCitadel City\r\nCN2 9DA', 'images/default_avatars/Avatar3.jpg'),
(7, 'TestUser', 'Test', 'User', '04758602757', '99 Example Valley, \nTest Town, \nTE5 9ST', 'Images/default_avatars/Avatar2.jpg'),
(9, 'Librarian1', 'Mosfet', 'Transistador', '08575847587', '1 Lois LaneLutetiaFranceSpain', 'images/custom_avatars/9.png'),
(10, '', '', '', '', '', 'Images/default_avatars/Avatar1.jpg'),
(12, '[SY]Red.Velvet', 'Smith', 'Smithson', '0784943', '10 Mountain View, Calsea', 'Images/default_avatars/Avatar1.jpg'),
(14, 'Valeria', 'King', 'Kong', '07824567835', '32 Amazon Valley, Port Orchid', 'Images/default_avatars/Avatar1.jpg'),
(15, 'Minor', 'Jenny', 'Jenkins', '07657323479', 'Copper Terrace, Silver Field', 'Images/default_avatars/Avatar1.jpg'),
(16, 'Librarian2', 'Kafia Money', 'Brodgens Honey', '0978564349', '5 Louis LaneBrockwellGreatBritain', 'Images/default_avatars/Avatar1.jpg'),
(17, 'dfds', 'dsfgss', 'sgfghs', 'dggge', 'sgfgghrr', 'Images/default_avatars/Avatar1.jpg');

-- --------------------------------------------------------

--
-- Table structure for table `videogames`
--

CREATE TABLE `videogames` (
  `resourcesId` int(11) NOT NULL,
  `Publisher` varchar(128) CHARACTER SET utf8 NOT NULL,
  `Genre` varchar(128) CHARACTER SET utf8 NOT NULL,
  `CertificateRating` varchar(16) CHARACTER SET utf8 NOT NULL,
  `MultiplayerSupport` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `videogames`
--

INSERT INTO `videogames` (`resourcesId`, `Publisher`, `Genre`, `CertificateRating`, `MultiplayerSupport`) VALUES
(5, 'Nintendo', 'First Person Shooter', 'Teen', 1);

-- --------------------------------------------------------

--
-- Structure for view `resourcetypes`
--
DROP TABLE IF EXISTS `resourcetypes`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `resourcetypes`  AS  select `resources`.`resourcesId` AS `resourcesId`,((((`books`.`resourcesId` is not null) + ((`dvds`.`resourcesId` is not null) * 2)) + ((`laptopcomputers`.`resourcesId` is not null) * 3)) + ((`videogames`.`resourcesId` is not null) * 4)) AS `resourceType` from ((((`resources` left join `books` on((`books`.`resourcesId` = `resources`.`resourcesId`))) left join `dvds` on((`dvds`.`resourcesId` = `resources`.`resourcesId`))) left join `laptopcomputers` on((`laptopcomputers`.`resourcesId` = `resources`.`resourcesId`))) left join `videogames` on((`videogames`.`resourcesId` = `resources`.`resourcesId`))) ;

-- --------------------------------------------------------

--
-- Structure for view `resourcetypeslookup`
--
DROP TABLE IF EXISTS `resourcetypeslookup`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `resourcetypeslookup`  AS  select 1 AS `resourceTypeId`,'Books' AS `resourceTypeString` union select 2 AS `resourceTypeId`,'DVDs' AS `resourceTypeString` union select 3 AS `resourceTypeId`,'LaptopComputers' AS `resourceTypeString` union select 4 AS `resourceTypeId`,'VideoGames' AS `resourceTypeString` ;

-- --------------------------------------------------------

--
-- Structure for view `resourcetypesnamed`
--
DROP TABLE IF EXISTS `resourcetypesnamed`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `resourcetypesnamed`  AS  select `resourcetypes`.`resourcesId` AS `resourcesId`,`resourcetypeslookup`.`resourceTypeString` AS `resourceTypeString` from (`resourcetypes` join `resourcetypeslookup` on((`resourcetypes`.`resourceType` = `resourcetypeslookup`.`resourceTypeId`))) ;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `books`
--
ALTER TABLE `books`
  ADD PRIMARY KEY (`resourcesId`),
  ADD UNIQUE KEY `ISBN` (`ISBN`);

--
-- Indexes for table `copies`
--
ALTER TABLE `copies`
  ADD PRIMARY KEY (`copiesId`),
  ADD KEY `resourceRef` (`resourceRef`),
  ADD KEY `custRef` (`custRef`),
  ADD KEY `reservedBy` (`reservedBy`);

--
-- Indexes for table `customers`
--
ALTER TABLE `customers`
  ADD PRIMARY KEY (`usersId`);

--
-- Indexes for table `defaultavatars`
--
ALTER TABLE `defaultavatars`
  ADD PRIMARY KEY (`defaultAvatarsId`);

--
-- Indexes for table `dvds`
--
ALTER TABLE `dvds`
  ADD PRIMARY KEY (`resourcesId`);

--
-- Indexes for table `dvdsubtitles`
--
ALTER TABLE `dvdsubtitles`
  ADD PRIMARY KEY (`resourcesId`,`subtitleLanguage`);

--
-- Indexes for table `eventbookings`
--
ALTER TABLE `eventbookings`
  ADD PRIMARY KEY (`eventsId`,`customerRef`),
  ADD KEY `customerRef` (`customerRef`);

--
-- Indexes for table `events`
--
ALTER TABLE `events`
  ADD PRIMARY KEY (`eventsId`);

--
-- Indexes for table `laptopcomputers`
--
ALTER TABLE `laptopcomputers`
  ADD PRIMARY KEY (`resourcesId`);

--
-- Indexes for table `librarians`
--
ALTER TABLE `librarians`
  ADD PRIMARY KEY (`usersId`),
  ADD UNIQUE KEY `staffNumber` (`staffNumber`);

--
-- Indexes for table `ratings`
--
ALTER TABLE `ratings`
  ADD PRIMARY KEY (`resourceRef`,`customerRef`),
  ADD KEY `customerRef` (`customerRef`);

--
-- Indexes for table `resourcerequests`
--
ALTER TABLE `resourcerequests`
  ADD PRIMARY KEY (`resourceRef`,`customerRef`),
  ADD KEY `customerRef` (`customerRef`);

--
-- Indexes for table `resources`
--
ALTER TABLE `resources`
  ADD PRIMARY KEY (`resourcesId`);

--
-- Indexes for table `transactionfines`
--
ALTER TABLE `transactionfines`
  ADD PRIMARY KEY (`transactionFinesId`),
  ADD KEY `copyRef` (`copyRef`),
  ADD KEY `custRef` (`custRef`);

--
-- Indexes for table `transactionloans`
--
ALTER TABLE `transactionloans`
  ADD PRIMARY KEY (`transactionLoansId`),
  ADD KEY `copyRef` (`copyRef`),
  ADD KEY `custRef` (`custRef`);

--
-- Indexes for table `transactionpayments`
--
ALTER TABLE `transactionpayments`
  ADD PRIMARY KEY (`transactionPaymentsId`),
  ADD KEY `custRef` (`custRef`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`usersId`),
  ADD UNIQUE KEY `userName` (`userName`);

--
-- Indexes for table `videogames`
--
ALTER TABLE `videogames`
  ADD PRIMARY KEY (`resourcesId`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `copies`
--
ALTER TABLE `copies`
  MODIFY `copiesId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;
--
-- AUTO_INCREMENT for table `defaultavatars`
--
ALTER TABLE `defaultavatars`
  MODIFY `defaultAvatarsId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;
--
-- AUTO_INCREMENT for table `events`
--
ALTER TABLE `events`
  MODIFY `eventsId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;
--
-- AUTO_INCREMENT for table `resources`
--
ALTER TABLE `resources`
  MODIFY `resourcesId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;
--
-- AUTO_INCREMENT for table `transactionfines`
--
ALTER TABLE `transactionfines`
  MODIFY `transactionFinesId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT for table `transactionloans`
--
ALTER TABLE `transactionloans`
  MODIFY `transactionLoansId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;
--
-- AUTO_INCREMENT for table `transactionpayments`
--
ALTER TABLE `transactionpayments`
  MODIFY `transactionPaymentsId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;
--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `usersId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;
--
-- Constraints for dumped tables
--

--
-- Constraints for table `books`
--
ALTER TABLE `books`
  ADD CONSTRAINT `books_ibfk_1` FOREIGN KEY (`resourcesId`) REFERENCES `resources` (`resourcesId`);

--
-- Constraints for table `copies`
--
ALTER TABLE `copies`
  ADD CONSTRAINT `copies_ibfk_1` FOREIGN KEY (`resourceRef`) REFERENCES `resources` (`resourcesId`),
  ADD CONSTRAINT `copies_ibfk_2` FOREIGN KEY (`custRef`) REFERENCES `customers` (`usersId`),
  ADD CONSTRAINT `copies_ibfk_3` FOREIGN KEY (`reservedBy`) REFERENCES `customers` (`usersId`);

--
-- Constraints for table `customers`
--
ALTER TABLE `customers`
  ADD CONSTRAINT `customers_ibfk_1` FOREIGN KEY (`usersId`) REFERENCES `users` (`usersId`);

--
-- Constraints for table `dvds`
--
ALTER TABLE `dvds`
  ADD CONSTRAINT `dvds_ibfk_1` FOREIGN KEY (`resourcesId`) REFERENCES `resources` (`resourcesId`);

--
-- Constraints for table `dvdsubtitles`
--
ALTER TABLE `dvdsubtitles`
  ADD CONSTRAINT `dvdsubtitles_ibfk_1` FOREIGN KEY (`resourcesId`) REFERENCES `dvds` (`resourcesId`);

--
-- Constraints for table `eventbookings`
--
ALTER TABLE `eventbookings`
  ADD CONSTRAINT `eventbookings_ibfk_1` FOREIGN KEY (`eventsId`) REFERENCES `events` (`eventsId`),
  ADD CONSTRAINT `eventbookings_ibfk_2` FOREIGN KEY (`customerRef`) REFERENCES `customers` (`usersId`);

--
-- Constraints for table `laptopcomputers`
--
ALTER TABLE `laptopcomputers`
  ADD CONSTRAINT `laptopcomputers_ibfk_1` FOREIGN KEY (`resourcesId`) REFERENCES `resources` (`resourcesId`);

--
-- Constraints for table `librarians`
--
ALTER TABLE `librarians`
  ADD CONSTRAINT `librarians_ibfk_1` FOREIGN KEY (`usersId`) REFERENCES `users` (`usersId`);

--
-- Constraints for table `ratings`
--
ALTER TABLE `ratings`
  ADD CONSTRAINT `ratings_ibfk_1` FOREIGN KEY (`resourceRef`) REFERENCES `resources` (`resourcesId`),
  ADD CONSTRAINT `ratings_ibfk_2` FOREIGN KEY (`customerRef`) REFERENCES `customers` (`usersId`);

--
-- Constraints for table `resourcerequests`
--
ALTER TABLE `resourcerequests`
  ADD CONSTRAINT `resourcerequests_ibfk_1` FOREIGN KEY (`resourceRef`) REFERENCES `resources` (`resourcesId`),
  ADD CONSTRAINT `resourcerequests_ibfk_2` FOREIGN KEY (`customerRef`) REFERENCES `customers` (`usersId`);

--
-- Constraints for table `transactionfines`
--
ALTER TABLE `transactionfines`
  ADD CONSTRAINT `transactionfines_ibfk_1` FOREIGN KEY (`copyRef`) REFERENCES `copies` (`copiesId`),
  ADD CONSTRAINT `transactionfines_ibfk_2` FOREIGN KEY (`custRef`) REFERENCES `customers` (`usersId`);

--
-- Constraints for table `transactionloans`
--
ALTER TABLE `transactionloans`
  ADD CONSTRAINT `transactionloans_ibfk_1` FOREIGN KEY (`copyRef`) REFERENCES `copies` (`copiesId`),
  ADD CONSTRAINT `transactionloans_ibfk_2` FOREIGN KEY (`custRef`) REFERENCES `customers` (`usersId`);

--
-- Constraints for table `transactionpayments`
--
ALTER TABLE `transactionpayments`
  ADD CONSTRAINT `transactionpayments_ibfk_1` FOREIGN KEY (`custRef`) REFERENCES `customers` (`usersId`);

--
-- Constraints for table `videogames`
--
ALTER TABLE `videogames`
  ADD CONSTRAINT `videogames_ibfk_1` FOREIGN KEY (`resourcesId`) REFERENCES `resources` (`resourcesId`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
