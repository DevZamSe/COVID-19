package com.hackiu.covid_19;

import android.Manifest;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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

    TextView info, registro, login, huellanosupport;
    ImageView imagenhuella;
    LottieAnimationView huella;
    TelephonyManager telephonyManager;

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

        huellanosupport = findViewById(R.id.huellanosupport);
        imagenhuella = findViewById(R.id.imagenhuella);
        huella = findViewById(R.id.huella);
        info = findViewById(R.id.info);
        registro = findViewById(R.id.registro);
        login = findViewById(R.id.login);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.READ_PHONE_STATE}, 0);
            requestPermissions(new String[] {Manifest.permission.USE_FINGERPRINT}, 0);
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            requestPermissions(new String[] {Manifest.permission.CALL_PHONE}, 0);
            return;
        }
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = telephonyManager.getDeviceId();
        Log.e("unique id", deviceId);

        if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.USE_FINGERPRINT}, 0);
        }

        if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 0);
//                textView.setText("Please enable the fingerprint permission");
        }

        if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.CALL_PHONE}, 0);
//                textView.setText("Please enable the fingerprint permission");
        }




        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(Login.this);
                final View view = layoutInflater.inflate(R.layout.login_dialog, null);

                final SlideToActView sta = view.findViewById(R.id.login);
                final TextView tlff = view.findViewById(R.id.tlff);
                final TextView passs = view.findViewById(R.id.pass);
                final TextView incorrect = view.findViewById(R.id.incorrecto);
                tlff.setTransformationMethod(null);
                ImageView close = view.findViewById(R.id.close);

                final AlertDialog alertDialogs = new AlertDialog.Builder(Login.this)
                        .setView(view).create();
                alertDialogs.show();
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialogs.dismiss();
                    }
                });

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (!Thread.currentThread().isInterrupted())
                            if(tlff.getText().toString().length() == 9 && !passs.getText().toString().isEmpty()){
                                sta.setLocked(false);
                            } else {
                                sta.setLocked(true);
                            }
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (!Thread.currentThread().isInterrupted()){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    sta.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
                                        @Override
                                        public void onSlideComplete(SlideToActView slideToActView) {
                                            if(tlff.getText().toString().length() == 9 && !passs.getText().toString().isEmpty()){
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        alertDialogs.dismiss();
                                                        huella.playAnimation();
                                                        new Handler().postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Intent i = new Intent(Login.this, IntroActivity.class);
                                                                startActivity(i);
                                                                finish();
                                                            }
                                                        },2500);
                                                    }
                                                },500);
                                            } else {
                                                incorrect.setVisibility(view.VISIBLE);
                                            }
                                        }
                                    });
                                }
                            });
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();

                sta.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
                    @Override
                    public void onSlideComplete(@NonNull SlideToActView view) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                alertDialogs.dismiss();
                                huella.playAnimation();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent i = new Intent(Login.this, IntroActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                },2500);
                            }
                        },500);
                    }
                });

            }
        });




        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            LayoutInflater layoutInflater = LayoutInflater.from(Login.this);
            final View view = layoutInflater.inflate(R.layout.register_dialog, null);

            ImageView close = view.findViewById(R.id.close);
            final SlideToActView sta = view.findViewById(R.id.register);

            final TextView name = view.findViewById(R.id.name);
            final TextView tlf = view.findViewById(R.id.tlff);
            final TextView pass = view.findViewById(R.id.pass);

            final TextView incorrecto = view.findViewById(R.id.incorrecto);

            tlf.setTransformationMethod(null);

            final AlertDialog alertDialog = new AlertDialog.Builder(Login.this)
                    .setView(view).create();
            alertDialog.show();
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!Thread.currentThread().isInterrupted())
                        if(!name.getText().toString().isEmpty() && tlf.getText().toString().length() == 9 && !pass.getText().toString().isEmpty()){
                            sta.setLocked(false);
                        } else {
                            sta.setLocked(true);
                        }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();


            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!Thread.currentThread().isInterrupted()){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                sta.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
                                    @Override
                                    public void onSlideComplete(SlideToActView slideToActView) {
                                        if(!name.getText().toString().isEmpty() && tlf.getText().toString().length() == 9 && !pass.getText().toString().isEmpty()){
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    alertDialog.dismiss();
                                                    huella.playAnimation();
                                                    new Handler().postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Intent i = new Intent(Login.this, IntroActivity.class);
                                                            startActivity(i);
                                                            finish();
                                                        }
                                                    },2500);
                                                }
                                            },500);
                                        } else {
                                            incorrecto.setVisibility(view.VISIBLE);
                                        }
                                    }
                                });
                            }
                        });
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(Login.this);
                View view = layoutInflater.inflate(R.layout.info_dialog, null);

                ImageView close = view.findViewById(R.id.close);


                final AlertDialog alertDialog = new AlertDialog.Builder(Login.this)
                    .setView(view).create();

                alertDialog.show();
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
            }

            if (!fingerprintManager.isHardwareDetected()) {
                huellanosupport.setText("Tu dispositivo no soporta la huella, inicia sesión con tu número de celular");
            }

            if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {Manifest.permission.USE_FINGERPRINT}, 0);
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