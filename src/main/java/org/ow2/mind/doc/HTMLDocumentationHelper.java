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
 * Authors: michel.metzger@st.com
 * Contributors: sseyvoz@assystem.com
 */

package org.ow2.mind.doc;

import static org.ow2.mind.NameHelper.isValid;
import static org.ow2.mind.NameHelper.splitName;
import static org.ow2.mind.PathHelper.fullyQualifiedNameToPath;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.fractal.adl.Node;
import org.ow2.mind.NameHelper;
import org.ow2.mind.PathHelper;
import org.ow2.mind.annotation.Annotation;
import org.ow2.mind.annotation.AnnotationElement;
import org.ow2.mind.annotation.AnnotationHelper;
import org.ow2.mind.value.ast.BooleanLiteral;
import org.ow2.mind.value.ast.NumberLiteral;
import org.ow2.mind.value.ast.Reference;
import org.ow2.mind.value.ast.StringLiteral;
import org.ow2.mind.value.ast.Value;


public final class HTMLDocumentationHelper {

  public static enum SourceKind {
    COMPONENT,
    INTERFACE,
    PACKAGE
  }

  public final static String FILE_EXT = ".html";

  public final static String ADL_DOC_EXT = ".ADL" + FILE_EXT;
  public final static String ITF_DOC_EXT = ".ITF" + FILE_EXT;

  private static final String INTERFACE_ANCHOR_PREFIX = "ITF_";
  private static final String COMPONENT_ANCHOR_PREFIX = "CMP_";
  private static final String ATTRIBUTE_ANCHOR_PREFIX = "ATTR_";
  private static final String BINDING_ANCHOR_PREFIX   = "BIND_";
  private static final String IMPLEMENTATION_ANCHOR_PREFIX = "IMPL_";
  private static final String METHOD_ANCHOR_PREFIX    = "METH_";
  private static final String ENUM_ANCHOR_PREFIX      = "ENUM_";
  private static final String STRUCT_ANCHOR_PREFIX    = "STRUCT_";
  private static final String UNION_ANCHOR_PREFIX     = "UNION_";
  private static final String TYPEDEF_ANCHOR_PREFIX   = "TYPEDEF_";

  public static final String COMPONENT_SECTION_ANCHOR   = "COMPONENTS";
  public static final String CLIENT_INTERFACE_SECTION_ANCHOR   = "CLIENT_ITF";
  public static final String SERVER_INTERFACE_SECTION_ANCHOR   = "SERVER_ITF";
  public static final String ATTRIBUTE_SECTION_ANCHOR   = "ATTRIBUTES";
  public static final String BINDING_SECTION_ANCHOR = "BINDINGS";
  public static final String DATA_SECTION_ANCHOR = "DATA";
  public static final String IMPLEMENTATION_SECTION_ANCHOR = "IMPLEMENTATIONS";
  public static final String METHOD_SECTION_ANCHOR = "METHODS";
  public static final String TYPE_SECTION_ANCHOR = "TYPES";

  private HTMLDocumentationHelper() {}

  public static String getShortName(final String s) {
    final String[] sn = splitName(toValidName(s));
    return sn[sn.length - 1];
  }

  /**
   * Takes a definition name and removes template parameters
   * @param name
   * @return
   */
  public static final String toValidName(final String name) {
    final int i = name.indexOf('<');
    if (i >= 0) {
      final String defName = name.substring(0, i);
      if (!isValid(defName))
        throw new IllegalArgumentException("Name \"" + defName
            + "\" is not a valid name.");
      return defName;
    }

    if (!isValid(name))
      throw new IllegalArgumentException("Name \"" + name
          + "\" is not a valid name.");

    return name;
  }

  public static String getPathToRoot(final String name) {
    return getPackagePathToRoot(NameHelper.getPackageName(name));
  }

  /**
   * Returns the relative path to the root for a given name
   * @param name
   * @return
   */
  public static String getPackagePathToRoot(final String name) {
    if(name == null || name.length() == 0)
      return "./";
    final int depth = splitName(toValidName(name)).length;
    final StringBuilder sb = new StringBuilder();
    if (depth > 0) {
      sb.append("..");
      for (int i = 1; i < depth; i++) {
        sb.append("/..");
      }
    }
    return sb.toString();
  }

  private static String getRelativePathTo(final String sourceName, final String targetName, final SourceKind sourceKind, final String fileSuffix) {
    if(sourceKind == SourceKind.PACKAGE) {
      return getPackagePathToRoot(sourceName) + fullyQualifiedNameToPath(toValidName(targetName), fileSuffix);
    } else {
      return getPathToRoot(sourceName) + fullyQualifiedNameToPath(toValidName(targetName), fileSuffix);
    }
  }

  public static String getRelativePathToADL(final String sourceName, final String targetName, final SourceKind sourceKind) {
    return getRelativePathTo(sourceName, targetName, sourceKind, ADL_DOC_EXT);
  }

