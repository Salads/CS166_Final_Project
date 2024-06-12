DROP INDEX IF EXISTS gameID_index;
DROP INDEX IF EXISTS gamePrice_index;
DROP INDEX IF EXISTS gameGenre_index;

CREATE INDEX gameID_index
ON Catalog
USING BTREE
(gameID);

CREATE INDEX gamePrice_index
ON Catalog
USING BTREE
(price);

CREATE INDEX gameGenre_index
ON Catalog 
USING BTREE
(genre);

