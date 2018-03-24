package mmt;

import java.io.Serializable;
import java.time.LocalTime;
import java.time.Duration;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;


/**
 * A passenger has a name, an accumulated time and amount, a category 
 * (that can be normal, special or frequent), a number of completed itineraries
 * and is identified by a unique number.
 */
public class Passenger implements Serializable {
  
  /** Passenger name. */
  private String _name;
  
  /** Passenger id. */
  private int _passengerId;

  /** Passenger accumulated time. */
  private Duration _time;
  
  /** Passenger accumulated amount with discounts. */
  private double _amount;
  
  /** Passenger category. */
  private Category _category;

  /** Number of itineraries completed */
  private int _numItineraries;

  /** List of comitted itineraries. */
  private List<Itinerary> _itineraries = new ArrayList<Itinerary>();
  
  /**
  * Constructor.
  * 
  * @param name
  *          the name of the passenger          
  * @param passengerId
  *          the id of the passenger
  */
  public Passenger(String name, int passengerId) {
    _passengerId = passengerId;
    _name = name;
    _amount = 0.00;
    _time = Duration.parse("PT0H0M");
    _category = new Normal(this, 0.00);
    _numItineraries = 0;
  }
  

  /**
  * Get the accumulated amount of the last ten commited itineraries
  */
  public double getValueOfLastTenItineraries() {
    double total = 0;

    if (_numItineraries < 10) {
      for (Itinerary i: _itineraries)
        total += i.getCost();
    }
    else {
      int limit = _numItineraries - 10;
      int begin = _numItineraries - 1;
      for (int i = begin; i > limit; i--) 
        total += _itineraries.get(i).getCost();
    }
    return total;
  }
  

  public void commitItinerary(Itinerary i) {
    _numItineraries++;
    _time = _time.plus(i.getTime());
    _amount += _category.getDiscount(i);
    _itineraries.add(i);
    _category.commitItinerary(i);
  }
  
  /**
  * Get the name of this passenger
  */
  public String getName() {
    return _name;
  }
  
  
  /**
  * Get the id of this passenger
  */
  public int getPassengerId() {
    return _passengerId;
  }
  
  
  /**
  * Set a name for this passenger
  */
  public void setName(String name) {
    _name = name;
  }

  /**
  * Set a category
  */
  public void setCategory(Category categ) {
    _category = categ;
  }

  
  public boolean passengerWithItineraries() {
    return _numItineraries > 0;
  }

  /**
  * Returns a list of all the itineraries ordenated by their date
  */
  public List<Itinerary> getItineraries() {
    if (_numItineraries == 0)
      return null;

    int count = 0;
    List<Itinerary> itineraries = new ArrayList<Itinerary>();
    for (Itinerary i: _itineraries)
      itineraries.add(i);

    Collections.sort(itineraries);
    return Collections.unmodifiableList(itineraries);
  }


  /**
  * Returns a representation of the time.
  */
  public String timeToString(){
    long hours = _time.toHours();
    long minutes = _time.toMinutes() - 60*hours;
    return String.format("%02d:%02d",hours, minutes);
  }
  

  /** @see java.lang.Object#toString() */
  @SuppressWarnings("nls")
  @Override
  public String toString() {
    return _passengerId + "|" + _name + "|" + _category + "|" + _numItineraries + "|" + String.format("%.2f",_amount) + "|" + timeToString();
  }

  /**
  * Returns a string that represents a passenger with itineraries.
  */ 
  public String passengerWithItinerariesToString() {
    String str = "";
    List<Itinerary> itineraries = getItineraries();
    int count = 0;

    str += "== Passageiro " +_passengerId + ": " +_name +" ==\n";
    for (Itinerary i: itineraries)
      str += "\n" + i.newToString(++count) +"\n";
    return str;
  }
}
    
  
  
