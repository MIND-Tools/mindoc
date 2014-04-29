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

import static org.ow2.mind.PathHelper.fullyQualifiedNameToPath;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.TreeSet;

import org.objectweb.fractal.adl.interfaces.Interface;
import org.objectweb.fractal.adl.interfaces.InterfaceContainer;
import org.ow2.mind.NameHelper;
import org.ow2.mind.PathHelper;
import org.ow2.mind.adl.ast.ASTHelper;
import org.ow2.mind.adl.ast.Component;
import org.ow2.mind.adl.ast.DefinitionReference;
import org.ow2.mind.adl.ast.MindInterface;
import org.ow2.mind.adl.ast.Source;
import org.objectweb.fractal.adl.ADLException;
import org.objectweb.fractal.adl.Definition;
import org.objectweb.fractal.adl.Loader;
import org.ow2.mind.adl.ast.Binding;
import org.ow2.mind.adl.generic.ast.FormalTypeParameterReference;
import org.ow2.mind.adl.implementation.ImplementationLocator;
import org.ow2.mind.doc.HTMLDocumentationHelper;
import org.ow2.mind.io.OutputFileLocator;
import org.objectweb.fractal.adl.types.TypeInterface;

import com.google.inject.Inject;


public class DotWriter {

  /**
   * The PrintWriter that will be used for all code generation of this component.
   */
  private PrintWriter currentPrinter;
  /**
   * The instance name of this component :
   * containing all levels of composite from the top level component
   */
  private String compName;
  /**
   * The instance name of this component :
   * as stated on the "contains" line in ADL
   */
  private String localName;
  /**
   * The directory where the graphviz files will be located
   */
  private String buildDir;
  /**
   * The graphviz file for this component
   */
  private String fileName;
  /**
   * The graphviz source code string that will represent the sources
   */
  private String srcs="{\ncolor=none;\n";
  /**
   * A counter for the number of source files
   */
  private int srcNb=0;
  /**
   * The graphviz source code string that will represent the server interfaces
   */
  private String srvItfs="{rank=source; color=none; ";
  /**
   * A counter for the number of server interfaces
   */
  private int srvItfsNb=0;
  /**
   * The graphviz source code string that will represent the client interfaces
   */
  private String cltItfs="{rank=sink; color=none; ";
  /**
   * A counter for the number of client interfaces
   */
  private int cltItfsNb=0;
  /**
   * Either the same as srvItfsNb or cltItfsNb
   * Used to adapt the size of composite interface boxes
   */
  private int maxItf=0;
  /**
   * A graphviz color identifier
   * Used for the edges (helps visual identification)
   */
  private int color=1;
  private Map<Object, Object> context;

  @Inject
  protected ImplementationLocator implementationLocatorItf;

  @Inject
  Loader adlLoaderItf;

  @Inject
  protected OutputFileLocator outputFileLocatorItf;

  private Definition definition;

  private boolean isType;

