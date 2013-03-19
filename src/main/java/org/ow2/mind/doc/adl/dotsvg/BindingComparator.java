package org.ow2.mind.doc.adl.dotsvg;

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


