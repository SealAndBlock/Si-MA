package sima.core.agent;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * All infos of an {@link AbstractAgent}. This class is {@link Serializable}.
 */
public class AgentInfo implements Serializable {

    // Variables.

    private final UUID agentID;

    private final String agentName;

    // Constructors.

    public AgentInfo(UUID agentID, String agentName) {
        this.agentID = agentID;
        this.agentName = agentName;
    }

    // Methods.

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AgentInfo)) return false;
        AgentInfo agentInfo = (AgentInfo) o;
        return Objects.equals(agentID, agentInfo.agentID) &&
                Objects.equals(agentName, agentInfo.agentName);
    }

    // Getters and Setters.

    public UUID getAgentID() {
        return agentID;
    }

    public String getAgentName() {
        return agentName;
    }
}
