/**
 * Copyright (C) 2009 STMicroelectronics
 *
 * This file is part of "Mind Compiler".
 * "Mind Compiler" is a free software tool.
 * This file is licensed under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact: mind@ow2.org
 *
 * Authors: Ali-Erdem Ozcan, Michel Metzger
 * Contributors:
 */

package org.ow2.mind.doc;

import org.testng.annotations.Test;

public class IDLDocumentationGeneratorTest
    extends
      AbstractDocumentationGeneratorTest {

  @Test
  public void testSimpleInterface() throws Exception {
    compileIDL("interfaces.TestInterface");
  }

  @Test
  public void testParamAnnotationInInterface() throws Exception {
    compileIDL("param_test.Service");
  }

  @Test
  public void testReturnAnnotationInInterface() throws Exception {
    compileIDL("return_test.TestInterface");
  }
}
