package me.therealdan.lights.programmer;

import me.therealdan.lights.fixtures.Channel;
import me.therealdan.lights.fixtures.Fixture;
import me.therealdan.lights.fixtures.Group;
import me.therealdan.lights.ui.views.Sequences;
import me.therealdan.lights.ui.views.live.ui.PatchUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class Programmer {

    private static List<Fixture> selectedFixtures = new ArrayList<>();
    private static LinkedHashMap<Channel.Type, List<Integer>> selectedParameters = new LinkedHashMap<>();

    private static Sequence sequence = null;

    public static void set(Sequence sequence) {
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
                        sequence.getActiveFrame().set(fixture, channel.getType(), value, parameter);
                    }
                }
            }
        }
    }

    public static void save() {
        Sequences.add(getSequence().clone());
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

    public static void select(Channel.Type channelType, int parameter) {
        if (!selectedParameters.containsKey(channelType))
            selectedParameters.put(channelType, new ArrayList<>());
        selectedParameters.get(channelType).add(parameter);
    }

    public static void deselect(Fixture fixture) {
        selectedFixtures.remove(fixture);
    }

    public static void deselect(Group group) {
        for (Fixture fixture : group.fixtures())
            deselect(fixture);
    }

    public static void deselect(Channel.Type channelType) {
        selectedParameters.get(channelType).clear();
    }

    public static void deselect(Channel.Type channelType, int parameter) {
        List<Integer> selectedParameters = getSelectedParameters(channelType);
        deselect(channelType);

        for (int selectedParameter : selectedParameters)
            if (selectedParameter != parameter)
                select(channelType, selectedParameter);
    }

    public static void deselectAllFixtures() {
        for (Fixture fixture : getSelectedFixtures())
            deselect(fixture);
    }

    public static void deselectAllGroups() {
        for (Group group : PatchUI.groups())
            deselect(group);
    }

    public static void deselectAllParameters() {
        for (Channel.Type channelType : Channel.Type.values())
            selectedParameters.get(channelType).clear();
    }

    public static float getValue(int address) {
        return sequence.getActiveFrame().getValue(address);
    }

    public static boolean hasValue(int address) {
        return getSequence().getActiveFrame().hasValue(address);
    }

    public static boolean hasFixturesSelected() {
        return selectedFixtures.size() > 0;
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

    public static boolean isSelected(Channel.Type channelType) {
        return getSelectedParameters(channelType).size() > 0;
    }

    public static boolean isSelected(Channel.Type channelType, int parameter) {
        return getSelectedParameters(channelType).contains(parameter);
    }

    public static Sequence getSequence() {
        if (sequence == null) clear();
        return sequence;
    }

    public static List<Fixture> getSelectedFixtures() {
        return new ArrayList<>(selectedFixtures);
    }

    public static List<Channel.Type> getSelectedChannelTypes() {
        List<Channel.Type> channelTypes = new ArrayList<>();
        for (Channel.Type channelType : Channel.Type.values())
            if (getSelectedParameters(channelType).size() > 0)
                channelTypes.add(channelType);
        return channelTypes;
    }

    public static List<Integer> getSelectedParameters(Channel.Type channelType) {
        if (!selectedParameters.containsKey(channelType))
            selectedParameters.put(channelType, new ArrayList<>());
        return selectedParameters.get(channelType);
    }

    public static LinkedHashMap<Channel.Type, Integer> getAvailableParameters() {
        LinkedHashMap<Channel.Type, Integer> parameters = new LinkedHashMap<>();

        for (Fixture fixture : getSelectedFixtures()) {
            LinkedHashMap<Channel.Type, Integer> tempParameters = new LinkedHashMap<>();
            for (Channel channel : fixture.channels())
                tempParameters.put(channel.getType(), tempParameters.getOrDefault(channel.getType(), 0) + 1);

            for (Channel.Type channelType : tempParameters.keySet())
                if (tempParameters.get(channelType) > parameters.getOrDefault(channelType, 0))
                    parameters.put(channelType, tempParameters.get(channelType));
        }

        return parameters;
    }
}