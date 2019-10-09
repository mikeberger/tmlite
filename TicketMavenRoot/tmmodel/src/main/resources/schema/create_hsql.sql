---
-- #%L
-- tmmodel
-- %%
-- Copyright (C) 2019 Michael Berger
-- %%
-- This program is free software: you can redistribute it and/or modify
-- it under the terms of the GNU General Public License as
-- published by the Free Software Foundation, either version 3 of the 
-- License, or (at your option) any later version.
-- 
-- This program is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU General Public License for more details.
-- 
-- You should have received a copy of the GNU General Public 
-- License along with this program.  If not, see
-- <http://www.gnu.org/licenses/gpl-3.0.html>.
-- #L%
---

CREATE CACHED TABLE customers (
  first_name varchar(40) default '' NOT NULL,
  last_name varchar(40)default ''  NOT NULL ,
  record_id IDENTITY,
  email varchar(50) default '',
  phone varchar(20) default '',
  notes varchar(100) default '',
  allowed_tickets integer  default '2' NOT NULL ,
  special_needs varchar(50) default '',
  total_tickets integer  default '0' NOT NULL ,
  total_quality integer  default '0' NOT NULL ,
  address varchar(100) default '',
  resident varchar(1) default 'Y' NOT NULL ,
  PRIMARY KEY  (record_id),
  UNIQUE (first_name,last_name)
);

CREATE CACHED TABLE layouts (
	record_id IDENTITY,
	name varchar(50),
	centerseat integer default '0' NOT NULL,
	num_rows integer default '0' NOT NULL,
	num_seats integer default '0' NOT NULL,
	seating varchar(15) default '' NOT NULL,
	PRIMARY KEY (record_id),
	UNIQUE(name)
);

CREATE CACHED TABLE shows (
  record_id IDENTITY,
  name varchar(100) default '' NOT NULL,
  time datetime default '1000-01-01 00:00:00' NOT NULL,
  price integer  default '0' NOT NULL,
  layout integer default '1' NOT NULL,
  cost integer  default '0' NOT NULL,
  format longvarchar,
  PRIMARY KEY  (record_id),
  FOREIGN KEY (layout) REFERENCES layouts (record_id)
);

CREATE CACHED TABLE packages (
  record_id IDENTITY,
  name varchar(100) default '' NOT NULL,
  price integer  default '0' NOT NULL,
  PRIMARY KEY  (record_id)
);

CREATE CACHED TABLE pkgshows (
  pkg_id integer default '0' NOT NULL,
  show_id integer default '0' NOT NULL,
  FOREIGN KEY (pkg_id) REFERENCES packages (record_id),
  FOREIGN KEY (show_id) REFERENCES shows (record_id),
  PRIMARY KEY  (pkg_id,show_id)
);
CREATE INDEX pkgs1 ON pkgshows (pkg_id);

CREATE CACHED TABLE requests (
  record_id IDENTITY,
  customer_id integer default '0' not null,
  show_id integer  default '0' NOT NULL,
  tickets integer default '0' NOT NULL,
  discount double,
  PRIMARY KEY  (record_id),
  UNIQUE (show_id,customer_id),
 FOREIGN KEY (customer_id) REFERENCES customers (record_id),
 FOREIGN KEY (show_id) REFERENCES shows (record_id)
);

CREATE CACHED TABLE zones (
  record_id IDENTITY,
  name varchar(50) NOT NULL,
  exclusive varchar(1) default 'N' NOT NULL,
  PRIMARY KEY  (record_id),
  UNIQUE (name)
);


CREATE CACHED TABLE seats (
  record_id IDENTITY,
  row varchar(10) default '' NOT NULL,
  seat integer  default '0' NOT NULL,
  weight integer default '15' NOT NULL,
  "END" varchar(10) default 'No' NOT NULL,
  available varchar(10) default 'Y' NOT NULL,
  layout integer default '1' NOT NULL,
  zone integer,
  label varchar(10),
  PRIMARY KEY  (record_id),
  UNIQUE  (row,seat,layout),
  FOREIGN KEY (layout) REFERENCES layouts (record_id),
  FOREIGN KEY (zone) REFERENCES zones (record_id)
);

CREATE CACHED TABLE tickets (
  record_id IDENTITY,
  customer_id integer  default '0' NOT NULL,
  show_id integer default '0'  NOT NULL,
  seat_id integer  default '0' NOT NULL,
  ticket_price integer,
  PRIMARY KEY  (record_id),
  UNIQUE (show_id,seat_id),
  FOREIGN KEY (customer_id) REFERENCES customers (record_id),
  FOREIGN KEY (show_id) REFERENCES shows (record_id),
  FOREIGN KEY (seat_id) REFERENCES seats (record_id)
);

CREATE CACHED TABLE tmtables (
  record_id IDENTITY,
  seats integer default '0' NOT NULL,
  layout integer default '0'  NOT NULL,
  xpos integer default '0' NOT NULL ,
  ypos integer default '0' NOT NULL ,
  width integer default '0' NOT NULL,
  height integer default '0' NOT NULL,
  label varchar(30) default '',
  tbltype varchar(5) default '',
  PRIMARY KEY  (record_id),
  FOREIGN KEY (layout) REFERENCES layouts (record_id)
);

CREATE CACHED TABLE reservations (
  record_id IDENTITY,
  customer_id integer  default '0' NOT NULL,
  show_id integer default '0'  NOT NULL,
  table_id integer default '0'  NOT NULL,
  num integer default '1' NOT NULL,
  payment varchar(50) default '',
  amount varchar(10) default '',
  notes varchar(400) default '',
  PRIMARY KEY  (record_id),
  UNIQUE (customer_id,show_id,table_id),
  FOREIGN KEY (show_id) REFERENCES shows (record_id),
  FOREIGN KEY (customer_id) REFERENCES customers (record_id),
  FOREIGN KEY (table_id) REFERENCES tmtables (record_id)
);


