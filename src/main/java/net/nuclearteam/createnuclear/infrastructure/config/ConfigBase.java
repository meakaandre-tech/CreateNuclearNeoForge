package net.nuclearteam.createnuclear.infrastructure.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.nuclearteam.createnuclear.CreateNuclear;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * JSON-backed replacement for Create's ConfigBase on top of the NeoForge config spec.
 * The values keep their get() call and the groups their nesting.
 */
public abstract class ConfigBase {
    private final List<Entry> entries = new ArrayList<>();

    private interface Entry {
        void read(JsonObject json);

        void write(JsonObject json);
    }

    public abstract String getName();

    protected ConfigBool b(boolean current, String name, String... comment) {
        ConfigBool value = new ConfigBool(name, current, comment);
        entries.add(value);
        return value;
    }

    protected ConfigInt i(int current, int min, int max, String name, String... comment) {
        ConfigInt value = new ConfigInt(name, current, min, max, comment);
        entries.add(value);
        return value;
    }

    protected ConfigInt i(int current, int min, String name, String... comment) {
        return i(current, min, Integer.MAX_VALUE, name, comment);
    }

    protected ConfigInt i(int current, String name, String... comment) {
        return i(current, Integer.MIN_VALUE, Integer.MAX_VALUE, name, comment);
    }

    protected <T extends ConfigBase> T nested(int depth, Supplier<T> constructor, String... comment) {
        T config = constructor.get();
        entries.add(new Entry() {
            @Override
            public void read(JsonObject json) {
                if (json.has(config.getName()) && json.get(config.getName()).isJsonObject())
                    config.read(json.getAsJsonObject(config.getName()));
            }

            @Override
            public void write(JsonObject json) {
                JsonObject child = new JsonObject();
                if (comment.length > 0)
                    child.addProperty("_comment", String.join(" ", comment));
                config.write(child);
                json.add(config.getName(), child);
            }
        });
        return config;
    }

    public void read(JsonObject json) {
        for (Entry entry : entries)
            entry.read(json);
    }

    public void write(JsonObject json) {
        for (Entry entry : entries)
            entry.write(json);
    }

    public abstract static class CValue<T> implements Entry {
        protected final String name;
        protected final String[] comment;
        protected T value;

        protected CValue(String name, T value, String[] comment) {
            this.name = name;
            this.value = value;
            this.comment = comment;
        }

        public T get() {
            return value;
        }

        public void set(T value) {
            this.value = value;
        }

        public String getName() {
            return name;
        }

        protected abstract T parse(JsonElement element);

        @Override
        public void read(JsonObject json) {
            if (!json.has(name))
                return;
            try {
                value = parse(json.get(name));
            } catch (Exception e) {
                CreateNuclear.LOGGER.warn("Invalid config value for '{}', keeping default", name);
            }
        }

        protected void writeComment(JsonObject json) {
            if (comment.length > 0)
                json.addProperty("_comment: " + name, String.join(" ", comment));
        }
    }

    public static class ConfigBool extends CValue<Boolean> {
        public ConfigBool(String name, boolean def, String... comment) {
            super(name, def, comment);
        }

        @Override
        protected Boolean parse(JsonElement element) {
            return element.getAsBoolean();
        }

        @Override
        public void write(JsonObject json) {
            writeComment(json);
            json.addProperty(name, value);
        }
    }

    public static class ConfigInt extends CValue<Integer> {
        private final int min, max;

        public ConfigInt(String name, int current, int min, int max, String... comment) {
            super(name, current, comment);
            this.min = min;
            this.max = max;
        }

        @Override
        protected Integer parse(JsonElement element) {
            return Math.max(min, Math.min(max, element.getAsInt()));
        }

        @Override
        public void write(JsonObject json) {
            writeComment(json);
            json.addProperty(name, value);
        }
    }
}
