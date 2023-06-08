ALTER TABLE sapin.location
ADD COLUMN src_loc_id text NOT NULL;

ALTER TABLE sapin.location
ADD COLUMN src_parent_loc_id text;
