package ru.yandex.practicum.filmorate.storage.mem;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BaseInMemoryStorage<T> {

    protected final Map<Long, T> data;
    protected final Comparator<T> byId;
    protected final Function<T, Long> idGetter;
    protected final BiConsumer<T, Long> idSetter;
    protected long lastUsedId;

    protected BaseInMemoryStorage(final Function<T, Long> idGetter, final BiConsumer<T, Long> idSetter) {
        this.data = new HashMap<>();
        this.byId = Comparator.comparing(idGetter);
        this.idGetter = idGetter;
        this.idSetter = idSetter;
        this.lastUsedId = 0L;
    }

    public T save(final T entity) {
        Objects.requireNonNull(entity, "Cannot save entity: is null");
        idSetter.accept(entity, ++lastUsedId);
        data.put(lastUsedId, entity);
        return entity;
    }

    public Optional<T> findById(final long id) {
        return Optional.ofNullable(data.get(id));
    }

    public Collection<T> findById(final Collection<Long> ids) {
        return ids.stream()
                .map(data::get)
                .collect(Collectors.toSet());
    }

    public Collection<T> findAll() {
        return data.values().stream()
                .sorted(byId)
                .toList();
    }

    public Optional<T> update(final T entity) {
        Objects.requireNonNull(entity, "Cannot update entity: is null");
        final Long id = idGetter.apply(entity);
        if (data.containsKey(id)) {
            data.put(id, entity);
            return Optional.of(entity);
        } else {
            return Optional.empty();
        }
    }

    public boolean delete(final long id) {
        return data.remove(id) != null;
    }

    public void deleteAll() {
        data.clear();
    }
}
