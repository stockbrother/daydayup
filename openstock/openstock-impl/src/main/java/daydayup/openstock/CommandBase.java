package daydayup.openstock;

public abstract class CommandBase<T> {

	public abstract T execute(CommandContext cc);

}
