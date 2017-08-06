package daydayup;

import daydayup.openstock.DdrContext;

public abstract class Handler<T,R> {

    public abstract R execute(T arg);

    public DdrContext getDdrContext(){
        return HandlerService.getDdrContext();
    }

}
