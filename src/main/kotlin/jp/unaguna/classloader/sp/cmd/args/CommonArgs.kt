package jp.unaguna.classloader.sp.cmd.args

import com.beust.jcommander.Parameter

class CommonArgs {
    @Parameter(names = ["--help"], help = true)
    var help = false

    @Parameter(names = ["--version"], help = true)
    var version = false

    @Parameter(names = ["-cp", "--classpath"], description = "the classpath to scan")
    var classpath: String? = null
}
