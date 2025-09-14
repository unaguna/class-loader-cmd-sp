package jp.unaguna.classloader.core

abstract class ClassCounter<E, S : ScannedClassElement<E>, CT> {
    /**
     * カウント対象を示す。
     *
     * この値が同一/相違は [countIfMatch] の同一/相違に一致する。
     * また、このカウンタを生成する [ClassCounterFactory] の type も同一の値である必要がある。
     */
    abstract val type: CT

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

interface ClassCounterFactory<E, S : ScannedClassElement<E>, CT> {
    /**
     * カウント対象を示す。
     *
     * このファクトリが生成するカウンタの type も同一の値である必要がある。
     */
    val type: CT

    fun create(): ClassCounter<E, S, CT>
}
