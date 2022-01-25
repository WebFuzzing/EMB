def junit_suite_test(name, srcs, deps, size="small", resources=[], classpath_resources=[], jvm_flags=[], tags=[], data=[]):
  tests = []
  package = PACKAGE_NAME.replace("timbuctoo-instancev4/src/test/java/", "").replace("/", ".")

  for src in srcs:
    if src.endswith("Test.java"):
      if ("/" in src):
        tests += [package + "." + src.replace("/", ".").replace(".java", ".class")]
      else:
        tests += [src.replace(".java", ".class")]

  native.genrule(
    name = name + "_AllTests_gen",
    outs = ["AllTests.java"],
    cmd = """
      cat <<EOF >> $@
package %s;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({\n  %s\n})
public class AllTests {}
EOF
    """ % (package, ",\n  ".join(tests))
  )

  native.java_test(
    name = name,
    srcs = srcs + ["AllTests.java"],
    test_class = package + ".AllTests",
    resources = resources,
    classpath_resources = classpath_resources,
    data = data,
    size = size,
    tags = tags,
    jvm_flags = jvm_flags,
    deps = deps + [
      "//third_party:junit_junit",
      "//third_party:org_hamcrest_hamcrest_all",
      "//third_party:org_mockito_mockito_core",
      "//timbuctoo-test-services",
    ],
  )
