package com.dducwsjvbe.article_service.command.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article,String> {
    @Query("""
            SELECT a.id
                  FROM Article a 
                        WHERE a.isReady =:isReady 
            """)
    Page<String> findAllArticleIds(Pageable pageable, @Param("isReady") Boolean isReady);

    @Query("""
                        SELECT a FROM Article a
            WHERE a.id IN :ids
            """)
    List<Article> findAllByIds(@Param("ids") List<String> ids);
}
