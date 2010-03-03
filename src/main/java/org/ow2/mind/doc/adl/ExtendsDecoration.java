package org.ow2.mind.doc.adl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;

import org.objectweb.fractal.adl.Node;
import org.ow2.mind.NodeContainerDecoration;
import org.ow2.mind.adl.ast.DefinitionReference;

public class ExtendsDecoration implements NodeContainerDecoration {

  private final ArrayList<DefinitionReference> definitionReferences = new ArrayList<DefinitionReference>();

  public void add(final DefinitionReference reference) {
    definitionReferences.add(reference);
  }

  public Collection<Node> getNodes() {
    return new ArrayList<Node>(definitionReferences);
  }

  public void replaceNodes(final IdentityHashMap<Node, Node> replacements) {
    for (int i = 0; i < definitionReferences.size(); i++) {
      final Node ref = definitionReferences.get(i);
      if(replacements.containsKey(ref))
        definitionReferences.set(i, (DefinitionReference)replacements.get(ref));
    }
  }

  public Collection<DefinitionReference> getExtends() {
    return definitionReferences;
  }

}
