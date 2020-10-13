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

    private final int agentNumberId;

    private final List<String> behaviors;

    private final List<ProtocolIdentifier> protocols;

    private final List<String> environments;

    // Constructors.

    /**
     * Create an {@link AgentInfo}. All specified list must be {@link Serializable}.
     *
     * @param agentID       the agent {@link UUID}
     * @param agentName     the agent name
     * @param agentNumberId the agent number id
     * @param behaviors     the agent behavior list
     * @param protocols     the agent protocol list
     * @param environments  the agent environment list
     * @throws NullPointerException if the agentID or the agentName is null.
     */
    public AgentInfo(UUID agentID, String agentName, int agentNumberId, List<String> behaviors, List<ProtocolIdentifier> protocols,
                     List<String> environments) {
        this.agentID = Optional.of(agentID).get();
        this.agentName = Optional.of(agentName).get();
        this.agentNumberId = agentNumberId;

        this.behaviors = Optional.ofNullable(behaviors).orElse(Collections.emptyList());
        this.protocols = Optional.ofNullable(protocols).orElse(Collections.emptyList());
        this.environments = Optional.ofNullable(environments).orElse(Collections.emptyList());
    }

    // Methods.

    /**
     * Only use the fields {@link #agentName} and {@link #agentID}.
     *
     * @param o the object to compare
     * @return true if the object is equal to the {@code AgentInfo}, else false.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AgentInfo)) return false;
        AgentInfo agentInfo = (AgentInfo) o;
        return agentID.equals(agentInfo.agentID) &&
                agentName.equals(agentInfo.agentName);
    }

    /**
     * Only use the fields {@link #agentName} and {@link #agentID}.
     *
     * @return the hash code of the {@code AgentInfo}.
     */
    @Override
    public int hashCode() {
        return Objects.hash(agentID, agentName);
    }

    // Getters and Setters.

    public UUID getAgentID() {
        return agentID;
    }

    public int getAgentNumberId() {
        return agentNumberId;
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

    public List<String> getEnvironments() {
        return environments;
    }
}
