/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.user.info;

import org.springframework.data.repository.CrudRepository;

/** A {@link CrudRepository} for a {@link UserInfo}. */
public interface UserInfoRepository extends CrudRepository<UserInfo, Integer> {}
