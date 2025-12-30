package com.homestay.module.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.homestay.common.exception.BusinessException;
import com.homestay.common.result.PageResult;
import com.homestay.common.result.Result;
import com.homestay.context.UserContextHolder;
import com.homestay.module.system.entity.Guest;
import com.homestay.module.system.service.GuestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "宾客管理")
@RestController
@RequestMapping("/guest")
public class GuestController {

    @Autowired
    private GuestService guestService;

    @Operation(summary = "获取宾客列表")
    @GetMapping("/page")
    public Result<PageResult<Guest>> getGuestPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        IPage<Guest> pageResult = guestService.getGuestPage(page, size, keyword);
        pageResult.getRecords().forEach(g -> g.setPassword(null));
        return Result.success(PageResult.of(pageResult));
    }

    @Operation(summary = "获取宾客详情")
    @GetMapping("/{id}")
    public Result<Guest> getGuest(@PathVariable Long id) {
        Guest guest = guestService.getById(id);
        if (guest != null) {
            guest.setPassword(null);
        }
        return Result.success(guest);
    }

    @Operation(summary = "获取当前登录宾客信息")
    @GetMapping("/current")
    public Result<Guest> getCurrentGuest() {
        Long userId = UserContextHolder.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        Guest guest = guestService.getById(userId);
        if (guest == null) {
            throw new BusinessException("用户不存在");
        }
        guest.setPassword(null);
        return Result.success(guest);
    }

    @Operation(summary = "更新宾客信息")
    @PutMapping
    public Result<Void> updateGuest(@RequestBody Guest guest) {
        Long userId = UserContextHolder.getUserId();
        guest.setId(userId);
        guestService.updateGuest(guest);
        return Result.success();
    }

    @Operation(summary = "修改密码")
    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestBody PasswordRequest request) {
        Long userId = UserContextHolder.getUserId();
        guestService.updatePassword(userId, request.getOldPassword(), request.getNewPassword());
        return Result.success();
    }

    @Data
    public static class PasswordRequest {
        private String oldPassword;
        private String newPassword;
    }
}
