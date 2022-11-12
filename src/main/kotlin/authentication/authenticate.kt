package authentication

import io.ktor.util.*
import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.pbkdf2.HMac
import java.nio.charset.Charset
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

private val hashKey = System.getenv("HASH_SECRET_KEY").toByteArray()
private val hMacKey = SecretKeySpec(hashKey, "HmacSHA1")

fun hash(password: String): String {
    val hMac = Mac.getInstance("HmacSHA1")
    hMac.init(hMacKey)
    return hex(hMac.doFinal(password.toByteArray(Charsets.UTF_8)))
}