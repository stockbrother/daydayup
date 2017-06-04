package daydayup.util;

public class StringUtil {

	public static String unescapeJavaString(String line) {
		char[] sb = new char[line.length()];
		line.getChars(0, line.length(), sb, 0);
		return unescapeJavaString(sb, 0, sb.length);
	}

	public static String unescapeJavaString(char[] cbuf, int offset, int len) {

		StringBuilder sb = new StringBuilder(len);

		for (int i = offset; i < len; i++) {
			char ch = cbuf[i];
			if (ch == '\\') {
				char nextChar = (i == len - 1) ? '\\' : cbuf[i + 1];
				// Octal escape?
				if (nextChar >= '0' && nextChar <= '7') {
					String code = "" + nextChar;
					i++;
					if ((i < len - 1) && cbuf[i + 1] >= '0' && cbuf[i + 1] <= '7') {
						code += cbuf[i + 1];
						i++;
						if ((i < len - 1) && cbuf[i + 1] >= '0' && cbuf[i + 1] <= '7') {
							code += cbuf[i + 1];
							i++;
						}
					}
					sb.append((char) Integer.parseInt(code, 8));
					continue;
				}
				switch (nextChar) {
				case '\\':
					ch = '\\';
					break;
				case 'b':
					ch = '\b';
					break;
				case 'f':
					ch = '\f';
					break;
				case 'n':
					ch = '\n';
					break;
				case 'r':
					ch = '\r';
					break;
				case 't':
					ch = '\t';
					break;
				case '\"':
					ch = '\"';
					break;
				case '\'':
					ch = '\'';
					break;
				// Hex Unicode: u????
				case 'u':
					if (i >= len - 5) {
						ch = 'u';
						break;
					}
					int code = Integer.parseInt("" + cbuf[i + 2] + cbuf[i + 3] + cbuf[i + 4] + cbuf[i + 5], 16);
					sb.append(Character.toChars(code));
					i += 5;
					continue;
				}
				i++;
			}
			sb.append(ch);
		}
		return sb.toString();
	}
}
