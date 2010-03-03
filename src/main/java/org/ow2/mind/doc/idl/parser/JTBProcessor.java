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

package org.ow2.mind.doc.idl.parser;

import static org.objectweb.fractal.adl.NodeUtil.castNodeError;

import java.util.List;

import org.objectweb.fractal.adl.Node;
import org.objectweb.fractal.adl.xml.XMLNodeFactory;
import org.ow2.mind.doc.adl.parser.CommentProcessor;
import org.ow2.mind.doc.ast.CommentDecoration;
import org.ow2.mind.idl.ast.Type;
import org.ow2.mind.idl.ast.TypeCollectionContainer;
import org.ow2.mind.idl.jtb.syntaxtree.Annotation;
import org.ow2.mind.idl.jtb.syntaxtree.Annotations;
import org.ow2.mind.idl.jtb.syntaxtree.EnumDefinition;
import org.ow2.mind.idl.jtb.syntaxtree.EnumReference;
import org.ow2.mind.idl.jtb.syntaxtree.EnumSpecification;
import org.ow2.mind.idl.jtb.syntaxtree.InterfaceDefinition;
import org.ow2.mind.idl.jtb.syntaxtree.MethodDefinition;
import org.ow2.mind.idl.jtb.syntaxtree.NodeChoice;
import org.ow2.mind.idl.jtb.syntaxtree.NodeToken;
import org.ow2.mind.idl.jtb.syntaxtree.StructOrUnionDefinition;
import org.ow2.mind.idl.jtb.syntaxtree.StructOrUnionReference;
import org.ow2.mind.idl.jtb.syntaxtree.StructOrUnionSpecification;
import org.ow2.mind.idl.jtb.syntaxtree.TypeDefName;
import org.ow2.mind.idl.jtb.syntaxtree.TypeDefSpecification;
import org.ow2.mind.idl.jtb.syntaxtree.TypeDefinition;
import org.ow2.mind.idl.jtb.syntaxtree.TypeQualifier;
import org.ow2.mind.idl.jtb.syntaxtree.TypeSpecifiers;


public class JTBProcessor
    extends
      org.ow2.mind.idl.parser.JTBProcessor {

  public JTBProcessor(final XMLNodeFactory nodeFactory, final String idlDtd,
      final String filename) {
    super(nodeFactory, idlDtd, filename);
  }

  /**
   * Gets the comment from a NodeToken.
   *
   * @param token
   * @return null if there is no comment associated to this token.
   */
  private String getComment(final NodeToken token) {
    if (token.specialTokens != null) {
      return CommentProcessor.processComment(token.getSpecialAt(0).tokenImage);
    }
    return null;
  }

  private NodeToken getCommentFromAnnotation(final Annotations annotation) {
    NodeToken commentToken = null;
    if(annotation.f0.present()) {
      commentToken = ((Annotation)annotation.f0.elementAt(0)).f0;
    }
    return commentToken;
  }

  @Override
  public Object visit(final InterfaceDefinition n, final Node argu) {
    final Node result = (Node) super.visit(n, argu);
    NodeToken commentToken = getCommentFromAnnotation(n.f0);
    if (commentToken == null){
      commentToken = n.f1;
    }
    CommentDecoration.setComment(result, getComment(commentToken));
    return result;
  }

  @Override
  public Object visit(final MethodDefinition n, final Node argu) {
    final Node result = (Node) super.visit(n, argu);

    NodeToken commentToken = getCommentFromAnnotation(n.f0);

    if(commentToken == null) {
      if(n.f1.f0.present()) { //type qualifier
        commentToken = (NodeToken) ((TypeQualifier) n.f1.f0.elementAt(0)).f0.choice;
      } else {
        final org.ow2.mind.idl.jtb.syntaxtree.Node typeSpec = n.f1.f1.f0.choice;
        if(typeSpec instanceof TypeDefName) { //typedef name
          commentToken = ((TypeDefName) typeSpec).f0.f0;
        } else if (typeSpec instanceof StructOrUnionSpecification) { //struct or union specification
          final StructOrUnionSpecification structOrUnion = (StructOrUnionSpecification) typeSpec;
          if (structOrUnion.f0.choice instanceof StructOrUnionDefinition) { //struct or union definition
            commentToken = (NodeToken) ((StructOrUnionDefinition)structOrUnion.f0.choice).f0.f0.choice;
          } else { //struct or union reference
            commentToken = (NodeToken) ((StructOrUnionReference)structOrUnion.f0.choice).f0.f0.choice;
          }
        } else if(typeSpec instanceof EnumSpecification) { //Enum specification
          final EnumSpecification enumSpec = (EnumSpecification) typeSpec;
          if(enumSpec.f0.choice instanceof EnumDefinition) { //enum definition
            commentToken = ((EnumDefinition)enumSpec.f0.choice).f0;
          } else { //enum reference
            commentToken = ((EnumReference)enumSpec.f0.choice).f0;
          }
        } else { //type specifier
          commentToken = (NodeToken) ((NodeChoice)((TypeSpecifiers)typeSpec).f0.elementAt(0)).choice;
        }
      }
    }
    CommentDecoration.setComment(result, getComment(commentToken));
    return result;
  }

  @Override
  public Object visit(final TypeDefinition n, final Node argu) {
    assert argu != null;
    final TypeCollectionContainer container = castNodeError(argu,
        TypeCollectionContainer.class);

    final Object def = n.f0.accept(this, argu);

    if (def instanceof List<?>) {
      for (final Object d : (List<?>) def) {
        assert d instanceof Type;
        final Type type = (Type) d;
        container.addType(type);
        addTypeDefinitionComment(n, type);
        type.astSetDecoration("syntax-tree", n);
      }
    } else {
      assert def instanceof Type;
      final Type type = (Type) def;
      container.addType(type);
      addTypeDefinitionComment(n, type);
      type.astSetDecoration("syntax-tree", n);
    }
    return null;
  }

  private void addTypeDefinitionComment(final TypeDefinition n, final Type type) {
    final org.ow2.mind.idl.jtb.syntaxtree.Node typeDefinition = n.f0.choice;

    final NodeToken commentToken;
    if(typeDefinition instanceof TypeDefSpecification) { //typedef name
      commentToken = ((TypeDefSpecification) typeDefinition).f0;
    } else if (typeDefinition instanceof StructOrUnionSpecification) { //struct or union specification
      final StructOrUnionSpecification structOrUnion = (StructOrUnionSpecification) typeDefinition;
      if (structOrUnion.f0.choice instanceof StructOrUnionDefinition) { //struct or union definition
        commentToken = (NodeToken) ((StructOrUnionDefinition)structOrUnion.f0.choice).f0.f0.choice;
      } else { //struct or union reference
        commentToken = (NodeToken) ((StructOrUnionReference)structOrUnion.f0.choice).f0.f0.choice;
      }
    } else {//Enum specification
      final EnumSpecification enumSpec = (EnumSpecification) typeDefinition;
      if(enumSpec.f0.choice instanceof EnumDefinition) { //enum definition
        commentToken = ((EnumDefinition)enumSpec.f0.choice).f0;
      } else { //enum reference
        commentToken = ((EnumReference)enumSpec.f0.choice).f0;
      }
    }
    CommentDecoration.setComment(type, getComment(commentToken));
  }
}
