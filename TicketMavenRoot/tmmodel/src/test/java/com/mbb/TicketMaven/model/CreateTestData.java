
package com.mbb.TicketMaven.model;

import com.github.javafaker.Faker;
import com.mbb.TicketMaven.model.entity.*;
import com.mbb.TicketMaven.model.jdbc.DumpJdbcDB;
import com.mbb.TicketMaven.model.jdbc.JdbcDB;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class CreateTestData {
	

	public static void createDB() throws Exception {

		String dbdir = "jdbc:hsqldb:mem:whatever";
		JdbcDB.setDbUrl(dbdir);
		JdbcDB.connect(false);

		Faker faker = new Faker();

		ArrayList<Zone> zonelist = new ArrayList<Zone>();

		for (String zone : new String[] { "Mobility Impaired", "Hearing Impaired", "Lavatory", "Vision Impaired",
				"Front Row", "Lip Reader" }) {
			Zone bzone = ZoneModel.getReference().newRecord();
			bzone.setExclusive("N");
			bzone.setName(zone);
			ZoneModel.getReference().saveRecord(bzone);
			zonelist.add(bzone);
		}

		Zone bzone = ZoneModel.getReference().newRecord();
		bzone.setExclusive("Y");
		bzone.setName("Wheel Chair");
		ZoneModel.getReference().saveRecord(bzone);
		zonelist.add(bzone);

		for (int i = 1; i <= 200; i++) {
			Customer c = CustomerModel.getReference().newRecord();
			c.setFirstName(faker.name().firstName());
			c.setLastName(faker.name().lastName());
			c.setResident("Y");
			c.setAllowedTickets(2);
			c.setAddress(faker.address().fullAddress());
			c.setEmail(faker.internet().emailAddress());
			c.setPhone(faker.phoneNumber().cellPhone());
			c.setSpecialNeedsType(CustomerModel.NONE);

			if (i <= 50)
				c.setSpecialNeedsType(zonelist.get(i % zonelist.size()).getName());

			CustomerModel.getReference().saveRecord(c);

		}

		Layout l = LayoutModel.getReference().newRecord();
		l.setName("Full Auditorium");
		l.setSeating(LayoutModel.AUDITORIUM);
		l.setCenterSeat(1);
		l.setNumRows(15);
		l.setNumSeats(32);
		int layoutid = LayoutModel.getReference().saveRecord(l);
		
		SeatModel.generateMissingSeats(layoutid);

		for (int i = 1; i <= 10; i++) {

			Show s = ShowModel.getReference().newRecord();
			s.setDateTime(faker.date().future(100, 7, TimeUnit.DAYS));
			s.setLayout(layoutid);
			s.setName(faker.funnyName().name());
			s.setCost(i * 1000);
			s.setPrice(i * 100);
			int showid = ShowModel.getReference().saveRecord(s);

			for (Customer c : CustomerModel.getReference().getRecords()) {
				TicketRequest r = TicketRequestModel.getReference().newTicketRequest();
				r.setCustomerId(c.getKey());
				r.setShowId(showid);
				r.setTickets(2);
				TicketRequestModel.getReference().saveRecord(r);

			}
		}



	}

	public static void main(String args[]) throws Exception {
			createDB();

		String sql = DumpJdbcDB.dumpData();

		System.out.println(sql);

		try {
			JdbcDB.cleanup();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
