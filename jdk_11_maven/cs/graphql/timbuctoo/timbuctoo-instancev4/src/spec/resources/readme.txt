Contents
--------
- nl/knaw/huygens/timbuctoo/server/endpoints/v2 contains the html files used by concordion.
- schema contains the schema files needs to create an html book
    - http://oreillymedia.github.io/HTMLBook
    - https://github.com/oreillymedia/HTMLBook
- index.html is a file that is used to group the test results

Generate a test html book
-------------------------
1. From the module root run: mvn integration-test
2. In this folder run: xmllint --dropdtd --noent schema/htmlbook.xsd index.html > <output-file>.html
