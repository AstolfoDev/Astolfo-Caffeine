package tech.Astolfo.AstolfoCaffeine.main.util;

import java.util.HashMap;
import java.util.Collections;
import com.jagrosh.jdautilities.command.CommandEvent;
import tech.Astolfo.AstolfoCaffeine.main.msg.Logging;

public class ParamChecker {

    private HashMap<Integer, String> checks;

    public ParamChecker() {
        checks = new HashMap<>();
    }

    public ParamChecker addCheck(int count, String err_message) {
        checks.put(count, err_message);
        return this;
    }

    public boolean parse(CommandEvent ctx) {
        String[] params = ctx.getArgs().split("\\s+");
        Logging errors = new Logging(ctx);
        int max = Collections.max(checks.keySet());
        int min = Collections.min(checks.keySet());
        if (params.length < min) return check(min, errors);
        if (params.length > max) return check(max, errors);
        for (int i : checks.keySet()) {
            if (params.length == i) return check(i, errors);
        }
        System.out.println("Failed to catch case");
        return false;
    }

    private boolean check(int index, Logging errors) {
        if (checks.get(index).equals("VALID")) {
            return true;
        } else {
            errors.error(checks.get(index));
            return false;
        }
    }
}

