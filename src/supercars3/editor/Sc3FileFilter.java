package supercars3.editor;

import java.io.File;
import javax.swing.filechooser.*;

import supercars3.base.DirectoryBase;

/**
 * <p>Titre : Asm Processor</p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2003</p>
 * <p>Société : Crack Inc.</p>
 * @author JOTD
 * @version 1.0
 */

public class Sc3FileFilter extends FileFilter {
  public Sc3FileFilter() {
  }

  public boolean accept(File f)
  {
    String s = f.toString().toLowerCase();

    return (f.isDirectory() || (s.endsWith(DirectoryBase.CIRCUIT_EXTENSION)));
  }

  public String getDescription()
  {
    return "Supercars 3 circuit files (*"+DirectoryBase.CIRCUIT_EXTENSION+")";
  }


}
