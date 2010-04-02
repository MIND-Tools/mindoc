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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.fractal.adl.ADLException;
import org.objectweb.fractal.adl.Definition;
import org.objectweb.fractal.adl.Loader;
import org.objectweb.fractal.adl.xml.XMLNodeFactory;
import org.objectweb.fractal.adl.xml.XMLNodeFactoryImpl;
import org.ow2.mind.BasicInputResourceLocator;
import org.ow2.mind.adl.ADLLocator;
import org.ow2.mind.adl.DefinitionCompiler;
import org.ow2.mind.annotation.AnnotationLocatorHelper;
import org.ow2.mind.doc.adl.DocumentationBackendFactory;
import org.ow2.mind.doc.adl.DocumentationFrontendFactory;
import org.ow2.mind.doc.idl.IDLBackendFactory;
import org.ow2.mind.doc.idl.IDLLoaderChainFactory;
import org.ow2.mind.idl.IDLLoader;
import org.ow2.mind.idl.IDLLocator;
import org.ow2.mind.idl.IDLVisitor;
import org.ow2.mind.idl.ast.IDL;
import org.ow2.mind.io.BasicOutputFileLocator;
import org.ow2.mind.plugin.SimpleClassPluginFactory;
import org.ow2.mind.st.StringTemplateComponentLoader;
import org.ow2.mind.st.templates.parser.StringTemplateLoader;


public class DefinitionDocumentGenerator {
  public Loader                       adlLoader;
  public IDLLoader                    idlLoader;

// /public Instantiator graphInstantiator;

  public DefinitionCompiler    adlCompiler;
  public IDLVisitor            idlCompiler;

  public Map<Object, Object>          context;

  public DefinitionDocumentGenerator(final File sourceDirectories[], final File rootDirectory, final File targetDirectory) throws IOException {

    // input locators
    final BasicInputResourceLocator inputResourceLocator = new BasicInputResourceLocator();
    final IDLLocator idlLocator = IDLLoaderChainFactory.newLocator();
    final ADLLocator adlLocator = DocumentationFrontendFactory.newLocator();

    // String Template Component Loaders
    final StringTemplateComponentLoader stcLoader = new StringTemplateComponentLoader();
    final StringTemplateLoader templateLoader = new StringTemplateLoader();
    final XMLNodeFactory nodeFactory = new XMLNodeFactoryImpl();

    templateLoader.nodeFactoryItf = nodeFactory;
    stcLoader.loaderItf = templateLoader;

    // Plugin Manager Components
    final org.objectweb.fractal.adl.Factory pluginFactory = new SimpleClassPluginFactory();

    // loader chains
    idlLoader = IDLLoaderChainFactory.newLoader(idlLocator);
    adlLoader = DocumentationFrontendFactory.newLoader(inputResourceLocator,
        adlLocator, idlLocator, idlLoader, pluginFactory);

    // instantiator chain
    // graphInstantiator = Factory.newInstantiator(adlLoader);

    adlCompiler = DocumentationBackendFactory.newDefinitionCompiler();

    idlCompiler = IDLBackendFactory.newIDLCompiler();

    // init context
    context = new HashMap<Object, Object>();
    context.put(BasicOutputFileLocator.OUTPUT_DIR_CONTEXT_KEY, targetDirectory);
    final URL urls[] = new URL[sourceDirectories.length];
    for (int i = 0; i < sourceDirectories.length; i++) {
      final File directory = sourceDirectories[i];
      urls[i] = directory.toURI().toURL();
    }

    AnnotationLocatorHelper.addDefaultAnnotationPackage("org.ow2.mind.adl.annotation.predefined",
        context);

    final ClassLoader srcClassLoader = new URLClassLoader(urls, getClass().getClassLoader());
    context.put("classloader", srcClassLoader);
    context.put("sourceDirectory", rootDirectory);
  }

  public Definition loadADL(final String adlName) throws ADLException {
    return adlLoader.load(adlName, context);
  }

  public void generateADLDocumentation(final String adlName)
      throws ADLException, InterruptedException {
    final Definition d = loadADL(adlName);
    adlCompiler.visit(d, context);
  }

  public IDL loadIDL(final String idlName) throws ADLException {
    return idlLoader.load(idlName, context);
  }

  public void generateIDLDocumentation(final String idlName)
      throws ADLException, InterruptedException {
    final IDL idl = loadIDL(idlName);
    idlCompiler.visit(idl, context);
  }
}
