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
