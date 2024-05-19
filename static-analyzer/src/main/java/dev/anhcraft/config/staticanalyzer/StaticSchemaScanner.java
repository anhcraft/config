package dev.anhcraft.config.staticanalyzer;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.resolution.TypeSolver;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import org.jetbrains.annotations.NotNull;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class StaticSchemaScanner {

  public @NotNull StaticSchema scanSchema(@NotNull InputStream inputStream) {
    return null;
  }

  public static void analyzeJavaSource(InputStream inputStream) {
    // Configure JavaParser to use type resolution
    TypeSolver reflectionTypeSolver = new ReflectionTypeSolver();
    TypeSolver javaParserTypeSolver = new JavaParserTypeSolver(new File("C:\\Users\\huynh\\IdeaProjects\\config\\core\\src\\main\\java"));

    CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
    combinedTypeSolver.add(reflectionTypeSolver);
    combinedTypeSolver.add(javaParserTypeSolver);

    JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);

    // Parse the Java source file
    ParseResult<CompilationUnit> parseResult = new JavaParser(new ParserConfiguration().setSymbolResolver(symbolSolver)).parse(inputStream);
    if (parseResult.isSuccessful()) {
      CompilationUnit cu = parseResult.getResult().get();
      // Visit and analyze field declarations
      new FieldVisitor().visit(cu, null);
    } else {
      // Handle parse errors
      parseResult.getProblems().forEach(System.out::println);
    }
  }

  // Visitor class to traverse field declarations
  private static class FieldVisitor extends VoidVisitorAdapter<Void> {
    @Override
    public void visit(FieldDeclaration fd, Void arg) {
      super.visit(fd, arg);
      // Get field type and name
      String resolvedType = fd.getVariables().get(0).getType().resolve().describe();
      String fieldName = fd.getVariables().get(0).getNameAsString();
      System.out.println("Field Name: " + fieldName);
      System.out.println("Field Type: " + resolvedType);
      // Print associated annotations if any
      fd.getAnnotations().forEach(annotation -> System.out.println("Annotation: " + annotation.getNameAsString()));
      System.out.println("------------------------");
    }
  }

}
