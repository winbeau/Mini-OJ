package oj.judge;

import oj.core.Problem;
import oj.core.JudgeResult;

public interface Judge {
    JudgeResult judge(Problem problem, Solution solution);
}
