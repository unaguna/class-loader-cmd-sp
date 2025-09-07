package jp.unaguna.classloader.core

abstract class ClassCounter<E, S : ScannedClassElement<E>> {
    private var cnt: Int = 0
    val count: Int
        get() = cnt

    fun clear() {
        this.cnt = 0
    }

    abstract fun matches(element: S): Boolean

    fun countIfMatch(element: S) {
        if (matches(element)) {
            cnt += 1
        }
    }
}

interface ClassCounterFactory<E, S : ScannedClassElement<E>> {
    fun create(): ClassCounter<E, S>
}
