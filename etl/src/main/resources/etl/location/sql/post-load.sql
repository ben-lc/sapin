UPDATE sapin.location l1
SET
  parent_loc_id = (
    SELECT
      loc_id
    FROM
      sapin.location l2
    WHERE
      l2.src_loc_id = l1.src_parent_loc_id
  );

UPDATE sapin.location t1
SET
  tree_path = (
    SELECT
      string_agg(t.loc_id::varchar, '.')::ltree
    FROM
      (
        WITH RECURSIVE
          loc_id_plus_parent (loc_id) AS (
            SELECT
              loc_id,
              parent_loc_id
            FROM
              sapin.location t2
            WHERE
              t2.loc_id = t1.loc_id
            UNION
            SELECT
              t3.loc_id,
              t3.parent_loc_id
            FROM
              sapin.location t3
              JOIN loc_id_plus_parent ch ON ch.parent_loc_id = t3.loc_id
          )
        SELECT
          loc_id,
          row_number() OVER () AS rnum
        FROM
          loc_id_plus_parent
        ORDER BY
          rnum DESC
      ) t
  );

ALTER TABLE sapin.location
DROP COLUMN src_loc_id;

ALTER TABLE sapin.location
DROP COLUMN src_parent_loc_id;
