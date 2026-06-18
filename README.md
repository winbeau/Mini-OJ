# Mini-OJ

Current stage: M5b.

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
java -cp "build:lib/*" oj.Main
```

Pass one or more problem IDs to run selected samples:

```bash
java -cp "build:lib/*" oj.Main 1002 1004
```
