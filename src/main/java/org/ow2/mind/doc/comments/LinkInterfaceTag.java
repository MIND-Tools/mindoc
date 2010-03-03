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

import static org.ow2.mind.doc.HTMLDocumentationHelper.getRelativeLinkToEnum;
import static org.ow2.mind.doc.HTMLDocumentationHelper.getRelativeLinkToMethod;
import static org.ow2.mind.doc.HTMLDocumentationHelper.getRelativeLinkToStruct;
import static org.ow2.mind.doc.HTMLDocumentationHelper.getRelativeLinkToTypedef;
import static org.ow2.mind.doc.HTMLDocumentationHelper.getRelativeLinkToUnion;
import static org.ow2.mind.doc.HTMLDocumentationHelper.getRelativePathToITF;

import org.ow2.mind.doc.HTMLDocumentationHelper;
import org.ow2.mind.doc.HTMLDocumentationHelper.SourceKind;
import org.ow2.mind.doc.comments.LinkTag.InterfaceLinkElementType;


public class LinkInterfaceTag extends LinkTag<InterfaceLinkElementType> {

  public LinkInterfaceTag(final String target, final String subElemType, final String subElemName,
      final int beginIndex, final int endIndex) throws CommentParserException {
    super(target, subElemType, subElemName, beginIndex, endIndex);
    if(this.subElemType == InterfaceLinkElementType.TYPES && subElemName != null) {
      throw new CommentParserException();
    } else if(this.subElemType != null) {
      if(this.subElemType != InterfaceLinkElementType.TYPES && this.subElemType != InterfaceLinkElementType.METHODS && subElemName == null) {
        throw new CommentParserException();
      }
    }
  }

  @Override
  protected InterfaceLinkElementType parseSubElemType(final String type) {
    return InterfaceLinkElementType.valueOf(type.toUpperCase());
  }

  @Override
  protected String getSubElemHref(final String definitionName, final HTMLDocumentationHelper.SourceKind sourceKind) {
    switch (getSubElemType()) {
      case ENUMS: return getRelativeLinkToEnum(definitionName, getTarget(), getSubElemName(), sourceKind);
      case STRUCTS: return getRelativeLinkToStruct(definitionName, getTarget(), getSubElemName(), sourceKind);
      case UNIONS: return getRelativeLinkToUnion(definitionName, getTarget(), getSubElemName(), sourceKind);
      case TYPEDEFS: return getRelativeLinkToTypedef(definitionName, getTarget(), getSubElemName(), sourceKind);
      case METHODS: return getRelativeLinkToMethod(definitionName, getTarget(), getSubElemName(), sourceKind);
      default: return "";
    }
  }

  @Override
  protected String getSubElemTypeHref(final String definitionName,
      final SourceKind sourceKind) {
    switch (getSubElemType()) {
      case METHODS: return HTMLDocumentationHelper.getRelativeLinkToMethodSection(definitionName, getTarget(), sourceKind);
      case TYPES: return HTMLDocumentationHelper.getRelativeLinkToTypeSection(definitionName, getTarget(), sourceKind);
      default: return "";
    }
  }

  @Override
  protected String getTargetHref(final String definitionName, final HTMLDocumentationHelper.SourceKind sourceKind) {
    return getRelativePathToITF(definitionName, getTarget(), sourceKind);
  }

  @Override
  protected String getSubElemLabel() {
    switch (getSubElemType()) {
      case ENUMS: return "enum";
      case STRUCTS: return "struct";
      case UNIONS: return "union";
      case TYPEDEFS: return "typedef";
      case METHODS: return "method";
      default: return "";
    }
  }


}
