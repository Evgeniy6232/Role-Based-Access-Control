package com.evgenii.rbac.command;

import java.util.List;
import java.util.Map;

//Structure of the flags parser output
public record ArgumentSet(
        Map<String, List<String>> flags, // Name flag and args
        List<String> baseArgs // Arg that do not have flasg
) {}
