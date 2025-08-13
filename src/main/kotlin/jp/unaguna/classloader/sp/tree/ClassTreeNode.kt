package jp.unaguna.classloader.sp.tree

interface ClassTreeNode<E> {
    val element: E
    fun parent(): E?
    fun parentNode(): ClassTreeNode<E>?
    fun children(): Iterable<E>
    fun childNode(index: Int): ClassTreeNode<E>
    fun hasNoChild(): Boolean
    fun hasChild(): Boolean {
        return !hasNoChild()
    }
    fun childNum(): Int
    fun hasChildIndexed(index: Int): Boolean {
        return index < childNum()
    }
}
