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

import java.util.logging.Logger;

import org.objectweb.fractal.adl.Node;
import org.ow2.mind.doc.HTMLDocumentationHelper.SourceKind;
import org.ow2.mind.idl.ast.Method;
import org.ow2.mind.idl.ast.Parameter;

public class ParamTag extends CommentTag {

  public static final Logger  logger                  = Logger.getAnonymousLogger();

  /**
   *
   * @param n Keeping the node allows us to decorate parameter nodes for StringTemplates.
   * @param paramName
   * @param paramDesc
   * @param definitionName
   * @param beginIndex
   * @param endIndex
   */
  public ParamTag(final Node n, final String definitionName, final String paramName, final String paramDesc, final int beginIndex, final int endIndex) {
    super(beginIndex, endIndex);

    boolean valid = false;

    // in case of IDL node, but could also be @param on ADLs
    if (n instanceof Method)
      valid = processMethodNode((Method) n, definitionName, paramName, paramDesc);

    // allow creating the Parameters paragraph/div if everything is ok & not already done
    if (valid && (n.astGetDecoration("gen-params") == null))
      n.astSetDecoration("gen-params", new Boolean(true));
  }

  @Override
  public String getReplacement(final String definitionName, final SourceKind sourceKind) {
    return ""; // We don't want the comment to be shown in the result.
  }

  public boolean processMethodNode(final Method method, final String definitionName, final String paramName, final String paramDesc) {
    boolean result = false;

    for (final Parameter currParam : method.getParameters()) {
      if (currParam.getName().equals(paramName)) {
        currParam.astSetDecoration("description", paramDesc);
        result = true;
        break;
      }
    }

    if (!result)
      logger.warning("In interface " + definitionName + ", @param referenced parameter '" + paramName + "' of method '" + method.getName() + "' doesn't exist - Skip");

    return result;
  }

}
