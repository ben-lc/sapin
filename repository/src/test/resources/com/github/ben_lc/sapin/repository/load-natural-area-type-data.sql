INSERT INTO
  sapin.natural_area_type (name, code, description)
VALUES
  (
    'Natura 2000',
    'NATURA2000',
    'Le réseau Natura 2000 rassemble des sites naturels ou semi-naturels de l''Union européenne ayant une grande valeur patrimoniale, par la faune et la flore exceptionnelles qu''ils contiennent'
  ),
  (
    'Zone Naturelle d''intérêt écologique, faunistique et floristique',
    'ZNIEFF',
    null
  ),
  ('Something', 'a natural area type', null);

INSERT INTO
  sapin.location (level, name, iso_id, tree_path, geom)
VALUES
  (
    1,
    'Italy',
    'ITA',
    '1',
    ST_GeomFromText (
      'POLYGON((6.63087892600015 35.492915999000104,6.63087892600015 47.09265121200008,18.520694733000084 47.09265121200008,18.520694733000084 35.492915999000104,6.63087892600015 35.492915999000104))',
      4326
    )
  ),
  (
    1,
    'Japan',
    'JPN',
    '2',
    ST_GeomFromText (
      'POLYGON((122.93319702100007 24.045416000000102,122.93319702100007 45.52278518700018,153.98693800000012 45.52278518700018,153.98693800000012 24.045416000000102,122.93319702100007 24.045416000000102))',
      4326
    )
  ),
  (
    1,
    'France',
    'FRA',
    '3',
    ST_GeomFromText (
      'POLYGON((-5.143750999999895 41.33375200000012,-5.143750999999895 51.089397432000055,9.560416222000129 51.089397432000055,9.560416222000129 41.33375200000012,-5.143750999999895 41.33375200000012))',
      4326
    )
  );

INSERT INTO
  sapin.natural_area_type_location_asso (type_id, loc_id)
VALUES
  (1, 1),
  (1, 3),
  (2, 3);
