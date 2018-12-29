package me.therealdan.lights.programmer;

import java.util.ArrayList;
import java.util.List;

public class Sequence {

    private List<Frame> frames = new ArrayList<>();

    private String name;
    private int currentFrame = 0;
    private int lastFrame = 0;

    private boolean loop;
    private boolean globalFrameTime;
    private boolean globalFadeTime;

    private boolean useTempo = false;
    private boolean playing = false;

    private long started = System.currentTimeMillis();

    public Sequence(String name) {
        this(name, false, false, false);
    }

    public Sequence(String name, boolean loop, boolean globalFrameTime, boolean globalFadeTime) {
        this.name = name;
        this.loop = loop;
        this.globalFrameTime = globalFrameTime;
        this.globalFadeTime = globalFadeTime;
    }

    public void loop(boolean enabled) {
        this.loop = enabled;
    }

    public void globalFrameTime(boolean enabled) {
        this.globalFrameTime = enabled;
    }

    public void globalFadeTime(boolean enabled) {
        this.globalFadeTime = enabled;
    }

    public void useTempo(boolean enabled) {
        this.useTempo = enabled;
    }

    public void toggleLoop() {
        loop(!doesLoop());
    }

    public void toggleGlobalFrameTime() {
        globalFrameTime(!globalFrameTime());
    }

    public void toggleGlobalFadeTime() {
        globalFadeTime(!globalFadeTime());
    }

    public void toggleUseTempo() {
        useTempo(!useTempo());
    }

    public void first() {
        set(0);
    }

    public void last() {
        set(length() - 1);
    }

    public void previous() {
        set(this.currentFrame - 1);
    }

    public void next() {
        set(this.currentFrame + 1);
    }

    public void play() {
        first();
        started = System.currentTimeMillis();
        playing = true;
    }

    public void stop() {
        playing = false;
    }

    public void set(int frame) {
        this.currentFrame = frame;
    }

    public void add(Frame frame) {
        frames.add(frame);
    }

    public void delete(int index) {
        frames.remove(index);
    }

    public void rename(String name) {
        this.name = name;
    }

    public void resetLastFrame() {
        this.lastFrame = -1;
    }

    public boolean doesLoop() {
        return loop;
    }

    public boolean globalFrameTime() {
        return globalFrameTime;
    }

    public boolean globalFadeTime() {
        return globalFadeTime;
    }

    public boolean useTempo() {
        return useTempo;
    }

    public boolean isPlaying() {
        return playing;
    }

    public int length() {
        return frames.size();
    }

    public int getCurrentFrame() {
        if (isPlaying()) {
            if (frames().size() > currentFrame) {
                long timePassed = System.currentTimeMillis() - started;
                long frameEnd = 0;
                for (Frame frame : frames()) {
                    frameEnd += frame.getFrameTime();
                    if (frame == frames.get(currentFrame)) break;
                }
                if (timePassed >= frameEnd) {
                    next();
                }
            }
        }

        if (currentFrame >= length()) {
            if (doesLoop()) {
                first();
                started = System.currentTimeMillis();
            } else {
                last();
            }
        }
        if (currentFrame < 0) first();

        if (currentFrame != lastFrame)
            for (Frame frame : frames())
                frame.reset();

        lastFrame = currentFrame;
        return currentFrame;
    }

    public String getName() {
        return name;
    }

    public Frame getActiveFrame() {
        if (length() == 0) return new Frame();
        return frames.get(getCurrentFrame());
    }

    public List<Frame> frames() {
        return new ArrayList<>(frames);
    }

    @Override
    public Sequence clone() {
        Sequence sequence = new Sequence(getName(), doesLoop(), globalFrameTime(), globalFadeTime());
        for (Frame frame : frames())
            sequence.add(frame.clone());
        return sequence;
    }
}