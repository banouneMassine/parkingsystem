package com.parkit.parkingsystem;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
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

import static org.assertj.core.api.Assertions.assertThat;

import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Test;

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
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
            Ticket ticket = new Ticket();
            LocalDateTime InTime = LocalDateTime.now().minusMinutes(60);
            ticket.setInTime(InTime);
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
            when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
           

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }
    }

    @Test
    @DisplayName("Tester que la mise a jour du parking a la sortie du client a bien été faite")
    public void processExitingVehicle_callUpdateParking_wheneUpdateTicketIsTrue(){
    	
    	when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
         
        parkingService.processExitingVehicle();
        
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    }
    
    @Test
    @DisplayName("Tester que quand la mise à jour du ticket renvoie False alors un message d'erreur/d'info s'affiche   ")
    public void processExitingVehicle_returnErrorMessage_wheneUpdateTicketIsFalse()
    {
    	//GIVEN
    	LogCaptor logCaptor = LogCaptor.forClass(ParkingService.class);
    	when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);
    	
    	//WHEN
        parkingService.processExitingVehicle();
        
        // THEN 
        assertThat(logCaptor.getInfoLogs()).containsExactly("Unable to update ticket information. Error occurred");
    }
     
    @Test
    @DisplayName("Tester que quand une erreur survienne dans le bloc try alors un message d'erreur/d'info s'affiche  ")
    public void processExitingVehicle_returnTheExptionMessage_wheneErrorInTheBlocTry()
    {
    	//GIVEN
    	LogCaptor logCaptor = LogCaptor.forClass(ParkingService.class);
        
    	when(ticketDAO.getTicket(anyString())).thenThrow(new RuntimeException());
    	
    	//WHEN
        parkingService.processExitingVehicle();
        
        // THEN 
        assertThat(logCaptor.getInfoLogs()).containsExactly("Unable to process exiting vehicle");
    }
    
    
    
    
    
    
    
    
    @Test
    @DisplayName("Tester que la sauvgarde du ticket se fait bien à pour véhicule entrant ")
    @Disabled
    public void processIncomingVehicle_callSaveTicket_wheneparkingSpotIdIS()
    {
    	//GIVEN
    	when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);
    	
    	//
        parkingService.processExitingVehicle();
        
        // THEN 
        
    }

}
