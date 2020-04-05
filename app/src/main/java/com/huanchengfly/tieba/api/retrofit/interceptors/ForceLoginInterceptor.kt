package com.huanchengfly.tieba.api.retrofit.interceptors

import com.huanchengfly.tieba.api.Error.ERROR_NOT_LOGGED_IN
import com.huanchengfly.tieba.api.Header
import com.huanchengfly.tieba.api.retrofit.exception.TiebaLocalException
import com.huanchengfly.tieba.post.base.BaseApplication
import com.huanchengfly.tieba.post.utils.AccountUtil
import okhttp3.Interceptor
import okhttp3.Response

object ForceLoginInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var headers = request.headers
        var httpUrl = request.url
        var body = request.body

        //是否强制登录
        var forceLogin = false
        val forceLoginHeader = headers[Header.FORCE_LOGIN]
        if (forceLoginHeader != null) {
            if (forceLoginHeader == Header.FORCE_LOGIN_TRUE) forceLogin = true
            headers = headers.newBuilder().removeAll(Header.FORCE_LOGIN).build()
        }

        if (forceLogin && !AccountUtil.isLoggedIn(BaseApplication.getInstance())) {
            throw TiebaLocalException(ERROR_NOT_LOGGED_IN, "Not logged in.")
        }

        return chain.proceed(
                request.newBuilder()
                        .headers(headers)
                        .url(httpUrl)
                        .method(request.method, body)
                        .build()
        )
    }

}