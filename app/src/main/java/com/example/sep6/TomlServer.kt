package com.example.sep6

import org.stellar.sdk.federation.ConnectionErrorException
import org.stellar.sdk.federation.StellarTomlNotFoundInvalidException
import shadow.com.google.gson.Gson
import shadow.com.moandjiezana.toml.Toml
import shadow.okhttp3.HttpUrl
import shadow.okhttp3.OkHttpClient
import shadow.okhttp3.Request
import shadow.okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

object Toml {
    private fun createHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .build()
    }

    fun createForDomain(domain: String): Toml {
        val uriBuilder = StringBuilder()
        uriBuilder.append(domain)
        uriBuilder.append("/.well-known/stellar.toml")
        val stellarTomlUri = HttpUrl.parse(uriBuilder.toString())
        val httpClient = createHttpClient()

        val request = Request.Builder().get().url(stellarTomlUri).build()
        var response: Response? = null
        try {
            response = httpClient.newCall(request).execute()

            if (response!!.code() >= 300) {
                throw StellarTomlNotFoundInvalidException()
            }

            val stellarToml = Toml().read(response.body().string())

            return stellarToml
        } catch (e: IOException) {
            throw ConnectionErrorException()
        } finally {
            response?.close()
        }
    }

    fun checkTrailingSlash(website: String): String {
        return if (website.endsWith("/")) website else "$website/"
    }

    fun getTransferInfo(transferServer:String) : String {
        val safeTransferServer = checkTrailingSlash(transferServer)
        val httpClient = createHttpClient()
        val uriBuilder = StringBuilder()
        uriBuilder.append(safeTransferServer)
        uriBuilder.append("info")
        val request = Request.Builder().get().url(uriBuilder.toString()).build()
        var response: Response? = null
        try {
           response = httpClient.newCall(request).execute()
           return response.body().string()
        } catch (e: IOException) {
            throw ConnectionErrorException()
        } finally {
            response?.close()
        }
    }

    fun getWithdrawInfo(transferServer:String, asset_code:String, account:String, dest:String?) : WithdrawData {
        val safeTransferServer = checkTrailingSlash(transferServer)
        val httpClient = createHttpClient()
        val uriBuilder = StringBuilder()
        uriBuilder.append(safeTransferServer)
        uriBuilder.append("withdraw")
        uriBuilder.append("?asset_code=$asset_code")
        uriBuilder.append("&account=$account")

        if (dest == null) {
            uriBuilder.append("&dest=$dest")
        }

        val request = Request.Builder().get().url(uriBuilder.toString()).build()
        var response: Response? = null
        try {
            response = httpClient.newCall(request).execute()
            return Gson().fromJson(response.body().string(), WithdrawData::class.java)
        } catch (e: IOException) {
            throw ConnectionErrorException()
        } finally {
            response?.close()
        }
    }

    fun getDepositUrl(transferServer:String, asset_code:String, account:String, email_address:String) : String {
        val httpClient = createHttpClient()
        val uriBuilder = StringBuilder()
        uriBuilder.append(transferServer)
        uriBuilder.append("deposit")
        uriBuilder.append("?asset_code=$asset_code")
        uriBuilder.append("&account=$account")
        uriBuilder.append("&email_address=$email_address")

        val request = Request.Builder().get().url(uriBuilder.toString()).build()
        var response: Response? = null
        try {
            response = httpClient.newCall(request).execute()

            return response.body().string()
        } catch (e: IOException) {
            throw ConnectionErrorException()
        } finally {
            response?.close()
        }
    }
}