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
import org.ow2.mind.doc.comments.LinkInterfaceTag;
import org.ow2.mind.doc.comments.LinkTag.InterfaceLinkElementType;
import org.testng.annotations.Test;



public class CommentProcessorInterfaceTagTest extends AbstractDocumentationGeneratorTest {

  public CommentProcessorInterfaceTagTest() throws Exception {
    super();
  }

  @Test
  public void testLinkThis() {
    final String comment = "This is a comment with a link. @interface this#methods#toto";
    final List<CommentTag> tags = extractTags(comment);

    assertEquals(tags.size(), 1, "Invalid results: 1 tag should have been extracted, actual result is " + tags.size());
    assertTrue(tags.get(0) instanceof LinkInterfaceTag);
    final LinkInterfaceTag tag = (LinkInterfaceTag) tags.get(0);
    assertEquals(tag.getTarget(), "this");
    assertEquals(tag.getSubElemType(), InterfaceLinkElementType.METHODS);
    assertEquals(tag.getSubElemName(), "toto");
  }

  @Test
  public void testLinkOther() {
    final String comment = "This is a comment with a link. @interface AnInterface#typedefs#tutu";
    final List<CommentTag> tags = extractTags(comment);

    assertEquals(tags.size(), 1, "Invalid results: 1 tag should have been extracted, actual result is " + tags.size());
    assertTrue(tags.get(0) instanceof LinkInterfaceTag);
    final LinkInterfaceTag tag = (LinkInterfaceTag) tags.get(0);
    assertEquals(tag.getTarget(), "AnInterface");
    assertEquals(tag.getSubElemType(), InterfaceLinkElementType.TYPEDEFS);
    assertEquals(tag.getSubElemName(), "tutu");
  }

  @Test
  public void testLinkOtherPackage() {
    final String comment = "This is a comment with a link. @interface a.long.package.name.AnInterface#structs#toto";
    final List<CommentTag> tags = extractTags(comment);

    assertEquals(tags.size(), 1, "Invalid results: 1 tag should have been extracted, actual result is " + tags.size());
    assertTrue(tags.get(0) instanceof LinkInterfaceTag);
    final LinkInterfaceTag tag = (LinkInterfaceTag) tags.get(0);
    assertEquals(tag.getTarget(), "a.long.package.name.AnInterface");
    assertEquals(tag.getSubElemType(), InterfaceLinkElementType.STRUCTS);
    assertEquals(tag.getSubElemName(), "toto");
  }

  @Test
  public void testLinkDefinition() {
    final String comment = "This is a comment with a link. @interface AnInterface";
    final List<CommentTag> tags = extractTags(comment);

    assertEquals(tags.size(), 1, "Invalid results: 1 tag should have been extracted, actual result is " + tags.size());
    assertTrue(tags.get(0) instanceof LinkInterfaceTag);
    final LinkInterfaceTag tag = (LinkInterfaceTag) tags.get(0);
    assertEquals(tag.getTarget(), "AnInterface");
    assertNull(tag.getSubElemType());
    assertNull(tag.getSubElemName());
  }

  @Test
  public void testLinkDefinitionWithPackage() {
    final String comment = "@interface package.name.AnInterface. This is a comment with a link. ";
    final List<CommentTag> tags = extractTags(comment);

    assertEquals(tags.size(), 1, "Invalid results: 1 tag should have been extracted, actual result is " + tags.size());
    assertTrue(tags.get(0) instanceof LinkInterfaceTag);
    final LinkInterfaceTag tag = (LinkInterfaceTag) tags.get(0);
    assertEquals(tag.getTarget(), "package.name.AnInterface");
    assertNull(tag.getSubElemType());
    assertNull(tag.getSubElemName());
  }

  @Test
  public void testLinkSectionThis() {
    final String comment = "@interface this#methods.";
    final List<CommentTag> tags = extractTags(comment);

    assertEquals(tags.size(), 1, "Invalid results: 1 tag should have been extracted, actual result is " + tags.size());
    assertTrue(tags.get(0) instanceof LinkInterfaceTag);
    final LinkInterfaceTag tag = (LinkInterfaceTag) tags.get(0);
    assertEquals(tag.getTarget(), "this");
    assertEquals(tag.getSubElemType(), InterfaceLinkElementType.METHODS);
    assertNull(tag.getSubElemName());
  }

