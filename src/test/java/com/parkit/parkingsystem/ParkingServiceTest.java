package com.parkit.parkingsystem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

import nl.altindag.log.LogCaptor;

@ExtendWith(MockitoExtension.class)

public class ParkingServiceTest {

	private static ParkingService parkingService;

	@Mock
	private static InputReaderUtil inputReaderUtil;
	@Mock
	private static ParkingSpotDAO parkingSpotDAO;
	@Mock
	private static TicketDAO ticketDAO;

	@BeforeEach
	private void setUpPerTest() {
		try {

			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		} catch (Exception e) { 
			e.printStackTrace();
			throw new RuntimeException("Failed to set up test mock objects");
		}
	}

	@Disabled
	@Nested
	@Tag("traiter_le_vehicule_sortant")
	class TraiterVehiculeSortant {
		@Test
		@DisplayName("Tester que la mise a jour du parking a la sortie du client a bien été faite")
		public void processExitingVehicle_callUpdateParking_wheneUpdateTicketIsTrue() {
			try {
				when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
			} catch (Exception e) {
				e.printStackTrace();
			} // pourquoi ??
			when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
			when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
			Ticket ticket = new Ticket();
			LocalDateTime InTime = LocalDateTime.now().minusMinutes(60);
			ticket.setInTime(InTime);
			ticket.setParkingSpot(parkingSpot);
			ticket.setVehicleRegNumber("ABCDEF");
			when(ticketDAO.getTicket(anyString())).thenReturn(ticket);

			parkingService.processExitingVehicle();

			verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
		}

		@Test
		@DisplayName("Tester que quand la mise à jour du ticket renvoie False alors un message d'erreur/d'info s'affiche   ")
		public void processExitingVehicle_returnErrorMessage_wheneUpdateTicketIsFalse() {
			// GIVEN
			LogCaptor logCaptor = LogCaptor.forClass(ParkingService.class);
			try {
				when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
			} catch (Exception e) {
				e.printStackTrace();
			}
			when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);

			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
			Ticket ticket = new Ticket();
			LocalDateTime InTime = LocalDateTime.now().minusMinutes(60);
			ticket.setInTime(InTime);
			ticket.setParkingSpot(parkingSpot);
			ticket.setVehicleRegNumber("ABCDEF");
			when(ticketDAO.getTicket(anyString())).thenReturn(ticket);

			// WHEN
			parkingService.processExitingVehicle();

			// THEN
			assertThat(logCaptor.getInfoLogs()).containsExactly("Unable to update ticket information. Error occurred");
		}

