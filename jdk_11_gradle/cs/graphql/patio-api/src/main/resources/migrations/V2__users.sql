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

CREATE TABLE IF NOT EXISTS users (
  id UUID PRIMARY KEY,
  name varchar(200) NOT NULL,
  email varchar(200) NOT NULL,
  password varchar(200),
  otp varchar(200) NULL
);


CREATE TABLE IF NOT EXISTS users_groups (
  user_id UUID,
  group_id UUID,
  is_admin boolean NOT NULL DEFAULT false,
  CONSTRAINT PK_Users_Groups PRIMARY KEY (user_id, group_id),
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (group_id) REFERENCES groups(id)
);