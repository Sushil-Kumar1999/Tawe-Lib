CREATE DATABASE Tawe_Lib;
USE Tawe_Lib;

CREATE TABLE Users (
	usersId INTEGER AUTO_INCREMENT PRIMARY KEY,
	userName NVARCHAR(64) UNIQUE,
	firstName NVARCHAR(64) NOT NULL,
	surname NVARCHAR(64) NOT NULL,
	mobileNumber CHAR(11) NOT NULL,
	address NVARCHAR(256) NOT NULL,
	profileImagePath NVARCHAR(256) NOT NULL
);

CREATE TABLE Customers (
	usersId INTEGER PRIMARY KEY,
	balance INTEGER DEFAULT 0 NOT NULL,
	--2038-01-19 03:14:07 is the latest datetime allowed by TimeStamp
	lastSeen TIMESTAMP NOT NULL DEFAULT '2038-01-19 03:14:07',
	FOREIGN KEY (usersId) REFERENCES Users(usersId)
);

CREATE TABLE Librarians (
	usersId INTEGER PRIMARY KEY,
	staffNumber INTEGER UNIQUE NOT NULL,
	employmentDate DATETIME  NOT NULL,
	FOREIGN KEY (usersId) REFERENCES Users(usersId)
);

CREATE TABLE Resources (
	resourcesId INTEGER AUTO_INCREMENT PRIMARY KEY,
	title NVARCHAR(128) NOT NULL,
	year INTEGER NOT NULL,
	creationDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Books (
	resourcesId INTEGER PRIMARY KEY,
	author NVARCHAR(128) NOT NULL,
	publisher NVARCHAR(64) NOT NULL,
	genre NVARCHAR(64) NOT NULL,
	ISBN NVARCHAR(13) UNIQUE NOT NULL,
	language NVARCHAR(64) NOT NULL,
	FOREIGN KEY (resourcesId) REFERENCES Resources(resourcesId)
);

CREATE TABLE DVDs (
	resourcesId INTEGER PRIMARY KEY,
	director  NVARCHAR(128) NOT NULL,
	runtime INTEGER NOT NULL,
	language  NVARCHAR(64) NOT NULL,
	FOREIGN KEY (resourcesId) REFERENCES Resources(resourcesId)
);

CREATE TABLE DVDSubtitles (
	resourcesId INTEGER,
	subtitleLanguage NVARCHAR(64),
	FOREIGN KEY (resourcesId) REFERENCES DVDs(resourcesId),
	PRIMARY KEY (resourcesId,subtitleLanguage)
);

CREATE TABLE LaptopComputers (
	resourcesId INTEGER PRIMARY KEY,
	manufacturer NVARCHAR(64) NOT NULL,
	model NVARCHAR(64) NOT NULL,
	OS NVARCHAR(64) NOT NULL,
	FOREIGN KEY (resourcesId) REFERENCES Resources(resourcesId)
);

CREATE TABLE Copies (
	copiesId INTEGER AUTO_INCREMENT PRIMARY KEY,
	resourceRef INTEGER NOT NULL,
	loanDuration INTEGER,
	dueDate DATETIME,
	custRef INTEGER,
	reservedBy INTEGER,
	loanDate DATETIME,
	FOREIGN KEY (resourceRef) REFERENCES Resources(resourcesId),
	FOREIGN KEY (custRef) REFERENCES Customers(usersId),
	FOREIGN KEY (reservedBy) REFERENCES Customers(usersId)
);

CREATE TABLE TransactionFines (
	transactionFinesId INTEGER AUTO_INCREMENT PRIMARY KEY,
	transactionDate DATETIME NOT NULL,
	amount INTEGER,
	copyRef INTEGER NOT NULL,
	daysOverdue INTEGER NOT NULL,
	custRef INTEGER NOT NULL,
	FOREIGN KEY (copyRef) REFERENCES Copies(copiesId),	
	FOREIGN KEY (custRef) REFERENCES Customers(usersId)
);

CREATE TABLE TransactionLoans (
	transactionLoansId INTEGER AUTO_INCREMENT PRIMARY KEY,
	transactionDate DATETIME NOT NULL,
	copyRef INTEGER NOT NULL,
	custRef INTEGER NOT NULL,
	returnDate DATETIME,
	FOREIGN KEY (copyRef) REFERENCES Copies(copiesId),
	FOREIGN KEY (custRef) REFERENCES Customers(usersId)
);

CREATE TABLE TransactionPayments (
	transactionPaymentsId INTEGER AUTO_INCREMENT PRIMARY KEY,
	transactionDate DATETIME NOT NULL,
	custRef INTEGER NOT NULL,
	amount INTEGER,
	FOREIGN KEY (custRef) REFERENCES Customers(usersId)
);

CREATE TABLE ResourceRequests (
	resourceRef INTEGER,
	customerRef INTEGER,
	seq_resourceRequests INTEGER NOT NULL,
	PRIMARY KEY (resourceRef,customerRef),
	FOREIGN KEY (resourceRef) REFERENCES Resources(resourcesId),
	FOREIGN KEY (customerRef) REFERENCES Customers(usersId)
);

CREATE TABLE DefaultAvatars (
	defaultAvatarsId INTEGER AUTO_INCREMENT PRIMARY KEY,
	avatarName NVARCHAR(99),
	avatarPath NVARCHAR(255)	
);

CREATE TABLE VideoGames (
	resourcesId INT(11) PRIMARY KEY,
	Publisher NVARCHAR(128) NOT NULL,
	Genre NVARCHAR(128) NOT NULL,
	CertificateRating NVARCHAR(16) NOT NULL,
	MultiplayerSupport BOOLEAN NOT NULL,
	FOREIGN KEY (ResourcesId) REFERENCES resources(ResourcesId)
);

CREATE TABLE Ratings (
	resourceRef INT(11),
	customerRef INT(11),
	rating DECIMAL(2,1) NOT NULL,
	review NVARCHAR(4096),
	PRIMARY KEY (resourceRef, customerRef),
	FOREIGN KEY (resourceRef) REFERENCES Resources(resourcesId),
	FOREIGN KEY (customerRef) REFERENCES Customers(usersId)
);

CREATE TABLE Events (
	eventsId INT(11) AUTO_INCREMENT PRIMARY KEY,
	title NVARCHAR(128) NOT NULL,
	eventDate DATETIME NOT NULL,
	--2147483647 is max value for INT
	maxAttendees INT(10) NOT NULL DEFAULT 2147483647,
	description NVARCHAR (4096) NOT NULL
);
	
CREATE TABLE EventBookings (
	eventsId INT(11),
	customerRef INT(11),
	PRIMARY KEY (eventsId, customerRef),
	FOREIGN KEY (eventsId) REFERENCES Events(eventsId),
	FOREIGN KEY (customerRef) REFERENCES Customers(usersId)
);

CREATE TABLE ResourceTypesLookup (
    ResourceTypesLookupId INT(2) PRIMARY KEY,
    ResourceTypeName NVARCHAR(64) NOT NULL
)

CREATE VIEW ResourceTypes AS
SELECT resources.resourcesId, 
((NOT(ISNULL(books.resourcesId))) + ((NOT(ISNULL(dvds.resourcesId))) * 2) + ((NOT(ISNULL(laptopcomputers.resourcesId))) * 3) + ((NOT(ISNULL(videogames.resourcesId))) * 4)) AS resourceType
FROM resources LEFT JOIN books ON books.resourcesId = resources.resourcesId
LEFT JOIN dvds ON dvds.resourcesId = resources.resourcesId
LEFT JOIN laptopcomputers ON laptopcomputers.resourcesId = resources.resourcesId
LEFT JOIN videogames ON videogames.resourcesId = resources.resourcesId) AS getResourceTypes;

CREATE VIEW ResourceTypesLookup AS
SELECT 1 AS resourceTypeId, 'Books' AS resourceTypeString
    UNION
    SELECT 2 AS resourceTypeId, 'DVDs' AS resourceTypeString
    UNION
    SELECT 3 AS resourceTypeId, 'LaptopComputers' AS resourceTypeString
    UNION
    SELECT 4 AS resourceTypeId, 'VideoGames' AS resourceTypeString;

CREATE VIEW ResourceTypesNamed AS
SELECT resourcesId, resourceTypeString
FROM resourceTypes JOIN resourceTypesLookup ON
resourcetypes.resourceType = resourcetypeslookup.resourceTypeId;

