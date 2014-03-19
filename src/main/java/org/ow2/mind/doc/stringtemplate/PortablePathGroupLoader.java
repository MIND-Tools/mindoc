/**
 * Copyright (C) 2013 Schneider-Electric
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
 * Authors: Stephane Seyvoz (sseyvoz@assystem.com)
 * Contributors:
 */
package org.ow2.mind.doc.stringtemplate;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.antlr.stringtemplate.PathGroupLoader;
import org.antlr.stringtemplate.StringTemplateErrorListener;

/**
 * @author Stephane Seyvoz
 * This class is used to make the PathGroupLoader portable.
 * The standard implementation using ':' as path separator,
 * any absolute Windows path (e.g. D:\path\to\file)
 * is split to "D" and "\path\to\file" which is obviously
 * an error, and used to be an issue when running our program
 * from a different drive than where its files (including
 * StringTemplates...) are stored.
 */
public class PortablePathGroupLoader extends PathGroupLoader {

  /** Pass a single dir or multiple dirs separated by platform-specific
   * separators from which to load groups/interfaces.
   */
  public PortablePathGroupLoader(final String dirStr, final StringTemplateErrorListener errors) {
    // Needed otherwise it won't compile. However useless here since we'll override its values.
    super(dirStr, errors);
    // Cancel the initialization we don't want
    dirs = null;

    this.errors = errors;
    final StringTokenizer tokenizer = new StringTokenizer(dirStr, File.pathSeparator, false);
    while (tokenizer.hasMoreElements()) {
        final String dir = (String) tokenizer.nextElement();
        if ( dirs==null ) {
            dirs = new ArrayList();
        }
        dirs.add(dir);
    }
  }

}
