package dev.therealdan.lights.store;

import dev.therealdan.lights.files.ProfileFile;
import dev.therealdan.lights.fixtures.fixture.Profile;
import dev.therealdan.lights.util.sorting.Sortable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ProfilesStore implements Store {

    private HashSet<Profile> _profiles = new HashSet<>();

    public void register(Profile profile) {
        _profiles.add(profile);
    }

    public void delete(Profile profile) {
        _profiles.remove(profile);
    }

    @Override
    public void loadFromFile() {
        for (ProfileFile profileFile : ProfileFile.load())
            register(profileFile.getProfile());
    }

    @Override
    public void saveToFile() {
        for (Profile profile : getProfiles())
            new ProfileFile(profile).saveAsTxt();
    }

    @Override
    public int count() {
        return _profiles.size();
    }

    public Profile getProfileByName(String name) {
        for (Profile profile : getProfiles())
            if (profile.getName().equalsIgnoreCase(name))
                return profile;

        return null;
    }

    public List<Profile> getProfiles(Sortable.Sort... sort) {
        if (sort.length == 0) return new ArrayList<>(_profiles);

        List<Profile> profiles = new ArrayList<>();
        for (Sortable sortable : Sortable.sort(new ArrayList<>(_profiles), sort))
            profiles.add((Profile) sortable);
        return profiles;
    }
}