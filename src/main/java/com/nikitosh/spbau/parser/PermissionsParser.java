package com.nikitosh.spbau.parser;


import java.util.HashMap;

public interface PermissionsParser {
    HashMap<String, RobotsTxtPermissions> domainPermissions = new HashMap<>();

    Permissions getPermissions(String url);

    void getRobotsTxtPermissions(String url);
}
