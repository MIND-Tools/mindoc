package org.ow2.mind.doc;

import java.io.File;
import org.ow2.mind.doc.DefinitionDocumentGenerator;
import org.testng.annotations.Test;

public class IDLDocumentationGeneratorTest {
  private final File target = new File("target/doc");
  private final File source[] = new File[] { new File("src/test/resources")};
  DefinitionDocumentGenerator generator;

  public IDLDocumentationGeneratorTest() throws Exception {
    generator = new DefinitionDocumentGenerator(source, source[0], target);
  }

  @Test
  public void testSimpleInterface() throws Exception {
    generator.generateIDLDocumentation("interfaces.TestInterface");
  }
}
