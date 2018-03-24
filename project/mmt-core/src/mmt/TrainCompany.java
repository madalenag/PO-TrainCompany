package mmt;

import java.io.Serializable;
import mmt.exceptions.BadDateSpecificationException;
import mmt.exceptions.BadEntryException;
import mmt.exceptions.BadTimeSpecificationException;
import mmt.exceptions.InvalidPassengerNameException;
import mmt.exceptions.NoSuchDepartureException;
import mmt.exceptions.NoSuchPassengerIdException;
import mmt.exceptions.NoSuchServiceIdException;
import mmt.exceptions.NoSuchStationNameException;
import mmt.exceptions.NoSuchItineraryChoiceException;
import mmt.exceptions.NonUniquePassengerNameException;
import mmt.exceptions.ImportFileException;
import java.io.FileNotFoundException;

import java.util.Collections;
import java.util.Collection;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.Comparator;
import java.util.Iterator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.time.LocalTime;
import java.time.LocalDate;
import java.time.Duration;

/**
 * A train company has schedules (services) for its trains and passengers that
 * acquire itineraries based on those schedules.
 */
public class TrainCompany implements Serializable {

  /** Serial number for serialization. */
  private static final long serialVersionUID = 201708301010L;
  
  /** A map of passengers, organizated by their id. */
  private Map<Integer, Passenger> _passengers = new TreeMap<Integer, Passenger>();
  
  /** A list of services. */
  private Map<Integer, Service> _services = new TreeMap<Integer,Service>();
  
  /** The index of passengers of this traincompany. */
  private int _passengerIdx = 0;

  private List<Itinerary> _itineraries = new ArrayList<Itinerary>();
  /**
  * Import a text file and interpret it.
  *
  * @param datafile
  * @throws ImportFileException
  */
  public void importFile(String datafile) throws ImportFileException {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(datafile));
      String line;
        
