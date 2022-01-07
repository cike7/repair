package com.mqp.repair;

/**
 * bug测试类
 */
public class BugTest {

    @Replace(clazz = "com.example.bthvi.xxx.BugTest", method = "getBug")
    public String getBug() {
//        return "有bug";
        return "修复bug";
    }

}

