package jp.unaguna.classloader.cmd.validator

import com.beust.jcommander.IParameterValidator
import com.beust.jcommander.ParameterException

class NonOption : IParameterValidator {
    override fun validate(name: String, value: String?) {
        if (value?.startsWith("-") == true) {
            throw ParameterException("Unknown option: $value")
        }
    }
}
