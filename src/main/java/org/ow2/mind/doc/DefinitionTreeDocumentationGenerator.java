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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.filefilter.FileFilterUtils;


public class DefinitionTreeDocumentationGenerator extends DirectoryWalker {
  private final File sourceDirectories[];

  public DefinitionTreeDocumentationGenerator(final File sourceDirectories[]) {
    super(FileFilterUtils.trueFileFilter(),
        FileFilterUtils.orFileFilter(FileFilterUtils.suffixFileFilter(".adl"),
            FileFilterUtils.suffixFileFilter(".itf")),
        -1);
    this.sourceDirectories = sourceDirectories;
  }

  public void generateDocumentation(final File targetDirectory) throws Exception{
    for (final File rootDirectory : sourceDirectories) {
      final List<File> definitions = new LinkedList<File>();
      walk(rootDirectory, definitions);
      final DefinitionDocumentGenerator generator = new DefinitionDocumentGenerator(sourceDirectories, rootDirectory, targetDirectory);
      for (final File definition : definitions) {
        final String definitionName = HTMLDocumentationHelper.getDefinitionName(rootDirectory.getCanonicalPath(), definition.getCanonicalPath());
        Launcher.logger.finer("Generating documentation for " + definitionName);
        if(definition.getName().endsWith(".adl")) {
          generator.generateADLDocumentation(definitionName);
        } else if(definition.getName().endsWith(".itf")) {
          generator.generateIDLDocumentation(definitionName);
        }
      }
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void handleFile(final File file, final int depth, @SuppressWarnings("rawtypes") final Collection results)
  throws IOException {
    results.add(file);
  }
}