  public static String getRelativePathToITF(final String sourceName, final String targetName, final SourceKind sourceKind) {
    return getRelativePathTo(sourceName, targetName, sourceKind, ITF_DOC_EXT);
  }

  public static String getPathToADL(final String adlName) {
    return fullyQualifiedNameToPath(adlName, ADL_DOC_EXT);
  }

  public static String getPathToITF(final String itfName) {
    return fullyQualifiedNameToPath(itfName, ITF_DOC_EXT);
  }

  private static String getRelativeLinkToADLSubElem(final String sourceName,
      final String targetName, final String anchor, final SourceKind sourceKind) {
    String prefix = null;
    if(sourceName.equals(targetName) && sourceKind == SourceKind.COMPONENT) {
      prefix = "";
    } else {
      prefix = getRelativePathToADL(sourceName, targetName, sourceKind);
    }
    return prefix + (anchor.length() == 0 ? "" : "#" + anchor);
  }

  private static String getRelativeLinkToITFSubElem(final String sourceName,
      final String targetName, final String anchor, final SourceKind sourceKind) {
    String prefix = null;
    if(sourceName.equals(targetName) && sourceKind == SourceKind.INTERFACE) {
      prefix = "";
    } else {
      prefix = getRelativePathToITF(sourceName, targetName, sourceKind);
    }
    return prefix + (anchor.length() == 0 ? "" : "#" + anchor);
  }

  public static String getInterfaceAnchor(final String interfaceName) {
    return INTERFACE_ANCHOR_PREFIX + interfaceName;
  }

  public static String getSubComponentAnchor(final String componentName) {
    return COMPONENT_ANCHOR_PREFIX + componentName;
  }

  public static String getAttributeAnchor(final String attributeName) {
    return ATTRIBUTE_ANCHOR_PREFIX + attributeName;
  }

  public static String getBindingAnchor(final String fromComponent, final String fromInterface, final String toComponent, final String toInterface) {
    return String.format(BINDING_ANCHOR_PREFIX +  "%s_%s_to_%s_%s", fromComponent, fromInterface, toComponent, toInterface);
  }

  public static String getImplementationAnchor(final String path) {
    return String.format(IMPLEMENTATION_ANCHOR_PREFIX + path.replace('/', '_'));
  }

  public static String getEnumAnchor(final String enumName) {
    return ENUM_ANCHOR_PREFIX + enumName;
  }

  public static String getStructAnchor(final String structName) {
    return STRUCT_ANCHOR_PREFIX + structName;
  }

  public static String getUnionAnchor(final String unionName) {
    return UNION_ANCHOR_PREFIX + unionName;
  }

  public static String getTypedefAnchor(final String typedefName) {
    return TYPEDEF_ANCHOR_PREFIX + typedefName;
  }

  public static String getMethodAnchor(final String methodName) {
    return METHOD_ANCHOR_PREFIX + methodName;
  }

  public static String getRelativeLinkToInterface(final String sourceName, final String targetName, final String interfaceName, final SourceKind sourceKind) {
    return getRelativeLinkToADLSubElem(sourceName, targetName, getInterfaceAnchor(interfaceName), sourceKind);
  }

  public static String getRelativeLinkToSubComponent(final String sourceName, final String targetName, final String componentName, final SourceKind sourceKind) {
    return getRelativeLinkToADLSubElem(sourceName, targetName, getSubComponentAnchor(componentName), sourceKind);
  }

  public static String getRelativeLinkToAttribute(final String sourceName, final String targetName, final String attributeName, final SourceKind sourceKind) {
    return getRelativeLinkToADLSubElem(sourceName, targetName, getAttributeAnchor(attributeName), sourceKind);
  }

  public static String getRelativeLinkToInterfaceSection(
      final String sourceName, final String targetName, final SourceKind sourceKind) {
    return getRelativeLinkToADLSubElem(sourceName, targetName, CLIENT_INTERFACE_SECTION_ANCHOR + "_SUM", sourceKind);
  }

  public static String getRelativeLinkToAttributeSection(
      final String sourceName, final String targetName, final SourceKind sourceKind) {
    return getRelativeLinkToADLSubElem(sourceName, targetName, ATTRIBUTE_SECTION_ANCHOR + "_SUM", sourceKind);
  }

  public static String getRelativeLinkToSubComponentSection(
      final String sourceName, final String targetName, final SourceKind sourceKind) {
    return getRelativeLinkToADLSubElem(sourceName, targetName, COMPONENT_SECTION_ANCHOR + "_SUM", sourceKind);
  }

  public static String getRelativeLinkToStruct(final String sourceName, final String targetName, final String structName, final SourceKind sourceKind) {
    return getRelativeLinkToITFSubElem(sourceName, targetName, getStructAnchor(structName), sourceKind);
  }

