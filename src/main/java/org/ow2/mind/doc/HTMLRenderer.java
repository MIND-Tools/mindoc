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

import static org.ow2.mind.NameHelper.getPackageName;
import static org.ow2.mind.NameHelper.toValidName;
import static org.ow2.mind.doc.HTMLDocumentationHelper.getShortName;

import org.antlr.stringtemplate.AttributeRenderer;

public class HTMLRenderer implements AttributeRenderer {
  public static final String SHORT_NAME   = "shortName";
  public static final String PACKAGE_NAME = "packageName";
  public static final String NAME_TO_PATH = "nameToPath";
  public static final String TO_LOWER = "toLower";

  public String toString(final Object o, final String format) {
    if (SHORT_NAME.equals(format)) {
      return getShortName(o.toString());
    } else if (PACKAGE_NAME.equals(format)) {
      final String pName = getPackageName(o.toString());
      return  pName == null ? "" : pName;
    } else if (NAME_TO_PATH.equals(format)) {
      return toValidName(o.toString()).replace('.', '/');
    } else if (TO_LOWER.equals(format)) {
      return o.toString().toLowerCase();
    }
    return o.toString();
  }

  public String toString(final Object o) {
    return o.toString();
  }
}
