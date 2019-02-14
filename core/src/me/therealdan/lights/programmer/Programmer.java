package me.therealdan.lights.programmer;

import me.therealdan.lights.fixtures.Channel;
import me.therealdan.lights.fixtures.Fixture;
import me.therealdan.lights.fixtures.Group;
import me.therealdan.lights.ui.views.live.ui.PatchUI;
import me.therealdan.lights.ui.views.live.ui.SequencesUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Programmer {

    private static List<Fixture> selectedFixtures = new ArrayList<>();
    private static List<Frame> selectedFrames = new ArrayList<>();

    private static Sequence sequence = null;

    public static void edit(Sequence sequence) {
        Programmer.sequence = sequence;
    }

    public static void set(float address, float value) {
        for (Fixture fixture : PatchUI.fixtures()) {
            HashMap<String, Integer> parameters = new HashMap<>();
            for (Channel channel : fixture.channels()) {
                parameters.put(channel.getType().toString(), parameters.getOrDefault(channel.getType().toString(), 0) + 1);
                int parameter = parameters.get(channel.getType().toString());

                for (int addressOffset : channel.addressOffsets()) {
                    int physical = fixture.getAddress() + addressOffset;
                    if (physical == address) {
                        set(fixture, channel.getType(), value, parameter);
                    }
                }
            }
        }
    }

    public static void set(Fixture fixture, Channel.Type channelType, float value, int... parameters) {
        for (int parameter : parameters)
            getSequence().getActiveFrame().set(fixture, channelType, value, parameter);
    }

    public static void save() {
        SequencesUI.add(getSequence().clone());
    }

    public static void clear() {
        sequence = new Sequence("New Sequence");
        getSequence().add(new Frame());
    }

    public static void select(Fixture fixture) {
        if (!selectedFixtures.contains(fixture)) selectedFixtures.add(fixture);
    }

    public static void select(Group group) {
        for (Fixture fixture : group.fixtures())
            select(fixture);
    }

    public static void select(Frame frame) {
        selectedFrames.add(frame);
    }

    public static void deselect(Fixture fixture) {
        selectedFixtures.remove(fixture);
    }

    public static void deselect(Group group) {
        for (Fixture fixture : group.fixtures())
            deselect(fixture);
    }

    public static void deselect(Frame frame) {
        selectedFrames.remove(frame);
    }

    public static void deselectAllFrames() {
        selectedFrames.clear();
    }

    public static int countSelectedFixtures() {
        return selectedFixtures.size();
    }

    public static int countSelectedFrames() {
        return selectedFrames.size();
    }

    public static float getValue(int address) {
        return getSequence().getActiveFrame().getValue(address);
    }

    public static float getValue(Fixture fixture, Channel.Type channelType, int parameter) {
        return getSequence().getActiveFrame().getValue(fixture, channelType, parameter);
    }

    public static boolean hasValue(int address) {
        return getSequence().getActiveFrame().hasValue(address);
    }

    public static boolean hasValue(Fixture fixture, Channel.Type channelType, int parameter) {
        return getSequence().getActiveFrame().hasValue(fixture, channelType, parameter);
    }

    public static boolean isSelected(Fixture fixture) {
        return selectedFixtures.contains(fixture);
    }

    public static boolean isSelected(Group group) {
        for (Fixture fixture : group.fixtures())
            if (!isSelected(fixture))
                return false;

        return true;
    }

    public static boolean isSelected(Frame frame) {
        return selectedFrames.contains(frame);
    }

    public static Sequence getSequence() {
        if (sequence == null) clear();
        return sequence;
    }

    public static Frame getFirstSelectedFrame() {
        if (countSelectedFrames() > 0) return getSelectedFrames().get(0);
        return null;
    }

    public static List<Fixture> getSelectedFixtures() {
        return new ArrayList<>(selectedFixtures);
    }

    public static List<Frame> getSelectedFrames() {
        return new ArrayList<>(selectedFrames);
    }
}