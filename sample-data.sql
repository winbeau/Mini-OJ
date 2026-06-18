USE minioj;

INSERT INTO problems (
    id, title, judge_class, time_limit_ms, mem_limit_mb
) VALUES
    (1001, 'Multi-Case A+B', 'oj.judge.StandardJudge', 1500, 256),
    (1002, 'Maximum of Three', 'oj.judge.StandardJudge', 1000, 256),
    (1003, 'Sum from 1 to N', 'oj.judge.StandardJudge', 1000, 256),
    (1004, 'Leap Year Check', 'oj.judge.StandardJudge', 1000, 256)
ON DUPLICATE KEY UPDATE
    title = VALUES(title),
    judge_class = VALUES(judge_class),
    time_limit_ms = VALUES(time_limit_ms),
    mem_limit_mb = VALUES(mem_limit_mb);
