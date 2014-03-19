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

import static org.ow2.mind.doc.Launcher.DOC_FILES_DIRECTORY;
import static org.ow2.mind.doc.Launcher.HTML_RESOURCES_DIR;
import static org.ow2.mind.doc.Launcher.getMindocHome;
import static org.ow2.mind.doc.Launcher.logger;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.FileUtils;

public class ResourceCopier {

  private static class DocFilesCopier extends DirectoryWalker {
    private final String sourceDirectory;
    private final File targetDirectory;

    public DocFilesCopier(final File sourceDirectory, final File targetDirectory) throws IOException {
      this.sourceDirectory = sourceDirectory.getCanonicalPath();
      this.targetDirectory = targetDirectory;
    }

    public static List<File> copy(final File sourceDirectory, final File targetDirectory) throws IOException {
      final DocFilesCopier finder = new DocFilesCopier(sourceDirectory, targetDirectory);
      final List<File> results = new LinkedList<File>();
      finder.walk(sourceDirectory, results);
      return results;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected boolean handleDirectory(final File directory, final int depth,
        final Collection results) throws IOException {
      if(directory.getName().equals(DOC_FILES_DIRECTORY)) {
        final String docFilesDirName = directory.getCanonicalPath();
        final String localName = docFilesDirName.substring(sourceDirectory.length());
        final File destDir = new File(targetDirectory, localName);
        FileUtils.copyDirectory(directory, destDir);
        return false; //don't walk in subdirectories
      } else {
        return true;
      }
    }
  }

  public static void copyResources(final File sourceDirectories[], final File targetDirectory) {
    //copy js, css and html resources
    final File htmlResourceDirectory = new File(getMindocHome(), HTML_RESOURCES_DIR);

    if(!htmlResourceDirectory.canRead()) {
      logger.severe("Cannot read resource directory: " + htmlResourceDirectory.getPath());
      System.exit(1);
    }

    final FileFilter ff = new FileFilter() {
      public boolean accept(final File file) {
        return file.getName().endsWith(".css") || file.getName().endsWith(".html") || file.getName().endsWith(".js") || file.isDirectory();
      }
    };
    try {
      FileUtils.copyDirectory(htmlResourceDirectory, targetDirectory, ff);
    } catch (final IOException e) {
      logger.severe("Error while copying resources: " + e.getLocalizedMessage());
      System.exit(1);
    }



    //copy **/doc-files/*
    try {
      for (final File sourceDirectory : sourceDirectories) {
        DocFilesCopier.copy(sourceDirectory, targetDirectory);
      }
    } catch (final IOException e) {
      logger.severe("Error while copying resources: " + e.getLocalizedMessage());
      System.exit(1);
    }
  }

}
