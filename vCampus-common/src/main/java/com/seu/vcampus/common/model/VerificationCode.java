// com.seu.vcampus.common.model.VerificationCode
package com.seu.vcampus.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerificationCode {
    private String target;        // 手机号或邮箱
    private String code;          // 验证码，如 "123456"
    private long expireTime;      // 过期时间（毫秒）
    private int attemptCount;     // 尝试次数

    public VerificationCode(String target, String code, long ttlSeconds) {
        this.target = target;
        this.code = code;
        this.expireTime = System.currentTimeMillis() + ttlSeconds * 1000;
        this.attemptCount = 0;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expireTime;
    }

    public boolean isValid(String inputCode) {
        if (isExpired()) return false;
        if (attemptCount >= 5) return false; // 限制尝试5次
        return code.equals(inputCode);
    }

    public void incrementAttempt() {
        this.attemptCount++;
    }
}