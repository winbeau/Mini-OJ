package oj.queue;

import oj.core.JudgeResult;
import oj.core.Status;
import oj.db.SubmissionDao;

public class JudgeWorker extends Thread {
    private final JudgeQueue queue = JudgeQueue.getInstance();
    private final SubmissionDao submissionDao = new SubmissionDao();
    private final int workerId;
    private volatile boolean running = true; // volatile 保证多线程可见性

    public JudgeWorker(int workerId) {
        this.workerId = workerId;
        this.setName("JudgeWorker-Thread-" + workerId);
    }

    @Override
    public void run() {
        System.out.println("[" + getName() + "] 判题内核线程已就绪，开始古法监听阻塞队列...");

        while (running) {
            // 1. 从手撸的队列里拿任务。如果队列空，线程在这里交出钥匙进入 WAITING 状态
            JudgeTask task = queue.take();
            if (task == null) continue;

            System.out.println("[" + getName() + "] 成功捕获提交任务！正在评测 Submission ID: " + task.getSubmission().getId());

            try {
                // 2. 模拟评测的耗时（为下一步 ProcessBuilder 唤起 C++ 判题机埋下物理伏笔）
                Thread.sleep(1500);

                // 3. 产生评测结果并回填
                int totalCases = task.getProblem().getCases().size();
                JudgeResult result = new JudgeResult(
                    Status.AC, 
                    totalCases, 
                    totalCases, 
                    "Successfully judged asynchronously by " + getName(), 
                    28
                );
                task.getSubmission().setResult(result);

                // 4. 将最终结果异步持久化到数据库
                submissionDao.save(task.getSubmission());
                System.out.println("[" + getName() + "] 评测完毕，数据已安全回填 MySQL。");

            } catch (InterruptedException e) {
                System.out.println("[" + getName() + "] 收到终止信号，正在退出...");
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void shutdown() {
        this.running = false;
        this.interrupt();
    }
}
