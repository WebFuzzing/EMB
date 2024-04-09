ALTER TABLE JOURNALPOST
    ADD COLUMN OPPRETTET_TID TIMESTAMP(3) DEFAULT localtimestamp,
    ADD COLUMN OPPRETTET_AV  VARCHAR      DEFAULT 'VL' NOT NULL;

UPDATE JOURNALPOST
SET OPPRETTET_TID = '2020-06-24 00:00:00-00';

ALTER TABLE JOURNALPOST
    ALTER COLUMN OPPRETTET_TID SET NOT NULL;

ALTER TABLE behandling
    DROP COLUMN journalpost_id;