DELETE FROM sapin.taxon_vernacular_name;

DELETE FROM sapin.taxon;

ALTER SEQUENCE sapin.taxon_id_seq RESTART
WITH
  1;
