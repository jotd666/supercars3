package supercars3.editor;

import java.io.File;
import javax.swing.filechooser.*;

/**
 * <p>Titre : Asm Processor</p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2003</p>
 * <p>Société : Crack Inc.</p>
 * @author JOTD
 * @version 1.0
 */

public class CircuitImageFileFilter extends FileFilter {
  public CircuitImageFileFilter() {
  }

  public boolean accept(File f)
  {
    String s = f.toString().toLowerCase();

    String proj = s.replaceAll(".png", ".sc3");
    
    return ((f.isDirectory() || ((s.endsWith(".png"))) && 
    		(s.indexOf("_shadow") < 0) && !(new File(proj).exists())));
  }

  public String getDescription()
  {
    return "New circuit image files (*^(_shadow).png)";
  }
}
