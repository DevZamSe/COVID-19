package com.hackiu.covid_19;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.Manifest;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.airbnb.lottie.LottieAnimationView;

import javax.crypto.Cipher;


@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private CancellationSignal cancellationSignal;
    ImageView imagenhuella;
    Cipher cipher;
    LottieAnimationView huella;
    private Context context;


    public FingerprintHandler(Context mContext, ImageView imagenhuella, LottieAnimationView huella, Cipher cipher) {
        this.context = mContext;
        this.imagenhuella = imagenhuella;
        this.huella = huella;
        this.cipher = cipher;
    }

    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {
        cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
        }
    }

    @Override
    public void onAuthenticationError(int errMsgId,
                                      CharSequence errString) {
       // Toast.makeText(context, "Authentication error\n" + errString, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationFailed() {
        //Toast.makeText(context, "Authentication failed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId,
                                     CharSequence helpString) {
        //Toast.makeText(context, "Authentication help\n" + helpString, Toast.LENGTH_LONG).show();
    }


    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {

        imagenhuella.setVisibility(View.GONE);
        huella.pauseAnimation();
        huella.playAnimation();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                context.startActivity(new Intent(context, IntroActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                Log.e("cipher", cipher.toString());
//                Toast.makeText(context, "Success!", Toast.LENGTH_LONG).show();
            }
        }, 2250);
    }


}