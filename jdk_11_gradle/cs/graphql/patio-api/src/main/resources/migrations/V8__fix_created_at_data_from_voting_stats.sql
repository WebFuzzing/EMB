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

--  Populates `create_at` field from voting_stats, taking the last vote from each `voting`

UPDATE
  voting_stats
SET
  created_at = x.created_at
FROM
    (
    SELECT
        vs.id AS id,
        v.created_at
    FROM
        voting v
      JOIN voting_stats vs ON
        vs.voting_id = v.id
    ) x
WHERE
  voting_stats.id = x.id
