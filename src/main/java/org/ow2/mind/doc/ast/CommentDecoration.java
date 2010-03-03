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

package org.ow2.mind.doc.ast;

import org.objectweb.fractal.adl.Node;

public final class CommentDecoration {
  private CommentDecoration() {
  }

  public final static String COMMENT_DECORATION       = "comment";
  public final static String SHORT_COMMENT_DECORATION = "short_comment";

  public static void setComment(final Node node, final String comment) {
    if (comment != null) {
      node.astSetDecoration(COMMENT_DECORATION, comment);
    }
  }

  public static void setShortComment(final Node node, final String comment) {
    if (comment != null) {
      node.astSetDecoration(SHORT_COMMENT_DECORATION, comment);
    }
  }

  public static String getComment(final Node node) {
    return (String) node.astGetDecoration(COMMENT_DECORATION);
  }

  public static String getShortComment(final Node node) {
    return (String) node.astGetDecoration(SHORT_COMMENT_DECORATION);
  }

}
