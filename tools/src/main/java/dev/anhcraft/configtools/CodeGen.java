package dev.anhcraft.configtools;

import dev.anhcraft.config.Dictionary;
import dev.anhcraft.config.json.JsonParser;
import dev.anhcraft.config.util.StringUtil;
import dev.anhcraft.jvmkit.utils.HttpUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class CodeGen {

  private static final String VERSION_MANIFEST_URL =
      "https://launchermeta.mojang.com/mc/game/version_manifest.json";

  public static void main(String[] args) throws IOException {
    String json = HttpUtil.fetchString(VERSION_MANIFEST_URL);
    List<String> releaseVersions = getReleaseVersions(json);
    printVersions(releaseVersions);
  }

  private static List<String> getReleaseVersions(String json) throws IOException {
    Dictionary dict =
        (Dictionary) new JsonParser(new BufferedReader(new StringReader(json))).parse();
    Object[] versions = (Object[]) dict.get("versions");

    List<String> releaseVersions = new ArrayList<>();
    for (Object v : versions) {
      Dictionary version = (Dictionary) v;
      if (version.get("type").equals("release")) {
        String versionId = (String) version.get("id");
        releaseVersions.add(versionId);
      }
    }

    return releaseVersions;
  }

  private static void printVersions(List<String> releaseVersions) {
    for (String versionId : releaseVersions) {
      List<String> parts = StringUtil.fastSplit(versionId, '.');
      int major = Integer.parseInt(parts.get(0));
      int minor = Integer.parseInt(parts.get(1));
      int patch = parts.size() > 2 ? Integer.parseInt(parts.get(2)) : 0;

      if (minor < 12) continue;

      if (patch == 0) {
        System.out.printf(
            "public static final MinecraftVersion v%d_%d = new MinecraftVersion(%d, %d, 0);%n",
            major, minor, major, minor);
      } else {
        System.out.printf(
            "public static final MinecraftVersion v%d_%d_%d = new MinecraftVersion(%d, %d, %d);%n",
            major, minor, patch, major, minor, patch);
      }
    }
  }
}