  @Test
  public void testLinkSectionOther() {
    final String comment = "@interface package.Interface#types.";
    final List<CommentTag> tags = extractTags(comment);

    assertEquals(tags.size(), 1, "Invalid results: 1 tag should have been extracted, actual result is " + tags.size());
    assertTrue(tags.get(0) instanceof LinkInterfaceTag);
    final LinkInterfaceTag tag = (LinkInterfaceTag) tags.get(0);
    assertEquals(tag.getTarget(), "package.Interface");
    assertEquals(tag.getSubElemType(), InterfaceLinkElementType.TYPES);
    assertNull(tag.getSubElemName());
  }

  @Test
  public void testLinkSectionSyntaxErrorDetected() {
    final String comment = "@interface package.Interface#types#toto.";
    final  List<CommentTag> tags = extractTags(comment);

    assertEquals(tags.size(), 0, "Invalid results: no tag should have been extracted, actual result is " + tags.size());
  }

  @Test
  public void testLinkSectionSyntaxErrorDetected2() {
    final String comment = "@interface package.Interface#typedefs.";
    final  List<CommentTag> tags = extractTags(comment);
    assertEquals(tags.size(), 0, "Invalid results: no tag should have been extracted, actual result is " + tags.size());
  }


  @Test
  public void testReplacementUnion() throws ADLException {
    final String comment = "This is a comment with a link. @interface this#unions#toto";
    final CommentTagProcessor commentProc = new CommentTagProcessor("interface_test.Interface1", comment, SourceKind.INTERFACE);
    final String result = commentProc.replaceTagsInComment();
    //System.out.println("Original: \"" + comment + "\"");
    //System.out.println("Result:   \"" + result  + "\"");
    assertEquals(result, "This is a comment with a link. <a href=\"#UNION_toto\" title=\"union interface_test.Interface1.toto\">Interface1.toto</a>");
  }

  @Test
  public void testReplacementEnum() throws ADLException {
    final String comment = "This is a comment with a link. @interface package.name.AnInterface#enums#toto";
    final CommentTagProcessor commentProc = new CommentTagProcessor("interface_test.Interface1", comment, SourceKind.INTERFACE);
    final String result = commentProc.replaceTagsInComment();
    //System.out.println("Original: \"" + comment + "\"");
    //System.out.println("Result:   \"" + result  + "\"");
    assertEquals(result, "This is a comment with a link. <a href=\"../package/name/AnInterface.ITF.html#ENUM_toto\" title=\"enum package.name.AnInterface.toto\">AnInterface.toto</a>");
  }

  @Test
  public void testReplacementMethod() throws ADLException {
    final String comment = "This is a comment with a link. @interface AnInterface#methods#tutu";
    final CommentTagProcessor commentProc = new CommentTagProcessor("interface_test.Interface1", comment, SourceKind.INTERFACE);
    final String result = commentProc.replaceTagsInComment();
    //System.out.println("Original: \"" + comment + "\"");
    //System.out.println("Result:   \"" + result  + "\"");
    assertEquals(result, "This is a comment with a link. <a href=\"../AnInterface.ITF.html#METH_tutu\" title=\"method AnInterface.tutu\">AnInterface.tutu</a>");
  }

  @Test
  public void testReplacementTypedef() throws ADLException {
    final String comment = "This is a comment with a link. @interface AnInterface#typedefs#tutu";
    final CommentTagProcessor commentProc = new CommentTagProcessor("interface_test.Interface1", comment, SourceKind.INTERFACE);
    final String result = commentProc.replaceTagsInComment();
    //System.out.println("Original: \"" + comment + "\"");
    //System.out.println("Result:   \"" + result  + "\"");
    assertEquals(result, "This is a comment with a link. <a href=\"../AnInterface.ITF.html#TYPEDEF_tutu\" title=\"typedef AnInterface.tutu\">AnInterface.tutu</a>");
  }

