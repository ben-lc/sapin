UPDATE sapin.natural_area na1
SET
  domain = CASE
    WHEN (
      EXISTS (
        SELECT
          1
        FROM
          sapin.location
        WHERE
          ST_Intersects (location.geom, na1.geom)
          AND level = 1
      )
    ) THEN 'TERRESTRIAL'::sapin.natural_area_domain_enum
    ELSE 'MARINE'::sapin.natural_area_domain_enum
  END;

ALTER TABLE sapin.natural_area
ALTER COLUMN domain
SET NOT NULL;
