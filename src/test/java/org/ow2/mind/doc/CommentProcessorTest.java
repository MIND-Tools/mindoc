package org.ow2.mind.doc;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.ow2.mind.doc.adl.parser.CommentProcessor;
import org.testng.annotations.Test;


public class CommentProcessorTest {

  @Test
  public void testJavadocComments(){
    final String comment =
    "/**\n" +
    " * This is a comment.\n" +
    " * With multiple lines.\n" +
    " * But it does not provide information.\n" +
    " */";

    final String expected =
      "\n" +
      "This is a comment.\n" +
      "With multiple lines.\n" +
      "But it does not provide information.\n" +
      "\n";

    final String result = CommentProcessor.processComment(comment);
    assertEquals(result, expected);
  }

  @Test
  public void testSingleLineComments(){
    final String comment = "// A single line comment";
    final String result = CommentProcessor.processComment(comment);
    assertNull(result);
  }

  @Test
  public void testMultiLineComments(){
    final String comment =
      "/*A multiple lines comment.\n" +
      " bla bla.*/";
    final String result = CommentProcessor.processComment(comment);
    assertNull(result);
  }


}
