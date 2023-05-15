-- delete temporary table and indexes
DROP INDEX IF EXISTS sapin.taxon_scientific_name_scientific_name_idx;

DROP INDEX IF EXISTS sapin.taxon_accepted_name_idx;

DROP TABLE IF EXISTS sapin.tmp_taxon_scientific_name;

DROP TABLE IF EXISTS sapin.tmp_taxon_id_src_taxon_name_id_asso;
