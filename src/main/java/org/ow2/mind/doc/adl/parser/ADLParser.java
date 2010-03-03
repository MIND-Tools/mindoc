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

package org.ow2.mind.doc.adl.parser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import org.objectweb.fractal.adl.ADLErrors;
import org.objectweb.fractal.adl.ADLException;
import org.objectweb.fractal.adl.Definition;
import org.objectweb.fractal.adl.error.BasicErrorLocator;
import org.objectweb.fractal.adl.error.ErrorLocator;
import org.ow2.mind.InputResourcesHelper;
import org.ow2.mind.adl.jtb.ParseException;
import org.ow2.mind.adl.jtb.Parser;
import org.ow2.mind.adl.jtb.syntaxtree.ADLFile;

public class ADLParser extends org.ow2.mind.adl.parser.ADLParser {

  // ---------------------------------------------------------------------------
  // Implementation of the Loader interface
  // ---------------------------------------------------------------------------

  @Override
  public Definition load(final String name, final Map<Object, Object> context)
      throws ADLException {
    final URL adlFile = locateADL(name, context);

    Definition d;
    try {
      d = readADL(adlFile.openStream(), adlFile.getPath());
    } catch (final IOException e) {
      throw new ADLException(ADLErrors.IO_ERROR, e, adlFile.getPath());
    } catch (final ParseException e) {
      final ErrorLocator locator = new BasicErrorLocator(adlFile.getPath(),
          e.currentToken.beginLine, e.currentToken.beginColumn);
      throw new ADLException(ADLErrors.PARSE_ERROR, locator, e.getMessage());
    }

    InputResourcesHelper.addInputResource(d, adlLocatorItf
        .toInputResource(name));

    return d;
  }

  @Override
  protected Definition readADL(final InputStream is, final String fileName)
      throws IOException, ParseException, ADLException {
    final Parser parser = new Parser(is);
    final JTBProcessor processor = new JTBProcessor(nodeFactoryItf, DTD,
        fileName);
    final ADLFile content = parser.ADLFile();
    return processor.toDefinition(content);
  }

}
