/*
 * This file is part of the Markdownj Command-line Interface program
 * (aka: markdownj-cli).
 *
 * Copyright (C) 2020 Bradley Willcott
 *
 * markdownj-cli is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * markdownj-cli is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.markdownj.cli;

/**
 * Sample code that finds files that match the specified glob pattern.
 * For more information on what constitutes a glob pattern, see
 * https://docs.oracle.com/javase/tutorial/essential/io/fileOps.html#glob
 * <p>
 * The file or directories that match the pattern are printed to
 * standard out. The number of matches is also printed.
 * <p>
 * When executing this application, you must put the glob pattern
 * in quotes, so the shell will not expand any wild cards:
 * java Find . -name "*.java"
 */
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Character.MAX_VALUE;
import static java.nio.file.FileVisitResult.*;
import static java.nio.file.Files.getLastModifiedTime;
import static java.nio.file.Files.notExists;
import static java.nio.file.Path.of;

/**
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 0.1
 * @version 1.0
 */
public class Find {

    private static final String DefaultMD = ".md";
    private static final String DefaultHTML = ".html";

    public static class Finder
            extends SimpleFileVisitor<Path> {

        private final int vlevel;
        private final PathMatcher matcher;
        private int numMatches = 0;
        private final SortedSet<Path> filenames = new TreeSet<>();

        Finder(String pattern, int vlevel) {
            matcher = FileSystems.getDefault()
                    .getPathMatcher("glob:" + pattern);
            this.vlevel = vlevel;
        }

        // Compares the glob pattern against
        // the file or directory name.
        void find(Path file) {
            Path name = file.getFileName();
            if (name != null && matcher.matches(name))
            {
                numMatches++;

                if (vlevel >= 2)
                {
                    System.err.println(file);
                }

                filenames.add(file);
            }
        }

        // Prints the total number of
        // matches to standard out.
        SortedSet<Path> done() {
            if (vlevel >= 1)
            {
                System.err.println("Matched: "
                                   + numMatches);
            }
            return filenames;
        }

        // Invoke the pattern matching
        // method on each file.
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            find(file);
            return CONTINUE;
        }

        // Invoke the pattern matching
        // method on each directory.
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
//            find(dir);
            return CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) {
            System.err.println("visitFileFailed: " + exc);
            return CONTINUE;
        }
    }

    /**
     * Provides a list of files.
     *
     * @param sourceDir Start file search from this directory. (Default: "" - Current Working Directory)
     * @param pattern   Glob file search pattern. (Default: "{@code *}")
     * @param recursive {@code True} sets recursive directory tree walk.
     *                  {@code False} keeps search to the current directory, only.
     * @param vlevel    {@code 0} Run silent ...
     *                  {@code 1} Printout basic information only.
     *                  {@code 2} Printout progress/diagnostic information during processing.
     *
     *
     * @return Set of {@code Path}s representing the files and directories found.
     *
     * @throws IOException
     */
    public static SortedSet<Path> getFileList(String sourceDir, String pattern, boolean recursive, int vlevel) throws IOException {
        Path currentDir = FileSystems.getDefault().getPath("").toAbsolutePath();

        if (vlevel >= 1)
        {
            System.err.println("PWD: " + currentDir);
        }

        Finder finder = new Finder(pattern != null ? pattern : "*" + DefaultMD, vlevel);
        Path srcPath = (sourceDir != null ? of(sourceDir) : of(""));

        Files.walkFileTree(srcPath, EnumSet.noneOf(FileVisitOption.class), recursive ? MAX_VALUE : 1, finder);

        return finder.done();
    }

    /**
     * Provides a list of files that need to be updated.
     *
     * @param sourceDir Start file search from this directory. (Default: "" - Current Working Directory)
     * @param destDir   Prepare return list with this directory merged into output file paths. (Default: &lt;sourceDir&gt;)
     * @param pattern   Glob file search pattern. (Default: "{@code *.md}")
     * @param outExtn   Output file extension. (Default: "{@code .html}")
     * @param recursive {@code True} sets recursive directory tree walk.
     *                  {@code False} keeps search to the current directory, only.
     * @param vlevel    {@code 0} Run silent ...
     *                  {@code 1} Printout basic information only.
     *                  {@code 2} Printout progress/diagnostic information during processing.
     *
     * @return List containing Path arrays. Each with two elements. [0] Source file, [1] Destination file.
     *
     * @throws IOException
     */
    public static List<Path[]> getUpdateList(String sourceDir, String destDir, String pattern, String outExtn, boolean recursive, int vlevel) throws IOException {
        Path currentDir = FileSystems.getDefault().getPath("").toAbsolutePath();

        if (vlevel >= 1)
        {
            System.err.println("PWD: " + currentDir);
        }

        Finder finder = new Finder(pattern != null ? pattern : "*" + DefaultMD, vlevel);
        Path srcPath = (sourceDir != null ? of(sourceDir) : of(""));

        Files.walkFileTree(srcPath, EnumSet.noneOf(FileVisitOption.class), recursive ? MAX_VALUE : 1, finder);

        SortedSet<Path> inpList = finder.done();
        List<Path[]> outList = new ArrayList<>(inpList.size());

        if (vlevel >= 2)
        {
            System.err.println("inpList:");
        }

        for (Path inPath : inpList)
        {
            Matcher m;

            if (srcPath.toString().isEmpty() || destDir == null)
            {
                m = Pattern.compile("^(?<basename>.*?)(?:[.]\\w+)?$").matcher(inPath.toString());
            } else
            {
                m = Pattern.compile("^(?:" + srcPath + "/)(?<basename>.*?)(?:[.]\\w+)?$").matcher(inPath.toString());
            }

            if (m.find())
            {
                String basename = m.group("basename");
                Path outPath = of(destDir != null ? destDir : "", basename + (outExtn != null ? outExtn : DefaultHTML));

                if (notExists(outPath) || getLastModifiedTime(inPath).compareTo(getLastModifiedTime(outPath)) > 0)
                {
                    Path[] files = new Path[2];

                    files[0] = inPath;
                    files[1] = outPath;
                    outList.add(files);

                    if (vlevel >= 2)
                    {
                        System.err.println(outPath);
                    }
                }
            }
        }

        if (vlevel >= 2)
        {
            System.err.println("outList:");

            outList.forEach((files) ->
            {
                System.err.println(files[1]);
            });
        }

        return outList;
    }

    static void usage() {
        System.err.println("java Find <path>"
                           + " -name \"<glob_pattern>\"");
        System.exit(-1);
    }

    public static void main(String[] args) throws IOException {

//        List<Path[]> fileList = Find.getUpdateList("src", "target", null, null, true, 2);
        SortedSet<Path> fileList = getFileList("target/manual", "*", true, 2);
    }
}
