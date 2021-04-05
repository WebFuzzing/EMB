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

--  Populates `moving_average` field from `voting_stats` calculating the data from its `voting` data

DO $$
DECLARE
    gid uuid;
BEGIN
    FOR gid IN SELECT * FROM "groups" LOOP
      update
          voting_stats
      set
        moving_average = subquery.moving_average_60
      from
        (
        select
            vs.id as voting_stats_id,
            average,
            avg(vs.average) over(
            order by vs.created_at rows between 59 preceding and current row) as moving_average_60
        from
            voting_stats vs
        join voting v on
            vs.voting_id = v.id
        join "groups" g on
            v.group_id = g.id
        where
            vs.average is not null
            and voting_id in (select id from voting where group_id = gid)
          ) subquery
      where
        id = subquery.voting_stats_id;
    END LOOP;
END $$;
