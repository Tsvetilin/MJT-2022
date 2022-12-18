package bg.sofia.uni.fmi.mjt.grading.simulator;

import bg.sofia.uni.fmi.mjt.grading.simulator.assignment.Assignment;
import bg.sofia.uni.fmi.mjt.grading.simulator.grader.AdminGradingAPI;

public class Assistant extends Thread {

    private String name;
    private AdminGradingAPI grader;

    private int gradedAssignmentsCount;

    public Assistant(String name, AdminGradingAPI grader) {
        this.name = name;
        this.grader = grader;
        this.gradedAssignmentsCount = 0;
    }

    @Override
    public void run() {
        while (true) {
            Assignment assignment = grader.getAssignment();

            if (assignment == null) {
                return;
            }

            try {
                Thread.sleep(assignment.type().getGradingTime());
                gradedAssignmentsCount++;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public int getNumberOfGradedAssignments() {
        return gradedAssignmentsCount;
    }

}