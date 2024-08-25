package ru.yandex.practicum.filmorate.storage.mem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.api.MpaStorage;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class MpaInMemoryStorage extends BaseInMemoryStorage<Mpa> implements MpaStorage {

    public MpaInMemoryStorage() {
        super(Mpa::getId, Mpa::setId);
        this.data.putAll(getMpas());
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
