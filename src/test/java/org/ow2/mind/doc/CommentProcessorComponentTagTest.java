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
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.objectweb.fractal.adl.ADLException;
import org.ow2.mind.doc.HTMLDocumentationHelper.SourceKind;
import org.ow2.mind.doc.comments.CommentTag;
import org.ow2.mind.doc.comments.CommentTagProcessor;
import org.ow2.mind.doc.comments.LinkComponentTag;
import org.ow2.mind.doc.comments.LinkTag.ComponentLinkElementType;
import org.testng.annotations.Test;



public class CommentProcessorComponentTagTest extends AbstractDocumentationGeneratorTest {

  public CommentProcessorComponentTagTest() throws Exception {
    super();
  }


  @Test
  public void testLinkMultipleSpaces() {
    final String comment = "@component     this#interfaces#toto";
    final List<CommentTag> tags = extractTags(comment);

    assertEquals(tags.size(), 1, "Invalid results: 1 tag should have been extracted, actual result is " + tags.size());
    assertTrue(tags.get(0) instanceof LinkComponentTag);
    final LinkComponentTag tag = (LinkComponentTag) tags.get(0);
    assertEquals(tag.getTarget(), "this");
    assertEquals(tag.getSubElemType(), ComponentLinkElementType.INTERFACES);
    assertEquals(tag.getSubElemName(), "toto");
  }


  @Test
  public void testLinkThis() {
    final String comment = "This is a comment with a link. @component this#interfaces#toto";
    final List<CommentTag> tags = extractTags(comment);

    assertEquals(tags.size(), 1, "Invalid results: 1 tag should have been extracted, actual result is " + tags.size());
    assertTrue(tags.get(0) instanceof LinkComponentTag);
    final LinkComponentTag tag = (LinkComponentTag) tags.get(0);
    assertEquals(tag.getTarget(), "this");
    assertEquals(tag.getSubElemType(), ComponentLinkElementType.INTERFACES);
    assertEquals(tag.getSubElemName(), "toto");
  }

  @Test
  public void testLinkOther() {
    final String comment = "This is a comment with a link. @component AComponent#components#tutu";
    final List<CommentTag> tags = extractTags(comment);

    assertEquals(tags.size(), 1, "Invalid results: 1 tag should have been extracted, actual result is " + tags.size());
    assertTrue(tags.get(0) instanceof LinkComponentTag);
    final LinkComponentTag tag = (LinkComponentTag) tags.get(0);
    assertEquals(tag.getTarget(), "AComponent");
    assertEquals(tag.getSubElemType(), ComponentLinkElementType.COMPONENTS);
    assertEquals(tag.getSubElemName(), "tutu");
  }

  @Test
  public void testLinkOtherPackage() {
    final String comment = "This is a comment with a link. @component a.long.package.name.AComponent#attributes#toto";
    final List<CommentTag> tags = extractTags(comment);

    assertEquals(tags.size(), 1, "Invalid results: 1 tag should have been extracted, actual result is " + tags.size());
    assertTrue(tags.get(0) instanceof LinkComponentTag);
    final LinkComponentTag tag = (LinkComponentTag) tags.get(0);
    assertEquals(tag.getTarget(), "a.long.package.name.AComponent");
    assertEquals(tag.getSubElemType(), ComponentLinkElementType.ATTRIBUTES);
    assertEquals(tag.getSubElemName(), "toto");
  }

  @Test
  public void testLinkDefinition() {
    final String comment = "This is a comment with a link. @component AComponent";
    final List<CommentTag> tags = extractTags(comment);

    assertEquals(tags.size(), 1, "Invalid results: 1 tag should have been extracted, actual result is " + tags.size());
    assertTrue(tags.get(0) instanceof LinkComponentTag);
    final LinkComponentTag tag = (LinkComponentTag) tags.get(0);
    assertEquals(tag.getTarget(), "AComponent");
    assertEquals(tag.getSubElemType(), null);
    assertNull(tag.getSubElemName());
  }

  @Test
  public void testLinkDefinitionWithPackage() {
    final String comment = "@component package.name.AComponent. This is a comment with a link. ";
    final List<CommentTag> tags = extractTags(comment);

    assertEquals(tags.size(), 1, "Invalid results: 1 tag should have been extracted, actual result is " + tags.size());
    assertTrue(tags.get(0) instanceof LinkComponentTag);
    final LinkComponentTag tag = (LinkComponentTag) tags.get(0);
    assertEquals(tag.getTarget(), "package.name.AComponent");
    assertEquals(tag.getSubElemType(), null);
    assertNull(tag.getSubElemName());
  }

