package com.abap.assistant.models;

import java.util.*;

/**
 * Clase que representa información sobre una variable ABAP
 * Incluye detalles sobre declaración, tipo y uso a través del código
 */
public class VariableInfo {
    private String name;
    private String type;
    private int declarationLine;
    private String scope;
    private List<Integer> usageLines;
    private Map<String, String> attributes;
    private boolean isParameter;
    private boolean isFieldSymbol;
    private String defaultValue;

    public VariableInfo(String name, String type, int declarationLine) {
        this.name = name;
        this.type = type != null ? type : "UNKNOWN";
        this.declarationLine = declarationLine;
        this.usageLines = new ArrayList<>();
        this.attributes = new HashMap<>();
        this.scope = "LOCAL";
        this.isParameter = false;
        this.isFieldSymbol = false;
    }

    // Getters y setters básicos
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getDeclarationLine() { return declarationLine; }
    public void setDeclarationLine(int declarationLine) { this.declarationLine = declarationLine; }

    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }

    public List<Integer> getUsageLines() { return usageLines; }
    public void addUsageLine(int lineNumber) { 
        if (!usageLines.contains(lineNumber)) {
            this.usageLines.add(lineNumber); 
        }
    }

    public Map<String, String> getAttributes() { return attributes; }
    public void addAttribute(String key, String value) { this.attributes.put(key, value); }

    public boolean isParameter() { return isParameter; }
    public void setParameter(boolean parameter) { this.isParameter = parameter; }

    public boolean isFieldSymbol() { return isFieldSymbol; }
    public void setFieldSymbol(boolean fieldSymbol) { this.isFieldSymbol = fieldSymbol; }

    public String getDefaultValue() { return defaultValue; }
    public void setDefaultValue(String defaultValue) { this.defaultValue = defaultValue; }

    /**
     * Determina si la variable es usada frecuentemente
     */
    public boolean isFrequentlyUsed() {
        return usageLines.size() > 3;
    }

    /**
     * Obtiene el rango de líneas donde se usa la variable
     */
    public String getUsageRange() {
        if (usageLines.isEmpty()) {
            return "No utilizada";
        }
        
        Collections.sort(usageLines);
        int min = usageLines.get(0);
        int max = usageLines.get(usageLines.size() - 1);
        
        if (min == max) {
            return "Línea " + min;
        }
        return "Líneas " + min + "-" + max;
    }

    /**
     * Analiza el patrón de uso de la variable
     */
    public String analyzeUsagePattern() {
        if (usageLines.isEmpty()) {
            return "DECLARADA_NO_USADA";
        } else if (usageLines.size() == 1) {
            return "USO_SIMPLE";
        } else if (isFrequentlyUsed()) {
            return "USO_FRECUENTE";
        } else {
            return "USO_MODERADO";
        }
    }

    /**
     * Genera un resumen de la variable
     */
    public String generateSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append(name).append(" (").append(type).append(")");
        summary.append(" - Declarada línea: ").append(declarationLine);
        summary.append(", Usada: ").append(usageLines.size()).append(" veces");
        summary.append(", Patrón: ").append(analyzeUsagePattern());
        return summary.toString();
    }

    @Override
    public String toString() {
        return "VariableInfo{" +
            "name='" + name + '\'' +
            ", type='" + type + '\'' +
            ", declarationLine=" + declarationLine +
            ", usageLines=" + usageLines.size() +
            ", pattern='" + analyzeUsagePattern() + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VariableInfo that = (VariableInfo) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
