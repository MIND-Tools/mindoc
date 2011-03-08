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
 * Authors: ali-erdem.ozcan@st.com, michel.metzger@st.com
 * Contributors:
 */

package org.ow2.mind.doc;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.fractal.adl.ADLException;
import org.ow2.mind.doc.DefinitionDocumentGenerator;
import org.ow2.mind.inject.GuiceModuleExtensionHelper;
import org.ow2.mind.plugin.PluginLoaderModule;
import org.ow2.mind.plugin.PluginManager;
import org.testng.annotations.BeforeTest;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class AbstractDocumentationGeneratorTest {
  protected final File                    target    = new File("target/doc");
  protected final File                    sources[] = new File[]{new File(
                                                      "src/test/resources")};

  protected DefinitionDocumentGenerator generator;
  protected Map<Object, Object>         context;
  protected Injector injector;

  @BeforeTest(alwaysRun = true)
  public void setUp() throws Exception {
    context = new HashMap<Object, Object>();

    // create plugin manager
    final Injector pluginManagerInjector = Guice
        .createInjector(new PluginLoaderModule());
    final PluginManager pluginManager = pluginManagerInjector
        .getInstance(PluginManager.class);

    // init context

    // Put this in context to enable mindoc Guice modules.
    context.put("org.ow2.mind.doc.GenrateDoc", Boolean.TRUE);

    injector = Guice.createInjector(GuiceModuleExtensionHelper
        .getModules(pluginManager, context));
    context.put("sourceDirectory", sources[0]);

    generator = injector.getInstance(DefinitionDocumentGenerator.class);
  }

  public Collection<File> compileADL(final String adlName) throws ADLException,
      InterruptedException {
    generator.generateADLDocumentation(adlName, context);
    return null;
  }

  public void compileIDL(final String idlName) throws ADLException,
      InterruptedException {
    generator.generateIDLDocumentation(idlName, context);
  }
}