  @Test
  public void testReplacementStruct() throws ADLException {
    final String comment = "This is a comment with a link. @interface AnInterface#structs#tutu";
    final CommentTagProcessor commentProc = new CommentTagProcessor("interface_test.Interface1", comment, SourceKind.INTERFACE);
    final String result = commentProc.replaceTagsInComment();
    //System.out.println("Original: \"" + comment + "\"");
    //System.out.println("Result:   \"" + result  + "\"");
    assertEquals(result, "This is a comment with a link. <a href=\"../AnInterface.ITF.html#STRUCT_tutu\" title=\"struct AnInterface.tutu\">AnInterface.tutu</a>");
  }

  @Test
  public void testReplacementDefinitionWithPackage() throws ADLException {
    final String comment = "@interface package.name.AnInterface. This is a comment with a link.";
    final CommentTagProcessor commentProc = new CommentTagProcessor("interface_test.Interface1", comment, SourceKind.INTERFACE);
    final String result = commentProc.replaceTagsInComment();
    //System.out.println("Original: \"" + comment + "\"");
    //System.out.println("Result:   \"" + result  + "\"");
    assertEquals(result, "<a href=\"../package/name/AnInterface.ITF.html\" title=\"package.name.AnInterface\">AnInterface</a>. This is a comment with a link.");
  }

  @Test
  public void testReplacementDefinition() throws ADLException {
    final String comment = "@interface AnInterface. This is a comment with a link.";
    final CommentTagProcessor commentProc = new CommentTagProcessor("interface_test.Interface1", comment, SourceKind.INTERFACE);
    final String result = commentProc.replaceTagsInComment();
    //System.out.println("Original: \"" + comment + "\"");
    //System.out.println("Result:   \"" + result  + "\"");
    assertEquals(result, "<a href=\"../AnInterface.ITF.html\" title=\"AnInterface\">AnInterface</a>. This is a comment with a link.");
  }

  @Test
  public void testReplacementSectionThis() throws ADLException {
    final String comment = "@interface this#types. This is a comment with a link.";
    final CommentTagProcessor commentProc = new CommentTagProcessor("interface_test.Interface1", comment, SourceKind.INTERFACE);
    final String result = commentProc.replaceTagsInComment();
    //System.out.println("Original: \"" + comment + "\"");
    //System.out.println("Result:   \"" + result  + "\"");
    assertEquals(result, "<a href=\"#TYPES_SUM\">types</a>. This is a comment with a link.");
  }

  @Test
  public void testReplacementSectionOther() throws ADLException {
    final String comment = "@interface package.Interface#methods. This is a comment with a link.";
    final CommentTagProcessor commentProc = new CommentTagProcessor("interface_test.Interface1", comment, SourceKind.INTERFACE);
    final String result = commentProc.replaceTagsInComment();
    //System.out.println("Original: \"" + comment + "\"");
    //System.out.println("Result:   \"" + result  + "\"");
    assertEquals(result, "<a href=\"../package/Interface.ITF.html#METHODS_SUM\">methods</a>. This is a comment with a link.");
  }

  @Test
  public void testLinksInComments() throws ADLException, InterruptedException {
    compileIDL("link_test.MyInterface");
    compileIDL("interfaces.TestInterface");
    compileIDL("Main");
  }

  @Test
  public void testNameAmbiguity() throws Exception {
    final String comment = "@interface patterns.communication.Exchanger#methods#exchange";
    final CommentTagProcessor commentProc = new CommentTagProcessor("patterns.communication.Exchanger", comment, SourceKind.COMPONENT);
    final String result = commentProc.replaceTagsInComment();
    //System.out.println("Original: \"" + comment + "\"");
    //System.out.println("Result:   \"" + result  + "\"");
    assertEquals(result, "<a href=\"../../patterns/communication/Exchanger.ITF.html#METH_exchange\" title=\"method patterns.communication.Exchanger.exchange\">Exchanger.exchange</a>");
  }
}
