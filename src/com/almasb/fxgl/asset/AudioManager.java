/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.almasb.fxgl.asset;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.almasb.fxgl.FXGLManager;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.media.MediaPlayer.Status;

/**
 * Controls playback and the volume of {@link com.almasb.fxgl.asset.Sound}
 * and {@link com.almasb.fxgl.asset.Music}.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class AudioManager extends FXGLManager {

    /**
     * Contains sounds which are currently playing.
     */
    private List<Sound> activeSounds = new ArrayList<>();

    /**
     * Contains music objects which are currently playing or paused.
     */
    private List<Music> activeMusic = new ArrayList<>();

    private DoubleProperty globalMusicVolume = new SimpleDoubleProperty(1.0);

    /**
     *
     * @return global music volume property
     */
    public DoubleProperty globalMusicVolumeProperty() {
        return globalMusicVolume;
    }

    /**
     *
     * @return global music volume
     */
    public double getGlobalMusicVolume() {
        return globalMusicVolumeProperty().get();
    }

    /**
     * Set global music volume in the range [0..1],
     * where 0 = 0%, 1 = 100%
     *
     * @param volume
     */
    public void setGlobalMusicVolume(double volume) {
        globalMusicVolumeProperty().set(volume);
    }

    private DoubleProperty globalSoundVolume = new SimpleDoubleProperty(1.0);

    /**
     *
     * @return global sound volume property
     */
    public DoubleProperty globalSoundVolumeProperty() {
        return globalSoundVolume;
    }

    /**
     *
     * @return global sound volume
     */
    public double getGlobalSoundVolume() {
        return globalSoundVolumeProperty().get();
    }

    /**
     * Set global sound volume in the range [0..1],
     * where 0 = 0%, 1 = 100%
     *
     * @param volume
     */
    public void setGlobalSoundVolume(double volume) {
        globalSoundVolumeProperty().set(volume);
    }

    /**
     * Plays given sound based on its properties.
     *
     * @param sound
     */
    public void playSound(Sound sound) {
        if (!activeSounds.contains(sound))
            activeSounds.add(sound);
        sound.clip.volumeProperty().bind(globalSoundVolumeProperty());
        sound.clip.play();
    }

    /**
     * Stops playing given sound.
     *
     * @param sound
     */
    public void stopSound(Sound sound) {
        activeSounds.remove(sound);
        sound.clip.stop();
    }

    /**
     * Stops playing all sounds.
     */
    public void stopAllSounds() {
        for (Iterator<Sound> it = activeSounds.iterator(); it.hasNext(); ) {
            it.next().clip.stop();
            it.remove();
        }
    }

    /**
     * Plays given music based on its properties.
     *
     * @param music
     */
    public void playMusic(Music music) {
        if (!activeMusic.contains(music))
            activeMusic.add(music);
        music.mediaPlayer.volumeProperty().bind(globalMusicVolumeProperty());
        music.mediaPlayer.play();
    }

    /**
     * Pauses given music if it was previously started with {@link #playSound(Sound)}.
     * It can then be restarted by {@link #resumeMusic(Music)}.
     *
     * @param music
     */
    public void pauseMusic(Music music) {
        if (activeMusic.contains(music))
            music.mediaPlayer.pause();
    }

    /**
     * Resumes previously paused {@link #pauseMusic(Music)} music.
     *
     * @param music
     */
    public void resumeMusic(Music music) {
        if (activeMusic.contains(music))
            music.mediaPlayer.play();
    }

    /**
     * Stops currently playing music. It cannot be restarted
     * using {@link #resumeMusic(Music)}. The music object needs
     * to be started again by {@link #playMusic(Music)}.
     *
     * @param music
     */
    public void stopMusic(Music music) {
        if (activeMusic.contains(music)) {
            activeMusic.remove(music);
            music.mediaPlayer.stop();
        }
    }

    /**
     * Pauses all currently playing music. These can be
     * resumed using {@link #resumeAllMusic()}.
     */
    public void pauseAllMusic() {
        activeMusic.forEach(music -> music.mediaPlayer.pause());
    }

    /**
     * Resumes all currently paused music.
     */
    public void resumeAllMusic() {
        activeMusic.forEach(music -> music.mediaPlayer.play());
    }

    /**
     * Stops all currently playing music. The music cannot be restarted
     * by calling {@link #resumeAllMusic()}. Each music object will need
     * to be started by {@link #playMusic(Music)}.
     */
    public void stopAllMusic() {
        for (Iterator<Music> it = activeMusic.iterator(); it.hasNext(); ) {
            it.next().mediaPlayer.stop();
            it.remove();
        }
    }

    @Override
    protected void onUpdate(long now) {
        activeSounds.removeIf(sound -> !sound.clip.isPlaying());
        activeMusic.removeIf(music -> music.mediaPlayer.getStatus() == Status.STOPPED);
    }
}