package com.ltan.sign.util

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.util.Log
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 *
 * @desc
 * @author  tanlin
 * @since  2020/5/9
 * @version 1.0
 */
object AppUtil {

    private const val TAG = "Sign/AppUtil"

    fun getSignature(context: Context?, packageName: String, type: String): String {
        var tmp = ""
        if (context == null) {
            return tmp
        }

        val signs = getSignatures(context, packageName) ?: return tmp
        for (sig in signs) {
            tmp = getSignatureString(sig, type)
        }
        return tmp
    }

    private fun getSignatures(context: Context, packageName: String): Array<Signature>? {
        try {
            val packageInfo = context.packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            return packageInfo.signatures
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, "getSignatures e:$e")
        }
        return null
    }

    private fun getSignatureString(sign: Signature, type: String): String {
        val hexBytes = sign.toByteArray()
        var fingerprint = "error!"
        try {
            val digest: MessageDigest = MessageDigest.getInstance(type)
            val digestBytes: ByteArray = digest.digest(hexBytes)
            val sb = StringBuilder()
            for (byte in digestBytes) {

                val b: Int = byte.toInt().and(0xFF)
                val s = Integer.toHexString(b)//.toUpperCase(Locale.US)
                if (s.length == 1) {
                    sb.append("0")
                }
                sb.append(s)
            }
            fingerprint = sb.toString()
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "getSignatureString $e")
        }
        return fingerprint
    }
}
