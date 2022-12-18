package bg.sofia.uni.fmi.mjt.grading.simulator.grader;

import bg.sofia.uni.fmi.mjt.grading.simulator.Student;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CodePostGraderTest {

    private static final int NUMBER_OF_SUBMISSIONS = 20;

    @Test
    void test() {
        AdminGradingAPI grader = new CodePostGrader(5);

        for (int i = 0; i < NUMBER_OF_SUBMISSIONS; i++) {
            Student st = new Student(i, "" + i, grader);
            st.run();
        }

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        grader.finalizeGrading();
        assertEquals(NUMBER_OF_SUBMISSIONS, grader.getSubmittedAssignmentsCount());
        int sum = 0;
        for (var ass : grader.getAssistants()) {
            sum += ass.getNumberOfGradedAssignments();
        }
        assertEquals(NUMBER_OF_SUBMISSIONS, sum);
    }
}
