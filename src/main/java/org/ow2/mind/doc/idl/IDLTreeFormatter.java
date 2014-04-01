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
 * Authors: Ali-Erdem Ozcan, Michel Metzger, Matthieu Leclercq
 * Contributors:
 */

package org.ow2.mind.doc.idl;

import java.util.Enumeration;
import java.util.Vector;

import org.ow2.mind.doc.HTMLDocumentationHelper;
import org.ow2.mind.idl.jtb.syntaxtree.EnumDefinition;
import org.ow2.mind.idl.jtb.syntaxtree.EnumMember;
import org.ow2.mind.idl.jtb.syntaxtree.EnumMemberList;
import org.ow2.mind.idl.jtb.syntaxtree.EnumReference;
import org.ow2.mind.idl.jtb.syntaxtree.EnumValue;
import org.ow2.mind.idl.jtb.syntaxtree.Node;
import org.ow2.mind.idl.jtb.syntaxtree.NodeSequence;
import org.ow2.mind.idl.jtb.syntaxtree.NodeToken;
import org.ow2.mind.idl.jtb.syntaxtree.QualifiedTypeSpecification;
import org.ow2.mind.idl.jtb.syntaxtree.StructMember;
import org.ow2.mind.idl.jtb.syntaxtree.StructMemberList;
import org.ow2.mind.idl.jtb.syntaxtree.StructOrUnion;
import org.ow2.mind.idl.jtb.syntaxtree.StructOrUnionDefinition;
import org.ow2.mind.idl.jtb.syntaxtree.StructOrUnionReference;
import org.ow2.mind.idl.jtb.syntaxtree.TypeDefName;
import org.ow2.mind.idl.jtb.syntaxtree.TypeDefSpecification;
import org.ow2.mind.idl.jtb.visitor.TreeFormatter;


public class IDLTreeFormatter extends TreeFormatter {
  public IDLTreeFormatter() {
  }

  @Override
  public void visit(final TypeDefSpecification n) {
    clearSpecialTokens(n.f0);
    n.f0.accept(this);
    add(space());
    n.f1.accept(this);
    add(space());
    n.f2.accept(this);
  }

  @Override
  public void visit(final TypeDefName n) {
    final String name = n.f0.f0.tokenImage;
    n.f0.f0 = new NodeToken(String.format("<a href=\"#%1$s\" title=\"struct %2$s\">%2$s</a>", HTMLDocumentationHelper.getTypedefAnchor(name), name));
    n.f0.accept(this);
  }

  @Override
  public void visit(final QualifiedTypeSpecification n) {
    if (n.f0.present()) {
      processList(n.f0);
      add(space());
    }
    n.f1.accept(this);
  }

  @Override
  public void visit(final StructOrUnionDefinition n) {
    n.f0.accept(this);
    add(space());
    if (n.f1.present()) {
      n.f1.accept(this);
      add(space());
    }
    n.f2.accept(this);
    add(indent());
    add(force());
    n.f3.accept(this);
    add(outdent());
    add(force());
    n.f4.accept(this);
  }

  @Override
  public void visit(final StructOrUnion n) {
    final NodeToken nodeToken = (NodeToken)n.f0.choice;
    clearSpecialTokens(nodeToken);
    super.visit(n);
  }

  private void clearSpecialTokens(final NodeToken nodeToken) {
    final Vector<NodeToken> specialTokens = nodeToken.specialTokens;
    if(specialTokens != null)
      specialTokens.clear();
  }

  @Override
  public void visit(final StructMemberList n) {
    if (n.f0.present()) {
      processList(n.f0, force());
    }
  }

  @Override
  public void visit(final StructMember n) {
    //n.f0.accept(this);
    n.f1.accept(this);
    add(space());
    n.f2.accept(this);
    if (n.f3.present()) {
      n.f3.accept(this);
    }
    n.f4.accept(this);
  }

  @Override
  public void visit(final StructOrUnionReference n) {
    n.f0.accept(this);
    final String structOrUnion = ((NodeToken)n.f0.f0.choice).tokenImage;

    final String name = n.f1.tokenImage;
    if("struct".equals(structOrUnion)) {
      n.f1 = new NodeToken(String.format("<a href=\"#%1$s\" title=\"struct %2$s\">%2$s</a>", HTMLDocumentationHelper.getStructAnchor(name), name));
    } else {
      n.f1 = new NodeToken(String.format("<a href=\"#%1$s\" title=\"union %2$s\">%2$s</a>", HTMLDocumentationHelper.getStructAnchor(name), name));
    }
    add(space());
    n.f1.accept(this);
  }

  @Override
  public void visit(final EnumDefinition n) {
    clearSpecialTokens(n.f0);
    n.f0.accept(this);
    add(space());
    if (n.f1.present()) {
      n.f1.accept(this);
      add(space());
    }
    n.f2.accept(this);
    add(indent());
    add(force());
    n.f3.accept(this);
    add(outdent());
    add(force());
    n.f4.accept(this);
  }

  @Override
  public void visit(final EnumMemberList n) {
    n.f0.accept(this);
    if (n.f1.present()) {
      for (final Enumeration<Node> e = n.f1.elements(); e.hasMoreElements();) {
        final NodeSequence seq = (NodeSequence) e.nextElement();
        seq.nodes.elementAt(0).accept(this); // comma
        add(force());
        seq.nodes.elementAt(1).accept(this); // enumMember
      }

    }
  }

  @Override
  public void visit(final EnumMember n) {
    //n.f0.accept(this);
    n.f1.accept(this);
    if (n.f2.present()) {
      add(space());
      n.f2.accept(this);
    }
  }

  @Override
  public void visit(final EnumValue n) {
    n.f0.accept(this);
    add(space());
    n.f1.accept(this);
  }

  @Override
  public void visit(final EnumReference n) {
    n.f0.accept(this);
    add(space());
    final String name = n.f1.tokenImage;
    n.f1 = new NodeToken(String.format("<a href=\"#%1$s\" title=\"struct %2$s\">%2$s</a>", HTMLDocumentationHelper.getEnumAnchor(name), name));
    n.f1.accept(this);
  }
}
