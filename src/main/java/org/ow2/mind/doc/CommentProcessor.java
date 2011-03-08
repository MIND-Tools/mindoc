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

package org.ow2.mind.doc;

public final class CommentProcessor {

  public final static String processComment(String commentString) {
    commentString = commentString.trim();
    if(commentString.startsWith("/**") && commentString.endsWith("*/")) {
      String result = commentString.substring(3);
      result = result.substring(0, result.length() - 2);
      final String lines[] = result.split("\n");
      for (int i = 0; i < lines.length; i++) {
        String l = lines[i].trim();
        if (l.startsWith("*")) {
          l = l.substring(1);
        }
        lines[i] = l.trim();
      }
      final StringBuilder sb = new StringBuilder();
      for (final String string : lines) {
        sb.append(string);
        sb.append('\n');
      }
      return sb.toString();
    } else {
      return null;
    }
  }
}
