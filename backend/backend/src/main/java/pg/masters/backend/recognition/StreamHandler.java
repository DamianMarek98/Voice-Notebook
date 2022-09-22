package pg.masters.backend.recognition;

interface StreamHandler<T> {
    void start(T config);

    void pushFile(String filePath);

    void stop();
}
