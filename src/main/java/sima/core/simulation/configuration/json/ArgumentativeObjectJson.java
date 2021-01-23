package sima.core.simulation.configuration.json;

import java.util.List;

public interface ArgumentativeObjectJson {

    /**
     * In Json configuration, argument of an object are an array of array. Seconds arrays are array which must contains
     * only and only two values. The first value is the argument name, the second value is the value of the argument.
     * <p>
     * {@code "args":[["arg1", "valueArg1"], ["arg2", "valueArg2"]]} in this Json, there are two arguments, the argument
     * {@code "arg1"} which has the value {@code "valueArg1"} and the argument {@code "arg2"} which has the value {@code
     * "valueArg2"}.
     *
     * @return the list of args couples. If there is no args, returns null.
     */
    List<List<String>> getArgs();

}
