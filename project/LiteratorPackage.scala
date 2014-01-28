/* ### Working with files */

package com.faacets.literator

import java.io._
import java.nio.file.Path

import lib.LanguageMap._
import lib.FileUtils._

package object lib {
  val markdownHeader = 
"""HTML header:  <script type="text/javascript"
    src="http://cdn.mathjax.org/mathjax/latest/MathJax.js?config=TeX-AMS-MML_HTMLorMML">
    </script>
    <link rel="stylesheet" href="http://yandex.st/highlightjs/7.5/styles/default.min.css">
    <script src="http://yandex.st/highlightjs/7.5/highlight.min.js"></script>
    <script>hljs.initHighlightingOnLoad();</script>
latex input:        mmd-article-header
Title:              Literate documentation 
Base Header Level:  1
LaTeX Mode:         memoir  
latex input:        mmd-article-begin-doc
latex footer:       mmd-memoir-footer

"""

  implicit class FileLiterator(root: File) {

    /* Checks that the file has a known source format */
    def isSource: Boolean = langMap.isDefinedAt(root.ext)

    /*  This is the key function. If the source file is a directory, it traverses it, takes all 
        children, parses each and writes a corresponding markdown file. If parser encounters any 
        errors, it returns them in a list. 
    */
    def literate(
          destBase: Option[File] = None
        ): List[String] = {

      /* First we can generate index section */
/*      val index = root getFileTree { f => f.isDirectory || f.isSource } match {
          case Some(ix) if withIndex => Seq("------", "### Index", ix) mkString "\n\n"
          case _ => ""
        }*/

      /* Then we start with traversing list of source files */
      val fileList: Seq[File] = root getFileList { _.isSource }
      var markdownContent = collection.mutable.Map.empty[File, String]
      var masterFiles = collection.mutable.ArrayBuffer.empty[File]

      val parserResult = fileList flatMap { child =>

        /* And for each of them we generate a block of relative links */
/*        val linksList = fileList map { f =>
            "["+f.relativePath(root).toString+"]: "+f.relativePath(child).toString+".md"
          } mkString("\n")*/

        langMap.get(child.ext) flatMap { lang =>

          /* Knowing the language of the source we can parse it */
          val literator = LiteratorParsers(lang)
          val src = scala.io.Source.fromFile(child).mkString
          val parsed = literator.parseAll(literator.markdown, src) 

          parsed match {
            case literator.NoSuccess(msg, _) => Some(child + " " + parsed)
            case literator.Success(text, _) => {
              markdownContent += (child -> text)
              if (text contains "MasterDocument")
                masterFiles.append(child)
              None
            }
          }

        }

      }

      masterFiles foreach { child =>
        /* And if we parsed something, we write it to the file */
        destBase map { db =>
          import util.matching.Regex
          val includeRegex = new Regex("""(?m)^Include:\s*(.*)$""")
          val masterRegex = new Regex("""(?m)^MasterDocument\s*$""")
          val contentWithIncludes = includeRegex replaceAllIn (markdownContent(child), m => {
            val filename = m.group(1)
            val includedFile = new File(child.getParent, filename)
            Regex.quoteReplacement(markdownContent(includedFile))
          })
          val content = masterRegex replaceAllIn (contentWithIncludes, "")          
          val base: File = db.getCanonicalFile
          val relative: Path = child.getCanonicalFile.getParentFile.relativePath(root)
          val destDir: File = new File(base, relative.toString)
          if (!destDir.exists) destDir.mkdirs
          new File(destDir, child.name+".md").write(markdownHeader + content)
        }
      }
      parserResult.toList
    }

  }

}

