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
            
            switch (directory)
            {
                //Console.WriteLine("{0}, {1}", directory, suffix);
                case "text":
                {
                    if ("txt".Equals(suffix))
                    {
                        result = 1;
                    }

                    break;
                }
                case "acrobat":
                {
                    if ("pdf".Equals(suffix))
                    {
                        //print("acrobat");
                        result = 2;
                    }

                    break;
                }
                case "word":
                {
                    if ("doc".Equals(suffix))
                    {
                        //print("word");
                        result = 3;
                    }

                    break;
                }
                case "bin":
                {
                    if ("exe".Equals(suffix))
                    {
                        //print("bin");
                        result = 4;
                    }

                    break;
                }
                case "lib":
                {
                    if ("dll".Equals(suffix))
                    {
                        //print("lib");
                        result = 5;
                    }

                    break;
                }
            }

            return "" + result;
        }
    }
}