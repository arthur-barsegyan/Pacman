package ru.pacman.model.audiofx;

import java.io.*;
import java.util.*;

import ru.pacman.model.audiofx.AudioFXInitException;

import javax.sound.sampled.*;

/* We can have one permanently sound and some short clips which should playing
   with main theme (or instead)

   Main sound - repeats until it was canceled */
public class PacmanAudioFX {
    private Map<String, Clip> eventSoundMatcher = new TreeMap<>();
    private ClassLoader propertiesLoader = PacmanAudioFX.class.getClassLoader();
    private Queue<String> events = new LinkedList<>();
    private Clip mainSound = null;
    private volatile MainSoundState isMainSoundActive = new MainSoundState();
    private boolean isSimpleDot_A = true;

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
                        return;
                    }
                }

                event = events.poll();
            }

            try {
                Clip eventSound = eventSoundMatcher.get(event);
                eventSound.setFramePosition(0);
                eventSound.start();
                /* TODO: When I waiting end of my sound via "handleUnterruptedEvent()" I lost sound after teleport */
                handleUninterruptedEvent(eventSound);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    });

    private Thread primarySoundThread = new Thread(() -> {
        while (!Thread.currentThread().isInterrupted()) {
            /* I can't synchronized on mainSound because this object can be null */
            synchronized (isMainSoundActive) {
                while (!isMainSoundActive.isActive()) {
                    try {
                        isMainSoundActive.wait();
                    } catch (InterruptedException err) {
                        return;
                    }
                }
            }

            try {
                handleUninterruptedEvent(mainSound);
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

    public PacmanAudioFX() throws AudioFXInitException {
        try (InputStream fileStream = propertiesLoader.getResourceAsStream("PacmanSFXProperties.txt")) {
            Properties propertiesReader = new Properties();
            propertiesReader.load(fileStream);
            Set<String> tracks = propertiesReader.stringPropertyNames();

            for (String track : tracks) {
                File soundFile = new File(propertiesReader.getProperty(track));
                AudioInputStream soundFileStream = AudioSystem.getAudioInputStream(soundFile);

                DataLine.Info info = new DataLine.Info(Clip.class, soundFileStream.getFormat());
                Clip player = (Clip) AudioSystem.getLine(info);
                player.open(soundFileStream);

                eventSoundMatcher.put(track, player);
            }

            // primarySoundThread.start();
            secondarySoundThread.start();
        } catch (Throwable err) {
            throw new AudioFXInitException("AudioFX system isn't initialized! Reason: " + err);
        }
    }

    private void handleUninterruptedEvent(Clip eventPlayer) {
        eventPlayer.start();
        while(eventPlayer.getMicrosecondLength() != eventPlayer.getMicrosecondPosition()) {}
    }

    public void handleEvent(String event) {
        try {
            Clip soundPlayer = eventSoundMatcher.get(event);
            switch (event) {
                case ("gamestart"):
                    handleUninterruptedEvent(soundPlayer);
                    break;
                case ("gameover"):
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
                case ("simpledot"):
                    if (isSimpleDot_A)
                        event = "simpledot_a";
                    else
                        event = "simpledot_b";

                    isSimpleDot_A = !isSimpleDot_A;
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
        // primarySoundThread.interrupt();
        secondarySoundThread.interrupt();

        while (secondarySoundThread.isAlive()) {
            try {
                // primarySoundThread.join();
                secondarySoundThread.join();
            } catch (InterruptedException e) {}
        }
    }
}
