package com.dnanh01.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dnanh01.backend.exception.ProductException;
import com.dnanh01.backend.exception.UserException;
import com.dnanh01.backend.model.Product;
import com.dnanh01.backend.model.User;
import com.dnanh01.backend.request.AddItemRequest;
import com.dnanh01.backend.request.ChangePasswordRequest;
import com.dnanh01.backend.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfileHandler(
            @RequestHeader("Authorization") String jwt) throws UserException {
        User user = userService.findUserProfileByJwt(jwt);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(user);
    }
    
    @PutMapping("/update")
    public ResponseEntity<User> updateProfile(
            @RequestBody User req,
            @RequestHeader("Authorization") String jwt) throws UserException {
        try {
            // Tìm người dùng bằng JWT
            User user = userService.findUserProfileByJwt(jwt);

            // Cập nhật thông tin từ request
            user.setFirstName(req.getFirstName());
            user.setLastName(req.getLastName());
            //user.setEmail(req.getEmail());
            user.setMobile(req.getMobile());

            // Lưu người dùng đã cập nhật vào cơ sở dữ liệu
            User updatedUser = userService.updateUserProfile(user.getId(), req);

            return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
        } catch (UserException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    

    @PostMapping("/change-password")
    public ResponseEntity<String> changePasswordHandler(
    		@RequestBody ChangePasswordRequest req,
            @RequestHeader("Authorization") String jwt) throws UserException {
      
            User user = userService.findUserProfileByJwt(jwt);

            // Validate the current password
            if (!userService.validatePassword(user.getId(),req)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid current password");
            }

            // Change the password
            userService.changePassword(user.getId(), req);

            return ResponseEntity.status(HttpStatus.OK).body("Password changed successfully");
    }
}
