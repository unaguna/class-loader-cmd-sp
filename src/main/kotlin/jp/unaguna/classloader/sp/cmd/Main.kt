package jp.unaguna.classloader.sp.cmd

import com.beust.jcommander.JCommander
import jp.unaguna.classloader.sp.cmd.args.CommonArgs
import jp.unaguna.classloader.sp.cmd.args.LsClasses

class Main {
    val args = CommonArgs()
    val subcommands = listOf(
        LsClasses(),
    ).associateBy { it.name }

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
        val subcommand = commander.parsedCommand
        subcommands[subcommand]!!.execute(args)
    }

    companion object {
        @JvmStatic
        fun main(argv: Array<String>) {
            Main().run(argv)
        }
    }
}
