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
  sapin.location (
    parent_id,
    level,
    level_name,
    level_name_en,
    name,
    iso_id,
    tree_path,
    geom
  )
VALUES
  (
    3,
    2,
    'Région',
    'Region',
    'Nouvelle-Aquitaine',
    'FR-NAQ',
    '3.4',
    ST_GeomFromText (
      'POLYGON((-1.788472055999932 42.77767181300004,-1.788472055999932 47.175758362000124,2.609396219000075 47.175758362000124,2.609396219000075 42.77767181300004,-1.788472055999932 42.77767181300004))',
      4326
    )
  );

INSERT INTO
  sapin.location (
    parent_id,
    level,
    level_name,
    level_name_en,
    name,
    iso_id,
    tree_path,
    geom
  )
VALUES
  (
    4,
    3,
    'Département',
    'Department',
    'Gironde',
    'FR-33',
    '3.4.5',
    ST_GeomFromText (
      'POLYGON((-1.788472055999932 42.77767181300004,-1.788472055999932 47.175758362000124,2.609396219000075 47.175758362000124,2.609396219000075 42.77767181300004,-1.788472055999932 42.77767181300004))',
      4326
    )
  );

INSERT INTO
  sapin.location (
    parent_id,
    level,
    level_name,
    level_name_en,
    name,
    iso_id,
    tree_path,
    geom
  )
VALUES
  (
    1,
    3,
    'Regione',
    'Region',
    'Lazio',
    'IT-62',
    '1.6',
    ST_GeomFromText (
      'POLYGON ((11.450326919000133 40.78458400000011, 11.450326919000133 42.839725496000085, 14.028536797000015 42.839725496000085, 14.028536797000015 40.78458400000011, 11.450326919000133 40.78458400000011))',
      4326
    )
  );