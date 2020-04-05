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

public class ImageFileFilter extends FileFilter {
  public ImageFileFilter() {
  }

  public boolean accept(File f)
  {
    String s = f.toString().toLowerCase();

    return (f.isDirectory() || (s.endsWith(".png")));
  }

  public String getDescription()
  {
    return "Image files (*.png)";
  }
}
