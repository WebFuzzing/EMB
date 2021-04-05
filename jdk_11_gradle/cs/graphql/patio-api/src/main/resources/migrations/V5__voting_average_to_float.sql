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

ALTER TABLE voting ALTER COLUMN average type DECIMAL(10,2);

UPDATE voting
SET average = subquery.average
FROM (
  SELECT AVG(vote.score) AS average, voting.id AS voting_id
  FROM vote, voting
  WHERE voting.id = vote.voting_id
  GROUP BY voting.id
) AS subquery
WHERE subquery.voting_id = id;