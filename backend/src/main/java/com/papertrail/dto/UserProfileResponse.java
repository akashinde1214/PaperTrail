package com.papertrail.dto;

import com.papertrail.model.User;

public record UserProfileResponse(
        Long id,
        String name,
        String mobile,
        String email,
        String address,
        Integer age,
        Boolean verified
) {
    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getMobile(),
                user.getEmail(),
                user.getAddress(),
                user.getAge(),
                user.getVerified()
        );
    }
}
