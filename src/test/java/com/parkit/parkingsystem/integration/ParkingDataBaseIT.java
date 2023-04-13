package com.parkit.parkingsystem.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;



@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;
 
    
  
    
    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown(){

    }

    @Test
    @DisplayName("verifier quun ticket est effectivement enregistre dans la base de donnees et que la table de stationnement est mise a jour avec la disponibilite")
    public void testParkingACar(){
    	
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
      
        Ticket savedTicket =ticketDAO.getTicket("ABCDEF");
     
        ParkingSpot parkingSport = new ParkingSpot(1,ParkingType.CAR,false);
        assertThat(savedTicket.getParkingSpot()).isEqualTo(parkingSport);
        assertThat(savedTicket.getVehicleRegNumber()).isEqualTo("ABCDEF");
        
    }

    @Test
    @DisplayName("verifier que le tarif genere et lheure de depart sont correctement renseignes dans la base de donnees")
    public void testParkingLotExit() throws InterruptedException{

    	ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        Thread.sleep(500);
        parkingService.processExitingVehicle();

        Ticket savedTicket =ticketDAO.getTicket("ABCDEF");
      
        Long durationInMinutes = Duration.between(savedTicket.getInTime(), savedTicket.getOutTime()).toMinutes();
      
        assertThat(savedTicket.getOutTime()).isNotNull();
        assertThat(savedTicket.getPrice()).isEqualTo(durationInMinutes/60.0 * Fare.CAR_RATE_PER_HOUR);
    }

}
