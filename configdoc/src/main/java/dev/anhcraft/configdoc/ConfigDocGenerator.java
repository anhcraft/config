package dev.anhcraft.configdoc;

import dev.anhcraft.config.annotations.Configurable;
import dev.anhcraft.config.annotations.Validation;
import dev.anhcraft.config.schema.ConfigSchema;
import dev.anhcraft.config.schema.EntrySchema;
import dev.anhcraft.config.schema.SchemaScanner;
import dev.anhcraft.configdoc.internal.ResourceLoader;
import dev.anhcraft.configdoc.internal.TextReplacer;
import dev.anhcraft.jvmkit.utils.FileUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class ConfigDocGenerator {
    private static final ResourceLoader resourceLoader = new ResourceLoader();
    private final List<ConfigSchema> schemas = new ArrayList<>();
    private final Map<Pattern, String> javaDocs = new HashMap<>();
    private boolean showFooter = true;

    public ConfigDocGenerator() {
        addJavadoc("(org.bukkit*)|(org.spigotmc*)", "https://jd.papermc.io/paper/1.19/");
        addJavadoc("(com.destroystokyo.paper*)", "https://jd.papermc.io/paper/1.19/");
        addJavadoc("(io.papermc.paper*)", "https://jd.papermc.io/paper/1.19/");
    }

    @Contract("_ -> this")
    public ConfigDocGenerator combineWith(@NotNull ConfigDocGenerator configDocGenerator) {
        schemas.addAll(configDocGenerator.schemas);
        javaDocs.putAll(configDocGenerator.javaDocs);
        return this;
    }

    @Contract("_ -> this")
    public ConfigDocGenerator withSchema(@NotNull ConfigSchema schema) {
        schemas.add(schema);
        return this;
    }

    @Contract("_ -> this")
    public ConfigDocGenerator withSchemaOf(@NotNull Class<?> schemaClass) {
        ConfigSchema schema = SchemaScanner.scanConfig(schemaClass);
        if (schema == null) throw new IllegalArgumentException("given class not configurable: " + schemaClass.getName());
        schemas.add(schema);
        return this;
    }

    @Contract("_, _ -> this")
    public ConfigDocGenerator addJavadoc(@NotNull String classPattern, @NotNull String link) {
        return addJavadoc(Pattern.compile(classPattern), link);
    }

    @Contract("_, _ -> this")
    public ConfigDocGenerator addJavadoc(@NotNull Pattern classPattern, @NotNull String link) {
        if (!link.endsWith("/")) link = link + '/';
        javaDocs.put(classPattern, link);
        return this;
    }

    @Contract("_ -> this")
    public ConfigDocGenerator showFooter(boolean showFooter) {
        this.showFooter = showFooter;
        return this;
    }

    private TextReplacer handleText(String[] e) {
        return new TextReplacer(s -> String.join("\n", e));
    }

    private TextReplacer handleText(EntrySchema entry) {
        return new TextReplacer(s -> {
            if (s.equals("key")) {
                return entry.getKey();
            } else if (s.equals("description")) {
                return entry.getDescription() == null ? "" : String.join("<br>", entry.getDescription().value());
            } else if (entry.getExamples() != null && s.startsWith("examples?")) {
                String file = s.substring("examples?".length()).trim();
                String content = resourceLoader.get(file);
                StringBuilder sb = new StringBuilder();
                for (String[] e : entry.getExamples()) {
                    sb.append(handleText(e).replace(content));
                }
                return sb.toString();
            } else if (s.equals("type")) {
                Field field = entry.getField();
                String fullType = field.getType().toGenericString();
                StringBuilder type = new StringBuilder(field.getType().getSimpleName());
                StringBuilder vb = new StringBuilder(" ");
                if (field.getType().isAnnotationPresent(Configurable.class)) {
                    vb.append("<a href=\"").append(field.getType().getSimpleName()).append(".schema.html\">").append(type).append("</a>");
                } else {
                    boolean found = false;
                    for (Map.Entry<Pattern, String> jd : javaDocs.entrySet()) {
                        if (jd.getKey().matcher(fullType).matches()) {
                            vb.append("<a href=\"").append(jd.getValue()).append(fullType.replace('.', '/').replace('$', '.')).append(".html\">").append(type).append("</a>");
                            found = true;
                            break;
                        }
                    }
                    if (!found) vb.append(type);
                }
                if (entry.getValidation() != null) {
                    Validation validation = entry.getValidation();
                    if (!validation.silent()) {
                        if (validation.notNull()) {
                            vb.append(" <b>not-null</b>");
                        }
                        if (validation.notEmpty()) {
                            vb.append(" <b>not-empty</b>");
                        }
                    }
                }
                if (entry.isOptional()) {
                    vb.append(" <b>optional</b>");
                }
                return vb.toString();
            }
            return "";
        });
    }

    private UnaryOperator<String> handleText(ConfigSchema schema) {
        return s -> {
            if (s.equals("name")) {
                return schema.getOwner().getSimpleName();
            } else if (s.equals("description")) {
                return schema.getDescription() == null ? "" : String.join("<br>", schema.getDescription().value());
            } else if (s.equals("entry_count")) {
                return String.valueOf(schema.getEntrySchemas().size());
            } else if (s.startsWith("entries?")) {
                String file = s.substring("entries?".length()).trim();
                String content = resourceLoader.get(file);
                StringBuilder sb = new StringBuilder();
                for (EntrySchema entry : schema.getEntrySchemas()) {
                    if (entry.isVirtual() || entry.isConstant()) continue;
                    sb.append(handleText(entry).replace(content));
                }
                return sb.toString();
            } else if (schema.getExamples() != null && s.startsWith("examples?")) {
                String file = s.substring("examples?".length()).trim();
                String content = resourceLoader.get(file);
                StringBuilder sb = new StringBuilder();
                for (String[] e : schema.getExamples()) {
                    sb.append(handleText(e).replace(content));
                }
                return sb.toString();
            }
            return handleText().apply(s);
        };
    }

    private UnaryOperator<String> handleText() {
        return s -> {
            if (s.startsWith("schemas?")) {
                String file = s.substring("schemas?".length()).trim();
                String content = resourceLoader.get(file);
                StringBuilder sb = new StringBuilder();
                for (ConfigSchema schema : schemas) {
                    sb.append(new TextReplacer(handleText(schema)).replace(content));
                }
                return sb.toString();
            } else if (s.equals("footer") && showFooter) {
                return resourceLoader.get("footer.html");
            }
            return "";
        };
    }

    @Contract("_ -> this")
    public ConfigDocGenerator generate(@NotNull File output) {
        output.mkdirs();

        String schemHtml = resourceLoader.get("schema.html");

        for (ConfigSchema schem : schemas) {
            FileUtil.write(new File(output, schem.getOwner().getSimpleName() + ".schema.html"), new TextReplacer(handleText(schem)).replace(schemHtml));
        }

        FileUtil.write(new File(output, "index.html"), new TextReplacer(handleText()).replace(resourceLoader.get("index.html")));
        FileUtil.write(new File(output, "main.js"), resourceLoader.get("main.js"));
        FileUtil.write(new File(output, "main.css"), resourceLoader.get("main.css"));
        return this;
    }
}
