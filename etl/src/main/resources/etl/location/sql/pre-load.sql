ALTER TABLE sapin.location
ADD COLUMN src_id text NOT NULL;

ALTER TABLE sapin.location
ADD COLUMN src_parent_id text;
