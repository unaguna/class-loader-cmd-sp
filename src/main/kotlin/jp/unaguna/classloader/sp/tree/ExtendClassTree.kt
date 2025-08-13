package jp.unaguna.classloader.sp.tree

import org.springframework.beans.factory.config.BeanDefinition

class ExtendClassTree(private val classLoader: ClassLoader) : ClassTree<BeanDefinition>() {
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

    fun append(bd: BeanDefinition) {
        val className = bd.beanClassName!!
        // すでにツリーに入っているならなにもしない
        if (nameMap.contains(className)) {
            return
        }

        val newNode = ExtendTreeNode(bd)

        val cls = try {
            classLoader.loadClass(className)
        } catch (e: ClassNotFoundException) {
            null
        } catch (e: NoClassDefFoundError) {
            null
        }
        val superClassName = cls?.superclass?.name ?: ""

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

    fun appendAll(bd: Iterable<BeanDefinition>) {
        bd.forEach { append(it) }
    }
}

class ExtendTreeNode(private val el: BeanDefinition?) : ClassTreeNode<BeanDefinition> {
    var parentNode: ExtendTreeNode? = null
    val childNodes = mutableListOf<ExtendTreeNode>()

    override val element: BeanDefinition
        get() = el ?: error("dummy element has been referenced")

    override fun parent(): BeanDefinition? {
        return this.parentNode?.element
    }

    override fun parentNode(): ClassTreeNode<BeanDefinition>? {
        return this.parentNode
    }

    override fun children(): Iterable<BeanDefinition> {
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
