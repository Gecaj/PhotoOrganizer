package com.cislo.photos;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

enum FileOperation {
    COPY((a, b) -> Files.copy(a, b, StandardCopyOption.REPLACE_EXISTING)),
    MOVE((a, b) -> Files.move(a, b, StandardCopyOption.REPLACE_EXISTING));

    private CheckedFunction<Path, Path, Path> operation;

    FileOperation(CheckedFunction<Path, Path, Path> operation) {
        this.operation = operation;
    }

    Path apply(Path source, Path target) throws IOException {
        return operation.apply(source, target);
    }

    @FunctionalInterface
    interface CheckedFunction<T, U, R> {
        R apply(T t, U u) throws IOException;
    }
}
