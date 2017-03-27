package sample;

import java.io.File;
import java.net.MalformedURLException;

/**
 * A class that defines utility methods for stylesheets.
 */
public class Stylesheet {

    /**
     * Returns a `file:` URL that represents the given filename.
     */
    public static String getUrl(String filename) {
        try {
            return new File(filename).toURI().toURL().toExternalForm();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
