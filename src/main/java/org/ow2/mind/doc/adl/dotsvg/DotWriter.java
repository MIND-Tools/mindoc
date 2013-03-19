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
  private String srvItfs="{rank=source; Servers [shape=Mrecord,style=filled,fillcolor=blue,label=\" Servers | {{ ";
  private int srvItfsNb=0;
  private String cltItfs="{rank=sink Clients [shape=Mrecord,style=filled,fillcolor=blue,label=\" Clients | {{ ";;
  private int cltItfsNb=0;
  private int maxItf=0; // Used to adapt the size of composite interface boxes
  private int color=1;

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

  private void writeHeader() {
    currentPrinter.println("digraph " + localName  + " {");
    currentPrinter.println("rankdir=LR;");
    currentPrinter.println("ranksep=3;");
    currentPrinter.println("subgraph cluster_membrane {");
    currentPrinter.println("penwidth=15;");
    currentPrinter.println("color=blue;");
    currentPrinter.println("style=rounded;");
    //currentPrinter.println("height=20;"); // max number of itf /50*18
  }

  /**
   * @param component
   */
  public void addSubComponent(final Component component) {
    try {
      int clientItf = 0;
      int serverItf = 0;

      final DefinitionReference defRef = component.getDefinitionReference();
      final Definition definition = ASTHelper.getResolvedDefinition(defRef, null, null);

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
      targetHtmlFileDir.mkdirs();

      // compute definition short name (removing package)
      String shortDefName = null;
      final int i = definition.getName().lastIndexOf('.');
      if (i == -1)
        shortDefName = definition.getName();
      else
        shortDefName = definition.getName().substring(i + 1);

      // mindoc naming convention includes ".ADL"
      // please note we use target="main-frame" for SVG to replace the current frame (otherwise only the embed containing SVG is replaced)
      currentPrinter.print(component.getName() + "[URL=\"" + backToOutputDir.toString() + targetHtmlFileDirName + shortDefName + ".ADL" + ".html\",target=\"main-frame\",shape=Mrecord,style=filled,fillcolor=lightgrey,label=\"" + component.getName() + " | {{ " );

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
    currentPrinter.println( fc + ":" + fi + "->" + tc + ":" + ti + "[tailport=e headport=w colorscheme=\"paired12\" color=" + color + "];");
  }

  public void addSource(final Source source) {
    if (source.getPath() != null) {
      final String s = "";
      srcs=srcs + srcNb + "[label=\"" + source.getPath() + s + "\""+ "];\n";
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

  public void close() {
    writeFooter();

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
    for (int i=0; i<srcNb; i++) {
      if ((srvItfsNb*srcNb) > 0) currentPrinter.println("Servers->"+i+"[color=none]");
      if ((srcNb*cltItfsNb) > 0)currentPrinter.println(i+"->Clients[color=none]");
    }
    currentPrinter.println("}");
    currentPrinter.println("}");
    currentPrinter.close();
  }
}
