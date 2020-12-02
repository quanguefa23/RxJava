package com.zing.zalo.zalorx.schedulers

import com.zing.zalo.zalorx.Disposable

object EmptyDisposable: Disposable {
    override fun dispose() {}

    override fun isDisposed() = true
}