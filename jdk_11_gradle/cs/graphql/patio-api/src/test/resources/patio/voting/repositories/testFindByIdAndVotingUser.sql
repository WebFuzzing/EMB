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

INSERT INTO groups (id, name, anonymous_vote, voting_time, voting_days, voting_duration) VALUES ('d64db962-3455-11e9-b210-d663bd873d93','Fantastic Four', true, time with time zone '10:48:12.146512+01:00', '{"MONDAY"}', 24);
INSERT INTO users_groups (group_id, user_id, is_admin) VALUES ('d64db962-3455-11e9-b210-d663bd873d93','486590a3-fcc1-4657-a9ed-5f0f95dadea6', 'f');
INSERT INTO voting (id, group_id, voting_stats_id, created_at, created_by) VALUES ('7772e35c-5a87-4ba3-ab93-da8a957037fd', 'd64db962-3455-11e9-b210-d663bd873d93', 'b3576bc7-2cb4-4680-9445-bda0bc615238', '2020-05-04T10:15:30+01:00', '486590a3-fcc1-4657-a9ed-5f0f95dadea6');
INSERT INTO voting_stats (id, voting_id, created_at, average) VALUES ('b3576bc7-2cb4-4680-9445-bda0bc615238', '7772e35c-5a87-4ba3-ab93-da8a957037fd', now(), 3);
INSERT INTO vote (id, voting_id, created_at, created_by, comment, score) VALUES ('d246d65c-be84-4140-85e1-9cf495523730', '7772e35c-5a87-4ba3-ab93-da8a957037fd', now() - interval '5 day', '486590a3-fcc1-4657-a9ed-5f0f95dadea6', 'Ut sit labore eius.', 3);