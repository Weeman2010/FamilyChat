package com.example.familychat.Util;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.Locale;


public class MessageToSpeech {

  private static TextToSpeech textToSpeechSystem;
    public static void play(Context context,String text){
        textToSpeechSystem = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeechSystem.setLanguage(Locale.GERMANY);
                    if(checkSoundisOn(context)) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            textToSpeechSystem.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
                        } else {
                            textToSpeechSystem.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                        }
                    }
                }
            }
        });
    }

    private static boolean checkSoundisOn(Context context) {
        AudioManager manager=(AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        int musicVolume = manager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if(musicVolume <=5){
            manager.setStreamVolume(AudioManager.STREAM_MUSIC,(manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)/2),AudioManager.FLAG_PLAY_SOUND);
            Toast.makeText(context,"Lautstärke wurde erhöht bitte erneut drücken",Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }
    }

}
