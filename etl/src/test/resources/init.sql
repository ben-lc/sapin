-- postgres extentions to load
CREATE EXTENSION IF NOT EXISTS postgis
WITH
  SCHEMA public;

CREATE EXTENSION IF NOT EXISTS pg_trgm
WITH
  SCHEMA public;

CREATE EXTENSION IF NOT EXISTS ltree
WITH
  SCHEMA public;

CREATE SCHEMA IF NOT EXISTS sapin;

-- location data
CREATE TABLE IF NOT EXISTS
  sapin.location (
    id serial PRIMARY KEY,
    parent_id integer REFERENCES sapin.location (id),
    level smallint NOT NULL CHECK (
      level > 0
      AND level < 4
    ),
    name text NOT NULL,
    level_name text,
    level_name_en text,
    iso_id text,
    parent_iso_id text,
    geom geometry NOT NULL,
    tree_path ltree,
    src_id text NOT NULL,
    src_parent_id text
  );
