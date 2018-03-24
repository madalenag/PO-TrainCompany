package mmt;

import java.io.Serializable;
import java.time.Duration;

public class Frequent extends Category implements Serializable {

	public Frequent(Passenger p, double amount) {
  	super(p, amount);
  }

  public void commitItinerary(Itinerary itinerary) {
  	addItinerary(itinerary);
  	
  	Passenger p = getPassenger();
  	double value = p.getValueOfLastTenItineraries();
  	
  	if (value > 2500)
  		p.setCategory(new Special(p, getAmount()));
  	else if (value <= 250)
  		p.setCategory(new Special(p, getAmount()));
  }
  

  public double getDiscount(Itinerary itinerary) {
  	return 0.85*itinerary.getCost();
  }


  public String toString() {
    return "FREQUENTE";
  }
    
}
