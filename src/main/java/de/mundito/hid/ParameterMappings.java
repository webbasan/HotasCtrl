package de.mundito.hid;

import de.mundito.args.Parameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * User: webbasan Date: 05.05.15 Time: 21:40
 */
public class ParameterMappings {

    private Map<Parameter.Sub, List<InternalValues>> paramMappings;

    public ParameterMappings() {
        this.paramMappings = createParamMappings();
    }

    public List<InternalValues.LightSource> getLightSourceMappings(Parameter.Sub key) {
        List<InternalValues> internalValues = getMappedValues(this.paramMappings, key);
        List<InternalValues.LightSource> lightSources = new ArrayList<>(internalValues.size());
        for (InternalValues internalValue : internalValues) {
            lightSources.add((InternalValues.LightSource)internalValue);
        }
        return lightSources;
    }

    public List<InternalValues.Led> getLedMappings(Parameter.Sub key) {
        List<InternalValues> internalValues = getMappedValues(this.paramMappings,  key);
        List<InternalValues.Led> leds = new ArrayList<>(internalValues.size());
        for (InternalValues internalValue : internalValues) {
            leds.add((InternalValues.Led)internalValue);
        }
        return leds;
    }

    public InternalValues.LedColor getLedColorMappings(Parameter.Sub key) {
        List<InternalValues> internalValues = getMappedValues(this.paramMappings, key);
        return internalValues.isEmpty() ? InternalValues.LedColor.OFF : (InternalValues.LedColor)internalValues.get(0);
    }

    private Map<Parameter.Sub, List<InternalValues>> createParamMappings() {
        Map<Parameter.Sub, List<InternalValues>> paramMappings = new HashMap<>();

        for (InternalValues.LightSource lightSource : InternalValues.LightSource.values()) {
            addMapping(paramMappings, lightSource);
        }
        for (InternalValues.Led led : InternalValues.Led.values()) {
            addMapping(paramMappings, led);
        }
        for (InternalValues.LedColor ledColor : InternalValues.LedColor.values()) {
            addMapping(paramMappings, ledColor);
        }

        return paramMappings;
    }

    private static void addMapping(Map<Parameter.Sub, List<InternalValues>> paramMappings, InternalValues internalValues) {
        for (Parameter.Sub key : internalValues.getKeys()) {
            List<InternalValues> valuesList = getMappedValues(paramMappings, key);
            valuesList.add(internalValues);
        }
    }

    private static List<InternalValues> getMappedValues(Map<Parameter.Sub, List<InternalValues>> paramMappings, Parameter.Sub key) {
        List<InternalValues> valuesList = paramMappings.get(key);
        if (valuesList == null) {
            valuesList = new ArrayList<>();
            paramMappings.put(key, valuesList);
        }
        return valuesList;
    }
}
