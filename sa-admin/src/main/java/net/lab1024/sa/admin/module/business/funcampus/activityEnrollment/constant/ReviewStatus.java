package net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.constant;

/**
 * author:akkkka114514
 * create at 2025-10-18 15:51
 */
public final class ReviewStatus {
    private ReviewStatus() {
    }
    // 待审核
    public static final byte PENDING = 0;
    // 审核通过
    public static final byte PASSED = 1;
    // 审核拒绝
    public static final byte REJECTED = 2;
}
