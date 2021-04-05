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
INSERT INTO users (id, name, email, password, otp) VALUES ('486590a3-fcc1-4657-a9ed-5f0f95dadea7','Unknown', 'unknown@email.com', 'password', '');

-- Group with a vote and a voting period of 1 hour
INSERT INTO groups (id, name, anonymous_vote, voting_time, voting_days, voting_duration) VALUES ('d64db962-3455-11e9-b210-d663bd873d93','Fantastic Four', true, now(),  '{"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"}', 1);
INSERT INTO users_groups (group_id, user_id, is_admin) VALUES ('d64db962-3455-11e9-b210-d663bd873d93','486590a3-fcc1-4657-a9ed-5f0f95dadea6', 'f');

INSERT INTO voting (id, group_id, voting_stats_id, created_at, created_by) VALUES ('953951f9-3f6f-421e-a12c-270cfcabb2d0', 'd64db962-3455-11e9-b210-d663bd873d93', 'b3576bc7-2cb4-4680-9445-bda0bc615238', now() + interval '1 minute', '486590a3-fcc1-4657-a9ed-5f0f95dadea6');
INSERT INTO voting_stats (id, voting_id, created_at, average) VALUES ('b3576bc7-2cb4-4680-9445-bda0bc615238', '953951f9-3f6f-421e-a12c-270cfcabb2d0', now() + interval '1 minute', 3);
INSERT INTO vote (id, voting_id, created_at, created_by, comment, score) VALUES ('d246d65c-be84-4140-85e1-9cf495523730', '953951f9-3f6f-421e-a12c-270cfcabb2d0', now() + interval '1 minute', '486590a3-fcc1-4657-a9ed-5f0f95dadea6', 'Ut sit labore eius.', 3);

-- Group with a vote and a voting period of 24 hour
INSERT INTO groups (id, name, anonymous_vote, voting_time, voting_days, voting_duration) VALUES ('d64db962-3455-11e9-b210-d663bd873d94','Avengers', true, time with time zone '00:00:00.146512+01:00', '{"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"}', 24);
INSERT INTO users_groups (group_id, user_id, is_admin) VALUES ('d64db962-3455-11e9-b210-d663bd873d94','486590a3-fcc1-4657-a9ed-5f0f95dadea6', 'f');

INSERT INTO voting (id, group_id, voting_stats_id, created_at, created_by) VALUES ('7772e35c-5a87-4ba3-ab93-da8a957036fd', 'd64db962-3455-11e9-b210-d663bd873d94', '612b08c5-b9e5-4490-a17a-54d3d56def5f', now(), '486590a3-fcc1-4657-a9ed-5f0f95dadea6');
INSERT INTO voting_stats (id, voting_id, created_at, average) VALUES ('612b08c5-b9e5-4490-a17a-54d3d56def5f', '7772e35c-5a87-4ba3-ab93-da8a957036fd', now() + interval '5 minutes', 3);
INSERT INTO vote (id, voting_id, created_at, created_by, comment, score) VALUES ('d246d65c-be84-4140-85e1-9cf495523731', '7772e35c-5a87-4ba3-ab93-da8a957036fd', now() + interval '5 minutes', '486590a3-fcc1-4657-a9ed-5f0f95dadea6', 'Ut sit labore eius.', 3);