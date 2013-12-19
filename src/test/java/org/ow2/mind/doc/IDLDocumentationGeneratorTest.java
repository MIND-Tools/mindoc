
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
