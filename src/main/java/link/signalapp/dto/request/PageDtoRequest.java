package link.signalapp.dto.request;

public interface PageDtoRequest {

    String getSearch();

    int getPage();

    int getSize();

    String getSortBy();

    String getSortDir();
}
