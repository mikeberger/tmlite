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
package com.mbb.TicketMaven.model;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mbb.TicketMaven.model.entity.Customer;
import com.mbb.TicketMaven.model.entity.Seat;
import com.mbb.TicketMaven.model.entity.Show;
import com.mbb.TicketMaven.model.entity.Ticket;
import com.mbb.TicketMaven.model.entity.TicketRequest;
import com.mbb.TicketMaven.model.entity.Zone;
import com.mbb.TicketMaven.model.jdbc.JdbcDB;
import com.mbb.TicketMaven.util.PrefName;
import com.mbb.TicketMaven.util.Prefs;

/**
 * This class runs the actual lotteries
 */

public class LotteryManager {

	static private final Logger log = Logger.getLogger("com.mbb.TicketMaven");

	private static int center_ = 0; // cached center seat for compare functions
	// to avoid expensive lookup during sorting

	// the show that this instance is dealing with
	private int show_id;

	// if set to false, aisle requests do not get assigned BEFORE the general
	// public
	// instead they get assigned with the general public so that large Aisle
	// parties don't hog the front rows
	// in certain layouts
	private boolean favor_aisle_requests = true;

	public boolean isFavor_aisle_requests() {
		return favor_aisle_requests;
	}

	public void setFavor_aisle_requests(boolean favor_aisle_requests) {
		this.favor_aisle_requests = favor_aisle_requests;
	}

	/**
	 * Instantiates a new lottery manager for a particular show.
	 *
	 * @param show
	 *            the show key
	 */
	public LotteryManager(int show) {
		show_id = show;
	}

	/**
	 * Run the lottery.
	 *
	 * @throws Exception
	 *             the exception
	 */
	public void runLottery() throws Exception {

		Collection<Seat> seats = SeatModel.getReference().getAvailableSeatsForShow(show_id);
		runLottery(seats.size());

		Collection<TicketRequest> reqs = TicketRequestModel.getReference().getRequestsForShow(show_id);

		// there are still outstanding requests. run another lottery with no capacity limit so that every request will
		// be considered. This may fill in the remaining seats
		if (!reqs.isEmpty()) {
			runLottery(-1);
		}

	}

