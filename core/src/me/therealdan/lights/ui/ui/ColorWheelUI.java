package me.therealdan.lights.ui.ui;

import com.badlogic.gdx.graphics.Color;
import me.therealdan.lights.main.Lights;
import me.therealdan.lights.programmer.Frame;
import me.therealdan.lights.programmer.Programmer;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.renderer.Task;
import me.therealdan.lights.ui.UIHandler;
import me.therealdan.lights.util.Util;

public class ColorWheelUI implements UI {

    public ColorWheelUI() {
        setWidth(250);
        setHeight(280);
    }

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        UI.super.draw(renderer, X, Y, WIDTH, HEIGHT);
        if (containsMouse()) UIHandler.setSection(UIHandler.Section.COLOR_WHEEL);
        boolean interacted = false;

//        List<Channel.Type> channelTypes = Programmer.getSelectedChannelTypes();
//        if (!(channelTypes.contains(Channel.Type.RED) && channelTypes.contains(Channel.Type.GREEN) && channelTypes.contains(Channel.Type.BLUE))) return interacted;

        Frame frame = Programmer.getSequence().getActiveFrame();

        float cellHeight = 30;

        float x = getX();
        float y = getY();
        float width = getWidth();
        float height = getHeight() - cellHeight;

        Util.box(renderer, x, y, width, cellHeight, Lights.color.DARK_BLUE, setWidth(renderer, "Colour Wheel"));

        float third = width / 3f;
        float seventh = width / 7f;
        for (float xp = 0; xp < width - 1; xp++) {
            for (float yp = 1; yp < height; yp++) {

                float var_X = xp / 100;
                float var_Y = yp / 100;
                float var_Z = 0 / 100;

                float var_R = var_X * 3.2406f + var_Y * -1.5372f + var_Z * -0.4986f;
                float var_G = var_X * -0.9689f + var_Y * 1.8758f + var_Z * 0.0415f;
                float var_B = var_X * 0.0557f + var_Y * -0.2040f + var_Z * 1.0570f;

                if (var_R > 0.0031308) {
                    var_R = 1.055f * (float) Math.pow(var_R, (1.0 / 2.4)) - 0.055f;
                } else {
                    var_R = 12.92f * var_R;
                }
                if (var_G > 0.0031308) {
                    var_G = 1.055f * (float) Math.pow(var_G, (1 / 2.4)) - 0.055f;
                } else {
                    var_G = 12.92f * var_G;
                }
                if (var_B > 0.0031308) {
                    var_B = 1.055f * (float) Math.pow(var_B, (1 / 2.4)) - 0.055f;
                } else {
                    var_B = 12.92f * var_B;
                }

                float red = var_R * 255f;
                float green = var_G * 255f;
                float blue = var_B * 255f;

                Color color = new Color(red, green, blue, 1);
                renderer.queue(new Task(x + xp, y + yp - height - cellHeight).rect(1, 1).setColor(color));
            }
        }

        return interacted;
    }
}