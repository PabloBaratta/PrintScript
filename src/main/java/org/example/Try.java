package org.example;

import java.util.Optional;

public class Try<S, F extends Exception> {
    private S success;
    private F fail;

    public Try(S success){
        this.success = success;
    }

    public Try(F fail){
        this.fail = fail;
    }
    public Optional<S> getSuccess() {
        return Optional.ofNullable(success);
    }

    public boolean isSuccess() {
        return success != null;
    }

    public Optional<F> getFail() {
        return Optional.ofNullable(fail);
    }

    public boolean isFail() {
        return fail != null;
    }
}

