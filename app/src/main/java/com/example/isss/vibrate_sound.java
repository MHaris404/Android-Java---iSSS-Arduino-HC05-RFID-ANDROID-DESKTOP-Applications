package com.example.isss;

import android.content.Context;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class vibrate_sound {

    public void vibrate(Context context) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(300);
        }
    }

    public void sound(Context context, int type) {
        try {
            Resources res = context.getResources();
            int sound;
            switch (type) {
                case 2:
                    sound = res.getIdentifier(String.valueOf(R.raw.disconn), "raw", context.getPackageName());
                    break;
                case 3:
                    sound = res.getIdentifier(String.valueOf(R.raw.item), "raw", context.getPackageName());
                    break;
                case 4:
                    sound = res.getIdentifier(String.valueOf(R.raw.error), "raw", context.getPackageName());
                    break;
                default:
                case 1:
                    sound = res.getIdentifier(String.valueOf(R.raw.conn), "raw", context.getPackageName());
                    break;
            }


            MediaPlayer mediaPlayer = MediaPlayer.create(context, sound);
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
