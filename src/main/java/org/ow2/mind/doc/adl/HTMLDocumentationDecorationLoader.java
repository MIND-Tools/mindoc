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

package org.ow2.mind.doc.adl;

import java.util.ArrayList;
import java.util.Map;

import org.objectweb.fractal.adl.ADLException;
import org.objectweb.fractal.adl.AbstractLoader;
import org.objectweb.fractal.adl.Definition;
import org.ow2.mind.adl.DefinitionReferenceResolver;
import org.ow2.mind.adl.ast.Component;
import org.ow2.mind.adl.ast.ComponentContainer;
import org.ow2.mind.adl.ast.DefinitionReference;
import org.ow2.mind.adl.ast.MindDefinition;
import org.ow2.mind.adl.parameter.ast.Argument;
import org.ow2.mind.adl.parameter.ast.ArgumentContainer;
import org.ow2.mind.doc.HTMLDocumentationHelper;


public class HTMLDocumentationDecorationLoader extends AbstractLoader {

  /** The interface used to resolve referenced definitions. */
  public DefinitionReferenceResolver  definitionReferenceResolverItf;

  public Definition load(final String name, final Map<Object, Object> context)
      throws ADLException {
    Definition d = clientLoader.load(name, context);
    if(d instanceof MindDefinition) {
      d = addDecorations((MindDefinition) d, context);
      d = addArgumentDecorations((MindDefinition) d);
    }
    return d;
  }

  private MindDefinition addDecorations(final MindDefinition d, final Map<Object, Object> context) throws ADLException {
    if (d.getExtends() == null) return d;

    final ExtendsDecoration list = new ExtendsDecoration();

    for (final DefinitionReference extend : d.getExtends().getDefinitionReferences()) {
      list.add(extend);
    }

    d.astSetDecoration("extends", list);

    return d;
  }

  private MindDefinition addArgumentDecorations(final MindDefinition d) {
    if (d instanceof ComponentContainer) {

      for (final Component component : ((ComponentContainer) d).getComponents()) {
        if (component.getDefinitionReference() instanceof ArgumentContainer) {
          final ArrayList<String> argList = new ArrayList<String>();
          final ArgumentContainer argContainer = (ArgumentContainer) component.getDefinitionReference();
          for (final Argument arg : argContainer.getArguments()) {
            argList.add(HTMLDocumentationHelper.getValueString(arg.getValue()));
          }
          component.getDefinitionReference().astSetDecoration("arguments", argList);
        }
      }
    }
    return d;
  }
}
