package me.aki.linkstone.annotations;

/**
 * Server versions
 */
public enum Version {
    V1_11_R1("1.11-R1"), V1_12_R1("1.12-R1");

    public static Version forName(String name) {
        for(Version version : values()) {
            if (version.getName().equals(name)) {
                return version;
            }
        }
        throw new IllegalArgumentException("No enum named " + name + ".");
    }

    private final String name;

    Version(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
