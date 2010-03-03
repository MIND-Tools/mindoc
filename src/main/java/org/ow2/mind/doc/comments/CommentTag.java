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

import org.ow2.mind.doc.HTMLDocumentationHelper;

public abstract class CommentTag {

  protected final int beginIndex;
  protected final int endIndex;

  protected CommentTag(final int beginIndex, final int endIndex) {
    this.beginIndex = beginIndex;
    this.endIndex = endIndex;
  }

  public abstract String getReplacement(final String definitionName,
      HTMLDocumentationHelper.SourceKind sourceKind);


  public static class Comparator implements java.util.Comparator<CommentTag> {

    public int compare(final CommentTag tag1, final CommentTag tag2) {
      return tag1.beginIndex - tag2.beginIndex;
    }

  }
}