  /**
   * Initialize the DotWriter with the associated instance info
   * @param dir the build directory for the output file
   * @param name the full instance name (path in the instance diagram)
   * @param component The "type" of the component
   * @param isType Is a pure type or composite/primitive ?
   * @param cont the context
   */
  public void init(final String dir, final Definition definition, final Component component, final boolean isType, final Map<Object, Object> context) {
    this.context = context;
    this.definition = definition;
    try {
      this.isType = isType;
      compName = definition.getName();
      final int i = definition.getName().lastIndexOf('.');
      if (i == -1 ) {
        localName = definition.getName();
      } else {
        localName = definition.getName().substring(i + 1);
      }
      buildDir = dir;
      fileName = buildDir + File.separator + compName + ".gv";
      currentPrinter = new PrintWriter( new FileWriter( fileName ) );
      String adlSource = null;
      if (component!=null)
        try {
          //get adlSource in the form /absolute/path/comp.adl:[line,column]-[line,column]
          adlSource = ASTHelper.getResolvedDefinition(component.getDefinitionReference(), adlLoaderItf, context).astGetSource();
          //removing line information. (using lastIndexOf instead of split[0] as ":" is a valid path character)
          adlSource = adlSource.substring(0,adlSource.lastIndexOf(":"));
        } catch (final ADLException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      if (isType)
        writeTypeHeader(adlSource);
      else
        writeHeader(adlSource);
    } catch ( final IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Write the header of the graphviz source code
   * @param adlSource The ADL file describing the component
   */
  private void writeHeader(final String adlSource) {
    currentPrinter.println("digraph " + localName + " {");
    currentPrinter.println("rankdir=LR;");
    currentPrinter.println("ranksep=3;");
    currentPrinter.println("subgraph cluster_membrane {" );

    // we don't embed .adl files in the HTML pages
    //if (adlSource != null) currentPrinter.println("URL=\"" + adlSource + "\"");

    currentPrinter.println("penwidth=15;");
    currentPrinter.println("color=dodgerblue;"); // lighter blue
    currentPrinter.println("style=rounded;");
    //currentPrinter.println("height=20;"); // max number of itf /50*18


  }

  private void writeTypeHeader(final String adlSource) {
    currentPrinter.println("digraph " + localName  + " {");
    currentPrinter.println("rankdir=LR;");
    currentPrinter.println("ranksep=3;");
    currentPrinter.println("subgraph cluster_membrane {");
    currentPrinter.println("penwidth=5;");

    // we don't embed .adl files in the HTML pages
    //if (adlSource != null) currentPrinter.println("URL=\"" + adlSource + "\"");

    currentPrinter.println("color=dodgerblue;"); // lighter blue
    currentPrinter.println("style=\"rounded, dashed\";");
    //currentPrinter.println("height=20;"); // max number of itf /50*18


  }

  /**
   * @param component
   */
  public void addSubComponent(final Component component) {
    try {
      int clientItf = 0;
      int serverItf = 0;

      boolean isFormalTypeParameterReference = false;
      Definition definition = null;
      DefinitionReference defRef = null;

      // Templates support, inspired from TemplateInstantiatorImpl logic
      if ((component instanceof FormalTypeParameterReference) && (((FormalTypeParameterReference) component).getTypeParameterReference() !=null))
        isFormalTypeParameterReference = true;

      if (!isFormalTypeParameterReference) {
        // Standard sub-component
        defRef = component.getDefinitionReference();
        definition = ASTHelper.getResolvedDefinition(defRef, adlLoaderItf, context);
      } else {
        // sub-component is "templated"
        definition = ASTHelper.getResolvedComponentDefinition(component, adlLoaderItf, context);
      }

      // the mindoc @figure tag uses the package name for folders and subfolder "doc-files"
      // calculate strings
      final String currentFileName = compName;
      final String[] splitName = NameHelper.splitName(currentFileName);
      final StringBuilder backToOutputDir = new StringBuilder();

      for(int i = 0; i < (splitName.length-1) ; i++) {
        backToOutputDir.append("../");
      }

      // this bonus "../" counts because our SVG will be located in the "doc-files" subfolder
      backToOutputDir.append("../");

      final String packageDirName = PathHelper.fullyQualifiedNameToDirName(definition.getName());
      // here the return dirName will start with "/" : careful !
      final String targetHtmlFileDirName = packageDirName.substring(1) + "/";

      // compute definition short name (removing package)
      String shortDefName = null;
      final int i = definition.getName().lastIndexOf('.');
      if (i == -1)
        shortDefName = definition.getName();
      else
        shortDefName = definition.getName().substring(i + 1);

      // mindoc naming convention includes ".ADL"
      // please note we use target="main-frame" for SVG to replace the current frame (otherwise only the embed containing SVG is replaced)
      if (!isFormalTypeParameterReference) {
        currentPrinter.print(component.getName() + "Comp [URL=\"" + backToOutputDir.toString() + targetHtmlFileDirName + shortDefName + ".ADL" + ".html\",target=\"main-frame\",shape=Mrecord,style=filled,fillcolor=gainsboro,tooltip=\"Type: " + shortDefName + "\"" + ",label=\"" +  component.getName() + " | {{ " );
      } else {
        currentPrinter.print(component.getName() + "Comp [URL=\"" + backToOutputDir.toString() + targetHtmlFileDirName + shortDefName + ".ADL" + ".html\",target=\"main-frame\",shape=Mrecord,style=\"filled, dashed\",fillcolor=snow,tooltip=\"Type: " + shortDefName + "\"" + " "  + ",label=\"" + component.getName() + " | {{ " );
      }

      if (definition instanceof InterfaceContainer) {

        final TreeSet<MindInterface> interfaces = new TreeSet<MindInterface>(new MindInterfaceComparator());
        for (final Interface itf : ((InterfaceContainer) definition).getInterfaces())
          interfaces.add((MindInterface) itf);
        //final Interface[] interfaces = ((InterfaceContainer) definition).getInterfaces();
        //			for (int i = 0; i < interfaces.length; i++) {
        //				final MindInterface itf = (MindInterface) interfaces[i];
        for (final MindInterface itf : interfaces) {
          if (itf.getRole().equals(TypeInterface.SERVER_ROLE)) {
            if ( serverItf !=0 ) currentPrinter.print(" | ");
            currentPrinter.print("<" + itf.getName() + "> " + itf.getName());
            serverItf++;
            //itf.getSignature()); //TODO might put this info somwhere latter
          }
        }
        currentPrinter.print(" } | | { ");
        //			for (int i = 0; i < interfaces.length; i++) {
        //				final MindInterface itf = (MindInterface) interfaces[i];
        for (final MindInterface itf : interfaces) {
          if (itf.getRole().equals(TypeInterface.CLIENT_ROLE)) {
            if ( clientItf !=0 ) currentPrinter.print(" | ");
            currentPrinter.print("<" + itf.getName() + "> " + itf.getName());
            clientItf++;
            //itf.getSignature());
          }
        }
        currentPrinter.print(" }} | \" ];");
        currentPrinter.println("");
        if (clientItf > maxItf) maxItf = clientItf;
        if (serverItf > maxItf) maxItf = serverItf;
      }
    } catch (final ADLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void addBinding(final Binding binding) {
    color++;
    if (color >= 11) color=1;
    final String fc = binding.getFromComponent();
    final String fi = binding.getFromInterface();
    final String tc = binding.getToComponent();
    final String ti = binding.getToInterface();
    String from = null;
    String to = null;
    if (fc.equals("this"))
      from = "Srv" + fi;
    else
      from = fc + "Comp:" + fi;

    if (tc.equals("this"))
      to = "Clt" + ti;
    else
      to = tc + "Comp:" + ti;
    currentPrinter.println( from + "->" + to + "[colorscheme=\"paired12\" color=" + color + "];");
  }

  /**
   * Add a source file in  the graphviz source code.
   * Modified from the original in the dumpdot-annotation plugin.
   *
   * URL computing inspired from:
   * - HTMLDocumentGenerator#getLinkMap
   * - HTMLDocumentGenerator#copySourceToHTML
   *
   * @param source : the source File
   */
  public void addSource(final Source source) {
    final String srcPath=source.getPath();
    if (srcPath != null) {

      // Locate output file
      final File outputFile = outputFileLocatorItf.getCSourceOutputFile(
          getOutputFileName(definition), context);

      // Compute the transformed output name that uses Google Prettify
      final String destFileName = "impl" + source.hashCode() + ".html";
      final File destinationFile = new File(outputFile.getParentFile(), destFileName);

      String s = "";

      try {
        final URL url = destinationFile.toURI().toURL();
        s = ", URL=\"" + url.toString() + "\",target=\"main-frame\"";
      } catch (final MalformedURLException e) {
        // Do nothing, s remains "" and link just will not be generated, only label
      }

      srcs=srcs + srcNb + "[shape=note,label=\"" + source.getPath() + "\"" + s + "];\n";
      srcNb++;
    }
  }

  /**
   * Add a server interface to the graphviz source code.
   * @param itfName : the name of the interface instance (as on the "provides" line in ADL)
   * @param itfURI : the source file path for the .itf file.
   */
  public void addServer(final String itfName, final String itfSignature) {
    // Current figure uses the package name for folders and subfolder "doc-files"
    // calculate strings
    final String currentFileName = compName;
    final String[] splitName = NameHelper.splitName(currentFileName);
    final StringBuilder backToOutputDir = new StringBuilder();

    for(int i = 0; i < (splitName.length-1) ; i++) {
      backToOutputDir.append("../");
    }

    // this bonus "../" counts because our SVG will be located in the "doc-files" subfolder
    backToOutputDir.append("../");

    final String packageDirName = PathHelper.fullyQualifiedNameToDirName(itfSignature);
    // here the return dirName will start with "/" : careful !
    final String targetHtmlFileDirName = packageDirName.substring(1) + "/";

    // compute definition short name (removing package)
    String shortItfDefName = null;
    final int i = itfSignature.lastIndexOf('.');
    if (i == -1)
      shortItfDefName = itfSignature;
    else
      shortItfDefName = itfSignature.substring(i + 1);

    srvItfs=srvItfs + "Srv" + itfName + " [shape=Mrecord,style=filled,fillcolor=red,label=\"" + itfName + "\", URL=\"" + backToOutputDir.toString() + targetHtmlFileDirName + shortItfDefName + ".ITF" + ".html\",target=\"main-frame\"" + ", height=1 ];";
    srvItfsNb++;
  }

  /**
   * Add a client interface to the graphviz source code.
   * @param itfName : the name of the interface instance (as on the "requires" line in ADL)
   * @param itfURI : the source file path for the .itf file.
   */
  public void addClient(final String itfName, final String itfSignature) {
    // Current figure uses the package name for folders and subfolder "doc-files"
    // calculate strings
    final String currentFileName = compName;
    final String[] splitName = NameHelper.splitName(currentFileName);
    final StringBuilder backToOutputDir = new StringBuilder();

    for(int i = 0; i < (splitName.length-1) ; i++) {
      backToOutputDir.append("../");
    }

    // this bonus "../" counts because our SVG will be located in the "doc-files" subfolder
    backToOutputDir.append("../");

    final String packageDirName = PathHelper.fullyQualifiedNameToDirName(itfSignature);
    // here the return dirName will start with "/" : careful !
    final String targetHtmlFileDirName = packageDirName.substring(1) + "/";

    // compute definition short name (removing package)
    String shortItfDefName = null;
    final int i = itfSignature.lastIndexOf('.');
    if (i == -1)
      shortItfDefName = itfSignature;
    else
      shortItfDefName = itfSignature.substring(i + 1);

    cltItfs=cltItfs + "Clt" + itfName + " [shape=Mrecord,style=filled,fillcolor=green,label=\"" + itfName + "\", URL=\"" + backToOutputDir.toString() + targetHtmlFileDirName + shortItfDefName + ".ITF" + ".html\",target=\"main-frame\"" + ", height=1 ];";
    cltItfsNb++;
  }

  /**
   * Dirty hack (?) to reserve some space in the middle of type files.
   * Otherwise if a type provides a single interface it fills the whole drawing.
   * And in the case of provided and required and provided interfaces they would
   * be stuck together.
   */
  public void addTypeEmptySpace() {
    currentPrinter.println("{ 0 [label=\"" + "        " + "\", style=invisible" + "]; }\n");
  }

  public void close() {
    writeFooter();

  }

  public void deleteFile() {
    final File file = new File(fileName);
    file.delete();
  }

  /**
   * Write the footer for the graphviz source file.
   * (Closes the opened structures)
   */
  private void writeFooter() {
    if (cltItfsNb > maxItf) maxItf=cltItfsNb;
    if (srvItfsNb > maxItf) maxItf=srvItfsNb;
    if (srcNb > maxItf) maxItf=srcNb;
    srvItfs=srvItfs + "}";
    cltItfs=cltItfs + "}";
    srcs=srcs + "}\n";
    if (srvItfsNb > 0) currentPrinter.println(srvItfs);
    if (cltItfsNb > 0) currentPrinter.println(cltItfs);
    if (srcNb > 0) currentPrinter.println(srcs);

    // can't happen at the same time as sources
    if (this.isType)
      addTypeEmptySpace();

    currentPrinter.println("}");
    currentPrinter.println("}");
    currentPrinter.close();
  }

  protected String getOutputFileName(final Definition definition) {
    return fullyQualifiedNameToPath(definition.getName(), HTMLDocumentationHelper.ADL_DOC_EXT);
  }
}
