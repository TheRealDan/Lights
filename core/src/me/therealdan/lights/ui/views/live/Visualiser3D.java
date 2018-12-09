package me.therealdan.lights.ui.views.live;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import me.therealdan.lights.programmer.Programmer;
import me.therealdan.lights.ui.views.Hotkeys;
import me.therealdan.lights.ui.views.Live;
import me.therealdan.lights.ui.views.Patch;

public class Visualiser3D {

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

        for (Fixture fixture : Patch.fixtures()) {
            fixture.buildModel();
        }
    }

    public void update() {
        if (Live.getSection().equals(Live.Section.VISUALISER3D)) {
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                Fixture fixture = getFixture(Gdx.input.getX(), Gdx.input.getY());
                if (fixture != null)
                    Programmer.select(fixture);
            }
        }

        DMX visualiser = DMX.get("VISUALISER");
        for (Fixture fixture : Patch.fixtures()) {
            fixture.updateColor(visualiser);
        }
    }

    public void draw(float deltaTime) {
        update();

        controls(deltaTime);

        camera.update();

        modelBatch.begin(camera);
        for (Fixture fixture : Patch.fixtures()) {
            modelBatch.render(fixture.getModels(), environment);
        }
        modelBatch.end();
    }

    private void controls(float deltaTime) {
        boolean shift = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
        float velocity = this.velocity * (shift ? 10 : 1);

        if (Programmer.getSelectedFixtures().size() == 1) {
            Fixture fixture = Programmer.getSelectedFixtures().get(0);
            if (Hotkeys.contains(Hotkeys.Control.CAMERA_FORWARD) && keys.containsKey(Hotkeys.get(Hotkeys.Control.CAMERA_FORWARD))) {
                fixture.move(0, 0, -fixtureVelocity);
            }
            if (Hotkeys.contains(Hotkeys.Control.CAMERA_BACKWARD) && keys.containsKey(Hotkeys.get(Hotkeys.Control.CAMERA_BACKWARD))) {
                fixture.move(0, 0, fixtureVelocity);
            }
            if (Hotkeys.contains(Hotkeys.Control.CAMERA_STRAFE_LEFT) && keys.containsKey(Hotkeys.get(Hotkeys.Control.CAMERA_STRAFE_LEFT))) {
                fixture.move(-fixtureVelocity, 0, 0);
            }
            if (Hotkeys.contains(Hotkeys.Control.CAMERA_STRAFE_RIGHT) && keys.containsKey(Hotkeys.get(Hotkeys.Control.CAMERA_STRAFE_RIGHT))) {
                fixture.move(fixtureVelocity, 0, 0);
            }
            if (Hotkeys.contains(Hotkeys.Control.CAMERA_UP) && keys.containsKey(Hotkeys.get(Hotkeys.Control.CAMERA_UP))) {
                fixture.move(0, fixtureVelocity, 0);
            }
            if (Hotkeys.contains(Hotkeys.Control.CAMERA_DOWN) && keys.containsKey(Hotkeys.get(Hotkeys.Control.CAMERA_DOWN))) {
                fixture.move(0, -fixtureVelocity, 0);
            }
        } else {
            if (Hotkeys.contains(Hotkeys.Control.CAMERA_FORWARD) && keys.containsKey(Hotkeys.get(Hotkeys.Control.CAMERA_FORWARD))) {
                tmp.set(camera.direction).nor().scl(deltaTime * velocity);
                camera.position.add(tmp);
            }
            if (Hotkeys.contains(Hotkeys.Control.CAMERA_BACKWARD) && keys.containsKey(Hotkeys.get(Hotkeys.Control.CAMERA_BACKWARD))) {
                tmp.set(camera.direction).nor().scl(-deltaTime * velocity);
                camera.position.add(tmp);
            }
            if (Hotkeys.contains(Hotkeys.Control.CAMERA_STRAFE_LEFT) && keys.containsKey(Hotkeys.get(Hotkeys.Control.CAMERA_STRAFE_LEFT))) {
                tmp.set(camera.direction).crs(camera.up).nor().scl(-deltaTime * velocity);
                camera.position.add(tmp);
            }
            if (Hotkeys.contains(Hotkeys.Control.CAMERA_STRAFE_RIGHT) && keys.containsKey(Hotkeys.get(Hotkeys.Control.CAMERA_STRAFE_RIGHT))) {
                tmp.set(camera.direction).crs(camera.up).nor().scl(deltaTime * velocity);
                camera.position.add(tmp);
            }
            if (Hotkeys.contains(Hotkeys.Control.CAMERA_UP) && keys.containsKey(Hotkeys.get(Hotkeys.Control.CAMERA_UP))) {
                tmp.set(camera.up).nor().scl(deltaTime * velocity);
                camera.position.add(tmp);
            }
            if (Hotkeys.contains(Hotkeys.Control.CAMERA_DOWN) && keys.containsKey(Hotkeys.get(Hotkeys.Control.CAMERA_DOWN))) {
                tmp.set(camera.up).nor().scl(-deltaTime * velocity);
                camera.position.add(tmp);
            }
        }
    }

    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }

    public boolean keyDown(int keycode) {
        keys.put(keycode, keycode);
        return true;
    }

    public boolean keyUp(int keycode) {
        keys.remove(keycode, 0);
        return true;
    }

    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (Live.getSection().equals(Live.Section.VISUALISER3D)) {
            float deltaX = -Gdx.input.getDeltaX() * degreesPerPixel;
            float deltaY = -Gdx.input.getDeltaY() * degreesPerPixel;
            camera.direction.rotate(camera.up, deltaX);
            tmp.set(camera.direction).crs(camera.up).nor();
            camera.direction.rotate(tmp, deltaY);
        }
        return true;
    }

    public Fixture getFixture(int screenX, int screenY) {
        Ray ray = camera.getPickRay(screenX, screenY);
        for (Fixture fixture : Patch.fixtures()) {
            if (fixture.getModels().size() > 0) {
                if (Intersector.intersectRayBoundsFast(ray, fixture.getPosition(), fixture.getDimensions())) {
                    return fixture;
                }
            }
        }
        return null;
    }
}