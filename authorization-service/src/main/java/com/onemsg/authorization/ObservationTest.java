package com.onemsg.authorization;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;

public class ObservationTest {
 
    
    public static void main(String[] args) {
        // ObservationRegistry registry = ObservationRegistry.create();

        // Observation.createNotStarted("foo", registry)
        //     .lowCardinalityKeyValue("lowTag", "lowTagValue")
        //     .highCardinalityKeyValue("highTag", "highTagValue")
        //     .observe(() -> System.out.println("Hello"));

        MeterRegistry registry = new SimpleMeterRegistry();
        Timer.Sample sample = Timer.start(registry);
        try {
            // do some work here
        } finally {
            sample.stop(Timer.builder("my.timer").register(registry));
        }
    }
}
