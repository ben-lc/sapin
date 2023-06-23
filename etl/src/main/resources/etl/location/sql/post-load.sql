UPDATE sapin.location l1
SET
  parent_id = (
    SELECT
      id
    FROM
      sapin.location l2
    WHERE
      l2.src_id = l1.src_parent_id
  );

UPDATE sapin.location t1
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
              sapin.location t2
            WHERE
              t2.id = t1.id
            UNION
            SELECT
              t3.id,
              t3.parent_id
            FROM
              sapin.location t3
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
  );

ALTER TABLE sapin.location
DROP COLUMN src_id;

ALTER TABLE sapin.location
DROP COLUMN src_parent_id;