	/**
	 * Run the lottery.
	 *
	 * @throws Exception
	 *             the exception
	 */
	private void runLottery(int capacity) throws Exception {


		try {

			favor_aisle_requests = Prefs.is(PrefName.FAVOR_AISLE_REQUESTS, "true");

			// look up the center seat value for later
			center_ = LayoutModel.getCenter(show_id);

			// start a transaction in case we have to roll it all back
			JdbcDB.startTransaction();

			log.fine("Running Lottery for show " + show_id + " capacity=" + capacity);

			// get the list of available seats for the show
			Collection<Seat> availableSeats = SeatModel.getReference().getAvailableSeatsForShow(show_id);

			dumpSeats("initial AVAIL", availableSeats);

			// get the list of requests
			Collection<TicketRequest> requests = getRequests(capacity);

			// sort Requests in the order that they should be honored
			// requests are sorted by past quality plus a random factor for
			// people whi have never gotten tickets.
			// We will eventually assign tickets from
			// the best to the worst, so at this point, we are ordering folks in
			// the line at the door.
			// the folks who got the worst seats in the past are put at the
			// front of the line.
			TreeSet<TicketRequest> sortedRequests = new TreeSet<TicketRequest>(new requestCompare());
			sortedRequests.addAll(requests);

			dumpRequests(sortedRequests);

			// NOTE: once we start assigning tickets, we need to make sure that
			// we do not
			// break up a party across an aisle or in non-contiguous seats. So,
			// if we ever get to the point where
			// there are single empty seats scattered about, the program will
			// not assign request for
			// 2 or more tickets. If the user wants to break up ticket requests
			// into smaller parties, then
			// that must be done manually.

			// assign front row seats
			// people with special needs type of front only are assigned here.
			// if they don't get a front row seat - they get no ticket
			assignSeats(getFrontRowSeats(availableSeats), sortedRequests, CustomerModel.FRONT_ONLY);

			// assign all special seats from zone table
			// now we assign all of the special needs people according to the
			// user's custom special needs
			// if a special needs person does not get an available seat that
			// matches their need - they get no ticket
			// it's important to make sure there are enough special needs seats
			// when creating the layout
			// NOTE: all assignment still is servicing people in order of past
			// quality whether it's
			// special needs seating or regular
			Collection<Zone> zones = ZoneModel.getReference().getRecords();
			for (Zone z : zones) {
				Collection<Seat> zoneSeats = SeatModel.getReference().getAvailableSpecialSeatsForShow(show_id,
						z.getKey());
				assignSeats(zoneSeats, sortedRequests, z.getName());
			}

			// now we assign aisle seats for people with the built-in Aisle
			// needs type
			if (favor_aisle_requests) {
				availableSeats = SeatModel.getReference().getAvailableSeatsForShow(show_id);
				assignAisleSeats(availableSeats, sortedRequests);
			}

			// assign front seats - the folks with this special need just get
			// whatever seats a
			// left as close to the front as possible
			availableSeats = SeatModel.getReference().getAvailableSeatsForShow(show_id);
			TreeSet<Seat> frontSortedSeats = new TreeSet<Seat>(new frontSeatCompare());
			dumpSeats("before front sorted", availableSeats);
			frontSortedSeats.addAll(availableSeats);
			dumpSeats("front sorted", frontSortedSeats);
			assignSeats(frontSortedSeats, sortedRequests, CustomerModel.FRONT);

			// assign rear seats - the folks with this special need get seats as
			// far back as possible
			TreeSet<Seat> rearSortedSeats = new TreeSet<Seat>(new rearSeatCompare());
			rearSortedSeats.addAll(frontSortedSeats);
			dumpSeats("rear sorted", rearSortedSeats);
			assignSeats(rearSortedSeats, sortedRequests, CustomerModel.REAR);

			// assign the rest
			// now we assign anyone without a special need to whatever seats are
			// left...
			// still in order of past quality.
			TreeSet<Seat> regularSortedSeats = new TreeSet<Seat>(new regularSeatCompare());
			regularSortedSeats.addAll(rearSortedSeats);
			dumpSeats("regular sorted", regularSortedSeats);
			assignSeats(regularSortedSeats, sortedRequests, CustomerModel.NONE);

			JdbcDB.commitTransaction();
		} catch (Exception e) {
			JdbcDB.rollbackTransaction();
			throw e;
		}
	}

	private static class NotEnoughSeats extends Exception {
		private static final long serialVersionUID = 1L;
	}

	/**
	 * Assign tickets for a single request over a set of seats. By the time you
	 * get here - the seats have been chosen
	 *
	 * @param req
	 *            the request
	 * @param seats
	 *            the chosen set of contiguous seats
	 *
	 * @throws Exception
	 *             the exception
	 */
	private void assignTickets(TicketRequest req, Collection<Seat> seats) throws Exception {

		// calculate ticket price based on any discount
		Show sh = ShowModel.getReference().getShow(req.getShowId().intValue());
		int show_price = sh.getPrice().intValue();
		if (req.getDiscount() != null && req.getDiscount().doubleValue() > 0) {
			show_price = (int) (show_price * (1 - req.getDiscount().doubleValue() / 100.0));
		}

		// create the ticket records
		int total_quality = 0;
		for (Seat s : seats) {
			Ticket tkt = TicketModel.getReference().newTicket();
			tkt.setCustomerId(req.getCustomerId());
			tkt.setSeatId(Integer.valueOf(s.getKey()));
			tkt.setShowId(req.getShowId());
			log.fine("Assigning Ticket: " + req.getCustomerName() + " " + s.getKey() + ":" + s.getRow() + "/"
					+ s.getSeat() + " " + req.getSpecialNeeds() + " " + req.getLotteryPosition());

			// assign price from show with discount from request

			tkt.setPrice(Integer.valueOf(show_price));
			TicketModel.getReference().saveRecord(tkt);

			total_quality += s.getWeight().intValue();
		}

		// update the quality counts for the customer
		Customer cust = CustomerModel.getReference().getCustomer(req.getCustomerId().intValue());
		cust.setTotalTickets(Integer.valueOf(req.getTickets().intValue() + cust.getTotalTickets().intValue()));
		cust.setTotalQuality(Integer.valueOf(total_quality + cust.getTotalQuality().intValue()));
		CustomerModel.getReference().saveRecord(cust);

		// delete the request
		TicketRequestModel.getReference().delete(req);

	}

