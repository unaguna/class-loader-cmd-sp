package jp.unaguna.classloader.sp.cmd.args

import com.beust.jcommander.Parameter

class CommonArgs {
    @Parameter(names = ["-cp", "--classpath"], description = "The classpath to scan")
    var classpath: String? = null
}
