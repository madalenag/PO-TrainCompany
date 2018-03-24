package mmt;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Iterator;

/**
 * A service contains information about departures, each one has a cost
 * and is identified by a number.
 */

public class Service implements Serializable, Iterable<Departure>{

  /** Service id. */
  private int _serviceId;
  
  /** Service cost. */
  private double _serviceCost;

  /** Departures. */
  private List<Departure> _departures = new ArrayList<Departure>();
  
  
  /**
  * Constructor.
  * 
  * @param serviceId
  *          service id           
  * @param serviceCost
  *          service cost
  */
  public Service(int serviceId) {
    _serviceId = serviceId;
  }

  public Service(int serviceId, double serviceCost) {
    this(serviceId);
    _serviceCost = serviceCost;
  }


  /**
  * Get the id of this service
  */
  public int getServiceId() {
    return _serviceId;
  }
  
  
  /**
  * Get the cost of this service
  */
  public double getServiceCost() {
    return _serviceCost;
  }
  
  
  /**
  * Get the first departure of this service
  */
  public String getDepartureStation() {
    return _departures.get(0).getStationName();
  }
  
  /**
  * Get the first departure of this service
  */
  public String getArrivalStation() {
    return _departures.get(_departures.size()-1).getStationName();
  }
  
  
  /**
  * Get the departure time of this service
  *
  * @return a local time correspondenting of the departure time
  */
  public LocalTime getDepartureTime() {
    return _departures.get(0).getTime();
  }
  
  /**
  * Get the arriving time of this service
  *
  * @return a local time correspondenting of the departure time
  */
  public LocalTime getArrivalTime() {
    return _departures.get(_departures.size()-1).getTime();
  }
   
  /**
  * Add a desparture in this service
  */
  public void addDeparture(Departure d) {
    _departures.add(d);
  }
  
  /**
  * Returns a list of all the departures of this service
  */
  public List<Departure> getDepartures() {
    return Collections.unmodifiableList(_departures);
  }


  public Duration getTotalTime() {
    return Duration.between(getDepartureTime(), getArrivalTime());
  }
  

  public double getCost() {
    return _serviceCost;
  }

  public Iterator<Departure> iterator() {
    return _departures.iterator();
  }

  public boolean equals(Object other) {
    if (other instanceof Service){
      Service service = (Service) other;
      return _serviceId == service.getServiceId();
    }
    return false;
  }

  
  public double getDeparturesCost(Departure d1, Departure d2) {
    Duration totalDuration = getTotalTime();
    LocalTime t1 = d1.getTime();
    LocalTime t2 = d2.getTime();
    Duration duration = Duration.between(t1, t2);
    return (_serviceCost * duration.toMinutes())/totalDuration.toMinutes();
  }

 
  /** @see java.lang.Object#toString() */
  @SuppressWarnings("nls")
  @Override
  public String toString() {
    String str = "";
    str += "Serviço #" + _serviceId + " @ " + String.format("%.2f",_serviceCost);
    for (Departure d: _departures)
      str += "\n" + d.toString();
    return str;
  }


  public List<Departure> getDeparturesBetween(Departure d1, Departure d2) {
    boolean add = false;
    List<Departure> departures = new ArrayList<Departure>();

    for (Departure d: _departures) {
      if (d.equals(d1) || add == true) {
        departures.add(d);
        add = true;
        if (d.equals(d2)) add = false;
      }
    }
    return departures;
  }

  
  public String serviceOfItineraryToString(double cost, Departure first, Departure last) {
    String str = "";

    str += "Serviço #" + _serviceId + " @ " + String.format("%.2f",cost);

    List<Departure> departures = getDeparturesBetween(first, last);
    for (Departure d: departures) {
      str += "\n" + d.toString();
    }
    return str;
  }
}
