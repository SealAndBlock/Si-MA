package sima.core.agent;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * Allows the identification of an {@link SimaAgent}.
 * <p>
 * This class is as a primary key of a {@code AbstractAgent}. With an {@code AgentIdentifier} we can identify in the simulation a particular
 * agent.
 * <p>
 * This class contains only {@link SimaAgent#getAgentName()}, {@link SimaAgent#getSequenceId()} and {@link SimaAgent#getUniqueId()} fields.
 */
public class AgentIdentifier implements Serializable {
    
    // Variables.

    private final String agentName;

    private final int agentSequenceId;

    private final int agentUniqueId;

    // Constructors.
    
    /**
     * @param agentName       the agent name
     * @param agentSequenceId the agent sequence id
     * @param agentUniqueId   the agent number id
     *
     * @throws NullPointerException     if agentName is null
     * @throws IllegalArgumentException if agentSequenceId or agentNumberId or less than 0 or if the agentName is empty
     */
    public AgentIdentifier(String agentName, int agentSequenceId, int agentUniqueId) {
        this.agentName = Optional.of(agentName).get();
        if (this.agentName.isEmpty())
            throw new IllegalArgumentException("The agentName cannot be empty.");
        
        this.agentSequenceId = agentSequenceId;
        if (this.agentSequenceId < 0)
            throw new IllegalArgumentException("The agentSequenceId cannot be less than 0.");
        
        this.agentUniqueId = agentUniqueId;
        if (this.agentUniqueId < 0)
            throw new IllegalArgumentException("The agentUniqueId cannot be less than 0.");
    }
    
    // Methods.
    
    @Override
    public String toString() {
        return "[AgentIdentifier - " +
                "agentName=" + agentName +
                ", agentSequenceId=" + agentSequenceId +
                ", agentUniqueId=" + agentUniqueId + "]";
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AgentIdentifier that)) return false;
        return agentName.equals(that.agentName) && agentUniqueId == that.agentUniqueId && agentSequenceId == that.agentSequenceId;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(agentName, agentSequenceId, agentUniqueId);
    }

    // Getters.

    public String getAgentName() {
        return agentName;
    }

    public int getAgentSequenceId() {
        return agentSequenceId;
    }

    public int getAgentUniqueId() {
        return agentUniqueId;
    }
}
