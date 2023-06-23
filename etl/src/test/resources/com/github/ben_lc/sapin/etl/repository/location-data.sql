INSERT INTO sapin.location (level, name, iso_id, src_id, geom) VALUES (1, 'Italy', 'ITA', 'ITA', ST_GeomFromText('POLYGON ((10 30, 30 30, 30 10, 10 10, 10 30))'));
INSERT INTO sapin.location (level, name, iso_id, src_id, geom) VALUES (1, 'Japan', 'JPN', 'JPN',ST_GeomFromText('POLYGON ((10 30, 30 30, 30 10, 10 10, 10 30))'));
INSERT INTO sapin.location (level, name, iso_id, src_id, geom) VALUES (1, 'France', 'FRA', 'FRA', ST_GeomFromText('POLYGON ((10 30, 30 30, 30 10, 10 10, 10 30))'));
INSERT INTO sapin.location (level, level_local_name, level_local_name_en, name, iso_id, src_id, src_parent_id, geom) VALUES (2, 'Région', 'Region', 'Nouvelle-Aquitaine', 'FR-NAQ', 'FR.1', 'FR', ST_GeomFromText('POLYGON ((10 30, 30 30, 30 10, 10 10, 10 30))'));
