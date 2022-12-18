package bg.sofia.uni.fmi.mjt.grading.simulator.grader;

import bg.sofia.uni.fmi.mjt.grading.simulator.Assistant;
import bg.sofia.uni.fmi.mjt.grading.simulator.assignment.Assignment;

import java.util.ArrayList;
import java.util.List;


public class CodePostGrader implements AdminGradingAPI {

    List<Assignment> assignments;
    List<Assistant> assistants;

    int submittedAssignmentsCount;
    boolean isFinalized;

    public CodePostGrader(int numberOfAssistants) {
        assistants = new ArrayList<>();
        assignments = new ArrayList<>();

        for (int i = 0; i < numberOfAssistants; i++) {
            assistants.add(new Assistant("Ass #" + i, this));
            assistants.get(i).start();
        }
    }

    @Override
    public synchronized Assignment getAssignment() {

        if (isFinalized) {
            return null;
        }

        while (assignments.size() == 0) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (isFinalized) {
                return null;
            }
        }

        Assignment assignment = assignments.get(0);
        assignments.remove(0);
        return assignment;
    }

    @Override
    public synchronized int getSubmittedAssignmentsCount() {
        return submittedAssignmentsCount;
    }

    @Override
    public synchronized void finalizeGrading() {
        isFinalized = true;
        this.notifyAll();
    }

    @Override
    public synchronized List<Assistant> getAssistants() {
        return assistants;
    }

    @Override
    public synchronized void submitAssignment(Assignment assignment) {
        if (isFinalized) {
            return;
        }
        assignments.add(assignment);
        submittedAssignmentsCount++;
        this.notifyAll();
    }
}
