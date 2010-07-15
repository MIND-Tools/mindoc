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

import static org.ow2.mind.doc.comments.CommentTagProcessor.extractTags;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.objectweb.fractal.adl.ADLException;
import org.ow2.mind.doc.HTMLDocumentationHelper.SourceKind;
import org.ow2.mind.doc.comments.CommentTag;
import org.ow2.mind.doc.comments.CommentTagProcessor;
import org.ow2.mind.doc.comments.FigureTag;
import org.testng.annotations.Test;



public class CommentProcessorFigureTagTest extends AbstractDocumentationGeneratorTest {

  public CommentProcessorFigureTagTest() throws Exception {
    super();
  }

  @Test
  public void testFigure() {
    final String comment = "bla bla @figure test/test.jpg bla bla bla";
    final List<CommentTag> tags = extractTags(comment);
    assertEquals(tags.size(), 1, "Invalid results: 1 tag should have been extracted, actual result is " + tags.size());
    assertTrue(tags.get(0) instanceof FigureTag);
    final FigureTag tag = (FigureTag) tags.get(0);
    assertEquals(tag.path, "test/test.jpg");
  }

  @Test
  public void testFigureDash() {
    final String comment = "bla bla @figure test/figure-test.jpg bla bla bla";
    final List<CommentTag> tags = extractTags(comment);
    assertEquals(tags.size(), 1, "Invalid results: 1 tag should have been extracted, actual result is " + tags.size());
    assertTrue(tags.get(0) instanceof FigureTag);
    final FigureTag tag = (FigureTag) tags.get(0);
    assertEquals(tag.path, "test/figure-test.jpg");
  }

  @Test
  public void testFigureSize() {
    final String comment = "bla bla @figure test/figure-test.jpg width=50px.bla bla bla. @figure test/figure-test.jpg height=130px";
    final List<CommentTag> tags = extractTags(comment);
    assertEquals(tags.size(), 2, "Invalid results: 1 tag should have been extracted, actual result is " + tags.size());
    assertTrue(tags.get(0) instanceof FigureTag);
    FigureTag tag = (FigureTag) tags.get(0);
    assertEquals(tag.path, "test/figure-test.jpg");
    assertEquals(tag.width, 50);
    assertTrue(tags.get(1) instanceof FigureTag);
    tag = (FigureTag) tags.get(1);
    assertEquals(tag.path, "test/figure-test.jpg");
    assertEquals(tag.height, 130);
  }

  @Test
  public void testReplacementImage() throws ADLException {
    final String comment = "more bla bla @figure test/test.gif. This is a comment with a figure.";
    final CommentTagProcessor commentProc = new CommentTagProcessor("composite_test.Composite1", comment, SourceKind.COMPONENT);
    final String result = commentProc.replaceTagsInComment();
    //System.out.println("Original: \"" + comment + "\"");
    //System.out.println("Result:   \"" + result  + "\"");
    assertEquals(result, "more bla bla <img src=\"doc-files/test/test.gif.\" class=\"figure\" /> This is a comment with a figure.");
  }

  @Test
  public void testReplacementFigureSize() throws ADLException {
    final String comment = "more bla bla @figure test/test.gif width=10px. This is a comment with a figure.";
    final CommentTagProcessor commentProc = new CommentTagProcessor("composite_test.Composite1", comment, SourceKind.COMPONENT);
    final String result = commentProc.replaceTagsInComment();
    //System.out.println("Original: \"" + comment + "\"");
    //System.out.println("Result:   \"" + result  + "\"");
    assertEquals(result, "more bla bla <img src=\"doc-files/test/test.gif\" class=\"figure\" width=\"10px\"/>. This is a comment with a figure.");
  }

  @Test
  public void testFiguresInComments() throws ADLException, InterruptedException {
    compileADL("figure_test.Composite");
  }
}
