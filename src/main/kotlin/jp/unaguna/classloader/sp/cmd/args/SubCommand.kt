package jp.unaguna.classloader.sp.cmd.args

interface SubCommand {
    val name: String
    fun execute(commonArgs: CommonArgs)
}
