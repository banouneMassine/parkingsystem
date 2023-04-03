package com.parkit.parkingsystem.service;

import java.time.Duration;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {
	private double far;
	
    public void calculateFare(Ticket ticket){
    	
    	if( (ticket.getOutTime() == null) || (ticket.getOutTime().isBefore(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }
    	Long durationInMinutes = Duration.between(ticket.getInTime(),ticket.getOutTime()).toMinutes();
    	
    	//float duration = (ticket.getOutTime().getTime() -ticket.getInTime().getTime()); 
        //duration/=3600000;
        double coef = (ticket.getNumberOfVisites()  > 4 )? 0.95 : 1.00 ; // calcul du coefficient pour la réduction de 5 %  
        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
            	
            	far = (durationInMinutes <= 30)?Fare.Vehicule_RATE_Less_30_minutes : Fare.CAR_RATE_PER_HOUR ;
                ticket.setPrice(durationInMinutes/60.0 * far * coef);
                System.out.println("durationInMinutes : "+durationInMinutes/60.0 + " far : " + far + " coef  : " + coef  + " prix :" + durationInMinutes/60.0 * far * coef); 
                break;
            }
            case BIKE: {
            	
            	 far = (durationInMinutes <= 30)?Fare.Vehicule_RATE_Less_30_minutes : Fare.BIKE_RATE_PER_HOUR ;
                 ticket.setPrice(durationInMinutes/60.0 * far * coef );
                 System.out.println("durationInMinutes : "+durationInMinutes/60.0 + " far : " + far + " coef  : " + coef + " prix :" + durationInMinutes/60.0 * far * coef); 
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}