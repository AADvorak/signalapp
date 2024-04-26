package link.signalapp.dto.request;

public interface FilterDto {

    String getSearch();

    int getPage();

    int getSize();

    String getSortBy();

    String getSortDir();
}
