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
