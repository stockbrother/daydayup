package daydayup.openstock;

public abstract class BaseSheetCommand<T> extends CommandBase<T> {

	@Override
	protected T doExecute(CommandContext cc) {
		SheetCommandContext scc = (SheetCommandContext) cc;
		return this.doExecute(scc);
	}

	protected abstract T doExecute(SheetCommandContext cc);

}
