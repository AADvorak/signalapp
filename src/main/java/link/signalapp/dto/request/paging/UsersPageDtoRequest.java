package link.signalapp.dto.request.paging;

import java.util.List;

public class UsersPageDtoRequest extends PageDtoRequest<UserFiltersDto> {

    // todo this constructor and methods are needed for tests compatibility
    public UsersPageDtoRequest() {
        super();
        this.setFilters(new UserFiltersDto());
    }

    public UsersPageDtoRequest setPage(int page) {
        super.setPage(page);
        return this;
    }

    public UsersPageDtoRequest setSize(int size) {
        super.setSize(size);
        return this;
    }

    public UsersPageDtoRequest setSearch(String search) {
        this.getFilters().setSearch(search);
        return this;
    }

    public UsersPageDtoRequest setSortBy(String sortBy) {
        this.getSort().setBy(sortBy);
        return this;
    }

    public UsersPageDtoRequest setSortDir(String sortDir) {
        this.getSort().setDir(sortDir);
        return this;
    }

    public UsersPageDtoRequest setRoleIds(List<Integer> roleIds) {
        this.getFilters().setRoleIds(roleIds);
        return this;
    }
}
