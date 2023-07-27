DELETE FROM sapin.natural_area_type_location_asso;

DELETE FROM sapin.natural_area_type;

ALTER SEQUENCE sapin.natural_area_type_id_seq RESTART
WITH
  1;
