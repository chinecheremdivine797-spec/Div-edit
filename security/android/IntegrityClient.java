package ng.divstudio.divcut.security;

import android.content.Context;
import android.util.Base64;
import com.google.android.play.core.integrity.IntegrityManager;
import com.google.android.play.core.integrity.IntegrityManagerFactory;
import com.google.android.play.core.integrity.IntegrityTokenRequest;
import java.security.SecureRandom;

public final class IntegrityClient {
    private IntegrityClient() {}

    public interface Callback {
        void success(String token);
        void failure(Exception error);
    }

    public static void request(Context context, Callback callback) {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        String nonce = Base64.encodeToString(bytes, Base64.NO_WRAP);
        IntegrityManager manager = IntegrityManagerFactory.create(context);
        manager.requestIntegrityToken(
            IntegrityTokenRequest.builder().setNonce(nonce).build()
        ).addOnSuccessListener(result -> callback.success(result.token()))
         .addOnFailureListener(error -> callback.failure(
             error instanceof Exception ? (Exception) error : new Exception(error)
         ));
    }
}
