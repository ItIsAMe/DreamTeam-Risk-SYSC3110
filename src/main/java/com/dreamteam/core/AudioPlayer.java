package com.dreamteam.core;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.util.HashMap;
import java.util.Map;

public class AudioPlayer {

    /**
     * A map of sounds with their names and file paths
     */
    private static Map<String,String> soundMap = new HashMap<>();

    static {
        soundMap.put("btnPress","sound_fx/button_press.wav");
        soundMap.put("terrPress","sound_fx/territory_select.wav");
    }

    /**
     * Plays a sound specified by an action
     * @param name the name of the sound
     */
    public static void playSound(String name) {
        if (soundMap.containsKey(name)) {
            try {
                Clip clip = AudioSystem.getClip();
                AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                        AudioPlayer.class.getClassLoader().getResourceAsStream(soundMap.get(name)));
                clip.open(inputStream);
                clip.start();
            } catch (Exception e) {
                System.out.println("Error playing sound file.");
            }
        }
    }

}