  @Test
  public void testLinkSectionThis() throws Exception {
    final String comment = "This is a comment @component this#attributes. More details";
    final List<CommentTag> tags = extractTags(comment);

    assertEquals(tags.size(), 1, "Invalid results: 1 tag should have been extracted, actual result is " + tags.size());
    assertTrue(tags.get(0) instanceof LinkComponentTag);
    final LinkComponentTag tag = (LinkComponentTag) tags.get(0);
    assertEquals(tag.getTarget(), "this");
    assertEquals(tag.getSubElemType(), ComponentLinkElementType.ATTRIBUTES);
    assertNull(tag.getSubElemName());
  }

  @Test
  public void testLinkSectionOther() throws Exception {
    final String comment = "This is a comment @component package.Component#components. More details";
    final List<CommentTag> tags = extractTags(comment);

    assertEquals(tags.size(), 1, "Invalid results: 1 tag should have been extracted, actual result is " + tags.size());
    assertTrue(tags.get(0) instanceof LinkComponentTag);
    final LinkComponentTag tag = (LinkComponentTag) tags.get(0);
    assertEquals(tag.getTarget(), "package.Component");
    assertEquals(tag.getSubElemType(), ComponentLinkElementType.COMPONENTS);
    assertNull(tag.getSubElemName());
  }

  @Test
  public void testReplacementInterface() throws ADLException {
   final String comment = "This is a comment with a link. @component this#interfaces#toto";
   final CommentTagProcessor commentProc = new CommentTagProcessor("composite_test.Composite1", comment, SourceKind.COMPONENT);
   final String result = commentProc.replaceTagsInComment();
   assertEquals(result, "This is a comment with a link. <a href=\"#ITF_toto\" title=\"interface composite_test.Composite1.toto\">Composite1.toto</a>");
   //System.out.println("Original: \"" + comment + "\"");
   //System.out.println("Result:   \"" + result  + "\"");
  }

  @Test
  public void testReplacementInterface2() throws ADLException {
   final String comment = "This is a comment with a link. @component package.name.AComponent#interfaces#toto";
   final CommentTagProcessor commentProc = new CommentTagProcessor("composite_test.Composite1", comment, SourceKind.COMPONENT);
   final String result = commentProc.replaceTagsInComment();
   assertEquals(result, "This is a comment with a link. <a href=\"../package/name/AComponent.ADL.html#ITF_toto\" title=\"interface package.name.AComponent.toto\">AComponent.toto</a>");
   //System.out.println("Original: \"" + comment + "\"");
   //System.out.println("Result:   \"" + result  + "\"");
  }

  @Test
  public void testReplacementComponent() throws ADLException {
    final String comment = "This is a comment with a link. @component AComponent#components#tutu";
    final CommentTagProcessor commentProc = new CommentTagProcessor("composite_test.Composite1", comment, SourceKind.COMPONENT);
    final String result = commentProc.replaceTagsInComment();
    assertEquals(result, "This is a comment with a link. <a href=\"../AComponent.ADL.html#CMP_tutu\" title=\"component AComponent.tutu\">AComponent.tutu</a>");
    //System.out.println("Original: \"" + comment + "\"");
    //System.out.println("Result:   \"" + result  + "\"");
   }

  @Test
  public void testReplacementAttribute() throws ADLException {
    final String comment = "This is a comment with a link. @component AComponent#attributes#tutu";
    final CommentTagProcessor commentProc = new CommentTagProcessor("composite_test.Composite1", comment, SourceKind.COMPONENT);
    final String result = commentProc.replaceTagsInComment();
    assertEquals(result, "This is a comment with a link. <a href=\"../AComponent.ADL.html#ATTR_tutu\" title=\"attribute AComponent.tutu\">AComponent.tutu</a>");
    //System.out.println("Original: \"" + comment + "\"");
    //System.out.println("Result:   \"" + result  + "\"");
   }

  @Test
  public void testReplacementSectionThis() throws ADLException {
    final String comment = "This is a comment with a link to its @component this#attributes";
    final CommentTagProcessor commentProc = new CommentTagProcessor("composite_test.Composite1", comment, SourceKind.COMPONENT);
    final String result = commentProc.replaceTagsInComment();
    assertEquals(result, "This is a comment with a link to its <a href=\"#ATTRIBUTES_SUM\">attributes</a>");
    //System.out.println("Original: \"" + comment + "\"");
    //System.out.println("Result:   \"" + result  + "\"");
   }

