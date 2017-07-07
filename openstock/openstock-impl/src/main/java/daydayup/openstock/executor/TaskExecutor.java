package daydayup.openstock.executor;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.star.task.XStatusIndicator;
import com.sun.star.task.XStatusIndicatorFactory;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;

import daydayup.openstock.CommandBase;
import daydayup.openstock.CommandContext;
import daydayup.openstock.ooa.DocUtil;

public class TaskExecutor {

	private static final Logger LOG = LoggerFactory.getLogger(TaskExecutor.class);

	ExecutorService executor = Executors.newFixedThreadPool(1);

	private TaskWrapper<?> taskWrapper;

	private static class TaskWrapper<T> implements Callable<T>, Interruptable {
		CommandBase task;
		Future<?> future;
		CommandContext cc;

		TaskWrapper(CommandBase task, CommandContext cc) {
			this.cc = cc;
			this.task = task;
		}

		@Override
		public void interrupt() {
			if (task instanceof Interruptable) {
				((Interruptable) this.task).interrupt();
			}
		}

		@Override
		public T call() throws Exception {
			try {
				task.execute(cc);				
			} catch (Exception e) {
				LOG.error("", e);
			} catch(Error e){
				LOG.error("", e);
			} catch(Throwable t){
				LOG.error("", t);
			}

			return null;
		}
	}

	public void interruptAll() {
		if (this.taskWrapper != null) {
			this.taskWrapper.interrupt();
		}
	}

	public void execute(CommandBase command, CommandContext cc) throws TaskConflictException {

		if (this.taskWrapper != null) {
			if (this.taskWrapper.future.isDone()) {
				this.taskWrapper = null;
			} else {
				throw new TaskConflictException("task is running, please wait");
			}
		}
		TaskWrapper<Object> tw = new TaskWrapper<Object>(command, cc);
		Future<?> future = executor.submit(tw);
		tw.future = future;

	}
}