		@Test
		@DisplayName("Tester que quand une erreur survient dans le bloc try alors un message d'erreur/d'info s'affiche  ")
		public void processExitingVehicle_returnTheExptionMessage_wheneErrorInTheBlocTry() {
			// GIVEN

			LogCaptor logCaptor = LogCaptor.forClass(ParkingService.class);
			try {
				when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
			} catch (Exception e) {
				e.printStackTrace();
			}
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
			Ticket ticket = new Ticket();
			LocalDateTime InTime = LocalDateTime.now().minusMinutes(60);
			ticket.setInTime(InTime);
			ticket.setParkingSpot(parkingSpot);
			ticket.setVehicleRegNumber("ABCDEF");
			when(ticketDAO.getTicket(anyString())).thenReturn(ticket);

			when(ticketDAO.getTicket(anyString())).thenThrow(new RuntimeException("Erreur Ticket"));

			// WHEN
			parkingService.processExitingVehicle();

			// THEN
			assertThat(logCaptor.getInfoLogs()).containsExactly("Unable to process exiting vehicle");
		}
	}

	//@Disabled
	@Nested
	@Tag("traiter_le_vehicule_entrant")
	class TraiterVehiculeEntrant {
		//@Disabled
		@Test
		@DisplayName("Tester que la sauvgarde du ticket se fait bien à pour véhicule entrant ")
		public void processIncomingVehicle_callSaveTicket_wheneparkingSpotIdIsNotNull() {
			// GIVEN
			try {
				when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
			} catch (Exception e) { 
				e.printStackTrace();
			}
			when(inputReaderUtil.readSelection()).thenReturn(1);
			when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
			when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
			when(ticketDAO.verifyVehicleRegNumber(any(String.class))).thenReturn(false);
			// WHEN
			parkingService.processIncomingVehicle();

			// THEN
			verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));

		}
		
		
		@Test
		@DisplayName("Tester que Ce n'est pas possible de mettre deux fois la même voiture dans le parking sans l'avoir fait sortir")
		public void processIncomingVehicle_returnErrorMessage_WhenWeTryToIntroducetheSameVehicleWithouThavingItExit() {
			// GIVEN
			LogCaptor logCaptor = LogCaptor.forClass(ParkingService.class);
			try {
				when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
			} catch (Exception e) {
				e.printStackTrace();
			}
			when(inputReaderUtil.readSelection()).thenReturn(1);
			when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
			when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
			when(ticketDAO.verifyVehicleRegNumber(any(String.class))).thenReturn(true);
			// WHEN
			parkingService.processIncomingVehicle(); 

			// THEN
			assertThat(logCaptor.getErrorLogs()).containsExactly("The registration number you entered is not valid");

		}
		//@Disabled
		@Test
		@DisplayName("Tester que quand une erreur survient dans le bloc try alors un message d'erreur/d'info s'affiche")
		public void processIncomingVehicle_returnTheExptionMessage_wheneErrorInTheBlocTry() {
			// GIVEN
			LogCaptor logCaptor = LogCaptor.forClass(ParkingService.class);
			try {
				when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
			} catch (Exception e) {
				e.printStackTrace();
			}
			when(inputReaderUtil.readSelection()).thenReturn(1);
			when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
			when(parkingSpotDAO.updateParking(any(ParkingSpot.class)))
					.thenThrow(new RuntimeException("Erreur parkingSpot"));

			// WHEN
			parkingService.processIncomingVehicle();

			// THEN

			assertThat(logCaptor.getInfoLogs()).containsExactly("Unable to process incoming vehicle");

		}
	}

	@Nested
	@Tag("Obtenir_le_prochain_numero_de_stationnement_si_disponible")
	@Disabled
	class GetNextParkingNumberIfAvailable {
		@DisplayName("Tester que si une place de parcking est disponible,alors elle ne retourne cette place de parking")
		@Test
		public void getNextParkingNumberIfAvailable_returnParkingSpot_whenAvailableSlot() {
			// GIVEN
			when(inputReaderUtil.readSelection()).thenReturn(1);
			when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);

			// WHEN
			ParkingSpot parkingSpotResult = parkingService.getNextParkingNumberIfAvailable();

			// THEN
			assertThat(parkingSpotResult).isEqualTo(new ParkingSpot(1, ParkingType.CAR, true));
		}

	

		@DisplayName("Tester que si il n y a pas de  place de parcking  disponible,alors afficher un message d'info pour dire que le parcking est complet")
		@Test
		public void getNextParkingNumberIfAvailable_returnInfoMessagInException_whenParkingIsFull() {
			// GIVEN
			when(inputReaderUtil.readSelection()).thenReturn(1);
			when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(0);
			try {
				parkingService.getNextParkingNumberIfAvailable();
				fail("Should throw exception when parkingNumber = 0");
			} catch (Exception aExp) {
				assert (aExp.getMessage().contains("Error fetching parking number from DB. Parking slots might be full"));
			}

		}
		
		@DisplayName("Tester si le type de vehicule est valide , si  no afficher un message d'info ")
		@Test
		public void getNextParkingNumberIfAvailable_returnInfoMessagInTheIllegalArgumentException_whenParkingTypeIsUnknown() {
			// GIVEN
			LogCaptor logCaptor = LogCaptor.forClass(ParkingService.class);
			when(inputReaderUtil.readSelection()).thenReturn(3);
			
			// WHEN
			parkingService.getNextParkingNumberIfAvailable();
			
				
			// THEN
			assertThat(logCaptor.getInfoLogs()).containsExactly("Error parsing user input for type of vehicle");

		}
		
		@DisplayName("Tester si Erreur lors de la recherche de la prochaine place de parking disponible ")
		@Test
		public void getNextParkingNumberIfAvailable_returnInfoMessagException_whenGetNextAvailableSlotReturnAnError () {
			// GIVEN
			LogCaptor logCaptor = LogCaptor.forClass(ParkingService.class);
			when(inputReaderUtil.readSelection()).thenReturn(1);
			when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenThrow(new RuntimeException("Erreur parkingSpot"));
			
			
			// WHEN
			parkingService.getNextParkingNumberIfAvailable();
			
				
			// THEN
			assertThat(logCaptor.getInfoLogs()).containsExactly("Error fetching next available parking slot");

		}
	}
	

}
