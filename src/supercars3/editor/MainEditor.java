package supercars3.editor;

import javax.swing.UIManager;
import java.awt.*;

/**
 * <p>Titre : Asm Processor</p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2003</p>
 * <p>Société : Crack Inc.</p>
 * @author JOTD
 * @version 1.0
 */

public class MainEditor {
  FrmEditor frame = null;

  public MainEditor()
  {
    frame = new FrmEditor();
    init();

  }
  public MainEditor(String map_name)
  {
    frame = new FrmEditor(map_name);
    init();

  }


  private void init()
  {
    boolean packframe = false;
    //Valider les cadres ayant des tailles prédéfinies
    //Compacter les cadres ayant des infos de taille préférées - ex. depuis leur disposition

    if (packframe)
    {
      frame.pack();
    }
    else
    {
      frame.validate();
    }

    //Centrer la fenêtre
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = frame.getSize();
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
    frame.setVisible(true);
  }
  //Méthode main
  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    if (args.length == 0)
    {
      new MainEditor();
    }
    else
    {
      new MainEditor(args[0]);
    }
  }
}
