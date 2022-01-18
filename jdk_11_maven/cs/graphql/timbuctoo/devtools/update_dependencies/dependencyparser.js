//Run using mvn dependency:tree -Dverbose -DoutputFile=dependencies && find . -name dependencies | xargs node ./third_party/dependencyparser.js

var fs = require("fs");

function parseFile(string) {
  const result = [];
  const lookup = {};
  const stack = [];
  // console.log("        stack: \n" + JSON.stringify(stack, undefined, 2).split("\n").map(x => "               " + x).join("\n"));
  let lines = string.split("\n");
  for (let i = 0; i < lines.length; i++) {
    let line = lines[i];
    // console.log("DEBUG: line: " + JSON.stringify(line));
    if (line == "") {
      continue;
    }
    // console.log("       result: \n" + JSON.stringify(result, undefined, 2).split("\n").map(x => "               " + x).join("\n"));
    if (isToplevel(line)) {
      // console.log("       toplevel");
      stack.splice(0)
      result.push(init({localProject: true, line: line, isLast: false, coords: parseCoords(line), omissionReason: undefined }, stack, lookup));
    } else {
      let [prefix, cleanedLine] = cleanLine(line);
      // console.log("       cleanedLine: '" + cleanedLine + "'");
      init(parseDepLine(cleanedLine), stack, lookup)
      if (lines[i + 1] && lines[i + 1] != "" && !isToplevel(lines[i+1])) {
        let nextPrefix = cleanLine(lines[i + 1])[0];
        if (nextPrefix.length == prefix.length) {
          stack.pop();
        } else if (nextPrefix.length < prefix.length) {
          while (stack.length > 0 && stack[stack.length - 1].isLast) {
            stack.pop();
          }
          stack.pop();
        }
      }
    }
    // console.log("       stack: " + JSON.stringify(stack.map(x => x.line), undefined, 2).split("\n").map((x,i )=> i == 0 ? x : ("       " + x)).join("\n"));
  }
  return {
    result,
    lookup
  };
}

function isToplevel(line) {
  return !line.startsWith("+") && !line.startsWith(" ") && !line.startsWith("|") && !line.startsWith("\\")
}

function cleanLine(line) {
  if (line.indexOf("+- ") > -1) {
    let parts = line.split("+- ");
    return [parts[0], "+- " + parts[1]];
  } else if (line.indexOf("\\- ") > -1) {
    let parts = line.split("\\- ");
    return [parts[0], "\\- " + parts[1]];
  } else {
    throw new Error("This looks like a weird line! '" + line + "'")
  }
}

function makePrefix(stack) {
  let result = "";
  for (let i = 1; i < stack.length; i++) {
    let part = stack[i];
    if (part.isLast) {
      result += "   "
    } else {
      result += "|  "
    }
  }
  return result;
}

function parseCoords(line) {
  let parts = line.split(":");
  let groupId = parts[0];
  let artifactId = parts[1];
  let packaging;
  let version;
  let classifier;
  if (parts.length == 3) {
    packaging = "jar"
    version = parts[2]
  } else if (parts.length == 6) {
    packaging = parts[2]
    version = parts[4]
    classifier = parts[5]
  } else {
    packaging = parts[2]
    version = parts[3]
    classifier = parts[4]
  }
  return {
    groupId,
    artifactId,
    packaging,
    version,
    classifier,
    identifier: groupId + ":" + artifactId + (packaging == "jar" ? "" : ":" + packaging)
  }
}

function parseDepLine(line) {
  let omissionReason;
  // console.log("       skipped: '" + line.substr(3) + "'")
  let isLast = line.startsWith("\\- ");
  line = line.substr(3);
  if (line.startsWith("(")) {
    let parts = line.split(" - ", 2);
    // console.log("       parts: '" + parts[0] + "' - '" + parts[1] + "' - '" + parts[2] + "'")
    line = parts[0].substr(1)
    omissionReason = parts[1].substr(0, parts[1].length - 1)
  }
  return {
    isLast,
    coords: parseCoords(line),
    omissionReason,
    line
  }
}

function init(id, stack, lookup) {
  if (!lookup.hasOwnProperty(id.coords.identifier)) {
    // console.log("       " + id.coords.identifier + " did not exist yet!")
    lookup[id.coords.identifier] = id;
    id.dependencies = [];
  } else {
    if (id.localProject) {
      lookup[id.coords.identifier].localProject = true
    }
    if (!id.omissionReason) {
      lookup[id.coords.identifier].coords.version = id.coords.version
    }
  }
  if (stack.length > 0) {
    let parent = lookup[stack[stack.length - 1].coords.identifier];
    let child = lookup[id.coords.identifier];
    //HACK! maven sometimes reports a dependency where the parent is the same as the child
    //we remove it here
    if (parent !== child) {
      parent.dependencies.push(child);
    }
  }
  stack.push(id);
  return lookup[id.coords.identifier]
}

function end(stack, result) {
  return result[stack.pop()];
}