  @Test
  public void testReplacementSectionOther() throws ADLException {
    final String comment = "This is a comment with a link to some other @component AComponent#interfaces";
    final CommentTagProcessor commentProc = new CommentTagProcessor("composite_test.Composite1", comment, SourceKind.COMPONENT);
    final String result = commentProc.replaceTagsInComment();
    assertEquals(result, "This is a comment with a link to some other <a href=\"../AComponent.ADL.html#CLIENT_ITF_SUM\">interfaces</a>");
    //System.out.println("Original: \"" + comment + "\"");
    //System.out.println("Result:   \"" + result  + "\"");
   }

  @Test
  public void testReplacementDefinitionWithPackage() throws ADLException {
    final String comment = "@component package.name.AComponent. This is a comment with a link.";
    final CommentTagProcessor commentProc = new CommentTagProcessor("composite_test.Composite1", comment, SourceKind.COMPONENT);
    final String result = commentProc.replaceTagsInComment();
    assertEquals(result, "<a href=\"../package/name/AComponent.ADL.html\" title=\"package.name.AComponent\">AComponent</a>. This is a comment with a link.");
    //System.out.println("Original: \"" + comment + "\"");
    //System.out.println("Result:   \"" + result  + "\"");
  }

  @Test
  public void testReplacementDefinition() throws ADLException {
    final String comment = "@component AComponent. This is a comment with a link.";
    final CommentTagProcessor commentProc = new CommentTagProcessor("composite_test.Composite1", comment, SourceKind.COMPONENT);
    final String result = commentProc.replaceTagsInComment();
    assertEquals(result, "<a href=\"../AComponent.ADL.html\" title=\"AComponent\">AComponent</a>. This is a comment with a link.");
    //System.out.println("Original: \"" + comment + "\"");
    //System.out.println("Result:   \"" + result  + "\"");
  }

  @Test
  public void testLinksInComments() throws ADLException, InterruptedException {
    compileADL("link_test.Composite");
    compileADL("common.PrimitiveServer");
    compileADL("common.PrimitiveClient");
  }

  @Test
  public void testShortComments() throws Exception {
    final String comment = "This is a comment @component package.Component. More details";
    final CommentTagProcessor commentProc = new CommentTagProcessor("composite_test.Composite1", comment, SourceKind.COMPONENT);
    assertEquals(commentProc.getEndOfFirstSentence(), 46);
    final String result = commentProc.replaceTagsInShortComment();
    assertEquals(result, "This is a comment <a href=\"../package/Component.ADL.html\" title=\"package.Component\">Component</a>.");
    //System.out.println("Original: \"" + comment + "\"");
    //System.out.println("Result:   \"" + result  + "\"");
  }

  @Test
  public void testShortComments2() throws Exception {
    final String comment = "This is a comment @component package.Component More details";
    final CommentTagProcessor commentProc = new CommentTagProcessor("composite_test.Composite1", comment, SourceKind.COMPONENT);
    assertEquals(commentProc.getEndOfFirstSentence(), -1);
    final String result = commentProc.replaceTagsInShortComment();
    //System.out.println("Original: \"" + comment + "\"");
    //System.out.println("Result:   \"" + result  + "\"");
    assertEquals(result, "This is a comment <a href=\"../package/Component.ADL.html\" title=\"package.Component\">Component</a> More details");
  }

  @Test
  public void testShortComments3() throws Exception {
    final String comment = "This is a comment @component package.Component bla bla@component package.Component bla. More details";
    final CommentTagProcessor commentProc = new CommentTagProcessor("composite_test.Composite1", comment, SourceKind.COMPONENT);
    assertEquals(commentProc.getEndOfFirstSentence(), 86);
    final String result = commentProc.replaceTagsInShortComment();
    //System.out.println("Original: \"" + comment + "\"");
    //System.out.println("Result:   \"" + result  + "\"");
    assertEquals(result, "This is a comment <a href=\"../package/Component.ADL.html\" title=\"package.Component\">Component</a> bla bla<a href=\"../package/Component.ADL.html\" title=\"package.Component\">Component</a> bla.");
  }

  @Test
  public void testShortComments4() throws Exception {
    final String comment = "This is a comment. More details.";
    final CommentTagProcessor commentProc = new CommentTagProcessor("composite_test.Composite1", comment, SourceKind.COMPONENT);
    assertEquals(commentProc.getEndOfFirstSentence(), 17);
    final String result = commentProc.replaceTagsInShortComment();
    //System.out.println("Original: \"" + comment + "\"");
    //System.out.println("Result:   \"" + result  + "\"");
    assertEquals(result, "This is a comment.");
  }

}
