package sima.core.agent;

import sima.core.agent.exception.AgentException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public abstract class AbstractAgent {

    // Variables.

    private final String agentName;

    private final Map<String, Environment> environments;

    private final List<Behavior> behaviors;

    private final Map<String, Protocol> mapProtocol;

    // Constructors.

    public AbstractAgent(String agentName, List<Environment> environments,
                         List<Class<? extends Behavior>> listBehaviors) throws AgentException {
        this.agentName = agentName;

        this.environments = new HashMap<>();
        for (Environment environment : environments) {
            this.environments.put(environment.getName(), environment);
        }

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

    /**
     * @param environmentName the environment name
     * @return true if the environment name is mapped to an environment in the agent.
     */
    public boolean isEvolvingInEnvironment(String environmentName) {
        return this.environments.containsKey(environmentName);
    }

    /**
     * Remove the environment of the agent.
     *
     * @param environmentName the environment name
     */
    public void quiteEnvironment(String environmentName) {
        this.environments.remove(environmentName);
    }

    // Getters and Setters.

    public String getAgentName() {
        return agentName;
    }

    public Map<String, Environment> getEnvironments() {
        return Collections.unmodifiableMap(environments);
    }

    public List<Behavior> getBehaviors() {
        return Collections.unmodifiableList(this.behaviors);
    }

    public Map<String, Protocol> getMapProtocol() {
        return Collections.unmodifiableMap(this.mapProtocol);
    }
}
