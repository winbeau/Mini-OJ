package oj.judge;

public class JudgeFactory {

    public static Judge create(String className) throws ReflectiveOperationException {
        Class<?> clazz = Class.forName(className);
        return (Judge) clazz.getDeclaredConstructor().newInstance();
    }
}
