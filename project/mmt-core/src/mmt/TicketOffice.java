package mmt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import mmt.exceptions.BadDateSpecificationException;
import mmt.exceptions.BadTimeSpecificationException;
import mmt.exceptions.ImportFileException;
import mmt.exceptions.InvalidPassengerNameException;
import mmt.exceptions.MissingFileAssociationException;
import mmt.exceptions.NoSuchPassengerIdException;
import mmt.exceptions.NoSuchServiceIdException;
import mmt.exceptions.NoSuchStationNameException;
import mmt.exceptions.NoSuchItineraryChoiceException;
import mmt.exceptions.NonUniquePassengerNameException;

import java.util.Collection;
import java.util.List;

/**
 * Façade for handling persistence and other functions.
 */
public class TicketOffice {

  /** The object doing most of the actual work. */
  private TrainCompany _trains = new TrainCompany();
    
  /** The associated File of this TicketOffice. */
  private String _associatedFile = null;
    
  /**
  * @return the associated file of this TrainCompany.
  */
  public String getAssociatedFile() {
    return _associatedFile;
  }
    
  /**
  * Save a file in the associatedFile of this TrainCompany.
  */ 
  public void save() throws IOException{
    save(_associatedFile);
  }
  
  /**
  * @param filename
  * @throws IOException
  */
  public void save(String filename) throws IOException {
    ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filename)));
    _associatedFile = filename;
    oos.writeObject(_trains);
    oos.close();
  }

  /**
  * Load the previous state of the application.
  * @param filename
  *          o nome do ficheiro com os dados serializados.
  * @throws IOException
  * @throws FileNotFoundException
  * @throws ClassNotFoundException
  */
  public void load(String filename) throws FileNotFoundException, IOException, ClassNotFoundException {
    ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)));
    _associatedFile = filename;
    _trains = (TrainCompany)ois.readObject();
    ois.close();
  }
    
    
  /**
  * Reset the application, only keeps the services of the previous TrainCompany.
  */  
  public void reset() {
    TrainCompany newTrain = new TrainCompany();
    copyServices(_trains,newTrain);
    _trains = newTrain;
    _associatedFile = null;
  }
  
  
  /**
  * @param datafile
  * @throws ImportFileException
  */
  public void importFile(String datafile) throws ImportFileException {
    _trains.importFile(datafile);
  }

  
  /**
  * @param passengerName
  *          the name of the passanger to regist.
  * @throws NonUniquePassengerNameException
  */
  public void registerPassenger(String passengerName) throws NonUniquePassengerNameException{
    _trains.registerPassenger(passengerName);
  }

  
  /**
  * @param passengerId
  *          the id of the passanger to change to show.
  * @throws NoSuchPassengerIdException
  *
  * @return the intended passenger.
  */
  public Passenger showPassengerById(int passengerId) throws NoSuchPassengerIdException{
    return _trains.showPassengerById(passengerId);
  }
  
  
  /**
  * @param passengerId
  *          the id of the passanger to change the name.
  * @param newName
  * @throws NonUniquePassengerNameException
  */
  public void changePassengerName(int passengerId, String newName) throws NoSuchPassengerIdException, NonUniquePassengerNameException {
    _trains.changePassengerName(passengerId, newName);
  }
  
  
  /**
  * @return a Collection of Passengers of this TrainCompany.
  */
  public Collection<Passenger> getPassengers() {
    return _trains.getPassengers();
  } 
  
  
  /**
  * @return a Collection of Services of this TrainCompany.
  */
  public Collection<Service> getServices() {
    return _trains.getServices();
  }
  
  
  /**
  * Copy services of a TrainCompany to another one.
  */
  public void copyServices(TrainCompany trains, TrainCompany newTrain) {
    for (Service s: trains.getServices())
      newTrain.addService(s);
  }
      
  
  /**
  * @param sId
  *          the id of the serving to show.
  * @throws NoSuchServiceIdException
  *
  * @return the intended service
  */
  public Service showServiceById(int sId) throws NoSuchServiceIdException {
    return _trains.showServiceById(sId);
  }
  
  
  /**
  * @return a List of Services of this TrainCompany that have a departing station,
  * ordered by their departing time
  */
  public List<Service> showServicesByDepartureStation(String stationName) throws NoSuchStationNameException{
    return _trains.showServicesByDepartureStation(stationName);
  }


  /**
  * @return a List of Services of this TrainCompany that have a departing station,
  * ordered by their departing time
  */
  public List<Service> showServicesByArrivalStation(String stationName) throws NoSuchStationNameException{
    return _trains.showServicesByArrivalStation(stationName);
  }

  public boolean passengerWithItineraries(Passenger p) {
    return _trains.passengerWithItineraries(p);
  }

  public boolean passengerWithItineraries(int pId) throws NoSuchPassengerIdException {
    return _trains.passengerWithItineraries(pId);
  }

  public String getPassengersItineraries(int passengerId) throws NoSuchPassengerIdException{
    return _trains.getPassengersItineraries(passengerId);
  }

  public String getAllItineraries() {
    return _trains.getAllItineraries();
  }

  public boolean hasItineraries() {
    return _trains.hasItineraries();
  }

  public void validChoice(int num, int passengerId) throws NoSuchItineraryChoiceException {
    _trains.validChoice(num, passengerId);
  }

  public String searchItineraries(int passengerId, String departureStation, String arrivalStation, String departureDate, String departureTime) 
                    throws NoSuchPassengerIdException, NoSuchStationNameException, BadTimeSpecificationException, BadDateSpecificationException {
    return _trains.getItineraries(passengerId, departureStation, arrivalStation, departureDate, departureTime);
  }

  public void commitItinerary(int passengerId, int itineraryChoice) throws NoSuchItineraryChoiceException, NoSuchPassengerIdException {
    _trains.commitItinerary(passengerId, itineraryChoice);
  }
}
