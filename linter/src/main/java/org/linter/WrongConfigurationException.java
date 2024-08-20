package org.linter;

import java.util.Arrays;

public class WrongConfigurationException extends Exception{

    public WrongConfigurationException(String property) {
        super("Configuration Property Not Recognized: " + property);
    }

    public WrongConfigurationException(String property, String option, String[] options) {
        super("Configuration option for property \"" + property + "\" not recognized: " + option +
                "\n you could try with: " + Arrays.toString(options));
    }
}
