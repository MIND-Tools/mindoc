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
 * Contact: mind-members@lists.minalogic.net
 *
 * Authors: Matthieu Leclercq
 * Contributors:
 */

package org.ow2.mind.doc.idl;

import org.objectweb.fractal.adl.xml.XMLNodeFactory;
import org.objectweb.fractal.adl.xml.XMLNodeFactoryImpl;
import org.ow2.mind.BasicInputResourceLocator;
import org.ow2.mind.InputResourceLocator;
import org.ow2.mind.idl.IDLLoader;
import org.ow2.mind.idl.IDLVisitor;
import org.ow2.mind.io.BasicOutputFileLocator;
import org.ow2.mind.io.OutputFileLocator;
import org.ow2.mind.st.StringTemplateComponentLoader;
import org.ow2.mind.st.templates.parser.StringTemplateLoader;

public final class IDLBackendFactory {
  private IDLBackendFactory() {
  }

  public static IDLVisitor newIDLCompiler() {
    final IDLLoader idlLoader = IDLLoaderChainFactory.newLoader().loader;
    final BasicInputResourceLocator inputResourceLocator = new BasicInputResourceLocator();
    final BasicOutputFileLocator outputFileLocator = new BasicOutputFileLocator();

    final StringTemplateComponentLoader stcLoader = new StringTemplateComponentLoader();
    final StringTemplateLoader templateLoader = new StringTemplateLoader();
    final XMLNodeFactory nodeFactory = new XMLNodeFactoryImpl();

    templateLoader.nodeFactoryItf = nodeFactory;
    stcLoader.loaderItf = templateLoader;

    return newIDLCompiler(idlLoader, inputResourceLocator, outputFileLocator,
        stcLoader);
  }

  public static IDLVisitor newIDLCompiler(final IDLLoader idlLoader,
      final InputResourceLocator inputResourceLocator,
      final OutputFileLocator outputFileLocator,
      final StringTemplateComponentLoader stcLoader) {
    final IDLTemplateProcessor idg = new IDLTemplateProcessor();

    idg.templateGroupLoaderItf = stcLoader;

    idg.inputResourceLocatorItf = inputResourceLocator;
    idg.outputFileLocatorItf = outputFileLocator;

    return idg;
  }
}
