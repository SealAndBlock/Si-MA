package sima.core.agent;

import sima.core.protocol.ProtocolIdentifier;

import java.io.Serializable;
import java.util.*;

/**
 * All infos of an {@link AbstractAgent}. This class is {@link Serializable}.
 */
public class AgentInfo implements Serializable {

    // Variables.

    private final UUID agentID;

    private final String agentName;

    private final List<String> behaviors;

    private final List<ProtocolIdentifier> protocols;

    // Constructors.

    /**
     * Create an {@link AgentInfo}. All specified list must be {@link Serializable}.
     *
     * @param agentID   the agent {@link UUID}
     * @param agentName the agent name
     * @param behaviors the agent behavior list
     * @param protocols the agent protocol list
     * @throws NullPointerException if the agentID or the agentName is null.
     */
    public AgentInfo(UUID agentID, String agentName, List<String> behaviors, List<ProtocolIdentifier> protocols) {
        this.agentID = Optional.of(agentID).get();
        this.agentName = Optional.of(agentName).get();

        this.behaviors = Objects.requireNonNullElse(behaviors, Collections.emptyList());
        this.protocols = Objects.requireNonNullElse(protocols, Collections.emptyList());
    }

    // Methods.

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AgentInfo)) return false;
        AgentInfo agentInfo = (AgentInfo) o;
        return agentID.equals(agentInfo.agentID) &&
                agentName.equals(agentInfo.agentName) &&
                behaviors.equals(agentInfo.behaviors) &&
                protocols.equals(agentInfo.protocols);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agentID, agentName, behaviors, protocols);
    }

    // Getters and Setters.

    public UUID getAgentID() {
        return agentID;
    }

    public String getAgentName() {
        return agentName;
    }

    public List<String> getBehaviors() {
        return behaviors;
    }

    public List<ProtocolIdentifier> getProtocols() {
        return protocols;
    }
}
