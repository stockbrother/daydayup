package daydayup.openstock;

import daydayup.openstock.document.Spreadsheet;

public class SheetCommandContext extends CommandContext {

	private Attributes attributes = new Attributes();

	private String command;
	
	private Spreadsheet sheet;
	
	public SheetCommandContext(CommandContext parent, Spreadsheet sheet, String command) {
		super(parent.cc);
		this.sheet = sheet;
		this.command = command;
	}

	public Attributes getAttributes() {
		return attributes;
	}

	public Spreadsheet getSheet() {
		return sheet;
	}

}
