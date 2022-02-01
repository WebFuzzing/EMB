namespace SCS.Imp
{
    public class FileSuffix
    {
        public static string Subject(string directory, string file)
        {
            var result = 0;

            //EG pathname = "...WORD/FILE.DOC";
            // files : Object[];
            //var lastFile : int = 0;

            var fileParts = file.Split(".");
            
            var lastPart = fileParts.Length - 1;
            
            if (lastPart <= 0) return "" + result;
            
            var suffix = fileParts[lastPart];

            if (directory == "text") {
                if ("txt".Equals(suffix)) {
                    result = 1;
                }
            }
            else if (directory == "acrobat") {
                if ("pdf".Equals(suffix)) {
                    //print("acrobat");
                    result = 2;
                }
            }
            else if (directory == "word") {
                if ("doc".Equals(suffix)) {
                    //print("word");
                    result = 3;
                }
            }
            else if (directory == "bin") {
                if ("exe".Equals(suffix)) {
                    //print("bin");
                    result = 4;
                }
            }
            else if (directory == "lib") {
                if ("dll".Equals(suffix)) {
                    //print("lib");
                    result = 5;
                }
            }

            return "" + result;
        }
    }
}