package com.javainuse.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.javainuse.model.User;

import java.util.List;

public interface UserRepository extends ElasticsearchRepository<User, String> {
    List<User> findByName(String Name);
}