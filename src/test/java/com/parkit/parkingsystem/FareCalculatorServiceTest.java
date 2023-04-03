package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
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
    
    @DisplayName("Tester le calcul d'un parking pour une voiture")
    @Test
    public void calculateFareCar(){
    	
    	//GIVEN 
    	LocalDateTime inTime = LocalDateTime.now().minusMinutes(60);// inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        LocalDateTime outTime =LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        
        //WHENE 
        fareCalculatorService.calculateFare(ticket);
        
        //THENE
        assertEquals( Fare.CAR_RATE_PER_HOUR ,ticket.getPrice());
    }
    
    @DisplayName("Tester le calcul d'un parking pour un vélo ")
    @Test
    public void calculateFareBike(){
    	
    	//GIVEN 
    	LocalDateTime inTime = LocalDateTime.now().minusHours(1);// inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        LocalDateTime outTime =LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        //WHENE
        fareCalculatorService.calculateFare(ticket);
        //THENE
        assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
    }
    
    @DisplayName("Tester le calcul d'un parking pour un type de véhicule inconnu ")
    @Test
    public void calculateFareUnkownType(){
    	
    	//GIVEN 
    	LocalDateTime inTime = LocalDateTime.now().minusHours(1);// inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        LocalDateTime outTime =LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, null,false);
        
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        
        
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithFutureInTime(){
    	LocalDateTime inTime = LocalDateTime.now().plusHours(1);// inTime.setTime( System.currentTimeMillis() + (  60 * 60 * 1000) );
        LocalDateTime outTime =LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot)
        ;
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithLessThanOneHourParkingTime(){
    	//GIVEN
        LocalDateTime inTime = LocalDateTime.now().minusMinutes(45);// inTime.setTime( System.currentTimeMillis() - (  45 * 60 * 1000) );
        LocalDateTime outTime =LocalDateTime.now();//45 minutes parking time should give 3/4th parking fare
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        //WHEN
        fareCalculatorService.calculateFare(ticket);
        //THEN
        assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice() );
    }

    @Test
    public void calculateFareCarWithLessThanOneHourParkingTime(){
    	
    	// GIVEN
        LocalDateTime inTime = LocalDateTime.now().minusMinutes(45);//45 minutes parking time should give 3/4th parking fare
        LocalDateTime outTime =LocalDateTime.now();// inTime.setTime( System.currentTimeMillis() - (  45 * 60 * 1000) );
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
    	// GIVEN
        LocalDateTime inTime = LocalDateTime.now().minusHours(24);//24 hours parking time should give 24 * parking fare per hour     
        LocalDateTime outTime =LocalDateTime.now();  //  inTime.setTime( System.currentTimeMillis() - (  24 * 60 * 60 * 1000) );
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        //WhENE 
        fareCalculatorService.calculateFare(ticket);
        
        //THENE
        assertEquals( (24 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }
    
    @Test
    public void calculateFareBikeWithMoreThanADayParkingTime(){
    	// GIVEN
        LocalDateTime inTime = LocalDateTime.now().minusHours(24);//24 hours parking time should give 24 * parking fare per hour
        LocalDateTime outTime =LocalDateTime.now(); 
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        //WhENE 
        fareCalculatorService.calculateFare(ticket);
        
      //THENE
        assertEquals( (24 * Fare.BIKE_RATE_PER_HOUR) , ticket.getPrice());
    }
    

    @Test
    @DisplayName("Tester qu'un parking d'une voiture d'une durée de moins de 30 minutes doit nous couter 0 dollars")
    public void calculateFareCar_WithLessThan30MinutesParkingTime_shouldCost0Dollars(){
    	
    	// GIVEN
    	LocalDateTime inTime = LocalDateTime.now().minusMinutes(30);//30 minutes parking time should give 0 dollars
        LocalDateTime outTime =LocalDateTime.now();//inTime.setTime( System.currentTimeMillis() - (  30 * 60 * 1000) );
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
    	LocalDateTime inTime = LocalDateTime.now().minusMinutes(30);//30 minutes parking time should give 0 dollars
        LocalDateTime outTime =LocalDateTime.now();//inTime.setTime( System.currentTimeMillis() - (  30 * 60 * 1000) );
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

    	LocalDateTime inTime = LocalDateTime.now().minusMinutes(60);////60 minutes parking  time should give 1.425 dollars
        LocalDateTime outTime =LocalDateTime.now(); //inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) ); 
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
    	LocalDateTime inTime = LocalDateTime.now().minusMinutes(60);////60 minutes parking  time should give 1.425 dollars
        LocalDateTime outTime =LocalDateTime.now(); //inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) ); 
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
    	LocalDateTime inTime = LocalDateTime.now().minusMinutes(60);  
        LocalDateTime outTime =LocalDateTime.now();//inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
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
    	LocalDateTime inTime = LocalDateTime.now().minusMinutes(60);  
        LocalDateTime outTime =LocalDateTime.now();//inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
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
