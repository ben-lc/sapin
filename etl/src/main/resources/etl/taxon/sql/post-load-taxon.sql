-- set parent_taxon_id column
-- since there can be duplicates of taxons in original dataset
UPDATE sapin.taxon t1
SET
  parent_taxon_id = (
    SELECT
      t2.taxon_id
    FROM
      sapin.tmp_taxon_scientific_name ttsn1
      JOIN sapin.tmp_taxon_scientific_name ttsn2 ON ttsn1.src_parent_name_id = ttsn2.src_taxon_name_id
      JOIN sapin.taxon t2 ON ttsn2.scientific_name = t2.accepted_name
    WHERE
      ttsn1.src_taxon_name_id = t1.src_taxon_name_id
    ORDER BY
      ttsn2.taxonomic_status
    LIMIT
      1
  );

-- set tree_path column by concatenation of taxon_id and its parent ones
UPDATE sapin.taxon t1
SET
  tree_path = (
    SELECT
      string_agg(t.taxon_id::varchar, '.')::ltree
    FROM
      (
        WITH RECURSIVE
          taxon_id_plus_parent (taxon_id) AS (
            SELECT
              taxon_id,
              parent_taxon_id
            FROM
              sapin.taxon t2
            WHERE
              t2.taxon_id = t1.taxon_id
            UNION
            SELECT
              t3.taxon_id,
              t3.parent_taxon_id
            FROM
              sapin.taxon t3
              JOIN taxon_id_plus_parent ch ON ch.parent_taxon_id = t3.taxon_id
          )
        SELECT
          taxon_id,
          row_number() OVER () AS rnum
        FROM
          taxon_id_plus_parent
        ORDER BY
          rnum DESC
      ) t
  );

-- set taxon_id column of taxon_scientific_name table
UPDATE sapin.taxon_scientific_name tsn
SET
  taxon_id = (
    SELECT
      t.taxon_id
    FROM
      sapin.tmp_taxon_scientific_name ttsn1
      JOIN sapin.tmp_taxon_scientific_name ttsn2 ON ttsn1.src_accepted_name_id = ttsn2.src_taxon_name_id
      JOIN sapin.taxon t ON ttsn2.scientific_name = t.accepted_name
    WHERE
      ttsn1.src_taxon_name_id = tsn.src_taxon_name_id
  )
WHERE
  taxonomic_status = 'SYNONYM';

UPDATE sapin.taxon_scientific_name tsn
SET
  taxon_id = (
    SELECT
      taxon_id
    FROM
      sapin.taxon t
    WHERE
      tsn.src_taxon_name_id = t.src_taxon_name_id
  )
WHERE
  taxonomic_status != 'SYNONYM';

-- create temporary table to keep mapping between src_taxon_name_id and toxon_id
CREATE TABLE
  sapin.tmp_taxon_id_src_taxon_name_id_asso AS (
    SELECT
      taxon_id,
      ttsn.src_taxon_name_id
    FROM
      sapin.taxon_scientific_name
      JOIN sapin.tmp_taxon_scientific_name ttsn USING (scientific_name)
  );

ALTER TABLE sapin.tmp_taxon_id_src_taxon_name_id_asso
ADD PRIMARY KEY (src_taxon_name_id, taxon_id);

-- set accepted name of taxon_scientific_name table
UPDATE sapin.taxon_scientific_name tsn1
SET
  accepted_name_id = (
    SELECT
      tsn2.taxon_name_id
    FROM
      sapin.tmp_taxon_scientific_name ttsn1
      JOIN sapin.tmp_taxon_scientific_name ttsn2 ON ttsn1.src_accepted_name_id = ttsn2.src_taxon_name_id
      JOIN sapin.taxon_scientific_name tsn2 ON ttsn2.scientific_name = tsn2.scientific_name
    WHERE
      ttsn1.src_taxon_name_id = tsn1.src_taxon_name_id
  );

-- set back constraints on tables
ALTER TABLE sapin.taxon
ADD CONSTRAINT taxon_parent_taxon_id_fkey FOREIGN KEY (parent_taxon_id) REFERENCES sapin.taxon (taxon_id);

ALTER TABLE sapin.taxon_scientific_name
ALTER COLUMN taxon_id
SET NOT NULL;

ALTER TABLE sapin.taxon_scientific_name
ADD CONSTRAINT taxon_scientific_name_taxon_id_fkey FOREIGN KEY (taxon_id) REFERENCES sapin.taxon (taxon_id);

ALTER TABLE sapin.taxon_scientific_name
ADD CONSTRAINT taxon_scientific_name_accepted_name_id_fkey FOREIGN KEY (accepted_name_id) REFERENCES sapin.taxon_scientific_name (taxon_name_id);
