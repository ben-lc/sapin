-- delete temporary table and indexes
DROP INDEX IF EXISTS sapin.taxon_scientific_name_name_idx;

DROP INDEX IF EXISTS sapin.taxon_accepted_name_idx;

DROP TABLE IF EXISTS sapin.tmp_taxon_scientific_name;

DROP TABLE IF EXISTS sapin.tmp_taxon_id_src_id_asso;

-- post processing of taxon distribution
DELETE FROM sapin.tmp_taxon_distribution
WHERE
  taxon_id IS NULL;

INSERT INTO
  sapin.location_taxon_asso (loc_id, taxon_id)
SELECT
  loc_id,
  taxon_id
FROM
  (
    SELECT
      coalesce(
        (
          SELECT
            loc_id
          FROM
            sapin.tmp_location_taxon_distribution_mapping
          WHERE
            string_to_array(location_id, '|') && string_to_array(location_id_in, ',')
        ),
        (
          SELECT
            loc_id
          FROM
            sapin.tmp_location_taxon_distribution_mapping
          WHERE
            location_id IS NULL
            AND location_name @@ to_tsquery('simple', location_name_contains)
        )
      ) AS loc_id,
      taxon_id
    FROM
      sapin.tmp_taxon_distribution
  ) t
WHERE
  t.loc_id IS NOT null
ON CONFLICT DO NOTHING;

DROP TABLE IF EXISTS sapin.tmp_location_taxon_distribution_mapping;

DROP TABLE IF EXISTS sapin.tmp_taxon_distribution;
