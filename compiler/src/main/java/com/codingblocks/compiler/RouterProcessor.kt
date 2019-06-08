package com.codingblocks.compiler

import com.codingblocks.annotation.RouterMap
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.WildcardTypeName
import java.util.*
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.annotation.processing.FilerException
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.tools.Diagnostic


@AutoService(Processor::class)
class RouterProcessor : AbstractProcessor() {
    private var mMessager: Messager? = null
    private var mFiler: Filer? = null
    private var elementUtils: Elements? = null

    val supportedAnnotationTypes: Set<String>
        get() = Collections.singleton(RouterMap::class.java!!.getCanonicalName())

    val supportedSourceVersion: SourceVersion
        get() = SourceVersion.latestSupported()

    @Synchronized
    fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        mMessager = processingEnv.messager
        mFiler = processingEnv.filer
        elementUtils = processingEnv.elementUtils
    }

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        val elements = roundEnv.getElementsAnnotatedWith(RouterMap::class.java)

        try {
            val type = getRouterTableInitializer(elements)
            if (type != null) {
                JavaFile.builder("cn.campusapp.router.router", type).build().writeTo(mFiler)
            }
        } catch (e: FilerException) {
            e.printStackTrace()
        } catch (e: Exception) {
            error(e.message)
        }

        return true
    }

    @Throws(ClassNotFoundException::class, TargetErrorException::class)
    private fun getRouterTableInitializer(elements: Set<Element>?): TypeSpec? {
        if (elements == null || elements.size == 0) {
            return null
        }
        val activityType = elementUtils!!.getTypeElement("android.app.Activity")

        val mapTypeName = ParameterizedTypeName
            .get(
                ClassName.get(Map<*, *>::class.java),
                ClassName.get(String::class.java),
                ParameterizedTypeName.get(
                    ClassName.get(Class<*>::class.java),
                    WildcardTypeName.subtypeOf(ClassName.get(activityType))
                )
            )
        val mapParameterSpec = ParameterSpec.builder(mapTypeName, "router")
            .build()
        val routerInitBuilder = MethodSpec.methodBuilder("initRouterTable")
            .addAnnotation(Override::class.java)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(mapParameterSpec)
        for (element in elements) {
            if (element.getKind() !== ElementKind.CLASS) {
                throw TargetErrorException()
            }
            val router = element.getAnnotation(RouterMap::class.java)
            val routerUrls = router.value
            if (routerUrls != null) {
                for (routerUrl in routerUrls!!) {
                    routerInitBuilder.addStatement(
                        "router.put(\$S, \$T.class)",
                        routerUrl,
                        ClassName.get(element as TypeElement)
                    )
                }
            }
        }
        val routerInitMethod = routerInitBuilder.build()
        val routerInitializerType =
            elementUtils!!.getTypeElement("cn.campusapp.router.router.IActivityRouteTableInitializer")
        return TypeSpec.classBuilder("AnnotatedRouterTableInitializer")
            .addSuperinterface(ClassName.get(routerInitializerType))
            .addModifiers(Modifier.PUBLIC)
            .addMethod(routerInitMethod)
            .build()
    }


    private fun error(error: String) {
        mMessager!!.printMessage(Diagnostic.Kind.ERROR, error)
    }


}