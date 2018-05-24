package com.face.jfshare.annotationlib;

import com.face.jfshare.annotationlib.annotation.FindId;
import com.face.jfshare.annotationlib.annotation.OnClick;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;

@AutoService(Processor.class)
public class FindIdProcessor extends AbstractProcessor {


    private Filer filer;
    private Elements elementUtils;
    private Messager messager;
    /**
     * 一个需要生成的类的集合（key为类的全名，value为该类所有相关的需要的信息）
     */
    private HashMap<String,ProxyInfo> proxyInfos = new HashMap<>();

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotationType = new LinkedHashSet<>();
        annotationType.add(FindId.class.getCanonicalName());
        annotationType.add(OnClick.class.getCanonicalName());
        return annotationType;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        filer = processingEnvironment.getFiler();
        elementUtils = processingEnvironment.getElementUtils();
        messager = processingEnvironment.getMessager();

    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        collectionInfo(roundEnvironment);
        generateClass();
        return false;
    }



    private void collectionInfo (RoundEnvironment roundEnvironment){
        proxyInfos.clear();
        //获得被该注解声明的类和变量
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(FindId.class);

        for (Element element : elements) {
            if (element.getKind() == ElementKind.CLASS) {  //当前类型是类
                TypeElement typeElement = (TypeElement) element;
                //获取类完整的路径
                String qualifiedName = typeElement.getQualifiedName().toString();
                //类名
                String className = typeElement.getSimpleName().toString();
                //获取包名
                String packageName = elementUtils.getPackageOf(typeElement).getQualifiedName().toString();
                FindId findId = typeElement.getAnnotation(FindId.class);
                if (findId != null) {
                    int value = findId.value();
                    ProxyInfo proxyInfo = proxyInfos.get(qualifiedName);
                    if (proxyInfo == null) {
                        proxyInfo = new ProxyInfo();
                        proxyInfos.put(qualifiedName,proxyInfo);
                    }

                    proxyInfo.value = value;
                    proxyInfo.typeElement = typeElement;
                    proxyInfo.packageName = packageName;
                }
            }else if (element.getKind() == ElementKind.FIELD) {
                FindId findIds = element.getAnnotation(FindId.class);
                if (findIds != null) {
                    int value = findIds.value();

                    VariableElement variableElement = (VariableElement) element;
                    String qualified = ((TypeElement) element.getEnclosingElement()).getQualifiedName().toString();
                    ProxyInfo proxyInfo = proxyInfos.get(qualified);
                    if (proxyInfo == null) {
                        proxyInfo = new ProxyInfo();
                        proxyInfos.put(qualified,proxyInfo);
                    }

                    proxyInfo.variableElements.put(value,variableElement);


                }else {
                    continue;
                }
            }
        }

        Set<? extends Element> onClicks = roundEnvironment.getElementsAnnotatedWith(OnClick.class);
        for (Element element : onClicks) {

            if (element.getKind() == ElementKind.METHOD) {
                OnClick onClick = element.getAnnotation(OnClick.class);
                if (onClick != null) {
                    ExecutableElement executableElement = (ExecutableElement) element;
                    String qualifiedName= ((TypeElement) element.getEnclosingElement()).getQualifiedName().toString();
                    ProxyInfo proxyInfo = proxyInfos.get(qualifiedName);
                    if (proxyInfo == null) {
                        proxyInfo = new ProxyInfo();
                        proxyInfos.put(qualifiedName,proxyInfo);
                    }

                    int[] value = onClick.value();
                    for (Integer i : value) {
                        proxyInfo.executableElements.put(i,executableElement);
                    }
                }
            }else {
                continue;
            }
        }


    }


    private void  generateClass(){
        for (String key : proxyInfos.keySet()) {
            ProxyInfo proxyInfo = proxyInfos.get(key);
            JavaFileObject sourceFile = null;

            try {
                sourceFile = filer.createSourceFile(proxyInfo.getProxyClassFullName(),proxyInfo.typeElement);

                Writer writer = sourceFile.openWriter();
                writer.write(proxyInfo.generateJavaCode());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


}
