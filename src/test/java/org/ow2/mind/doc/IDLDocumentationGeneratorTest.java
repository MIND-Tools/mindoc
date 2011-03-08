
package org.ow2.mind.doc;

import org.testng.annotations.Test;

public class IDLDocumentationGeneratorTest
    extends
      AbstractDocumentationGeneratorTest {

  @Test
  public void testSimpleInterface() throws Exception {
    compileIDL("interfaces.TestInterface");
  }
}
