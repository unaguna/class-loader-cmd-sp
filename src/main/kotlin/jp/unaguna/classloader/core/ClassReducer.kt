package jp.unaguna.classloader.core

interface ClassReducer<E, S : ScannedClassElement<E>, CT, R> {
    /**
     * reducer の種類を表す。
     *
     * この値が同一であれば、同一のクラス群に対して同じ計算結果を出さなくてはならない。
     */
    val type: CT
    val value: R

    fun apply(element: S)
}

abstract class ClassCounter<E, S : ScannedClassElement<E>, CT>: ClassReducer<E, S, CT, Int> {
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

interface ClassReducerFactory<E, S : ScannedClassElement<E>, CT, R> {
    /**
     * reducer の種類を表す。
     *
     * このファクトリが生成する reducer の type も同一の値である必要がある。
     */
    val type: CT

    fun create(): ClassReducer<E, S, CT, R>
}
