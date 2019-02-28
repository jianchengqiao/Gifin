package com.sohu.inputmethod.sogou.http;

import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableSingleObserver;

/**
 * Created by Qiao on 2016/12/22.
 */

public abstract class ResponseSubscriber<M> extends DisposableSingleObserver<M> {

    @Override
    public final void onError(@NonNull Throwable e) {
        onFailure(-1, e.getMessage());
    }

    @Override
    public abstract void onSuccess(M model);

    public abstract void onFailure(int code, String msg);
}
