-- create temporary table and indexes to load taxonomic data
CREATE TABLE IF NOT EXISTS
  sapin.tmp_taxon_scientific_name (
    src_id text PRIMARY KEY,
    taxonomic_status sapin.taxonomic_status_enum NOT NULL,
    name text NOT NULL,
    accepted_name_id integer,
    canonical_name text,
    authorship text,
    generic_name text,
    specific_epithet text,
    infraspecific_epithet text,
    name_published_in text,
    src_accepted_name_id text,
    src_parent_id text,
    rank sapin.taxon_rank_enum
  );

CREATE INDEX tmp_taxon_scientific_name_name_idx ON sapin.tmp_taxon_scientific_name (name);

CREATE INDEX tmp_taxon_scientific_name_src_parent_id_idx ON sapin.tmp_taxon_scientific_name (src_parent_id);

CREATE INDEX tmp_taxon_scientific_name_accepted_name_id_idx ON sapin.tmp_taxon_scientific_name (accepted_name_id);

CREATE UNIQUE INDEX taxon_scientific_name_name_idx ON sapin.taxon_scientific_name (name);

CREATE UNIQUE INDEX taxon_accepted_name_idx ON sapin.taxon (accepted_name);

-- delete table constraints during import
ALTER TABLE sapin.taxon
DROP CONSTRAINT taxon_parent_id_fkey;

ALTER TABLE sapin.taxon_scientific_name
ALTER COLUMN taxon_id
DROP NOT NULL;

ALTER TABLE sapin.taxon_scientific_name
DROP CONSTRAINT taxon_scientific_name_taxon_id_fkey;

ALTER TABLE sapin.taxon_scientific_name
DROP CONSTRAINT taxon_scientific_name_accepted_name_id_fkey;

ALTER TABLE sapin.taxon_vernacular_name
ALTER COLUMN taxon_id
DROP NOT NULL;

CREATE TABLE IF NOT EXISTS
  sapin.tmp_taxon_distribution (
    taxon_id integer REFERENCES sapin.taxon (id),
    location_id text,
    location_name tsvector,
    location_country text,
    location_country_id varchar(2)
  );

CREATE TABLE IF NOT EXISTS
  sapin.tmp_location_taxon_distribution_mapping (
    loc_id integer REFERENCES sapin.location (id) PRIMARY KEY,
    location_id_in text,
    location_name_contains text
  )
