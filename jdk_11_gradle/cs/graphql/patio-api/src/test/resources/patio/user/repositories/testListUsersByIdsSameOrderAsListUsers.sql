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
INSERT INTO users (id, name, email, password, otp) VALUES ('c2a771bc-f8c5-4112-a440-c80fa4c8e382','Ben Grim', 'bgrim@email.com', 'password', '');
INSERT INTO users (id, name, email, password, otp) VALUES ('84d48a35-7659-4710-ad13-4c47785a0e9d','Johnny Storm', 'jstorm@email.com', 'password', '');
INSERT INTO users (id, name, email, password, otp) VALUES ('1998c588-d93b-4db6-92e2-a9dbb4cf03b5','Steve Rogers', 'srogers@email.com', 'password', '');
INSERT INTO users (id, name, email, password, otp) VALUES ('3465094c-5545-4007-a7bc-da2b1a88d9dc','Tony Stark', 'tstark@email.com', 'password', '');

INSERT INTO groups (id, name, anonymous_vote, voting_time, voting_days, voting_duration) VALUES ('d64db962-3455-11e9-b210-d663bd873d93','Fantastic Four', time with time zone '10:48:12.146512+01:00', '{"MONDAY"}', 24);

INSERT INTO users_groups (group_id, user_id, is_admin) VALUES ('d64db962-3455-11e9-b210-d663bd873d93','486590a3-fcc1-4657-a9ed-5f0f95dadea6', 't');
INSERT INTO users_groups (group_id, user_id, is_admin) VALUES ('d64db962-3455-11e9-b210-d663bd873d93','c2a771bc-f8c5-4112-a440-c80fa4c8e382', 'f');
INSERT INTO users_groups (group_id, user_id, is_admin) VALUES ('d64db962-3455-11e9-b210-d663bd873d93','84d48a35-7659-4710-ad13-4c47785a0e9d', 'f');
INSERT INTO users_groups (group_id, user_id, is_admin) VALUES ('d64db962-3455-11e9-b210-d663bd873d93','1998c588-d93b-4db6-92e2-a9dbb4cf03b5', 'f');
INSERT INTO users_groups (group_id, user_id, is_admin) VALUES ('d64db962-3455-11e9-b210-d663bd873d93','3465094c-5545-4007-a7bc-da2b1a88d9dc', 'f');

INSERT INTO voting (id, group_id, created_at, created_by, average) VALUES ('7772e35c-5a87-4ba3-ab93-da8a957037fd', 'd64db962-3455-11e9-b210-d663bd873d93', '2020-05-04T10:15:30+01:00', '486590a3-fcc1-4657-a9ed-5f0f95dadea6', 3);
INSERT INTO vote (id, voting_id, created_at, created_by, comment, score) VALUES ('c6d28e58-fffa-40be-b84a-2eb62d0c04e9', '7772e35c-5a87-4ba3-ab93-da8a957037fd', now() - interval '5 day', '486590a3-fcc1-4657-a9ed-5f0f95dadea6', 'Ut numquam tempora velit sit.', 3);
INSERT INTO vote (id, voting_id, created_at, created_by, comment, score) VALUES ('d246d65c-be84-4140-85e1-9cf495523727', '7772e35c-5a87-4ba3-ab93-da8a957037fd', now() - interval '5 day', 'c2a771bc-f8c5-4112-a440-c80fa4c8e382', 'Ut sit labore eius.', 3);
INSERT INTO vote (id, voting_id, created_at, created_by, comment, score) VALUES ('d246d65c-be84-4140-85e1-9cf495523728', '7772e35c-5a87-4ba3-ab93-da8a957037fd', now() - interval '5 day', '84d48a35-7659-4710-ad13-4c47785a0e9d', 'Ut sit labore eius.', 3);
INSERT INTO vote (id, voting_id, created_at, created_by, comment, score) VALUES ('d246d65c-be84-4140-85e1-9cf495523729', '7772e35c-5a87-4ba3-ab93-da8a957037fd', now() - interval '5 day', '1998c588-d93b-4db6-92e2-a9dbb4cf03b5', 'Ut sit labore eius.', 3);
INSERT INTO vote (id, voting_id, created_at, created_by, comment, score) VALUES ('d246d65c-be84-4140-85e1-9cf495523730', '7772e35c-5a87-4ba3-ab93-da8a957037fd', now() - interval '5 day', '3465094c-5545-4007-a7bc-da2b1a88d9dc', 'Ut sit labore eius.', 3);
