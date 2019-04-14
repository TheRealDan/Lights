package me.therealdan.lights.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import me.therealdan.lights.fixtures.Channel;
import me.therealdan.lights.fixtures.ModelDesign;
import me.therealdan.lights.fixtures.Profile;
import me.therealdan.lights.main.Lights;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.renderer.Task;

public class ProfileEditor implements Visual {

    // TODO - Finish Profile editor; Channels, Models and how they interact

    private Profile profile;
    private Section section;

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

        if (profile == null) return true;

        // PROFILE OPTIONS
        width = WIDTH / 4;
        renderer.box(x, y, width, cellHeight, Lights.color.DARK_BLUE, "Profile Options", Task.TextPosition.CENTER);
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, isEditing(Section.NAME) ? Lights.color.DARK_RED : Lights.color.MEDIUM, "Name: " + profile.getName());
        if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.mouse.leftClicked()) edit(Section.NAME);
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, isEditing(Section.PHYSICAL_CHANNELS) ? Lights.color.DARK_RED : Lights.color.MEDIUM, "Physical Channels: " + profile.getPhysicalChannels());
        if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.mouse.leftClicked()) edit(Section.PHYSICAL_CHANNELS);
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, isEditing(Section.VIRTUAL_CHANNELS) ? Lights.color.DARK_RED : Lights.color.MEDIUM, "Virtual Channels: " + profile.getVirtualChannels());
        if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.mouse.leftClicked()) edit(Section.VIRTUAL_CHANNELS);
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, isEditing(Section.MODEL) ? Lights.color.DARK_RED : Lights.color.MEDIUM, "Model: " + profile.countModels());
        if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.mouse.leftClicked()) edit(Section.MODEL);
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, Lights.color.YELLOW, "Save & Close");
        if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.mouse.leftClicked()) {
            Lights.openMainView();
            return false;
        }
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, Lights.color.RED, "Delete");
        if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.mouse.leftClicked()) {
            // TODO - Adding delete functionality is going to require a little rework which needs to happen anyway
        }
        y -= cellHeight;

        x += width;
        y = Y - cellHeight;

        // PHYSICAL CHANNELS
        if (isEditing(Section.PHYSICAL_CHANNELS)) {
            width = WIDTH / 4;
            renderer.box(x, y, width, cellHeight, Lights.color.DARK_BLUE, "Physical Channels: " + profile.getPhysicalChannels(), Task.TextPosition.CENTER);
            y -= cellHeight;
            // TODO - Format this properly
            y = Y - cellHeight;
        }

        // VIRTUAL CHANNELS
        if (isEditing(Section.VIRTUAL_CHANNELS)) {
            width = WIDTH / 4;
            renderer.box(x, y, width, cellHeight, Lights.color.DARK_BLUE, "Virtual Channels: " + profile.getVirtualChannels(), Task.TextPosition.CENTER);
            y -= cellHeight;
            for (Channel channel : profile.channels()) {
                // TODO - Format this properly
                renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, channel.getType().getCategory().getName() + " > " + channel.getType().getName() + ": " + channel.addressOffsets().toString());
                y -= cellHeight;
            }
            x += width;
            y = Y - cellHeight;
        }

        // MODEL
        if (isEditing(Section.MODEL)) {
            width = WIDTH / 4;
            renderer.box(x, y, width, cellHeight, Lights.color.DARK_BLUE, "Model: " + profile.countModels(), Task.TextPosition.CENTER);
            y -= cellHeight;
            for (ModelDesign modelDesign : profile.getModelDesigns()) {
                renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, modelDesign.toString());
                y -= cellHeight;
            }
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
        switch (keycode) {
            case Input.Keys.ESCAPE:
                Lights.openMainView();
                return false;
        }

        return true;
    }

    public void edit(Profile profile) {
        this.profile = profile;
    }

    private void edit(Section section) {
        this.section = section;
    }

    private boolean isEditing(Section section) {
        return section.equals(this.section);
    }

    public enum Section {
        NAME, PHYSICAL_CHANNELS, VIRTUAL_CHANNELS, MODEL;
    }
}