package mmt.app.main;

import java.io.IOException;

import mmt.TicketOffice;
import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.Input;

/**
 * §3.1.1. Save to file under current name (if unnamed, query for name).
 */
public class DoSave extends Command<TicketOffice> {
  
  Input<String> _filename;

  /**
   * @param receiver
   */
  public DoSave(TicketOffice receiver) {
    super(Label.SAVE, receiver);
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() {
    if (_receiver.getAssociatedFile() != null) {
      try {
        _receiver.save();
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      _filename = _form.addStringInput(Message.newSaveAs());
      _form.parse();
      try {
        _receiver.save(_filename.value());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
