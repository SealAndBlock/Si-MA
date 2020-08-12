package sima.core.agent;

import sima.core.agent.exception.AgentException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public abstract class AbstractAgent {

    // Variables.

    private final String agentName;

    private final List<Environment> environments;

    private final List<Behavior> behaviors;

    private final Map<String, Protocol> mapProtocol;

    // Constructors.

    public AbstractAgent(String agentName, List<Class<? extends Behavior>> listBehaviors) throws AgentException {
        this.agentName = agentName;
        this.environments = new ArrayList<>();

        this.behaviors = new ArrayList<>();
        for (Class<? extends Behavior> behaviorClass : listBehaviors) {
            try {
                Constructor<? extends Behavior> constructor = behaviorClass.getConstructor(AbstractAgent.class);
                Behavior behavior = constructor.newInstance(this);
                this.behaviors.add(behavior);
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException
                    | InvocationTargetException e) {
                throw new AgentException(e);
            }
        }

        this.mapProtocol = new HashMap<>();
    }

    // Methods.

    public abstract void start();

    // Getters and Setters.

    public String getAgentName() {
        return agentName;
    }

    public List<Environment> getEnvironments() {
        return Collections.unmodifiableList(environments);
    }

    public List<Behavior> getBehaviors() {
        return Collections.unmodifiableList(this.behaviors);
    }

    public Map<String, Protocol> getMapProtocol() {
        return Collections.unmodifiableMap(this.mapProtocol);
    }
}
