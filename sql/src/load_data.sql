/* Replace the location to where you saved the data files*/

\! echo "USER: " $USER
\! echo "Working Directory: '$(pwd)'"

\set username `echo $USER`
\set usercsvpath '/home/csmajs/':username'/CS166_Final_Project/data/users.csv'
\set catalogPath '/home/csmajs/':username'/CS166_Final_Project/data/catalog.csv'
\set rentalOrderPath '/home/csmajs/':username'/CS166_Final_Project/data/rentalorder.csv'
\set trackingInfoPath '/home/csmajs/':username'/CS166_Final_Project/data/trackinginfo.csv'
\set GamesInOrderPath '/home/csmajs/':username'/CS166_Final_Project/data/gamesinorder.csv'
\set courierscsvPath '/home/csmajs/':username'/CS166_Final_Project/data/couriers.csv'
\set citiescsvPath '/home/csmajs/':username'/CS166_Final_Project/data/cities.csv'

COPY Users
FROM :'usercsvpath'
WITH DELIMITER ',' CSV HEADER;

COPY Catalog
FROM :'catalogPath'
WITH DELIMITER ',' CSV HEADER;

COPY RentalOrder
FROM :'rentalOrderPath'
WITH DELIMITER ',' CSV HEADER;

COPY TrackingInfo
FROM :'trackingInfoPath'
WITH DELIMITER ',' CSV HEADER;

COPY GamesInOrder
FROM :'GamesInOrderPath'
WITH DELIMITER ',' CSV HEADER;

COPY Couriers
FROM :'courierscsvPath'
WITH DELIMITER ',' CSV HEADER;

COPY Cities
FROM :'citiescsvPath'
WITH DELIMITER ',' CSV HEADER;
