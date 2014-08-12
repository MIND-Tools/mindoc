/**
 * Copyright (C) 2009 STMicroelectronics
 *
 * This file is part of "Mind Compiler".
 * "Mind Compiler" is a free software tool.
 * This file is licensed under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact: mind@ow2.org
 *
 * Authors: Michel Metzger
 * Contributors:
 */

/**
   \file com/st/p2012/mind/idl/IDLTemplateProcessor.java
   \brief The class encapsulating the template component for the IDL documentation generator.
 */

package org.ow2.mind.doc.idl;

import static org.ow2.mind.PathHelper.fullyQualifiedNameToPath;
import static org.ow2.mind.SourceFileWriter.writeToFile;
import static org.ow2.mind.doc.HTMLDocumentationHelper.addAnnotationDecoration;
import static org.ow2.mind.doc.HTMLDocumentationHelper.getMethodAnchor;
import static org.ow2.mind.doc.HTMLDocumentationHelper.getPathToRoot;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.antlr.stringtemplate.language.DefaultTemplateLexer;
import org.objectweb.fractal.adl.ADLException;
import org.objectweb.fractal.adl.CompilerError;
import org.ow2.mind.adl.AbstractSourceGenerator;
import org.ow2.mind.doc.HTMLDocumentationHelper;
import org.ow2.mind.doc.HTMLRenderer;
import org.ow2.mind.doc.comments.CommentProcessor;
import org.ow2.mind.idl.IDLVisitor;
import org.ow2.mind.idl.ast.EnumDefinition;
import org.ow2.mind.idl.ast.IDL;
import org.ow2.mind.idl.ast.InterfaceDefinition;
import org.ow2.mind.idl.ast.Method;
import org.ow2.mind.idl.ast.StructDefinition;
import org.ow2.mind.idl.ast.Type;
import org.ow2.mind.idl.ast.TypeCollectionContainer;
import org.ow2.mind.idl.ast.TypeDefinition;
import org.ow2.mind.idl.ast.UnionDefinition;
import org.ow2.mind.idl.jtb.visitor.TreeFormatter;
import org.ow2.mind.io.IOErrors;

