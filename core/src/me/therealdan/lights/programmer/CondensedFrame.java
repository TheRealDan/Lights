package me.therealdan.lights.programmer;

import me.therealdan.lights.controllers.Fader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CondensedFrame {

    private HashMap<Task, Long> tasks = new HashMap<>();

    public CondensedFrame() {

    }

    public void calculate(CondensedFrame targetCondensedFrame, CondensedFrame previousCondensedFrame, long timestamp) {
        long timepassed = System.currentTimeMillis() - timestamp;
        List<Task> fullListTasks = tasks();
        for (Task targetTask : targetCondensedFrame.tasks()) {
            boolean gotIt = false;
            for (Task task : tasks()) {
                if (task.equals(targetTask, true)) {
                    gotIt = true;
                    break;
                }
            }
            if (!gotIt) fullListTasks.add(targetTask);
        }

        for (Task task : fullListTasks) {
            Task targetTask = null;
            for (Task eachTargetTask : targetCondensedFrame.tasks()) {
                if (task.equals(eachTargetTask, true)) {
                    targetTask = eachTargetTask;
                    break;
                }
            }
            if (targetTask == null) {
                targetTask = task.clone();
                targetTask.setValue(0);
            }

            Task previousTask = null;
            for (Task eachPreviousTask : previousCondensedFrame.tasks()) {
                if (task.equals(eachPreviousTask, true)) {
                    previousTask = eachPreviousTask;
                    break;
                }
            }
            if (previousTask == null) {
                previousTask = task.clone();
                previousTask.setValue(0);
            }

            Task currentTask = null;
            for (Task eachCurrentTask : tasks()) {
                if (targetTask.equals(eachCurrentTask, true)) {
                    currentTask = eachCurrentTask;
                    break;
                }
            }
            if (currentTask == null) {
                currentTask = targetTask.clone();
                currentTask.setValue(0);
            }

            double direction = targetTask.getValue() - previousTask.getValue();
            double distance = Math.abs(direction);

            double fadeTime = targetCondensedFrame.getFadeTime(targetTask);

            double percentage = 1;
            if (fadeTime > 0) percentage = timepassed > fadeTime ? 1 : timepassed / fadeTime;

            double distanceCovered = percentage * distance;

            if (direction > 0) {
                currentTask.setValue(previousTask.getValue() + (float) distanceCovered);
                tasks.put(currentTask, (long) fadeTime);
            } else if (direction < 0) {
                task.setValue(previousTask.getValue() - (float) distanceCovered);
                tasks.put(currentTask, (long) fadeTime);
            } else {
                tasks.put(currentTask, (long) fadeTime);
            }
        }
    }

    public void merge(Fader fader) {
        Frame frame = fader.getSequence().getActiveFrame();
        for (Task task : frame.tasks()) {
            boolean foundMatch = false;
            for (Task existing : tasks()) {
                if (task.equals(existing, true)) {
                    foundMatch = true;
                    switch (fader.getType()) {
                        case MASTER:
                            existing.setValue(existing.getValue() * fader.getValue());
                            break;
                        case INHIBITOR:
                            existing.setValue(Math.min(existing.getValue(), existing.getValue() * fader.getValue()));
                            break;
                        case AMBIENT:
                            existing.setValue(Math.max(existing.getValue(), existing.getValue() * fader.getValue()));
                            break;
                        case OVERRIDE:
                            if (fader.getValue() > 0) existing.setValue(existing.getValue() * fader.getValue());
                            break;
                    }
                    break;
                }
            }
            if (!foundMatch) {
                Task newTask = task.clone();
                switch (fader.getType()) {
                    case MASTER:
                        newTask.setValue(newTask.getValue() * fader.getValue());
                        break;
                    case INHIBITOR:
                        newTask.setValue(Math.min(newTask.getValue(), newTask.getValue() * fader.getValue()));
                        break;
                    case AMBIENT:
                        newTask.setValue(Math.max(newTask.getValue(), newTask.getValue() * fader.getValue()));
                        break;
                    case OVERRIDE:
                        if (fader.getValue() > 0) newTask.setValue(newTask.getValue() * fader.getValue());
                        break;
                }
                tasks.put(newTask, 0L);
            }
        }
    }

    public void merge(Sequence sequence) {
        merge(sequence.getActiveFrame());
    }

    public void merge(Frame frame) {
        for (Task task : frame.tasks()) {
            boolean foundMatch = false;
            for (Task existing : tasks()) {
                if (task.equals(existing, true)) {
                    foundMatch = true;
                    existing.setValue(existing.getValue() * (task.getValue() / 255.0f));
                    tasks.put(existing, frame.getFadeTime());
                    break;
                }
            }
            if (!foundMatch) {
                tasks.put(task.clone(), frame.getFadeTime());
            }
        }
    }

    public int getValue(int address) {
        boolean found = false;
        float value = 255f;
        for (Task task : tasks()) {
            if (task.getAddresses().contains(address)) {
                found = true;
                value *= task.getValue() / 255f;
            }
        }

        if (!found) return 0;
        return (int) value;
    }

    public long getFadeTime(Task task) {
        if (!tasks.containsKey(task)) return 0;
        return tasks.get(task);
    }

    public boolean equals(CondensedFrame condensedFrame) {
        for (Task task : tasks()) {
            boolean foundMatch = false;
            for (Task task1 : condensedFrame.tasks()) {
                if (task.equals(task1)) {
                    foundMatch = true;
                    break;
                }
            }
            if (!foundMatch) return false;
        }

        return true;
    }

    @Override
    public CondensedFrame clone() {
        CondensedFrame condensedFrame = new CondensedFrame();
        for (Task task : tasks())
            condensedFrame.tasks.put(task.clone(), getFadeTime(task));
        return condensedFrame;
    }

    public List<Task> tasks() {
        return new ArrayList<>(tasks.keySet());
    }
}