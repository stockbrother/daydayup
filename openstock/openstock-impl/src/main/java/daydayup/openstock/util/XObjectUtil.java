package daydayup.openstock.util;

import com.sun.star.beans.Property;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.container.NoSuchElementException;
import com.sun.star.container.XNameAccess;
import com.sun.star.lang.IndexOutOfBoundsException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.sheet.XAreaLinks;
import com.sun.star.uno.Any;

public class XObjectUtil {
	public static void format(XPropertySet arg0, StringBuffer sb) {
		Property[] pts = arg0.getPropertySetInfo().getProperties();

		for (int i = 0; i < pts.length; i++) {
			Property p = pts[i];
			Object value = null;
			try {
				value = arg0.getPropertyValue(p.Name);
			} catch (UnknownPropertyException e) {
				throw new RuntimeException(e);
			} catch (WrappedTargetException e) {
				throw new RuntimeException(e);
			}

			sb.append("[").append(i).append("]").append("Name:").append(p.Name).append(",Type:").append(p.Type)
					.append(",Attributes:").append(p.Attributes).append(",Handle:").append(p.Handle).append("Value:");
			format(value, sb, 8);
			sb.append("\n");

		}
	}

	public static void intend(StringBuffer sb, boolean brk, int intend) {
		if (brk) {
			sb.append("\n");
		}
		for (int i = 0; i < intend; i++) {
			sb.append(" ");
		}
	}

	public static void format(Object obj, StringBuffer sb, int intends) {
		intend(sb, false, intends);
		if (obj instanceof Any) {
			Any any = (Any) obj;
			any.getObject();
			sb.append("Any/type:").append(any.getType()).append(",object:").append("\n");
			format(any.getObject(), sb, intends + 8);
		} else if (obj instanceof XAreaLinks) {
			XAreaLinks xO = (XAreaLinks) obj;
			int count = xO.getCount();
			sb.append(XAreaLinks.class.getName()).append("/elementType:").append(xO.getElementType()).append("/count:")
					.append(count);

			for (int i = 0; i < count; i++) {
				Object value = null;
				try {
					value = xO.getByIndex(i);
				} catch (IndexOutOfBoundsException e) {
					throw new RuntimeException(e);
				} catch (WrappedTargetException e) {
					throw new RuntimeException(e);
				}
				intend(sb, true, intends);
				sb.append("[").append(i).append("]:\n");
				format(value, sb, intends + 8);
			}
		} else if (obj instanceof XNameAccess) {
			XNameAccess xO = (XNameAccess) obj;
			sb.append(XNameAccess.class.getName()).append("/elementType:").append(xO.getElementType())
					.append("/hasElements:").append(xO.hasElements());

			String[] names = xO.getElementNames();
			for (int i = 0; i < names.length; i++) {
				Object value = null;
				try {
					value = xO.getByName(names[i]);
				} catch (WrappedTargetException e) {
					throw new RuntimeException(e);
				} catch (NoSuchElementException e) {
					throw new RuntimeException(e);
				}
				intend(sb, true, intends);
				sb.append("[").append(names[i]).append("]:\n");
				format(value, sb, intends + 8);
			}

		} else {
			sb.append(obj);
		}
	}
}
