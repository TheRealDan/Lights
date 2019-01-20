package me.therealdan.lights.programmer;

import me.therealdan.lights.ui.views.Live;

import java.util.ArrayList;
import java.util.List;

public class Sequence {

    private List<Frame> frames = new ArrayList<>();

    private String name;
    private int currentFrame = 0;
    private int lastFrame = 0;

    private boolean loop;
    private boolean clear;
    private boolean globalFrameTime;
    private boolean globalFadeTime;

    private boolean useTempo = false;
    private boolean playing = false;

    private long started = System.currentTimeMillis();

    public Sequence(String name) {
        this(name, false, false, false, false);
    }

    public Sequence(String name, boolean loop, boolean clear, boolean globalFrameTime, boolean globalFadeTime) {
        this.name = name;
        this.loop = loop;
        this.clear = clear;
        this.globalFrameTime = globalFrameTime;
        this.globalFadeTime = globalFadeTime;
    }

    public void loop(boolean enabled) {
        this.loop = enabled;
        if (enabled) this.clear = false;
    }

    public void clear(boolean enabled) {
        this.clear = enabled;
        if (enabled) this.loop = false;
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

    public void toggleClear() {
        clear(!doesClear());
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

        run();
    }

    public void run() {
        for (Task task : getActiveFrame().tasks()) {
            float value = Live.getLastFrame().getValue(task.getFixture(), task.getChannelType(), task.getParameter());
            Frame.setPrevious(task.getFixture(), task.getChannelType(), value, task.getParameter());
        }
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

    public boolean doesClear() {
        return clear;
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

    public boolean onLastFrame() {
        return currentFrame == length() - 1;
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
            if (doesLoop() && !doesClear()) {
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
        Sequence sequence = new Sequence(getName(), doesLoop(), doesClear(), globalFrameTime(), globalFadeTime());
        for (Frame frame : frames())
            sequence.add(frame.clone());
        return sequence;
    }
}