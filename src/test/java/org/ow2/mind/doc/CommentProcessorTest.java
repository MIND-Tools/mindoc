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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

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
