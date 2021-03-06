package dev.anhcraft.configdoc;

import com.google.common.base.Preconditions;
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

    public ConfigDocGenerator(){
        addJavadoc("(org.bukkit.*)|(org.spigotmc*)", "https://hub.spigotmc.org/javadocs/spigot/");
        addJavadoc("(com.destroystokyo.paper*)", "https://papermc.io/javadocs/paper/1.17/");
    }

    @Contract("_ -> this")
    public ConfigDocGenerator combineWith(@NotNull ConfigDocGenerator configDocGenerator){
        Preconditions.checkNotNull(configDocGenerator);
        schemas.addAll(configDocGenerator.schemas);
        javaDocs.putAll(configDocGenerator.javaDocs);
        return this;
    }

    @Contract("_ -> this")
    public ConfigDocGenerator withSchema(@NotNull ConfigSchema schema){
        Preconditions.checkNotNull(schema);
        schemas.add(schema);
        return this;
    }

    @Contract("_ -> this")
    public ConfigDocGenerator withSchemaOf(@NotNull Class<?> schemaClass){
        Preconditions.checkNotNull(schemaClass);
        schemas.add(SchemaScanner.scanConfig(schemaClass));
        return this;
    }

    @Contract("_, _ -> this")
    public ConfigDocGenerator addJavadoc(@NotNull String classPattern, @NotNull String link){
        Preconditions.checkNotNull(classPattern);
        return addJavadoc(Pattern.compile(classPattern), link);
    }

    @Contract("_, _ -> this")
    public ConfigDocGenerator addJavadoc(@NotNull Pattern classPattern, @NotNull String link){
        Preconditions.checkNotNull(classPattern);
        if(!link.endsWith("/")) link = link + '/';
        javaDocs.put(classPattern, link);
        return this;
    }

    private TextReplacer handleText(String[] e){
        return new TextReplacer(s -> String.join("\n", e));
    }

    private TextReplacer handleText(EntrySchema entry){
        return new TextReplacer(s -> {
            if(s.equals("key")){
                return entry.getKey();
            } else if(s.equals("description")){
                return entry.getDescription() == null ? "" : String.join("<br>", entry.getDescription().value());
            }  else if(entry.getExamples() != null && s.startsWith("examples?")){
                String file = s.substring("examples?".length()).trim();
                String content = resourceLoader.get(file);
                StringBuilder sb = new StringBuilder();
                for(String[] e : entry.getExamples()){
                    sb.append(handleText(e).replace(content));
                }
                return sb.toString();
            }else if(s.equals("type")){
                Field field = entry.getField();
                String fullType = field.getType().toGenericString();
                StringBuilder type = new StringBuilder(field.getType().getSimpleName());
                StringBuilder vb = new StringBuilder(" ");
                if(field.getType().isAnnotationPresent(Configurable.class)){
                    vb.append("<a href=\"").append(field.getType().getSimpleName()).append(".schema.html\">").append(type).append("</a>");
                } else {
                    boolean found = false;
                    for(Map.Entry<Pattern, String> jd : javaDocs.entrySet()){
                        if(jd.getKey().matcher(fullType).matches()){
                            vb.append("<a href=\"").append(jd.getValue()).append(fullType.replace('.', '/').replace('$', '.')).append(".html\">").append(type).append("</a>");
                            found = true;
                            break;
                        }
                    }
                    if(!found) vb.append(type);
                }
                if(entry.getValidation() != null){
                    Validation validation = entry.getValidation();
                    if(!validation.silent()) {
                        if (validation.notNull()) {
                            vb.append(" <b>not-null</b>");
                        }
                        if (validation.notEmpty()) {
                            vb.append(" <b>not-empty</b>");
                        }
                    }
                }
                return vb.toString();
            }
            return "";
        });
    }

    private UnaryOperator<String> handleText(ConfigSchema schema){
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
                    sb.append(handleText(entry).replace(content));
                }
                return sb.toString();
            } else if(schema.getExamples() != null && s.startsWith("examples?")){
                String file = s.substring("examples?".length()).trim();
                String content = resourceLoader.get(file);
                StringBuilder sb = new StringBuilder();
                for(String[] e : schema.getExamples()){
                    sb.append(handleText(e).replace(content));
                }
                return sb.toString();
            }
            return handleText().apply(s);
        };
    }

    private UnaryOperator<String> handleText(){
        return s -> {
            if(s.startsWith("schemas?")){
                String file = s.substring("schemas?".length()).trim();
                String content = resourceLoader.get(file);
                StringBuilder sb = new StringBuilder();
                for(ConfigSchema schema : schemas){
                    sb.append(new TextReplacer(handleText(schema)).replace(content));
                }
                return sb.toString();
            }
            return "";
        };
    }

    @Contract("_ -> this")
    public ConfigDocGenerator generate(@NotNull File output){
        Preconditions.checkNotNull(output);
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
