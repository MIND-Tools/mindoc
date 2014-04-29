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

package org.ow2.mind.doc.adl.graph;

import java.io.File;
import java.util.Map;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.objectweb.fractal.adl.ADLException;
import org.objectweb.fractal.adl.Definition;
import org.objectweb.fractal.adl.Loader;
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
import org.ow2.mind.idl.IDLLoader;
import org.ow2.mind.io.BasicOutputFileLocator;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * @author Julien TOUS
 * @author Stephane Seyvoz
 */
public class Gv2SVGProcessor {

  /*
   * Works because our Loader is itself loaded by Google Guice.
   */
  @Inject
  protected Injector injector;

  @Inject
  protected IDLLoader idlLoaderItf;

  @Inject
  protected Loader adlLoaderItf;

  private String buildDir;
  private final GraphvizImageConverter gic;

  // The Gv2SVGProcessor logger
  protected Logger dotLogger = FractalADLLogManager
      .getLogger("Gv2SVG");

  public Gv2SVGProcessor() {
    gic = new GraphvizImageConverter("svg");
  }

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
      if (itf.getRole().equals(TypeInterface.SERVER_ROLE))
        currentDefinitionDot.addServer(itf.getName(), itf.getSignature());
      if (itf.getRole().equals(TypeInterface.CLIENT_ROLE))
        currentDefinitionDot.addClient(itf.getName(), itf.getSignature());
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
      if (itf.getRole().equals(TypeInterface.SERVER_ROLE))
        currentDefinitionDot.addServer(itf.getName(), itf.getSignature());
      if (itf.getRole().equals(TypeInterface.CLIENT_ROLE))
        currentDefinitionDot.addClient(itf.getName(), itf.getSignature());
    }
  }

  private void showType(final Definition definition, final DotWriter currentDefinitionDot) {

    final TreeSet<MindInterface> interfaces = new TreeSet<MindInterface>(new MindInterfaceComparator());
    for (final Interface itf : ((InterfaceContainer) definition).getInterfaces())
      interfaces.add((MindInterface) itf);

    for (final MindInterface itf : interfaces) {
      if (itf.getRole().equals(TypeInterface.SERVER_ROLE))
        currentDefinitionDot.addServer(itf.getName(), itf.getSignature());
      if (itf.getRole().equals(TypeInterface.CLIENT_ROLE))
        currentDefinitionDot.addClient(itf.getName(), itf.getSignature());
    }
  }

  /**
   * Entry point
   * @param definition
   * @param cont
   * @throws ADLException
   */
  public void process(final Definition definition, final Map<Object, Object> cont) {

    final File outputDir = (File) cont.get(BasicOutputFileLocator.OUTPUT_DIR_CONTEXT_KEY);
    // useful for some test cases where output directory isn't configured
    if (outputDir == null) return;
    buildDir = outputDir.getPath() + File.separator;

    if (!gic.canDotExecutableBeRan()) {
      return;
    }

    // If Dot is accessible we can create SVGs, let's enable the according HTML section
    definition.astSetDecoration("embed-svg", true);

    // Create files
    dotLogger.log(Level.FINE, "Building GV file for " + definition.getName() + " definition");

    // Get instance from the injector so its @Inject fields get properly injected (ADL Loader especially)
    final DotWriter dotWriter = injector.getInstance(DotWriter.class);

    // Select surrounding digraph style according to type
    if (ASTHelper.isType(definition))
      dotWriter.init(buildDir, definition, null, true, cont);
    else
      // standard mode
      dotWriter.init(buildDir, definition, null, false, cont);

    // Start file write
    if (ASTHelper.isComposite(definition)) {
      showComposite(definition, dotWriter);
    } else if (ASTHelper.isPrimitive(definition)) {
      showPrimitive(definition, dotWriter);
    } else if (ASTHelper.isType(definition)) {
      showType(definition, dotWriter);
    }

    dotWriter.close();

    // the mindoc @figure tag uses the package name for folders and subfolder "doc-files"
    final String packageDirName = PathHelper.fullyQualifiedNameToDirName(definition.getName());
    // here the return dirName will start with "/" : careful !
    // and add the mindoc "doc-files" folder as a convention
    final String targetDocFilesDirName = buildDir + packageDirName.substring(1) + File.separator + "doc-files" + File.separator;
    final File currentDocFilesDir = new File(targetDocFilesDirName);
    currentDocFilesDir.mkdirs();

    // compute definition short name (removing package)
    String shortDefName = null;
    final int i = definition.getName().lastIndexOf('.');
    if (i == -1) shortDefName = definition.getName();
    else shortDefName = definition.getName().substring(i + 1);

    dotLogger.log(Level.FINE, "Converting " + buildDir.toString() + definition.getName() + ".gv to " + targetDocFilesDirName + shortDefName + ".svg");
    gic.convertGvToImage(buildDir, definition.getName(), targetDocFilesDirName, shortDefName);

    // cleanup
    final Boolean keepGVStatus = (Boolean) cont.get("org.ow2.mind.doc.KeepGV");
    if (keepGVStatus != null && !keepGVStatus)
      dotWriter.deleteFile();
  }

}