	/**
	 * Gets a set of contiguous seats to honor a request
	 *
	 * @param availableSeats
	 *            the available seats to pick from
	 * @param startingSeat
	 *            the starting seat
	 * @param number
	 *            the number of seats
	 * @param aisle
	 *            if true, we are starting on an aisle
	 *
	 * @return the contiguous seats
	 *
	 * @throws NotEnoughSeats
	 *             the not enough seats
	 * @throws Exception
	 *             the exception
	 */
	private Collection<Seat> getContiguousSeats(Collection<Seat> availableSeats, Seat startingSeat, int number,
			boolean aisle) throws NotEnoughSeats, Exception {

		Collection<Seat> seats = new ArrayList<Seat>();
		seats.add(startingSeat);

		// always assign seats towards the center
		boolean assignToTheRight = true;

		// if we are already to the right of center, assign to the left
		int st = startingSeat.getSeat().intValue();
		if (st >= LayoutModel.getCenter(show_id))
			assignToTheRight = false;

		// if assigning aisle seats - assign away from the aisle
		if (aisle == true) {
			if (startingSeat.getEnd().equals(SeatModel.LEFT)) {
				assignToTheRight = true;
			} else if (startingSeat.getEnd().equals(SeatModel.RIGHT)) {
				assignToTheRight = false;
			}
		}

		Seat curseat = startingSeat;
		for (int i = 0; i < number - 1; i++) {
			// check if there is no next seat
			if ((assignToTheRight && curseat.getEnd().equals(SeatModel.RIGHT))
					|| (!assignToTheRight && curseat.getEnd().equals(SeatModel.LEFT))) {
				throw new NotEnoughSeats();
			}

			// check if next seat is available
			int nextseat;
			if (assignToTheRight) {
				nextseat = curseat.getSeat().intValue() + 1;
			} else {
				nextseat = curseat.getSeat().intValue() - 1;
			}

			curseat = null;

			// find the next seat over
			for (Seat ns : availableSeats) {
				if (ns.getSeat().intValue() == nextseat && ns.getRow().equals(startingSeat.getRow())) {
					curseat = ns;
					seats.add(curseat);
					break;

				}
			}

			// if the next seat over is not in the available list, then fail
			if (curseat == null)
				throw new NotEnoughSeats();
		}

		// we succeeded, remove the assigned seats from the available list and
		// return them
		availableSeats.removeAll(seats);
		return seats;
	}

	/**
	 * Assign aisle seats to requests with a special need of aisle.
	 *
	 * @param availableSeats
	 *            the available seats
	 * @param requests
	 *            the requests
	 *
	 * @throws Exception
	 *             the exception
	 */
	private void assignAisleSeats(Collection<Seat> availableSeats, Collection<TicketRequest> requests)
			throws Exception {

		Collection<Seat> aisleSeats = getAisleSeats(availableSeats);

		dumpSeats("Aisle Seats", aisleSeats);
		Iterator<TicketRequest> it = requests.iterator();

		// for each request
		while (it.hasNext()) {
			TicketRequest tr = it.next();

			if (tr.getSpecialNeeds().equals(CustomerModel.AISLE)) {

				// assign N contiguous seats, but only start hunting from an
				// aisle seat
				// loop through the aisle seats
				Iterator<Seat> seatit = aisleSeats.iterator();
				while (seatit.hasNext()) {
					try {

						// see if this aisle seat has enough contiguous seats to
						// satisfy the requested number of seats
						Collection<Seat> seats = getContiguousSeats(availableSeats, seatit.next(),
								tr.getTickets().intValue(), true);

						// assign the seats
						assignTickets(tr, seats);
						availableSeats.removeAll(seats);
						aisleSeats.removeAll(seats);
						break;

					} catch (NotEnoughSeats nes) {
						// not enough seats, try the next aisle seat
						continue;
					}
				}

				it.remove();

			} else
				continue;
		}
	}

