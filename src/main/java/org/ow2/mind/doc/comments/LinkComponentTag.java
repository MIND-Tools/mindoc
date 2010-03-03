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

import static org.ow2.mind.doc.HTMLDocumentationHelper.getRelativeLinkToAttribute;
import static org.ow2.mind.doc.HTMLDocumentationHelper.getRelativeLinkToInterface;
import static org.ow2.mind.doc.HTMLDocumentationHelper.getRelativeLinkToSubComponent;
import static org.ow2.mind.doc.HTMLDocumentationHelper.getRelativePathToADL;

import org.ow2.mind.doc.HTMLDocumentationHelper;
import org.ow2.mind.doc.HTMLDocumentationHelper.SourceKind;
import org.ow2.mind.doc.comments.LinkTag.ComponentLinkElementType;


public class LinkComponentTag extends LinkTag<ComponentLinkElementType> {

  public LinkComponentTag(final String target, final String subElemType, final String subElemName,
      final int beginIndex, final int endIndex) throws CommentParserException {
    super(target, subElemType, subElemName, beginIndex, endIndex);
  }

  @Override
  protected ComponentLinkElementType parseSubElemType(final String type) throws CommentParserException {
    try {
      return ComponentLinkElementType.valueOf(type.toUpperCase());
    } catch (final IllegalArgumentException e) {
      throw new CommentParserException();
    }
  }

  @Override
  protected  String getTargetHref(final String definitionName, final SourceKind sourceKind) {
    return getRelativePathToADL(definitionName, getTarget(), sourceKind);
  }

  @Override
  protected String getSubElemHref(final String definitionName, final SourceKind sourceKind) {
    switch (getSubElemType()) {
      case INTERFACES:
        return getRelativeLinkToInterface(definitionName, getTarget(), getSubElemName(), sourceKind);
      case ATTRIBUTES:
        return getRelativeLinkToAttribute(definitionName, getTarget(), getSubElemName(), sourceKind);
      case COMPONENTS:
        return getRelativeLinkToSubComponent(definitionName, getTarget(), getSubElemName(), sourceKind);
      default: return "";
    }
  }

  @Override
  protected String getSubElemTypeHref(final String definitionName,
      final SourceKind sourceKind) {
    switch (getSubElemType()) {
      case INTERFACES:
        return HTMLDocumentationHelper.getRelativeLinkToInterfaceSection(definitionName, getTarget(), sourceKind);
      case ATTRIBUTES:
        return HTMLDocumentationHelper.getRelativeLinkToAttributeSection(definitionName, getTarget(), sourceKind);
      case COMPONENTS:
        return HTMLDocumentationHelper.getRelativeLinkToSubComponentSection(definitionName, getTarget(), sourceKind);
      default: return "";
    }
  }

  @Override
  protected String getSubElemLabel() {
    switch (getSubElemType()) {
      case ATTRIBUTES:
        return "attribute";
      case COMPONENTS:
        return "component";
      case INTERFACES:
        return "interface";
      default : return "";
    }
  }
}
