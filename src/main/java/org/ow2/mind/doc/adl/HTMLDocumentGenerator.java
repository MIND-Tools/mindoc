/**
 * Copyright (C) 2009 STMicroelectronics
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
 * Authors: ali-erdem.ozcan@st.com
 * Contributors:
 */

package org.ow2.mind.doc.adl;

import static org.objectweb.fractal.adl.types.TypeInterfaceUtil.isClient;
import static org.objectweb.fractal.adl.types.TypeInterfaceUtil.isServer;
import static org.ow2.mind.PathHelper.fullyQualifiedNameToPath;
import static org.ow2.mind.adl.ast.ASTHelper.isComposite;
import static org.ow2.mind.adl.ast.ASTHelper.isPrimitive;
import static org.ow2.mind.adl.ast.ASTHelper.isType;
import static org.ow2.mind.doc.HTMLDocumentationHelper.getAttributeAnchor;
import static org.ow2.mind.doc.HTMLDocumentationHelper.getBindingAnchor;
import static org.ow2.mind.doc.HTMLDocumentationHelper.getImplementationAnchor;
import static org.ow2.mind.doc.HTMLDocumentationHelper.getInterfaceAnchor;
import static org.ow2.mind.doc.HTMLDocumentationHelper.getPathToRoot;
import static org.ow2.mind.doc.HTMLDocumentationHelper.getSubComponentAnchor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.antlr.stringtemplate.language.DefaultTemplateLexer;
import org.apache.commons.io.IOUtils;
import org.objectweb.fractal.adl.ADLException;
import org.objectweb.fractal.adl.CompilerError;
import org.objectweb.fractal.adl.Definition;
import org.objectweb.fractal.adl.interfaces.Interface;
import org.objectweb.fractal.adl.interfaces.InterfaceContainer;
import org.objectweb.fractal.adl.types.TypeInterface;
import org.ow2.mind.SourceFileWriter;
import org.ow2.mind.adl.AbstractSourceGenerator;
import org.ow2.mind.adl.DefinitionSourceGenerator;
import org.ow2.mind.adl.ast.ASTHelper;
import org.ow2.mind.adl.ast.Attribute;
import org.ow2.mind.adl.ast.AttributeContainer;
import org.ow2.mind.adl.ast.Binding;
import org.ow2.mind.adl.ast.BindingContainer;
import org.ow2.mind.adl.ast.Component;
import org.ow2.mind.adl.ast.ComponentContainer;
import org.ow2.mind.adl.ast.Data;
import org.ow2.mind.adl.ast.DefinitionReference;
import org.ow2.mind.adl.ast.ImplementationContainer;
import org.ow2.mind.adl.ast.Source;
import org.ow2.mind.adl.generic.ast.FormalTypeParameter;
import org.ow2.mind.adl.generic.ast.FormalTypeParameterContainer;
import org.ow2.mind.adl.generic.ast.TypeArgument;
import org.ow2.mind.adl.generic.ast.TypeArgumentContainer;
import org.ow2.mind.doc.HTMLDocumentationHelper;
import org.ow2.mind.doc.HTMLRenderer;
import org.ow2.mind.doc.Launcher;
import org.ow2.mind.doc.HTMLDocumentationHelper.SourceKind;
import org.ow2.mind.doc.adl.graph.Gv2SVGProcessor;
import org.ow2.mind.doc.comments.CommentProcessor;
import org.ow2.mind.io.IOErrors;

import com.google.inject.Inject;
import com.google.inject.Injector;

