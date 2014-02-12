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

package org.ow2.mind.doc.comments;

import static org.ow2.mind.doc.ast.CommentDecoration.setComment;
import static org.ow2.mind.doc.ast.CommentDecoration.setShortComment;

import java.util.Map;
import java.util.regex.Matcher;

import org.objectweb.fractal.adl.Definition;
import org.objectweb.fractal.adl.Node;
import org.ow2.mind.doc.HTMLDocumentationHelper.SourceKind;
import org.ow2.mind.doc.ast.CommentDecoration;
import org.ow2.mind.idl.ast.IDL;


public class CommentProcessor {
  private final String rootName;

  private CommentProcessor(final String rootName) {
    this.rootName = rootName;
  }

  public static void process(final Definition definition, final Map<Object, Object> context) {
    final CommentProcessor processor = new CommentProcessor(definition.getName());
    processor.internalProcess(definition, SourceKind.COMPONENT, context);
  }

  public static void process(final IDL definition, final Map<Object, Object> context) {
    final CommentProcessor processor = new CommentProcessor(definition.getName());
    processor.internalProcess(definition, SourceKind.INTERFACE, context);
  }

  private void internalProcess(final Node n, final SourceKind sourceKind, final Map<Object, Object> context) {
    final String comment = (String)n.astGetDecoration(CommentDecoration.COMMENT_DECORATION);
    if(comment != null) {
      final CommentTagProcessor tagProcessor = new CommentTagProcessor(n, rootName, comment, sourceKind);

      setShortComment(n, tagProcessor.replaceTagsInShortComment());
      setComment(n, tagProcessor.replaceTagsInComment());
    }
    for (final String nodeType : n.astGetNodeTypes()) {
      for (final Node subNode : n.astGetNodes(nodeType)) {
        if(subNode != null)
          internalProcess(subNode, sourceKind, context);
      }
    }
  }

  @SuppressWarnings("unused")
  private static void printGroups(final Matcher m) {
    System.out.print("-------- ");
    int i;
    for (i = 1; i < m.groupCount(); i++) {
      System.out.print("group#" + i + " = " + m.group(i) + ", ");
    }
    System.out.print("group#" + i + " = " + m.group(m.groupCount()));
    System.out.println();
  }
}
