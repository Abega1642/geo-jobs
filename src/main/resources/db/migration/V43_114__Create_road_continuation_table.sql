CREATE TYPE process_status AS ENUM ('PROCESSING', 'CONTINUED');

CREATE TABLE IF NOT EXISTS road_continuation
(
    rc_id                 VARCHAR PRIMARY KEY,
    original_geojson_path TEXT           NOT NULL,
    bucket_key            TEXT,
    image_zoom            INTEGER        NOT NULL,
    image_size            INTEGER        NOT NULL,
    status                process_status NOT NULL DEFAULT 'PROCESSING'
);
