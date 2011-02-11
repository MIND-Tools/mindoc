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
 * Authors: ali-erdem.ozcan@st.com
 * Contributors:
 */

package org.ow2.mind.doc;

import java.util.Map;

import org.objectweb.fractal.adl.ADLException;
import org.objectweb.fractal.adl.Definition;
import org.objectweb.fractal.adl.Loader;
import org.ow2.mind.adl.DefinitionSourceGenerator;
import org.ow2.mind.idl.IDLLoader;
import org.ow2.mind.idl.IDLVisitor;
import org.ow2.mind.idl.ast.IDL;
import com.google.inject.Inject;

public class DefinitionDocumentGenerator {
  @Inject
  protected Loader                    adlLoader;
  @Inject
  protected DefinitionSourceGenerator adlGenerator;

  @Inject
  protected IDLLoader                 idlLoader;
  @Inject
  protected IDLVisitor                idlGenerator;

  public void generateADLDocumentation(final String adlName,
      final Map<Object, Object> context) throws ADLException,
      InterruptedException {
    final Definition d = adlLoader.load(adlName, context);
    adlGenerator.visit(d, context);
  }

  public void generateIDLDocumentation(final String idlName,
      final Map<Object, Object> context) throws ADLException,
      InterruptedException {
    final IDL idl = idlLoader.load(idlName, context);
    idlGenerator.visit(idl, context);
  }
}
