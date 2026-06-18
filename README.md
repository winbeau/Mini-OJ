# Mini-OJ

Current stage: M5c.

## Data Responsibilities

- MySQL: problem metadata and submission history
- Filesystem: test cases and submitted source files
- `ProblemService`: combines database metadata with filesystem test cases

## Database

MySQL connection: `root/root`, database `minioj`, port `3306`.

```bash
mysql -h127.0.0.1 -P3306 -uroot -proot < schema.sql
mysql -h127.0.0.1 -P3306 -uroot -proot < sample-data.sql
```

## Build And Run

```bash
make -C judge
javac -cp "lib/*" -d build $(find src -name '*.java')
java -cp "build:lib/*" oj.gui.Main
```

The Swing client loads problem metadata from MySQL, reads test cases from the
filesystem, invokes the external C++ judge in a `SwingWorker`, and stores each
result through `SubmissionDao`.

If the database is provided by the local container, start it first with
`docker start mysql-house`.
