package me.therealdan.lights.files;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import me.therealdan.lights.fixtures.fixture.Profile;
import me.therealdan.lights.fixtures.fixture.profile.Channel;
import me.therealdan.lights.fixtures.fixture.profile.ModelDesign;

import java.util.ArrayList;
import java.util.List;

public class ProfileFile {

    public static final String DIR = "Lights/Profiles/";

    private String filename;

    private Profile profile;
    private Json json;
    private String txt;

    public ProfileFile(Profile profile) {
        this.profile = profile;
    }

    public ProfileFile(Json json) {
        this.json = json;
    }

    public ProfileFile(String txt) {
        this.txt = txt;
    }

    public void saveAsTxt() {
        FileHandle fileHandle = Gdx.files.local(DIR + getFilename() + ".txt");
        fileHandle.writeString(getTxt(), false);
    }

    public void saveAsJson() {
        // TODO Save json
    }

    public String getFilename() {
        if (this.filename == null) {
            if (this.profile != null) {
                this.filename = this.profile.getName();
            } else if (this.json != null) {
                // TODO get filename from json
            } else if (this.txt != null) {
                this.filename = getProfile().getName();
            }
        }
        return this.filename;
    }

    public Profile getProfile() {
        if (this.profile == null) {
            if (this.json != null) {
                // TODO profile from json
            } else if (this.txt != null) {
                String name = this.txt.length() > 16 ? this.txt.substring(16) : this.txt;
                List<Channel> channels = new ArrayList<>();
                List<ModelDesign> modelDesigns = new ArrayList<>();
                boolean isChannel = false, isModel = false;
                for (String line : this.txt.split("\\r?\\n")) {
                    if (line.startsWith("Name: ")) {
                        name = line.split(": ")[1];
                        continue;
                    } else if (line.startsWith("Channels:")) {
                        isChannel = true;
                        isModel = false;
                        continue;
                    } else if (line.startsWith("Models:")) {
                        isChannel = false;
                        isModel = true;
                        continue;
                    }
                    if (isChannel) {
                        channels.add(new Channel(line.substring(2)));
                    } else if (isModel) {
                        modelDesigns.add(ModelDesign.fromString(line.substring(2)));
                    }
                }
                this.profile = new Profile(name, modelDesigns, channels);
            }
        }
        return this.profile;
    }

    public Json getJson() {
        if (this.json == null) {
            if (this.profile != null) {
                // TODO - Build json from profile
            }
        }
        return this.json;
    }

    public String getTxt() {
        if (this.txt == null) {
            if (this.profile != null) {
                StringBuilder stringBuilder = new StringBuilder("Name: " + this.profile.getName() + "\r\nChannels:\r\n");

                for (Channel channel : this.profile.channels())
                    stringBuilder.append("  ").append(channel.toString()).append("\r\n");

                stringBuilder.append("Models:\r\n");
                for (ModelDesign modelDesign : this.profile.getModelDesigns())
                    stringBuilder.append("- ").append(modelDesign.toString()).append("\r\n");

                this.txt = stringBuilder.toString();
            }
        }
        return this.txt;
    }

    public static List<ProfileFile> load() {
        List<ProfileFile> profileFiles = new ArrayList<>();

        FileHandle fileHandle = Gdx.files.local(DIR);
        if (fileHandle.exists() && fileHandle.isDirectory()) {
            for (FileHandle child : fileHandle.list()) {
                if (child.extension().equalsIgnoreCase("txt")) {
                    profileFiles.add(new ProfileFile(child.readString()));
                } else if (child.extension().equalsIgnoreCase("json")) {
                    // TODO - Load ProfileFile from json
                }
            }
        }

        return profileFiles;
    }
}