package nut.jpa;

import nut.jpa.extension.XSuperGenerator;

import java.io.IOException;

public class XSuperGeneratorTest {

    public static void main(String[] args) throws IOException {
        XSuperGenerator.XSuperGeneratorObj obj = new XSuperGenerator.XSuperGeneratorObj();
        obj.setDataSourceUserName("yan");
        obj.setDataSourcePassword("yan123");
        obj.setDataSourceUrl("jdbc:mysql://120.79.100.24:3306/locals_yan");
        obj.setTablePrefix(new String[]{"exp_"});
        obj.setPackageName("nut.example");
        obj.setExcludeTables(new String[]{"schema_version"});
        XSuperGenerator.generate(obj);
    }
}
