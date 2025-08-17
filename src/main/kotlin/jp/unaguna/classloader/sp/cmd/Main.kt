package jp.unaguna.classloader.sp.cmd

import com.beust.jcommander.JCommander
import jp.unaguna.classloader.sp.cmd.args.CommonArgs
import jp.unaguna.classloader.sp.cmd.args.LsClasses

class Main {
    val args = CommonArgs()
    val subcommands = listOf(
        LsClasses(),
    ).associateBy { it.name }

    @Suppress("SpreadOperator")
    fun run(argv: Array<String>) {
        val commander = JCommander.newBuilder()
            .apply {
                addObject(args)
                subcommands.forEach { name, subArgs ->
                    addCommand(name, subArgs)
                }
            }
            .build()

        commander.parse(*argv)

        if (!args.help && args.version) {
            val version = Version().getVersion()
            println("Class Loader Command ${version ?: ""}")
            return
        }
        if (args.help || commander.parsedCommand == null) {
            commander.usage()
            return
        }

        subcommands[commander.parsedCommand]!!.execute(args)
    }

    companion object {
        @Suppress("MemberNameEqualsClassName")
        @JvmStatic
        fun main(argv: Array<String>) {
            Main().run(argv)
        }
    }
}
