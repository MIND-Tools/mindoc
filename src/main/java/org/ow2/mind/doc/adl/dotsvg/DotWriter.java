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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
import org.ow2.mind.adl.ast.Binding;
import org.ow2.mind.adl.generic.ast.FormalTypeParameterReference;
import org.ow2.mind.adl.implementation.BasicImplementationLocator;
import org.objectweb.fractal.adl.types.TypeInterface;


public class DotWriter {
  //public static final String            DUMP_DOT = "DumpDot";
  private PrintWriter currentPrinter;
  private String compName;
  private String localName;
  private String buildDir;
  private String fileName;
  private String srcs="subgraph cluster_sources {\ncolor=none;\n";
  private int srcNb=0;
  private String srvItfs="{rank=source; Servers [shape=Mrecord,style=filled,fillcolor=lightskyblue,label=\" Servers | {{ ";
  private int srvItfsNb=0;
  private String cltItfs="{rank=sink Clients [shape=Mrecord,style=filled,fillcolor=lightskyblue,label=\" Clients | {{ ";;
  private int cltItfsNb=0;
  private int maxItf=0; // Used to adapt the size of composite interface boxes
  private int color=1;
  private boolean isType = false;

  public BasicImplementationLocator implementationLocatorItf = new BasicImplementationLocator();

  public DotWriter(final String dir, final String name) {
    try {
      compName = name;
      final int i = name.lastIndexOf('.');
      if (i == -1 ) {
        localName = name;
      } else {
        localName = name.substring(i + 1);
      }
      buildDir = dir;
      fileName = buildDir + File.separator + compName + ".dot";
      currentPrinter = new PrintWriter( new FileWriter( fileName ) );
      writeHeader();
    } catch ( final IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Here we want to change the skin for the type mode
   * @param dir
   * @param name
   * @param isType
   */
  public DotWriter(final String dir, final String name, final boolean isType) {
    try {
      this.isType = isType;
      compName = name;
      final int i = name.lastIndexOf('.');
      if (i == -1 ) {
        localName = name;
      } else {
        localName = name.substring(i + 1);
      }
      buildDir = dir;
      fileName = buildDir + File.separator + compName + ".dot";
      currentPrinter = new PrintWriter( new FileWriter( fileName ) );
      if (isType)
        writeTypeHeader();
      else
        writeHeader();
    } catch ( final IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void writeHeader() {
    currentPrinter.println("digraph " + localName  + " {");
    currentPrinter.println("rankdir=LR;");
    currentPrinter.println("ranksep=3;");
    currentPrinter.println("subgraph cluster_membrane {");
    currentPrinter.println("penwidth=5;");

    currentPrinter.println("color=dodgerblue;");
    currentPrinter.println("style=rounded;");

    //currentPrinter.println("height=20;"); // max number of itf /50*18
  }

  private void writeTypeHeader() {
    currentPrinter.println("digraph " + localName  + " {");
    currentPrinter.println("rankdir=LR;");
    currentPrinter.println("ranksep=3;");
    currentPrinter.println("subgraph cluster_membrane {");
    currentPrinter.println("penwidth=5;");

    currentPrinter.println("color=dodgerblue;");
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
        definition = ASTHelper.getResolvedDefinition(defRef, null, null);
      } else {
        // sub-component is "templated"
        definition = ASTHelper.getResolvedComponentDefinition(component, null, null);
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
      final File targetHtmlFileDir = new File(targetHtmlFileDirName);

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
        currentPrinter.print(component.getName() + "[URL=\"" + backToOutputDir.toString() + targetHtmlFileDirName + shortDefName + ".ADL" + ".html\",target=\"main-frame\",shape=Mrecord,style=filled,fillcolor=gainsboro,tooltip=\"Type: " + shortDefName + "\"" + ",label=\"" +  component.getName() + " | {{ " );
      } else {
        currentPrinter.print(component.getName() + "[URL=\"" + backToOutputDir.toString() + targetHtmlFileDirName + shortDefName + ".ADL" + ".html\",target=\"main-frame\",shape=Mrecord,style=\"filled, dashed\",fillcolor=snow,tooltip=\"Type: " + shortDefName + "\"" + " "  + ",label=\"" + component.getName() + " | {{ " );
      }

      if (definition instanceof InterfaceContainer) {

        final TreeSet<MindInterface> interfaces = new TreeSet<MindInterface>(new MindInterfaceComparator());
        for (final Interface itf : ((InterfaceContainer) definition).getInterfaces())
          interfaces.add((MindInterface) itf);
        //final Interface[] interfaces = ((InterfaceContainer) definition).getInterfaces();
        //			for (int i = 0; i < interfaces.length; i++) {
        //				final MindInterface itf = (MindInterface) interfaces[i];
        for (final MindInterface itf : interfaces) {
          if (itf.getRole()==TypeInterface.SERVER_ROLE) {
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
          if (itf.getRole()==TypeInterface.CLIENT_ROLE) {
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
    if (color >= 12) color=1;
    String fc = binding.getFromComponent();
    final String fi = binding.getFromInterface();
    String tc = binding.getToComponent();
    final String ti = binding.getToInterface();
    if (fc == "this") fc="Servers";
    if (tc == "this") tc="Clients";
    currentPrinter.println( fc + ":" + fi + ":e" + "->" + tc + ":" + ti + ":w" + "[colorscheme=\"paired12\" color=" + color + "];");
  }

  public void addSource(final Source source) {
    if (source.getPath() != null) {
      final String s = "";
      srcs=srcs + srcNb + "[label=\"" + source.getPath() + s + "\", shape=\"note\""+ "];\n";
      srcNb++;
    }
  }

  public void addServer(final String itf) {
    if (srvItfsNb != 0) srvItfs=srvItfs + " | ";
    srvItfs=srvItfs + "<" + itf + "> " + itf;
    srvItfsNb++;
  }

  public void addClient(final String itf) {
    if (cltItfsNb != 0) cltItfs=cltItfs + " | ";
    cltItfs=cltItfs + "<" + itf + "> " + itf;
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

  private void writeFooter() {
    if (cltItfsNb > maxItf) maxItf=cltItfsNb;
    if (srvItfsNb > maxItf) maxItf=srvItfsNb;
    if (srcNb > maxItf) maxItf=srcNb;
    srvItfs=srvItfs + "}} | \",height=" + ((maxItf+3)/2) + " ];}";
    cltItfs=cltItfs + "}} | \",height=" + ((maxItf+3)/2) + " ];}";
    srcs=srcs + "}\n";
    if (srvItfsNb > 0) currentPrinter.println(srvItfs);
    if (cltItfsNb > 0) currentPrinter.println(cltItfs);
    if (srcNb > 0) currentPrinter.println(srcs);

    // can't happen at the same time as sources
    if (this.isType)
      addTypeEmptySpace();

    for (int i=0; i<srcNb; i++) {
      if ((srvItfsNb*srcNb) > 0) currentPrinter.println("Servers->"+i+"[color=none]");
      if ((srcNb*cltItfsNb) > 0)currentPrinter.println(i+"->Clients[color=none]");
    }
    currentPrinter.println("}");
    currentPrinter.println("}");
    currentPrinter.close();
  }
}
