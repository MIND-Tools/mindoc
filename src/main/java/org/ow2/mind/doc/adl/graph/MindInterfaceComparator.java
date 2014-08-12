/**
 * Copyright (C) 2012 Schneider Electric
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
 * Authors: Julien TOUS
 * Contributors: Stéphane Seyvoz
 */

package org.ow2.mind.doc.adl.graph;

import java.util.Comparator;
import org.objectweb.fractal.adl.types.TypeInterface;
import org.ow2.mind.adl.ast.MindInterface;


public class MindInterfaceComparator  implements Comparator<MindInterface> {

		public int compare(final MindInterface a, final MindInterface b) {
			int result;

			//Compare roles
			result = compareRoles(a,b);
			if (result!=0) return result;

			//Compare signatures
			result = compareSignatures(a,b);
			if (result!=0) return result;

			//Compare names
			return compareNames(a,b);
		}

		private int compareRoles(final MindInterface a, final MindInterface b) {
			final String aRole = a.getRole();
			final String bRole = b.getRole();
			if (aRole.equals(bRole)) return 0; // == or .equals ???
			else if (aRole.equals(TypeInterface.SERVER_ROLE)) return 1;
			else return -1;
		}

		private int compareSignatures(final MindInterface a, final MindInterface b) {
			final String aSignature = a.getSignature();
			final String bSignature = b.getSignature();
			return aSignature.compareTo(bSignature);
		}

		private int compareNames(final MindInterface a, final MindInterface b) {
			final String aName = a.getName();
			final String bName = b.getName();
			return aName.compareTo(bName);
		}
	}


