package com.hackiu.covid_19;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.ncorti.slidetoact.SlideToActView;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class Login extends AppCompatActivity {

    TextView info, registro, login;
    ImageView imagenhuella;
    LottieAnimationView huella;

    private static final String KEY_NAME = "yourKey";
    private Cipher cipher;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private FingerprintManager.CryptoObject cryptoObject;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        imagenhuella = findViewById(R.id.imagenhuella);
        huella = findViewById(R.id.huella);
        info = findViewById(R.id.info);
        registro = findViewById(R.id.registro);
        login = findViewById(R.id.login);

        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            LayoutInflater layoutInflater = LayoutInflater.from(Login.this);
            View view = layoutInflater.inflate(R.layout.register_dialog, null);

            SlideToActView sta = view.findViewById(R.id.register);

//                SlideToActView.OnSlideCompleteListener

            TextView tlf = view.findViewById(R.id.tlf);
            tlf.setTransformationMethod(null);

            AlertDialog alertDialog = new AlertDialog.Builder(Login.this)
                    .setView(view).create();

            alertDialog.show();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            LayoutInflater layoutInflater = LayoutInflater.from(Login.this);
            View view = layoutInflater.inflate(R.layout.login_dialog, null);

            SlideToActView sta = view.findViewById(R.id.login);
            TextView tlf = view.findViewById(R.id.tlf);
            tlf.setTransformationMethod(null);

            AlertDialog alertDialog = new AlertDialog.Builder(Login.this)
                .setView(view).create();

            alertDialog.show();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
            }

            if (!fingerprintManager.isHardwareDetected()) {
//                textView.setText("Your device doesn't support fingerprint authentication");
            }

            if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
//                textView.setText("Please enable the fingerprint permission");
            }

            if (!fingerprintManager.hasEnrolledFingerprints()) {
//                textView.setText("No fingerprint configured. Please register at least one fingerprint in your device's Settings");

            }

            if (!keyguardManager.isKeyguardSecure()) {
            } else {
                try {
                    generateKey();
                } catch (FingerprintException e) {
                    e.printStackTrace();
                }
                if (initCipher()) {
                    cryptoObject = new FingerprintManager.CryptoObject(cipher);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            FingerprintHandler helper = new FingerprintHandler(getBaseContext(), imagenhuella, huella, cipher);
                            helper.startAuth(fingerprintManager, cryptoObject);
                        }
                    });
                }
            }
        }

        Log.e("cipher", cipher+"");
    }


    private void generateKey() throws FingerprintException {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            keyStore.load(null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                keyGenerator.init(new
                        KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setUserAuthenticationRequired(true)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        .build());
            }
            keyGenerator.generateKey();
        } catch (KeyStoreException | NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException | IOException | CertificateException exc) {
            exc.printStackTrace();
            throw new FingerprintException(exc);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean initCipher() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException
                | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }



    private class FingerprintException extends Exception {

        public FingerprintException(Exception e) {
            super(e);
        }
    }
}