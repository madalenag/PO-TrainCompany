package mmt;

import java.io.Serializable;
import java.time.Duration;

public class Special extends Category implements Serializable {
  
  public Special(Passenger p, double amount) {
  	super(p, amount);
  }

  public void commitItinerary(Itinerary itinerary) {
  	addItinerary(itinerary);

  	Passenger p = getPassenger();
  	double value = p.getValueOfLastTenItineraries();
  	
  	if (value <= 250)
  		p.setCategory(new Normal(p, getAmount()));
  	else if (value <= 2500)
  		p.setCategory(new Frequent(p, getAmount()));
  }
  
  public double getDiscount(Itinerary itinerary) {
  	return 0.5*itinerary.getCost();
  }
    
  public String toString() {
    return "ESPECIAL";
  }
    
}
