ALTER TABLE PO_STATSBORGERSKAP ALTER COLUMN opprettet_tid TYPE TIMESTAMP(3);
ALTER TABLE PO_STATSBORGERSKAP ALTER COLUMN opprettet_tid SET DEFAULT localtimestamp;