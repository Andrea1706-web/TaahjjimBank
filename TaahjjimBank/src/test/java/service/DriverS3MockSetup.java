package service;

import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.util.*;

public class DriverS3MockSetup {

    private static final Map<String, Object> fakeStorage = new HashMap<>();

    private static MockedConstruction<DriverS3> mockedConstruction;

    public static void startMock() {
        if (mockedConstruction != null) return;

        mockedConstruction = Mockito.mockConstruction(DriverS3.class, (mock, context) -> {
            // Mock readAll
            Mockito.when(mock.readAll(Mockito.anyString())).thenAnswer(invocation -> {
                String prefix = invocation.getArgument(0);
                List<Object> results = new ArrayList<>();
                synchronized (fakeStorage) {
                    fakeStorage.forEach((key, value) -> {
                        if (key.startsWith(prefix)) {
                            results.add(value);
                        }
                    });
                }
                return results;
            });

            // Mock read
            Mockito.when(mock.read(Mockito.anyString())).thenAnswer(invocation -> {
                String key = invocation.getArgument(0);
                synchronized (fakeStorage) {
                    return Optional.ofNullable(fakeStorage.get(key));
                }
            });

            // Mock save
            Mockito.doAnswer(invocation -> {
                String key = invocation.getArgument(0);
                Object value = invocation.getArgument(1);
                synchronized (fakeStorage) {
                    fakeStorage.put(key, value);
                }
                return null;
            }).when(mock).save(Mockito.anyString(), Mockito.any());
        });
    }

    public static void stopMock() {
        if (mockedConstruction != null) {
            mockedConstruction.close();
            mockedConstruction = null;
        }
        clearStorage();
    }

    public static void insert(String key, Object value) {
        synchronized (fakeStorage) {
            fakeStorage.put(key, value);
        }
    }

    public static Optional<Object> get(String key) {
        synchronized (fakeStorage) {
            return Optional.ofNullable(fakeStorage.get(key));
        }
    }

    public static void clearStorage() {
        synchronized (fakeStorage) {
            fakeStorage.clear();
        }
    }
}
