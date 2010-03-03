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

import org.ow2.mind.doc.DocumentationIndexGenerator;
import org.testng.annotations.Test;

public class DocumentationIndexGeneratorTest {

  private final File target = new File("target/doc");
  private final File source[] = new File []{new File ("src/test/resources")};
  private final File resource = new File ("src/main/resources");
  private final File overview = new File ("src/test/resources/overview.html");
  private final String title = "Test Documentation<br/><font size=\"-1\">small text</font>";


  @Test
  public void testAllDefinitionFrame() throws Exception {
    final DocumentationIndexGenerator generator =
      new DocumentationIndexGenerator(source, resource, title, overview);
    generator.generateAllDefinitionFrame(target);
  }

  @Test
  public void testOverviewFrame() throws Exception {
    final DocumentationIndexGenerator generator =
      new DocumentationIndexGenerator(source, resource, title, overview);
    generator.generateOverviewFrame(target);
  }

  @Test
  public void testPackageFrame() throws Exception {
    final DocumentationIndexGenerator generator =
      new DocumentationIndexGenerator(source, resource, title, overview);
    generator.generatePackageFrame(target);
  }

  @Test
  public void testOverviewSummary() throws Exception {
    final DocumentationIndexGenerator generator =
      new DocumentationIndexGenerator(source, resource, title, overview);
    generator.generateOverviewSummary(target);
  }

  @Test
  public void testPackageSummary() throws Exception {
    final DocumentationIndexGenerator generator =
      new DocumentationIndexGenerator(source, resource, title, overview);
    generator.generatePackageSummary(target);
  }
}
