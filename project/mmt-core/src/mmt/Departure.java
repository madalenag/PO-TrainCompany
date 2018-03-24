package mmt;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;


/**
 * A departure contains a station name and a departure time
 */
 
public class Departure implements Serializable {
  /** Departure station name */
  private String _stationName;
  
  /** The departure time */
  private LocalTime _departureTime;
  
  
  /**
  * Constructor.
  * 
  * @param name
  *          station name          
  * @param departure time
  */
  public Departure(String name, LocalTime departureTime) {
    _stationName = name;
    _departureTime = departureTime;
  }
  
  
  /**
  * Get the station name of this departure
  */
  public String getStationName() {
    return _stationName;
  }
  
  
  /**
  * Get the departure time
  */
  public LocalTime getTime() {
    return _departureTime;
  }
  
  /** @see java.lang.Object#toString() */
  @SuppressWarnings("nls")
  @Override
  public String toString() {
    return _departureTime + " " + _stationName;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Departure) {
      Departure d = (Departure) o;
      return _stationName.equals(d._stationName) && _departureTime.equals(d._departureTime);
    }
    return false;
  }
}
