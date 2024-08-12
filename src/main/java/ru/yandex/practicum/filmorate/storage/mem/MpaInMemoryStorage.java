package ru.yandex.practicum.filmorate.storage.mem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.api.MpaStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class MpaInMemoryStorage implements MpaStorage {

    private static final Map<Long, Mpa> MPAS = getMpas();

    @Override
    public Collection<Mpa> findAll() {
        return MPAS.values().stream()
                .sorted(Comparator.comparing(Mpa::getId))
                .toList();
    }

    @Override
    public Optional<Mpa> findById(Long id) {
        return Optional.ofNullable(MPAS.get(id));
    }

    private static Map<Long, Mpa> getMpas() {
        String[] mpaNames = {"G", "PG", "PG-13", "R", "NC-17"};
        Map<Long, Mpa> mpas = new HashMap<>();
        for (int i = 0; i < mpaNames.length; i++) {
            Mpa mpa = new Mpa();
            mpa.setId(i + 1L);
            mpa.setName(mpaNames[i]);
            mpas.put(mpa.getId(), mpa);
        }
        return mpas;
    }
}
