package io.github.rosemoe.editor.core.extension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import junit.framework.TestCase;

public class ExtensionTest extends TestCase {

    public void testConfigure() throws JsonProcessingException {

        Extension e = new Extension(null);
        String config = new String();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode;


        /**
            "color": {
                "defaultColors": [
                {"name": "accent1", "value": "#FFFF0000"},
                        ],
            },
        */
        config = "{\"defaultColors\": [{\"name\": \"accent1\", \"value\": \"#FFFF0000\"}] }";
        e.configure(mapper.readTree(config));
        assertTrue(e.isEnabled());


        /**
            "linenumber": {
                "enabled": false
            }
        */
        config = "{\"enabled\": false}";
        e.configure(mapper.readTree(config));
        assertTrue(e.isDisabled());
    }

    public void testInitFromJson() {

    }
}