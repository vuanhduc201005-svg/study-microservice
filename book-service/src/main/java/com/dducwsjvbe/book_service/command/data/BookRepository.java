package com.dducwsjvbe.book_service.command.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book,String> {
    @Query("""
            SELECT b.id
                  FROM Book b 
                        WHERE b.isReady =:isReady 
            """)
    Page<String> findAllBooksIds(Pageable pageable, @Param("isReady") Boolean isReady);

    @Query("""
                        SELECT b FROM Book b
            WHERE b.id IN :ids
            """)
    List<Book> findAllByIds(@Param("ids") List<String> ids);
}
