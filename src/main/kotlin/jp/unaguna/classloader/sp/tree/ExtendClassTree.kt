package jp.unaguna.classloader.sp.tree

import jp.unaguna.classloader.sp.ClassFileMetadata

class ExtendClassTree() : ClassTree<ClassFileMetadata>() {
    override val ignoreRoot: Boolean = true
    private val nameMap: MutableMap<String, ExtendTreeNode> = mutableMapOf()
    /** クラス名とそのクラスを継承するサブクラスのマップ */
    private val superClsNameMap: MutableMap<String, MutableList<ExtendTreeNode>> = mutableMapOf()
    private val constRoot = ExtendTreeNode(null)

    init {
        // root が1つになるとは限らないので、ダミーの root を置き、その子を実質的なrootとする。
        this.root = constRoot
    }

    override fun isEmpty(): Boolean {
        return this.constRoot.hasNoChild()
    }

    fun append(el: ClassFileMetadata) {
        val className = el.classMetadata.className
        // すでにツリーに入っているならなにもしない
        if (nameMap.contains(className)) {
            return
        }

        val newNode = ExtendTreeNode(el)

        val superClassName = el.classMetadata.superClassName ?: ""

        val existSuperClassNode = nameMap[superClassName]
        val existSubClassNodes = superClsNameMap[className] ?: emptyList()

        // このアルゴリズムは A->B->C の継承関係があるときに A->C のリンクができることはないので、
        // this が A の親クラスであれば A の親はまだない (rootが親) はずである。
        if (existSubClassNodes.any { it.parentNode != constRoot }) {
            error("Illegal tree state")
        }

        existSubClassNodes.forEach {
            it.parentNode?.childNodes?.remove(it)
            it.parentNode = newNode
            newNode.childNodes.add(it)
        }
        if (existSuperClassNode != null) {
            existSuperClassNode.addChild(newNode)
            newNode.parentNode = existSuperClassNode
        } else {
            constRoot.addChild(newNode)
            newNode.parentNode = constRoot
        }
        superClsNameMap.getOrPut(superClassName) { mutableListOf() }.add(newNode)
        nameMap[className] = newNode

    }

    fun appendAll(els: Iterable<ClassFileMetadata>) {
        els.forEach { append(it) }
    }
}

class ExtendTreeNode(private val el: ClassFileMetadata?) : ClassTreeNode<ClassFileMetadata> {
    var parentNode: ExtendTreeNode? = null
    val childNodes = mutableListOf<ExtendTreeNode>()

    override val element: ClassFileMetadata
        get() = el ?: error("dummy element has been referenced")

    override fun parent(): ClassFileMetadata? {
        return this.parentNode?.element
    }

    override fun parentNode(): ClassTreeNode<ClassFileMetadata>? {
        return this.parentNode
    }

    override fun children(): Iterable<ClassFileMetadata> {
        return childNodes.map { it.element }
    }

    override fun childNode(index: Int): ExtendTreeNode {
        return childNodes[index]
    }

    override fun hasNoChild(): Boolean {
        return childNodes.isEmpty()
    }

    override fun childNum(): Int {
        return childNodes.size
    }

    fun addChild(node: ExtendTreeNode) {
        childNodes.add(node)
    }
}
