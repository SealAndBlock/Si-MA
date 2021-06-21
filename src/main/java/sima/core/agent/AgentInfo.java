package sima.core.agent;

import sima.core.protocol.ProtocolIdentifier;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * All infos of an {@link SimpleAgent}. This class is {@link Serializable}.
 */
public class AgentInfo implements Serializable {

    // Variables.

    private final AgentIdentifier agentIdentifier;

    private final List<String> behaviors;

    private final List<ProtocolIdentifier> protocols;

    private final List<String> environments;

    // Constructors.

    /**
     * Create an {@link AgentInfo}. All specified list must be {@link Serializable}.
     *
     * @param agentIdentifier the agent identifier
     * @param behaviors       the agent behavior list
     * @param protocols       the agent protocol list
     * @param environments    the agent environment list
     * @throws NullPointerException if the agentID or the agentName is null.
     */
    public AgentInfo(AgentIdentifier agentIdentifier, List<String> behaviors, List<ProtocolIdentifier> protocols,
                     List<String> environments) {
        this.agentIdentifier = Optional.of(agentIdentifier).get();

        this.behaviors = Optional.ofNullable(behaviors).orElse(Collections.emptyList());
        this.protocols = Optional.ofNullable(protocols).orElse(Collections.emptyList());
        this.environments = Optional.ofNullable(environments).orElse(Collections.emptyList());
    }

    // Methods.

    // Getters and Setters.

    public AgentIdentifier getAgentIdentifier() {
        return agentIdentifier;
    }

    public List<String> getBehaviors() {
        return behaviors;
    }

    public List<ProtocolIdentifier> getProtocols() {
        return protocols;
    }

    public List<String> getEnvironments() {
        return environments;
    }
}
