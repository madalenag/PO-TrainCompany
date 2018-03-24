package mmt;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalTime;

public abstract class Category implements Serializable{

  /** Accumulated amount without discounts */
  private double _realAmount;

  /** Passenger of this Category */
  private Passenger _passenger;
  
  public Category(Passenger p, double amount) {
  	_realAmount = amount;
  	_passenger = p;
  }

  public abstract void commitItinerary(Itinerary itinerary);
  
  public abstract double getDiscount(Itinerary itinerary);

  
  public Passenger getPassenger() {
  	return _passenger;
  }

  public void addItinerary(Itinerary itinerary) {
  	_realAmount += itinerary.getCost();
  }

  public double getAmount() {
  	return _realAmount;
  }

}
