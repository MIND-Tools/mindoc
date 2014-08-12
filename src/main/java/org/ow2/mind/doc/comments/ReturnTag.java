/**
 * Copyright (C) 2014 Schneider-Electric
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
 * Authors: Stephane Seyvoz (sseyvoz@assystem.com)
 * Contributors:
 */

package org.ow2.mind.doc.comments;

import org.objectweb.fractal.adl.Node;
import org.ow2.mind.doc.HTMLDocumentationHelper.SourceKind;
import org.ow2.mind.idl.ast.Method;
import org.ow2.mind.idl.ast.PrimitiveType;
import org.ow2.mind.idl.ast.Type;

public class ReturnTag extends CommentTag {

  public ReturnTag(final Node n, final String returnDesc, final int beginIndex, final int endIndex) {
    super(beginIndex, endIndex);

    // in case of IDL node, but could also be @param on ADLs
    if (n instanceof Method)
      processMethodNode((Method) n, returnDesc);
  }

  @Override
  public String getReplacement(final String definitionName, final SourceKind sourceKind) {
    return ""; // We don't want the comment to be shown in the result.
  }

  public void processMethodNode(final Method method, final String returnDesc) {
    // getType is return type
    final Type type = method.getType();

    // like Javadoc, we omit @return for methods that return void.
    if (type instanceof PrimitiveType && ((PrimitiveType) type).getName().equals("void"))
      return;

    type.astSetDecoration("description", returnDesc);
  }

}
