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

INSERT INTO users (id, name, email, password, otp) VALUES ('486590a3-fcc1-4657-a9ed-5f0f95dadea6','Sue Storm', 'sstorm@email.com', 'password', '');

-- Voting times are open from midnight, all day long
INSERT INTO groups (id, name, anonymous_vote, voting_time, voting_days, voting_duration) VALUES ('d64db962-3455-11e9-b210-d663bd873d93','Fantastic Four', true, time with time zone '00:00:00.146512+01:00', '{"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"}', 24);
INSERT INTO groups (id, name, anonymous_vote, voting_time, voting_days, voting_duration) VALUES ('d64db962-3455-11e9-b210-d663bd873d94','Avengers', true, time with time zone '00:00:00.146512+01:00', '{"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"}', 24);

INSERT INTO users_groups (group_id, user_id, is_admin) VALUES ('d64db962-3455-11e9-b210-d663bd873d93','486590a3-fcc1-4657-a9ed-5f0f95dadea6', 'f');
INSERT INTO users_groups (group_id, user_id, is_admin) VALUES ('d64db962-3455-11e9-b210-d663bd873d94','486590a3-fcc1-4657-a9ed-5f0f95dadea6', 'f');
