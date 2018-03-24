package mmt.app.itineraries;

import mmt.TicketOffice;
import mmt.Itinerary;
import mmt.Passenger;
import java.util.List;
import mmt.app.exceptions.BadDateException;
import mmt.app.exceptions.BadTimeException;
import mmt.app.exceptions.NoSuchItineraryException;
import mmt.app.exceptions.NoSuchPassengerException;
import mmt.app.exceptions.NoSuchStationException;
import mmt.exceptions.BadDateSpecificationException;
import mmt.exceptions.BadTimeSpecificationException;
import mmt.exceptions.NoSuchPassengerIdException;
import mmt.exceptions.NoSuchStationNameException;
import mmt.exceptions.NoSuchItineraryChoiceException;
import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;

//FIXME import other classes if necessary

/**
 * §3.4.3. Add new itinerary.
 */
public class DoRegisterItinerary extends Command<TicketOffice> {

  Input<Integer> _passengerId;
  Input<String> _departureStation;
  Input<String> _arrivalStation;
  Input<String> _departureDate;
  Input<String> _departureTime;
  Input<Integer> _itineraryChoice;

  /**
   * @param receiver
   */
  public DoRegisterItinerary(TicketOffice receiver) {
    super(Label.REGISTER_ITINERARY, receiver);
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() throws DialogException {
    _passengerId = _form.addIntegerInput(Message.requestPassengerId());
    _departureStation = _form.addStringInput(Message.requestDepartureStationName());
    _arrivalStation = _form.addStringInput(Message.requestArrivalStationName());
    _departureDate = _form.addStringInput(Message.requestDepartureDate());
    _departureTime = _form.addStringInput(Message.requestDepartureTime());
    
    _form.parse();
    try {
      String itineraries = _receiver.searchItineraries(_passengerId.value(), _departureStation.value(), _arrivalStation.value(), _departureDate.value(), _departureTime.value());
      Passenger p = _receiver.showPassengerById(_passengerId.value());

      if (_receiver.hasItineraries()) {
        _display.addLine(itineraries);
        _display.display();
        _form.clear();

        _itineraryChoice = _form.addIntegerInput(Message.requestItineraryChoice());
        _form.parse();
        _receiver.commitItinerary(_passengerId.value(), _itineraryChoice.value());
      }

    } catch (NoSuchPassengerIdException e) {
      throw new NoSuchPassengerException(e.getId());
    } catch (NoSuchStationNameException e) {
      throw new NoSuchStationException(e.getName());
    } catch (NoSuchItineraryChoiceException e) {
      _form.clear();
      throw new NoSuchItineraryException(e.getPassengerId(), e.getItineraryId());
    } catch (BadDateSpecificationException e) {
      throw new BadDateException(e.getDate());
    } catch (BadTimeSpecificationException e) {
      throw new BadTimeException(e.getTime());
    }
    _form.clear();
  }
}