public class IDLTemplateProcessor extends AbstractSourceGenerator
    implements
      IDLVisitor {

  public IDLTemplateProcessor() {
    super("st.definitions.documentation.Interface");
  }

  // ---------------------------------------------------------------------------
  // Implementation of the Visitor interface
  // ---------------------------------------------------------------------------

  public void visit(final IDL idl, final Map<Object, Object> context)
      throws ADLException {

    final StringTemplate st = getInstanceOf("InterfaceDocumentation");

    final File headerFile = outputFileLocatorItf.getCSourceOutputFile(
        getOutputFileName(idl), context);

    CommentProcessor.process(idl, context);

    st.setAttribute("idl", idl);
    st.setAttribute("pathToRoot", getPathToRoot(idl.getName()));
    st.setAttribute("anchors", getAnchorMap(idl));
    st.setAttribute("sectionAnchors", getSectionAnchorMap(idl));

    addDecorations(idl);

    try {
      writeToFile(headerFile, st.toString());
    } catch (final IOException e) {
      throw new CompilerError(IOErrors.WRITE_ERROR, e,
          headerFile.getAbsolutePath());
    }
  }

  private Map<String, String> getSectionAnchorMap(final IDL idl) {
    final Map<String, String> map = new HashMap<String, String>();

    if (idl instanceof InterfaceDefinition) {
      final InterfaceDefinition def = (InterfaceDefinition) idl;
      if (def.getMethods().length != 0) {
        map.put("methods", "METHODS");
      }
    }

    if (idl instanceof TypeCollectionContainer) {
      final TypeCollectionContainer container = (TypeCollectionContainer) idl;

      if (container.getTypes().length != 0) {
        map.put("types", "TYPES");
      }
    }

    return map;
  }

  private Map<String, Object> getAnchorMap(final IDL idl) {
    final Map<String, Object> superMap = new HashMap<String, Object>();

    if (idl instanceof InterfaceDefinition) {
      final InterfaceDefinition def = (InterfaceDefinition) idl;

      final Map<String, String> map = new HashMap<String, String>();
      superMap.put("methods", map);

      for (final Method method : def.getMethods()) {
        map.put(method.getName(), getMethodAnchor(method.getName()));
      }
    }

    if (idl instanceof TypeCollectionContainer) {
      final TypeCollectionContainer container = (TypeCollectionContainer) idl;

      final Map<String, Map<String, String>> typeMap = new HashMap<String, Map<String, String>>();
      superMap.put("types", typeMap);

      final Map<String, String> typedefMap = new HashMap<String, String>();
      typeMap.put("typedef", typedefMap);

      final Map<String, String> structMap = new HashMap<String, String>();
      typeMap.put("struct", structMap);

      final Map<String, String> unionMap = new HashMap<String, String>();
      typeMap.put("union", unionMap);

      final Map<String, String> enumMap = new HashMap<String, String>();
      typeMap.put("enum", enumMap);

      for (final Type type : container.getTypes()) {
        if (type instanceof TypeDefinition) {
          final String name = ((TypeDefinition) type).getName();
          typedefMap.put(name, HTMLDocumentationHelper.getTypedefAnchor(name));
        } else if (type instanceof StructDefinition) {
          final String name = ((StructDefinition) type).getName();
          structMap.put(name, HTMLDocumentationHelper.getStructAnchor(name));
        } else if (type instanceof UnionDefinition) {
          final String name = ((UnionDefinition) type).getName();
          unionMap.put(name, HTMLDocumentationHelper.getUnionAnchor(name));
        } else if (type instanceof EnumDefinition) {
          final String name = ((EnumDefinition) type).getName();
          enumMap.put(name, HTMLDocumentationHelper.getEnumAnchor(name));
        }
      }
    }

    return superMap;
  }

  private void addDecorations(final IDL idl) {
    final List<Type> types = getTypeList(idl);
    idl.astSetDecoration("types", types);
    addAnnotationDecoration(idl);

    if (idl instanceof InterfaceDefinition) {
      final InterfaceDefinition def = (InterfaceDefinition) idl;

      for (final Method method : def.getMethods()) {
        addAnnotationDecoration(method);
      }
    }
  }

  private List<Type> getTypeList(final IDL idl) {
    final List<Type> result = new LinkedList<Type>();

    if (idl instanceof TypeCollectionContainer) {
      final TypeCollectionContainer container = (TypeCollectionContainer) idl;
      for (final Type type : container.getTypes()) {
        if (type instanceof TypeDefinition) {
          result.add(type);
          setPrettyPrintSource(type);
        } else if (type instanceof StructDefinition) {
          final StructDefinition struct = (StructDefinition) type;
          if (!(struct.getName().length() == 0)) {
            result.add(type);
            setPrettyPrintSource(type);
          }
        } else if (type instanceof UnionDefinition) {
          final UnionDefinition struct = (UnionDefinition) type;
          if (!(struct.getName().length() == 0)) {
            result.add(type);
            setPrettyPrintSource(type);
          }
        } else if (type instanceof EnumDefinition) {
          final EnumDefinition struct = (EnumDefinition) type;
          if (!(struct.getName().length() == 0)) {
            result.add(type);
            setPrettyPrintSource(type);
          }
        }
      }
    }
    return result;
  }

  private void setPrettyPrintSource(final Type type) {
    final StringWriter sw = new StringWriter();
    final TreeFormatter formatter = new IDLTreeFormatter();
    final IDLTreeDumper dumper = new IDLTreeDumper(sw);
    final org.ow2.mind.idl.jtb.syntaxtree.TypeDefinition astNode = (org.ow2.mind.idl.jtb.syntaxtree.TypeDefinition) type
        .astGetDecoration("syntax-tree");
    if (astNode != null) {
      formatter.visit(astNode);
      dumper.visit(astNode);
      type.astSetDecoration("source", sw.toString());
    }
  }

  protected String getOutputFileName(final IDL interfaceDefinition) {
    return fullyQualifiedNameToPath(interfaceDefinition.getName(),
        HTMLDocumentationHelper.ITF_DOC_EXT);
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