	/**
	 * Assign seats for requests.
	 *
	 * @param sortedSeats
	 *            the sorted seats
	 * @param requests
	 *            the requests
	 * @param special_needs
	 *            the special_needs
	 *
	 * @throws Exception
	 *             the exception
	 */
	private void assignSeats(Collection<Seat> sortedSeats, Collection<TicketRequest> requests, String special_needs)
			throws Exception {

		Iterator<TicketRequest> it = requests.iterator();

		// for each request
		while (it.hasNext()) {
			TicketRequest tr = it.next();

			// process any AISLE requests with the general population, if any
			// left
			if (tr.getSpecialNeeds().equals(CustomerModel.AISLE) && special_needs.equals(CustomerModel.NONE)) {
				Collection<Seat> aisleSeats = getAisleSeats(sortedSeats);

				// assign N contiguous seats, but only start hunting from an
				// aisle seat
				// loop through the aisle seats
				Iterator<Seat> seatit = aisleSeats.iterator();
				while (seatit.hasNext()) {
					try {

						// see if this aisle seat has enough contiguous seats to
						// satisfy the requested number of seats
						Collection<Seat> seats = getContiguousSeats(sortedSeats, seatit.next(),
								tr.getTickets().intValue(), true);

						// assign the seats
						assignTickets(tr, seats);
						sortedSeats.removeAll(seats);
						aisleSeats.removeAll(seats);
						break;

					} catch (NotEnoughSeats nes) {
						// not enough seats, try the next aisle seat
						continue;
					}
				}

				it.remove();
				continue;
			}

			// skip any requests that are not for the special need that we are
			// assigning
			if (!tr.getSpecialNeeds().equals(special_needs))
				continue;

			// loop through the seats, which are sorted from best to worst
			// we will hunt for seats starting with every seat in the place
			// until we find a bunch of avaialble seats or give up
			// the seats are sorted from best to worst and the requests are
			// sorted
			// with the most desrving on top - so each request will get the best
			// seats available
			// that can satisfy it.
			Iterator<Seat> seatit = sortedSeats.iterator();
			while (seatit.hasNext()) {
				try {

					// see if this seat has enough contiguous seats to
					// satisfy the requested number of seats
					Collection<Seat> seats = getContiguousSeats(sortedSeats, seatit.next(), tr.getTickets().intValue(),
							false);

					// assign the seats
					assignTickets(tr, seats);
					sortedSeats.removeAll(seats);
					break;

				} catch (NotEnoughSeats nes) {
					// not enough seats, try the next aisle seat
					continue;
				}
			}

			it.remove();
		}
	}

	/**
	 * Gets the available front row seats. Front seats are seats in row A plus
	 * any other seats specially marked as FRONT
	 *
	 * @param availableSeats
	 *            the available seats
	 *
	 * @return the front row seats
	 */
	private Collection<Seat> getFrontRowSeats(Collection<Seat> availableSeats) {
		Collection<Seat> seats = new TreeSet<Seat>(new regularSeatCompare());
		for (Seat s : availableSeats) {
			if (s.getRow().equals("A") || s.getEnd().equals(SeatModel.FRONT)) {
				seats.add(s);
			}
		}
		return seats;
	}

	/**
	 * Gets the available aisle seats - which are marked as rigt or left
	 *
	 * @param availableSeats
	 *            the available seats
	 *
	 * @return the aisle seats
	 */
	private Collection<Seat> getAisleSeats(Collection<Seat> availableSeats) {
		Collection<Seat> seats = new TreeSet<Seat>(new regularSeatCompare());
		for (Seat s : availableSeats) {
			if (s.getEnd().equals(SeatModel.LEFT) || s.getEnd().equals(SeatModel.RIGHT)) {
				seats.add(s);
			}
		}
		return seats;
	}

	/**
	 * Assign a random number to each request to be used later
	 *
	 * @return the winning requests
	 *
	 * @throws Exception
	 *             the exception
	 */
	private Collection<TicketRequest> getRequests(int capacity) throws Exception {
		ArrayList<TicketRequest> req = TicketRequestModel.getReference().getRequestsForShow(show_id);
		ArrayList<TicketRequest> winners = new ArrayList<TicketRequest>();
		Random ran = new Random();

		int winningseats = 0;
		while (req.size() > 0) {
			int idx = ran.nextInt(req.size());
			TicketRequest tr = req.remove(idx);

			winners.add(tr);
			tr.setLotteryPosition(Integer.valueOf(winners.size()));
			winningseats += tr.getTickets();

			if (capacity != -1 && winningseats > capacity) {
				log.fine("Sold-out picked winners...");
				break;
			}

		}

		return winners;
	}

