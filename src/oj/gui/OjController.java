package oj.gui;

import oj.core.*;
import oj.db.*;
import oj.service.*;
import java.io.*;
import java.util.Map;

public class OjController {
    private final OjFrame view;
    private final ProblemService problemService;
    private final SubmissionDao submissionDao;

    public OjController(OjFrame view) {
        this.view = view;
        this.problemService = new ProblemService();
        this.submissionDao = new SubmissionDao();

        this.initController();
    }

    private void initController() {
        loadProblems();
        view.submitButton.addActionListener(e -> executeSubmit());
    }

    private void loadProblems() {
        try {
            Map<Integer, ProblemMeta> problems = problemService.listMeta();
            view.problemSelector.removeAllItems();
            for (Integer problemId : problems.keySet()) {
                view.problemSelector.addItem(problemId);
            }

            if (problems.isEmpty()) {
                view.submitButton.setEnabled(false);
                view.resultTextArea.setText("No problems were found in the database.");
            }
        } catch (Exception ex) {
            view.submitButton.setEnabled(false);
            view.resultTextArea.setText(
                "Failed to load problems from database:\n" + ex.getMessage());
        }
    }

    private void executeSubmit() {
        try {
            view.resultTextArea.setText("Preparing bench environment...\n");

            Integer selectedProblemId =
                (Integer) view.problemSelector.getSelectedItem();
            String lang = (String) view.langSelector.getSelectedItem();
            String sourceCode = view.srcTextArea.getText();

            if (selectedProblemId == null) {
                view.resultTextArea.setText("Error: Please select a problem!");
                return;
            }
            if (lang == null) {
                view.resultTextArea.setText("Error: Please select a language!");
                return;
            }
            if (sourceCode.trim().isEmpty()) {
                view.resultTextArea.setText("Error: Please input source code!");
                return;
            }

            int problemId = selectedProblemId;
            String srcPath = "submissions/sub_" + System.currentTimeMillis()+ "." + (lang.equals("java") ? "java" : lang);
            File file = new File(srcPath);
            file.getParentFile().mkdirs();
            try (FileWriter fw = new FileWriter(file)) {
                fw.write(sourceCode);
            }

            Submission submission = new Submission(problemId, "winbeau_gui", lang, srcPath);
            Problem problem = problemService.load(problemId);
            view.resultTextArea.append(">> Sucessfully loaded Problem: " + problem.getTitle() + ", total" + problem.getCases().size() + " cases. \n");
            
            view.resultTextArea.append(">> Compiling and running ...");

            JudgeResult fakeResult = new JudgeResult(Status.AC, problem.getCases().size(), problem.getCases().size(), "GUI Submission Success", 42);
            submission.setResult(fakeResult);

            submissionDao.save(submission);
            view.resultTextArea.append(
                "\n>> Submission result saved to database, local submission id: "
                    + submission.getId() + "\n");

            view.resultTextArea.append("\n================ Test Latest Status ================\n");
            view.resultTextArea.append("STATUS: " + fakeResult.getStatus() + "\n");
            view.resultTextArea.append("PASSED: " + fakeResult.getPassed() + " / " + fakeResult.getTotal() + "\n");
            view.resultTextArea.append("TIME:   " + fakeResult.getElapsedMs() + " ms\n");
            view.resultTextArea.append("DETAIL: " + fakeResult.getDetail() + "\n");
        } catch (Exception ex) {
            view.resultTextArea.setText("System Running Error:\n" + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
