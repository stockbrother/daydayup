package daydayup.openstock.executor;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import daydayup.openstock.CommandBase;
import daydayup.openstock.CommandContext;

public class TaskExecutor {

	private static final Logger LOG = LoggerFactory.getLogger(TaskExecutor.class);

	ExecutorService executor = Executors.newFixedThreadPool(1);

	private TaskWrapper taskWrapper;

	private static class TaskWrapper {
		Runnable task;
		Future<?> future;

		TaskWrapper(Runnable task, Future<?> future2) {
			this.task = task;
			this.future = future2;
		}

		public void interrupt() {
			if (task instanceof Interruptable) {
				((Interruptable) this.task).interrupt();
			}
		}
	}

	private static class CommandWrapper implements Runnable, Interruptable {
		CommandBase command;
		CommandContext cc;

		CommandWrapper(CommandBase command, CommandContext cc) {
			this.command = command;
			this.cc = cc;
		}

		@Override
		public void interrupt() {
			if (this.command instanceof Interruptable) {
				((Interruptable) this.command).interrupt();
			}
		}

		@Override
		public void run() {
			this.command.execute(cc);
		}

	}

	public void interruptAll() {
		if (this.taskWrapper != null) {
			this.taskWrapper.interrupt();
		}
	}

	public void execute(CommandBase command, CommandContext cc) throws TaskConflictException {
		execute(new CommandWrapper(command, cc));
	}

	public void execute(Runnable task) throws TaskConflictException {

		if (this.taskWrapper != null) {
			if (this.taskWrapper.future.isDone()) {
				this.taskWrapper = null;
			} else {
				throw new TaskConflictException("task is running, please wait");
			}
		}

		Future<?> future = executor.submit(new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				try {
					task.run();
				} catch (Exception e) {
					LOG.error("", e);
				}

				return null;
			}

		});
		this.taskWrapper = new TaskWrapper(task, future);

	}
}
