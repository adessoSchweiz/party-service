package ch.adesso.partyservice.party.boundary;

import javax.enterprise.inject.Specializes;

import com.airhacks.porcupine.configuration.control.ExecutorConfigurator;
import com.airhacks.porcupine.execution.control.ExecutorConfiguration;

@Specializes
public class CustomExecutorConfigurator extends ExecutorConfigurator {

    @Override
    public ExecutorConfiguration defaultConfigurator() {
        return super.defaultConfigurator();
    }

    @Override
    public ExecutorConfiguration forPipeline(String name) {
            return new ExecutorConfiguration.Builder().
                    corePoolSize(4).
                    maxPoolSize(8).
                    queueCapacity(16).
                    keepAliveTime(1).
                    callerRunsPolicy().
                    build();
    }

}