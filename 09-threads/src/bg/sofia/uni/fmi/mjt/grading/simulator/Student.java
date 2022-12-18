package bg.sofia.uni.fmi.mjt.grading.simulator;

import bg.sofia.uni.fmi.mjt.grading.simulator.assignment.Assignment;
import bg.sofia.uni.fmi.mjt.grading.simulator.assignment.AssignmentType;
import bg.sofia.uni.fmi.mjt.grading.simulator.grader.StudentGradingAPI;

import java.util.Random;

public class Student implements Runnable {

    private static final int OPTION_ONE = 0;
    private static final int OPTION_TWO = 1;
    private static final int OPTION_THREE = 2;
    private static final int OPTION_FOUR = 3;
    private static final int TOTAL_OPTIONS = 4;
    private static final int MAX_WAIT_TIME = 501;
    private int fn;
    private String name;
    private StudentGradingAPI studentGradingAPI;

    public Student(int fn, String name, StudentGradingAPI studentGradingAPI) {
        this.fn = fn;
        this.name = name;
        this.studentGradingAPI = studentGradingAPI;
    }

    @Override
    public void run() {
        Random rnd = new Random();
        var type = switch (rnd.nextInt(0, TOTAL_OPTIONS)) {
            case OPTION_ONE -> AssignmentType.LAB;
            case OPTION_TWO -> AssignmentType.HOMEWORK;
            case OPTION_THREE -> AssignmentType.PROJECT;
            case OPTION_FOUR -> AssignmentType.PLAYGROUND;
            default -> throw new IllegalStateException("Unexpected value: " + rnd.nextInt(0, TOTAL_OPTIONS));
        };

        try {
            Thread.sleep(rnd.nextInt(0, MAX_WAIT_TIME));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        studentGradingAPI.submitAssignment(new Assignment(fn, name, type));
    }

    public int getFn() {
        return fn;
    }

    public String getName() {
        return name;
    }

    public StudentGradingAPI getGrader() {
        return studentGradingAPI;
    }

}