package ru.pacman.model;

import java.awt.event.ActionEvent;
import java.io.*;
import java.util.*;
import java.util.Timer;

import com.sun.org.apache.bcel.internal.util.ClassLoader;
//import jaco.mp3.player.MP3Player;
import javafx.beans.property.BooleanProperty;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;

/* We can have one permanently sound and some temp sound which should playing
   with main theme (or instead) */
class PacmanAudioFX extends JFXPanel {
    private Map<String, Clip> eventSoundMatcher = new TreeMap<>();
    private Properties propertiesReader = new Properties();
    private ClassLoader propertiesLoader = new ClassLoader();
    private Queue<String> events = new LinkedList<>();
    private Clip mainSound = null;
    private volatile MainSoundState isMainSoundActive = new MainSoundState();

    class MainSoundState {
        private boolean state = false;

        void changeState(boolean newState) { state = newState; }
        boolean isActive() { return state; }
    }

    private Thread secondarySoundThread = new Thread(() -> {
        String event = "";
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (events) {
                while (events.isEmpty()) {
                    try {
                        events.wait();
                    } catch (InterruptedException err) {
                        break;
                    }
                }

                event = events.poll();
            }

            try {
                /*MP3Player eventSound = eventSoundMatcher.get(event);*/
                Clip eventSound = eventSoundMatcher.get(event);
                handleUninterruptedEvent(eventSound);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    });

    /*javax.swing.Timer siren = new javax.swing.Timer(2000, (ActionEvent event) -> {
        handleUninterruptedEvent(mainSound);
    });*/

    private Thread primarySoundThread = new Thread(() -> {
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (isMainSoundActive) {
                while (!isMainSoundActive.isActive()) {
                    try {
                        isMainSoundActive.wait();
                    } catch (InterruptedException err) {
                        break;
                    }
                }
            }

            try {
                //mainSound.setRepeat(false);
                //mainSound.play();
                /*mainSound.setStartTime(new Duration(150));
                mainSound.setStopTime(new Duration(1400));
                *///mainSound.setCycleCount(MediaPlayer.INDEFINITE);
                /*mainSound.setOnPlaying(() -> {
                    System.out.println("HERE");
                    if (mainSound.getCurrentTime().greaterThan(new Duration(1500))) {
                        mainSound.seek(new Duration(150));
                    }
                });*/

                handleUninterruptedEvent(mainSound);
                Thread.sleep(1500);
                Thread test = new Thread(() -> {
                   handleUninterruptedEvent(mainSound);
                });
                test.run();
                /*mainSound.setOnEndOfMedia(new Runnable() {
                    public void run() {
                        mainSound.seek(Duration.ZERO);
                        mainSound.play();
                    }
                });*/
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    });

    private boolean isHaveNewMainSound() {
        synchronized (isMainSoundActive) {
            return isMainSoundActive.isActive();
        }
    }

    public PacmanAudioFX() throws IOException {
        InputStream fileStream = propertiesLoader.getResourceAsStream("PacmanSFXProperties.txt");

        if (fileStream == null) {
            throw new RuntimeException("Can't open SFX properties file!");
        }

        try {
            propertiesReader.load(fileStream);
            Set<String> tracks = propertiesReader.stringPropertyNames();

            for (String track : tracks) {
                File tempFile = new File(propertiesReader.getProperty(track));
                //Media sound = new Media(tempFile.toURI().toString());
                /*AudioClip player = new AudioClip(tempFile.toURI().toString());
                */
                AudioInputStream sound = AudioSystem.getAudioInputStream(tempFile);
                Clip clip = AudioSystem.getClip();
                clip.open(sound);
                eventSoundMatcher.put(track, clip);
                /*MP3Player playMP3 = new MP3Player(tempFile);
                eventSoundMatcher.put(track, playMP3);*/
            }

            primarySoundThread.start();
            //secondarySoundThread.start();
        } catch (IOException err) {
            //log.error("Problem with parsing factory config file!");
            throw err;
        } catch (Throwable err) {
            System.out.println(err.getMessage());
        } finally {
            if (fileStream != null)
                fileStream.close();
        }
    }

    private void handleUninterruptedEvent(Clip eventPlayer) {
       synchronized (eventPlayer) {
           eventPlayer.setFramePosition(0);
           eventPlayer.start();
       }
    }

    public void handleEvent(String event) {
        try {
            Clip soundPlayer = eventSoundMatcher.get(event);
            System.out.println("Get new player for event " + event);
            switch (event) {
                case ("gamestart"):
                    handleUninterruptedEvent(soundPlayer);
                    break;
                case ("gameend"):
                    handleUninterruptedEvent(soundPlayer);
                    break;
                case ("chasemode"):
                    /* TODO: Check this decision */
                    synchronized (isMainSoundActive) {
                        if (!isMainSoundActive.isActive()) {
                            mainSound = soundPlayer;
                            isMainSoundActive.changeState(true);
                        } else
                            mainSound = soundPlayer;

                        isMainSoundActive.notify();
                    }
                    break;
                default: {
                    synchronized (events) {
                        events.add(event);
                        events.notifyAll();
                    }
                }
            }

        } catch (IllegalMonitorStateException err) {
            System.out.println("I found mistake");
        }
        catch (Throwable err) {
            System.out.println("[Pacman AudioFX] Problems with event: " + event);
        }
    }

    public void close() {
        primarySoundThread.interrupt();
        secondarySoundThread.interrupt();
        /* TODO: Why join() throwing InterruptedException? */
        //soundThread.join();
    }
}
