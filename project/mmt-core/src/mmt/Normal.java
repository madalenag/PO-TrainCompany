package mmt;

import java.io.Serializable;
import java.time.Duration;

public class Normal extends Category implements Serializable {

  public Normal(Passenger p, double amount) {
  	super(p, amount);
  }

  public void commitItinerary(Itinerary itinerary) {
  	addItinerary(itinerary);

  	Passenger p = getPassenger();
  	double value = p.getValueOfLastTenItineraries();

  	if (value > 2500)
  		p.setCategory(new Special(p, getAmount()));
  	else if (value > 250)
  		p.setCategory(new Frequent(p, getAmount()));
  }
  

  public double getDiscount(Itinerary itinerary) {
  	return itinerary.getCost();
  }


  public String toString() {
    return "NORMAL";
  }  
}
