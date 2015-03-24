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
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.objectweb.fractal.adl.Loader;
import org.ow2.mind.idl.IDLLoader;
import org.ow2.mind.io.BasicOutputFileLocator;

import com.google.inject.Inject;

public class DefinitionTreeDocumentationGenerator extends DirectoryWalker {

  @Inject
  protected Loader                    adlLoader;

  @Inject
  protected IDLLoader                 idlLoader;

  @Inject
  DefinitionDocumentGenerator generator;

  private final Set<String> adlDefinitionsSet = new HashSet<String>();
  private final Set<String> itfDefinitionsSet = new HashSet<String>();

  public DefinitionTreeDocumentationGenerator() {
    super(FileFilterUtils.trueFileFilter(), FileFilterUtils.orFileFilter(
        FileFilterUtils.suffixFileFilter(".adl"),
        FileFilterUtils.suffixFileFilter(".itf")), -1);
  }

  public void generateDocumentation(final File sourceDirectories[],
      final File targetDirectory, final Map<Object, Object> context) throws Exception {

    context.put(BasicOutputFileLocator.OUTPUT_DIR_CONTEXT_KEY, targetDirectory);
    final URL urls[] = new URL[sourceDirectories.length];
    for (int i = 0; i < sourceDirectories.length; i++) {
      final File directory = sourceDirectories[i];
      urls[i] = directory.toURI().toURL();
    }

    final ClassLoader srcClassLoader = new URLClassLoader(urls,
        Launcher.class.getClassLoader());
    context.put("classloader", srcClassLoader);

    /*
     * First LOAD all in cache so we can do global coherent generation.
     * This is critical for inheritance information management.
     *
     * Previous documentation generation used for each file to both do
     * in the same loop:
     * - load
     * - generate
     *
     * However, if mindoc first handled a super type and generated its
     * documentation, the child type "extends" information and its
     * reverse "sub-definition" info were computed afterwards, leading
     * to missing information (since no super-type generation was triggered).
     *
     * Issue solved with 2 consecutive loops.
     * Standard generator behaviour with "load" is kept since the cache
     * mecanism will be triggered and the pre-loaded definition will be
     * directly returned: No performance issue.
     */
    for (final File rootDirectory : sourceDirectories) {
      final List<File> definitions = new LinkedList<File>();

      walk(rootDirectory, definitions);

      context.put("sourceDirectory", rootDirectory);

      for (final File definition : definitions) {
        final String definitionName = HTMLDocumentationHelper
            .getDefinitionName(rootDirectory.getCanonicalPath(),
                definition.getCanonicalPath());

        if (definition.getName().endsWith(".adl") && !adlDefinitionsSet.add(definitionName)) {
          Launcher.logger.warning("Duplicate definition of " + definitionName + " component - skipping");
          continue;
        }
        else if (definition.getName().endsWith(".itf") && !itfDefinitionsSet.add(definitionName)) {
          Launcher.logger.warning("Duplicate definition of " + definitionName + " interface - skipping");
          continue;
        }

        Launcher.logger.finer("Loading " + definitionName + " into cache...");

        if (definition.getName().endsWith(".adl")) {
          adlLoader.load(definitionName, context);
        } else if (definition.getName().endsWith(".itf")) {
          idlLoader.load(definitionName, context);
        }
      }
    }
    // Now GENERATE
    for (final File rootDirectory : sourceDirectories) {
      final List<File> definitions = new LinkedList<File>();

      walk(rootDirectory, definitions);

      context.put("sourceDirectory", rootDirectory);

      for (final File definition : definitions) {
        final String definitionName = HTMLDocumentationHelper
            .getDefinitionName(rootDirectory.getCanonicalPath(),
                definition.getCanonicalPath());

        Launcher.logger.finer("Generating documentation for " + definitionName);

        if (definition.getName().endsWith(".adl")) {
          generator.generateADLDocumentation(definitionName, context);
        } else if (definition.getName().endsWith(".itf")) {
          generator.generateIDLDocumentation(definitionName, context);
        }
      }
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void handleFile(final File file, final int depth,
      @SuppressWarnings("rawtypes") final Collection results)
          throws IOException {
    results.add(file);
  }

}
