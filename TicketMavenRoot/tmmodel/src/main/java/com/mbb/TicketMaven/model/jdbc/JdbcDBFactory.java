/*
 * #%L
 * tmmodel
 * %%
 * Copyright (C) 2019 Michael Berger
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

package com.mbb.TicketMaven.model.jdbc;

import com.mbb.TicketMaven.model.KeyedEntityDB;
import com.mbb.TicketMaven.model.entity.Customer;
import com.mbb.TicketMaven.model.entity.KeyedEntity;
import com.mbb.TicketMaven.model.entity.Layout;
import com.mbb.TicketMaven.model.entity.Reservation;
import com.mbb.TicketMaven.model.entity.Seat;
import com.mbb.TicketMaven.model.entity.Show;
import com.mbb.TicketMaven.model.entity.TMPackage;
import com.mbb.TicketMaven.model.entity.Table;
import com.mbb.TicketMaven.model.entity.Ticket;
import com.mbb.TicketMaven.model.entity.TicketRequest;
import com.mbb.TicketMaven.model.entity.Zone;

/**
 * A singleton instance which creates class-specific {@link KeyedEntityDB KeyedEntityDB}
 * instances.
 */
public class JdbcDBFactory {
	/**
	 * Singleton.
	 */
	public static JdbcDBFactory getInstance() {
		return instance;
	}

	public final KeyedEntityDB<?> create(Class<? extends KeyedEntity> cls)
			 {
		if (cls == Customer.class)
			return new CustomerJdbcDB();
		else if (cls == Seat.class)
			return new SeatJdbcDB();
		else if (cls == Show.class)
			return new ShowJdbcDB();
		else if (cls == TicketRequest.class)
			return new RequestJdbcDB();
		else if (cls == Ticket.class)
			return new TicketJdbcDB();
		else if (cls == Layout.class)
			return new LayoutJdbcDB();
		else if (cls == Zone.class)
			return new ZoneJdbcDB();
		else if (cls == Reservation.class)
			return new ReservationJdbcDB();
		else if (cls == Table.class)
			return new TableJdbcDB();
		else if (cls == TMPackage.class)
			return new PackageJdbcDB();

		

		throw new IllegalArgumentException(cls.getName());
	}

	// private //
	private static final JdbcDBFactory instance = new JdbcDBFactory();

	private JdbcDBFactory() {
		// empty
	}
}
