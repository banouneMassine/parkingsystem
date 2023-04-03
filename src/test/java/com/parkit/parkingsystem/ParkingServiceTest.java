package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

public class ParkingServiceTest {

    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;

    
    ParkingService parkingService2 = new ParkingService(inputReaderUtil,parkingSpotDAO,ticketDAO);
    @BeforeEach
    private void setUpPerTest() {
        try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");// récuperer la matriculation de la voiture

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }
    }

    @Test
    @DisplayName("Tester le cas nominal pour processExitingVehicleTest(try)")
    public void processExitingVehicleTest(){
    	
    	ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);// place de parking
        Ticket ticket = new Ticket();
       // ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));// l'heure d'entrée 
        ticket.setParkingSpot(parkingSpot);// numéro de parking
        ticket.setVehicleRegNumber("ABCDEF");// matricule
         
    	when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
        
        parkingService.processExitingVehicle();
       
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    }
    
    @Test
    @DisplayName("Tester le cas ou l'utilisateur a renseigner une immatriculation inconnue (catch)")
    public void processExitingVehicle_TestWheneVehicleRegNumberIsUnknown_TheneGetExeption()
    {
    		//GIVEN
    	 	doThrow(new RuntimeException("mock exception")).when(ticketDAO).getTicket(anyString());
    	 
    	 	String expectedOutput = "Unable to process exiting vehicle";
    	    ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    	    System.setOut(new PrintStream(outContent));
    	    
    	    //WHENE
    	    parkingService.processExitingVehicle();
    	   
    	    //THENE
    	    assertThat(outContent.toString().trim()).contains(expectedOutput);
    }
    @Test
    @DisplayName("Tester le cas ou la MAJ du ticket renvoie un false ( la MAJ ne s'est pas déroulé correctement)")
    public void processExitingVehicle_TestWheneUpdateTicketReturnFalse_TheneGetMessageError()
    {
    		//GIVEN
	    	ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);// place de parking
	        Ticket ticket = new Ticket();
	        //ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));// l'heure d'entrée 
	        ticket.setParkingSpot(parkingSpot);// numéro de parking
	        ticket.setVehicleRegNumber("ABCDEF");// matricule
	         
	        when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
    		when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);
    	 
    	 	String expectedOutput = "Unable to update ticket information. Error occurred";
    	    ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    	    System.setOut(new PrintStream(outContent));
    	    
    	    //WHENE
    	    parkingService.processExitingVehicle();
    	   
    	    //THENE
    	    assertThat(outContent.toString().trim()).contains(expectedOutput);
    }
    
    
    @Test
    @DisplayName("Tester le cas nominal pour processIncomingVehicle(try)")
    @Disabled
    public void processIncomingVehicleTest()
    {
    	ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,true);// place de parking
    	parkingSpot.setAvailable(false);
    	
    	LocalDateTime inTime = LocalDateTime.now();
        Ticket ticket = new Ticket();
        //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
        //ticket.setId(ticketID);
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");// matricule
        ticket.setPrice(0);
        ticket.setInTime(inTime);
        ticket.setOutTime(null);
       
    	
    	parkingService.processIncomingVehicle();
        
    
    }
    
    
    
}
