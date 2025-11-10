package service.interfaces;

        import java.io.InputStream;
        import java.util.List;
        import java.util.Optional;

public interface iDriverS3<T> {
    void save(String key, T object);
    void saveFile(String key, InputStream inputStream, long contentLength, String contentType);
    Optional<T> read(String key);
    List<T> readAll(String prefix);
    <E> void saveList(String key, List<E> list);
    <E> Optional<List<E>> readList(String key, Class<E> elementType);
    List<String> listObjectsNames(String prefix);
    void deleteObject(String key);
}
