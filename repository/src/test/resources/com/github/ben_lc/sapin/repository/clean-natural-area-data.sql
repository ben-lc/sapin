DELETE FROM sapin.location;

ALTER SEQUENCE sapin.location_id_seq RESTART
WITH
  1;

DELETE FROM sapin.natural_area;

ALTER SEQUENCE sapin.natural_area_id_seq RESTART
WITH
  1;

DELETE FROM sapin.natural_area_type;

ALTER SEQUENCE sapin.natural_area_type_id_seq RESTART
WITH
  1;
