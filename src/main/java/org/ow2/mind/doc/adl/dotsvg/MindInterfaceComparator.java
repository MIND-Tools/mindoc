package org.ow2.mind.doc.adl.dotsvg;

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
			if (aRole == bRole) return 0; // == or .equals ???
			else if (aRole ==TypeInterface.SERVER_ROLE) return 1;
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


