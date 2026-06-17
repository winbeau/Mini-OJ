package oj.core;

import java.io.Serializable;

public class Submission implements Serializable {
    private static final long serialVersionUID = 1L;
    private static int counter = 0;

    private final int id;
    private final int problemId;
    private final String userName;
    private final String lang;
    private final String srcPath;
    private JudgeResult result;

    public Submission(int problemId, String userName, String lang, String srcPath) {
        this.id = ++counter;
        this.problemId = problemId;
        this.userName = userName;
        this.lang = lang;
        this.srcPath = srcPath;
    }

    public int getId() {
        return id;
    }

    public int getProblemId() {
        return problemId;
    }

    public String getUserName() {
        return userName;
    }

    public String getLang() {
        return lang;
    }

    public String getSrcPath() {
        return srcPath;
    }

    public JudgeResult getResult() {
        return result;
    }

    public void setResult(JudgeResult result) {
        this.result = result;
    }

    public static int count() {
        return counter;
    }
}
