CREATE TABLE temp (id char(512) PRIMARY KEY NOT NULL, path char(512) NOT NULL, name char(512) NOT NULL, type INTEGER NOT NULL, objectId char(512), parentId char(512) NOT NULL, syncStatus INTEGER DEFAULT 1, sha1 char(512));
INSERT INTO temp SELECT * FROM tb_remoteInfo;
DROP TABLE tb_remoteInfo;
ALTER TABLE temp RENAME TO tb_remoteInfo;
CREATE INDEX REMOTEINFO_IDX1 ON tb_remoteInfo(id);