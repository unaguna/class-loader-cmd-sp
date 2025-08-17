package jp.unaguna.classloader.sp.metaloader

import jp.unaguna.classloader.core.Visibility
import org.springframework.asm.ClassReader
import org.springframework.asm.ClassVisitor
import org.springframework.asm.FieldVisitor
import org.springframework.asm.Opcodes
import org.springframework.core.io.Resource

class ClassStaticLoader {
    @Suppress("MagicNumber")
    fun load(resource: Resource): StaticClassData {
        resource.inputStream.use { inStream ->
            val serialVersionUIDVisitor = SerialVersionUIDVisitor()
            val cr = ClassReader(inStream).apply {
                accept(
                    serialVersionUIDVisitor,
                    ClassReader.SKIP_CODE or ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES,
                )
            }

            val minor = cr.readUnsignedShort(4)
            val major = cr.readUnsignedShort(6)

            return StaticClassData(
                visibility = when {
                    (cr.access and Opcodes.ACC_PUBLIC) != 0 -> Visibility.PUBLIC
                    (cr.access and Opcodes.ACC_PRIVATE) != 0 -> Visibility.PRIVATE
                    (cr.access and Opcodes.ACC_PROTECTED) != 0 -> Visibility.PROTECTED
                    else -> Visibility.PACKAGE_PRIVATE
                },
                major = major,
                minor = minor,
                serialVersionUID = serialVersionUIDVisitor.value,
            )
        }
    }

    class SerialVersionUIDVisitor : ClassVisitor(Opcodes.ASM9) {
        private var visited: Long? = null

        val value: Long?
            get() = visited

        override fun visitField(
            access: Int,
            name: String,
            descriptor: String?,
            signature: String?,
            value: Any?,
        ): FieldVisitor? {
            if ("serialVersionUID" == name && "J" == descriptor) {
                // descriptor "J" = long 型
                if (value is Long) {
                    this.visited = value
                }
            }
            return super.visitField(access, name, descriptor, signature, value)
        }
    }

}
