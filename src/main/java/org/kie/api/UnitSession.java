package org.kie.api;

public interface UnitSession<T extends Unit> {

    interface Factory<U extends Unit, US extends UnitSession<? extends U>> {

        US create(U unit) ;
    }
    T unit();

    void start();
    void halt();
}
