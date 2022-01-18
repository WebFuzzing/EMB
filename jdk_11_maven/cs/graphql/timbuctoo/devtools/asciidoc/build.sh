#!/bin/sh

cd -P -- "$(dirname -- "$0")" #go to dir of script even if it was called as a symbolic link

rootsrcdir="$(git rev-parse --show-toplevel)"

mkdir -p "$rootsrcdir"/documentation/output
echo generating documentation as HTML >&2
#docker run -it --rm -v "$PWD":/documents/ -v "$devtoolsdir":/devtools/ asciidoctor/docker-asciidoctor bash
docker run -it --rm -v "$rootsrcdir":/documents/ -v "$PWD":/devtools/ asciidoctor/docker-asciidoctor asciidoctor \
  -a stylesheet=readthedocs.css \
  -r asciidoctor-diagram \
  -r /devtools/macros/featureMacro.rb \
  -D documentation/output \
  documentation/index.adoc

cd "$rootsrcdir"/documentation
cp readthedocs.css concordion.css api.html *.png ./output
