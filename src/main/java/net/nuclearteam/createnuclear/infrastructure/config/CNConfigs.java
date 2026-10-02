package net.nuclearteam.createnuclear.infrastructure.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.nuclearteam.createnuclear.CreateNuclear;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/** The common config, kept in config/createnuclear-common.json (was createnuclear-common.toml). */
public class CNConfigs {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static CNCCommon common;

    public static CNCCommon common() {
        return common;
    }

    public static void register() {
        common = new CNCCommon();
        load(CreateNuclear.MOD_ID + "-common.json", common);
    }

    private static void load(String fileName, ConfigBase config) {
        Path path = FabricLoader.getInstance().getConfigDir().resolve(fileName);
        if (Files.exists(path)) {
            try (Reader reader = Files.newBufferedReader(path)) {
                JsonObject read = GSON.fromJson(reader, JsonObject.class);
                if (read != null)
                    config.read(read);
            } catch (Exception e) {
                CreateNuclear.LOGGER.error("Could not read {}, using defaults", fileName, e);
            }
        }
        JsonObject out = new JsonObject();
        config.write(out);
        try (Writer writer = Files.newBufferedWriter(path)) {
            GSON.toJson(out, writer);
        } catch (Exception e) {
            CreateNuclear.LOGGER.error("Could not write {}", fileName, e);
        }
    }
}
