CREATE TABLE public_group
(
    id integer NOT NULL,
    type character(1) NOT NULL,
    order integer,
    has_childrem boolean DEFAULT false,
    parent_id integer null,
    access_hash bigint NOT NULL,
    CONSTRAINT public_groups_pkey PRIMARY KEY (id)
)