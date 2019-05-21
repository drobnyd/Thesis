package eu.profinit.manta.dataflow.generator.modeling.common.rtf;

import com.rtfparserkit.converter.text.StringTextConverter;
import com.rtfparserkit.parser.RtfStreamSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Helper class for handling rich text format (RTF) strings that may be found in attributes of modeling tools.
 * Singleton class.
 *
 * @author ddrobny
 */
public class RtfTools {
    /** The singleton instance. */
    private static RtfTools ourInstance = new RtfTools();
    /** Rtf to plain text converter. */
    StringTextConverter converter = new StringTextConverter();

    private RtfTools() {
    }

    /**
     * Gets the singleton instance of the class.
     * @return the singleton instance.
     */
    public static RtfTools getInstance() {
        return ourInstance;
    }

    /**
     * Transforms a rich text formatted string into a plain text string.
     * @param rtfString the text to be transformed.
     * @return plain text version of the rtfString. {@code null} if the former rtfString was {@code null}.
     */
    public String rtfToPlain(String rtfString) {
        if (rtfString == null) {
            return null;
        }

        // Is plain already, don't parse
        if (!rtfString.startsWith("{\\rtf")) {
            return rtfString;
        }

        // Otherwise strip the RTF
        try {
            converter.convert(new RtfStreamSource(new ByteArrayInputStream(rtfString.getBytes("UTF-8"))));
        } catch (IOException e) {
            return null;
        }
        return converter.getText();
    }
}
