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

CREATE TABLE IF NOT EXISTS voting (
  id UUID PRIMARY KEY,
  group_id UUID NOT NULL,
  created_at timestamp with time zone NOT NULL,
  created_by UUID,
  average int,
  FOREIGN KEY (created_by) REFERENCES users(id),
  FOREIGN KEY (group_id) REFERENCES groups(id)
);

CREATE TABLE IF NOT EXISTS vote (
  id UUID PRIMARY KEY,
  voting_id UUID NOT NULL,
  created_by UUID,
  created_at timestamp with time zone NOT NULL,
  comment text,
  score int NOT NULL,
  FOREIGN KEY (created_by) REFERENCES users(id),
  FOREIGN KEY (voting_id) REFERENCES voting(id)
);