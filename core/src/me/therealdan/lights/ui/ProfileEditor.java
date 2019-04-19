package me.therealdan.lights.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import me.therealdan.lights.fixtures.Channel;
import me.therealdan.lights.fixtures.ModelDesign;
import me.therealdan.lights.fixtures.MutableProfile;
import me.therealdan.lights.fixtures.Profile;
import me.therealdan.lights.main.Lights;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.renderer.Task;

public class ProfileEditor implements Visual {

    // TODO - Finish Profile editor; Channels, Models and how they interact

    private Profile profile;
    private MutableProfile mutableProfile;

    private Section section;

    private Channel channel;
    private ModelDesign modelDesign;

    @Override
    public boolean draw(Renderer renderer) {
        float X = 0;
        float Y = Gdx.graphics.getHeight();
        float WIDTH = Gdx.graphics.getWidth();
        float cellHeight = 30;

        float x = X;
        float y = Y;
        float width = WIDTH;

        renderer.box(x, y, width, cellHeight, Lights.color.DARK_BLUE, "Profile Editor", Task.TextPosition.CENTER);
        y -= cellHeight;

        if (getMutableProfile() == null) return true;

        // PROFILE OPTIONS
        width = WIDTH / 4;
        renderer.box(x, y, width, cellHeight, Lights.color.DARK_BLUE, "Profile Options", Task.TextPosition.CENTER);
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, isEditing(Section.NAME) ? Lights.color.DARK_RED : Lights.color.MEDIUM, "Name: " + getMutableProfile().getName());
        if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.mouse.leftClicked()) edit(Section.NAME);
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, isEditing(Section.PHYSICAL_CHANNELS) ? Lights.color.DARK_RED : Lights.color.MEDIUM, "Physical Channels: " + getMutableProfile().getPhysicalChannels());
        if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.mouse.leftClicked()) edit(Section.PHYSICAL_CHANNELS);
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, isEditing(Section.VIRTUAL_CHANNELS) ? Lights.color.DARK_RED : Lights.color.MEDIUM, "Virtual Channels: " + getMutableProfile().getVirtualChannels());
        if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.mouse.leftClicked()) edit(Section.VIRTUAL_CHANNELS);
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, isEditing(Section.MODEL) ? Lights.color.DARK_RED : Lights.color.MEDIUM, "Model: " + getMutableProfile().countModels());
        if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.mouse.leftClicked()) edit(Section.MODEL);
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, Lights.color.YELLOW, "Save Changes");
        if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.mouse.leftClicked()) {
            profile.update(getMutableProfile());
            return false;
        }
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, Lights.color.YELLOW, "Clear Changes");
        if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.mouse.leftClicked()) {
            edit(profile);
            return false;
        }
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, Lights.color.YELLOW, "Close Editor");
        if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.mouse.leftClicked()) {
            Lights.openMainView();
            return false;
        }
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, Lights.color.RED, "Delete Profile");
        if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.mouse.leftClicked()) {
            Profile.delete(getMutableProfile());
            edit((Profile) null);
            Lights.openMainView();
            return false;
        }
        y -= cellHeight;

        x += width;
        y = Y - cellHeight;

        // PHYSICAL CHANNELS
        if (isEditing(Section.PHYSICAL_CHANNELS)) {
            width = WIDTH / 4;
            renderer.box(x, y, width, cellHeight, Lights.color.DARK_BLUE, "Physical Channels: " + getMutableProfile().getPhysicalChannels(), Task.TextPosition.CENTER);
            y -= cellHeight;
            for (int offset = 0; offset < getMutableProfile().getPhysicalChannels(); offset++) {
                StringBuilder channels = new StringBuilder();
                for (Channel channel : getMutableProfile().channels()) {
                    for (int addressOffset : channel.addressOffsets()) {
                        if (addressOffset == offset) {
                            channels.append(", ").append(channel.getType().getName());
                        }
                    }
                }
                renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, offset + " - " + channels.toString().replaceFirst(", ", ""));
                y -= cellHeight;
            }
            x += width;
            y = Y - cellHeight;
        }

        // VIRTUAL CHANNELS
        if (isEditing(Section.VIRTUAL_CHANNELS)) {
            width = WIDTH / 4;
            renderer.box(x, y, width, cellHeight, Lights.color.DARK_BLUE, "Virtual Channels: " + getMutableProfile().getVirtualChannels(), Task.TextPosition.CENTER);
            y -= cellHeight;
            for (Channel channel : getMutableProfile().channels()) {
                renderer.box(x, y, width, cellHeight, channel.equals(getSelectedChannel()) ? Lights.color.DARK_RED : Lights.color.MEDIUM, channel.getType().getName() + " - " + channel.addressOffsetsAsString());
                if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.mouse.leftClicked()) {
                    select(channel);
                }
                y -= cellHeight;
            }

            if (hasChannelSelected()) {
                renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, Lights.color.YELLOW, "Change Type");
                if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.mouse.leftClicked(500)) {
                    getMutableProfile().changeType(getSelectedChannel());
                }
                y -= cellHeight;
            }

            renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, Lights.color.GREEN, "Add Channel");
            if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.mouse.leftClicked()) {
                getMutableProfile().addChannel(Channel.DEFAULT_TYPE);
            }
            y -= cellHeight;

            if (hasChannelSelected()) {
                renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, Lights.color.RED, "Delete Channel");
                if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.mouse.leftClicked() && Lights.keyboard.isShift()) {
                    getMutableProfile().removeChannel(getSelectedChannel());
                    select((Channel) null);
                }
                y -= cellHeight;
            }

            x += width;
            y = Y - cellHeight;
        }

        // MODELS
        if (isEditing(Section.MODEL)) {
            width = WIDTH / 4;
            renderer.box(x, y, width, cellHeight, Lights.color.DARK_BLUE, "Models: " + getMutableProfile().countModels(), Task.TextPosition.CENTER);
            y -= cellHeight;
            for (ModelDesign modelDesign : getMutableProfile().getModelDesigns()) {
                renderer.box(x, y, width, cellHeight, modelDesign.equals(getSelectedModelDesign()) ? Lights.color.DARK_RED : Lights.color.MEDIUM, modelDesign.toString());
                if (Lights.mouse.contains(x, y, width, cellHeight)) {
                    if (Lights.mouse.leftClicked()) {
                        select(modelDesign);
                    }
                }
                y -= cellHeight;
            }

            renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, Lights.color.GREEN, "Add Model");
            if (Lights.mouse.contains(x, y, width, cellHeight)) {
                if (Lights.mouse.leftClicked(500)) {
                    getMutableProfile().addModelDesign(new ModelDesign(1));
                }
            }
            y -= cellHeight;

            if (hasModelDesignSelected()) {
                renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, Lights.color.RED, "Delete Model");
                if (Lights.mouse.contains(x, y, width, cellHeight)) {
                    if (Lights.mouse.leftClicked(500)) {
                        getMutableProfile().removeModelDesign(getSelectedModelDesign());
                        select((ModelDesign) null);
                    }
                }
            }
            y -= cellHeight;

            x += width;
            y = Y - cellHeight;
        }

        // MODEL PREVIEW
        width = WIDTH - x;
        renderer.box(x, y, width, cellHeight, Lights.color.DARK_BLUE, "Model Preview", Task.TextPosition.CENTER);

        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        boolean shift = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);

        switch (keycode) {
            case Input.Keys.ESCAPE:
                Lights.openMainView();
                return false;
        }

        if (isEditing(Section.NAME)) {
            switch (keycode) {
                case Input.Keys.BACKSPACE:
                    if (getMutableProfile().getName().length() > 0)
                        getMutableProfile().rename(getMutableProfile().getName().substring(0, getMutableProfile().getName().length() - 1));
                    if (shift) getMutableProfile().rename("");
                    return false;
                case Input.Keys.SPACE:
                    getMutableProfile().rename(getMutableProfile().getName() + " ");
                    return false;
                default:
                    String string = Input.Keys.toString(keycode);
                    if ("ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".contains(string)) {
                        if (!shift) string = string.toLowerCase();
                        getMutableProfile().rename(getMutableProfile().getName() + string);
                    }
                    return false;
            }
        }

        if (isEditing(Section.VIRTUAL_CHANNELS) && hasChannelSelected()) {
            switch (keycode) {
                case Input.Keys.BACKSPACE:
                    if (getSelectedChannel().countAddressOffsets() == 0) return false;
                    if (Integer.toString(getSelectedChannel().getLastAddressOffset()).length() == 1) {
                        getMutableProfile().removeLastOffset(getSelectedChannel());
                    } else {
                        String lastOffset = Integer.toString(getSelectedChannel().getLastAddressOffset());
                        getMutableProfile().removeLastOffset(getSelectedChannel());
                        getMutableProfile().addOffset(getSelectedChannel(), Integer.parseInt(lastOffset.substring(0, lastOffset.length() - 1)));
                    }
                    return false;
                case Input.Keys.ENTER:
                    getMutableProfile().addOffset(getSelectedChannel(), 0);
                    return false;
                default:
                    if (getSelectedChannel().countAddressOffsets() == 0) return false;
                    String string = Input.Keys.toString(keycode);
                    if ("1234567890".contains(string)) {
                        string = getSelectedChannel().getLastAddressOffset() + string;
                        getMutableProfile().removeLastOffset(getSelectedChannel());
                        getMutableProfile().addOffset(getSelectedChannel(), Integer.parseInt(string));

                        if (getSelectedChannel().getLastAddressOffset() > 511) {
                            getMutableProfile().removeLastOffset(getSelectedChannel());
                            getMutableProfile().addOffset(getSelectedChannel(), 511);
                        }
                    }
                    return false;
            }
        }

        return true;
    }

    private void select(Channel channel) {
        this.channel = channel;
    }

    private void select(ModelDesign modelDesign) {
        this.modelDesign = modelDesign;
    }

    public void edit(Profile profile) {
        this.profile = profile;
        this.mutableProfile = profile != null ? new MutableProfile(profile) : null;
        this.channel = null;
    }

    private void edit(Section section) {
        this.section = section;
    }

    private boolean isEditing(Section section) {
        return section.equals(this.section);
    }

    public MutableProfile getMutableProfile() {
        return mutableProfile;
    }

    private boolean hasChannelSelected() {
        return getSelectedChannel() != null;
    }

    private Channel getSelectedChannel() {
        return channel;
    }

    private boolean hasModelDesignSelected() {
        return getSelectedModelDesign() != null;
    }

    private ModelDesign getSelectedModelDesign() {
        return modelDesign;
    }

    public enum Section {
        NAME, PHYSICAL_CHANNELS, VIRTUAL_CHANNELS, MODEL;
    }
}