package jp.unaguna.classloader

import org.springframework.util.AntPathMatcher

class ClassNameGrobMatcher(ignoreCase: Boolean) {
    private val pathMatcher: AntPathMatcher = AntPathMatcher(".").apply {
        setCaseSensitive(!ignoreCase)
    }

    fun match(pattern: String, className: String): Boolean {
        return pathMatcher.match(pattern, className)
    }
}
