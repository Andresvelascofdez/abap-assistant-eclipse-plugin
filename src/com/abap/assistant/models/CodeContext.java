package com.abap.assistant.models;

import java.util.*;

/**
 * Clase que representa el contexto completo de un archivo de código ABAP
 * Incluye información sobre estructura, variables, dependencias y metadatos
 */
public class CodeContext {
    private String fileName;
    private String programType;
    private List<String> classNames;
    private List<String> includes;
    private List<String> methodCalls;
    private List<VariableInfo> variables;
    private List<DependencyInfo> dependencies;
    private Map<String, String> metadata;

    public CodeContext(String fileName) {
        this.fileName = fileName;
        this.classNames = new ArrayList<>();
        this.includes = new ArrayList<>();
        this.methodCalls = new ArrayList<>();
        this.variables = new ArrayList<>();
        this.dependencies = new ArrayList<>();
        this.metadata = new HashMap<>();
    }

    // Getters y setters
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getProgramType() { return programType; }
    public void setProgramType(String programType) { this.programType = programType; }

    public List<String> getClassNames() { return classNames; }
    public void addClassName(String className) { this.classNames.add(className); }

    public List<String> getIncludes() { return includes; }
    public void addInclude(String include) { this.includes.add(include); }

    public List<String> getMethodCalls() { return methodCalls; }
    public void addMethodCall(String methodCall) { this.methodCalls.add(methodCall); }

    public List<VariableInfo> getVariables() { return variables; }
    public void addVariable(VariableInfo variable) { this.variables.add(variable); }

    public List<DependencyInfo> getDependencies() { return dependencies; }
    public void addDependency(DependencyInfo dependency) { this.dependencies.add(dependency); }

    public Map<String, String> getMetadata() { return metadata; }
    public void addMetadata(String key, String value) { this.metadata.put(key, value); }

    /**
     * Busca una variable por nombre
     */
    public VariableInfo findVariable(String name) {
        return variables.stream()
            .filter(v -> v.getName().equalsIgnoreCase(name))
            .findFirst()
            .orElse(null);
    }

    /**
     * Obtiene dependencias por tipo
     */
    public List<DependencyInfo> getDependenciesByType(String type) {
        return dependencies.stream()
            .filter(d -> d.getType().equals(type))
            .collect(ArrayList::new, (list, item) -> list.add(item), (list1, list2) -> list1.addAll(list2));
    }

    /**
     * Genera un resumen textual del contexto
     */
    public String generateSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Archivo: ").append(fileName).append("\n");
        summary.append("Tipo: ").append(programType).append("\n");
        summary.append("Clases: ").append(classNames.size()).append("\n");
        summary.append("Variables: ").append(variables.size()).append("\n");
        summary.append("Dependencias: ").append(dependencies.size()).append("\n");
        return summary.toString();
    }

    @Override
    public String toString() {
        return "CodeContext{" +
            "fileName='" + fileName + '\'' +
            ", programType='" + programType + '\'' +
            ", classNames=" + classNames.size() +
            ", variables=" + variables.size() +
            ", dependencies=" + dependencies.size() +
            '}';
    }
}