	/**
	 * sort requests by past ticket quality for a customer.
	 */
	private static class requestCompare implements Comparator<TicketRequest> {

		/*
		 * (non-Javadoc)
		 *
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(TicketRequest r1, TicketRequest r2) {

			// compare avg quality. multiply by 100000 so we can use integer
			// math and not fractions

			// only compare quality for prior customers. New customers
			// will be random

			if (r1.getTotalTickets().intValue() != 0 && r2.getTotalTickets().intValue() != 0) {
				int quality1 = (100000 * r1.getTotalQuality().intValue()) / r1.getTotalTickets().intValue();

				int quality2 = (100000 * r2.getTotalQuality().intValue()) / r2.getTotalTickets().intValue();

				if (quality2 != quality1)
					return quality1 - quality2;
			}

			// never return 0, new customers depend on random placement
			return (r1.getLotteryPosition().intValue() - r2.getLotteryPosition().intValue());
		}

	}

	/**
	 * sort seats by how towards the front they are, then by quality, then by
	 * distance from center, finally by Db record key to avoid dups
	 */
	private static class frontSeatCompare implements Comparator<Seat> {

		/*
		 * (non-Javadoc)
		 *
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(Seat s1, Seat s2) {

			String r1 = s1.getRow();
			String r2 = s2.getRow();

			// ROW
			if (r1.length() != r2.length())
				return (r1.length() - r2.length());
			int c = r1.compareTo(r2);
			if (c != 0)
				return c;

			// QUAL
			int q = qualityCompare(s1, s2);
			if (q != 0)
				return q;

			// DISTANCE FROM CENTER
			int sc = seatNumberCompare(s1, s2);
			if (sc != 0)
				return sc;

			// never return 0
			return (s1.getKey() - s2.getKey());

		}

	}

	// compare seats by distance form center
	private static int seatNumberCompare(Seat s1, Seat s2) {
		int seat1dist = Math.abs(center_ - s1.getSeat().intValue());
		int seat2dist = Math.abs(center_ - s2.getSeat().intValue());
		return (seat1dist - seat2dist);
	}

	/**
	 * compare seat quality
	 */
	private static int qualityCompare(Seat s1, Seat s2) {
		return (s2.getWeight().intValue() - s1.getWeight().intValue());
	}

	/**
	 * sort seats by how towards the rear they are, then by quality, then by
	 * distance from center, finally by Db record key to avoid dups
	 */
	private static class rearSeatCompare implements Comparator<Seat> {

		/*
		 * (non-Javadoc)
		 *
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(Seat s1, Seat s2) {

			String r1 = s1.getRow();
			String r2 = s2.getRow();

			// ROW
			if (r2.length() != r1.length())
				return (r2.length() - r1.length());
			int c = r2.compareTo(r1);
			if (c != 0)
				return c;

			// QUAL
			int q = qualityCompare(s1, s2);
			if (q != 0)
				return q;

			// DISTANCE FROM CENTER
			int sc = seatNumberCompare(s1, s2);
			if (sc != 0)
				return sc;

			// never return 0
			return (s1.getKey() - s2.getKey());

		}

	}

	/**
	 * sort seats by quality, then distance from front, then by distance from
	 * center, finally by Db record key to avoid dups
	 */
	private static class regularSeatCompare implements Comparator<Seat> {

