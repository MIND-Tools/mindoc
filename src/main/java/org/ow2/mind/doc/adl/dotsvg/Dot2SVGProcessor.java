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
 * Authors: Julien TOUS
 * Contributors: Stéphane Seyvoz
 */

package org.ow2.mind.doc.adl.dotsvg;

import java.io.File;
import java.util.Map;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.objectweb.fractal.adl.ADLException;
import org.objectweb.fractal.adl.Definition;
import org.objectweb.fractal.adl.interfaces.Interface;
import org.objectweb.fractal.adl.interfaces.InterfaceContainer;
import org.objectweb.fractal.adl.types.TypeInterface;
import org.objectweb.fractal.adl.util.FractalADLLogManager;
import org.ow2.mind.PathHelper;
import org.ow2.mind.adl.ast.ASTHelper;
import org.ow2.mind.adl.ast.Binding;
import org.ow2.mind.adl.ast.BindingContainer;
import org.ow2.mind.adl.ast.Component;
import org.ow2.mind.adl.ast.ComponentContainer;
import org.ow2.mind.adl.ast.ImplementationContainer;
import org.ow2.mind.adl.ast.MindInterface;
import org.ow2.mind.adl.ast.Source;
import org.ow2.mind.io.BasicOutputFileLocator;

/**
 * @author Julien TOUS
 * @author Stephane Seyvoz
 */
public class Dot2SVGProcessor {

  private String buildDir;
  private GraphvizImageConverter gic;

  private static Dot2SVGProcessor instance = null;

  // The Dot2SVGProcessor logger
  protected static Logger dotLogger = FractalADLLogManager
      .getLogger("Dot2SVG");

  private void showComposite(final Definition definition, final DotWriter currentDefinitionDot) {
    final Component[] subComponents = ((ComponentContainer) definition)
        .getComponents();
    for (final Component subComponent : subComponents)
      currentDefinitionDot.addSubComponent(subComponent);

    final TreeSet<Binding> bindings = new TreeSet<Binding>( new BindingComparator() );
    for ( final Binding binding: ((BindingContainer) definition).getBindings() )
      bindings.add(binding);

    for (final Binding binding : bindings)
      currentDefinitionDot.addBinding(binding);

    final TreeSet<MindInterface> interfaces = new TreeSet<MindInterface>(new MindInterfaceComparator());
    for (final Interface itf : ((InterfaceContainer) definition).getInterfaces())
      interfaces.add((MindInterface) itf);

    for (final MindInterface itf : interfaces) {
      if (itf.getRole()==TypeInterface.SERVER_ROLE)
        currentDefinitionDot.addServer(itf.getName());
      if (itf.getRole()==TypeInterface.CLIENT_ROLE)
        currentDefinitionDot.addClient(itf.getName());
    }
  }

  private void showPrimitive(final Definition definition, final DotWriter currentDefinitionDot) {
    final Source[] sources = ((ImplementationContainer) definition).getSources();

    for (final Source source : sources)
      currentDefinitionDot.addSource(source);

    final TreeSet<MindInterface> interfaces = new TreeSet<MindInterface>(new MindInterfaceComparator());
    for (final Interface itf : ((InterfaceContainer) definition).getInterfaces())
      interfaces.add((MindInterface) itf);

    for (final MindInterface itf : interfaces) {
      if (itf.getRole()==TypeInterface.SERVER_ROLE)
        currentDefinitionDot.addServer(itf.getName());
      if (itf.getRole()==TypeInterface.CLIENT_ROLE)
        currentDefinitionDot.addClient(itf.getName());
    }
  }

  private void showType(final Definition definition, final DotWriter currentDefinitionDot) {

    final TreeSet<MindInterface> interfaces = new TreeSet<MindInterface>(new MindInterfaceComparator());
    for (final Interface itf : ((InterfaceContainer) definition).getInterfaces())
      interfaces.add((MindInterface) itf);

    for (final MindInterface itf : interfaces) {
      if (itf.getRole()==TypeInterface.SERVER_ROLE)
        currentDefinitionDot.addServer(itf.getName());
      if (itf.getRole()==TypeInterface.CLIENT_ROLE)
        currentDefinitionDot.addClient(itf.getName());
    }
  }

  private Dot2SVGProcessor(final Map<Object, Object> cont) {
    gic = new GraphvizImageConverter("svg");
    final File outputDir = (File) cont.get(BasicOutputFileLocator.OUTPUT_DIR_CONTEXT_KEY);
    // useful for some test cases where output directory isn't configured
    if (outputDir == null) return;
    buildDir = outputDir.getPath() + File.separator;
  }


  /**
   * Entry point
   * @param definition
   * @param cont
   * @throws ADLException
   */
  public static void process(final Definition definition, final Map<Object, Object> cont) {

    final DotWriter dotWriter;

    if (instance == null)
      instance = new Dot2SVGProcessor(cont);

    if (!instance.gic.canDotExecutableBeRan()) {
      return;
    }

    // If Dot is accessible we can create SVGs, let's enable the according HTML section
    definition.astSetDecoration("embed-svg", true);

    // Create files
    dotLogger.log(Level.FINE, "Building Dot file for " + definition.getName() + " definition");


    // Select surrounding digraph style according to type
    if (ASTHelper.isType(definition))
      dotWriter = new DotWriter(instance.buildDir, definition.getName(), true);
    else
      // standard mode
      dotWriter = new DotWriter(instance.buildDir, definition.getName());

    // Start file write
    if (ASTHelper.isComposite(definition)) {
      instance.showComposite(definition, dotWriter);
    } else if (ASTHelper.isPrimitive(definition)) {
      instance.showPrimitive(definition, dotWriter);
    } else if (ASTHelper.isType(definition)) {
      instance.showType(definition, dotWriter);
    }

    dotWriter.close();

    // the mindoc @figure tag uses the package name for folders and subfolder "doc-files"
    final String packageDirName = PathHelper.fullyQualifiedNameToDirName(definition.getName());
    // here the return dirName will start with "/" : careful !
    // and add the mindoc "doc-files" folder as a convention
    final String targetDocFilesDirName = instance.buildDir + packageDirName.substring(1) + File.separator + "doc-files" + File.separator;
    final File currentDocFilesDir = new File(targetDocFilesDirName);
    currentDocFilesDir.mkdirs();

    // compute definition short name (removing package)
    String shortDefName = null;
    final int i = definition.getName().lastIndexOf('.');
    if (i == -1) shortDefName = definition.getName();
    else shortDefName = definition.getName().substring(i + 1);

    dotLogger.log(Level.FINE, "Converting " + instance.buildDir.toString() + definition.getName() + ".dot to " + targetDocFilesDirName + shortDefName + ".svg");
    instance.gic.convertDotToImage(instance.buildDir, definition.getName(), targetDocFilesDirName, shortDefName);

    // cleanup
    final Boolean keepDotStatus = (Boolean) cont.get("org.ow2.mind.doc.KeepDot");
    if (keepDotStatus != null && !keepDotStatus)
      dotWriter.deleteFile();
  }

}
