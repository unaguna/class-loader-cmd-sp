package jp.unaguna.classloader.sp.cmd

import java.util.Properties

class Version {
    private val props = Properties().also { props ->
        javaClass.classLoader.getResourceAsStream("class-loader-cmd/version.properties")
            ?.use(props::load)
    }

    fun getVersion(): String? {
        return props.getProperty("version", null)
    }
}
