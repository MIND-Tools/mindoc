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

package org.ow2.mind.doc.adl;

import org.objectweb.fractal.adl.xml.XMLNodeFactory;
import org.objectweb.fractal.adl.xml.XMLNodeFactoryImpl;
import org.ow2.mind.BasicInputResourceLocator;
import org.ow2.mind.InputResourceLocator;
import org.ow2.mind.adl.BasicDefinitionCompiler;
import org.ow2.mind.adl.DefinitionCompiler;
import org.ow2.mind.adl.DefinitionSourceGenerator;
import org.ow2.mind.adl.implementation.BasicImplementationLocator;
import org.ow2.mind.adl.implementation.ImplementationLocator;
import org.ow2.mind.compilation.CompilerWrapper;
import org.ow2.mind.compilation.gcc.GccCompilerWrapper;
import org.ow2.mind.doc.idl.IDLBackendFactory;
import org.ow2.mind.idl.IDLLoader;
import org.ow2.mind.idl.IDLLoaderChainFactory;
import org.ow2.mind.idl.IDLVisitor;
import org.ow2.mind.io.BasicOutputFileLocator;
import org.ow2.mind.io.OutputFileLocator;
import org.ow2.mind.preproc.BasicMPPWrapper;
import org.ow2.mind.preproc.MPPWrapper;
import org.ow2.mind.st.StringTemplateComponentLoader;
import org.ow2.mind.st.templates.parser.StringTemplateLoader;


public final class DocumentationBackendFactory {
  private DocumentationBackendFactory() {
  }

  public static final DefinitionSourceGenerator newDefinitionSourceGenerator(
      final InputResourceLocator inputResourceLocator,
      final OutputFileLocator outputFileLocator, final IDLLoader idlLoader,
      final IDLVisitor idlCompiler,
      final StringTemplateComponentLoader stcLoader) {

    DefinitionSourceGenerator definitionSourceGenerator;
    final HTMLDocumentGenerator hdg = new HTMLDocumentGenerator();

    definitionSourceGenerator = hdg;

    hdg.inputResourceLocatorItf = inputResourceLocator;
    hdg.outputFileLocatorItf = outputFileLocator;

    // Life-cycle control
    hdg.templateGroupLoaderItf = stcLoader;

    return definitionSourceGenerator;
  }

  public static DefinitionCompiler newDefinitionCompiler(
      final DefinitionSourceGenerator definitionSourceGenerator,
      final ImplementationLocator implementationLocator,
      final OutputFileLocator outputFileLocator,
      final CompilerWrapper compilerWrapper, final MPPWrapper mppWrapper) {

    DefinitionCompiler definitionCompiler;
    final BasicDefinitionCompiler bdc = new BasicDefinitionCompiler();

    definitionCompiler = bdc;
    bdc.definitionSourceGeneratorItf = definitionSourceGenerator;
    bdc.implementationLocatorItf = implementationLocator;
    bdc.outputFileLocatorItf = outputFileLocator;
    bdc.compilerWrapperItf = compilerWrapper;
    bdc.mppWrapperItf = mppWrapper;

    return definitionCompiler;
  }

  public static DefinitionCompiler newDefinitionCompiler() {
    final IDLLoader idlLoader = IDLLoaderChainFactory.newLoader();
    final BasicInputResourceLocator inputResourceLocator = new BasicInputResourceLocator();
    final BasicOutputFileLocator outputFileLocator = new BasicOutputFileLocator();
    final ImplementationLocator implementationLocator = new BasicImplementationLocator();
    final StringTemplateComponentLoader stcLoader = new StringTemplateComponentLoader();
    final StringTemplateLoader templateLoader = new StringTemplateLoader();
    final XMLNodeFactory nodeFactory = new XMLNodeFactoryImpl();

    templateLoader.nodeFactoryItf = nodeFactory;
    stcLoader.loaderItf = templateLoader;

    final IDLVisitor idlCompiler = IDLBackendFactory.newIDLCompiler(idlLoader,
        inputResourceLocator, outputFileLocator, stcLoader);
    final DefinitionSourceGenerator definitionSourceGenerator = newDefinitionSourceGenerator(
        inputResourceLocator, outputFileLocator, idlLoader, idlCompiler,
        stcLoader);
    final CompilerWrapper compilerWrapper = new GccCompilerWrapper();
    final MPPWrapper mppWrapper = new BasicMPPWrapper();
    return newDefinitionCompiler(definitionSourceGenerator,
        implementationLocator, outputFileLocator, compilerWrapper, mppWrapper);
  }

}
