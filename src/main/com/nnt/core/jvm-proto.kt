package com.nnt.core

import kotlin.reflect.KClass

class JvmProtoClass : JvmClass() {

    override fun calcDepends(classes: Map<KClass<*>, JvmClass>) {
        super.calcDepends(classes)

        val fps = GetAllFields(clazz)
        if (fps != null) {
            fps.forEach { _, fp ->
                if (fp.valtype == null)
                    return@forEach
                val tgt = classes[fp.valtype]
                if (tgt == null)
                    return@forEach
                if (tgt.depends == null)
                    tgt.depends = mutableSetOf()
                tgt.depends!!.add(this)
            }
        }
    }
}

class JvmProtoPackage : JvmPackage() {

    protected override fun instanceClass(): JvmClass {
        return JvmProtoClass()
    }

    protected override fun instancePackage(): JvmPackage {
        return JvmProtoPackage()
    }
}