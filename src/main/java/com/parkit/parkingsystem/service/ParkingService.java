package com.parkit.parkingsystem.service;

import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;

public class ParkingService {

	private static final Logger logger = LogManager.getLogger(ParkingService.class);

	private static FareCalculatorService fareCalculatorService = new FareCalculatorService();
	private InputReaderUtil inputReaderUtil;
	private ParkingSpotDAO parkingSpotDAO;
	private TicketDAO ticketDAO;

	public ParkingService(InputReaderUtil inputReaderUtil, ParkingSpotDAO parkingSpotDAO, TicketDAO ticketDAO) {
		this.inputReaderUtil = inputReaderUtil;
		this.parkingSpotDAO = parkingSpotDAO;
		this.ticketDAO = ticketDAO;
	}

	public void processIncomingVehicle() {// traiter le véhicule entrant
		try {
			ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();
			if (parkingSpot != null && parkingSpot.getId() > 0) {
				String vehicleRegNumber = getVehichleRegNumber();
				parkingSpot.setAvailable(false);
				parkingSpotDAO.updateParking(parkingSpot);// allot this parking space and mark it's availability as
															// false
				LocalDateTime inTime = LocalDateTime.now();
				Ticket ticket = new Ticket();
				ticket.setParkingSpot(parkingSpot);
				ticket.setVehicleRegNumber(vehicleRegNumber);
				ticket.setPrice(0);
				ticket.setInTime(inTime);
				LocalDateTime outTime = null;
				ticket.setOutTime(outTime);

				if (ticketDAO.verifyVehicleRegNumber(vehicleRegNumber)) {
					logger.error("The registration number you entered is not valid");
				} else {
					System.out.println("saveTicket saveTicket saveTicketsaveTicket saveTicket");
					ticketDAO.saveTicket(ticket);
				}
				System.out.println("Generated Ticket and saved in DB");
				System.out.println("Please park your vehicle in spot number:" + parkingSpot.getId());
				System.out.println("Recorded in-time for vehicle number:" + vehicleRegNumber + " is:" + inTime);
			
			}
		} catch (Exception e) {
			logger.error("Unable to process incoming vehicle", e);
		}
	}

	private String getVehichleRegNumber() throws Exception {
		System.out.println("Please type the vehicle registration number and press enter key");
		return inputReaderUtil.readVehicleRegistrationNumber();
	}

	public ParkingSpot getNextParkingNumberIfAvailable() {
		int parkingNumber = 0;
		ParkingSpot parkingSpot = null;
		try {
			ParkingType parkingType = getVehichleType();
			parkingNumber = parkingSpotDAO.getNextAvailableSlot(parkingType);
			if (parkingNumber > 0) {
				parkingSpot = new ParkingSpot(parkingNumber, parkingType, true);
			} else {
				throw new Exception("Error fetching parking number from DB. Parking slots might be full");
			}
		} catch (IllegalArgumentException ie) {
			logger.info("Error parsing user input for type of vehicle", ie);
		} catch (Exception e) {
			logger.info("Error fetching next available parking slot", e);
		}
		return parkingSpot;
	}

	private ParkingType getVehichleType() {
		System.out.println("Please select vehicle type from menu");
		System.out.println("1 CAR");
		System.out.println("2 BIKE");
		int input = inputReaderUtil.readSelection();
		switch (input) {
		case 1: {
			return ParkingType.CAR;
		}
		case 2: {
			return ParkingType.BIKE;
		}
		default: {
			System.out.println("Incorrect input provided");
			throw new IllegalArgumentException("Entered input is invalid");
		}
		}
	}
	
	public void processExitingVehicle() {// traiter le véhicule sortant
		try {
			String vehicleRegNumber = getVehichleRegNumber();
			System.out.println("vehicleRegNumber  ===>" + vehicleRegNumber);
			if (ticketDAO.verifyVehicleRegNumber(vehicleRegNumber)) {
				Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);
				LocalDateTime outTime = LocalDateTime.now();
				ticket.setOutTime(outTime);
				ticket.setNumberOfVisites(ticketDAO.getNumberOfVisitesDAO(vehicleRegNumber));// ligne pour affecter le
																								// nombre de visites
				System.out.println("outTime  avant calculateFare ===>" + outTime);																				// (réduction 5%)
				fareCalculatorService.calculateFare(ticket);

				if (ticketDAO.updateTicket(ticket)) {
					ParkingSpot parkingSpot = ticket.getParkingSpot();
					parkingSpot.setAvailable(true);
					parkingSpotDAO.updateParking(parkingSpot);
					System.out.println("Please pay the parking fare:" + ticket.getPrice());
					System.out.println(
							"Recorded out-time for vehicle number:" + ticket.getVehicleRegNumber() + " is:" + outTime);
				} else {
					logger.info("Unable to update ticket information. Error occurred");
				}
			} else {
				logger.error("The registration number you entered is not valid");
			}
		} catch (Exception e) {
			logger.info("Unable to process exiting vehicle", e);
		}
	}
}
