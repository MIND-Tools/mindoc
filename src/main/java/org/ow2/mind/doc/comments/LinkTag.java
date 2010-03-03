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
 * Contributors: 
 */
package org.ow2.mind.doc.comments;

import static org.ow2.mind.doc.HTMLDocumentationHelper.getShortName;

import org.ow2.mind.doc.HTMLDocumentationHelper;


public abstract class LinkTag<E extends Enum<E>> extends CommentTag {

  public static enum ComponentLinkElementType {
    INTERFACES, COMPONENTS, ATTRIBUTES;
  }

  public static enum InterfaceLinkElementType {
    METHODS,
    UNIONS,
    STRUCTS,
    ENUMS,
    TYPEDEFS,
    TYPES
  }

  protected static final String THIS_IDENTIFIER = "this";
  protected String target;
  protected final E subElemType;
  protected final String subElemName;

  public LinkTag(final String target, final String type, final String subElemName,
      final int beginIndex, final int endIndex) throws CommentParserException {
    super(beginIndex, endIndex);

    if(type != null)
      this.subElemType = parseSubElemType(type);
    else
      this.subElemType = null;

    this.target = target;
    this.subElemName = subElemName;
  }

  protected abstract E parseSubElemType(String type) throws CommentParserException;

  public String getTarget() {
    return target;
  }

  public E getSubElemType() {
    return subElemType;
  }

  public String getSubElemName() {
    return subElemName;
  }

  protected abstract String getTargetHref(final String definitionName, final HTMLDocumentationHelper.SourceKind sourceKind);
  protected abstract String getSubElemHref(final String definitionName, final HTMLDocumentationHelper.SourceKind sourceKind);
  protected abstract String getSubElemTypeHref(final String definitionName, final HTMLDocumentationHelper.SourceKind sourceKind);
  protected abstract String getSubElemLabel();

  @Override
  public String getReplacement(final String definitionName, final HTMLDocumentationHelper.SourceKind sourceKind) {

    //Resolve scope when it's "this"
    if(THIS_IDENTIFIER.equals(target)) {
      if(sourceKind == HTMLDocumentationHelper.SourceKind.PACKAGE) {
        throw new IllegalArgumentException("Cannot use 'this' as a scope in package documentation");
      }
      target = definitionName;
    }

    if(subElemType == null) { //link directly to the target
      return String.format("<a href=\"%s\" title=\"%s\">%s</a>",
          getTargetHref(definitionName, sourceKind),
          getTarget(), getShortName(getTarget()));
    } else {
      if(subElemName == null) {
        return String.format("<a href=\"%s\">%s</a>",
            getSubElemTypeHref(definitionName, sourceKind),
            subElemType.toString().toLowerCase());
      } else {
        return String.format("<a href=\"%s\" title=\"%s %s.%s\">%s.%s</a>",
            getSubElemHref(definitionName, sourceKind),
            getSubElemLabel(), getTarget(), getSubElemName(),
            getShortName(getTarget()), getSubElemName());
      }
    }
  }

  @Override
  public String toString() {
    return String.format("link<scope=%s,type=%s,target=%s,begin=%d,end=%d>",
        target,
        subElemType != null ? subElemType.toString().toLowerCase() : "null",
        subElemName,
        beginIndex, endIndex);
  }


}
