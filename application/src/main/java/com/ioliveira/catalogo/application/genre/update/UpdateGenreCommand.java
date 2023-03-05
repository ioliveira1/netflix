package com.ioliveira.catalogo.application.genre.update;

import java.util.List;

public record UpdateGenreCommand(String id, String name, boolean active, List<String> categories) {

    public static UpdateGenreCommand with(final String id, final String name, final Boolean active, final List<String> categories) {

        return new UpdateGenreCommand(id, name, active != null ? active : true, categories);
    }
}