		/*
		 * (non-Javadoc)
		 *
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(Seat s1, Seat s2) {

			// QUAL
			int q = qualityCompare(s1, s2);
			if (q != 0)
				return q;

			String r1 = s1.getRow();
			String r2 = s2.getRow();

			// ROW
			if (r2.length() != r1.length())
				return (r1.length() - r2.length());
			int c = r1.compareTo(r2);
			if (c != 0)
				return c;

			// DISTANCE FROM CENTER
			int sc = seatNumberCompare(s1, s2);
			if (sc != 0)
				return sc;

			// never return 0
			return (s1.getKey() - s2.getKey());

		}

	}

	private static void dumpSeats(String title, Collection<Seat> seats) {
		if (log.isLoggable(Level.FINE)) {

			StringBuffer sb = new StringBuffer();
			sb.append(title + ":\n");
			for (Seat s : seats) {
				sb.append(s.getRow() + "/" + s.getSeat() + " ");
			}
			log.fine(seats.size() + " seats");
			log.fine(sb.toString());

		}
	}

	private static void dumpRequests(Collection<TicketRequest> reqs) {
		if (log.isLoggable(Level.FINE)) {
			log.fine(reqs.size() + " requests");

			StringBuffer sb = new StringBuffer();
			for (TicketRequest s : reqs) {
				sb.append(s.getCustomerName() + " " + s.getSpecialNeeds() + " " + s.getTotalTickets() + " "
						+ s.getTotalQuality() + " " + s.getLotteryPosition() + "\n");
			}
			log.fine(sb.toString());

		}
	}

	/**
	 * Undo a lottery. This method will convert all assgined tickets for a show
	 * back inot the original requests. It also undoes the updates to a
	 * customer's past ticket quality. It has to group tickets together to build
	 * up requests for multiple tickets.
	 *
	 * @throws Exception
	 *             the exception
	 */
	public void undoLottery() throws Exception {

		// begin trans
		JdbcDB.startTransaction();
		try {

			Show show = ShowModel.getReference().getShow(show_id);

			// hashmap to hold requests
			HashMap<Integer, TicketRequest> rmap = new HashMap<Integer, TicketRequest>();

			// hashmap to hold weights
			HashMap<Integer, Integer> wmap = new HashMap<Integer, Integer>();

			// collect up all tickets
			Collection<Ticket> tkts = TicketModel.getReference().getTicketsForShow(show_id);

			for (Ticket t : tkts) {
				Integer custid = t.getCustomerId();
				TicketRequest req = rmap.get(custid);
				if (req == null) {
					// add a new request for this customer
					req = new TicketRequest();
					req.setCustomerId(custid);
					req.setShowId(t.getShowId());
					req.setTickets(Integer.valueOf(1));
					// calculate the discount
					double d = 100.0 * (1.0 - t.getPrice().doubleValue() / show.getPrice().doubleValue());
					req.setDiscount(Double.valueOf(d));
					rmap.put(custid, req);

					// add an entry for the seat weight being deleted in this
					// ticket
					Seat seat = SeatModel.getReference().getSeat(t.getSeatId().intValue());
					wmap.put(custid, seat.getWeight());
				} else {
					// found a request - just bump the number of tickets
					req.setTickets(Integer.valueOf(req.getTickets().intValue() + 1));

					// add to the weight total so we can subtract it from the
					// customer later
					Seat seat = SeatModel.getReference().getSeat(t.getSeatId().intValue());
					Integer w = wmap.get(custid);
					wmap.put(custid, Integer.valueOf(seat.getWeight().intValue() + w.intValue()));
				}

			}

			// save requests
			for (TicketRequest r : rmap.values()) {
				try {
					TicketRequestModel.getReference().saveRecord(r);
				} catch (SQLException e) {
					// got here if request already exists - only happens if they
					// added a second request after the lottery for the same
					// customer
					// and show - so we need to add to that request
					ArrayList<TicketRequest> currentRequests = TicketRequestModel.getReference()
							.getRequestsForCustomer(r.getCustomerId());
					for (TicketRequest current : currentRequests) {
						if (current.getShowId().intValue() == r.getShowId().intValue()) {
							current.setTickets(current.getTickets() + r.getTickets());
							TicketRequestModel.getReference().saveRecord(current);
							break;
						}
					}

				}

				// adjust customer's quality
				Customer c = CustomerModel.getReference().getCustomer(r.getCustomerId().intValue());
				c.setTotalTickets(Integer.valueOf(c.getTotalTickets().intValue() - r.getTickets().intValue()));
				int w = wmap.get(r.getCustomerId()).intValue();
				c.setTotalQuality(Integer.valueOf(c.getTotalQuality().intValue() - w));
				if( c.getTotalTickets() < 0) c.setTotalTickets(0);
				if( c.getTotalQuality() < 0) c.setTotalQuality(0);
				CustomerModel.getReference().saveRecord(c);
			}

			// delete all tickets for show
			TicketModel.getReference().deleteTicketsForShow(show_id);

			// end trans
			JdbcDB.commitTransaction();
		} catch (Exception e) {
			JdbcDB.rollbackTransaction();
			throw e;
		}
	}

}