  public static String getRelativeLinkToUnion(final String sourceName, final String targetName, final String unionName, final SourceKind sourceKind) {
    return getRelativeLinkToITFSubElem(sourceName, targetName, getUnionAnchor(unionName), sourceKind);
  }

  public static String getRelativeLinkToEnum(final String sourceName, final String targetName, final String enumName, final SourceKind sourceKind) {
    return getRelativeLinkToITFSubElem(sourceName, targetName, getEnumAnchor(enumName), sourceKind);
  }

  public static String getRelativeLinkToTypedef(final String sourceName, final String targetName, final String typedefName, final SourceKind sourceKind) {
    return getRelativeLinkToITFSubElem(sourceName, targetName, getTypedefAnchor(typedefName), sourceKind);
  }

  public static String getRelativeLinkToTypeSection(final String sourceName, final String targetName, final SourceKind sourceKind) {
    return getRelativeLinkToITFSubElem(sourceName, targetName, TYPE_SECTION_ANCHOR + "_SUM", sourceKind);
  }

  public static String getRelativeLinkToMethod(final String sourceName, final String targetName, final String methodName, final SourceKind sourceKind) {
    return getRelativeLinkToITFSubElem(sourceName, targetName, getMethodAnchor(methodName), sourceKind);
  }

  public static String getRelativeLinkToMethodSection(final String sourceName,final String targetName, final SourceKind sourceKind) {
    return getRelativeLinkToITFSubElem(sourceName, targetName, METHOD_SECTION_ANCHOR + "_SUM", sourceKind);
  }

  protected static String getDefinitionName(final String sourceDirectory, final String definitionFileName) throws IOException {

    final String fileName;
    final String rootPath[];
    final String filePath[];

    if (System.getProperty("os.name").startsWith("Windows")) {
      // Equivalent implementation for Windows of PathHelper.removeExtension(definitionFileName);
      final int i = definitionFileName.lastIndexOf('.');
      final int j = definitionFileName.lastIndexOf('\\');
      if (i == -1 || j > i)
        fileName = definitionFileName;
      else fileName = definitionFileName.substring(0, i);

      rootPath = sourceDirectory.split("\\" + File.separator);
      filePath = fileName.split("\\" + File.separator);
    } else {
      // SSZ TODO: CHECK IF THE REMOVE EXTENSION METHOD HAS BEEN FIXED IN LATER VERSIONS OF THE LIB (1.0/1.1)
      // HERE IT ONLY WORKS FOR UNIX PATHS
      fileName = PathHelper.removeExtension(definitionFileName);
      rootPath = sourceDirectory.split(File.separator);
      filePath = fileName.split(File.separator);
    }

    int i;
    for(i = 0; i < rootPath.length; i++) {
      if(!filePath[i].equals(rootPath[i])) {
        throw new IllegalArgumentException("rootDirectory must be a parent of file");
      }
    }

    final StringBuilder sb = new StringBuilder();
    for(; i < filePath.length - 1; i++) {
      sb.append(filePath[i]);
      sb.append('.');
    }
    if(i < filePath.length) //necessary for empty package name
      sb.append(filePath[i]);

    return sb.toString();
  }

  public static class AnnotationDecoration {
    public String name;
    public String parameterString = null;

    public AnnotationDecoration(final Annotation annotation) {
      name = annotation.getClass().getName();
      final StringBuilder sb = new StringBuilder();
      for(final Field f: annotation.getClass().getFields()) {
        if(f.getAnnotation(AnnotationElement.class) != null) {
          try {
            sb.append(f.getName());
            sb.append("=");
            sb.append(f.get(annotation).toString());
            sb.append(",");
          } catch (final Exception e) {
            //ignore
          }
        }
      }
      if(sb.length() > 1) {
        sb.deleteCharAt(sb.length()-1);
        parameterString = sb.toString();
      }
    }
  }

  public static void addAnnotationDecoration(
      final Node container) {
    final Annotation annotations[] = AnnotationHelper.getAnnotations(container);
    if(annotations != null) {
      final List<AnnotationDecoration> annotationsList = new ArrayList<AnnotationDecoration>(annotations.length);
      for (final Annotation annotation : annotations) {
        annotationsList.add(new AnnotationDecoration(annotation));
        container.astSetDecoration("annotation_list", annotationsList);
      }
    }
  }

  public static String getValueString(final Value value) {
    String valueString = null;
    if(value != null) {

      if (value instanceof BooleanLiteral) {
        valueString = ((BooleanLiteral) value).getValue();
      } else if (value instanceof NumberLiteral) {
        valueString = ((NumberLiteral) value).getValue();
      } else if (value instanceof StringLiteral) {
        valueString = "\"" + ((StringLiteral) value).getValue() + "\"";
      } else if (value instanceof Reference) {
        valueString = ((Reference) value).getRef();
      }
    }
    return valueString;
  }
}
