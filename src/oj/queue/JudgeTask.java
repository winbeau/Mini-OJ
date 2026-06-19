package oj.queue;

import oj.core.*;

public class JudgeTask {
    private final Submission submission;
    private final Problem problem;

    public JudgeTask(Submission submission, Problem problem) {
        this.submission = submission;
        this.problem = problem;
    }

    public Submission getSubmission() {
        return submission;
    }

    public Problem getProblem() {
        return problem;
    }
}
