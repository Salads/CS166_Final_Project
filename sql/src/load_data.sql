/* Replace the location to where you saved the data files*/

\! echo "USER: " $USER
\! echo "Working Directory: '$(pwd)'"
\set username `echo $USER`
\set usercsvpath '/home/csmajs/':username'/github/CS166_Final_Project/data/users.csv'
\set catalogPath '/home/csmajs/':username'/github/CS166_Final_Project/data/catalog.csv'
\set rentalOrderPath '/home/csmajs/':username'/github/CS166_Final_Project/data/rentalorder.csv'
\set trackingInfoPath '/home/csmajs/':username'/github/CS166_Final_Project/data/trackinginfo.csv'
\set GamesInOrderPath '/home/csmajs/':username'/github/CS166_Final_Project/data/gamesinorder.csv'

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
