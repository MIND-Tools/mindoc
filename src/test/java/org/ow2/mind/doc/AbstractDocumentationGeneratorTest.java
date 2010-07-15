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
 * Authors: ali-erdem.ozcan@st.com, michel.metzger@st.com
 * Contributors:
 */

package org.ow2.mind.doc;

import java.io.File;
import java.util.Collection;

import org.objectweb.fractal.adl.ADLException;
import org.ow2.mind.doc.DefinitionDocumentGenerator;

public class AbstractDocumentationGeneratorTest {
  private final File target = new File("target/doc");
  private final File sources[] = new File[] { new File("src/test/resources")};

  DefinitionDocumentGenerator generator;

  public AbstractDocumentationGeneratorTest() throws Exception  {
    generator = new DefinitionDocumentGenerator(sources, sources[0], target);
  }

  /*public Definition loadADL(final String adlName) throws ADLException {
    return generator.load(adlName);
  }*/

  public Collection<File> compileADL(final String adlName)
      throws ADLException, InterruptedException {
    generator.generateADLDocumentation(adlName);
    return null;
  }

  /*public IDL loadIDL(final String idlName) throws ADLException {
    return generator.loadIDL(idlName);
  }*/

  public void compileIDL(final String idlName)
      throws ADLException, InterruptedException {
    generator.generateIDLDocumentation(idlName);
  }
}
