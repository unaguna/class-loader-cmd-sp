package jp.unaguna.classloadersp.tree

abstract class ClassTree<E> {
    protected var root: ClassTreeNode<E>? = null

    open fun isEmpty() = root == null

    fun iterator(): Iterator<Pair<E, Int>> {
        return ClassTreeIterator(this)
    }


    private class ClassTreeIterator<E>(private val tree: ClassTree<E>): Iterator<Pair<E, Int>> {
        var next: ClassTreeNode<E>? = tree.root
        val indexPath: ArrayDeque<Int> = ArrayDeque()

        override fun next(): Pair<E, Int> {
            val node = next ?: throw NoSuchElementException()

            val result = Pair(node.element, indexPath.size)

            // next を計算しておく
            next = null
            if (node.hasChild()) {
                // 子がいれば、次は子
                next = node.childNode(0)
                indexPath.add(0)
            } else {
                // 子がいなければ、上に戻って子を探す
                var parentNode = node.parentNode()

                // 弟妹がいれば次はそれ、いなければ親に戻ってまた弟妹探しの繰り返し
                while (parentNode != null) {
                    val lastIndex = indexPath.removeLast()

                    // 弟妹がいれば次はそれ
                    val elderIndex = lastIndex + 1
                    if (parentNode.hasChildIndexed(elderIndex)) {
                        next = parentNode.childNode(elderIndex)
                        indexPath.addLast(elderIndex)
                        break
                    }

                    // 弟妹がいない場合は親に戻る
                    parentNode = parentNode.parentNode()
                }
            }

            return result
        }

        override fun hasNext(): Boolean {
            return this.next != null
        }

    }

}
