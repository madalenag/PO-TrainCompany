package mmt.app.main;

import mmt.TicketOffice;
import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.Input;
import java.io.IOException;
import java.io.FileNotFoundException;
import mmt.exceptions.ImportFileException;


/**
 * §3.1.1. Open existing document.
 */
public class DoOpen extends Command<TicketOffice> {
  /**
   * @param receiver
   */
  Input<String> _filename;
  
  public DoOpen(TicketOffice receiver) {
    super(Label.OPEN, receiver);
    _filename = _form.addStringInput(Message.openFile());
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  
  public final void execute() {
    _form.parse();
    try {
      _receiver.load(_filename.value());
    } catch (FileNotFoundException fnfe) {
        _display.popup(Message.fileNotFound());
    } catch (ClassNotFoundException | IOException e) {
        e.printStackTrace();
    }
  }

}
