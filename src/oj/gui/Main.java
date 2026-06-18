package oj.gui;

import oj.db.Db;
import oj.db.ProblemDao;
import oj.db.SubmissionDao;
import oj.io.ProblemLoader;
import oj.judge.MachineJudge;
import oj.service.ProblemService;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        ProblemService problemService = new ProblemService(
            new ProblemDao(),
            new ProblemLoader("problems")
        );
        MachineJudge machineJudge = new MachineJudge("judge/judge");
        SubmissionDao submissionDao = new SubmissionDao();

        Runtime.getRuntime().addShutdownHook(new Thread(Db::close, "db-close"));

        SwingUtilities.invokeLater(() -> {
            OjFrame frame = new OjFrame();
            new OjController(
                frame,
                problemService,
                machineJudge,
                submissionDao
            );
            frame.setVisible(true);
        });
    }
}
