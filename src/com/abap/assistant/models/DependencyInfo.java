package com.abap.assistant.models;

import java.util.*;

/**
 * Clase que representa información sobre dependencias en código ABAP
 * Incluye dependencias de métodos, funciones, includes, tablas de BD, etc.
 */
public class DependencyInfo {
    private String name;
    private String type;
    private String source;
    private List<String> parameters;
    private Map<String, String> metadata;
    private int usageCount;
    private List<Integer> usageLines;
    private boolean isExternal;
    private String description;

    public DependencyInfo(String name, String type) {
        this.name = name;
        this.type = type;
        this.parameters = new ArrayList<>();
        this.metadata = new HashMap<>();
        this.usageLines = new ArrayList<>();
        this.usageCount = 0;
        this.isExternal = false;
    }

    // Getters y setters básicos
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public List<String> getParameters() { return parameters; }
    public void addParameter(String parameter) { this.parameters.add(parameter); }

    public Map<String, String> getMetadata() { return metadata; }
    public void addMetadata(String key, String value) { this.metadata.put(key, value); }

    public int getUsageCount() { return usageCount; }
    public void incrementUsageCount() { this.usageCount++; }
    public void setUsageCount(int usageCount) { this.usageCount = usageCount; }

    public List<Integer> getUsageLines() { return usageLines; }
    public void addUsageLine(int lineNumber) { 
        if (!usageLines.contains(lineNumber)) {
            this.usageLines.add(lineNumber);
            incrementUsageCount();
        }
    }

    public boolean isExternal() { return isExternal; }
    public void setExternal(boolean external) { this.isExternal = external; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    /**
     * Determina la criticidad de la dependencia
     */
    public String getCriticality() {
        if (usageCount > 10) return "HIGH";
        if (usageCount > 5) return "MEDIUM";
        if (usageCount > 1) return "LOW";
        return "MINIMAL";
    }

    /**
     * Indica si es una dependencia crítica
     */
    public boolean isCritical() {
        return "HIGH".equals(getCriticality()) || isExternal;
    }

    /**
     * Obtiene el tipo de dependencia categorizado
     */
    public String getCategorizedType() {
        switch (type.toLowerCase()) {
            case "method_call":
            case "function":
                return "EXECUTION";
            case "include":
            case "class":
                return "STRUCTURAL";
            case "database_table":
            case "view":
                return "DATA";
            case "transaction":
                return "NAVIGATION";
            default:
                return "OTHER";
        }
    }

    /**
     * Genera recomendaciones para la dependencia
     */
    public List<String> generateRecommendations() {
        List<String> recommendations = new ArrayList<>();
        
        if (isCritical()) {
            recommendations.add("Dependencia crítica - Revisar impacto antes de cambios");
        }
        
        if (usageCount == 0) {
            recommendations.add("Dependencia no utilizada - Considerar eliminación");
        }
        
        if (isExternal && usageCount > 5) {
            recommendations.add("Alto acoplamiento externo - Evaluar encapsulación");
        }
        
        if (type.equals("DATABASE_TABLE") && usageCount > 3) {
            recommendations.add("Uso frecuente de tabla - Considerar buffering/caching");
        }
        
        return recommendations;
    }

    /**
     * Genera un resumen de la dependencia
     */
    public String generateSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append(name).append(" (").append(type).append(")");
        summary.append(" - Usado: ").append(usageCount).append(" veces");
        summary.append(", Criticidad: ").append(getCriticality());
        summary.append(", Categoría: ").append(getCategorizedType());
        
        if (isExternal) {
            summary.append(", EXTERNA");
        }
        
        return summary.toString();
    }

    @Override
    public String toString() {
        return "DependencyInfo{" +
            "name='" + name + '\'' +
            ", type='" + type + '\'' +
            ", usageCount=" + usageCount +
            ", criticality='" + getCriticality() + '\'' +
            ", external=" + isExternal +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DependencyInfo that = (DependencyInfo) o;
        return Objects.equals(name, that.name) && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }
}
