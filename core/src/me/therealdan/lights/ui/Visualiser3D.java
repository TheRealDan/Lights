package me.therealdan.lights.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.IntIntMap;
import me.therealdan.lights.dmx.DMX;
import me.therealdan.lights.fixtures.Fixture;
import me.therealdan.lights.fixtures.Model;
import me.therealdan.lights.programmer.Programmer;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.settings.Control;
import me.therealdan.lights.settings.Setting;
import me.therealdan.lights.ui.ui.PatchUI;

public class Visualiser3D implements Visual {

    private PerspectiveCamera camera;

    private Environment environment;
    private ModelBatch modelBatch;

    private final IntIntMap keys = new IntIntMap();
    private float velocity = 5;
    private float fixtureVelocity = 0.1f;
    private float degreesPerPixel = 0.5f;
    private final Vector3 tmp = new Vector3();

    public Visualiser3D() {
        camera = new PerspectiveCamera(75, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0f, 0f, 10f);
        camera.lookAt(0f, 0f, 0f);
        camera.near = 0.1f;
        camera.far = 1000f;
        camera.update();

        modelBatch = new ModelBatch();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
        environment.add(new DirectionalLight().set(0.6f, 0.6f, 0.6f, 1f, 0.8f, 0.2f));

        for (Fixture fixture : PatchUI.fixtures()) {
            fixture.buildModels();
        }

        loadCameraPosition();
    }

    public void save() {
        saveCameraPosition();
    }

    private void loadCameraPosition() {
        if (Setting.byName(Setting.Name.REMEMBER_CAMERA_POSITION).isFalse()) return;

        FileHandle fileHandle = Gdx.files.local("Lights/Settings/Camera.txt");
        if (fileHandle.exists()) {
            boolean position = true;
            for (String line : fileHandle.readString().split("\\r?\\n")) {
                if (line.startsWith("Position:")) {
                    position = true;
                } else if (line.startsWith("Direction:")) {
                    position = false;
                } else {
                    float value = Float.parseFloat(line.substring(5));
                    (position ? camera.position : camera.direction).set(
                            line.startsWith("  X: ") ? value : (position ? camera.position : camera.direction).x,
                            line.startsWith("  Y: ") ? value : (position ? camera.position : camera.direction).y,
                            line.startsWith("  Z: ") ? value : (position ? camera.position : camera.direction).z
                    );
                }
            }
        }
    }

    private void saveCameraPosition() {
        FileHandle fileHandle = Gdx.files.local("Lights/Settings/Camera.txt");

        if (Setting.byName(Setting.Name.REMEMBER_CAMERA_POSITION).isFalse()) {
            if (fileHandle.exists()) fileHandle.delete();
            return;
        }

        fileHandle.writeString("", false);
        fileHandle.writeString("Position:\r\n", true);
        fileHandle.writeString("  X: " + camera.position.x + "\r\n", true);
        fileHandle.writeString("  Y: " + camera.position.y + "\r\n", true);
        fileHandle.writeString("  Z: " + camera.position.z + "\r\n", true);
        fileHandle.writeString("Direction:\r\n", true);
        fileHandle.writeString("  X: " + camera.direction.x + "\r\n", true);
        fileHandle.writeString("  Y: " + camera.direction.y + "\r\n", true);
        fileHandle.writeString("  Z: " + camera.direction.z + "\r\n", true);
    }

