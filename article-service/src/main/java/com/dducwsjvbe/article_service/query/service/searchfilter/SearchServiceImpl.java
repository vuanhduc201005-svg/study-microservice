package com.dducwsjvbe.article_service.query.service.searchfilter;

import com.dducwsjvbe.article_service.command.data.Article;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SearchServiceImpl implements SearchService {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Article> findByFilter(Pageable pageable, String[] article, Boolean isReady) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Article> query = criteriaBuilder.createQuery(Article.class);
        Root<Article> root = query.from(Article.class);
        List<Predicate> predicates = new ArrayList<>();
        Pattern pattern = Pattern.compile("(\\w+?)([:<>~!])(\\p{Punct}?)(.*?)(\\p{Punct}?)$");
        for (String e : article) {
            System.out.println("Filter = " + e);
            Matcher matcher = pattern.matcher(e);
            if (matcher.find()) {
                SpecSearchCriteria criteria = new SpecSearchCriteria(
                        matcher.group(1),
                        matcher.group(2),
                        matcher.group(4),
                        matcher.group(3),
                        matcher.group(5)
                );
                predicates.add(toBookPredicate(root, criteriaBuilder, criteria));
            }
//            predicates.add(
//                    criteriaBuilder.equal(root.get("isReady"), isReady)
//            );
        }
        predicates.add(
                criteriaBuilder.equal(root.get("isReady"), isReady)
        );
        if (!predicates.isEmpty()) {
            query.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));
        }
                List<Article> resultList = entityManager
                .createQuery(query)
                .setFirstResult(pageable.getPageNumber())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
        return resultList;
    }
    public Predicate toBookPredicate(Root<Article> root, CriteriaBuilder builder, SpecSearchCriteria criteria) {
        Path<?> path = root.get(criteria.getKey());
        Object converted = convertValue(path, criteria.getValue());
//        System.out.println(">>> key=" + criteria.getKey() + " | op=" + criteria.getOperation() + " | value=" + criteria.getValue());
        return switch (criteria.getOperation()) {
            //equal
            case EQUALITY -> builder.equal(path, converted);
            case NEGATION -> builder.notEqual(path, converted);
            case GREATER_THAN -> builder.greaterThan((Path<Comparable>) path, (Comparable) converted);
            case LESS_THAN -> builder.lessThan((Path<Comparable>) path, (Comparable) converted);
            case LIKE -> builder.like((Path<String>) path, "%" + criteria.getValue() + "%");
            case STARTS_WITH -> builder.like((Path<String>) path, criteria.getValue() + "%");
            case ENDS_WITH -> builder.like((Path<String>) path, "%" + criteria.getValue());
            case CONTAINS -> builder.like((Path<String>) path, "%" + criteria.getValue() + "%");
        };
    }

    private Object convertValue(Path<?> path, Object value) {
        Class<?> fieldType = path.getJavaType();
        String strValue = value.toString();
//        System.out.println(">>> convertValue: field=" + path + " | type=" + fieldType + " | value=" + strValue);
        if (fieldType == Date.class || fieldType == Timestamp.class) {
            try {
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strValue);
            } catch (ParseException e) {
                try {
                    return new SimpleDateFormat("yyyy-MM-dd").parse(strValue);
                } catch (ParseException ex) {
                    throw new RuntimeException("Invalid date format: " + strValue);
                }
            }
        }
        if (fieldType == Integer.class || fieldType == int.class) return Integer.parseInt(strValue);
        if (fieldType == Long.class || fieldType == long.class) return Long.parseLong(strValue);
        if (fieldType == Double.class || fieldType == double.class) return Double.parseDouble(strValue);
        if (fieldType == Boolean.class || fieldType == boolean.class) return Boolean.parseBoolean(strValue);
        // Enum
        if (fieldType.isEnum()) {
            return Enum.valueOf((Class<Enum>) fieldType, strValue);
        }
        return value; // String giữ nguyên
    }
}
/*
.setFirstResult((int) pageable.getOffset())
 */