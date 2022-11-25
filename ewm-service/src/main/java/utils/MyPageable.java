package utils;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.data.querydsl.QSort;

import java.util.Objects;
import java.util.Optional;

public class MyPageable implements Pageable {
    private final int offset;
    private final int size;
    private final Sort sort;

    public MyPageable(int offset, int size, Sort sort) {
        this.offset = offset;
        this.size = size;
        this.sort = sort;
    }

    @Override
    public boolean isPaged() {
        return Pageable.super.isPaged();
    }

    @Override
    public boolean isUnpaged() {
        return Pageable.super.isUnpaged();
    }

    public int getPageNumber() {
        int page = offset / size;//TBD
        return page;
    }

    public Pageable next() {
        return new MyPageable(offset + size, size, sort);
    }

    public Pageable previousOrFirst() {
        int prevoffset = offset - size;//TBD
        return new MyPageable((prevoffset < 0 ? 0 : prevoffset), size, sort);
    }

    public Pageable first() {
        return new MyPageable(0, size, sort);
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return new MyPageable(offset + size, size, sort);
    }

    public boolean hasPrevious() {
        return offset > 0;
    }

    @Override
    public Optional<Pageable> toOptional() {
        return Pageable.super.toOptional();
    }

    public long getOffset() {
        return offset;
    }

    public int getPageSize() {
        return size;
    }

    public Sort getSort() {
        return sort;
    }

    @Override
    public Sort getSortOr(Sort sort) {
        return Pageable.super.getSortOr(sort);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyPageable that = (MyPageable) o;
        return offset == that.offset && size == that.size && Objects.equals(sort, that.sort);
    }

    @Override
    public int hashCode() {
        return Objects.hash(offset, size, sort);
    }
}
