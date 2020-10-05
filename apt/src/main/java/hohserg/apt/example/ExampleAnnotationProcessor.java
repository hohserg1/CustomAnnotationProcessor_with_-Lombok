package hohserg.apt.example;

import com.google.common.collect.ImmutableSet;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.toList;

public class ExampleAnnotationProcessor extends AbstractProcessor {
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;


    private void note(Element e, String msg) {
        messager.printMessage(Diagnostic.Kind.NOTE, msg, e);
    }

    private void error(Element e, String msg) {
        messager.printMessage(Diagnostic.Kind.ERROR, msg, e);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(ExampleMark.class)) {
            if (annotatedElement.getKind() == ElementKind.CLASS) {
                TypeElement typeElement = (TypeElement) annotatedElement;
                note(typeElement, "Found marked element");
                typeElement.getEnclosedElements().stream().filter(e -> e.getKind() == ElementKind.FIELD).map(e -> ((VariableElement) e))
                        .map(e -> "field " + e.asType() + " " + e + ", modifiers" + e.getModifiers()).forEach(e -> note(typeElement, e));

                typeElement.getEnclosedElements().stream().filter(e -> e.getKind() == ElementKind.METHOD).map(e -> ((ExecutableElement) e))
                        .map(e -> "method " + e.getReturnType() + " " + e + ", modifiers" + e.getModifiers()).forEach(e -> note(typeElement, e));

                typeElement.getEnclosedElements().stream().filter(e -> e.getKind() == ElementKind.CONSTRUCTOR).map(e -> ((ExecutableElement) e))
                        .map(e -> "constructor " + e + ", modifiers" + e.getModifiers()).forEach(e -> note(typeElement, e));
            }
        }
        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ImmutableSet.of(ExampleMark.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
