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
import org.ow2.mind.doc.DefinitionTreeDocumentationGenerator;
import org.testng.annotations.Test;

public class ADLDocumentationGeneratorTest
    extends
      AbstractDocumentationGeneratorTest {

  public ADLDocumentationGeneratorTest() throws Exception {
    super();
  }

  private final File target = new File("target/doc");
  private final File source[] = new File[] { new File("src/test/resources")};

  //@Test
  public void testPrimitive() throws Exception {
    compileADL("helloworld.Client");
  }

  //@Test
  public void testComposite() throws Exception {
    compileADL("composite_test.Composite1");
    compileADL("common.PrimitiveServer");
    compileADL("common.PrimitiveClient");
  }

  //@Test
  public void testType() throws Exception {
    compileADL("type_test.myType");
  }

  //@Test
  public void testTemplate() throws Exception {
    compileADL("template_test.TopLevel");
    compileADL("template_test.MyTemplateComposite");
    compileADL("template_test.PrintClient");
    compileADL("template_test.PrintServer");
    compileADL("template_test.PrintClientImpl");
    compileADL("template_test.PrintServerImpl");
    compileADL("template_test.ProxyComponent");
    compileADL("template_test.ProxyPrintServer");
  }

  //@Test
  public void testExtends() throws Exception {
    compileADL("extends_test.MyComponent");
    compileADL("extends_test.ExtendedComponent1");
    compileADL("extends_test.ExtendedComponent2");
  }

  //@Test
  public void testParameters() throws Exception {
    compileADL("parameters.Top");
    compileADL("parameters.ParameterComponent");
  }

  @Test
  public void testDefinitionDocumentationGenerator() throws Exception {
    final DefinitionTreeDocumentationGenerator generator = new DefinitionTreeDocumentationGenerator(source);
    generator.generateDocumentation(target);
  }
}
