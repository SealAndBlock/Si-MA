package sima.core.agent;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Allows the identification of an {@link AbstractAgent}.
 * <p>
 * This class is as a primary key of a {@code AbstractAgent}. With an {@code AgentIdentifier} we can identify in
 * the simulation a particular agent.
 * <p>
 * This class contains all this fields of an agent:
 * <block>
 * {@link AbstractAgent#getUUID()}
 * {@link AbstractAgent#getAgentName()}
 * {@link AbstractAgent#getNumberId()}
 * </block>
 */
public class AgentIdentifier implements Serializable {

    // Variables.

    private final UUID agentUUID;

    private final String agentName;

    private final int agentNumberId;

    // Constructors.

    /**
     * @param agentUUID     the agent {@link UUID}
     * @param agentName     the agent name
     * @param agentNumberId the agent number id
     * @throws NullPointerException     if agentUUID or agentName is null.
     * @throws IllegalArgumentException if agentNumberId is less than 0.
     */
    public AgentIdentifier(UUID agentUUID, String agentName, int agentNumberId) {
        this.agentUUID = Optional.of(agentUUID).get();
        this.agentName = Optional.of(agentName).get();
        this.agentNumberId = agentNumberId;
        if (this.agentNumberId < 0)
            throw new IllegalArgumentException("The agentNumberId cannot be less than 0.");
    }

    // Methods.

    @Override
    public String toString() {
        return "[AgentIdentifier - " +
                "agentUUID=" + agentUUID +
                ", agentName=" + agentName +
                ", agentNumberId=" + agentNumberId + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AgentIdentifier)) return false;
        AgentIdentifier that = (AgentIdentifier) o;
        return agentNumberId == that.agentNumberId &&
                agentUUID.equals(that.agentUUID) &&
                agentName.equals(that.agentName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agentUUID, agentName, agentNumberId);
    }

    // Getters and Setters.

    public UUID getAgentUUID() {
        return agentUUID;
    }

    public String getAgentName() {
        return agentName;
    }

    public int getAgentNumberId() {
        return agentNumberId;
    }
}