const customRepositories = {
  "com.kjetland.dropwizard:dropwizard-activemq": "https://raw.githubusercontent.com/mbknor/mbknor.github.com/master/m2repo/releases",
  "com.github.DANS-KNAW:dans-dp-lib": "https://jitpack.io",
  "nl.knaw.huygens:huygens-persistence": "http://maven.huygens.knaw.nl/repository/",
  "nl.knaw.huygens:security-core": "http://maven.huygens.knaw.nl/repository/",
  "com.sleepycat:je": "http://download.oracle.com/maven",
  "handle.net:handle-client": "http://maven.huygens.knaw.nl/repository/"
}

const customUris = {
  "org.neo4j:neo4j-io:test-jar": "http://repo1.maven.org/maven2/org/neo4j/neo4j-io/%s/neo4j-io-%s-tests.jar",
  "org.neo4j:neo4j-kernel:test-jar": "http://repo1.maven.org/maven2/org/neo4j/neo4j-kernel/%s/neo4j-kernel-%s-tests.jar"
}

const dependenciesToIgnore = [
  "com.sun:tools"
]

function writeBazelScript(dependencies) {
  let result = "";
  result += "def generated_maven_jars():\n";
  for (let dependencyId of Object.keys(dependencies.lookup).sort()) {
    let dependency = dependencies.lookup[dependencyId]
    if (dependency.localProject) {
      continue;
    }
    if (dependenciesToIgnore.indexOf(dependencyId) > -1) {
      continue;
    }
    let name = dependency.coords.identifier.replace(/[^a-zA-Z0-9_]/g, "_");
    if (customUris.hasOwnProperty(dependency.coords.identifier)) {
      result += "  native.http_jar(\n";
      result += '    name = "' + name + '",\n';
      result += '    url = "' + customUris[dependency.coords.identifier].replace(/%s/g, dependency.coords.version) + '",\n';
      result += "  )\n";
    } else {
      result += "  native.maven_jar(\n";
      result += '    name = "' + name + '",\n';
      result += "    artifact = \"" + dependency.coords.groupId + ":" + dependency.coords.artifactId + ":" + dependency.coords.packaging + ":" + dependency.coords.version + '",\n';
      if (customRepositories.hasOwnProperty(dependency.coords.identifier)) {
        result += "    repository = \"" + customRepositories[dependency.coords.identifier] + '",\n';
      }
      result += "  )\n";
    }
  }
  result += "def generated_java_libraries():\n";
  for (let dependencyId of Object.keys(dependencies.lookup).sort()) {
    let dependency = dependencies.lookup[dependencyId]
    if (dependency.localProject) {
      continue;
    }
    if (dependenciesToIgnore.indexOf(dependencyId) > -1) {
      continue;
    }
    let name = dependency.coords.identifier.replace(/[^a-zA-Z0-9_]/g, "_");
    result += '  native.java_library(\n';
    result += '    name = "' + name + '",\n';
    result += '    visibility = ["//visibility:public"],\n';
    result += '    exports = ["@' + name + '//jar"],\n';
    result += '    runtime_deps = [\n';
    let resultObj = {}
    for (let sub of dependency.dependencies) {
      if (sub.localProject) {
        continue;
      }
      // console.log(JSON.stringify(sub))
      if (dependenciesToIgnore.indexOf(sub.coords.identifier) > -1) {
        continue;
      }
      resultObj[sub.coords.identifier.replace(/[^a-zA-Z0-9_]/g, "_")] = true
    }
    for (var key in resultObj) {
      result += '      ":' + key + '",\n';
    }
    result += '    ],\n';
    result += '  )\n';
  }
  return result;
}

function writeTopLevelDependencies(dependencies) {
  let result = "";
  for (let project of dependencies.result) {
    if (project.coords.artifactId == "timbuctoo-instancev4") {
      let result = {}
      for (let dependency of project.dependencies) {
        if (dependency.localProject) {
          continue;
        }
        result[dependency.coords.identifier.replace(/[^a-zA-Z0-9_]/g, "_")] = true
      }
      for (var key in result) {
        result += '        "//third_party:' + key + '",\n';
      }
    }
  }
  return result;
}

function writeAllDependencies(dependencies) {
  let result = "";
  for (let dependencyId of Object.keys(dependencies.lookup)) {
    let dependency = dependencies.lookup[dependencyId]
    if (dependency.localProject) {
      continue;
    }
    let name = dependency.coords.identifier.replace(/[^a-zA-Z0-9_]/g, "_");
    result += '        "//third_party:' + name + '",\n';
  }
  return result;
}

function execute() {
  let data = "";
  for (let file of process.argv.slice(2)) {
    data += fs.readFileSync(file, "utf-8");
  }
  let parsed = parseFile(data)
  fs.writeFileSync("./third_party/generate_workspace.bzl", writeBazelScript(parsed), "utf-8");
//  fs.writeFileSync("BUILD", fs.readFileSync('BUILD.template', "utf-8").replace("%DEPENDENCIES%", writeAllDependencies(parsed)), "utf-8");
}

execute();
