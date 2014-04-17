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
import org.ow2.mind.adl.ast.Binding;

public class BindingComparator implements Comparator<Binding> {

		public int compare(final Binding a, final Binding b) {
			int result;

			//Compare Servers
			result = compareServerComponents(a,b);
			if (result!=0) return result;
			result = compareServerInterfaces(a,b);
			if (result!=0) return result;

			//Compare Clients
			result = compareClientComponents(a,b);
			if (result!=0) return result;
			return compareClientInterfaces(a,b);
		}

		private int compareServerComponents(final Binding a, final Binding b) {
			return a.getToComponent().compareTo(b.getToComponent());
		}

		private int compareServerInterfaces(final Binding a, final Binding b) {
			return a.getToInterface().compareTo(b.getToInterface());
		}

		private int compareClientComponents(final Binding a, final Binding b) {
			return a.getFromComponent().compareTo(b.getFromComponent());
		}
		private int compareClientInterfaces(final Binding a, final Binding b) {
			return a.getFromInterface().compareTo(b. getFromInterface());
		}
	}


