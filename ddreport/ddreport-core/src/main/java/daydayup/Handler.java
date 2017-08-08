package daydayup;

public abstract class Handler<T,R> {

    public abstract R execute(T arg);

}
