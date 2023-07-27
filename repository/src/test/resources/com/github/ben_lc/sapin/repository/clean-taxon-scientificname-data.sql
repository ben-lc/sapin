DELETE FROM sapin.taxon_scientific_name;

ALTER SEQUENCE sapin.taxon_scientific_name_id_seq RESTART
WITH
  1;

DELETE FROM sapin.taxon;

ALTER SEQUENCE sapin.taxon_id_seq RESTART
WITH
  1;
