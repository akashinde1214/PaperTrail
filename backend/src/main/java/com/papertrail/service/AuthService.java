package com.papertrail.service;

import com.papertrail.dto.AuthResponse;
import com.papertrail.dto.LoginRequest;
import com.papertrail.dto.OtpSendRequest;
import com.papertrail.dto.OtpVerifyRequest;
import com.papertrail.dto.RegisterRequest;
import com.papertrail.dto.UserProfileResponse;
import com.papertrail.model.OtpChannel;
import com.papertrail.model.OtpCode;
import com.papertrail.model.User;
import com.papertrail.repository.OtpCodeRepository;
import com.papertrail.repository.UserRepository;
import com.papertrail.security.JwtService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final OtpCodeRepository otpCodeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final OtpDispatchService otpDispatchService;
    private final int otpExpiryMinutes;

    public AuthService(
            UserRepository userRepository,
            OtpCodeRepository otpCodeRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            OtpDispatchService otpDispatchService,
            @Value("${app.otp.expiration-minutes}") int otpExpiryMinutes
    ) {
        this.userRepository = userRepository;
        this.otpCodeRepository = otpCodeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.otpDispatchService = otpDispatchService;
        this.otpExpiryMinutes = otpExpiryMinutes;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email().trim().toLowerCase())) {
            throw new IllegalArgumentException("Email already registered");
        }
        if (userRepository.existsByMobile(request.mobile().trim())) {
            throw new IllegalArgumentException("Mobile already registered");
        }

        User user = new User();
        user.setName(request.name().trim());
        user.setMobile(request.mobile().trim());
        user.setEmail(request.email().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setAddress(request.address().trim());
        user.setAge(request.age());
        user.setVerified(false);

        User saved = userRepository.save(user);
        String token = jwtService.generateToken(saved.getId());

        return new AuthResponse(token, UserProfileResponse.from(saved));
    }

    public AuthResponse login(LoginRequest request) {
        String identifier = request.identifier().trim().toLowerCase();
        Optional<User> byEmail = userRepository.findByEmail(identifier);
        Optional<User> byMobile = userRepository.findByMobile(request.identifier().trim());

        User user = byEmail.or(() -> byMobile)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        String token = jwtService.generateToken(user.getId());
        return new AuthResponse(token, UserProfileResponse.from(user));
    }

    public UserProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return UserProfileResponse.from(user);
    }

    @Transactional
    public void sendOtp(Long userId, OtpSendRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        otpCodeRepository.deleteByExpiresAtBefore(LocalDateTime.now());

        String code = String.format("%06d", new Random().nextInt(1_000_000));
        String target = request.channel() == OtpChannel.SMS ? user.getMobile() : user.getEmail();

        OtpCode otp = new OtpCode();
        otp.setUser(user);
        otp.setPurpose(request.purpose().trim().toUpperCase());
        otp.setChannel(request.channel());
        otp.setTarget(target);
        otp.setCode(code);
        otp.setUsed(false);
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(otpExpiryMinutes));

        otpCodeRepository.save(otp);
        otpDispatchService.dispatch(request.channel(), target, code, otp.getPurpose());
    }

    @Transactional
    public void verifyOtp(Long userId, OtpVerifyRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        OtpCode otp = otpCodeRepository.findTopByUserAndPurposeAndChannelAndUsedFalseOrderByCreatedAtDesc(
                        user,
                        request.purpose().trim().toUpperCase(),
                        request.channel()
                )
                .orElseThrow(() -> new IllegalArgumentException("No OTP found for this channel and purpose"));

        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("OTP expired");
        }
        if (!otp.getCode().equals(request.code())) {
            throw new IllegalArgumentException("Invalid OTP");
        }

        otp.setUsed(true);
        otpCodeRepository.save(otp);

        if ("VERIFY_ACCOUNT".equalsIgnoreCase(request.purpose())) {
            user.setVerified(true);
            userRepository.save(user);
        }
    }
}
