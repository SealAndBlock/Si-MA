package sima.basic.environment.physical;

import org.jetbrains.annotations.NotNull;
import sima.core.agent.AgentIdentifier;
import sima.core.environment.Environment;
import sima.core.environment.physical.PhysicalConnectionLayer;
import sima.core.environment.physical.PhysicalEvent;

import java.util.Map;
import java.util.Optional;

import static sima.core.simulation.SimaSimulationUtils.randomLong;

public class FullyConnectedPhysicalLayer extends PhysicalConnectionLayer {
    
    // Static.
    
    public static final String MIN_SEND_DELAY_ARGS = "minSendDelay";
    public static final String MAX_SEND_DELAY_ARGS = "maxSendDelay";
    
    public static final long DEFAULT_MIN_SEND_DELAY = 10;
    public static final long DEFAULT_MAX_SEND_DELAY = 20;
    
    // Variables.
    
    private long minSendDelay;
    private long maxSendDelay;
    
    // Constructors.
    
    public FullyConnectedPhysicalLayer(Environment environment, Map<String, String> args) {
        super(environment, args);
        initSendDelay();
        processArgument(args);
    }
    
    // Methods.
    
    protected void processArgument(Map<String, String> args) {
        if (args != null)
            extractSendDelays(args);
    }
    
    private void initSendDelay() {
        minSendDelay = DEFAULT_MIN_SEND_DELAY;
        maxSendDelay = DEFAULT_MAX_SEND_DELAY;
    }
    
    private void extractSendDelays(Map<String, String> args) {
        setFromArgsMinSendDelay(args);
        setFromArgsMaxSendDelay(args);
        verifiesDelays();
    }
    
    /**
     * Verifies if {@link #minSendDelay} is really less or equal to {@link #maxSendDelay}. If it is not the case, inverse values to make it
     * valid.
     */
    private void verifiesDelays() {
        if (maxSendDelay < minSendDelay) {
            long tmp = maxSendDelay;
            maxSendDelay = minSendDelay;
            minSendDelay = tmp;
        }
    }
    
    private void setFromArgsMinSendDelay(Map<String, String> args) {
        if (args.containsKey(MIN_SEND_DELAY_ARGS))
            try {
                minSendDelay = Integer.parseInt(args.get(MIN_SEND_DELAY_ARGS));
            } catch (NumberFormatException e) {
                minSendDelay = DEFAULT_MIN_SEND_DELAY;
            }
        
        if (minSendDelay < 1)
            minSendDelay = DEFAULT_MIN_SEND_DELAY;
    }
    
    private void setFromArgsMaxSendDelay(Map<String, String> args) {
        if (args.containsKey(MAX_SEND_DELAY_ARGS)) {
            try {
                maxSendDelay = Integer.parseInt(args.get(MAX_SEND_DELAY_ARGS));
            } catch (NumberFormatException e) {
                maxSendDelay = DEFAULT_MAX_SEND_DELAY;
            }
        }
        
        if (maxSendDelay < 1)
            maxSendDelay = DEFAULT_MAX_SEND_DELAY;
    }
    
    @Override
    protected @NotNull PhysicalEvent decoratePhysicalEvent(PhysicalEvent physicalEvent) {
        return physicalEvent;
    }
    
    /**
     * @param a1 the agent a1
     * @param a2 the agent a2
     *
     * @return always true because all agents as direct connection between them.b
     */
    @Override
    public boolean hasPhysicalConnection(AgentIdentifier a1, AgentIdentifier a2) {
        if (Optional.ofNullable(a1).isEmpty() || Optional.ofNullable(a2).isEmpty())
            throw new IllegalArgumentException("a1 and a2 must be not null");
        
        return true;
    }
    
    @Override
    protected boolean canBeSent(AgentIdentifier initiator, AgentIdentifier target, PhysicalEvent physicalEvent) {
        return true;
    }
    
    @Override
    protected void scheduleInEnvironment(AgentIdentifier target, PhysicalEvent physicalEvent) {
        getEnvironment().assignEventOn(target, physicalEvent, randomLong(minSendDelay, maxSendDelay));
    }
    
    // Getters.
    
    public long getMinSendDelay() {
        return minSendDelay;
    }
    
    public long getMaxSendDelay() {
        return maxSendDelay;
    }
}
