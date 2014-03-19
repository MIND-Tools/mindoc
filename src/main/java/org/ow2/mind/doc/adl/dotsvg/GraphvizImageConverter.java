/**
 * Copyright (C) 2012 Schneider Electric
 *
 * This file is part of "Mind Compiler" is free software: you can redistribute
 * it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact: mind@ow2.org
 *
 * Authors: Stéphane Seyvoz
 */

package org.ow2.mind.doc.adl.dotsvg;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.objectweb.fractal.adl.util.FractalADLLogManager;

public class GraphvizImageConverter {

  protected static Logger gicLogger = FractalADLLogManager.getLogger("GIC");

  private final String imageFormat;
  private Boolean dotExeStatus = null;

  public GraphvizImageConverter(final String imageFormat) {
    this.imageFormat = imageFormat;
  }

  /**
   * This method does nothing in the default configuration.
   * If the user specified "@DumpDot(generateImage=<format>)" where format is svg or png,
   * create a picture from the previously generated dot file.
   */
  public void convertDotToImage(final String dir, final String name) {

    if (imageFormat.equals("none"))
      return;

    final String graphVizCommand[] = {"dot", "-T" + imageFormat, dir + name + ".dot"};

    // Better than Runtime getRuntime exec !
    final ProcessBuilder builder = new ProcessBuilder(graphVizCommand);
    builder.redirectOutput(new File(dir + name + "." + imageFormat));
    try {
      //Use the following to track the process: Process graphVizProcess = builder.start();
      builder.start();
    } catch (final IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Convert dot file to image with the help of the external GraphViz dot tool.
   */
  public void convertDotToImage(final String dir, final String name, final String outputDir, final String shortDefName) {

    if (imageFormat.equals("none"))
      return;

    final String graphVizCommand[] = {"dot", "-T" + imageFormat, dir + name + ".dot"};

    // Better than Runtime getRuntime exec !
    final ProcessBuilder builder = new ProcessBuilder(graphVizCommand);
    builder.redirectOutput(new File(outputDir + shortDefName + "." + imageFormat));
    try {
      //Use the following to track the process: Process graphVizProcess = builder.start();
      final Process p = builder.start();
      // thread-blocking
      p.waitFor();
    } catch (final IOException e) {
      e.printStackTrace();
    } catch (final InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Check if the dot executable can be ran from the path.
   * @return true if ok, false on failure
   */
  public boolean canDotExecutableBeRan() {

    if (dotExeStatus == null) {

      // let's suppose we'll get success
      dotExeStatus = new Boolean(true);

      final String dotCommand[] = {"dot", "-V"};
      final ProcessBuilder builder = new ProcessBuilder(dotCommand);
      try {
        //Use the following to track the process: Process graphVizProcess = builder.start();
        final Process p = builder.start();
        // thread-blocking
        p.waitFor();
      } catch (final IOException e) {
         dotExeStatus = new Boolean(false);
      } catch (final InterruptedException e) {
        dotExeStatus = new Boolean(false);
      }

      if (dotExeStatus.equals(Boolean.FALSE))
        gicLogger.log(Level.INFO, "GraphViz 'dot' executable not found - SVG architecture graphics can't and will not be generated");
    }

    return dotExeStatus.booleanValue();
  }

}
