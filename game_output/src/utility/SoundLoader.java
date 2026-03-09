package utility;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.net.URL;

public class SoundLoader {
    private static final int MAX_SOUNDS = 30;
    private Clip[] clips = new Clip[MAX_SOUNDS];
    private URL[] soundURL = new URL[MAX_SOUNDS];

    // List of sound filenames to preload
    private String[] soundFiles = {
        "dooropen.wav", //[0]
        "coin.wav",     //[1]
        "levelup.wav",  //[2]
        "powerup.wav",  //[3]
        "unlock.wav",   //[4]
        "loop.wav"      //[5]

        // Add more filenames here as needed
    };

    public SoundLoader() {
        String basePath = "src/main/resources/sound/";
        for (int i = 0; i < soundFiles.length && i < MAX_SOUNDS; i++) {
            try {
                File file = new File(basePath + soundFiles[i]);
                soundURL[i] = file.toURI().toURL();

                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
                clips[i] = AudioSystem.getClip();
                clips[i].open(audioInputStream);
            } catch (Exception e) {
                System.err.println("Failed to load sound: " + soundFiles[i]);
                e.printStackTrace();
            }
        }
    }

    public void play(int index) {
        if (index >= 0 && index < clips.length && clips[index] != null) {
            clips[index].setFramePosition(0);
            clips[index].start();
        }
    }

    public void stop(int index) {
        if (index >= 0 && index < clips.length && clips[index] != null && clips[index].isRunning()) {
            clips[index].stop();
        }
    }

    public void loop(int index) {
        if (index >= 0 && index < clips.length && clips[index] != null) {
            clips[index].loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public URL[] getSoundURLs() {
        return soundURL;
    }
}