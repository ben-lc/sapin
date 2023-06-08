-- create temporary table and indexes to load taxonomic data
CREATE TABLE IF NOT EXISTS
  sapin.tmp_taxon_scientific_name (
    src_taxon_name_id text PRIMARY KEY,
    taxonomic_status sapin.taxonomic_status_enum NOT NULL,
    scientific_name text NOT NULL,
    accepted_name_id integer,
    original_name_id integer,
    canonical_name text,
    scientific_name_authorship text,
    generic_name text,
    specific_epithet text,
    infraspecific_epithet text,
    name_published_in text,
    src_accepted_name_id text,
    src_original_name_id text,
    src_parent_name_id text,
    taxon_rank sapin.taxon_rank_enum
  );

CREATE INDEX tmp_taxon_scientific_name_scientific_name_idx ON sapin.tmp_taxon_scientific_name (scientific_name);

CREATE INDEX tmp_taxon_scientific_name_src_parent_name_id_idx ON sapin.tmp_taxon_scientific_name (src_parent_name_id);

CREATE INDEX tmp_taxon_scientific_name_accepted_name_id_idx ON sapin.tmp_taxon_scientific_name (accepted_name_id);

CREATE INDEX tmp_taxon_scientific_name_original_name_id_idx ON sapin.tmp_taxon_scientific_name (original_name_id);

CREATE UNIQUE INDEX taxon_scientific_name_scientific_name_idx ON sapin.taxon_scientific_name (scientific_name);

CREATE UNIQUE INDEX taxon_accepted_name_idx ON sapin.taxon (accepted_name);

-- delete table constraints during import
ALTER TABLE sapin.taxon
DROP CONSTRAINT taxon_parent_taxon_id_fkey;

ALTER TABLE sapin.taxon_scientific_name
ALTER COLUMN taxon_id
DROP NOT NULL;

ALTER TABLE sapin.taxon_scientific_name
DROP CONSTRAINT taxon_scientific_name_taxon_id_fkey;

ALTER TABLE sapin.taxon_scientific_name
DROP CONSTRAINT taxon_scientific_name_accepted_name_id_fkey;

ALTER TABLE sapin.taxon_scientific_name
DROP CONSTRAINT taxon_scientific_name_original_name_id_fkey;
