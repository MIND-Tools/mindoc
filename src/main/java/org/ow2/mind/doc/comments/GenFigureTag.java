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

package org.ow2.mind.doc.comments;

import static org.ow2.mind.doc.Launcher.DOC_FILES_DIRECTORY;

import org.ow2.mind.doc.HTMLDocumentationHelper.SourceKind;


public class GenFigureTag extends CommentTag {

  public int width  = -1;
  public int height = -1;


  protected GenFigureTag(final int beginIndex, final int endIndex) {
    super(beginIndex, endIndex);
  }

  public void setWidth(final int width) {
    this.width = width;
  }

  public void setHeight(final int height) {
    this.height = height;
  }

  public int getHeight() {
    return height;
  }

  public int getWidth() {
    return width;
  }

  @Override
  public String getReplacement(final String definitionName, final SourceKind sourceKind) {
    String sizeString = "";
    if(width != -1) {
      sizeString = String.format("width=\"%dpx\"", width);
    } else if(height != -1) {
      sizeString = String.format("height=\"%dpx\"", height);
    }

    final String defShortName = definitionName.substring(definitionName.lastIndexOf(".") + 1);

    // construct embed tag for the according SVG file
    return String.format("<embed src=\"%s\" class=\"svgFigure\" %s type=\"image/svg+xml\"/>",
        DOC_FILES_DIRECTORY + "/" + defShortName + ".svg", sizeString);
  }

}
