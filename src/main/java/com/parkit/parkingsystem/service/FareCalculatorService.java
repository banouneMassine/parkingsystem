package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {
	private double far;
	
    public void calculateFare(Ticket ticket){
    	
    	if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        float duration = (ticket.getOutTime().getTime() -ticket.getInTime().getTime()); 
        duration/=3600000;

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
            	far = (duration <= 0.5)?Fare.Vehicule_RATE_Less_30_minutes : Fare.CAR_RATE_PER_HOUR ;
                ticket.setPrice(duration * far);
                break;
            }
            case BIKE: {
            	 far = (duration <= 0.5)?Fare.Vehicule_RATE_Less_30_minutes : Fare.BIKE_RATE_PER_HOUR ;
                 ticket.setPrice(duration * far );
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}