@@@
use : articles
title: Markdownj CLI | ToDo List
@@@

[Home]

---

## ToDo List


- [ ]! Add file [meta block][mb]:
    - [x]! Add [named meta blocks][nmb]. `@@@[name]`
    - [ ]! Add reading project POM file (`pom.xml`). `[project]`
    - [ ]! Add include file:
        - [ ]! Include markdown file: `includeMD` : `filename.md`
        - [ ]! Include html file: `includeHTML` : `filename.html`
    - [x]! Add html template file option.
    - [x]! Add including CSS file.
    - [x]! Add variables for template replacement parameters.
- [x]! Add configuration file: `markdownj-cli.ini`
- [ ]! Add auto-generated Table of Contents for each page.
- [ ]! Add archive creation as `jar` file:
    - [x]! Auto-generate MANIFEST.mf file.
        - [ ]! include meta data from [MANIFEST.mf] section in `markdownj-cli.ini`.
    - [x]! Add options related to _jar_ file creation: `-j`
- [x]! Add new [switches][opt]:
    - [x]! (26/02/2020)`-W` : Create new directories (css, templates) in current directory.  
Add default versions of: `style.css` and `default.html`.
    - [x]! (26/02/2020)`-w` : Include/use the contents of these directories as needed.
- [x]! (24/02/2020) Added JSAP functionality. Configure the following [options][opt]:
    
Command Line Options
|Option|Description|
|----|----|
|`-i <filename>`|Input file|
|`-o <filename>`|Output file|
|`-s <Directory>`|Source directory (default: "")|
|`-d <Directory>`|Destination directory (default: "")|
|`-r`|Recursive processing of directory tree.|
|`-v:n`|Verbose processing.  List files as they are processed.<br>Set verbose level with "`-v:1`" or "`-v:2`".  "`-v`" defaults to level '1' |
|`-h --help`|Online help|[total]




[Home]:index.html
[mb]:Meta Blocks.html
[nmb]:Named Meta Blocks.html
[opt]:Options.html
