package jp.unaguna.classloader.core

interface ClassReducerType<R>

interface ClassReducer<E, S : ScannedClassElement<E>, R> {
    /**
     * reducer の種類を表す。
     *
     * この値が同一であれば、同一のクラス群に対して同じ計算結果を出さなくてはならない。
     */
    val type: ClassReducerType<R>
    val value: R

    fun apply(element: S)
}

abstract class ClassCounter<E, S : ScannedClassElement<E>> : ClassReducer<E, S, Int> {
    private var cnt: Int = 0
    override val value: Int
        get() = cnt

    fun clear() {
        this.cnt = 0
    }

    abstract fun matches(element: S): Boolean

    override fun apply(element: S) {
        if (matches(element)) {
            cnt += 1
        }
    }
}

abstract class ClassMaximumReducer<E, S : ScannedClassElement<E>, R : Comparable<R>> : ClassReducer<E, S, R> {
    open val initialValue: R? = null
    private var maximum = initialValue
    override val value
        get() = maximum ?: throw NoSuchElementException()

    override fun apply(element: S) {
        val targetValue = targetValueOf(element)
        if (maximum == null || maximum!! < targetValue) {
            maximum = targetValue
        }
    }

    abstract fun targetValueOf(element: S): R
}

interface ClassReducerFactory<E, S : ScannedClassElement<E>, R> {
    /**
     * reducer の種類を表す。
     *
     * このファクトリが生成する reducer の type も同一の値である必要がある。
     */
    val type: ClassReducerType<R>

    fun create(): ClassReducer<E, S, R>
}
