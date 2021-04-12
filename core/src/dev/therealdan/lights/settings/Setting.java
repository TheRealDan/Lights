package dev.therealdan.lights.settings;

public class Setting {

    private Key key;
    private String value;

    public Setting(Key key, boolean value) {
        this(key, value ? "True" : "False");
    }

    public Setting(Key key, long value) {
        this(key, Long.toString(value));
    }

    public Setting(Key key, String value) {
        this.key = key;
        this.value = value;
    }

    public void toggle() {
        if (!getKey().getType().equals(Type.BOOLEAN)) return;

        if (isTrue()) {
            setValue("False");
        } else {
            setValue("True");
        }
    }

    public void increment(long amount) {
        if (!getKey().getType().equals(Type.LONG)) return;

        setValue(Long.toString(Long.parseLong(getValue()) + amount));
    }

    public void decrement(long amount) {
        if (!getKey().getType().equals(Type.LONG)) return;

        setValue(Long.toString(Long.parseLong(getValue()) - amount));
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isFalse() {
        return !isTrue();
    }

    public boolean isTrue() {
        return getValue().equals("True");
    }

    public long getLong() {
        return Long.parseLong(getValue());
    }

    public int getInt() {
        return Integer.parseInt(getValue());
    }

    public Key getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public enum Type {
        BOOLEAN,
        LONG,
        STRING;

        @Override
        public String toString() {
            return this.toString().substring(0, 1).toUpperCase() + this.toString().substring(1).toLowerCase();
        }
    }

    public enum Key {
        INTERVAL,
        CONNECTION_WAIT,
        NEW_READ_TIMEOUT,
        NEW_WRITE_TIMEOUT,
        CHANNELS_PER_SEND,
        CHANNELS_PER_TIME,

        SHOW_DMX_SEND_DEBUG,
        CONTINUOUS,
        DRAW_DMX,
        REMEMBER_CAMERA_POSITION;

        public Type getType() {
            switch (this) {
                case SHOW_DMX_SEND_DEBUG:
                case CONTINUOUS:
                case DRAW_DMX:
                case REMEMBER_CAMERA_POSITION:
                    return Type.BOOLEAN;
                case INTERVAL:
                case CONNECTION_WAIT:
                case NEW_READ_TIMEOUT:
                case NEW_WRITE_TIMEOUT:
                case CHANNELS_PER_SEND:
                case CHANNELS_PER_TIME:
                    return Type.LONG;
                default:
                    return Type.STRING;
            }
        }

        public String getName() {
            StringBuilder stringBuilder = new StringBuilder();
            for (String string : this.toString().split("_")) {
                stringBuilder.append(" ");
                stringBuilder.append(string.substring(0, 1).toUpperCase());
                stringBuilder.append(string.substring(1).toLowerCase());
            }
            return stringBuilder.toString().substring(1);
        }
    }
}