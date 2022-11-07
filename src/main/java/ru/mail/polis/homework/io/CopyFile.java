package ru.mail.polis.homework.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;


public class CopyFile {

    /**
     * Реализовать копирование папки из pathFrom в pathTo. Скопировать надо все внутренности
     * Файлы копировать ручками через стримы. Используем новый API
     * В тесте для создания нужных файлов для первого запуска надо раскомментировать код в setUp()
     * 3 тугрика
     */
    public static void copyFiles(String pathFrom, String pathTo) {
        Path to = Paths.get(pathTo).normalize();
        Path from = Paths.get(pathFrom).normalize();
        if (!Files.exists(from)) {
            return;
        }

        try {
            if (Files.isRegularFile(from)) {
                Path thisFileTo = to.resolve(from.relativize(to)).normalize();
                Files.createDirectories(thisFileTo.getParent());
                copy(from, thisFileTo);
                return;
            }

            Files.createDirectories(to.getParent());
            Files.walkFileTree(from, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                        throws IOException {
                    Files.createDirectory(to.resolve(from.relativize(dir)));
                    return FileVisitResult.CONTINUE;
                }

                /**
                 * Invoked for a file in a directory.
                 *
                 * <p> Unless overridden, this method returns {@link FileVisitResult#CONTINUE
                 * CONTINUE}.
                 */
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                        throws IOException {
                    copy(file, to.resolve(from.relativize(file)));
                    return FileVisitResult.CONTINUE;
                }

            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void copy(Path from, Path to) throws IOException {
        String pathFrom = from.toString();
        String pathTo = to.toString();

        try (BufferedInputStream inp = new BufferedInputStream(Files.newInputStream(Paths.get(pathFrom)));
             BufferedOutputStream out = new BufferedOutputStream(Files.newOutputStream(Paths.get(pathTo)))) {
            byte[] buffer = new byte[1024];
            int sizeOfBlock;
            do {
                sizeOfBlock = inp.read(buffer);
                out.write(buffer);
            } while (sizeOfBlock > 0);
        }
    }

}
