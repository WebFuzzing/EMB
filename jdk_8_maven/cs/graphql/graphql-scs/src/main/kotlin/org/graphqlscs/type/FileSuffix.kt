package org.graphqlscs.type

import org.springframework.stereotype.Component

@Component
class FileSuffix {

    fun subject(directory: String, file: String): String {
        var result = 0

        //EG pathname = "...WORD/FILE.DOC";
        // files : Object[];
        var fileparts: Array<String?>? = null
        //var lastfile : int = 0;
        var lastpart = 0
        var suffix: String? = null
        fileparts = file.split(".".toRegex()).toTypedArray()
        lastpart = fileparts.size - 1
        if (lastpart > 0) {
            suffix = fileparts[lastpart]
            //Console.WriteLine("{0}, {1}", directory, suffix);
            if ("text" == directory) {
                if ("txt" == suffix) {
                    result = 1
                }
            }
            if ("acrobat" == directory) {
                if ("pdf" == suffix) {
                    //print("acrobat");
                    result = 2
                }
            }
            if ("word" == directory) {
                if ("doc" == suffix) {
                    //print("word");
                    result = 3
                }
            }
            if ("bin" == directory) {
                if ("exe" == suffix) {
                    //print("bin");
                    result = 4
                }
            }
            if ("lib" == directory) {
                if ("dll" == suffix) {
                    //print("lib");
                    result = 5
                }
            }
        }
        return "" + result
    }
}