      while ((line = reader.readLine()) != null) {
        String[] fields = line.split("\\|");
        registerFromFields(fields);
      }
      reader.close();
    } catch (NonUniquePassengerNameException e) {
      throw new ImportFileException();
    } catch (FileNotFoundException e) {
      throw new ImportFileException();
    } catch (IOException e) {
      throw new ImportFileException();
    }
  }
  
  
  /**
  * Check which class to regist.
  *
  * @param fields
  * @throws ImportFileException
  */ 
  private void registerFromFields(String[] fields) throws ImportFileException, NonUniquePassengerNameException {
    switch(fields[0]) {
      case "SERVICE":
        registerServiceFromFields(fields);
        break;

      case "PASSENGER":
        registerPassenger(fields[1]);
        break;

      case "ITINERARY":
        registerItineraryFromFields(fields);
        break;
      
      default:
        throw new ImportFileException();
    }
  }


  /**
  * Register a service from a file.
  *
  * @param fields
  * @throws ImportFileException
  */ 
  public void registerServiceFromFields(String[] fields) throws ImportFileException {
    if (fields.length < 5)
      throw new ImportFileException();
    
    int serviceId = Integer.parseInt(fields[1]);
    double serviceCost = Double.parseDouble(fields[2]);
    Service s = new Service(serviceId, serviceCost);

    for (int i = 3; i < fields.length ; i += 2) {
      LocalTime time = LocalTime.parse(fields[i]);
      String stationName = fields[i+1];
      s.addDeparture(new Departure(stationName, time));
    }
    _services.put(serviceId,s);
  }


  /**
  * Register a itinerary from a file.
  *
  * @param fields
  * @throws ImportFileException
  */ 
  public void registerItineraryFromFields(String[] fields) throws ImportFileException {
    if (fields.length < 4)
      throw new ImportFileException();
    
    int passengerId = Integer.parseInt(fields[1]);
    LocalDate date = LocalDate.parse(fields[2]);
    Itinerary itin = new Itinerary(date);

    for (int i = 3; i < fields.length ; i++) {
      String info[] = fields[i].split("/");
      int sId = Integer.parseInt(info[0]);
      String firstStation= info[1];
      String lastStation = info[2];

      Service s = _services.get(sId);
      itin.addService(s);
      addDeparturesToItinerary(s, itin, firstStation, lastStation);
    }
    Passenger p = _passengers.get(passengerId);
    p.commitItinerary(itin);
  }


  /**
  * Check if a departure has a specific name
  *
  * @param dep
  * @param stationName
  */ 

  public boolean checkStation(Departure dep, String stationName) {
    return dep.getStationName().equals(stationName);
  }

  /**
  * Add all the departures that are between two specified stations on the same service
  *
  * @param serv
  * @param itinerary
  * @param firstStaion
  * @param lastStation
  */ 
  public void addDeparturesToItinerary(Service serv, Itinerary itinerary, String firstStation, String lastStation) {
    for (Departure d: serv.getDepartures()) {
      if (checkStation(d, firstStation))
        itinerary.addDeparture(d);
      if (checkStation(d, lastStation))
        itinerary.addDeparture(d);
    }
  }
         
  /**
  * Returns a passanger with a correspondenting id.
  *
  * @param passengerId
  * @throws NoSuchPassengerIdException
  */ 
  public Passenger showPassengerById(int passengerId) throws NoSuchPassengerIdException {
    Passenger p = _passengers.get(passengerId);  
    if (p == null)
      throw new NoSuchPassengerIdException(passengerId);
    return p;
  }
  
  
  /**
  * Register and create a passenger.
  *
  * @param name
  *          the name of the new Passenger.
  * @throws NonUniquePassengerNameException, due to duplicateName function
  */ 
  public void registerPassenger(String name) throws NonUniquePassengerNameException {
    duplicateName(name);
    Passenger p = new Passenger(name, _passengerIdx);
    _passengers.put(_passengerIdx, p);
    _passengerIdx += 1;
  }
  
  
  /**
  * Change a passenger name with a specific id.
  *
  * @param passengerId
  * @param newName
  * @throws NoSuchPassengerIdException
  * @throws NonUniquePassengerNameException
  *
  * @return the passenger with a new name.
  */ 
  public void changePassengerName(int passengerId, String newName) throws NoSuchPassengerIdException, NonUniquePassengerNameException {
    duplicateName(newName);

    Passenger p = _passengers.get(passengerId);
    if (p == null)
      throw new NoSuchPassengerIdException(passengerId);
    
    p.setName(newName);
  }
  
  
  /**
  * Check if a passanger already has that name.
  *
  * @param name
  *          the name to check duplication.
  * @throws NonUniquePassengerNameException
  */ 
  public void duplicateName(String name) throws NonUniquePassengerNameException{
    for (Passenger p : _passengers.values())
      if (p.getName().equals(name))
        throw new NonUniquePassengerNameException(name);
  }
  
  
  /**
  * Return all the passengers of this TrainCompany.
  *
  * @return an unmodifiable collection with all the passengers.
  */ 
  public Collection<Passenger> getPassengers() {
    return Collections.unmodifiableCollection(_passengers.values());
  }
  
  
  /**
  * Add a service in this TrainCompany.
  *
  * @param Service
  */ 
  public void addService(Service s) {
    _services.put(s.getServiceId(),s);
  }
  
  
  /**
  * Return a service with a specific id.
  *
  * @param sId
  *          the requested service id.
  * @throws NoSuchServiceIdException
  */  
  public Service showServiceById(int sId) throws NoSuchServiceIdException {
    Service s = _services.get(sId);
    if (s == null)
      throw new NoSuchServiceIdException(sId);
    return s;
  }
  
  
  /**
  * Check if a station exists.
  *
  * @param name
  *          the name of the station.
  * @throws NoSuchStationNameException
  */
  private void existingStation(String stationName) throws NoSuchStationNameException {
    for (Service s : _services.values())
      for (Departure d: s.getDepartures()) {
        if (d.getStationName().equals(stationName))
          return;
      }
    throw new NoSuchStationNameException(stationName);
  }


  /**
  * Return an ordered list by a specified comparator that validates
  * a specific selector.
  *
  * @param ss
  *          the selector to validate
  * @param comparator
  *          the comparator that is going to be used for sorting
  */
  public List<Service> selectorFind(ServiceSelector ss, Comparator<Service> comparator) {
    List<Service> selectedServices = new ArrayList<Service>();

    for (Service s: _services.values()) {
      if(ss.ok(s))
        selectedServices.add(s);
    }
    Collections.sort(selectedServices, comparator);
    return Collections.unmodifiableList(selectedServices);
  }
  
  
  /**
  * Return the services of this TrainCompany that have a specific departing Station,
  * ordered by their departure time.
  *
  * @param stationName
  *          the departing station.
  * @throws NoSuchStationNameException, due to the function existingStation
  *
  * @return a list as an unmodifiable collection with services
  */    
  public List<Service> showServicesByDepartureStation(String stationName) throws NoSuchStationNameException {
    existingStation(stationName);

    class DepartureTimeSort implements Comparator<Service> {
      public int compare(Service s1, Service s2) {
        LocalTime t1 = s1.getDepartureTime();
        LocalTime t2 = s2.getDepartureTime();
        Duration duration = Duration.between(t2,t1);
        return (duration.isNegative() == true ? -1 : duration.isZero() == true ? 0:1);
      }
    }
    return selectorFind(new DepartureStationSelector(stationName), new DepartureTimeSort());
  }
  

  /**
  * Return the services of this TrainCompany that have a specific departing Station,
  * ordered by their arrival time.
  *
  * @param stationName
  *          the arrival station.
  * @throws NoSuchStationNameException, due to the function existingStation
  *
  * @return a list as an unmodifiable collection with services
  */    
  public List<Service> showServicesByArrivalStation(String stationName) throws NoSuchStationNameException {
    existingStation(stationName);

    class ArrivalTimeSort implements Comparator<Service>{
      public int compare (Service s1, Service s2) {
        LocalTime t1 = s1.getArrivalTime();
        LocalTime t2 = s2.getArrivalTime();
        Duration duration = Duration.between(t2,t1);
        return (duration.isNegative() == true ? -1 : duration.isZero() == true ? 0:1);
      }
    }
    return selectorFind(new ArrivalStationSelector(stationName), new ArrivalTimeSort());
  }

  /**
  * Returns the services of this TrainCompany.
  *
  * @return an unmodifiable collection with all the services.
  */ 
  public Collection<Service> getServices() {
    return Collections.unmodifiableCollection(_services.values());
  }


  /**
  * @param pass
  * @return true if a passanger has itineraries, false otherwise
  */  
  public boolean passengerWithItineraries(Passenger pass) {
    return pass.passengerWithItineraries();
  }

  
  /**
  * @param departure
  * @param time
  *       
  * @return true if a departure time is equals or after a specified time.
  */  
  public boolean checkTime(Departure departure, LocalTime time) {
    LocalTime t1 = departure.getTime();
    return t1.isAfter(time) || t1.equals(time);
  }

  /**
  * @param stations
  * @param station
  *         the station to be added
  * @return a list of stations equals to a previous one with one more element.
  */     
  public List<String> createLocalStations(List<String> stations, String station) {
    List<String> clonedStations = new ArrayList<String>();

    for (String s: stations)
      clonedStations.add(s);
    clonedStations.add(station);

    return clonedStations;
  }

  /**
  * @param departure
  * @param station
  * @param stations
  *       
  * @return true if a departure is equals or after a specified time.
  */  
  public boolean checkStation(Departure departure, String station, List<String> stations) {
    String name = departure.getStationName();
    if (!stations.contains(name))
      return name.equals(station);
    return false;
  }

  /**
  * @param station
  * @param time
  *
  * @return a list of services that have a specified station, and pass in
  * that station with a time after or equals a specified time.
  */  
  public List<Service> findAllServices(String station, LocalTime time) {
    List<Service> services = new ArrayList<Service>();

    for (Service s: _services.values()) {
      for (Departure d: s.getDepartures()) {
        if (checkStation(d, station) && !services.contains(s) && checkTime(d, time))
            services.add(s);
      }
    }
    return services;
  }
  
  /**
  * Return a list of all the services that have a 
  * station and validate a time, except the ones that contains
  *  a specified station and are equals to a specified service.
  *
  * @param station
  * @param other
  * @param time
  * @param notAllowedServices
  * @param stations
  */  
  public List<Service> findAllServicesExceptOne(String station, Service other, LocalTime time, List<Service> notAllowedServices) {
    List<Service> services = new ArrayList<Service>();

    for (Service s: _services.values()){
      if (!(other.equals(s)) && !notAllowedServices.contains(s)) {
        for (Departure d: s.getDepartures()) 
          if (checkStation(d, station) && !services.contains(s) && checkTime(d, time))
            services.add(s);
      }
    }
    return services;
  }


  /**
  * Return a list of all the services that are direct to a arrivalStation.
  *
  * @param departureStation
  * @param arrivalStation
  * @param services
  */  
  public List<Service> directServices(String departureStation, String arrivalStation, List<Service> services){
    List<Service> newServices = new ArrayList<Service>();

    for (Service s: services) {
      Departure departure = getDepartureByStationName(departureStation, s);
      LocalTime time = departure.getTime();

      for (Departure d: s.getDepartures())
        if ((checkStation(d, arrivalStation)) && checkTime(d, time) && !newServices.contains(s))
          newServices.add(s);
    }
    return newServices;
  }
  
  /**
  * Go to a specified station
  *
  * @param stationName
  * @param it
  *         the iterator of a service
  * @param s
  *         the service that passes in this station
  */    
  public Departure goToStation(String stationName, Iterator<Departure> it, Service s) {
    boolean findDeparture = false;
    
    if (it.hasNext()) {
      Departure nextDeparture = it.next();
      if (checkStation(nextDeparture, stationName))
        return nextDeparture;
      
      while (it.hasNext() && findDeparture == false) {
        nextDeparture = it.next();
        if (checkStation(nextDeparture, stationName)) 
          findDeparture = true;
      }
      return nextDeparture;
    }
    return null;
  }


  /**
  * @param services
  * @param service
  *         the service to be added
  * @return a list of services equals to a previous one with one more element.
  */     
  public List<Service> createLocalServices(List<Service> services, Service service) {
    List<Service> clonedServices = new ArrayList<Service>();
    
    for (Service s: services) 
      clonedServices.add(s);
    clonedServices.add(service);

    return clonedServices;
  }


  /**
  * @param stationName
  * @param service
  *
  * @return a departure that belongs to a specified service.
  */   
  public Departure getDepartureByStationName(String stationName, Service service) {
    for (Departure d: service.getDepartures()) 
      if (checkStation(d, stationName))
        return d;

    return null;
  }


  /**
  * @param stationName
  * @param services
  *
  * @return a list of departures that belongs to specified services.
  */   
  public List<Departure> getDeparturesByStation(String stationName, List<Service> services) {
    List<Departure> departures = new ArrayList<Departure>();

    for (Service s: services) {
      Departure departure = getDepartureByStationName(stationName, s);
      if (departure != null && !departures.contains(departure))
        departures.add(departure);
    }
    return departures;
  }

  /**
  * Add a itinerary to the list that contains all the possibles
  * itineraries of a search.
  *
  * @param itineraries
  *           a list with all the itinerary parts that were building.
  * @param date
  * @param service
  * @param notAllowedServices
  *           this two last parameters correspond to all the services 
  *           that were percurred in this itinerary.
  */   
  public void concatenateItinerary(List<Itinerary> itineraries, LocalDate date, List<Itinerary> possibilities, Service service, String arrivalStation, List<Service> notAllowedServices) {
    Itinerary realItinerary = new Itinerary(date);
    Itinerary prev = itineraries.get(0);
    realItinerary.addDeparture(prev.getFirstDeparture());

    for (Service s: notAllowedServices)
      realItinerary.addService(s);
    realItinerary.addService(service);

    for (Itinerary curr: itineraries) {
      Service prevService = prev.getFirstService();
      Service currService = curr.getFirstService();
      if (!currService.equals(prevService)) {
        realItinerary.addDeparture(prev.getFirstDeparture());
        realItinerary.addDeparture(curr.getFirstDeparture());
      }
      prev = curr;
    }
    Departure last = getDepartureByStationName(arrivalStation, service);
    realItinerary.addDeparture(last);
    possibilities.add(realItinerary);
  }

  /**
  * Create a direct itinerary.
  *
  * @param service
  * @param departureStation
  * @param arrivalStation
  * @param possibilities
  * @param date
  */ 
  public void createDirectItinerary(Service service, String departureStation, String arrivalStation, List<Itinerary> possibilities, 
    LocalDate date) {

    Itinerary i = new Itinerary(date);
    i.addService(service);
    addDeparturesToItinerary(service, i, departureStation, arrivalStation);
    possibilities.add(i);
  }
  
  
  /**
  * @param s
  * @param arrivalStation
  * @param time
  *
  * @return true if a service is direct to a arrival station, false otherwise.
  */ 
  public boolean isDirect(Service s, String arrivalStation, LocalTime time) {
    for (Departure d: s.getDepartures()) 
      if ((checkStation(d, arrivalStation)) && (checkTime(d, time)))
        return true;
    return false;
  }


  /**
  * Create a part of an itinerary.
  *
  * @param service
  * @param departure
  * @param itineraries
  *          the list of all the itineraries parts that were building in this search.
  * @param possibilities
  * @param date
  */ 
  public Itinerary createItineraryPart(Service service, Departure departure, List<Itinerary> itineraries, LocalDate date) {
    Itinerary i = new Itinerary(date);
    i.addDeparture(departure);
    i.addService(service);
    itineraries.add(i);  
    return i;
  }


  /**
  * @param atual
  * @param it
  * @param service
  * @param arrivalStation
  * @param time
  * @param stations
  * @param notAllowedServices
  * @param possibilities
  * @param date
  */ 
  public void checkSearch(String atual, Iterator<Departure> it, Service service, String arrivalStation, LocalTime time, 
    List<String> stations, List<Service> notAllowedServices, List<Itinerary> possibilities, LocalDate date, 
      List<Itinerary> itineraries, Itinerary itinerary) {
    
    if (it.hasNext()) {
      Departure next = it.next();
      String nextStation = next.getStationName();

      if (checkTime(next, time) && !stations.contains(nextStation)) {
        List<String> newStations = createLocalStations(stations, atual);
        Itinerary itin = createItineraryPart(service, next, itineraries, date);
        search(nextStation, it, service, arrivalStation, next.getTime(), newStations, notAllowedServices, possibilities, 
          date, itineraries, itin);
        itineraries.remove(itin);
      }
    }
  }
  
  /**
  * @param atual
  * @param it
  * @param service
  * @param arrivalStation
  * @param time
  * @param stations
  * @param notAllowedServices
  * @param possibilities
  * @param date
  */ 
  public void search(String atual, Iterator<Departure> it, Service service, String arrivalStation, LocalTime time, 
    List<String> stations, List<Service> notAllowedServices, List<Itinerary> possibilities, LocalDate date, 
    List<Itinerary> itineraries, Itinerary itinerary) {

    if (isDirect(service, arrivalStation, time)) {
      concatenateItinerary(itineraries, date, possibilities, service, arrivalStation, notAllowedServices);
      return;
    }
    else 
      checkSearch(atual, it, service, arrivalStation, time, stations, notAllowedServices, possibilities, 
        date, itineraries, itinerary);

    List<Service> services = findAllServicesExceptOne(atual, service, time, notAllowedServices);
    for (Service s: services) {
      List<Service> newNotAllowedServices = createLocalServices(notAllowedServices, service);
      Iterator<Departure> iter = s.iterator();
      Departure first = goToStation(atual, iter, s);
      Itinerary itiner = createItineraryPart(s, first, itineraries, date);
      checkSearch(atual, iter, s, arrivalStation, first.getTime(), stations, newNotAllowedServices, possibilities, 
        date, itineraries, itiner);
      itineraries.remove(itiner);
    }
  }
  

  /**
  * @param passengerId
  * @param departureStation
  * @param arrivalStation
  * @param departureDate
  * @param departureTime
  *
  * @return a list of all the itineraries founded in this search.
  */ 
  public List<Itinerary> searchItineraries(int passengerId, String departureStation, String arrivalStation, LocalDate date, LocalTime time) {
    List<Itinerary> possibilities = new ArrayList<Itinerary>();
    List<Itinerary> itineraries = new ArrayList<Itinerary>();
    List<String> stations = new ArrayList<String>();
    List<Service> notAllowedServices = new ArrayList<Service>(); 
    
    List<Service> services = findAllServices(departureStation, time);
    List<Service> directServices = directServices(departureStation, arrivalStation, services);
    
    if (!directServices.isEmpty()) 
      for (Service s: directServices) 
        createDirectItinerary(s, departureStation, arrivalStation, possibilities, date);
    else {
      for (Service s: services) {
        Iterator<Departure> it = s.iterator();
        Departure first = goToStation(departureStation, it, s);
        Itinerary itinerary = createItineraryPart(s, first, itineraries, date);
        search(departureStation, it, s, arrivalStation, first.getTime(), stations, notAllowedServices, possibilities, date, itineraries, itinerary);
        itineraries.remove(itinerary);
      }
    }
    return possibilities;
  }
  
  
  /**
  * Sort a list of itineraries by their departure time.
  * @param itineraries.
  */    
  public void sortItinerariesByDepartureTime(List<Itinerary> itineraries){ 
    class DepartureTimeComparator implements Comparator<Itinerary> {
      public int compare(Itinerary i1, Itinerary i2) {
        Departure d1 = i1.getFirstDeparture();
        Departure d2 = i2.getFirstDeparture();
        LocalTime t1 = d1.getTime();
        LocalTime t2 = d2.getTime();
        Duration duration = Duration.between(t2,t1);
        return (duration.isNegative() == true ? -1 : duration.isZero() == true ? 0:1);
      }
    }
    Collections.sort(itineraries, new DepartureTimeComparator());
  }

  /**
  * Sort a list of itineraries by their arrival time.
  * @param itineraries.
  */  
  public void sortItinerariesByArrivalTime(List<Itinerary> itineraries){
    class ArrivalTimeComparator implements Comparator<Itinerary> {
      public int compare(Itinerary i1, Itinerary i2) {
        Departure d1 = i1.getLastDeparture();
        Departure d2 = i2.getLastDeparture();
        LocalTime t1 = d1.getTime();
        LocalTime t2 = d2.getTime();
        Duration duration = Duration.between(t2,t1);
        return (duration.isNegative() == true ? -1 : duration.isZero() == true ? 0:1);
      }
    }
    Collections.sort(itineraries, new ArrivalTimeComparator());
  }

  /**
  * Sort a list of itineraries by their cost.
  * @param itineraries.
  */  
  public void sortItinerariesByCost(List<Itinerary> itineraries){
    class CostComparator implements Comparator<Itinerary> {
      public int compare(Itinerary i1, Itinerary i2) {
        double cost1 = i1.getCost();
        double cost2 = i2.getCost();
        double res = cost1 - cost2;
        return (res < 0 ? -1 : res == 0 ? 0:1);
      }
    }
    Collections.sort(itineraries, new CostComparator());
  }

  /**
  * Sort a list of itineraries by their cost, arrival time
  * and departure time.
  *
  * @param itineraries.
  */  
  public void tripleSort(List<Itinerary> itineraries) {
    sortItinerariesByCost(itineraries);
    sortItinerariesByArrivalTime(itineraries);
    sortItinerariesByDepartureTime(itineraries);
  }


  /**
  * @param itineraries.
  */  
  public Itinerary getTheBestItinerary(List<Itinerary> itineraries) {
    sortItinerariesByArrivalTime(itineraries);
    return itineraries.get(0);
  }


  /**
  * @param itineraries.
  */  
  public List<Itinerary> getTotalItineraries(List<Itinerary> itineraries) {
    List<Itinerary> total = new ArrayList<Itinerary>();
    List<Service> allServices = new ArrayList<Service>(); 
    
    for (Itinerary i: itineraries) {
      Service s = i.getFirstService();
      if (!allServices.contains(s)) 
        allServices.add(s); 
    }

    for (Service s: allServices) {
      List<Itinerary> itinerariesWithInitialService;
      itinerariesWithInitialService = new ArrayList<Itinerary>();
      for (Itinerary i: itineraries) 
        if (i.getFirstService().equals(s))
          itinerariesWithInitialService.add(i);
      Itinerary itinerary = getTheBestItinerary(itinerariesWithInitialService);
      total.add(itinerary);
    }
    
    tripleSort(total);
    return total;
  }


  /**
  * Get all the itineraries that have a departureStation, arrivalStation, departureDate,
  * departureTime.
  *
  * @param passengerId
  * @param departureStation
  * @param arrivalStation
  * @param departureDate
  * @param departureTime
  * @throws NoSuchPassengerIdException
  * @throws NoSuchStationNameException
  * @throws BadTimeSpecificationException
  * @throws BadDateSpecificationException
  *
  * @return a string that represents the sorted itineraries founded.
  */ 
  public String getItineraries(int passengerId, String departureStation, String arrivalStation, String departureDate, String departureTime) 
    throws NoSuchPassengerIdException, NoSuchStationNameException, BadTimeSpecificationException, BadDateSpecificationException {

    LocalTime time = LocalTime.parse(departureTime);
    LocalDate date = LocalDate.parse(departureDate);
    existingStation(departureStation);
    existingStation(arrivalStation);
    showPassengerById(passengerId);
    
    List<Itinerary> possibilities;
    possibilities = searchItineraries(passengerId, departureStation, arrivalStation, date, time);

    _itineraries = getTotalItineraries(possibilities);
    
    String str = "";
    int count = 0;
    for (Itinerary i: _itineraries)
      str += "\n" + i.newToString(++count) + "\n";
    return str;
  }


 /**
  * @return true if any itinerary was found, false otherwise.
  */
  public boolean hasItineraries() {
    return !_itineraries.isEmpty();
  }


 /**
  * @throws NoSuchItineraryChoiceException
  */
  public void validChoice(int num, int passengerId) throws NoSuchItineraryChoiceException {
    int size = _itineraries.size();
    if (num < 0 || num > size) 
      throw new NoSuchItineraryChoiceException(passengerId, num);
  }


 /**
  * @throws NoSuchItineraryChoiceException
  * @throws NoSuchPassengerIdException
  */
  public void commitItinerary(int passengerId, int itineraryChoice) throws NoSuchItineraryChoiceException, NoSuchPassengerIdException {
    if (itineraryChoice == 0)
      return;
    validChoice(itineraryChoice, passengerId);
    Passenger p = showPassengerById(passengerId);
    Itinerary itinerary = _itineraries.get(itineraryChoice - 1);
    p.commitItinerary(itinerary);
  }

 /**
  * @throws NoSuchPassengerIdException
  */
  public String getPassengersItineraries(int passengerId) throws NoSuchPassengerIdException{
    Passenger p = showPassengerById(passengerId);
    return p.passengerWithItinerariesToString();
  }


 /**
  * @throws NoSuchPassengerIdException
  */
  public boolean passengerWithItineraries(int pId) throws NoSuchPassengerIdException {
    Passenger p = showPassengerById(pId);
    return p.passengerWithItineraries();
  }

 /**
  * Get all the itineraries of this Train Company.
  */
  public String getAllItineraries() {
    String str = "";
    for (Passenger p: _passengers.values())
      if (p.passengerWithItineraries())
        str += p.passengerWithItinerariesToString();
    return str;
  }
}



