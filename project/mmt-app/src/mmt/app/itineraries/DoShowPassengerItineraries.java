package mmt.app.itineraries;

import mmt.TicketOffice;
import mmt.Itinerary;
import mmt.Service;
import mmt.Departure;
import mmt.Passenger;
import mmt.exceptions.NoSuchPassengerIdException;
import mmt.app.exceptions.NoSuchPassengerException;
import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;
import java.util.List;

/**
 * §3.4.2. Show all itineraries (for a specific passenger).
 */
public class DoShowPassengerItineraries extends Command<TicketOffice> {

  Input<Integer> _passengerId;

  /**
   * @param receiver
   */
  public DoShowPassengerItineraries(TicketOffice receiver) {
    super(Label.SHOW_PASSENGER_ITINERARIES, receiver);
    _passengerId = _form.addIntegerInput(Message.requestPassengerId());
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() throws DialogException {
    _form.parse();
    try {
      if (_receiver.passengerWithItineraries(_passengerId.value()))
        _display.addLine(_receiver.getPassengersItineraries(_passengerId.value()));
      else
        _display.addLine(Message.noItineraries(_passengerId.value()));
      _display.display();
    }
    catch (NoSuchPassengerIdException e) {
      throw new NoSuchPassengerException(_passengerId.value());
    }
  }

}
