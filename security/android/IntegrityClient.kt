package ng.divstudio.divcut.security

import android.content.Context
import android.util.Base64
import com.google.android.play.core.integrity.IntegrityManagerFactory
import com.google.android.play.core.integrity.IntegrityTokenRequest
import java.security.SecureRandom

object IntegrityClient {
    fun request(context: Context, onToken: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val nonceBytes = ByteArray(32).also { SecureRandom().nextBytes(it) }
        val nonce = Base64.encodeToString(nonceBytes, Base64.NO_WRAP)
        val manager = IntegrityManagerFactory.create(context)
        manager.requestIntegrityToken(
            IntegrityTokenRequest.builder().setNonce(nonce).build()
        ).addOnSuccessListener { onToken(it.token()) }
         .addOnFailureListener { onFailure(it) }
    }
}
