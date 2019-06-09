package com.codingblocks.compiler

import com.codingblocks.annotation.RouterMap
import com.google.auto.service.AutoService
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeSpec
import com.squareup.javapoet.WildcardTypeName
import java.util.*
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
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

    private lateinit var mMessager: Messager
    private lateinit var mFiler: Filer
    private lateinit var elementUtils: Elements

    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)

        mMessager = processingEnv.messager
        mFiler = processingEnv.filer
        elementUtils = processingEnv.elementUtils

    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return Collections.singleton(RouterMap::class.java.canonicalName)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun process(
        annotations: MutableSet<out TypeElement>,
        roundEnv: RoundEnvironment
    ): Boolean {
        val elements = roundEnv.getElementsAnnotatedWith(RouterMap::class.java)
        val type = getRouterTableInitializer(elements)
        if (type != null) {
            JavaFile.builder("com.codingblocks.routerblocks.router", type).build().writeTo(mFiler)
        }

        return true

    }

    private fun getRouterTableInitializer(elements: MutableSet<out Element>): TypeSpec? {
        if (!elements.isNullOrEmpty()) {
            return null
        }

        val activityType = elementUtils.getTypeElement("android.app.Activity")

        val mapTypeName = ParameterizedTypeName
            .get(
                ClassName.get(Map::class.java),
                ClassName.get(String::class.java),
                ParameterizedTypeName.get(
                    ClassName.get(Class::class.java),
                    WildcardTypeName.subtypeOf(ClassName.get(activityType))
                )
            )

        val mapParameterSpec = ParameterSpec.builder(mapTypeName, "router")
            .build()

        val routerInitBuilder = MethodSpec.methodBuilder("initRouterTable")
            .addAnnotation(Override::class.java)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(mapParameterSpec)

        elements.forEach {
            if (it.kind != ElementKind.CLASS) {
                throw Exception()

            }
            val router = it.getAnnotation(RouterMap::class.java)
            val routerUrls = router.value
            if (!routerUrls.isNullOrEmpty()) {
                routerUrls.forEach { routerUrl ->
                    routerInitBuilder.addStatement(
                        "router.put(\$S, \$T.class)",
                        routerUrl,
                        ClassName.get(it as TypeElement)
                    )
                }
            }
        }
        val routerInitMethod = routerInitBuilder.build()
        val routerInitializerType =
            elementUtils.getTypeElement("com.codingblocks.routerblocks.router.ActivityRouteTableInitializer")
        return TypeSpec.classBuilder("AnnotatedRouterTableInitializer")
            .addSuperinterface(ClassName.get(routerInitializerType))
            .addModifiers(Modifier.PUBLIC)
            .addMethod(routerInitMethod)
            .build()

    }


    private fun error(error: String) {
        mMessager.printMessage(Diagnostic.Kind.ERROR, error)
    }

}