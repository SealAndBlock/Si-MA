package sima.core.scheduler;

public class TestMultiThreadScheduler {

    // Inner classes.

    private class TestSchedulerWatcher implements Scheduler.SchedulerWatcher {

        // Variables.

        private int isPassToSchedulerStarted = 0;
        private int isPassToSchedulerKilled = 0;
        private int isPassToSimulationEndTimeReach = 0;
        private int isPassToNoExecutionToExecute = 0;

        // Methods.

        @Override
        public void schedulerStarted() {
            this.isPassToSchedulerStarted++;
        }

        @Override
        public void schedulerKilled() {
            this.isPassToSchedulerKilled++;
        }

        @Override
        public void simulationEndTimeReach() {
            this.isPassToSimulationEndTimeReach++;
        }

        @Override
        public void noExecutableToExecute() {
            this.isPassToNoExecutionToExecute++;
        }

        public void reset() {
            this.isPassToSchedulerStarted = 0;
            this.isPassToSchedulerKilled = 0;
            this.isPassToSimulationEndTimeReach = 0;
            this.isPassToNoExecutionToExecute = 0;
        }

        // Getters and Setters.

        public int isPassToSchedulerStarted() {
            return isPassToSchedulerStarted;
        }

        public int isPassToSchedulerKilled() {
            return isPassToSchedulerKilled;
        }

        public int isPassToSimulationEndTimeReach() {
            return isPassToSimulationEndTimeReach;
        }

        public int isPassToNoExecutionToExecute() {
            return isPassToNoExecutionToExecute;
        }
    }

}
