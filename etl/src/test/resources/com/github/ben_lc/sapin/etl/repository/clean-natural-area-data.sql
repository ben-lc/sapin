DELETE FROM sapin.natural_area;

DELETE FROM sapin.natural_area_type;

ALTER SEQUENCE sapin.natural_area_id_seq RESTART
WITH
  1;

ALTER SEQUENCE sapin.natural_area_type_id_seq RESTART
WITH
  1;
