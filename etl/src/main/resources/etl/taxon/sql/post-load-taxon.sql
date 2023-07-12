-- set parent_id column
-- since there can be duplicates of taxa in original dataset
UPDATE sapin.taxon t1
SET
  parent_id = (
    SELECT
      t2.id
    FROM
      sapin.tmp_taxon_scientific_name ttsn1
      JOIN sapin.tmp_taxon_scientific_name ttsn2 ON ttsn1.src_parent_id = ttsn2.src_id
      JOIN sapin.taxon t2 ON ttsn2.name = t2.accepted_name
    WHERE
      ttsn1.src_id = t1.src_name_id
    ORDER BY
      ttsn2.taxonomic_status
    LIMIT
      1
  )
WHERE
  parent_id IS NULL;

COMMIT;

-- set tree_path column by concatenation of id and its parent ones
UPDATE sapin.taxon t1
SET
  tree_path = (
    SELECT
      string_agg(t.id::varchar, '.')::ltree
    FROM
      (
        WITH RECURSIVE
          id_plus_parent (id) AS (
            SELECT
              id,
              parent_id
            FROM
              sapin.taxon t2
            WHERE
              t2.id = t1.id
            UNION
            SELECT
              t3.id,
              t3.parent_id
            FROM
              sapin.taxon t3
              JOIN id_plus_parent ch ON ch.parent_id = t3.id
          )
        SELECT
          id,
          row_number() OVER () AS rnum
        FROM
          id_plus_parent
        ORDER BY
          rnum DESC
      ) t
  )
WHERE
  tree_path IS NULL;

COMMIT;

-- set id column of taxon_scientific_name table
UPDATE sapin.taxon_scientific_name tsn
SET
  taxon_id = (
    SELECT
      t.id
    FROM
      sapin.tmp_taxon_scientific_name ttsn1
      JOIN sapin.tmp_taxon_scientific_name ttsn2 ON ttsn1.src_accepted_name_id = ttsn2.src_id
      JOIN sapin.taxon t ON ttsn2.name = t.accepted_name
    WHERE
      ttsn1.src_id = tsn.src_id
  )
WHERE
  taxonomic_status = 'SYNONYM'
  AND taxon_id IS NULL;

COMMIT;

UPDATE sapin.taxon_scientific_name tsn
SET
  taxon_id = (
    SELECT
      id
    FROM
      sapin.taxon t
    WHERE
      tsn.src_id = t.src_name_id
  )
WHERE
  taxonomic_status != 'SYNONYM'
  AND taxon_id IS NULL;

COMMIT;

-- set accepted name of taxon_scientific_name table
UPDATE sapin.taxon_scientific_name tsn1
SET
  accepted_name_id = (
    SELECT
      tsn2.id
    FROM
      sapin.tmp_taxon_scientific_name ttsn1
      JOIN sapin.tmp_taxon_scientific_name ttsn2 ON ttsn1.src_accepted_name_id = ttsn2.src_id
      JOIN sapin.taxon_scientific_name tsn2 ON ttsn2.name = tsn2.name
    WHERE
      ttsn1.src_id = tsn1.src_id
  )
WHERE
  accepted_name_id IS NULL;

COMMIT;

-- create temporary table to keep mapping between src_taxon_name_id and id
CREATE TABLE IF NOT EXISTS
  sapin.tmp_taxon_id_src_id_asso AS (
    SELECT
      taxon_id,
      ttsn.src_id,
      ttsn.rank
    FROM
      sapin.taxon_scientific_name
      JOIN sapin.tmp_taxon_scientific_name ttsn USING (name)
  );

ALTER TABLE sapin.tmp_taxon_id_src_id_asso
ADD PRIMARY KEY (src_id, taxon_id);

COMMIT;

-- set back constraints on tables
ALTER TABLE sapin.taxon
ADD CONSTRAINT taxon_parent_id_fkey FOREIGN KEY (parent_id) REFERENCES sapin.taxon (id);

ALTER TABLE sapin.taxon_scientific_name
ALTER COLUMN id
SET NOT NULL;

ALTER TABLE sapin.taxon_scientific_name
ADD CONSTRAINT taxon_scientific_name_taxon_id_fkey FOREIGN KEY (taxon_id) REFERENCES sapin.taxon (id);

ALTER TABLE sapin.taxon_scientific_name
ADD CONSTRAINT taxon_scientific_name_accepted_name_id_fkey FOREIGN KEY (accepted_name_id) REFERENCES sapin.taxon_scientific_name (id);
