package dev.therealdan.lights.programmer;

import dev.therealdan.lights.fixtures.fixture.profile.Channel;
import dev.therealdan.lights.fixtures.Fixture;
import dev.therealdan.lights.fixtures.Group;
import dev.therealdan.lights.ui.ui.SequencesUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Programmer {

    private static List<Fixture> selectedFixtures = new ArrayList<>();
    private static List<Frame> selectedFrames = new ArrayList<>();

    private static Sequence sequence = null;

    public static void edit(Sequence sequence) {
        Programmer.sequence = sequence.clone();
    }

    public static void set(float address, float value) {
        for (Fixture fixture : Fixture.fixtures()) {
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
            for (Frame frame : getSelectedFrames())
                frame.set(fixture, channelType, value, parameter);
    }

    public static void save() {
        SequencesUI.add(getSequence().clone());
    }

    public static void clear() {
        sequence = new Sequence("New Sequence");
        getSequence().add(new Frame());
        deselectAllFrames();
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

    public static float getActiveFrameValueFor(int address) {
        return getSequence().getActiveFrame().getValueFor(address);
    }

    public static float getSelectedFramesValueFor(Fixture fixture, Channel.Type channelType, int parameter) {
        for (Frame frame : getSelectedFrames())
            if (frame.hasValueFor(fixture, channelType, parameter))
                return frame.getValueFor(fixture, channelType, parameter);
        return 0;
    }

    public static boolean activeFrameHasValueFor(int address) {
        return getSequence().getActiveFrame().hasValueFor(address);
    }

    public static boolean selectedFramesHaveValueFor(Fixture fixture, Channel.Type channelType, int parameter) {
        for (Frame frame : getSelectedFrames())
            if (frame.hasValueFor(fixture, channelType, parameter))
                return true;
        return false;
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

    public static boolean selectedFixturesContain(Channel.Type channelType, int parameter) {
        for (Fixture fixture : getSelectedFixtures())
            if (fixture.hasChannel(channelType) && fixture.getParameters(channelType).contains(parameter))
                return true;
        return false;
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

    public static List<Channel.Category> availableChannelTypeCategories() {
        List<Channel.Category> categories = new ArrayList<>();
        for (Fixture fixture : getSelectedFixtures())
            for (Channel channel : fixture.channels())
                if (!categories.contains(channel.getType().getCategory()))
                    categories.add(channel.getType().getCategory());
        return categories;
    }

    public static List<Channel.Type> availableChannelTypes() {
        List<Channel.Type> channelTypes = new ArrayList<>();
        for (Fixture fixture : getSelectedFixtures())
            for (Channel channel : fixture.channels())
                if (!channelTypes.contains(channel.getType()))
                    channelTypes.add(channel.getType());
        return channelTypes;
    }

    public static List<Integer> availableParameters(Channel.Type channelType) {
        List<Integer> parameters = new ArrayList<>();
        for (Fixture fixture : getSelectedFixtures())
            for (int parameter : fixture.getParameters(channelType))
                if (!parameters.contains(parameter))
                    parameters.add(parameter);
        return parameters;
    }
}