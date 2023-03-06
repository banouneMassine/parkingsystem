package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;

    @BeforeAll
    private static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    private void setUpPerTest() {
        ticket = new Ticket();
    }

    @Test
    public void calculateFareCar(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareBike(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareUnkownType(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, null,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithFutureInTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() + (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithLessThanOneHourParkingTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  45 * 60 * 1000) );//45 minutes parking time should give 3/4th parking fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice() );
    }

    @Test
    public void calculateFareCarWithLessThanOneHourParkingTime(){
    	
    	// GIVEN
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  45 * 60 * 1000) );//45 minutes parking time should give 3/4th parking fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        
        //WhENE 
        fareCalculatorService.calculateFare(ticket);
        
        //THENE
        assertEquals( (0.75 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithMoreThanADayParkingTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  24 * 60 * 60 * 1000) );//24 hours parking time should give 24 * parking fare per hour
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( (24 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }

    @Test
    @DisplayName("Tester qu'un parking d'une voiture d'une durée de moins de 30 minutes doit nous couter 0 dollars")
    public void calculateFareCar_WithLessThan30MinutesParkingTime_shouldCost0Dollars(){
    	
    	// GIVEN
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  30 * 60 * 1000) );//30 minutes parking time should give 0 dollars
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        
        //WhENE 
        fareCalculatorService.calculateFare(ticket);
        
        //THENE
        assertEquals( (0) , ticket.getPrice());
    }
    
    @Test
    @DisplayName("Tester qu'un parking d'un vélo d'une durée de moins de 30 minutes doit nous couter 0 dollars")
    public void calculateFareBike_WithLessThan30MinutesParkingTime_shouldCost0Dollars(){
    	
    	// GIVEN
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  30 * 60 * 1000) );//30 minutes parking time should give 0 dollars
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        
        //WhENE 
        fareCalculatorService.calculateFare(ticket);
        
        //THENE
        assertEquals( (0) , ticket.getPrice());
    }
    @Test
    @DisplayName("Tester que pour plus de 4 visites, l'utilisateur va bénéficier d'une réduction de 5% (Voiture)")
    public void calculateFareCar_WhenTheUserIsRecurring_TheneReduction(){
    	
    	// GIVEN
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );//60 minutes parking  time should give 1.425 dollars
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        int numberOfVisite = 5; // pour plus de 4  visites le user doit bénéficier de 5 % de réduction 
        
        ticket.setNumberOfVisites(numberOfVisite);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        
        //WhENE 
        fareCalculatorService.calculateFare(ticket);
        
        //THENE
        assertEquals( (Fare.CAR_RATE_PER_HOUR * 0.95) , ticket.getPrice());
    }
    
    @Test
    @DisplayName("Tester que pour plus de 4 visites, l'utilisateur va bénéficier d'une réduction de 5% (Velo)")
    public void calculateFareBike_WhenTheUserIsRecurring_TheneReduction(){
    	
    	// GIVEN
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );//60 minutes parking  time should give 1.425 dollars
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);
        int numberOfVisite = 5; // pour plus de 4  visites le user doit bénéficier de 5 % de réduction 
        
        ticket.setNumberOfVisites(numberOfVisite);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        
        //WhENE 
        fareCalculatorService.calculateFare(ticket);
        
        //THENE
        assertEquals( (Fare.BIKE_RATE_PER_HOUR * 0.95) , ticket.getPrice());
    }
    @Test
    @DisplayName("Tester que pour moins ou égal à  4 visites, l'utilisateur ne va pas bénéficier d'une réduction de 5% (Voiture)")
    public void calculateFareCar_WhenTheUserIsNonRecurring_TheneNonReduction(){
    	
    	// GIVEN
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );//60 minutes parking  time should give 1.425 dollars
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        int numberOfVisite = 4; // pour moins de 4  visites le user ne doit pas bénéficier de 5 % de réduction n 
        
        ticket.setNumberOfVisites(numberOfVisite);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        
        //WhENE 
        fareCalculatorService.calculateFare(ticket);
        
        //THENE
        assertEquals( (Fare.CAR_RATE_PER_HOUR * 1) , ticket.getPrice());
    }
    
    @Test
    @DisplayName("Tester que pour moins ou égal à  4 visites, l'utilisateur ne va pas bénéficier d'une réduction de 5% (Velo)")
    public void calculateFareBike_WhenTheUserIsNonRecurring_TheneNonReduction(){
    	
    	// GIVEN
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );//60 minutes parking  time should give 1.425 dollars
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);
        int numberOfVisite = 4; // pour moins de 4  visites le user ne doit pas bénéficier de 5 % de réduction 
        
        ticket.setNumberOfVisites(numberOfVisite);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        
        //WhENE 
        fareCalculatorService.calculateFare(ticket);
        
        //THENE
        assertEquals( (Fare.BIKE_RATE_PER_HOUR * 1) , ticket.getPrice());
    }
}