    private void mouseSelectFixtures() {
        if (UIHandler.getSection().equals(UIHandler.Section.VISUALISER3D)) {
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                Fixture fixture = getFixture(Gdx.input.getX(), Gdx.input.getY());
                if (fixture != null)
                    Programmer.select(fixture);
            }
        }
    }

    private void updateFixtureColors() {
        DMX visualiser = DMX.get("VISUALISER");
        for (Fixture fixture : PatchUI.fixtures()) {
            fixture.updateColor(visualiser);
        }
    }

    private void controls(float deltaTime) {
        boolean shift = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
        float velocity = this.velocity * (shift ? 10 : 1);

        if (Programmer.getSelectedFixtures().size() == 1) {
            Fixture fixture = Programmer.getSelectedFixtures().get(0);
            if (keys.containsKey(Control.byName(Control.Name.CAMERA_FORWARD).getKeycode()))
                fixture.move(0, 0, -fixtureVelocity);
            if (keys.containsKey(Control.byName(Control.Name.CAMERA_BACKWARD).getKeycode()))
                fixture.move(0, 0, fixtureVelocity);
            if (keys.containsKey(Control.byName(Control.Name.CAMERA_STRAFE_LEFT).getKeycode()))
                fixture.move(-fixtureVelocity, 0, 0);
            if (keys.containsKey(Control.byName(Control.Name.CAMERA_STRAFE_RIGHT).getKeycode()))
                fixture.move(fixtureVelocity, 0, 0);
            if (keys.containsKey(Control.byName(Control.Name.CAMERA_UP).getKeycode()))
                fixture.move(0, fixtureVelocity, 0);
            if (keys.containsKey(Control.byName(Control.Name.CAMERA_DOWN).getKeycode()))
                fixture.move(0, -fixtureVelocity, 0);
        } else {
            if (keys.containsKey(Control.byName(Control.Name.CAMERA_FORWARD).getKeycode()))
                camera.position.add(tmp.set(camera.direction).nor().scl(deltaTime * velocity));
            if (keys.containsKey(Control.byName(Control.Name.CAMERA_BACKWARD).getKeycode()))
                camera.position.add(tmp.set(camera.direction).nor().scl(-deltaTime * velocity));
            if (keys.containsKey(Control.byName(Control.Name.CAMERA_STRAFE_LEFT).getKeycode()))
                camera.position.add(tmp.set(camera.direction).crs(camera.up).nor().scl(-deltaTime * velocity));
            if (keys.containsKey(Control.byName(Control.Name.CAMERA_STRAFE_RIGHT).getKeycode()))
                camera.position.add(tmp.set(camera.direction).crs(camera.up).nor().scl(deltaTime * velocity));
            if (keys.containsKey(Control.byName(Control.Name.CAMERA_UP).getKeycode()))
                camera.position.add(tmp.set(camera.up).nor().scl(deltaTime * velocity));
            if (keys.containsKey(Control.byName(Control.Name.CAMERA_DOWN).getKeycode()))
                camera.position.add(tmp.set(camera.up).nor().scl(-deltaTime * velocity));
        }
    }

    @Override
    public boolean draw(Renderer renderer) {
        mouseSelectFixtures();
        updateFixtureColors();

        controls(Gdx.graphics.getDeltaTime());

        camera.update();

        modelBatch.begin(camera);
        for (Fixture fixture : PatchUI.fixtures()) {
            modelBatch.render(fixture.getModelInstances(), environment);
        }
        modelBatch.end();

        return true;
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (UIHandler.getSection().equals(UIHandler.Section.VISUALISER3D)) {
            float deltaX = -Gdx.input.getDeltaX() * degreesPerPixel;
            float deltaY = -Gdx.input.getDeltaY() * degreesPerPixel;
            camera.direction.rotate(camera.up, deltaX);
            tmp.set(camera.direction).crs(camera.up).nor();
            camera.direction.rotate(tmp, deltaY);
        }
        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        keys.put(keycode, keycode);
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        keys.remove(keycode, 0);
        return true;
    }

    public Fixture getFixture(int screenX, int screenY) {
        Ray ray = camera.getPickRay(screenX, screenY);
        for (Fixture fixture : PatchUI.fixtures()) {
            if (fixture.getModels().size() > 0) {
                for (Model model : fixture.getModels()) {
                    if (Intersector.intersectRayBoundsFast(ray, model.getPosition(), model.getDimensions())) {
                        return fixture;
                    }
                }
            }
        }
        return null;
    }
}