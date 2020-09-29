package tech.Astolfo.AstolfoCaffeine.main.util

import com.jagrosh.jdautilities.command.CommandEvent
import tech.Astolfo.AstolfoCaffeine.main.msg.Logging

import java.util.Collections
import java.util.HashMap

class ParamCheckerKotlin {

    private val checks: HashMap<Int, String>

    init {
        checks = HashMap()
    }

    fun addCheck(count: Int, err_message: String): ParamCheckerKotlin {
        checks[count] = err_message
        return this
    }

    fun parse(ctx: CommandEvent): Boolean {
        val params = ctx.args.split("\\s+")
        val errors = Logging()
        val max = Collections.max(checks.keys)
        val min = Collections.min(checks.keys)
        if (params.size < min) return check(min, errors)
        if (params.size > max) return check(max, errors)
        for (i in checks.keys) {
            if (params.size == i) return check(i, errors)
        }
        println("Failed to catch case")
        return false
    }

    private fun check(index: Int, errors: Logging): Boolean {
        if (checks[index] == "VALID") {
            return true
        } else {
            errors.error(checks[index])
            return false
        }
    }
}

