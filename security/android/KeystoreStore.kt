package ng.divstudio.divcut.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.GCMParameterSpec

object KeystoreStore {
    private const val ALIAS = "div-edit-data-key"
    private const val TRANSFORM = "AES/GCM/NoPadding"

    private fun key(): java.security.Key {
        val ks = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        ks.getKey(ALIAS, null)?.let { return it }
        val gen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        gen.init(KeyGenParameterSpec.Builder(
            ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
         .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
         .setUserAuthenticationRequired(false)
         .build())
        return gen.generateKey()
    }

    fun encrypt(value: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORM)
        cipher.init(Cipher.ENCRYPT_MODE, key())
        return cipher.iv + cipher.doFinal(value)
    }

    fun decrypt(blob: ByteArray): ByteArray {
        require(blob.size > 12) { "Invalid encrypted payload" }
        val cipher = Cipher.getInstance(TRANSFORM)
        cipher.init(Cipher.DECRYPT_MODE, key(), GCMParameterSpec(128, blob.copyOfRange(0, 12)))
        return cipher.doFinal(blob.copyOfRange(12, blob.size))
    }
}
