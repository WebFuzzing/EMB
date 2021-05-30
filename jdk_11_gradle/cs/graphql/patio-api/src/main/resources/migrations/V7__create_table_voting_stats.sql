--
-- Copyright (C) 2019 Kaleidos Open Source SL
--
-- This file is part of PATIO.
-- PATIO is free software: you can redistribute it and/or modify
-- it under the terms of the GNU General Public License as published by
-- the Free Software Foundation, either version 3 of the License, or
-- (at your option) any later version.
--
-- PATIO is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU General Public License for more details.
--
-- You should have received a copy of the GNU General Public License
-- along with PATIO.  If not, see <https://www.gnu.org/licenses/>
--

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS voting_stats (
  id UUID NOT NULL PRIMARY KEY,
  voting_id UUID NOT NULL,
  created_at timestamp with time zone NOT NULL,
  average DECIMAL(10,2) NULL,
  moving_average DECIMAL(10,2) NULL,
  FOREIGN KEY (voting_id) REFERENCES voting(id) ON DELETE CASCADE
);

INSERT INTO voting_stats (id, voting_id, average, created_at)
    SELECT
      uuid_generate_v4(),
      voting.id AS voting_id,
      AVG(vote.score) AS average,
      now()
    FROM vote RIGHT OUTER JOIN voting ON
      voting.id = vote.voting_id
    GROUP BY voting.id;

ALTER TABLE voting
  ADD COLUMN voting_stats_id UUID NULL,
  ADD CONSTRAINT voting_stats_id
  FOREIGN KEY (voting_stats_id) REFERENCES voting_stats(id) DEFERRABLE INITIALLY DEFERRED,
  DROP COLUMN average;

UPDATE voting
SET voting_stats_id = vs.id
FROM voting_stats vs
WHERE voting.id = vs.voting_id;


