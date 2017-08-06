package daydayup;

import daydayup.openstock.DdrContext;
import daydayup.openstock.RtException;

public class HandlerService {
    DdrContext ddr;
    public static ThreadLocal<DdrContext> threadLocalDdr = new ThreadLocal<>();
    public HandlerService(DdrContext ddr) {
        this.ddr = ddr;
    }

    public static DdrContext getDdrContext(){
        return threadLocalDdr.get();
    }

    public <T, R, H extends Handler<T, R>> R handle(Class<H> handlerClass, T arg) {

        DdrContext ddr = threadLocalDdr.get();
        if(ddr == null){
            threadLocalDdr.set(this.ddr);
        }

        try {
            return handlerClass.newInstance().execute(arg);
        } catch (InstantiationException e) {
            throw new RtException(e);
        } catch (IllegalAccessException e) {
            throw new RtException(e);
        }
    }
}