public class HTMLDocumentGenerator extends AbstractSourceGenerator
implements
DefinitionSourceGenerator {

  public String              pathToRoot;
  private File outputFile;

  @Inject
  protected Injector injector;

  public HTMLDocumentGenerator() {
    super("st.definitions.documentation.Component");
  }

  public void visit(final Definition definition,
      final Map<Object, Object> context) throws ADLException {

    outputFile = outputFileLocatorItf.getCSourceOutputFile(
        getOutputFileName(definition), context);
    pathToRoot = getPathToRoot(definition.getName());

    addDecorations(definition, (File)context.get("sourceDirectory"));
    final StringTemplate st = getInstanceOf("ComponentDocumentation");
    st.setAttribute("definition", definition);
    final Map<String, Map<String, String>> anchorMap = getAnchorMap(definition);
    st.setAttribute("anchors", anchorMap);
    st.setAttribute("sectionAnchors", getSectionAnchors(definition, anchorMap));
    st.setAttribute("links", getLinkMap(definition, (File)context.get("sourceDirectory"))); //
    st.setAttribute("pathToRoot", pathToRoot);

    CommentProcessor.process(definition, context);

    // Create SVG component graph
    final Gv2SVGProcessor graphProcessor = injector.getInstance(Gv2SVGProcessor.class);
    graphProcessor.process(definition, context);

    try {
      SourceFileWriter.writeToFile(outputFile, st.toString());
    } catch (final IOException e) {
      throw new CompilerError(IOErrors.WRITE_ERROR, e, outputFile
          .getAbsolutePath());
    }
  }

  private Map<String, String> getSectionAnchors(final Definition definition,
      final Map<String, Map<String, String>> anchorMap) {
    final Map<String, String> map = new HashMap<String, String>();

    if(anchorMap.get("attr").size() != 0)
      map.put("attributes", HTMLDocumentationHelper.ATTRIBUTE_SECTION_ANCHOR);

    if (definition instanceof ImplementationContainer) {
      final ImplementationContainer implContainer = (ImplementationContainer)definition;
      final Data data = implContainer.getData();
      if(data != null && (data.getCCode() != null || data.getPath() != null))
        map.put("data", HTMLDocumentationHelper.DATA_SECTION_ANCHOR);
    }

    if(definition.astGetDecoration("clientInterfaces") != null)
      map.put("clientInterfaces", HTMLDocumentationHelper.CLIENT_INTERFACE_SECTION_ANCHOR);

    if(definition.astGetDecoration("serverInterfaces") != null)
      map.put("serverInterfaces", HTMLDocumentationHelper.SERVER_INTERFACE_SECTION_ANCHOR);

    if(anchorMap.get("cmp").size() != 0)
      map.put("components", HTMLDocumentationHelper.COMPONENT_SECTION_ANCHOR);

    if(anchorMap.get("bind").size() != 0)
      map.put("bindings", HTMLDocumentationHelper.BINDING_SECTION_ANCHOR);

    if(anchorMap.get("impl").size() != 0)
      map.put("implementations", HTMLDocumentationHelper.IMPLEMENTATION_SECTION_ANCHOR);

    return map;
  }

  private Map<String, Map<String, String>> getAnchorMap(
      final Definition definition) {
    final Map<String, Map<String, String>> superMap = new HashMap<String, Map<String, String>>();

    Map<String, String> map = new HashMap<String, String>();
    superMap.put("itf", map);
    if (definition instanceof InterfaceContainer) {
      final InterfaceContainer itfContainer = (InterfaceContainer) definition;
      for (final Interface itf : itfContainer.getInterfaces()) {
        map.put(itf.getName(), getInterfaceAnchor(itf.getName()));
      }
    }

    map = new HashMap<String, String>();
    superMap.put("cmp", map);
    if (definition instanceof ComponentContainer) {
      final ComponentContainer cmpContainer = (ComponentContainer) definition;
      for (final Component cmp : cmpContainer.getComponents()) {
        map.put(cmp.getName(), getSubComponentAnchor(cmp.getName()));
      }
    }

    map = new HashMap<String, String>();
    superMap.put("bind", map);
    if (definition instanceof BindingContainer) {
      final BindingContainer bndContainer = (BindingContainer) definition;
      for (final Binding binding : bndContainer.getBindings()) {
        map.put(binding.toString(), getBindingAnchor(
            binding.getFromComponent(), binding.getFromInterface(),
            binding.getToComponent(), binding.getToInterface()));

      }
    }

    map = new HashMap<String, String>();
    superMap.put("attr", map);
    if (definition instanceof AttributeContainer) {
      final AttributeContainer attrContainer = (AttributeContainer) definition;
      for (final Attribute attr : attrContainer.getAttributes()) {
        map.put(attr.getName(), getAttributeAnchor(attr.getName()));
      }
    }

    map = new HashMap<String, String>();
    superMap.put("impl", map);
    if (definition instanceof ImplementationContainer) {
      final ImplementationContainer implContainer = (ImplementationContainer) definition;
      for (final Source src : implContainer.getSources()) {
        if ((src.toString() != null) && (src.getPath() != null))
          map.put(src.toString(), getImplementationAnchor(src.getPath()));
      }
    }
    return superMap;
  }

  private Map<String, Map<String, String>> getLinkMap(
      final Definition definition, final File sourceDirectory) throws ADLException {
    final Map<String, Map<String, String>> superMap = new HashMap<String, Map<String, String>>();

    Map<String, String> map = new HashMap<String, String>();
    superMap.put("itf", map);
    if (definition instanceof InterfaceContainer) {
      final InterfaceContainer itfContainer = (InterfaceContainer) definition;
      for (final Interface itf : itfContainer.getInterfaces()) {
        if (itf instanceof TypeInterface) {
          final TypeInterface typeItf = (TypeInterface) itf;
          final String s = typeItf.getSignature();
          map.put(s, HTMLDocumentationHelper.getRelativePathToITF(definition.getName(), s, SourceKind.COMPONENT));

        }
      }
    }

    map = new HashMap<String, String>();
    superMap.put("cmp", map);
    if (definition instanceof ComponentContainer) {
      final ComponentContainer cmpContainer = (ComponentContainer) definition;
      for (final Component cmp : cmpContainer.getComponents()) {
        String name = null;

        final DefinitionReference defRef = cmp.getDefinitionReference();
        if(defRef != null) {
          name = defRef.getName();
          if(defRef instanceof TypeArgumentContainer)
            addTypeArgumentLinks(definition, map, (TypeArgumentContainer) defRef);
        } else {
          final Definition def =  ASTHelper.getResolvedComponentDefinition(cmp, null, null);
          if(def != null) {
            name = def.getName();
          } else {
            continue;
          }
        }
        map.put(name, HTMLDocumentationHelper.getRelativePathToADL(definition.getName(), name, SourceKind.COMPONENT));
      }
    }

    if(definition.astGetDecoration("extends") != null) {
      final ExtendsDecoration extendsList = (ExtendsDecoration) definition.astGetDecoration("extends");
      for (final DefinitionReference defRef : extendsList.getExtends()) {
        addDefinitionReferenceLink(definition, map, defRef);
      }
    }

    map = new HashMap<String, String>();
    superMap.put("impl", map);
    if (definition instanceof ImplementationContainer) {
      final ImplementationContainer implContainer = (ImplementationContainer) definition;
      for (final Source src : implContainer.getSources()) {

        if (src.getPath() != null) {

          final String sourceFileName = src.getPath().toString();
          final String destFileName = "impl" + src.hashCode() + ".html";
          String dest = null;

          dest = copySourceToHTML(sourceDirectory, sourceFileName, destFileName);
          if(dest != null)
            map.put(src.toString(), dest);
        }
      }
    }
    return superMap;
  }

  private int getPathDeph(final String path) {
    int depth=0;
    int i;
    char temp;
    for (i=0; i < path.length(); i++) {
      temp = path.charAt(i);
      if (temp == '/') depth++;
    }
    return depth;
  }

  private String copySourceToHTML(final File sourceDirectory,
      String sourceFileName, final String destFileName) {
    String dest = null;
    if(sourceFileName.startsWith("/")) {
      sourceFileName = sourceFileName.substring(1);
    }

    final File sourceFile = new File(sourceDirectory, sourceFileName);
    if(!sourceFile.canRead()) {
      Launcher.logger.warning("Cannot read implementation file '" + sourceFileName.toString() + "'.");
    } else {
      try {
        final File destinationFile = new File(outputFile.getParentFile(), destFileName);
        final FileWriter out = new FileWriter(destinationFile);
        final PrintWriter writer = new PrintWriter(out);
        final InputStream in = new FileInputStream(sourceFile);

        final int depth = getPathDeph(sourceFileName);
        String relativeBase = "";
        int i;
        for (i=0; i < depth; i++) { relativeBase += "../"; }

        writer.print("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
            "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n" +
            "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
            "<head>\n" +
            "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n" +
            "<!-- <script src=\"https://google-code-prettify.googlecode.com/svn/loader/run_prettify.js\"></script> -->\n" +
            "<link href=\"" + relativeBase + "google-code-prettify/prettify.css\" type=\"text/css\" rel=\"stylesheet\" />\n" +
            "<script type=\"text/javascript\" src=\"" + relativeBase + "google-code-prettify/prettify.js\"></script>");


        writer.printf("<title>%s</title>\n", sourceFileName);
        writer.println("</head>");
        writer.println("<body onload=\"prettyPrint()\">");
        writer.println("<xmp class=\"prettyprint linenums\" id=\"htmlXmp\">");
        IOUtils.copy(in, out);
        writer.println("</xmp>");
        writer.println("</body>");
        writer.println("</html>");
        writer.close();
        in.close();
        dest =  destinationFile.getName();
      } catch (final IOException e) {
        Launcher.logger.warning("Cannot copy implementation file '" + sourceFileName.toString() + "'. Reason: " + e.getLocalizedMessage());
      }
    }
    return dest;
  }

  private void addDefinitionReferenceLink(final Definition definition,
      final Map<String, String> map, final DefinitionReference definitionReference) {
    map.put(definitionReference.getName(), HTMLDocumentationHelper.getRelativePathToADL(definition.getName(), definitionReference.getName(), SourceKind.COMPONENT));
  }

  private void addTypeArgumentLinks(final Definition definition, final Map<String, String> map, final TypeArgumentContainer typeArgumentContainer) {
    for (final TypeArgument typeArg : typeArgumentContainer.getTypeArguments()) {
      final DefinitionReference argDefRef = typeArg.getDefinitionReference();

      // TODO: Handle this case more finely
      if (argDefRef == null)
        continue;

      addDefinitionReferenceLink(definition, map, argDefRef);
      if(argDefRef instanceof TypeArgumentContainer) {
        addTypeArgumentLinks(definition, map, (TypeArgumentContainer)argDefRef);
      }
    }
  }

  private void addDecorations(final Definition definition, final File sourceDirectory) throws ADLException {
    setDefinitionKind(definition);
    HTMLDocumentationHelper.addAnnotationDecoration(definition);

    final List<Interface> clientInterfaces = new LinkedList<Interface>();
    final List<Interface> serverInterfaces = new LinkedList<Interface>();
    if (definition instanceof InterfaceContainer) {
      final InterfaceContainer itfContainer = (InterfaceContainer) definition;
      for (final Interface itf : itfContainer.getInterfaces()) {
        if (isClient(itf)) {
          clientInterfaces.add(itf);
        } else if (isServer(itf)) {
          serverInterfaces.add(itf);
        }
        HTMLDocumentationHelper.addAnnotationDecoration(itf);
      }

    }
    if (!clientInterfaces.isEmpty()) {
      definition.astSetDecoration("clientInterfaces", clientInterfaces);
    }
    if (!serverInterfaces.isEmpty()) {
      definition.astSetDecoration("serverInterfaces", serverInterfaces);
    }

    if (definition instanceof ImplementationContainer) {
      final ImplementationContainer implContainer = (ImplementationContainer)definition;
      final Data data = implContainer.getData();
      if(data != null) {
        HTMLDocumentationHelper.addAnnotationDecoration(data);
        if(data.getPath() != null) {
          final String sourceFileName = data.getPath().toString();
          final String destFileName = "data" + data.hashCode() + ".html";
          String dest = null;

          dest = copySourceToHTML(sourceDirectory, sourceFileName, destFileName);
          if(dest != null)
            data.astSetDecoration("link", dest);
        }
      }
      for (final Source source : implContainer.getSources()) {
        HTMLDocumentationHelper.addAnnotationDecoration(source);
      }

    }

    if (definition instanceof ComponentContainer) {
      final ComponentContainer componentCont = (ComponentContainer)definition;
      for (final Component comp : componentCont.getComponents()) {
        if(comp.getDefinitionReference() != null) {
          final DefinitionReference defRef = comp.getDefinitionReference();
          setDefinitionReferenceKind(defRef);
        } else {
          setDefinitionKind(ASTHelper.getResolvedComponentDefinition(comp, null, null));
        }
        HTMLDocumentationHelper.addAnnotationDecoration(comp);
      }
    }

    if (definition instanceof FormalTypeParameterContainer) {
      for (final FormalTypeParameter typeParam: ((FormalTypeParameterContainer)definition).getFormalTypeParameters()) {
        final DefinitionReference defRef = typeParam.getDefinitionReference();
        if (defRef != null)
          setDefinitionReferenceKind(defRef);
      }
    }

    if(definition.astGetDecoration("extends") != null) {
      final ExtendsDecoration extendsList = (ExtendsDecoration) definition.astGetDecoration("extends");
      for (final DefinitionReference defRef : extendsList.getExtends()) {
        setDefinitionReferenceKind(defRef);
      }
    }

    if(definition instanceof BindingContainer) {
      for (final Binding binding : ((BindingContainer)definition).getBindings()) {
        HTMLDocumentationHelper.addAnnotationDecoration(binding);
      }
    }

    if(definition instanceof AttributeContainer) {
      for(final Attribute attr: ((AttributeContainer) definition).getAttributes()) {
        HTMLDocumentationHelper.addAnnotationDecoration(attr);
        final String valueString = HTMLDocumentationHelper.getValueString(attr.getValue());
        if(valueString != null)
          attr.astSetDecoration("value", valueString);
      }
    }
  }

  private String setDefinitionKind(final Definition definition) {
    final String kind = getKind(definition);
    definition.astSetDecoration("kind", kind);
    return kind;
  }

  private void setDefinitionReferenceKind(final DefinitionReference definitionReference) throws ADLException {
    final Definition def = ASTHelper.getResolvedDefinition(definitionReference, null, null);
    final String kind = setDefinitionKind(def);
    definitionReference.astSetDecoration("kind", kind);
  }

  private String getKind(final Definition definition) {
    if (isType(definition)) {
      return "Type";
    } else if (isPrimitive(definition)) {
      return "Primitive";
    } else if (isComposite(definition)) {
      return "Composite";
    }
    return "";
  }

  protected String getOutputFileName(final Definition definition) {
    return fullyQualifiedNameToPath(definition.getName(), HTMLDocumentationHelper.ADL_DOC_EXT);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected Class getTemplateLexer() {
    return DefaultTemplateLexer.class;
  }

  @Override
  protected void registerCustomRenderer(final StringTemplateGroup templateGroup) {
    templateGroup.registerRenderer(String.class, new HTMLRenderer());
  }
}
