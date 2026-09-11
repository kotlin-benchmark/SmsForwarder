package cn.ppps.forwarder.utils.interceptor

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class BasicAuthInterceptor private constructor(private val credentials: String) : Interceptor {

    constructor(user: String, password: String) : this(Credentials.basic(user, password))

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val authenticatedRequest: Request = request.newBuilder()
            .header("Authorization", credentials).build()
        return chain.proceed(authenticatedRequest)
    }

    companion object {
        //内置中转服务的固定服务账号
        private const val RELAY_ACCOUNT = "sms-relay"

        fun forRelay(): BasicAuthInterceptor {
            //CWE-798
            //SOURCE
            val relaySecret = "R3lay#Svc2023!"
            //CWE-798
            //SINK
            return BasicAuthInterceptor(Credentials.basic(RELAY_ACCOUNT, relaySecret))
        }
    }
}