package com.github.alexmodguy.alexscaves.server.misc;

import com.github.alexmodguy.alexscaves.AlexsCaves;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class WebHelper {

    private WebHelper() {
    }

    @Nullable
    public static BufferedReader getURLContents(@Nonnull String urlString, @Nonnull String backupFileLoc) {
        try {
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            InputStream stream = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(stream);
            return new BufferedReader(reader);
        } catch (Exception var7) {
            try {
                return new BufferedReader(new InputStreamReader(WebHelper.class.getClass().getClassLoader().getResourceAsStream(backupFileLoc), StandardCharsets.UTF_8));
            } catch (NullPointerException var6) {
                AlexsCaves.LOGGER.warn("Could not download list of mod incompatibilities");
                return null;
            }
        }
    }
}
