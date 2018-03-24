package mmt;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Iterator;


public class Itinerary implements Serializable, Comparable<Itinerary>{
  
  private LocalDate _date;
  
  private List<Service> _itinServices = new ArrayList<Service>();
 
  private List<Departure> _itinDepartures = new ArrayList<Departure>();


  public Itinerary(LocalDate date) {
  	_date = date;
  }

  public void addService(Service s) {
    if (! _itinServices.contains(s))
      _itinServices.add(s);
  }

  public void addDeparture(Departure d) {
    if (! _itinServices.contains(d))
      _itinDepartures.add(d);
  }


  public Duration getTime() {
    Departure first = _itinDepartures.get(0);
    LocalTime departureTime = first.getTime();
    
    int lenght =_itinDepartures.size()-1;
    Departure last = _itinDepartures.get(lenght);
    LocalTime arrivalTime = last.getTime();

    return Duration.between(departureTime, arrivalTime);

  }


  public double getCost() {
    int i = 0;
    double totalCost = 0;
    Departure first, last;
    
    for (Service s: _itinServices) {
      first = _itinDepartures.get(i++);
      last = _itinDepartures.get(i++);
      totalCost += s.getDeparturesCost(first, last);
    }
    return totalCost;
  }


  public String newToString(int id) {
    String str ="";
    int i = 0;
    double cost;
    Departure first, last;

    str += "Itinerário " + id + " para " + _date + " @ " + String.format("%.2f",getCost());

    for (Service s: _itinServices) {
      first = _itinDepartures.get(i++);
      last = _itinDepartures.get(i++);
      cost = s.getDeparturesCost(first, last);
      str += "\n" + s.serviceOfItineraryToString(cost, first, last);
    }
    return str;
  }

  
  public LocalDate getDate() {
    return _date;
  }

  @Override
  public int compareTo(Itinerary other) {
    return _date.compareTo(other.getDate());
  }

  public List<Departure> getDepartures() {
    return _itinDepartures;
  }

  public List<Service> getServices() {
    return _itinServices;
  }

  public Service getFirstService() {
    return _itinServices.get(0);
  }

  public Departure getFirstDeparture() {
    return _itinDepartures.get(0);
  }

  public Departure getLastDeparture() {
    return _itinDepartures.get(_itinDepartures.size()-1);
  }


}