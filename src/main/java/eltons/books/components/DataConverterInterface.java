package eltons.books.components;


public interface DataConverterInterface {
    <T> T getData(String json, Class<T> className);
}
