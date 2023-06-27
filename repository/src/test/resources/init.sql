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

-- taxonomic data
CREATE TYPE sapin.taxon_rank_enum AS ENUM(
  'KINGDOM',
  'PHYLUM',
  'CLASS',
  'ORDER',
  'FAMILY',
  'GENUS',
  'SPECIES',
  'SUBSPECIES',
  'VARIETY',
  'FORM'
);

CREATE TYPE sapin.taxonomic_status_enum AS ENUM('ACCEPTED', 'DOUBTFUL', 'SYNONYM');

CREATE TABLE IF NOT EXISTS
  sapin.taxon (
    id serial PRIMARY KEY,
    src_name_id text NOT NULL,
    parent_id integer REFERENCES sapin.taxon (id),
    rank sapin.taxon_rank_enum NOT NULL,
    accepted_name text NOT NULL,
    tree_path ltree,
    UNIQUE (accepted_name)
  );

CREATE UNIQUE INDEX IF NOT EXISTS taxon_src_name_id_idx ON sapin.taxon (src_name_id);

CREATE INDEX IF NOT EXISTS taxon_parent_id_idx ON sapin.taxon (parent_id);

CREATE INDEX IF NOT EXISTS taxon_rank_idx ON sapin.taxon (rank);

CREATE INDEX IF NOT EXISTS taxon_tree_path_idx ON sapin.taxon USING GIST (tree_path);

CREATE TABLE IF NOT EXISTS
  sapin.taxon_scientific_name (
    id serial PRIMARY KEY,
    taxon_id integer REFERENCES sapin.taxon (id) NOT NULL,
    src_id text NOT NULL,
    rank sapin.taxon_rank_enum NOT NULL,
    taxonomic_status sapin.taxonomic_status_enum NOT NULL,
    name text NOT NULL,
    accepted_name_id integer REFERENCES sapin.taxon_scientific_name (id),
    original_name_id integer REFERENCES sapin.taxon_scientific_name (id),
    canonical_name text,
    authorship text,
    generic_name text,
    specific_epithet text,
    infraspecific_epithet text,
    name_published_in text,
    UNIQUE (name)
  );

CREATE INDEX IF NOT EXISTS taxon_scientific_name_taxon_id_idx ON sapin.taxon_scientific_name (taxon_id);

CREATE UNIQUE INDEX IF NOT EXISTS taxon_scientific_name_src_id_idx ON sapin.taxon_scientific_name (src_id);

CREATE INDEX IF NOT EXISTS taxon_scientific_name_taxonomic_status_idx ON sapin.taxon_scientific_name (taxonomic_status);

CREATE INDEX IF NOT EXISTS taxon_scientific_name_trgm_idx ON sapin.taxon_scientific_name USING GIST (name gist_trgm_ops);

CREATE INDEX IF NOT EXISTS taxon_scientific_name_rank_idx ON sapin.taxon_scientific_name (rank);

CREATE TABLE IF NOT EXISTS
  sapin.taxon_vernacular_name (
    taxon_id integer references sapin.taxon (id),
    name text NOT NULL,
    rank sapin.taxon_rank_enum NOT NULL,
    language varchar(2),
    PRIMARY KEY (taxon_id, language),
    UNIQUE (taxon_id, name)
  );

CREATE INDEX IF NOT EXISTS taxon_vernacular_name_trgm_idx ON sapin.taxon_vernacular_name USING GIST (name gist_trgm_ops);
CREATE INDEX IF NOT EXISTS taxon_vernacular_name_rank_idx ON sapin.taxon_vernacular_name (rank);

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
    level_local_name text,
    level_local_name_en text,
    iso_id text,
    geom geometry NOT NULL,
    tree_path ltree
  );

CREATE INDEX IF NOT EXISTS location_name_trgm_idx ON sapin.location USING GIST (name gist_trgm_ops);

CREATE INDEX IF NOT EXISTS location_loc_level_idx ON sapin.location (level);

CREATE INDEX IF NOT EXISTS location_geom_idx ON sapin.location USING GIST (geom);

CREATE INDEX IF NOT EXISTS location_tree_path_idx ON sapin.location USING GIST (tree_path);

CREATE TABLE IF NOT EXISTS
  sapin.location_taxon_asso (
    loc_id integer REFERENCES sapin.location (id),
    taxon_id integer REFERENCES sapin.taxon (id),
    PRIMARY KEY (loc_id, taxon_id)
  );
