package bon

object LogConfig {
  // List of components and levels to hide, separated by comma,
  // could be "HTTP" or "debug" or "HTTP_debug"
  val disable_logs: Set<String> = Env["disable_logs", ""].split(",").to_set()

  fun is_enabled(component: String, level: String): Boolean {
    return !((component in disable_logs) or (level in disable_logs) or
      ("${component}.$level" in disable_logs))
  }
}

class Log (
  val component: String = ""
) {
  fun info(message: String): Void {
    if (!LogConfig.is_enabled(component, "info")) return
    log_stdout("      " + format_component(component) + " " + message)
  }

  fun warn(message: String, error: Throwable? = null): Void {
    if (!LogConfig.is_enabled(component, "warn")) return
    log_stderr("warn  " + format_component(component) + " " + message)
    if (error != null) log_stderr(error) // Utils.get_clean_stack_trace(e)
  }

  fun error(message: String, error: Throwable? = null): Void {
    if (!LogConfig.is_enabled(component, "error")) return
    log_stderr("error " + format_component(component) + " " + message)
    if (error != null) log_stderr(error)
  }

  fun debug(message: String): Void {
    if (!LogConfig.is_enabled(component, "debug")) return
    log_stdout("debug " + format_component(component) + " " + message)
  }
}

fun log_info(message: String): Void {
  if (!LogConfig.is_enabled("", "info")) return
  log_stdout("      " + format_component("") + " " + message)
}

fun log_error(message: String, error: Throwable?): Void {
  if (!LogConfig.is_enabled("", "error")) return
  log_stderr("error " + format_component("") + " " + message)
  if (error != null) log_stderr(error)
}

private fun format_component(component: String): String {
  val padded = StringBuffer(component)
  while (padded.length < 4) padded.append(" ")
  return padded.to_string().substring(0, 4)
}

private fun log_stdout(v: String) { println(v) }

private fun log_stderr(v: String) { System.err.println(v) }

private fun log_stderr(e: Throwable) { e.printStackTrace(System.err) }