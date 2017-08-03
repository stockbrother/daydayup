package daydayup.openstock;

public abstract class CommandBase<T> {
	public T execute(CommandContext cc) {
		return null;//
	}

	protected abstract T doExecute(CommandContext cc);

}
